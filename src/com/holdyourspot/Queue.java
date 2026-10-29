package com.holdyourspot;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Queue<T> {

    private class Node {
        T data;
        Node next = null;

        public Node(T data) {
            this.data = data;
        }
    }

    private Node front = null;
    private Node back = null;

    public void enqueue(T data) throws IllegalArgumentException {
        if(data == null) throw new IllegalArgumentException();

        if(back == null) {
            front = back = new Node(data);
            return;
        }

        back = back.next = new Node(data);
    }

    public T dequeue() throws IndexOutOfBoundsException {
        final T value = peek();
        front = front.next;

        if(front == null) {
            back = null;
        }

        return value;
    }

    public T peek() throws IndexOutOfBoundsException {
        if(front == null) throw new IndexOutOfBoundsException();
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        int i = 0;
        for(Node node = front; node != null; node = node.next) i++;
        return i;
    }

    public void removeIf(Function<T, Boolean> predicate) {
        for(Node prev = null, cur = front; cur != null; prev = cur, cur = cur.next) {
            if(!predicate.apply(cur.data)) continue;

            if(prev == null) {
                front = front.next;
            } else {
                prev.next = cur.next;
            }
            break;
        }
    }

    public void enumerate(BiConsumer<T, Integer> consumer) {
        int i = 0;
        for(Node cur = front; cur != null; cur = cur.next) {
            consumer.accept(cur.data, i++);
        }
    }

}
