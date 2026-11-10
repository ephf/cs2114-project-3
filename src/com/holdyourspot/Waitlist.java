package com.holdyourspot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

/** A FIFO waitlist with monotonically increasing ticket numbers. */
public class Waitlist {
    private record Entry(int ticketId, Party party) {}

    private Queue<Entry> waiting = new Queue<>();
    private final List<WaitlistListener> listeners = new ArrayList<>();
    private int nextTicketId = 1;

    /** Adds a party and returns its never-reused ticket ID. */
    public int addParty(String name, int size) {
        validateName(name);
        if (size <= 0) {
            throw new IllegalArgumentException("Party size must be positive");
        }
        int ticketId = nextTicketId++;
        Party party = new Party(name, size);
        waiting.enqueue(new Entry(ticketId, party));
        notifyListeners(WaitlistEvent.Type.ADDED, ticketId, party);
        return ticketId;
    }

    /** Seats and removes the longest-waiting party. */
    public Party seatNext() {
        if (waiting.isEmpty()) {
            throw new NoSuchElementException("Waitlist is empty");
        }
        Entry entry = waiting.dequeue();
        notifyListeners(WaitlistEvent.Type.SEATED, entry.ticketId(), entry.party());
        return entry.party();
    }

    /** Returns the next party without removing it or emitting an event. */
    public Party peekNext() {
        if (waiting.isEmpty()) {
            throw new NoSuchElementException("Waitlist is empty");
        }
        return waiting.peek().party();
    }

    /** Removes a party by ticket number without changing the order of others. */
    public Party removeParty(int ticketId) {
        Queue<Entry> remaining = new Queue<>();
        final Entry[] found = new Entry[1];
        waiting.enumerate((entry, index) -> {
            if (entry.ticketId() == ticketId) {
                found[0] = entry;
            } else {
                remaining.enqueue(entry);
            }
        });
        if (found[0] == null) {
            throw new NoSuchElementException("No party has ticket " + ticketId);
        }
        // Rebuild instead of Queue.removeIf: the existing implementation
        // does not repair its tail pointer when the last item is removed.
        waiting = remaining;
        notifyListeners(WaitlistEvent.Type.REMOVED, ticketId, found[0].party());
        return found[0].party();
    }

    /** Finds parties by exact name, case-insensitively, preserving FIFO order. */
    public List<Party> findByName(String name) {
        validateName(name);
        String target = name.trim().toLowerCase(Locale.ROOT);
        List<Party> matches = new ArrayList<>();
        waiting.enumerate((entry, index) -> {
            if (entry.party().name().trim().toLowerCase(Locale.ROOT).equals(target)) {
                matches.add(entry.party());
            }
        });
        return matches;
    }

    public boolean hasPartyNamed(String name) {
        return !findByName(name).isEmpty();
    }

    /** Returns a defensive snapshot; callers cannot modify the waitlist. */
    public List<Party> getWaitingParties() {
        List<Party> parties = new ArrayList<>();
        waiting.enumerate((entry, index) -> parties.add(entry.party()));
        return parties;
    }

    public int size() {
        return waiting.size();
    }

    public boolean isEmpty() {
        return waiting.isEmpty();
    }

    public void addListener(WaitlistListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        listeners.add(listener);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Party name cannot be blank");
        }
    }

    private void notifyListeners(WaitlistEvent.Type type, int ticketId, Party party) {
        WaitlistEvent event = new WaitlistEvent(type, size(), ticketId, party);
        for (WaitlistListener listener : listeners) {
            listener.onWaitlistEvent(event);
        }
    }
}
