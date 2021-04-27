package queues;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import taskmanager.PriorityEnum;
import taskmanager.Process;

import java.util.AbstractQueue;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static queues.QueueTypeEnum.*;
import static taskmanager.PriorityEnum.*;
import static taskmanager.TaskManangerTestUtil.createTestProcess;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QueueTest {

    @Test
    @Order(1)
    public void testTaskManagerFix() {
        AbstractQueue<Process> queue = QueueTestFactory.create(FIX, "MEDIUM HIGH LOW");
        assertEquals("(1,MEDIUM) (2,HIGH) (3,LOW)", QueueTestFactory.toTestString(queue.stream()));
        final IllegalStateException queueFull =
                assertThrows(IllegalStateException.class, () -> queue.add(createTestProcess(HIGH)));
        assertEquals("Queue full", queueFull.getMessage());
        assertFalse(queue.offer(createTestProcess(HIGH)));
        assertEquals("(1,MEDIUM) (2,HIGH) (3,LOW)", QueueTestFactory.toTestString(queue.stream()));
    }

    @Test
    @Order(2)
    public void testTaskManagerTime() {
        AbstractQueue<Process> queue = QueueTestFactory.create(TIME, "MEDIUM HIGH LOW");
        assertQueue(queue, 6);

        assertTrue(queue.add(createTestProcess(HIGH)));
        assertQueue(queue, 7);

        assertTrue(queue.add(createTestProcess(LOW)));
        assertQueue(queue, 8);

        assertTrue(queue.add(createTestProcess(MEDIUM)));
        assertQueue(queue, 9);

        assertEquals("(9,HIGH) (10,LOW) (11,MEDIUM)", QueueTestFactory.toTestString(pollProcessList(queue).stream()));
    }
    @Test
    @Order(3)
    public void testTaskManagerPriority() {
        AbstractQueue<Process> queue = QueueTestFactory.create(PRIORITY, "MEDIUM HIGH LOW");
        assertQueue(queue, LOW);

        queue.add(createTestProcess(MEDIUM));
        assertQueue(queue, MEDIUM);

        assertFalse(queue.add(createTestProcess(LOW)));
        assertQueue(queue, MEDIUM);

        assertTrue(queue.add(createTestProcess(HIGH)));
        assertQueue(queue, MEDIUM);

        assertTrue(queue.add(createTestProcess(HIGH)));
        assertQueue(queue, HIGH);
    }

    @Test
    @Order(4)
    public void testTaskManagerFixZero() {
        assertThrows(IllegalArgumentException.class, () -> QueueTestFactory.create(FIX, ""));
    }

    private List<Process> pollProcessList(AbstractQueue<Process> queue) {
        return IntStream.range(0, queue.size()).mapToObj(i -> queue.poll()).collect(Collectors.toList());
    }

    // Ordering of whole queue is not garanteed, just the peek,
    // therefor we do not compare strings
    private void assertQueue(AbstractQueue<Process> queue, int pid) {
        assert queue.peek() != null;
        assertEquals(pid, queue.peek().getPid());
        assertEquals(3, queue.size());
    }

    private void assertQueue(AbstractQueue<Process> queue, PriorityEnum priorityEnum) {
        assert queue.peek() != null;
        assertEquals(priorityEnum, queue.peek().getPriority());
        assertEquals(3, queue.size());
    }

}
