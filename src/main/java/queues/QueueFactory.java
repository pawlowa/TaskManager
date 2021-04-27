package queues;

import taskmanager.Process;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueFactory {

    public static AbstractQueue<Process> create(final QueueTypeEnum queueType, int maxCapacityRunning){
        switch (queueType) {

            case FIX:
                // Linked queues typically have higher throughput than array-based queues but
                // less predictable performance in most concurrent applications.
                // possible Alternative ArrayBlockingQueue
                return new LinkedBlockingQueue<>(maxCapacityRunning);

            case TIME:
                return new BoundedPriorityQueue<>(maxCapacityRunning, Comparator.comparing(Process::getPid));

            case PRIORITY:
                return new BoundedPriorityQueue<>(maxCapacityRunning, Comparator.comparing(Process::getPriority));

            default:
                throw new IllegalArgumentException("You added a new QueueType - please also add a case.");
        }
    }

}
