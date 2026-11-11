import static org.junit.jupiter.api.Assertions.*;

import com.holdyourspot.Party;
import com.holdyourspot.Queue;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class QueueTest {

    // N: Add one item
    @Test
    public void testEnqueue() {
        Queue<String> queue = new Queue<>();

        queue.enqueue("Smith");

        assertEquals(1, queue.size());
        assertEquals("Smith", queue.peek());
    }

    // N: Remove items in FIFO order
    @Test
    public void testDequeue() {
        Queue<String> queue = new Queue<>();

        queue.enqueue("Smith");
        queue.enqueue("Johnson");

        assertEquals("Smith", queue.dequeue());
        assertEquals("Johnson", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    // B: Cannot enqueue null
    @Test
    public void testEnqueueNull() {
        Queue<String> queue = new Queue<>();

        assertThrows(IllegalArgumentException.class,
            () -> queue.enqueue(null));
    }

    // E: Cannot dequeue empty queue
    @Test
    public void testDequeueEmpty() {
        Queue<String> queue = new Queue<>();

        assertThrows(IndexOutOfBoundsException.class,
            () -> queue.dequeue());
    }

    // N: Peek does not remove item
    @Test
    public void testPeek() {
        Queue<String> queue = new Queue<>();

        queue.enqueue("Smith");

        assertEquals("Smith", queue.peek());
        assertEquals(1, queue.size());
    }

    // E: Peek on empty queue
    @Test
    public void testPeekEmpty() {
        Queue<String> queue = new Queue<>();

        assertThrows(IndexOutOfBoundsException.class,
            () -> queue.peek());
    }

    // N: New queue is empty
    @Test
    public void testIsEmpty() {
        Queue<String> queue = new Queue<>();

        assertTrue(queue.isEmpty());

        queue.enqueue("Smith");

        assertFalse(queue.isEmpty());
    }

    // N: Size updates as items change
    @Test
    public void testSize() {
        Queue<String> queue = new Queue<>();

        assertEquals(0, queue.size());

        queue.enqueue("Smith");
        queue.enqueue("Johnson");

        assertEquals(2, queue.size());

        queue.dequeue();

        assertEquals(1, queue.size());
    }

    // N: Remove a matching party
    @Test
    public void testRemoveIf() {
        Queue<Party> queue = new Queue<>();

        queue.enqueue(new Party("Smith", 4));
        queue.enqueue(new Party("Johnson", 2));
        queue.enqueue(new Party("Williams", 3));

        queue.removeIf(p ->
            p.name().equals("Johnson"));

        assertEquals(2, queue.size());
        assertEquals("Smith", queue.dequeue().name());
        assertEquals("Williams", queue.dequeue().name());
    }

    // E: No matching item
    @Test
    public void testRemoveIfNoMatch() {
        Queue<String> queue = new Queue<>();

        queue.enqueue("Smith");
        queue.enqueue("Johnson");

        queue.removeIf(name ->
            name.equals("Williams"));

        assertEquals(2, queue.size());
    }

    // N: Enumerate elements in order
    @Test
    public void testEnumerate() {
        Queue<String> queue = new Queue<>();

        queue.enqueue("Smith");
        queue.enqueue("Johnson");

        List<String> names = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();

        queue.enumerate((name, index) -> {
            names.add(name);
            indexes.add(index);
        });

        assertEquals(
            List.of("Smith", "Johnson"), names);

        assertEquals(
            List.of(0, 1), indexes);
    }

    // E: Enumerating empty queue
    @Test
    public void testEnumerateEmpty() {
        Queue<String> queue = new Queue<>();
        List<String> names = new ArrayList<>();

        queue.enumerate((name, index) ->
            names.add(name));

        assertTrue(names.isEmpty());
    }

    // E: Reuse queue after removing last item
    // Exposes a possible bug in removeIf.
    @Test
    public void testRemoveLastThenEnqueue() {
        Queue<String> queue = new Queue<>();

        queue.enqueue("Smith");
        queue.enqueue("Johnson");

        queue.removeIf(name ->
            name.equals("Johnson"));

        queue.enqueue("Williams");

        assertEquals(2, queue.size());
        assertEquals("Smith", queue.dequeue());
        assertEquals("Williams", queue.dequeue());
    }
}
