package ru.promelectronika.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.promelectronika.dto.records.ConfigsDto;
import ru.promelectronika.dto.records.WifiParamsDto;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.util_stuff.*;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ServerHttp {
    private static HttpServer server;
    private static final ObjectMapper mapper = new ObjectMapper();


    public static void startHttpServer(String host, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.createContext("/check", new URLHandler_Get());
        server.createContext("/postDataToJava", new URLHandler_Post());
        server.createContext("/postWifiParamsToJava", new URLHandler_Post2());
        server.createContext("/reboot", new URLHandler_RebootPost3());
        server.createContext("/index", new StaticFileHandler());

        server.setExecutor(Executors.newFixedThreadPool(10)); // creates a default executor
        server.start();

        LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "SERVER_HTTP: STARTED ON ADDRESS: " + server.getAddress());


    }

    static class StaticFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {
            try {
                byte[] content = Files.readAllBytes(Paths.get("/home/root/chargingStation/index/index.html"));
                String contentType = "text/html";
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, content.length);
                exchange.getResponseBody().write(content);
                exchange.getResponseBody().close();
            } catch (IOException e) {
                // Handle 404
                e.printStackTrace();
            }

        }
    }


    static class URLHandler_Get implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<h1>localhost:3060/check URL IS FOUND</h1>";
            exchange.sendResponseHeaders(200, response.length());//response code and length
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response.getBytes());
            responseBody.close();
            if (!exchange.getResponseHeaders().isEmpty()) {
                System.out.println("NyHandler1 responds correctly on: localhost:3060/check");
            }

        }
    }

    static class URLHandler_RebootPost3 implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            String requestedMessage = "";
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    requestedMessage = getString(exchange.getRequestBody()); // getRequestBody is input stream
                    exchange.getRequestBody().close();
                }
                Thread.sleep(5000);
                LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "SERVER_HTTP: REBOOT: " + LocalDateTime.now());
                Runtime.getRuntime().exec((new String[]{"reboot"}));
                sendOKResponse(exchange,requestedMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    //     POST and GATHER DATA FROM JAVASCRIPT  TO JAVA
    static class URLHandler_Post implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            String requestedMessage = "";
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    requestedMessage = getString(exchange.getRequestBody()); // getRequestBody is input stream
                    exchange.getRequestBody().close();
                }
                if (!requestedMessage.isEmpty()) {
                    ConfigsDto dto = mapper.readValue(requestedMessage, ConfigsDto.class);
                    writeDataToJson(dto);
                    LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "SERVER_HTTP: GOT CONFIGS DATA FROM JAVASCRIPT: " + dto);
                } else {
                    LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "SERVER_HTTP: THERE IS EMPTY DTO: ");
                }

                // In here is forming headers that we send if everything is ok
                sendOKResponse(exchange,requestedMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendOKResponse(HttpExchange exchange,String requestedMessage) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders(); // create response header
        responseHeaders.add("Access-Control-Allow-Origin", exchange.getRequestHeaders().get("Origin").get(0)); // specify type and content info
        String response = "Got the message: \n" + requestedMessage; // form response
        exchange.sendResponseHeaders(200, response.length()); // send response headers
        OutputStream os = exchange.getResponseBody(); // returns output stream to which the response body must be written.
        os.write(response.getBytes()); // cast it to bytes
        os.close(); // close stream
    }

    static class URLHandler_Post2 implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            String requestedMessage = "";
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    requestedMessage = getString(exchange.getRequestBody()); // getRequestBody is input stream
                    exchange.getRequestBody().close();
                }
                if (!requestedMessage.isEmpty()) {
                    WifiParamsDto dto = mapper.readValue(requestedMessage, WifiParamsDto.class);
                    LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "SERVER_HTTP: GOT WIFI_PARAMS FROM JAVASCRIPT: " + dto);

                    //THESE ARE WIRENBOARD7_FILE_ADDRESSES
                    String newFilePath = "/etc/NetworkManager/system-connections/appro.nmconnection";
                    String tempFilePath = "/etc/NetworkManager/system-connections/";
                    if(!dto.wifi_networkName().isEmpty() && !dto.wifi_password().isEmpty()){
                        NetFileCreator.surrogateNetFile(newFilePath, tempFilePath, dto.wifi_networkName(), dto.wifi_password());
                        LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "SERVER_HTTP: WIFI_PARAMS_CHANGED: " + dto);
                    }
                }
                sendOKResponse(exchange,requestedMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private static void writeDataToJson(ConfigsDto dto) {
        String path = "/home/root/chargingStation/configurations/";
        try (var fileWriter = new FileWriter(path + "configuration.json")) {
            String json = new ObjectMapper().writeValueAsString(dto);
            fileWriter.write(json);

        } catch (IOException  e) {
//            throw new RuntimeException(e);
            LogHandler.loggerMain.info("IT IS NOT OK");
        }

    }
// GET : FROM JAVA TO JAVASCRIPT
//////////////////////////////////////////////////////////////////////////////////////////////////////
//    static class MyHandler3 implements HttpHandler {
//
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//
//            Headers headersResponse = exchange.getResponseHeaders();
//            headersResponse.add("Access-Control-Allow-Origin", exchange.getRequestHeaders().get("Origin").get(0));
//            Map<String, Object> dataMap = new HashMap<>();
//
//            if (!DataBaseSimple.getSentMsgDataBase().isEmpty()) {
//                SentParamDto sentParamDto = DataBaseSimple.getSentMsgDataBase().peekLast();
//                dataMap.put("sentData", sentParamDto);
//            }
//            if (!DataBaseSimple.getReceivedMsgDataBase().isEmpty()) {
//                System.out.println(ColorTuner.GREEN + "before size received base: "+ DataBaseSimple.getReceivedMsgDataBase().size() + ColorTuner.RESET);
//                ReceivedParamDto receivedParamDto = DataBaseSimple.getReceivedMsgDataBase().pollFirst();
//                System.out.println(ColorTuner.BLUE + " after size received base: "+ DataBaseSimple.getReceivedMsgDataBase().size() + ColorTuner.RESET);
//
//                dataMap.put("receivedData", receivedParamDto);
//            }
//            ObjectMapper mapper = new ObjectMapper();
//            String response = mapper.writeValueAsString(dataMap);
//            exchange.sendResponseHeaders(200, response.length());//response code and length
//            OutputStream responseBody = exchange.getResponseBody();// stream where response body must be written
//            responseBody.write(response.getBytes());
//            responseBody.close();
//
////            if (!exchange.getResponseHeaders().isEmpty()) {
////                System.out.println("NyHandler3 has a response" + exchange.getResponseHeaders().toString());
////            } else {
////                System.out.println("NyHandler3 doesn't have any response");
////            }
//
//        }
//    }


    public static String getString(InputStream in) {
        // pass in here Stream of request body , process it , get a string
        return new Scanner(in, StandardCharsets.UTF_8)
                .useDelimiter("\\z") // what you want to use as like delimiter
                .next();
    }


    public static HttpServer getServer() {
        return server;
    }

    public static void setServer(HttpServer server) {
        ServerHttp.server = server;
    }
}
