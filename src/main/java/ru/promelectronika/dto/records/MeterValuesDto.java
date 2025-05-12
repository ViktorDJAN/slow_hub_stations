package ru.promelectronika.dto.records;

import ru.promelectronika.dto.ExchangeableData;

public record MeterValuesDto(Integer evseId,double deliveredPower,
                             double currentPower ) implements ExchangeableData {
}
