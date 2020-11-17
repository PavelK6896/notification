package ru.geekbrains.parser.cian.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.entity.cian.RussianAddressObject;
import ru.geekbrains.repository.RussianAddressObjectRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RussianAddressObjectServiceImpl implements RussianAddressObjectService {

    private RussianAddressObjectRepository russianAddressObjectRepository;

    @Autowired
    public RussianAddressObjectServiceImpl(RussianAddressObjectRepository russianAddressObjectRepository) {
        this.russianAddressObjectRepository = russianAddressObjectRepository;
    }

    @Override
    public List<RussianAddressObject> findByFormalNameAndShortName(String objectName, String cityType) {
        return russianAddressObjectRepository.findByFormalNameAndShortName(objectName, cityType);
    }

    @Override
    public Optional<RussianAddressObject> findByAoGuid(String aoGuid) {
        return russianAddressObjectRepository.findByAoGuid(aoGuid);
    }
}
