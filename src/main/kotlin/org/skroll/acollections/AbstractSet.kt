package org.skroll.acollections

public abstract class AbstractSet<E> : AbstractCollection<E>(), Set<E> {
    override fun equals(other: Any?): Boolean {
        if (other === this)
            return true

        if (other !is Set<*>)
            return false

        if (other.size != size)
            return false

        try {
            return containsAllRaw(other)
        } catch (unused: ClassCastException) {
            return false
        } catch (unused: NullPointerException) {
            return false
        }
    }

    override fun hashCode(): Int {
        var h = 0
        this.forEach { if (it != null) h += it.hashCode() }
        return h
    }
}
