package ru.promelectronika.proxy_handlers;

import ru.promelectronika.dto.records.ProxyCommandDto;

public abstract class AbstractProxyHandler {
    public abstract void sendCommand(ProxyCommandDto dto);
    public abstract ProxyCommandDto receiveCommand();
}
