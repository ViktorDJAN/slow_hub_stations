package ru.promelectronika;

import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;

import java.io.IOException;

public class Testing {
    public static void main(String[] args) throws IOException {
        LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "BootNotification REQ: {}" + "request");



    }
}
