package taskmanager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManangerTestUtil {

    static List<PriorityEnum> mapPriorityList(List<Process> processes) {
        return processes.stream().map(Process::getPriority).collect(Collectors.toList());
    }

    static List<Integer> mapPidList(List<Process> processes) {
        return processes.stream().map(Process::getPid).collect(Collectors.toList());
    }

    /**
     * maps String representation of process list to process list
     * @param processListString, e.g. "MEDIUM HIGH LOW"
     * @return e.g. (1,MEDIUM) (2,HIGH) (3,LOW)
     */
    public static List<Process> toProcessList(String processListString) {
        return Arrays.stream(processListString.split(" "))
                .filter(s -> !(s.trim().isEmpty()))
                .map(name -> createTestProcess(PriorityEnum.valueOf(name)))
                .collect(Collectors.toList());
    }

    /**
     * Creates a process only for Tests. In Production, only the TaskManager creates Processes.
     */
    public static Process createTestProcess(PriorityEnum priorityEnum){
        return new Process(priorityEnum);
    }
}
