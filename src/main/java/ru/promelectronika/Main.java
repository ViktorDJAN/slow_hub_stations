package ru.promelectronika;

import ch.qos.logback.core.joran.spi.JoranException;


import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import functional.concrete_classes.ThreePhaseEnergyMeter;
import functional.modbus.DeviceModbus;
import ru.promelectronika.http.ServerHttp;
import ru.promelectronika.logHandler.LogHandler;
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

//        // INITIALIZATION
        LoggerPrinter.turnOnLogConfigurator(true);
        LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "PROGRAM STARTED IN MAIN");
//        // CONFIGURATIONS READING
        ConfigsFile.setConfigsBasedOnJSONFile("/home/root/chargingStation/configurations/configuration.json");
//        ConfigsFile.setConfigsBasedOnJSONFile("slow_charging_station/home/root/chargingStation/configurations/configuration.json");

        //         ** HTTP **
        ServerHttp.startHttpServer("192.168.3.200", 3060);
        LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "Http Started..." + ServerHttp.getServer().getAddress());


//        ScheduledExecutorService scheduled_service = Executors.newScheduledThreadPool(8 + ConfigsFile.mode3_controller_addresses.size());
//
//
//        // CHARGE_POINT_INITIALIZATION
//        var chargePointOcpp = new ChargePointOcpp(ConfigsFile.ocpp_server_address);
//        OcppOperation.setChargePointOcpp(chargePointOcpp);
//        scheduled_service.scheduleAtFixedRate(chargePointOcpp::connect, 20, 3000, TimeUnit.MILLISECONDS);
//        // MODBUS DEVICES INITIALIZATION
//        ModbusMaster master2 = DeviceModbus.initialModbusMasterTCP(ConfigsFile.outer_en_meter_ip_address);
//        var outerEnMeter = new ThreePhaseEnergyMeter(ConfigsFile.outer_en_meter_id, master2, ConfigsFile.outer_en_meter_ip_address);
//        List<ThreePhaseEnergyMeter> energyMeters = List.of(outerEnMeter);
//
//        // KICK OFF RPC_SERVER
//        rpcServer = new RpcServer(ConfigsFile.rpc_server_address, ConfigsFile.rpc_server_port);
//        scheduled_service.scheduleAtFixedRate(new ServerStarter(rpcServer), 20, 5000, TimeUnit.MILLISECONDS);
//
//
//        // KICK OFF ENERGY_METERS CALCULATING
//        EnergyMeterProcess.createProcess(energyMeters, true, true);
//        LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MAIN_LOGGER, "ENERGY_METER_COUNTERS ARE STARTED");
//
//        // GETTING AVAILABLE POWER OF THE BUILDING  kWT  (OUTER ENERGY_METER)
//        scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(ConfigsFile.outer_en_meter_id, EnergyMeterType.OUTER_ENERGY_METER, EnergyMeterKind.NON_BUILT_IN), 20, 650, TimeUnit.MILLISECONDS);
//        // KICK OFF TRACKER FOR TRACKING CURRENT CHANGES
//        scheduled_service.scheduleAtFixedRate(new PhaseCurrentChangesTracker(ConfigsFile.outer_en_meter_id, EnergyMeterType.OUTER_ENERGY_METER), 400, 500, TimeUnit.MILLISECONDS);
//
//        // OCPP_HANDLER_START
//        scheduled_service.execute(new OcppProxyHandler(chargePointOcpp));
//
//        // GETTING CONSUMING POWER BY THE SLOW_STATION  kWT  (INNER ENERGY_METER)
//        int count = 0;
//        for (String address : ConfigsFile.mode3_controller_addresses) {
//            int embeddedEnMeterId = Integer.parseInt(address.substring(10));
//            scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(embeddedEnMeterId, EnergyMeterType.INNER_ENERGY_METER, EnergyMeterKind.BUILT_IN), 20, 1500, TimeUnit.MILLISECONDS);
//
//            var station = new SlowChargingStation(++count, embeddedEnMeterId, address, ConfigsFile.mode3_controller_port, ConfigsFile.connector_id
//                    , ConfigsFile.rpc_server_address, ConfigsFile.rpc_server_port);
//            LogHandler.loggerMain.info("STATION CREATED " + count + " : " + station + " " + address);
//            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//            service.schedule(new StationRunProcess(station, chargePointOcpp),2000, TimeUnit.MILLISECONDS);
//        }





    }
}
