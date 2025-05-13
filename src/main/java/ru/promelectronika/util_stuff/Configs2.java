package ru.promelectronika.util_stuff;


import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.promelectronika.enums.LoggerType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Configs2 {

    // Settings of the main control device ( Single board computer - Forlinx)
    public static String vendor_name ;
    public static String cs_model ;
    public static String ocpp_server_address ;
    public static Integer accessible_house_power ;
    public static String rpc_server_address ;
    public static Integer rpc_server_port ;
    public static List<Integer> evse_ids ;
    public static Integer embedded_en_meter_id ;
    public static Integer outer_en_meter_id ;
    public static String outer_en_meter_ip_address ;
    public static List<String> mode3_controller_addresses ;
    public static Integer mode3_controller_port ;
    public static Integer connector_id ;
    public static String logger_remote_address ;
    public static Integer logger_remote_port ;


    public static void setConfigsBasedOnJSONFile(String path) throws IOException {
        File jsonFile = new File(path);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonFile);
        try {
            Configs2.vendor_name = jsonNode.get("vendor_name").asText();
            Configs2.cs_model = jsonNode.get("cs_model").asText();
            Configs2.ocpp_server_address = jsonNode.get("ocpp_server_address").asText();
            Configs2.accessible_house_power = jsonNode.get("accessible_house_power").asInt();
            Configs2.rpc_server_address = jsonNode.get("rpc_server_address").asText();
            Configs2.rpc_server_port = jsonNode.get("rpc_server_port").asInt();

            JsonNode evseIds_node = jsonNode.get("evse_ids");
            evse_ids = StreamSupport.stream(evseIds_node.spliterator(), false)
                    .map(JsonNode::asInt)
                    .toList();

            Configs2.embedded_en_meter_id = jsonNode.get("embedded_en_meter_id").asInt();
            Configs2.outer_en_meter_id = jsonNode.get("outer_en_meter_id").asInt();
            Configs2.outer_en_meter_ip_address = jsonNode.get("outer_en_meter_ip_address").asText();

            JsonNode addresses_node = jsonNode.get("mode3_controller_addresses");
            mode3_controller_addresses = StreamSupport.stream(addresses_node.spliterator(), false)
                    .map(JsonNode::asText)
                    .toList();

            Configs2.mode3_controller_port = jsonNode.get("mode3_controller_port").asInt();
            Configs2.connector_id = jsonNode.get("connector_id").asInt();

            Configs2.logger_remote_address = jsonNode.get("logger_remote_address").asText();
            Configs2.logger_remote_port = jsonNode.get("logger_remote_port").asInt();


            LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "CONFIGURATION_JSON WAS SUCCESSFULLY READ FROM JSON");
        } catch (NullPointerException e) {
            LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "CONFIGURATION WAS NOT READ FROM JSON");
            throw new RuntimeException("CONFIGURATIONS FIELDS ARE INCORRECT: " + e);
        }

    }

}
