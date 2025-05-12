package ru.promelectronika.dto.records;

import ru.promelectronika.dto.ExchangeableData;
import ru.promelectronika.enums.HandlerEnumType;

public record ProxyCommandDto(HandlerEnumType enumType, Integer command, ExchangeableData data){
}
