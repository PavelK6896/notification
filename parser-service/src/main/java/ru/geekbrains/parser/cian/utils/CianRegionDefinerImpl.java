package ru.geekbrains.parser.cian.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.entity.cian.RussianAddressObject;
import ru.geekbrains.parser.cian.service.CianRegionsService;
import ru.geekbrains.parser.cian.service.RussianAddressObjectService;
import ru.geekbrains.parser.cian.utils.exception.NoSuchRegionException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CianRegionDefinerImpl implements CianRegionDefiner {

    private RussianAddressObjectService russianAddressObjectService;

    private CianRegionsService cianRegionsService;

    private final String CITY_TYPE = "г"; //ищем только в городах

    @Autowired
    public CianRegionDefinerImpl(RussianAddressObjectService russianAddressObjectService, CianRegionsService cianRegionsService) {
        this.russianAddressObjectService = russianAddressObjectService;
        this.cianRegionsService = cianRegionsService;
    }

    public List<String> getRegions(String city) {
        List<RussianAddressObject> objects = russianAddressObjectService.findByFormalNameAndShortName(city, CITY_TYPE);
        return objects.stream().map(RussianAddressObject::getRegionCode)
                .filter(code -> !code.equals("99")) // Исключаем Байконур (ЦИАН не умеет там искать)
                .map(code -> cianRegionsService.findByRussianCode(code)
                        .orElseThrow(() -> new NoSuchRegionException("There is no such region in the Cian database")).getCianCode())
                .collect(Collectors.toList());
    }

}
