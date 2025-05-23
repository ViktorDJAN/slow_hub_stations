package ru.promelectronika.model;




import ru.promelectronika.rpcClientServer.Mode3Client;



public class SlowChargingStation {
    private final Integer evseId;
    private final Integer embeddedEnergyMeasurerId;

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

    public Integer getEvseId() {
        return evseId;
    }

    public Integer getEmbeddedEnergyMeasurerId() {
        return embeddedEnergyMeasurerId;
    }

    public Mode3Client getMode3Client() {
        return mode3Client;
    }

    public void setMode3Client(Mode3Client mode3Client) {
        this.mode3Client = mode3Client;
    }

    public String getMode3ClientAddress() {
        return mode3ClientAddress;
    }

    public Integer getMode3ClientPort() {
        return mode3ClientPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public Integer getConnectorId() {
        return connectorId;
    }
}
