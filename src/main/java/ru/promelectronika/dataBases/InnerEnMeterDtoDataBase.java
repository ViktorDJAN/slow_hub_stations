package ru.promelectronika.dataBases;

import lombok.Getter;
import ru.promelectronika.dto.InnerEnMeterDto;

import java.util.Hashtable;
import java.util.Map;


public class InnerEnMeterDtoDataBase {
    public static final Map<Integer, InnerEnMeterDto> map = new Hashtable<>();
}
