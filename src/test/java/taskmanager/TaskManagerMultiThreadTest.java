package taskmanager;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import queues.QueueTypeEnum;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static queues.QueueTestFactory.toTestString;
import static taskmanager.PriorityEnum.*;

/**
 * Simple multithread test in order too watch TaskManager in action.
 */
public class TaskManagerMultiThreadTest {

    public static final int MAX_CAPACITY_RUNNING = 10;

    @ParameterizedTest
    @EnumSource(QueueTypeEnum.class)
    void test(QueueTypeEnum queueTypeEnum) throws InterruptedException {
        TaskManager taskManager = TaskManager.create(queueTypeEnum, MAX_CAPACITY_RUNNING);
        ExecutorService executor = Executors.newWorkStealingPool(3);

        executor.execute(() -> admin(taskManager));
        executor.execute(() -> produce(taskManager));
        executor.execute(() -> consume(taskManager, HIGH));

        executor.awaitTermination(50, TimeUnit.MILLISECONDS);
    }

    private void admin(TaskManager taskManager) {
        while (true) {
            final List<Process> processes = taskManager.listByTime();
            System.out.println("Admin: " + taskManager.listById().size() + ": "
                    + toTestString(processes.stream()));
        }
    }

    private void produce(TaskManager taskManager) {
        while (true) {
            final Process pLow = taskManager.add(LOW);
            final Process pMedium = taskManager.add(MEDIUM);
            final Process pHigh = taskManager.add(HIGH);
            System.out.println(" + " + toTestString(pLow) + " " + toTestString(pMedium) + " " + toTestString(pHigh));
            assertTrue(taskManager.listById().size() <= MAX_CAPACITY_RUNNING);
        }
    }

    private void consume(TaskManager taskManager, PriorityEnum priorityEnum) {
        try {
            while (true) {
                final List<Process> killed = taskManager.kill(priorityEnum);
                System.out.println(" - #killedHigh=" + killed.size());
                Thread.sleep(3);
                assertTrue(taskManager.listById().size() <= MAX_CAPACITY_RUNNING);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
