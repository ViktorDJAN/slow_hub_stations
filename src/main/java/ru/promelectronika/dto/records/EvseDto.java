package ru.promelectronika.dto.records;

import ru.promelectronika.dto.ExchangeableData;

public record EvseDto(Integer evseId, Integer connectorId, Integer connectorState, String timestamp) implements ExchangeableData {
}

