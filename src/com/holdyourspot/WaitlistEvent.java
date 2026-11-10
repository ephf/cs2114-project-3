package com.holdyourspot;

/** Immutable description of a completed waitlist change. */
public record WaitlistEvent(Type type, int waitingCount, int ticketId, Party party) {
    public enum Type {
        ADDED, SEATED, REMOVED
    }
}
