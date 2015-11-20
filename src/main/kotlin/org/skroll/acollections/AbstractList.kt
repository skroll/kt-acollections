package org.skroll.acollections

import java.util.NoSuchElementException
import java.util.RandomAccess

/**
 * This class provides a skeletal implementation of the [List] interface
 * to minimize the effort required to implement this interface backed by a
 * "random access" data store (such as an array).
 *
 * For sequential access data (such as a linked list), [AbstractSequentialList]
 * should be used in preference to this class.
 */
public abstract class AbstractList<E> : AbstractCollection<E>(), List<E> {
    override fun indexOf(element: E): Int {
        val it = listIterator()
        while (it.hasNext()) {
            if (it.next() == element)
                return it.previousIndex()
        }
        return -1
    }

    override fun lastIndexOf(element: E): Int {
        val it = listIterator(size)
        while (it.hasPrevious()) {
            if (it.previous() == element)
                return it.nextIndex()
        }
        return -1
    }

    /**
     * Returns an iterator over the elements in this list (in proper sequence).
     *
     * This implementation merely returns a list iterator over the list.
     *
     * @return an iterator over the elements in this list (in proper sequence)
     */
    override fun iterator(): Iterator<E> = Itr()
    override fun listIterator(): ListIterator<E> = listIterator(0)
    override fun listIterator(index: Int): ListIterator<E> {
        rangeCheckForAdd(index)
        return ListItr(index)
    }

    open inner class Itr(protected var cursor: Int) : Iterator<E> {
        protected var lastRet = -1

        constructor() : this(0)

        override fun hasNext(): Boolean = cursor != size
        override fun next(): E {
            try {
                val i = cursor
                val next = get(i)
                lastRet = i
                cursor = i + 1
                return next
            } catch (e: IndexOutOfBoundsException) {
                throw NoSuchElementException()
            }
        }
    }

    inner class ListItr(index: Int) : Itr(index), ListIterator<E> {
        override fun hasPrevious(): Boolean = cursor != 0

        override fun previous(): E {
            try {
                val i = cursor - 1
                val previous = get(i)
                lastRet = i
                cursor = i
                return previous
            } catch (unused: IndexOutOfBoundsException) {
                throw NoSuchElementException()
            }
        }

        override fun nextIndex(): Int = cursor
        override fun previousIndex(): Int = cursor - 1
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> {
        return if (this is RandomAccess) RandomAccessSubList<E>(this, fromIndex, toIndex) else SubList(this, fromIndex, toIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this)
            return true
        if (other !is List<*>)
            return false

        val e1 = listIterator()
        val e2 = other.listIterator()
        while (e1.hasNext() && e2.hasNext()) {
            if (e1.next() != e2.next())
                return false
        }
        return !(e1.hasNext() || e2.hasNext())
    }

    override fun hashCode(): Int {
        var hashCode = 1
        for (e in this)
            hashCode = 31 * hashCode + (e?.hashCode() ?: 0)
        return hashCode
    }

    private fun rangeCheckForAdd(index: Int) {
        if (index < 0 || index > size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
    }
}

private open class SubList<E>(private val list: AbstractList<E>, fromIndex: Int, toIndex: Int) : AbstractList<E>() {
    private val offset = fromIndex
    private val subSize: Int

    init {
        if (fromIndex < 0)
            throw IndexOutOfBoundsException("fromIndex = $fromIndex")
        if (toIndex > list.size)
            throw IndexOutOfBoundsException("toIndex = $toIndex")
        if (fromIndex > toIndex)
            throw IllegalAccessException("fromIndex($fromIndex) > toIndex($toIndex)")

        subSize = toIndex - fromIndex
    }

    override val size: Int
        get() = subSize

    override fun get(index: Int): E {
        rangeCheck(index)
        return list[index + offset]
    }

    override fun iterator() = listIterator()

    override fun listIterator(index: Int): ListIterator<E> {
        rangeCheckForAdd(index)

        return object : ListIterator<E> {
            val i = list.listIterator(index + offset)

            override fun hasNext(): Boolean = nextIndex() < subSize

            override fun next(): E {
                if (hasNext())
                    return i.next()
                else
                    throw NoSuchElementException()
            }

            override fun hasPrevious(): Boolean = previousIndex() >= 0

            override fun previous(): E {
                if (hasPrevious())
                    return i.previous()
                else
                    throw NoSuchElementException()
            }

            override fun nextIndex(): Int = i.nextIndex() - offset

            override fun previousIndex(): Int = i.previousIndex() - offset
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> {
        return SubList(this, fromIndex, toIndex)
    }

    private fun rangeCheck(index: Int) {
        if (index < 0 || index >= subSize)
            throw IndexOutOfBoundsException(outOfBoundsMsg(index))
    }

    private fun rangeCheckForAdd(index: Int) {
        if (index < 0 || index > subSize)
            throw IndexOutOfBoundsException(outOfBoundsMsg(index))
    }

    private fun outOfBoundsMsg(index: Int) = "Index: $index, Size: $subSize"
}

private class RandomAccessSubList<E>(list: AbstractList<E>, fromIndex: Int, toIndex: Int) : SubList<E>(list, fromIndex, toIndex), RandomAccess {
    override fun subList(fromIndex: Int, toIndex: Int): List<E> {
        return RandomAccessSubList(this, fromIndex, toIndex)
    }
}