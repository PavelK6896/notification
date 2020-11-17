package ru.geekbrains.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.geekbrains.entity.Ad;
import ru.geekbrains.entity.bot.Answer;
import ru.geekbrains.entity.bot.BotData;
import ru.geekbrains.model.RequestParam;
import ru.geekbrains.model.ResponseMessage;
import ru.geekbrains.model.ResponseToNotifier;
import ru.geekbrains.model.Task;
import ru.geekbrains.service.search.SearchService;
import ru.geekbrains.utils.DateUtils;
import ru.geekbrains.utils.Formatter;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestService {

    private final SearchService searchService;

    //todo аналог БД для пользовательских запросов, вынести в БД
    private final Map<String, RequestParam> userRequestList = Collections.synchronizedMap(new HashMap<>());
    //храним таски
    private final Map<String, Task> taskList = Collections.synchronizedMap(new HashMap<>());

    public ResponseEntity<ResponseMessage> create(BotData botData){
        ResponseEntity<ResponseMessage> response;

        // создаем новый запрос
        RequestParam newRequest = getRequest(botData);
        //получаем ключ по параметрам запроса
        String keyRequestParam = getKeyFromUserRequest(newRequest);
        // создаем задачу для парсера по новым параметрам
        Task task = creteTask(newRequest.getAnswer());
        // добавляем taskID к запросу пользователя
        newRequest.setTaskID(task.getTaskId());
        
        log.info("task: " + task.toString());

        //ищем по ключу запрос, если нашли, проверяем его актуальность
        RequestParam oldRequest = userRequestList.get(keyRequestParam);
        if(oldRequest != null){
            if(isRequestActual(oldRequest)){
                // если данные актуальны, берем данные из базы без создания таски
                log.info("запрос все еще актуален, берем данные из базы");
                sendResponseStart(newRequest);
                return new ResponseEntity<>(new ResponseMessage("Создание задачи", "Парсинг по данному запросу актуален"), HttpStatus.OK);
            }
        } else {
            // создаем новый запрос в userRequestList
            userRequestList.put(keyRequestParam, newRequest);
        }

        //отправляем задачу и получаем ответ
        response = sendTaskToParsService(task);

        // проверяем ответ по коду
        if(response.getStatusCode() == HttpStatus.CREATED){
            log.info("задача для парсинга успешно создана");
            taskList.put(task.getTaskId(), task); //todo когда задача выполнится, ее нужно будет отсюда удалить
        } else {
            log.error("ошибка создания задачи");
        }
        return response;
    }

    private Task creteTask(Answer answer){
        return new Task(generateTaskId(), answer.getCountry(), answer.getCity());
    }
    
    private RequestParam getRequest(BotData botData){
        return new RequestParam(null, botData.getUser(), botData.getAnswer(), botData.getChatId(), new Date());
    }

    // если с даты создания запроса прошло меньше часа, то считаем, что объявление актуально
    private boolean isRequestActual(RequestParam requestParam){
        return DateUtils.getDurationInMinutes(requestParam.getDateCreate(), new Date()) < 60;
    }

    private String getKeyFromUserRequest(RequestParam requestParam) {
        return "" + requestParam.getAnswer().getCountry().toLowerCase() + "." +
                requestParam.getAnswer().getCity().toLowerCase() + ".";
    }

    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    private ResponseEntity<ResponseMessage> sendTaskToParsService(Task task) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8060").path("/task/create");
        String url = builder.build().encode().toUriString();
        log.info(String.format("url = %s", url));
        log.info(String.format("task = %s", task));
        return new RestTemplate().postForEntity(url, task, ResponseMessage.class);
    }

    public ResponseEntity<ResponseMessage> findTaskAndSendResponseToNotificationService(String taskId){
        log.info(String.format("Ищем задачу с id = %s", taskId));
        RequestParam requestParam = null;
        for(Map.Entry<String, RequestParam> entry : userRequestList.entrySet()) {
            if(entry.getValue().getTaskID().equals(taskId)){
                requestParam = entry.getValue();
            }
        }
        //        // заглуша для тестов
//        RequestParam requestParam = new RequestParam();
//        requestParam.setChatId(630322017);
//        Answer answer = new Answer();
//        answer.setId(1);
//        answer.setCountry("Россия");
//        answer.setCity("Калининград");
//        answer.setRooms("2");
//        answer.setMinPrice(12000);
//        answer.setMaxPrice(19000);
//        answer.setFloor("1,2,3,4,5,6,7");
//        requestParam.setAnswer(answer);
        return sendResponseStart(requestParam);
    }

    private ResponseEntity<ResponseMessage> sendResponseStart(RequestParam requestParam) {
        if(requestParam != null){
            log.info(String.format("отправляем уведомление пользователю по запросу %s", requestParam));
            Map<String, List<Ad>> ads = searchService.findAdByFilter(requestParam);
            String response = Formatter.adsToString(ads);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8081").path("/callback");
            String url = builder.build().encode().toUriString();
            log.info(String.format("url = %s", url));

            ResponseToNotifier responseToNotifier = new ResponseToNotifier("" + requestParam.getChatId(), response);

            log.info(String.format("task = %s", responseToNotifier));
            return new RestTemplate().postForEntity(url, responseToNotifier, ResponseMessage.class);
        } else {
            log.error("запрос пользователя не найден");
            ResponseMessage message = new ResponseMessage("Уведомление пользователю", String.format("запрос пользователя не найден"));
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }
}
