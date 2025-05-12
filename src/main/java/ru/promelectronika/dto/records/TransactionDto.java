package ru.promelectronika.dto.records;

import eu.chargetime.ocpp.model.core_2_0_1.data_types.IdTokenType;
import eu.chargetime.ocpp.model.core_2_0_1.data_types.TransactionType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.TransactionEventEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.TriggerReasonEnumType;
import ru.promelectronika.dto.ExchangeableData;

public record TransactionDto(TransactionEventEnumType eventType,
                             String timestamp,
                             TriggerReasonEnumType triggerReason,
                             Integer seqNo,
                             TransactionType transactionInfo,
                             IdTokenType idToken) implements ExchangeableData {
}
