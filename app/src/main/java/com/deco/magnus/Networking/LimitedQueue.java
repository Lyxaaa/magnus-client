package com.deco.magnus.Networking;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ForwardingQueue;
import com.google.common.collect.Iterables;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

/**
 * A non-blocking queue which automatically evicts elements from the head of the queue when
 * attempting to add new elements onto the queue and it is full. This queue orders elements FIFO
 * (first-in-first-out). This data structure is logically equivalent to a circular buffer (i.e.,
 * cyclic buffer or ring buffer).
 *
 * <p>An evicting queue must be configured with a maximum size. Each time an element is added to a
 * full queue, the queue automatically removes its head element. This is different from conventional
 * bounded queues, which either block or reject new elements when full.
 *
 * <p>This class is not thread-safe, and does not accept null elements.
 *
 * @author Kurt Alfred Kluever
 * @since 15.0
 */
@Beta
@GwtCompatible
public final class LimitedQueue<E> extends ForwardingQueue<E> implements Serializable {

    private final Queue<E> delegate;

    @VisibleForTesting final int maxSize;

    public LimitedQueue(int maxSize) {
        checkArgument(maxSize >= 0, "maxSize (%s) must >= 0", maxSize);
        this.delegate = new ArrayDeque<>(maxSize);
        this.maxSize = maxSize;
    }

    /**
     * Returns the number of additional elements that this queue can accept without evicting; zero if
     * the queue is currently full.
     *
     * @since 16.0
     */
    public int remainingCapacity() {
        return maxSize - size();
    }

    @Override
    protected Queue<E> delegate() {
        return delegate;
    }

    /**
     * Adds the given element to this queue. If the queue is currently full, the element at the head
     * of the queue is evicted to make room.
     *
     * @return {@code true} always
     */
    @Override
    @CanIgnoreReturnValue
    public boolean offer(E e) {
        return add(e);
    }

    /**
     * Adds the given element to this queue. If the queue is currently full, the element at the head
     * of the queue is evicted to make room.
     *
     * @return {@code true} always
     */
    @Override
    @CanIgnoreReturnValue
    public boolean add(E e) {
        checkNotNull(e); // check before removing
        if (maxSize == 0) {
            return true;
        }
        if (size() == maxSize) {
            delegate.remove();
        }
        delegate.add(e);
        return true;
    }

    @Override
    @CanIgnoreReturnValue
    public boolean addAll(Collection<? extends E> collection) {
        int size = collection.size();
        if (size >= maxSize) {
            clear();
            return Iterables.addAll(this, Iterables.skip(collection, size - maxSize));
        }
        return standardAddAll(collection);
    }

    @Override
    public boolean contains(Object object) {
        return delegate().contains(checkNotNull(object));
    }

    @Override
    @CanIgnoreReturnValue
    public boolean remove(Object object) {
        return delegate().remove(checkNotNull(object));
    }

    // TODO(kak): Do we want to checkNotNull each element in containsAll, removeAll, and retainAll?

    private static final long serialVersionUID = 0L;
}