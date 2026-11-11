import static org.junit.jupiter.api.Assertions.*;

import com.holdyourspot.Party;
import org.junit.jupiter.api.Test;

public class PartyTest {

    // N: Normal party creation
    @Test
    public void testCreateParty() {
        Party party = new Party("Smith", 4);

        assertEquals("Smith", party.name());
        assertEquals(4, party.size());
    }

    // N: Two parties with same values are equal
    @Test
    public void testPartyEquality() {
        Party first = new Party("Johnson", 3);
        Party second = new Party("Johnson", 3);

        assertEquals(first, second);
    }

    // E: Party containing one person
    @Test
    public void testSinglePersonParty() {
        Party party = new Party("Alex", 1);

        assertEquals(1, party.size());
    }

    // E: Empty name is currently accepted
    @Test
    public void testEmptyName() {
        Party party = new Party("", 2);

        assertEquals("", party.name());
    }

    // B: Negative size is currently accepted
    // This documents missing validation.
    @Test
    public void testNegativeSize() {
        Party party = new Party("Smith", -1);

        assertEquals(-1, party.size());
    }

    // B: Null name is currently accepted
    // This documents missing validation.
    @Test
    public void testNullName() {
        Party party = new Party(null, 2);

        assertNull(party.name());
    }
}
