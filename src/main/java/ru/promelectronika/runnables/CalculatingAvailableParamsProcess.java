package ru.promelectronika.runnables;


import functional.data_bases.MeasurementsBase;
import functional.dto.MeasurementDto;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.Configs2;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.dataBases.CtrlEnMeterParamsDataBase;
import ru.promelectronika.dataBases.InnerEnMeterDtoDataBase;
import ru.promelectronika.dataBases.OuterEnMeterDtoDataBase;
import ru.promelectronika.dto.InnerEnMeterDto;
import ru.promelectronika.dto.OuterEnMeterDto;
import ru.promelectronika.dto.records.BuiltInCtrlMeasurementsDto;
import ru.promelectronika.enums.*;

import ru.promelectronika.logHandler.LogHandler;

import java.util.*;
import java.util.List;

import static ru.promelectronika.enums.NetworkType.THREE_PHASE_NETWORK;
import static ru.promelectronika.enums.NetworkType.UNDEFINED;


public class CalculatingAvailableParamsProcess implements Runnable {
    private final int enMeterId;
    private final EnergyMeterType energyMeterType;
    private final EnergyMeterKind energyMeterKind;
    private NetworkType presentNetworkType;
    public static final double COS_PHI = 0.95;
    private int logMsgCounter = 0;

    private boolean isAccumulated = false;

    public CalculatingAvailableParamsProcess(int enMeterId, EnergyMeterType energyMeterType, EnergyMeterKind kind) {
        this.energyMeterType = energyMeterType;
        this.enMeterId = enMeterId;
        this.energyMeterKind = kind;

    }


    @Override
    public void run() {
        try {

            if (energyMeterType == EnergyMeterType.OUTER_ENERGY_METER) {
                computeHomeAvailableParams();
                logMsgCounter = 1;
            } else if (energyMeterType == EnergyMeterType.INNER_ENERGY_METER && energyMeterKind == EnergyMeterKind.BUILT_IN) {
                calculateCarConsumedPowerVia_BuiltInEnMeterInCtrl();
                logMsgCounter++;
            } else if (energyMeterType == EnergyMeterType.INNER_ENERGY_METER) {
                calculateCarConsumptionBy_Not_BuiltInCtrl();
                logMsgCounter++;
            }
            if (logMsgCounter == 2) {
                LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.ENERGY_METER_LOGGER,
                        "CALCULATING AVAILABLE CURRENT FROM HOUSE IS STARTED");
                LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.ENERGY_METER_LOGGER,
                        "CALCULATING CAR CONSUMED POWER IS STARTED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  collects params from OUTER Energy meter
    public void computeHomeAvailableParams() {
        OuterEnMeterDto outerEnMeterDto = new OuterEnMeterDto();
        double availableHomePower = 0;
        double phaseI = 0;
        if (presentNetworkType == null || presentNetworkType == UNDEFINED) {
            presentNetworkType = defineNetworkType(enMeterId);
        } else {
            switch (presentNetworkType) {
                case ONE_PHASE_NETWORK:
                    try {
                        phaseI = Math.round(getAvailablePhaseCurrent_ONEPhaseNetwork() * 100.0) / 100.0;
                        Optional<Double> phaseU = Optional.of(getVoltageMap().values().stream()
                                .filter(voltage -> voltage > 198)
                                .findAny().orElse(0.0));
                        availableHomePower = Configs2.accessible_house_power - calculatePresentConsumingPowerONE_Phase();

                        outerEnMeterDto.setPhaseCurrent(Math.floor(phaseI)); // CURRENT ON PHASE
                        outerEnMeterDto.setPhaseVoltage(phaseU.get());// VOLTAGE ON PHASE
                        outerEnMeterDto.setAvailablePower(availableHomePower);
                        OuterEnMeterDtoDataBase.map.put(enMeterId, outerEnMeterDto);
                    } catch (Exception e) {
                        LogHandler.logThrowableMain(e);
                    }
                    break;
                case THREE_PHASE_NETWORK:
                    phaseI = Math.round(getAvailablePhaseCurrent_THREEPhaseNetwork());
                    double averageVoltage = (getVoltageMap().values().stream()
                            .reduce((double) 0, Double::sum)) / 3;
                    availableHomePower = Configs2.accessible_house_power - calculatePresentConsumingPowerTHREE_Phases();

                    outerEnMeterDto.setPhaseCurrent(phaseI);
                    outerEnMeterDto.setPhaseVoltage(averageVoltage);
                    outerEnMeterDto.setAvailablePower(availableHomePower);
                    OuterEnMeterDtoDataBase.map.put(enMeterId, outerEnMeterDto);
                    break;
            }
        }

    }

    // collects params from INNER Energy meter
    public void calculateCarConsumedPowerVia_BuiltInEnMeterInCtrl() {
        var dto = new InnerEnMeterDto();
        double consumedPower = 0;
        Optional<Double> optionalPhaseI;

        BuiltInCtrlMeasurementsDto elParams = mapToDto();

        if (elParams != null) {
            List<Double> currentsList = List.of(elParams.curL1(), elParams.curL2(), elParams.curL3());
            List<Double> voltagesList = List.of(elParams.vL1(), elParams.vL2(), elParams.vL3());


            if (presentNetworkType == null || presentNetworkType == UNDEFINED) {
                presentNetworkType = defineNetworkTypeByBuiltInCtrl(enMeterId);
            } else {
                switch (presentNetworkType) {
                    case ONE_PHASE_NETWORK:

                        optionalPhaseI = Optional.of(currentsList.stream().filter(current -> current > 0.016).findAny().orElse(0.0));
                        Optional<Double> phaseU = voltagesList.stream()
                                .filter(voltage -> voltage > 198)
                                .findAny();
                        consumedPower = optionalPhaseI.get() * phaseU.get();
                        dto.setConsumedPower(consumedPower);
                        dto.setPhaseCurrent(optionalPhaseI.get());
                        dto.setPhaseVoltage(phaseU.get());
                        dto.setCurrentMap(getCurrentMap());
                        dto.setVoltageMap(getVoltageMap());

                        InnerEnMeterDtoDataBase.map.put(enMeterId, dto);
                        break;

                    case THREE_PHASE_NETWORK:
                        optionalPhaseI = Optional.of(currentsList.stream()
                                .filter(current -> current > 0.016)
                                .findAny()
                                .orElse(0.0));
                        ColorTuner.printPurpleText("CALCULATE_CONSUMED_CAR_CURRENT: " + optionalPhaseI.get() + " ID: " + enMeterId);

                        double averageVoltage = (voltagesList
                                .stream()
                                .reduce((double) 0, Double::sum)) / voltagesList.size();
                        consumedPower = getConsumedPowerOnTHREE_PhasesBuiltInCtrl(voltagesList, currentsList);
                        dto.setPhaseCurrent(optionalPhaseI.get());
                        dto.setConsumedPower(consumedPower);
                        dto.setPhaseVoltage(averageVoltage);

                        InnerEnMeterDtoDataBase.map.put(enMeterId, dto);
                        break;
                }
            }
        }
    }


    public void calculateCarConsumptionBy_Not_BuiltInCtrl() {
        var dto = new InnerEnMeterDto();
        double consumedPower = 0;
        Optional<Double> optionalPhaseI;
        if (presentNetworkType == null || presentNetworkType == UNDEFINED) {
            presentNetworkType = defineNetworkType(enMeterId);
        } else {
            switch (presentNetworkType) {
                case ONE_PHASE_NETWORK:
                    optionalPhaseI = Optional.of(getCurrentMap().values().stream()
                            .filter(current -> current > 0.016)
                            .findAny()
                            .orElse(0.0));
                    Optional<Double> phaseU = getVoltageMap().values().stream()
                            .filter(voltage -> voltage > 198)
                            .findAny();
                    consumedPower = optionalPhaseI.get() * phaseU.get();
                    dto.setConsumedPower(consumedPower);
                    dto.setPhaseCurrent(optionalPhaseI.get());
                    dto.setPhaseVoltage(phaseU.get());
                    dto.setCurrentMap(getCurrentMap());
                    dto.setVoltageMap(getVoltageMap());
                    //
                    InnerEnMeterDtoDataBase.map.put(enMeterId, dto);
                    break;

                case THREE_PHASE_NETWORK:
                    optionalPhaseI = Optional.of(getCurrentMap().values().stream()
                            .filter(current -> current > 0.016)
                            .findAny()
                            .orElse(0.0));
                    ColorTuner.printPurpleText("CALCULATE_CONSUMED_CAR_CURRENT: " + optionalPhaseI.get() + " ID: " + enMeterId);

                    double averageVoltage = (getVoltageMap()
                            .values()
                            .stream()
                            .reduce((double) 0, Double::sum)) / 3;
                    consumedPower = getConsumedPowerOnTHREE_Phases();
                    dto.setPhaseCurrent(optionalPhaseI.get());
                    dto.setConsumedPower(consumedPower);
                    dto.setPhaseVoltage(averageVoltage);
                    dto.setCurrentMap(getCurrentMap());
                    dto.setVoltageMap(getVoltageMap());
                    InnerEnMeterDtoDataBase.map.put(enMeterId, dto);
                    break;
            }
        }
    }


    public double getConsumedPowerOnTHREE_Phases() {
        TreeMap<String, Double> treeVoltageMap = new TreeMap<>(getVoltageMap());
        TreeMap<String, Double> treeCurrentMap = new TreeMap<>(getCurrentMap());
        List<Double> voltageList = new ArrayList<>(treeVoltageMap.values());
        List<Double> currentList = new ArrayList<>(treeCurrentMap.values());

        double consumedPower = 0;
        for (int i = 0; i < voltageList.size(); i++) {
            double phasePower = voltageList.get(i) * currentList.get(i);
            consumedPower += phasePower;
        }
        return consumedPower;
    }

    public double getConsumedPowerOnTHREE_PhasesBuiltInCtrl(List<Double> voltageList, List<Double> currentList) {
        double consumedPower = 0;
        for (int i = 0; i < voltageList.size(); i++) {
            double phasePower = voltageList.get(i) * currentList.get(i);
            consumedPower += phasePower;
        }
        return consumedPower;
    }


    public double getAvailablePhaseCurrent_ONEPhaseNetwork() {
        double availablePower = Configs2.accessible_house_power - calculatePresentConsumingPowerONE_Phase();
        double phaseI;
        Optional<Double> voltageOnePhase = Optional.of(getVoltageMap().values().stream()
                .filter(voltage -> voltage > 198)
                .findAny()
                .orElse(0.0));


        if (availablePower > 7000) {
            phaseI = 7000 / (voltageOnePhase.get());
            return phaseI;

        } else if (availablePower < 7000) {
            phaseI = availablePower / (voltageOnePhase.get());
            return phaseI;
        }
        LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.MAIN_LOGGER,
                "CALCULATING_AVAILABLE_PARAMS: getAvailablePhaseCurrent_ONEPhaseNetwork() ," +
                        "CAN'T GET AVAILABLE PHASE CURRENT ON ONE_PHASE_NETWORK, phaseI = 0");
        return 0;
    }

    public double getAvailablePhaseCurrent_THREEPhaseNetwork() {
        double availablePower = Configs2.accessible_house_power - calculatePresentConsumingPowerTHREE_Phases();
        double averageVoltage = (getVoltageMap().values().stream().reduce((double) 0, Double::sum)) / 3;
        double phaseI;

        if (availablePower > 22000) {
            phaseI = 22000 / (3 * averageVoltage * COS_PHI);
            return phaseI;
        } else if (availablePower < 22000) {
            phaseI = availablePower / (3 * averageVoltage * COS_PHI);
            return phaseI;
        }
        LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.MAIN_LOGGER, "CALCULATING_AVAILABLE_PARAMS: getAvailablePhaseCurrent_THREEPhaseNetwork() ," +
                "CAN'T GET AVAILABLE PHASE CURRENT ON THREE_PHASE_NETWORK, phaseI = 0");
        return 0;
    }


    public double calculatePresentConsumingPowerTHREE_Phases() {
        double averageVoltage = (getVoltageMap().values().stream()
                .reduce((double) 0, Double::sum)) / 3;
        double averageCurrent = (getCurrentMap().values().stream()
                .reduce((double) 0, Double::sum)) / 3;
        return averageVoltage * averageCurrent * COS_PHI; // in case of need , cosϕ may be removed;
    }

    public double calculatePresentConsumingPowerONE_Phase() {
        try {
            Optional<Double> voltageOnePhase = Optional.of(getVoltageMap().values().stream()
                    .filter(voltage -> voltage > 198)
                    .findAny().orElse(0.0));
            Optional<Double> currentOnePhase = Optional.of(getCurrentMap().values().stream()
                    .filter(current -> current >= 0.016)
                    .findAny().orElse(0.0));
            return voltageOnePhase.get() * currentOnePhase.get();

        } catch (NoSuchElementException e) {
            ColorTuner.redBackgroundBlackText("CALCULATING_AVAILABLE_PARAMS: calculatePresentConsumingPowerONE_Phase(); " +
                    " No such element: " + e.getMessage());
        }
        return 0;
    }

    public Map<String, Double> getVoltageMap() {
        MeasurementDto measurementDto = MeasurementsBase.map.get(enMeterId);
        Map<String, Double> voltageMap = new HashMap<>();
        voltageMap.put("voltagePL1", measurementDto.getVoltagePL1());
        voltageMap.put("voltagePL2", measurementDto.getVoltagePL2());
        voltageMap.put("voltagePL3", measurementDto.getVoltagePL3());
        return voltageMap;
    }


    public Map<String, Double> getCurrentMap() {
        MeasurementDto measurementDto = MeasurementsBase.map.get(enMeterId);
        Map<String, Double> currentMap = new HashMap<>();
        currentMap.put("currentPL1", measurementDto.getCurrentPL1());
        currentMap.put("currentPL2", measurementDto.getCurrentPL2());
        currentMap.put("currentPL3", measurementDto.getCurrentPL3());

        return currentMap;
    }


    public NetworkType defineNetworkType(int energyCounterId) {
        if (MeasurementsBase.map.get(energyCounterId) != null) {
            MeasurementDto dto = MeasurementsBase.map.get(energyCounterId);
            if ((dto.getVoltagePL1() > 198 && dto.getVoltagePL2() > 198 && dto.getVoltagePL3() > 198)) {
                LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MAIN_LOGGER,
                        THREE_PHASE_NETWORK + " energy_meter_ID: " + energyCounterId);

                return THREE_PHASE_NETWORK;

            } else if (dto.getVoltagePL1() >= 198 || dto.getVoltagePL2() >= 198 || dto.getVoltagePL3() >= 198) {
                LogHandler.loggerMain.info(NetworkType.ONE_PHASE_NETWORK + " energy_meter_ID: " + energyCounterId);
                LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MAIN_LOGGER,
                        NetworkType.ONE_PHASE_NETWORK + " energy_meter_ID: " + energyCounterId);

                return NetworkType.ONE_PHASE_NETWORK;

            } else if ((dto.getVoltagePL1() < 198 && dto.getVoltagePL2() < 198 || dto.getVoltagePL3() > 198)) {
                LoggerPrinter.logAndPrint(ColorKind.RED_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER,
                        "EN_METER_ID: " + enMeterId + " IMPOSSIBLE TO DEFINE NETWORK TYPE, LOW VOLTAGE: "
                                + "L1: " + dto.getVoltagePL1() + " L2: " + dto.getVoltagePL2() +
                                " L2:" + dto.getVoltagePL3() + "\n" + " " + UNDEFINED);
            }
        }

        return UNDEFINED;
    }

    public NetworkType defineNetworkTypeByBuiltInCtrl(int energyCounterId) {
        int countPhaseLine = 0;
        if (CtrlEnMeterParamsDataBase.map.get(energyCounterId) != null) {
            BuiltInCtrlMeasurementsDto dto =  mapToDto();
            if (dto.vL1() > 198) countPhaseLine++;
            if (dto.vL2() > 198) countPhaseLine++;
            if (dto.vL3() > 198) countPhaseLine++;

            if (countPhaseLine == 1) {
                LogHandler.loggerMain.info(NetworkType.ONE_PHASE_NETWORK + " energy_meter_ID: " + energyCounterId);
                LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MAIN_LOGGER,
                        NetworkType.ONE_PHASE_NETWORK + " energy_meter_ID: " + energyCounterId);

                return NetworkType.ONE_PHASE_NETWORK;

            } else if (countPhaseLine == 2 || countPhaseLine == 3) {
                LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MAIN_LOGGER,
                        THREE_PHASE_NETWORK + " energy_meter_ID: " + energyCounterId);

                return THREE_PHASE_NETWORK;

            } else if ((dto.vL1() < 198 && dto.vL2() < 198 || dto.vL3() < 198)) {
                LoggerPrinter.logAndPrint(ColorKind.RED_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER,
                        "EN_METER_ID: " + enMeterId + " IMPOSSIBLE TO DEFINE NETWORK TYPE, LOW VOLTAGE: "
                                + "L1: " + dto.vL1() + " L2: " + dto.vL2() +
                                " L2:" + dto.vL3() + "\n" + " " + UNDEFINED);
            }
        }

        return UNDEFINED;
    }

    private BuiltInCtrlMeasurementsDto mapToDto() {

        try {
//            System.out.println(CtrlEnMeterParamsDataBase.map);
            Map<String, Double> accumulatingMap = new HashMap<>();
            Map<String, Double> map = CtrlEnMeterParamsDataBase.map.get(enMeterId);
//            System.out.println(enMeterId+" " +map + " ");
            if (!map.isEmpty()) {
                if (!isAccumulated) {
                    for (int i = 0; i < 100; i++) {
                        if (map.get("vL1") > 0) {
                            double vl_1 = map.get("vL1");
                            accumulatingMap.put("vL1", vl_1);
                        }
                        if (map.get("vL2") > 0) {
                            double vl_2 = map.get("vL2");
                            accumulatingMap.put("vL2", vl_2);

                        }
                        if (map.get("vL3") > 0) {
                            double vl_3 = map.get("vL3");
                            accumulatingMap.put("vL3", vl_3);

                        }


                    }
                    isAccumulated = true;
                    return new BuiltInCtrlMeasurementsDto(accumulatingMap.get("vL1"), accumulatingMap.get("vL2"), accumulatingMap.get("vL3"),
                            map.get("curL1"), map.get("curL2"), map.get("curL3"));
                } else {
                    return new BuiltInCtrlMeasurementsDto(map.get("vL1"), map.get("vL2"), map.get("vL3"),
                            map.get("curL1"), map.get("curL2"), map.get("curL3"));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BuiltInCtrlMeasurementsDto(0, 0, 0, 0, 0, 0);
    }
}










