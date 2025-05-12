package ru.promelectronika.queues;

import ru.promelectronika.ocpp_charge_point.configuration.TransactionInfo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TransactionsQueue {
    public static Deque<TransactionInfo> queue = new ConcurrentLinkedDeque<>();

}
