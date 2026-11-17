package com.holdyourspot;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/** A FIFO waitlist with monotonically increasing ticket numbers. */
public class Waitlist {
    private record Entry(int ticketId, Party party) {}

    private final Queue<Entry> waiting = new Queue<>();
    private final List<WaitlistListener> listeners = new ArrayList<>();
    private int nextTicketId = 1;

    /** Adds a party and returns its never-reused ticket ID. */
    public int addParty(String name, int size) throws IllegalArgumentException {
        validateName(name);
        if (size <= 0) throw new IllegalArgumentException("Party size must be positive");

        final int ticketId = nextTicketId++;
        final Party party = new Party(name, size);
        waiting.enqueue(new Entry(ticketId, party));

        notifyListeners(WaitlistEvent.Type.ADDED, ticketId, party);
        return ticketId;
    }

    /** Seats and removes the longest-waiting party. */
    public Party seatNext() throws IndexOutOfBoundsException {
        final Entry entry = waiting.dequeue();
        notifyListeners(WaitlistEvent.Type.SEATED, entry.ticketId(), entry.party());
        return entry.party();
    }

    /** Returns the next party without removing it or emitting an event. */
    public Party peekNext() throws IndexOutOfBoundsException {
        return waiting.peek().party();
    }

    /** Removes a party by ticket number without changing the order of others. */
    public Party removeParty(int ticketId) throws NoSuchElementException {
        final var removed = waiting.removeIf(entry -> entry.ticketId() == ticketId);
        if(removed == null) throw new NoSuchElementException();

        notifyListeners(WaitlistEvent.Type.REMOVED, ticketId, removed.party());
        return removed.party();
    }

    /** Finds parties by name if they start with argument `name`, case-insensitively, preserving FIFO order. */
    public List<Party> searchForName(String name) {
        validateName(name);
        final String target = name.trim().toLowerCase();

        final List<Party> matches = new ArrayList<>();
        waiting.enumerate((entry, _) -> {
            if(entry.party().name().trim().toLowerCase().startsWith(target)) {
                matches.add(entry.party());
            }
        });

        return matches;
    }

    public boolean hasPartyNamed(String name) {
        return !searchForName(name).isEmpty();
    }

    /** Returns a defensive snapshot; callers cannot modify the waitlist. */
    public List<Party> getWaitingParties() {
        List<Party> parties = new ArrayList<>();
        waiting.enumerate((entry, _) -> parties.add(entry.party()));
        return parties;
    }

    public int size() {
        return waiting.size();
    }

    public boolean isEmpty() {
        return waiting.isEmpty();
    }

    public void addListener(WaitlistListener listener) throws IllegalArgumentException {
        if(listener == null) throw new IllegalArgumentException("Listener cannot be null");
        listeners.add(listener);
    }

    private static void validateName(String name) throws IllegalArgumentException {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Party name cannot be blank");
    }

    private void notifyListeners(WaitlistEvent.Type type, int ticketId, Party party) {
        final var event = new WaitlistEvent(type, size(), ticketId, party);
        for (final var listener : listeners) listener.onWaitlistEvent(event);
    }
}
