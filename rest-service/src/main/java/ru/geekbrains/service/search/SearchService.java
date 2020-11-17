package ru.geekbrains.service.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Ad;
import ru.geekbrains.entity.bot.Answer;
import ru.geekbrains.model.RequestParam;
import ru.geekbrains.repository.AdRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final AdRepository adRepository;

    @Transactional(readOnly = true)
    public Map<String, List<Ad>> findAdByFilter (RequestParam requestParam) {
        Specification<Ad> specification = AdSpecification.trueLiteral();
        Answer answer = requestParam.getAnswer();

        log.info(String.format("User filter = %s", answer));

        specification = specification.and(AdSpecification.priceLessThanEqual(answer.getMaxPrice()));
        specification = specification.and(AdSpecification.priceGreaterThanEqual(answer.getMinPrice()));
        specification = specification.and(AdSpecification.cityIs(answer.getCity()));
        specification = specification.and(AdSpecification.countryIs(answer.getCountry()));

        // фильтр комнат
        String[] arrayRooms = answer.getRooms().split(",");
        log.info(String.format("arrayRooms = %s", arrayRooms));
        Specification<Ad> specRooms = null;
        for (int i = 0; i < arrayRooms.length; i++) {
            if(specRooms == null){
                specRooms = AdSpecification.roomIs(arrayRooms[i]);
            } else {
                specRooms = specRooms.or(AdSpecification.roomIs(arrayRooms[i]));
            }
        }
        specification = specification.and(specRooms);

        // фильтр этажности
        if(!answer.getFloor().equals("-")) {
            String[] arrayFloors = answer.getFloor().split(",");
            log.info(String.format("arrayFloors = %s", arrayFloors));
            Specification<Ad> specFloor = null;
            for (int i = 0; i < arrayFloors.length; i++) {
                if (specFloor == null) {
                    specFloor = AdSpecification.roomIs(arrayFloors[i]);
                } else {
                    specFloor = specFloor.or(AdSpecification.floorIs(arrayFloors[i]));
                }
            }
            specification = specification.and(specFloor);
        }

        return excludeDuplicates(adRepository.findAll(specification));
    }

    private String getKey(Ad ad){
        return new String(ad.getAddress().getStreet().getDistrict().getName() + "." +
                ad.getAddress().getStreet().getName() + "." +
                ad.getFloor() + "." +
                ad.getArea()).toUpperCase();
    }

    private Map<String, List<Ad>> excludeDuplicates (List<Ad> ads){
        Map<String, List<Ad>> result = new HashMap<>();

        for (Ad ad:ads) {
            String key = getKey(ad);
            log.info("Key: " + key);
            List<Ad> adsFromResult = result.get(key);

            if (Objects.isNull(adsFromResult)){
                //если лист не найден, создаем новый
                List<Ad> newAdList = new ArrayList<>();
                newAdList.add(ad);
                result.put(key, newAdList);
            } else {
                //если лист найден, то объявление похоже дубликат, добавляем его в список
                adsFromResult.add(ad);
            }
        }

        //сортировка по ценам дубликатов
        result.forEach((key, adsList) -> {
            result.put(key, adsList.stream()
                    .sorted(Comparator.comparingLong(ad -> ad.getPrice().longValue()))
                    .collect(Collectors.toList()));
        });

        return result;
    }
}
