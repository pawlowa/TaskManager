package taskmanager;

import config.ConfigReader;
import config.yaml.CustomerConfig;
import queues.QueueFactory;
import queues.QueueTypeEnum;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskManager like in an operating system.
 * The System can be multi-tenant, so there could be more than one thread pool running.
 * Otherwise, it could be Singleton.
 */
public class TaskManager {

    private final AbstractQueue<Process> runningProcessQueue;

    private TaskManager(AbstractQueue<Process> runningProcessQueue) {
        this.runningProcessQueue = runningProcessQueue;
    }

    /**
     * Creates a TaskManger from the config.
     * The current customer name is in pom.xml property customer.
     * @return taskmanager from customer config
     */
    public static TaskManager createFromCustomerConfig() {
        CustomerConfig customerConfig = ConfigReader.getCustomerConfig();
        return create(
                customerConfig.getTaskmanager().getQueue().getType(),
                customerConfig.getTaskmanager().getQueue().getMaxRunningCapacity());
    }

    /**
     * Creates a new TaskManager.
     * Production TaskManagers are created through createFromCustomerConfig only.
     *
     * @param maxCapacityRunning: The task manager should have a prefixed maximum capacity,
     *                            it can not have more than a certain number of running processes within itself.
     */
    static TaskManager create(final QueueTypeEnum queueType, final int maxCapacityRunning) {
        return new TaskManager(QueueFactory.create(queueType,maxCapacityRunning));
    }

    /**
     * Add a process which is in state CREATED. When added, its state becomes RUNNING.
     *
     * The task manager should have a prefixed maximum capacity, so it can not have more than a certain
     * number of running processes within itself.
     * This value is defined at build time.
     * The add(process) method in TM is used for it.
     *
     * @return process return the new process or null
     *                 if the underlying runningProcessQueue does not accept this element according to its strategy.
     */
    public Process add(final PriorityEnum priorityEnum) throws IllegalStateException {
        final Process process = new Process(priorityEnum);
        process.setTaskManager(this);
        return (runningProcessQueue.offer(process)) ? process : null;
    }

    /**
     * List running processes sorted by id ascending.
     */
    public List<Process> listById() {
        return listBy(Process::getPid);
    }

    /**
     * List running processes sorted by time added to TaskManager ascending.
     * (implicitly we can consider it the time in which has been added to the TM).
     * In this implematation, time of creation = time of addition.
     */
    public List<Process> listByTime() {
        // the ids are ordered by timeOfCreation = timeOdAddition
        return listById();
    }

    /**
     * List running processes sorted by priority ascending, from LOW to HIGH.
     */
    public List<Process> listByPriority() {
        return listBy(p -> p.getPriority().ordinal());
    }

    private List<Process> listBy(Function<Process, Integer> processIntegerFunction) {
        return runningProcessQueue.stream()
                .sorted(Comparator.comparing(processIntegerFunction))
                .collect(Collectors.toList());
    }

    /**
     * Kill a specific process.
     * @return killed process
     */
    public Process kill(int pid) {
        Process process = runningProcessQueue.stream()
                .filter(p -> p.getPid() == pid)
                .findAny().orElse(null);
        if (process==null) return null;
        runningProcessQueue.remove(process);
        process.destroy();
        return process;
    }

    /**
     * Kill all processes with a specific priority.
     *
     * @return killed processes
     */
    public List<Process> kill(PriorityEnum priority) {
        List<Process> processList = runningProcessQueue.stream()
                .filter(p -> p.getPriority() == priority)
                .collect(Collectors.toList());
        runningProcessQueue.removeAll(processList);
        return processList;
    }

    /**
     * Kill all running processes.
     *
     * @return killed processes
     */
    public List<Process> killAll() {
        List<Process> processList = new ArrayList<>(runningProcessQueue);
        runningProcessQueue.removeAll(processList);
        return processList;
    }

}
