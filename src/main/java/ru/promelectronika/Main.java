package ru.promelectronika;

import ch.qos.logback.core.joran.spi.JoranException;


import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import functional.concrete_classes.ThreePhaseEnergyMeter;
import functional.modbus.DeviceModbus;
import ru.promelectronika.http.ServerHttp;
import ru.promelectronika.proxy_handlers.OcppProxyHandler;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.ConfigsFile;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.model.SlowChargingStation;


import ru.promelectronika.enums.EnergyMeterKind;
import ru.promelectronika.enums.EnergyMeterType;
import ru.promelectronika.enums.LoggerType;


import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.rpcClientServer.RpcServer;
import ru.promelectronika.runnables.*;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class Main {
    private volatile static RpcServer rpcServer = null;

    public static void main(String[] args) throws IOException, JoranException, InterruptedException {

        //         ** HTTP **
        ColorTuner.printBlackText("Http Started...");
        ServerHttp.startHttpServer("192.168.3.200", 3060);
//
//
//        // INITIALIZATION
        LoggerPrinter.turnOnLogConfigurator(true);
        LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "PROGRAM STARTED IN MAIN");
//        // CONFIGURATIONS READING
        ConfigsFile.setConfigsBasedOnJSONFile("/home/root/chargingStation/configurations/configuration.json");
//        ConfigsFile.setConfigsBasedOnJSONFile("slow_charging_station/home/root/chargingStation/configurations/configuration.json");

//
        ScheduledExecutorService scheduled_service = Executors.newScheduledThreadPool(7 + ConfigsFile.mode3_controller_addresses.size());

        // CHARGE_POINT_INITIALIZATION
        var chargePointOcpp = new ChargePointOcpp(ConfigsFile.ocpp_server_address);
        OcppOperation.setChargePointOcpp(chargePointOcpp);
        scheduled_service.scheduleAtFixedRate(chargePointOcpp::connect, 0, 3000, TimeUnit.MILLISECONDS);
        // MODBUS DEVICES INITIALIZATION
        ModbusMaster master2 = DeviceModbus.initialModbusMasterTCP(ConfigsFile.outer_en_meter_ip_address);
        var outerEnMeter = new ThreePhaseEnergyMeter(ConfigsFile.outer_en_meter_id, master2, ConfigsFile.outer_en_meter_ip_address);
        List<ThreePhaseEnergyMeter> energyMeters = List.of(outerEnMeter);

        // KICK OFF RPC_SERVER
        rpcServer = new RpcServer(ConfigsFile.rpc_server_address, ConfigsFile.rpc_server_port);
        scheduled_service.scheduleAtFixedRate(new ServerStarter(rpcServer), 0, 5000, TimeUnit.MILLISECONDS);



        // KICK OFF ENERGY_METERS CALCULATING
        EnergyMeterProcess.createProcess(energyMeters, true, true);
        LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MAIN_LOGGER, "ENERGY_METER_COUNTERS ARE STARTED");

        // GETTING AVAILABLE POWER OF THE BUILDING  kWT  (OUTER ENERGY_METER)
        scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(ConfigsFile.outer_en_meter_id, EnergyMeterType.OUTER_ENERGY_METER, EnergyMeterKind.NON_BUILT_IN), 50, 650, TimeUnit.MILLISECONDS);
        // KICK OFF TRACKER FOR TRACKING CURRENT CHANGES
        scheduled_service.scheduleAtFixedRate(new PhaseCurrentChangesTracker(ConfigsFile.outer_en_meter_id, EnergyMeterType.OUTER_ENERGY_METER), 400, 500, TimeUnit.MILLISECONDS);
        // GETTING CONSUMING POWER BY THE SLOW_STATION  kWT  (INNER ENERGY_METER)
        for (String address : ConfigsFile.mode3_controller_addresses) {
            int embeddedEnMeterId = Integer.parseInt(address.substring(10));
            scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(embeddedEnMeterId, EnergyMeterType.INNER_ENERGY_METER, EnergyMeterKind.BUILT_IN), 0, 1500, TimeUnit.MILLISECONDS);
        }

        // OCPP_HANDLER_START
        scheduled_service.execute(new OcppProxyHandler(chargePointOcpp));

        // START_CHARGING_CONTROLLERS
        for (int i = 0; i < ConfigsFile.mode3_controller_addresses.size(); i++) {
            String address = ConfigsFile.mode3_controller_addresses.get(i);
            int embeddedEnMeterId = Integer.parseInt(address.substring(10));
            var station = new SlowChargingStation(i + 1, embeddedEnMeterId, address, ConfigsFile.mode3_controller_port, ConfigsFile.connector_id
                    , ConfigsFile.rpc_server_address, ConfigsFile.rpc_server_port);

            scheduled_service.schedule(new StationRunProcess(station, chargePointOcpp), 4000, TimeUnit.MILLISECONDS);
        }


    }
}