package test.com.holdyourspot;

import com.holdyourspot.Party;
import com.holdyourspot.Waitlist;
import com.holdyourspot.WaitlistEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

class WaitlistTest {
    // W1 - N
    @Test void addPartyNormal() {
        Waitlist w = new Waitlist();
        List<WaitlistEvent> events = new ArrayList<>();
        w.addListener(events::add);
        assertEquals(1, w.addParty("Smith", 4));
        assertEquals(2, w.addParty("Jones", 2));
        assertEquals(2, w.size());
        assertEquals(2, events.size());
        assertEquals(WaitlistEvent.Type.ADDED, events.get(0).type());
        assertEquals(WaitlistEvent.Type.ADDED, events.get(1).type());
        assertEquals(1, events.get(0).waitingCount());
        assertEquals(2, events.get(1).waitingCount());
    }

    // W2 - B
    @Test void addPartyBadInput() {
        Waitlist w = new Waitlist();
        List<WaitlistEvent> events = new ArrayList<>();
        w.addListener(events::add);
        assertThrows(IllegalArgumentException.class, () -> w.addParty("", 2));
        assertThrows(IllegalArgumentException.class, () -> w.addParty("Lee", 0));
        assertEquals(0, w.size());
        assertTrue(events.isEmpty());
        assertEquals(1, w.addParty("Smith", 4));
    }

    // W3 - N
    @Test void seatNextNormal() {
        Waitlist w = new Waitlist();
        w.addParty("Smith", 4);
        w.addParty("Jones", 2);
        List<WaitlistEvent> events = new ArrayList<>();
        w.addListener(events::add);
        assertEquals("Smith", w.seatNext().name());
        assertEquals(1, w.size());
        assertEquals(1, events.size());
        assertEquals(WaitlistEvent.Type.SEATED, events.get(0).type());
    }

    // W4 - B
    @Test void seatNextEmpty() {
        Waitlist w = new Waitlist();
        List<WaitlistEvent> events = new ArrayList<>();
        w.addListener(events::add);
        assertThrows(NoSuchElementException.class, w::seatNext);
        assertTrue(events.isEmpty());
    }

    // W5 - N
    @Test void peekNextNormal() {
        Waitlist w = new Waitlist();
        w.addParty("Smith", 4);
        w.addParty("Jones", 2);
        List<WaitlistEvent> events = new ArrayList<>();
        w.addListener(events::add);
        assertEquals("Smith", w.peekNext().name());
        assertEquals(2, w.size());
        assertTrue(events.isEmpty());
    }

    // W6 - B
    @Test void peekNextEmpty() {
        Waitlist w = new Waitlist();
        assertThrows(NoSuchElementException.class, w::peekNext);
    }

    // W7 - N
    @Test void removePartyNormal() {
        Waitlist w = new Waitlist();
        w.addParty("Smith", 4);
        int ticket2 = w.addParty("Jones", 2);
        List<WaitlistEvent> events = new ArrayList<>();
        w.addListener(events::add);
        assertEquals("Jones", w.removeParty(ticket2).name());
        assertEquals(List.of("Smith"), w.getWaitingParties().stream().map(Party::name).toList());
        assertEquals(1, events.size());
        assertEquals(WaitlistEvent.Type.REMOVED, events.get(0).type());
    }

    // W8 - B
    @Test void removePartyInvalidTicket() {
        Waitlist w = new Waitlist();
        w.addParty("Smith", 4);
        List<WaitlistEvent> events = new ArrayList<>();
        w.addListener(events::add);
        assertThrows(NoSuchElementException.class, () -> w.removeParty(99));
        assertEquals(1, w.size());
        assertTrue(events.isEmpty());
    }

    // W9 - N
    @Test void findByNameNormal() {
        Waitlist w = new Waitlist();
        w.addParty("Smith", 4);
        w.addParty("Jones", 2);
        List<Party> found = w.findByName("jones");
        assertEquals(1, found.size());
        assertEquals("Jones", found.get(0).name());
    }

    // W10 - B
    @Test void findByNameNull() {
        Waitlist w = new Waitlist();
        assertThrows(IllegalArgumentException.class, () -> w.findByName(null));
    }

    // W11 - N
    @Test void hasPartyNamedNormal() {
        Waitlist w = new Waitlist();
        w.addParty("Smith", 4);
        assertTrue(w.hasPartyNamed("SMITH"));
        assertFalse(w.hasPartyNamed("Lee"));
    }

    // W12 - B
    @Test void hasPartyNamedBadInput() {
        Waitlist w = new Waitlist();
        assertThrows(IllegalArgumentException.class, () -> w.hasPartyNamed(null));
        assertThrows(IllegalArgumentException.class, () -> w.hasPartyNamed(""));
    }

    // W13 - N
    @Test void getWaitingPartiesNormal() {
        Waitlist w = new Waitlist();
        w.addParty("A", 2);
        w.addParty("B", 3);
        w.addParty("C", 4);
        w.seatNext();
        assertEquals(List.of("B", "C"), w.getWaitingParties().stream().map(Party::name).toList());
    }

    // W14 - E
    @Test void getWaitingPartiesDefensiveCopy() {
        Waitlist w = new Waitlist();
        List<Party> snapshot = w.getWaitingParties();
        assertTrue(snapshot.isEmpty());
        try {
            snapshot.add(new Party("Fake", 2));
        } catch (UnsupportedOperationException ignored) {
            // Immutable snapshots also satisfy the contract.
        }
        assertTrue(w.isEmpty());
    }

    // W15 - N/E
    @Test void sizeAndIsEmpty() {
        Waitlist w = new Waitlist();
        assertEquals(0, w.size());
        assertTrue(w.isEmpty());
        w.addParty("Smith", 4);
        assertEquals(1, w.size());
        assertFalse(w.isEmpty());
        w.seatNext();
        assertEquals(0, w.size());
        assertTrue(w.isEmpty());
    }

    // W16 - N
    @Test void addListenerNormal() {
        Waitlist w = new Waitlist();
        List<WaitlistEvent> events = new ArrayList<>();
        w.addListener(events::add);
        w.addParty("Smith", 4);
        assertEquals(1, events.size());
        assertEquals(WaitlistEvent.Type.ADDED, events.get(0).type());
        assertEquals(1, events.get(0).waitingCount());
    }

    // W17 - B
    @Test void addListenerNull() {
        Waitlist w = new Waitlist();
        assertThrows(IllegalArgumentException.class, () -> w.addListener(null));
    }

    // W18 - E
    @Test void ticketIdsNeverReused() {
        Waitlist w = new Waitlist();
        int first = w.addParty("A", 2);
        w.seatNext();
        int second = w.addParty("B", 3);
        assertEquals(1, first);
        assertEquals(2, second);
    }

    // Additional regression: removal of tail must not break future enqueues.
    @Test void removeTailThenAddAgain() {
        Waitlist w = new Waitlist();
        w.addParty("A", 2);
        int bTicket = w.addParty("B", 2);
        w.removeParty(bTicket);
        w.addParty("C", 2);
        assertEquals(List.of("A", "C"), w.getWaitingParties().stream().map(Party::name).toList());
    }
}
