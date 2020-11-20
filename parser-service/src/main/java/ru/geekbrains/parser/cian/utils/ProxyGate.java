package ru.geekbrains.parser.cian.utils;


import ru.geekbrains.entity.system.Proxy;


public interface ProxyGate {

    Proxy getProxy();

    void setProxyBlockedBy(String parserName, Proxy proxy);

}
