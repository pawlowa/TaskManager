package taskmanager;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * The process is immutable, it is generated with a priority and will die with this priority –
 * each process has a kill() method that will destroy it
 */
public class Process {

    private static final AtomicInteger nextPid = new AtomicInteger(1);

    /**
     * unique unmodifiable identifier (PID)
     */
    private final int pid;

    /**
     * priority (low, medium, high)
     */
    private final PriorityEnum priority;

    private TaskManager taskManager;

    Process(PriorityEnum priority) {
        this.pid = nextPid.getAndIncrement();
        this.priority = priority;
    }

    void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * Destroys process, which in Java means to make it unreachable and therefore eligable for garbage collection.
     * Finalizers are deprecated for good reason.
     */
    public Process kill() {
        taskManager.kill(pid);
        return this;
    }

    void destroy() {
        // finalize any costly resources here so hanging around till garbage collection will not cost much,
        // like java.lang.ProcessImpl.processHandle;
    }

    public int getPid() {
        return pid;
    }

    public PriorityEnum getPriority() {
        return priority;
    }

}
