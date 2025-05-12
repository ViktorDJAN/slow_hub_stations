package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;


import eu.chargetime.ocpp.model.core_2_0_1.messages.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.ChangeAvailabilityResponse;
import ru.promelectronika.util_stuff.ColorTuner;

/** CENTRAL_SYSTEM REQUESTS CHARGE_POINT TO CHANGE ITS AVAILABILITY
 */
public class ChangeAvailabilityRequestHandler {

    private final ChangeAvailabilityRequest request;

    public ChangeAvailabilityRequestHandler(ChangeAvailabilityRequest request) {
        this.request = request;
    }
// TODO implement functional
    public ChangeAvailabilityResponse getResponse() {
        ColorTuner.whiteBackgroundPurpleText(request.toString());
//        Properties connectorProperties;
//        try { // read data from file and put them into the connectorProperties
//            connectorProperties = ConnectorManager.getConnectorProperties(request.getConnectorId());
//        } catch (IOException e) {
//            LogHandler.logOcpp(e);
//            return new ChangeAvailabilityConfirmation(AvailabilityStatus.Rejected);
//        }
//        connectorProperties.setProperty("AvailabilityType", request.getType().name()); // Operative
//
//        ConnectorManager.setConnectorProperties(connectorProperties, request.getConnectorId());
//        switch (ChargePointStatus.valueOf(connectorProperties.getProperty("ChargePointStatus"))) {
//            case Available:
//            case Unavailable:
//                return new ChangeAvailabilityConfirmation(AvailabilityStatus.Accepted);
//            case Preparing:
//            case Charging:
//            case Reserved:
//            case SuspendedEV:
//            case SuspendedEVSE:
//            case Finishing:
//            case Faulted:
//                return new ChangeAvailabilityConfirmation(AvailabilityStatus.Scheduled);
//            default:
//                return new ChangeAvailabilityConfirmation(AvailabilityStatus.Rejected);
//        }
        return null;
    }
}
