package ru.promelectronika.dto.records;

import java.util.List;

public record ConfigsDto (String vendor_name,
                          String cs_model,
                          String ocpp_server_address,
                          double accessible_house_power,
                          String rpc_server_address,
                          int rpc_server_port,
                          int embedded_en_meter_id,
                          int outer_en_meter_id,
                          String outer_en_meter_ip_address,
                          int mode3_controller_port,
                          int connector_id,
                          String logger_remote_address,
                          int logger_remote_port,
                          List<String> mode3_controller_addresses ){
}
