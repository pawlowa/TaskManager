package queues;

import taskmanager.Process;
import taskmanager.TaskManangerTestUtil;

import java.util.AbstractQueue;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueueTestFactory {


    /**
     * maps process list to its String representation
     * @param stream (1,MEDIUM) (2,HIGH) (3,LOW)
     * @return e.g. "MEDIUM HIGH LOW"
     */
    public static String toTestString(final Stream<Process> stream) {
        return stream
                .map(QueueTestFactory::toTestString)
                .collect(Collectors.joining(" "));
    }

    public static String toTestString(Process p) {
        return (p==null) ? "null" : String.format("(%d,%s)", p.getPid(), p.getPriority().name());
    }

    public static AbstractQueue<Process> create(final QueueTypeEnum queueType, String processListString) {
        List<Process> processList = TaskManangerTestUtil.toProcessList(processListString);
        int size = processList.size();
        AbstractQueue<Process> queue = QueueFactory.create(queueType, size);
        queue.addAll(processList);
        return queue;
    }

}
