package queues;

public enum QueueTypeEnum {

    /** Fix size, additional elements cannot be added without removing one first **/
    FIX,

    /** manages size by pushing oldes element out (FIFO) **/
    TIME,

    /**
     * manages size by pushing lowest priority out:
     * When the max size is reached, evaluation:
     * if the new process has a higher priority compared to any of the existing one,
     * we remove the lowest priority that is the oldest,
     * otherwise we skip it.
     * **/
    PRIORITY
}
