package ru.promelectronika.ocpp_charge_point.configuration;

public class OcppConfigs {
    // Рабочий сервер (Уличная станция)
    // public static final MeasurandEnumType DEFAULT_SERVER_ADDRESS = "wss://central.electrifly.ru/ocpp/json/0Z0AMJQ1GEF7Q5D55YNXJ";
    // wss means web socket secure ///

    // Тестовый сервер
//     public static final MeasurandEnumType DEFAULT_SERVER_ADDRESS = "wss://central.demo.electrifly.ru/ocpp/json/928RZT108U4HDHU91NXJM";
 //    Test electrocars ocpp 2.0
//    public static final String ELECTROCARS_TEST_SERVER_ADDRESS = "wss://ocpp.electro.cars/ws/X22PMQEWF5FE";
//     public static final String ELECTROCARS_TEST_SERVER_ADDRESS = "wss://ocpp.electrocars.tech/ws/VP9AX329B23N";
    public static final String ELECTROCARS_TEST_SERVER_ADDRESS = "wss://ocpp.electro.cars/ws/X22PMQEWF5FE";


    // Заказчик
//    public static final MeasurandEnumType DEFAULT_SERVER_ADDRESS = "ws://bigbro.ruscharge.ru:11000";


    public static final String TRANSACTIONS_DIRECTORY = "src/main/resources/transactions/";
    public static final String OCPP_CONFIGURATIONS = "src/main/resources/config/configuration.json";
    public static final String CONNECTORS_DIRECTORY = "src/main/resources/connectors/";

    // CONSTANTS ARE NOT USED SO FAR
    public static final String AUTHORIZE_DIRECTORY = "/home/root/chargeStation/authorize/";
    public static final String RESERVATIONS_DIRECTORY = "/home/root/chargeStation/reservations/";
    public static final String RESET_COUNTER_DIRECTORY = "/home/root/chargeStation/config/resetCounter.ini";

    public static final String CS_MODEL = "WS60KW";
    public static final String VENDOR_NAME = "Promelectronika";


}
