package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core.ResetConfirmation;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetStatus;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigs;


import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;


/** CENTRAL_SYSTEM REQUESTS CHARGE_POINT TO RESET ITSELF.MIGHT BE HARD AND SOFT RESETS
 */
public class ResetRequestHandler {

    private final ResetRequest request;
    private final ChargePointOcpp chargePoint;

    public ResetRequestHandler(ResetRequest request, ChargePointOcpp chargePoint) {
        this.request = request;
        this.chargePoint = chargePoint;
    }

    public ResetConfirmation getResponse() {
        //DirectoryStream allows you to iterate through files in a directory without loading all the files
        //into memory at once, which is especially important for working with large directories.
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Path.of(OcppConfigs.TRANSACTIONS_DIRECTORY))) {
            if (dirStream.iterator().hasNext()) {
                File directory = new File(OcppConfigs.TRANSACTIONS_DIRECTORY);
                File[] transactionFiles = directory.listFiles();
                if (transactionFiles != null) {
                    for (File f : transactionFiles) {
        // the file name is exactly the transaction number
                        int tId = Integer.parseInt(f.getName());
//                        chargePoint.sendStopTransaction(tId, null); // custom
//                        chargePoint.sendStopTransactionRequest(new StopTransactionRequest()); // custom
                    }
                }
                //HARD RESET = is PHYSICAL RESET OF CHARGE POINT beginning with relay etc...
//                if (request.getType() == ResetType.Hard) {
//                    HardReset.startHardReset();
//                }
            }
            // todo Why it is like that ?????
//            chargePoint.getChargeCapable().reset(request.getType());
            return new ResetConfirmation(ResetStatus.Accepted);
        } catch (IOException e) {
            LogHandler.logOcpp(e);
            return new ResetConfirmation(ResetStatus.Rejected);
        }
    }
}
