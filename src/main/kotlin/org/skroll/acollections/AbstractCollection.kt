package org.skroll.acollections

import kotlin.Collection

/**
 * A skeletal implementation of the [Collection] interface, to minimize the
 * effort required to implement the [Collection] interface.
 */
public abstract class AbstractCollection<E> : Collection<E> {
    /**
     * Returns `true` if the collection is empty (contains no elements), `false` otherwise.
     */
    override fun isEmpty(): Boolean = size == 0

    /**
     * Checks if the specified element is contained in this collection.
     * @param element element whose presence in this collection is to be tested
     * @return `true` if this collection contains the specified element
     */
    override fun contains(element: E): Boolean {
        for (e in this) {
            if (e == element) return true
        }

        return false
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        for (element in elements) {
            if (!contains(element))
                return false
        }
        return true
    }
}

