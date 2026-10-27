package com.nextup;

public class Queue<T> {

    private class Node {
        T data;
        Node next = null;

        public Node(T data) {
            this.data = data;
        }
    }

    Node front = null;
    Node back = null;

    void enqueue(T data) throws IllegalArgumentException {
        if(data == null) throw new IllegalArgumentException();

        if(back == null) {
            front = back = new Node(data);
            return;
        }

        back = back.next = new Node(data);
    }

    T dequeue() throws IndexOutOfBoundsException {
        final T value = peek();
        front = front.next;

        if(front == null) {
            back = null;
        }

        return value;
    }

    T peek() throws IndexOutOfBoundsException {
        if(front == null) throw new IndexOutOfBoundsException();
        return front.data;
    }

    boolean isEmpty() {
        return front == null;
    }

    int size() {
        int i = 0;
        for(Node node = front; node != null; node = node.next) i++;
        return i;
    }

}
