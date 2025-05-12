package ru.promelectronika.queues;

import ru.promelectronika.dto.records.ProxyCommandDto;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ProxyQueue {
    public final  static Deque<ProxyCommandDto> queue =  new ConcurrentLinkedDeque<>();

}
