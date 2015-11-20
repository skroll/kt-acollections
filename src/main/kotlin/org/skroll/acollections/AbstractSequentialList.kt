package org.skroll.acollections

import kotlin.Iterator
import java.util.NoSuchElementException

/**
 * This class provides a skeletal implementation of the [List] interface
 * to minimize the effort required to implement this interface backed by
 * a "sequential access" data store (such as a linked list). For random
 * access data (such as an array), [AbstractList] should be used in
 * preference to this class.
 *
 * This class is the opposite of the [AbstractList] class in the sense
 * that it implements the "random access" method ([get]) on top of the
 * list's list iterator, instead of the other way around.
 *
 * To implement a list the programmer needs only to extend this class
 * and provide implementations for the [listIterator] method and [size]
 * property.
 */
public abstract class AbstractSequentialList<E> : AbstractList<E>() {
    /**
     * Returns the element at the specified position in this list.
     *
     * This implementation first gets a list iterator pointing to the
     * indexed element (with `listIterator(index)`). Then it gets the
     * element using `ListIterator.next` and returns it.
     *
     * @throws IndexOutOfBoundsException
     */
    override fun get(index: Int): E {
        try {
            return listIterator(index).next()
        } catch (e: NoSuchElementException) {
            throw IndexOutOfBoundsException("Index: $index")
        }
    }

    override fun iterator(): Iterator<E> {
        return listIterator()
    }

    /**
     * Returns a list iterator over the elements in this list (in proper sequence).
     *
     * @param index index of the first element to be returned from the list
     *        iterator (by a call to the `next` method)
     * @return a list iterator over the elements in this list (in proper
     *         sequence)
     * @throws IndexOutOfBoundsException
     */
    override abstract fun listIterator(index: Int): ListIterator<E>
}
