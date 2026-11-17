package com.holdyourspot;

import java.util.function.BiConsumer;

public class History<T> {

    private final Queue<T> records = new Queue<>();
    private final int capacity;

    public History(int capacity) throws IllegalArgumentException {
        if(capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
    }

    public void record(T data) {
        records.enqueue(data);
        if(records.size() > capacity) records.dequeue();
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    public int size() {
        return records.size();
    }

    public void enumerate(BiConsumer<T, Integer> consumer) {
        records.enumerate(consumer);
    }

}