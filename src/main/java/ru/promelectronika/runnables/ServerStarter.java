package ru.promelectronika.runnables;



import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.rpcClientServer.RpcServer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;




public class ServerStarter implements Runnable {
    private final RpcServer server;

    public ServerStarter(RpcServer server) {
        this.server = server;
    }


    @Override
    public void run() {

        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    public RpcServer getServer() {
        return server;
    }
}
