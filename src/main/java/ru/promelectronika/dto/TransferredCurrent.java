package ru.promelectronika.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferredCurrent {
    public static volatile double maximumCurrentLimitA;
}
