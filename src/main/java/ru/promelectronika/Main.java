package ru.promelectronika;

import ch.qos.logback.core.joran.spi.JoranException;


import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import functional.concrete_classes.ThreePhaseEnergyMeter;
import functional.modbus.DeviceModbus;
import ru.promelectronika.proxy_handlers.OcppProxyHandler;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.Configs2;
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

import java.util.List;
import java.util.concurrent.*;

public class Main {
    private volatile static RpcServer rpcServer = null;

    public static void main(String[] args) throws IOException, JoranException  {

        // INITIALIZATION
        LoggerPrinter.turnOnLogConfigurator(true);
        LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "PROGRAM STARTED IN MAIN");

        Configs2.setConfigsBasedOnJSONFile("src/main/java/ru/promelectronika/util_stuff/configuration1.json");
        ScheduledExecutorService scheduled_service = Executors.newScheduledThreadPool(7 + Configs2.mode3_controller_addresses.size());

        // CHARGE_POINT_INITIALIZATION
        var chargePointOcpp = new ChargePointOcpp(Configs2.ocpp_server_address);
        OcppOperation.setChargePointOcpp(chargePointOcpp);
        scheduled_service.scheduleAtFixedRate(chargePointOcpp::connect, 0, 3000, TimeUnit.MILLISECONDS);

        ModbusMaster master2 = DeviceModbus.initialModbusMasterTCP(Configs2.outer_en_meter_ip_address);

        var outerEnMeter = new ThreePhaseEnergyMeter(Configs2.outer_en_meter_id, master2, Configs2.outer_en_meter_ip_address);
        List<ThreePhaseEnergyMeter> energyMeters = List.of(outerEnMeter);

        rpcServer = new RpcServer(Configs2.rpc_server_address, Configs2.rpc_server_port);

     //                                ****   SCHEDULED_PROCESSES  ****
        // KICK OFF ENERGY_METERS CALCULATING
        EnergyMeterProcess.createProcess(energyMeters, true, true);
        LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MAIN_LOGGER, "ENERGY_METER_COUNTERS ARE STARTED");

        // GETTING AVAILABLE POWER OF THE BUILDING  kWT  (OUTER ENERGY_METER)

        scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(Configs2.outer_en_meter_id, EnergyMeterType.OUTER_ENERGY_METER, EnergyMeterKind.NON_BUILT_IN), 50, 600, TimeUnit.MILLISECONDS);
        // KICK OFF TRACKER FOR TRACKING CURRENT CHANGES
        scheduled_service.scheduleAtFixedRate(new PhaseCurrentChangesTracker(Configs2.outer_en_meter_id, EnergyMeterType.OUTER_ENERGY_METER), 400, 500, TimeUnit.MILLISECONDS);

        // GETTING CONSUMING POWER BY THE SLOW_STATION  kWT  (INNER ENERGY_METER)
        for (String address : Configs2.mode3_controller_addresses) {
            int embeddedEnMeterId = Integer.parseInt(address.substring(10));
            scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(embeddedEnMeterId, EnergyMeterType.INNER_ENERGY_METER, EnergyMeterKind.BUILT_IN), 600, 600, TimeUnit.MILLISECONDS);
        }

        // KICK OFF RPC_SERVER
        scheduled_service.scheduleAtFixedRate(new ServerStarter(rpcServer), 0, 5000, TimeUnit.MILLISECONDS);


        scheduled_service.execute(new OcppProxyHandler(chargePointOcpp));

        for (int i = 0;i<Configs2.mode3_controller_addresses.size();i++) {

            String address = Configs2.mode3_controller_addresses.get(i);
            int embeddedEnMeterId = Integer.parseInt(address.substring(10));

            var station = new SlowChargingStation(Configs2.evse_ids.get(i), embeddedEnMeterId, address, Configs2.mode3_controller_port, Configs2.connector_id
                    , Configs2.rpc_server_address, Configs2.rpc_server_port);

            scheduled_service.schedule(new StationRunProcess(station, chargePointOcpp), 1500, TimeUnit.MILLISECONDS);
        }


    }
}



