package ru.promelectronika;

import ch.qos.logback.core.joran.spi.JoranException;


import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import functional.concrete_classes.ThreePhaseEnergyMeter;
import functional.modbus.DeviceModbus;
import ru.promelectronika.proxy_handlers.OcppProxyHandler;
import ru.promelectronika.rpcClientServer.Mode3Client;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.model.SlowChargingStation;

import static ru.promelectronika.util_stuff.Configs.*;

import ru.promelectronika.util_stuff.Configs;
import ru.promelectronika.enums.EnergyMeterKind;
import ru.promelectronika.enums.EnergyMeterType;
import ru.promelectronika.enums.LoggerType;


import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigs;
import ru.promelectronika.rpcClientServer.RpcServer;
import ru.promelectronika.runnables.*;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.concurrent.*;

public class Main {
    private volatile static RpcServer rpcServer = null;
    private final static ScheduledExecutorService scheduled_service = Executors.newScheduledThreadPool(8);


    public static void main(String[] args) throws IOException,  JoranException, InvocationTargetException, IllegalAccessException {

        // INITIALIZATION
        LoggerPrinter.turnOnLogConfigurator(true);
        LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "PROGRAM STARTED IN MAIN");
        Configs.setConfigsBasedOnJSONFile("slow_charging_station/home/root/chargingStation/configurations/configuration.json");
//        Configs.setConfigsBasedOnJSONFile("home/root/chargingStation/configurations/configuration.json");
        ModbusMaster master2 = DeviceModbus.initialModbusMasterTCP(gateway_address_1);

        var outerEnMeter = new ThreePhaseEnergyMeter(outer_em_id, master2, gateway_address_1);
        List<ThreePhaseEnergyMeter> energyMeters = List.of(outerEnMeter);

        rpcServer = new RpcServer(Configs.rpc_server_address, rpc_server_port);

        var chargingStation = new SlowChargingStation(
                evse_id,
                embedded_em_id,
                mode3_controller_address,
                mode3_controller_port,
                connector_id,
                rpc_server_address,
                rpc_server_port
        );

        var station101 = new SlowChargingStation(evse_id, 101,
                "192.168.3.101", 19000, 1,
                rpc_server_address, rpc_server_port);

        var station102 = new SlowChargingStation(2, 102,
                "192.168.3.102", 19000, 1,
                rpc_server_address, rpc_server_port);


        // CHARGE_POINT_INITIALIZATION
        var chargePointOcpp = new ChargePointOcpp(OcppConfigs.ELECTROCARS_TEST_SERVER_ADDRESS);
        OcppOperation.setChargePointOcpp(chargePointOcpp);
        scheduled_service.scheduleAtFixedRate(chargePointOcpp::connect, 0, 3000, TimeUnit.MILLISECONDS);


//                                  ****   SCHEDULED_PROCESSES  ****
        // KICK OFF ENERGY_METERS CALCULATING
        EnergyMeterProcess.createProcess(energyMeters, true, true);
        LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MAIN_LOGGER, "ENERGY_METER_COUNTERS ARE STARTED");

//        // GETTING AVAILABLE POWER OF THE BUILDING  kWT  (OUTER ENERGY_METER)
        scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(outer_em_id, EnergyMeterType.OUTER_ENERGY_METER, EnergyMeterKind.NON_BUILT_IN), 50, 600, TimeUnit.MILLISECONDS);
//        // KICK OFF TRACKER FOR TRACKING CURRENT CHANGES
        scheduled_service.scheduleAtFixedRate(new PhaseCurrentChangesTracker(outer_em_id, EnergyMeterType.OUTER_ENERGY_METER), 400, 500, TimeUnit.MILLISECONDS);

//        // GETTING CONSUMING POWER BY THE SLOW_STATION  kWT  (INNER ENERGY_METER)

        scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(station101.getEmbeddedEnergyMeasurerId(), EnergyMeterType.INNER_ENERGY_METER, EnergyMeterKind.BUILT_IN), 0, 600, TimeUnit.MILLISECONDS);
        scheduled_service.scheduleAtFixedRate(new CalculatingAvailableParamsProcess(station102.getEmbeddedEnergyMeasurerId(), EnergyMeterType.INNER_ENERGY_METER, EnergyMeterKind.BUILT_IN), 100, 600, TimeUnit.MILLISECONDS);


        // KICK OFF RPC_SERVER
        scheduled_service.scheduleAtFixedRate(new ServerStarter(rpcServer), 0, 5000, TimeUnit.MILLISECONDS);

        // EXPERIMENT
        scheduled_service.execute(new OcppProxyHandler(chargePointOcpp));
        scheduled_service.schedule(new StationRunProcess(station101, chargePointOcpp), 1500, TimeUnit.MILLISECONDS);
        scheduled_service.schedule(new StationRunProcess(station102, chargePointOcpp), 2500, TimeUnit.MILLISECONDS);


    }
}



