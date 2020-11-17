package ru.geekbrains.service.parserservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Ad;
import ru.geekbrains.entity.Address;
import ru.geekbrains.parser.ApartmentParserInterface;
import ru.geekbrains.repository.AdRepository;
import ru.geekbrains.repository.AddressRepository;
import ru.geekbrains.service.geo.GeoService;

import java.util.List;
import java.util.Optional;

@Service
public class AdService {


    private GeoService geoService;
    private AdRepository repository;
    private AddressRepository addressRepository;

    @Autowired
    public AdService(GeoService geoService, AdRepository repository, AddressRepository addressRepository) {
        this.geoService = geoService;
        this.repository = repository;
        this.addressRepository = addressRepository;

    }


    public void saveAds(List<ApartmentParserInterface> list, String parserType) {
        for (ApartmentParserInterface apartment : list) {
            Optional<Ad> adOptional = this.repository.findByLink(apartment.getLink());
            Ad ad;
            if (adOptional.isPresent()) {
                ad = adOptional.get();
            } else {
                ad = new Ad();
                ad.setLink(apartment.getLink());
                ad.setType(parserType);
                ad.setIsActive(true);
            }

            this.populateAd(ad, apartment);
            this.save(ad);
        }

    }

    private void populateAd(Ad ad, ApartmentParserInterface apartment) {
        if (ad.getAddress() == null) {
            Address address = geoService.detectAddress(apartment.getCountry(), apartment.getRegion(), apartment.getCity(), apartment.getDistrict(), apartment.getStreet(), apartment.getHouse());
            if (address.getFloorsCount() == null && apartment.getFloorsCount() != null) {
                address.setFloorsCount(apartment.getFloorsCount());
                this.addressRepository.save(address);
            }
            ad.setAddress(address);
        }
        ad.setTitle(apartment.getTitle());
        ad.setDescription(apartment.getDescription());
        ad.setPrice(apartment.getPrice());
        ad.setRooms(apartment.getRooms());
        ad.setArea(apartment.getArea());
        ad.setAreaKitchen(apartment.getAreaKitchen());
        ad.setAreaLiving(apartment.getAreaLiving());
        ad.setFloor(apartment.getFloor());
        ad.setIsDaily(apartment.getIsDaily());
    }

    @Transactional
    public void save(@NonNull Ad ad) {
        repository.save(ad);
    }
}
