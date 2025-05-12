package ru.promelectronika.model;



import lombok.Getter;
import lombok.Setter;
import ru.promelectronika.rpcClientServer.Mode3Client;


@Getter
public class SlowChargingStation {
    private final Integer evseId;
    private final Integer embeddedEnergyMeasurerId;
    @Setter
    private Mode3Client mode3Client; // Must be initialized by setter
    private final String mode3ClientAddress;
    private final Integer mode3ClientPort;
    private final String serverAddress;
    private final Integer serverPort;
    private final Integer connectorId;

    public SlowChargingStation(Integer evseId, int embeddedEnergyMeasurerId, String mode3ClientAddress,
                               Integer mode3ClientPort, Integer connectorId, String serverAddress, Integer serverPort) {
        this.evseId = evseId;
        this.embeddedEnergyMeasurerId = embeddedEnergyMeasurerId;
        this.mode3ClientAddress = mode3ClientAddress;
        this.mode3ClientPort = mode3ClientPort;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.connectorId = connectorId;
    }
}
