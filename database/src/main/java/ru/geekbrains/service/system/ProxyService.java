package ru.geekbrains.service.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.system.Proxy;
import ru.geekbrains.repository.ProxyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProxyService {

    private final ProxyRepository repository;

    @Autowired
    public ProxyService(ProxyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(@NonNull Proxy proxy) {
        repository.save(proxy);
    }

    @Transactional(readOnly = true)
    public Optional<Proxy> findByActive()
    {
        return repository.findByActive(true);
    }

    @Transactional
    public List<Proxy> findAllNotBannedByCian() {
        return repository.findAllByBannedByCian(false);
    }

    @Transactional
    public List<Proxy> findAllNotBannedByAvito() {
        return repository.findAllByBannedByAvito(false);
    }


}
