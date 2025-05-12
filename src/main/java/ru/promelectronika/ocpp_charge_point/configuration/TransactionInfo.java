package ru.promelectronika.ocpp_charge_point.configuration;

import eu.chargetime.ocpp.model.core_2_0_1.messages.TransactionEventRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class TransactionInfo {
    public static int seqNo = 0;
    private final String transactionId;
    private TransactionEventRequest request;
    private Integer remoteId;
    private boolean isStarted =false;



    public TransactionInfo(String transactionId, TransactionEventRequest request ) {
        this.transactionId = transactionId;
        this.request = request;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionInfo that = (TransactionInfo) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(request, that.request) && Objects.equals(remoteId, that.remoteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, request, remoteId);
    }

    @Override
    public String toString() {
        return "TransactionInfo{" +
                "transactionId=" + transactionId +
                '}';
    }
}
