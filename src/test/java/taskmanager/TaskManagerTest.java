package taskmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import queues.QueueTypeEnum;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static queues.QueueTypeEnum.TIME;
import static taskmanager.PriorityEnum.*;
import static taskmanager.TaskManangerTestUtil.*;

public class TaskManagerTest {

    @Test
    public void testTaskManagerCreation() {
        assertNotNull(TaskManager.createFromCustomerConfig());
    }

    @ParameterizedTest
    @EnumSource(QueueTypeEnum.class)
    public void testAdd(final QueueTypeEnum queueTypeEnum) {
        final TaskManager taskManager = TaskManager.create(queueTypeEnum, 3);
        assertNotNull(taskManager.add(MEDIUM));
        assertNotNull(taskManager.add(HIGH));
        assertNotNull(taskManager.add(LOW));
        final List<Process> processes = taskManager.listById();
        final int size = processes.size();
        assertEquals(3, size);
        if (queueTypeEnum!=TIME) assertNull(taskManager.add(LOW));
        else                     assertNotNull(taskManager.add(LOW));
    }

    @ParameterizedTest
    @EnumSource(QueueTypeEnum.class)
    public void testListById(final QueueTypeEnum queueTypeEnum) {
        final TaskManager taskManager = TaskManager.create(queueTypeEnum, 20);
        assertTrue(taskManager.listById().isEmpty());

        taskManager.add(HIGH);
        taskManager.add(HIGH);

        final List<Integer> pids = mapPidList((taskManager.listById()));
        assertTrue (pids.get(0) < pids.get(1));
    }

    @ParameterizedTest
    @EnumSource(QueueTypeEnum.class)
    public void testListByPriority(final QueueTypeEnum queueTypeEnum) {
        final TaskManager taskManager = TaskManager.create(queueTypeEnum, 20);
        assertEquals(List.of(), taskManager.listByPriority());

        taskManager.add(LOW);
        taskManager.add(MEDIUM);
        taskManager.add(HIGH);
        taskManager.add(HIGH);
        taskManager.add(MEDIUM);
        taskManager.add(LOW);

        final List<PriorityEnum> priorityList = mapPriorityList((taskManager.listByPriority()));
        assertEquals ( List.of(LOW, LOW, MEDIUM, MEDIUM, HIGH, HIGH), priorityList);
    }

    @ParameterizedTest
    @EnumSource(QueueTypeEnum.class)
    public void testKillProcess(final QueueTypeEnum queueTypeEnum) {
        final TaskManager taskManager = TaskManager.create(queueTypeEnum, 3);

        final Process p1 = taskManager.add(LOW);
        assertNotNull(p1);
        assertEquals(LOW, taskManager.listById().get(0).getPriority());

        assertEquals(p1.getPid(), p1.kill().getPid());
        assertEquals(0, taskManager.listById().size());
    }

    @ParameterizedTest
    @EnumSource(QueueTypeEnum.class)
    public void testKillOne(final QueueTypeEnum queueTypeEnum) {
        final TaskManager taskManager = TaskManager.create(queueTypeEnum, 3);

        final Process p1 = taskManager.add(LOW);
        assertNotNull(p1);
        assertEquals(LOW, taskManager.listById().get(0).getPriority());

        assertEquals(p1.getPid(), taskManager.kill(p1.getPid()).getPid());
        assertEquals(0, taskManager.listById().size());
    }

    @ParameterizedTest
    @EnumSource(QueueTypeEnum.class)
    public void testKillGroupAndAll(final QueueTypeEnum queueTypeEnum) {
        final TaskManager taskManager = TaskManager.create(queueTypeEnum, 20);

        final int numLow = 3;
        IntStream.range(0, numLow).forEach(i -> taskManager.add(LOW));
        final int numMedium = 4;
        IntStream.range(0, numMedium).forEach(i -> taskManager.add(MEDIUM));
        final int numHigh = 5;
        IntStream.range(0, numHigh).forEach(i -> taskManager.add(HIGH));

        final List<Process> mediumProcesses = taskManager.kill(MEDIUM);
        final List<PriorityEnum> mediumPriorities = mapPriorityList(mediumProcesses);
        assertEquals(numLow + numHigh, taskManager.listById().size());
        assertEquals(numMedium, Collections.frequency(mediumPriorities, MEDIUM));

        final List<Process> allProcesses = taskManager.killAll();
        final List<PriorityEnum> allPriorities = mapPriorityList(allProcesses);
        assertEquals(numLow + numHigh, allProcesses.size());
        assertEquals(0, taskManager.listById().size());
        assertEquals(numLow, Collections.frequency(allPriorities, LOW));
        assertEquals(numHigh, Collections.frequency(allPriorities, HIGH));
    }


 

}
