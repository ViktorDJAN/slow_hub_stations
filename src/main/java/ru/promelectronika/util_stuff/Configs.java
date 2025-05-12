package ru.promelectronika.util_stuff;


import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.promelectronika.enums.LoggerType;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Configs {

    // Settings of the main control device ( Single board computer - Forlinx)
    public static String rpc_server_address;
    public static Integer rpc_server_port;

    //    public static final String PORT = "/dev/ttyRS485-1";
    public static String comport;
    public static String gateway_address_1; // for TCP connection     31
    public static String gateway_address_2; // for TCP connection    1
    //Home Energy Power
    //TODO! SINCE OUTER ENERGY METER IS ONLY ONE, FOR ALL INNER CHARGING MEASURERS , EN_METER SLAVE_ID MUST BE EQUAL = 1
    public static Integer outer_em_id; // define energy counter Id 31
    public static Integer embedded_em_id;  // define energy counter Id 1
    public static double accessible_house_power; // Wt
    //Slow Station Settings
    public static Integer evse_id; // evse(Electric Vehicle Supply Equipment) or Charging Station ID
    //Charging Controller Settings
    public static Integer mode3_controller_id;
    public static String mode3_controller_address; // remote address
    public static Integer mode3_controller_port; //remote_port
    public static Integer connector_id;
    //Logger Settings
    public static String logger_remote_address;
    public static Integer logger_remote_port;


    public static void setConfigsBasedOnJSONFile(String path) throws IOException, JoranException, InvocationTargetException, IllegalAccessException {
        File jsonFile = new File(path);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonFile);
        try {
            Configs.rpc_server_address = jsonNode.get("rpc_server_address").asText();
            Configs.rpc_server_port = jsonNode.get("rpc_server_port").asInt();
            Configs.comport = jsonNode.get("comport").asText();
            Configs.gateway_address_1 = jsonNode.get("gateway_address_1").asText();
            Configs.gateway_address_2 = jsonNode.get("gateway_address_2").asText();
            Configs.outer_em_id = jsonNode.get("outer_em_id").asInt();
            Configs.embedded_em_id = jsonNode.get("embedded_em_id").asInt();
            Configs.accessible_house_power = jsonNode.get("accessible_house_power").asInt();
            Configs.evse_id = jsonNode.get("evse_id").asInt();
            Configs.mode3_controller_id = jsonNode.get("mode3_controller_id").asInt();
            Configs.mode3_controller_address = jsonNode.get("mode3_controller_address").asText();
            Configs.mode3_controller_port = jsonNode.get("mode3_controller_port").asInt();
            Configs.connector_id = jsonNode.get("connector_id").asInt();
            Configs.logger_remote_address = jsonNode.get("logger_remote_address").asText();
            Configs.logger_remote_port = jsonNode.get("logger_remote_port").asInt();
            LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER,"CONFIGURATION_JSON WAS SUCCESSFULLY READ FROM JSON");
        } catch (NullPointerException e) {
            LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT,LoggerType.MAIN_LOGGER,"CONFIGURATION WAS NOT READ FROM JSON");
            throw new RuntimeException("CONFIGURATIONS FIELDS ARE INCORRECT: " + e);
        }

    }

}
