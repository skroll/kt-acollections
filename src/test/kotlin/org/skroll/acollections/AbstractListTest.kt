package org.skroll.acollections

import org.junit.Test
import kotlin.test.assertEquals

public class AbstractListTest {
    @Test
    public fun test() {
        val list = object : AbstractList<String>() {
            private val contents = listOf("A", "B", "C", "D", "E")

            override val size: Int get() = contents.size

            override fun get(index: Int): String {
                return contents[index]
            }
        }

        assertEquals(5, list.size)
        val sublist = list.subList(0, 1)
        assertEquals(1, sublist.size)
        assertEquals("A", list[0])
        assertEquals("A", sublist[0])
        assertEquals("E", list[4])
    }
}
