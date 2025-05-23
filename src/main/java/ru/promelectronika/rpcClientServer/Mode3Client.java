package ru.promelectronika.rpcClientServer;



import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.dto.TransferredCurrent;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.logHandler.LogHandler;

public class Mode3Client extends RpcClient {
    private static final String INTERFACE_ID = "IID_SECC_MODE3_2.2U";
    private int countErrorMessage = 0;
    private int countSuccessMessage = 0;
    private boolean isConnected = false;
    private final int connectorIdMode3;

    public Mode3Client(int connectorId) {
        super(connectorId);
        connectorIdMode3 = connectorId;
    }

    /**
     * MESSAGES FROM SERVER TO THE CHARGE CONTROLLER ADDRESS
     */
    public void rpcConnectRequest(String remoteAddress, int remotePort) {
        int connectionTimeout = 1500;
        int pingPeriod = 200;
        int pingCheckCount = 5;
        Object[] arg = {INTERFACE_ID, remoteAddress, remotePort, connectionTimeout, pingPeriod, pingCheckCount};
        try {
            sendMessage("rpcConnectRequest", arg); // RPC_CONNECT_REQUEST
        } catch (Throwable e) {
            LogHandler.logThrowableMain(e);
        }
        StringBuilder str = new StringBuilder();
        str.append("\n\tInterface ID: ");
        str.append(INTERFACE_ID);
        str.append("\n\tRemote Address: ");
        str.append(remoteAddress);
        str.append("\n\tRemote Port: ");
        str.append(remotePort);
        str.append("\n\tConnection Timeout: ");
        str.append(connectionTimeout);
        str.append("\n\tPing Period: ");
        str.append(pingPeriod);
        str.append("\n\tPing Check Count: ");
        str.append(pingCheckCount);
        logMode3ClientData("rpcConnectRequest: {} " + str);
    }

    public void rpcPing() {
        Object[] pingParams = {2, 2};
        try {
            sendMessage("rpcPing", pingParams);
            countSuccessMessage++;
            if(countSuccessMessage == 2) {
                isConnected = true;
                setCountSuccessMessage(0);
                setCountErrorMessage(0);
            }

        } catch (Throwable e) {
            countErrorMessage++;
//            e.printStackTrace();
            ColorTuner.blackBackgroundWhiteText("MODE3_CLIENT: ping, ERROR countMessage: " + countErrorMessage);
            if (countErrorMessage == 7) {
                ColorTuner.blackBackgroundRedText("MODE3 CLIENT DOES NOT RESPOND");
                throw new RuntimeException();
            }
        }
        StringBuilder str = new StringBuilder();
        str.append("\n\tConnection Input State: ");
        str.append(2);
        str.append("\n\tConnection Output State: ");
        str.append(2);
        LogHandler.loggerMode3Sending.debug("rpcPing: {}", str.toString());
    }

    public void rpcReqSeccCurrentState() {
        try {
            sendMessage("REQ_SECC_CURRENT_STATE");
        } catch (Throwable e) {
            LogHandler.logThrowableMode3(e);
        }
    }

    public void rpcSetCurrentLimits() {
        float maximumCurrentLimitA = (float) TransferredCurrent.maximumCurrentLimitA;
        Object[] arg = {maximumCurrentLimitA};
        try {
            sendMessage("SET_CURRENT_LIMIT", arg);
        } catch (Throwable e) {
            LogHandler.logThrowableMode3(e);
        }
        StringBuilder str = new StringBuilder();
        str.append("\n\tMax Current Limit: ");
        str.append(maximumCurrentLimitA);
        logMode3ClientData("rpcSetCurrentLimits: {} " + str);
    }

    public void rpcSetCurrentLimitsMANUAL(float phaseCurrent) {
        Object[] arg = {phaseCurrent};
        try {
            sendMessage("SET_CURRENT_LIMIT", arg);
        } catch (Throwable e) {
            LogHandler.logThrowableMode3(e);
        }
        StringBuilder str = new StringBuilder();
        str.append("\n\tMax Current Limit: ");
        str.append(phaseCurrent);
        logMode3ClientData("rpcSetCurrentLimits: {} " + str);
    }

    public void rpcAuthorize() {
        try {
            sendMessage("AUTHORIZE");
        } catch (Throwable e) {
            LogHandler.logThrowableMode3(e);
        }
        logMode3ClientData("CONNECTOR_" +connectorIdMode3 + " + rpcAuthorize");
    }

    public void rpcUserStop() {
        try {
            sendMessage("USER_STOP");
        } catch (Throwable e) {
            LogHandler.logThrowableMode3(e);
        }
        logMode3ClientData("CONNECTOR_" +connectorIdMode3 + " rpcUserStop");
    }

    public void rpcSetIp(String ip) {
        try {
            sendMessage(ip);
        } catch (Throwable e) {
            LogHandler.logThrowableMode3(e);
        }
        logMode3ClientData("rpc_set_ip");
    }

    public void rpcLog(boolean log, String remoteAddress, int remotePort) {
        Object[] arg = {log, remoteAddress, remotePort};
        try {
            sendMessage("LOG", arg);
        } catch (Throwable e) {
            LogHandler.logThrowableMode3(e);
        }
        StringBuilder str = new StringBuilder();
        str.append("\n\tLog: ");
        str.append(log);
        str.append("\n\tRemote Address: ");
        str.append(remoteAddress);
        str.append("\n\tRemote Port: ");
        str.append(remotePort);
        logMode3ClientData("rpcLog: {} " + str);
    }

    public void logMode3ClientData(String message) {
        LoggerPrinter.logAndPrint(ColorKind.YELLOW_BG_BLACK_TEXT, LoggerType.MODE3_LOGGER, message);
    }


    public String getName() {
        return "mode3";
    }

    public int getCountErrorMessage() {
        return countErrorMessage;
    }

    public void setCountErrorMessage(int countErrorMessage) {
        this.countErrorMessage = countErrorMessage;
    }

    public int getCountSuccessMessage() {
        return countSuccessMessage;
    }

    public void setCountSuccessMessage(int countSuccessMessage) {
        this.countSuccessMessage = countSuccessMessage;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}