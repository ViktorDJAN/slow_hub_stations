package ru.promelectronika.controllerDetails;


public interface Controller {
    void rpcConnectRequest(String remoteAddress, int remotePort);
    void rpcPing();

    void rpcAuthorize();
    void rpcUserStop();

    String getName();
}
