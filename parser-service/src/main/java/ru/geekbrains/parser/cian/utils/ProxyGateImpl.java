package ru.geekbrains.parser.cian.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.entity.system.Proxy;
import ru.geekbrains.parser.cian.utils.exception.ParserNameNotDefinedException;
import ru.geekbrains.service.system.ProxyService;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

@Slf4j
@Data
@Component
public class ProxyGateImpl implements ProxyGate {

    private final ProxyService proxyService;

    @Autowired
    public ProxyGateImpl(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * Returns most long ago used proxy
     * @return ru.geekbrains.entity.system.Proxy
     */

    public Proxy getProxy() {
        Proxy mostLongAgoUsedProxy = proxyService.findAllNotBannedByCian().stream().min(Comparator.comparing(Proxy::getLastUsed)).orElseThrow(RuntimeException::new);
        mostLongAgoUsedProxy.setLastUsed(new Timestamp(new Date().getTime()));
        proxyService.save(mostLongAgoUsedProxy);
        return mostLongAgoUsedProxy;
    }

    @Override
    public void setProxyBlockedBy(String parserName, Proxy proxy) {
        if (parserName.equals("Циан")) {
           proxy.setBannedByCian(true);
           proxyService.save(proxy);
        } else if (parserName.equals("Avito")) {
            proxy.setBannedByAvito(true);
            proxyService.save(proxy);
        } else {
            throw new ParserNameNotDefinedException("There is no parser with name " + parserName);
        }
    }
}
