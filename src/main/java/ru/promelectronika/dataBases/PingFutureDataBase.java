package ru.promelectronika.dataBases;


import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class PingFutureDataBase {
    public static final Map<Integer, ScheduledFuture<?>> map = new Hashtable<>();
}
