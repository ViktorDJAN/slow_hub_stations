package ru.promelectronika.dto;




public class ControllerParamsDto {
    //rpcPing
    private volatile int selfConnectionInputState;
    private volatile int selfConnectionOutputState;

    //SET_FW_VERSION
    private volatile String version;

    //SET_PROTOCOL_VERSION
    private volatile String protocolVersion;

    //SET_SECC_CURRENT_STATE
    private volatile int chargeState = 0;
    private volatile String chargeStateProtocolSpecific;

    public int getSelfConnectionInputState() {
        return selfConnectionInputState;
    }

    public void setSelfConnectionInputState(int selfConnectionInputState) {
        this.selfConnectionInputState = selfConnectionInputState;
    }

    public int getSelfConnectionOutputState() {
        return selfConnectionOutputState;
    }

    public void setSelfConnectionOutputState(int selfConnectionOutputState) {
        this.selfConnectionOutputState = selfConnectionOutputState;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getChargeState() {
        return chargeState;
    }

    public void setChargeState(int chargeState) {
        this.chargeState = chargeState;
    }

    public String getChargeStateProtocolSpecific() {
        return chargeStateProtocolSpecific;
    }

    public void setChargeStateProtocolSpecific(String chargeStateProtocolSpecific) {
        this.chargeStateProtocolSpecific = chargeStateProtocolSpecific;
    }

// For Logging

    public String getRpcPing() {
        StringBuilder str = new StringBuilder();
        str.append("\n\tConnection Input State: ");
        str.append(selfConnectionInputState);
        str.append("\n\tConnection Output State: ");
        str.append(selfConnectionOutputState);
        return str.toString();
    }

    public String getSetFwVersion() {
        StringBuilder str = new StringBuilder();
        str.append("\n\tVersion: ");
        str.append(version);
        return str.toString();
    }

    public String getSetProtocolVersion() {
        StringBuilder str = new StringBuilder();
        str.append("\n\tVersion: ");
        str.append(protocolVersion);
        return str.toString();
    }

    public String getSetSeccCurrentState() {
        StringBuilder str = new StringBuilder();
        str.append("\n\tCharge state: ");
        str.append(chargeState);
        str.append("\n\tCharge state protocol specific: ");
        str.append(chargeStateProtocolSpecific);
        return str.toString();
    }

    @Override
    public String toString() {
        return "ControllerParamsDto{" +
                "selfConnectionInputState=" + selfConnectionInputState +
                ", selfConnectionOutputState=" + selfConnectionOutputState +
                ", version='" + version + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", chargeState=" + chargeState +
                ", chargeStateProtocolSpecific='" + chargeStateProtocolSpecific + '\'' +
                '}';
    }
}
