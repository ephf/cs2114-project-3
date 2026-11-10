package com.holdyourspot;

/** Receives notifications when parties are added, seated, or removed. */
@FunctionalInterface
public interface WaitlistListener {
    void onWaitlistEvent(WaitlistEvent event);
}
