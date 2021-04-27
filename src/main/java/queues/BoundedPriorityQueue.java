package queues;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Priority queue with a maximum capacity.
 */
public class BoundedPriorityQueue<E> extends PriorityBlockingQueue<E> {

    final AtomicInteger maxCapacity;
    final Comparator<? super E> comparator;
    private final ReentrantLock lock = new ReentrantLock();

    public BoundedPriorityQueue(int maxCapacity, Comparator<? super E> comparator) {
        super(maxCapacity, comparator);
        this.maxCapacity = new AtomicInteger(maxCapacity);
        this.comparator = comparator;
    }

    /**
     * Inserts the specified element into this priority queue
     * if Queue has free capacity or existing elements are smaller (Comparator).
     *
     * @param e the element to add
     * @return {@code true}    Queue has free capacity or existing elements are smaller (Comparator).
     *         {@code false}   otherwise
     * @throws ClassCastException if the specified element cannot be compared
     *         with elements currently in the priority queue according to the
     *         priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean offer (E  e) {
        try {
            final ReentrantLock lock = this.lock;
            lock.lock();

            return (remainingCapacity()>0) ? super.offer(e) : rearrangeQueue(e);

        } finally {
            lock.unlock();
        }
    }

    /**
     * Looks if existing elements are smaller than the new one.
     */
    private boolean rearrangeQueue(E e) {
        if (comparator.compare(peek(), e)>=0) return false;

        poll();
        offer(e);
        return true;
    }

    @Override
    public int remainingCapacity() {
        return maxCapacity.get() - size();
    }

}
