package ru.promelectronika.ocpp_charge_point;

import eu.chargetime.ocpp.ClientEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEventHandler implements ClientEvents {

    private static final Logger logger = LoggerFactory.getLogger(ClientEventHandler.class);

    private final ChargePointOcpp chargePointOcpp;

    public ClientEventHandler(ChargePointOcpp chargePointOcpp) {
        this.chargePointOcpp = chargePointOcpp;
    }

    @Override
    public void connectionOpened() {
        logger.info("-=== Connect to Server OCPP ===---");
        chargePointOcpp.setConnected(true);
    }

    @Override
    public void connectionClosed() {
        logger.error("-=== Connection to the server is closed ===---");
        chargePointOcpp.setConnected(false);
    }
}
