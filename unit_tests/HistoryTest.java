import com.holdyourspot.History;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HistoryTest {

    private static <T> List<T> toList(History<T> history) {
        final List<T> list = new ArrayList<>();
        history.enumerate((item, i) -> list.add(item));
        return list;
    }

    @Test
    void zeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new History<String>(0));
    }

    @Test
    void negativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new History<String>(-1));
    }

    @Test
    void empty() {
        final History<String> history = new History<>(3);
        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
        assertTrue(toList(history).isEmpty());
    }

    @Test
    void order() {
        final History<String> history = new History<>(3);
        history.record("a");
        history.record("b");
        assertEquals(List.of("a", "b"), toList(history));
        assertEquals(2, history.size());
        assertFalse(history.isEmpty());
    }

    @Test
    void full() {
        final History<String> history = new History<>(3);
        history.record("a");
        history.record("b");
        history.record("c");
        assertEquals(List.of("a", "b", "c"), toList(history));
    }

    @Test
    void overflow() {
        final History<String> history = new History<>(3);
        for(final String s : List.of("a", "b", "c", "d", "e")) history.record(s);
        assertEquals(List.of("c", "d", "e"), toList(history));
        assertEquals(3, history.size());
    }

    @Test
    void rejectsNull() {
        final History<String> history = new History<>(3);
        assertThrows(IllegalArgumentException.class, () -> history.record(null));
        assertTrue(history.isEmpty());
    }
}