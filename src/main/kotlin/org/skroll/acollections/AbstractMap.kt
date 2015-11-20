package org.skroll.acollections

import kotlin.Map

public abstract class AbstractMap<K, V> : Map<K, V> {
    /**
     * Returns the number of key/value pairs in the map.
     *
     * @implementation
     * This implementation returns `entries.size`.
     */
    override val size: Int get() = entries.size

    /**
     * Returns `true` if the map is empty (contains no elements), `false` otherwise.
     *
     * @implementation
     * This implementation returns `size == 0`.
     */
    override fun isEmpty(): Boolean = size == 0

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     *
     * @implementation
     * This implementation iterates over [entries] searching for an entry
     * with the specified value. If such an entry is found, `true` is
     * returned. If the iteration terminates without finding such an entry,
     * `false` is returned. Note that this implementation requires linear
     * time in the size of the map.
     */
    override fun containsValue(value: V): Boolean {
        for ((k, v) in entries) {
            if (v == value)
                return true
        }

        return false
    }

    /**
     * Returns `true` if the map contains the specified [key].
     *
     * @implementation
     * This implementation iterates over [entries] searching for an entry
     * with the specified key. If such an entry is found, `true` is
     * returned. If the iteration terminates without finding such an entry,
     * `false` is returned. Note that this implementation requires linear
     * time in the size of the map; many implementations will override
     * this method.
     */
    override fun containsKey(key: K): Boolean {
        for ((k, v) in entries) {
            if (k == key)
                return true
        }

        return false
    }

    /**
     * Returns the value corresponding to the given [key], or `null` if such
     * a key is not present in the map.
     *
     * @implementation
     * This implementation iterates over [entries] searching for an
     * entry with the specified key. If such an entry is found, the
     * entry's value is returned. If the iteration terminates without
     * finding such an entry, `null` is returned. Note that this
     * implementation requires linear time in the size of the map;
     * many implementations will override this method.
     */
    override operator fun get(key: K): V? {
        for ((k, v) in entries) {
            if (key == k)
                return v
        }
        return null
    }

    @Transient @Volatile private var keysCache: Set<K>? = null
    @Transient @Volatile private var valuesCache: Collection<V>? = null

    /**
     * Returns a [Set] of all keys in this map.
     *
     * @implementation
     * This implementation returns a set that subclasses [AbstractSet].
     * The subclass's iterator method returns a "wrapper object" over this
     * map's [entries] iterator. The `size` property delegates to this map's
     * `size` property and the `contains` method delegates to this map's
     * `containsKey` method.
     *
     * The set is created the first time this method is called, and returned
     * in response to all subsequent calls. No synchronization is preformed,
     * so there is a slight chance that multiple calls to this method will
     * not all return the same set.
     */
    override val keys: Set<K>
        get() {
            if (keysCache == null) {
                keysCache = object : AbstractSet<K>() {
                    override fun iterator(): Iterator<K> = object : Iterator<K> {
                        private val i = entries.iterator()
                        override fun hasNext(): Boolean = i.hasNext()
                        override fun next(): K = i.next().key
                    }

                    override val size: Int
                        get() = this@AbstractMap.size

                    override fun isEmpty(): Boolean = this@AbstractMap.isEmpty()

                    override fun contains(element: K): Boolean = this@AbstractMap.containsKey(element)
                }
            }

            return keysCache!!
        }

    /**
     * Returns a [Collection] of all values in this map. Note that this
     * collection may contain duplicate values.
     *
     * @implementation
     * This implementation returns a set that subclasses [AbstractCollection].
     * The subclass's iterator method returns a "wrapper object" over this
     * map's [entries] iterator. The `size` property delegates to this map's
     * `size` property and the `contains` method delegates to this map's
     * `containsValue` method.
     *
     * The collection is created the first time this method is called, and
     * returned in response to all subsequent calls. No synchronization is
     * preformed, so there is a slight chance that multiple calls to this
     * method will not all return the same set.
     */
    override val values: Collection<V>
        get() {
            if (valuesCache == null) {
                valuesCache = object : AbstractCollection<V>() {
                    override fun iterator(): Iterator<V> = object : Iterator<V> {
                        private val i = entries.iterator()
                        override fun hasNext(): Boolean = i.hasNext()
                        override fun next(): V = i.next().value
                    }

                    override val size: Int = this@AbstractMap.size
                    override fun isEmpty(): Boolean = this@AbstractMap.isEmpty()
                    override fun contains(element: V): Boolean = this@AbstractMap.containsValue(element)
                }
            }

            return valuesCache!!
        }

    /**
     * Compares the specified object with this map for equality.
     *
     * @param other object to be compared for equality with this map
     * @return `true` if the specified object is equal to this map
     */
    override fun equals(other: Any?): Boolean {
        if (other === this)
            return true

        if (other !is Map<*, *>)
            return false

        if (other.size != size)
            return false

        try {
            for ((key, value) in entries) {
                if (value != other.getRaw(key))
                    return false
            }
        } catch (unused: ClassCastException) {
            return false
        } catch (unused: NullPointerException) {
            return false
        }

        return true
    }

    /**
     * Returns the hash code value for this map.
     *
     * @return the hash code value for this map
     */
    override fun hashCode(): Int {
        var h = 0
        entries.forEach { h += it.hashCode() }
        return h
    }

    /**
     * Returns a string representation of this map.
     *
     * The string representation consists of a list of key-value mappings
     * in the order returned by the map's [entries] view's iterator, enclosed
     * in braces (`{}`). Adjacent mappings are separated by the characters
     * `", "` (command and space). Each key-value mapping is rendered as the
     * key followed by an equals sign (`"="`) followed by the associated value.
     *
     * @return a string representation of this map
     */
    override fun toString(): String {
        val it = entries.iterator()
        if (!it.hasNext())
            return "{}"

        val sb = StringBuilder()
        sb.append('{')

        for ((key, value) in it) {
            sb.append(if (key == this) "(this Map)" else key)
            sb.append('=')
            sb.append(if (value == this) "(this Map" else value)
            if (!it.hasNext())
                break
            sb.append(", ")
        }

        return sb.append('}').toString()
    }

    /**
     * A Entry maintaining a key and a value.
     *
     * @constructor Creates an entry representing a mapping from the specified
     *              key to the specified value.
     */
    public class SimpleEntry<out K, out V>(override val key: K, override val value: V) : Map.Entry<K, V> {
        /**
         * Creates an entry representing the same mapping as the specified entry.
         *
         * @param entry the entry to copy
         */
        constructor(entry: Map.Entry<K, V>) : this(entry.key, entry.value)

        /**
         * Compares the specified object with this entry for equality.
         *
         * @param other object to be compared for equality with this map entry
         * @return `true` if the specified object is equal to this map entry
         * @see [hashCode]
         */
        override fun equals(other: Any?): Boolean {
            if (other !is Map.Entry<*, *>)
                return false

            return key == other.key && value == other.value
        }

        /**
         * Returns the hash code value for this map entry.
         *
         * @return the hash code value for this map entry
         * @see [equals]
         */
        override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)

        /**
         * Returns a [String] representation of this map entry.
         *
         * This implementation returns the string representation of this
         * entry's key followed by the equals character (`=`) followed
         * by the string representation of this entry's value.
         *
         * @return a [String] representation of this map entry
         */
        override fun toString(): String = "$key=$value"
    }
}

