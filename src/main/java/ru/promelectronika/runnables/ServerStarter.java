package ru.promelectronika.runnables;


import lombok.Getter;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.rpcClientServer.RpcServer;

import java.io.IOException;

@Getter
public class ServerStarter implements Runnable {
    private final RpcServer server;

    public ServerStarter(RpcServer server) {
        this.server = server;
    }


    @Override
    public void run() {
        try {
            LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.RPC_SERVER_LOGGER, "RpcServer has started on" + server.getServerSocketChannel().getLocalAddress());
            while (!Thread.interrupted()) {
                server.start();


            }

        } catch (IOException  e) {
            LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.RPC_SERVER_LOGGER, "SERVER_STARTER_GOT EXCEPTION" + e.getMessage());

        }
    }
}
