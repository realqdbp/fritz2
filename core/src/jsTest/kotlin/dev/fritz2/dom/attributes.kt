package dev.fritz2.dom

import dev.fritz2.binding.const
import dev.fritz2.dom.html.render
import dev.fritz2.identification.uniqueId
import dev.fritz2.test.initDocument
import dev.fritz2.test.runTest
import dev.fritz2.test.targetId
import kotlinx.coroutines.delay
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class AttributeTests {

    @Test
    fun testAttributes() = runTest {
        initDocument()

        val testRange = (0..4)
        val testId = uniqueId()

        val (name0, value0) = "test0" to "value0"
        val (name1, value1) = "test1" to "value1"
        val (name2, values2) = "test2" to testRange.map { "value$it" }
        val (name3, values3) = "test3" to testRange.map { "value$it" }

        render {
            div(id = testId) {
                attr(name0, value0)
                const(value1).bindAttr(name1)
                attr("data-$name0", value0)
                const(value1).bindAttr("data-$name1")
                attr(name2, values2)
                const(values3).bindAttr(name3)

                const(true).bindAttr("test4")
                const(false).bindAttr("test5")
                const(true).bindAttr("test6", "foo")
            }
        }.mount(targetId)

        delay(200)

        val element = document.getElementById(testId).unsafeCast<HTMLDivElement>()

        assertEquals(testId, element.id)
        assertEquals("div", element.localName)

        assertEquals(value0, element.getAttribute(name0))
        assertEquals(value1, element.getAttribute(name1))

        assertEquals(value0, element.getAttribute("data-$name0"))
        assertEquals(value1, element.getAttribute("data-$name1"))

        assertEquals(values2.joinToString(separator = " "), element.getAttribute(name2))
        assertEquals(values3.joinToString(separator = " "), element.getAttribute(name3))

        assertEquals(value0, element.getAttribute(name0))
        assertEquals(value1, element.getAttribute(name1))

        assertTrue(element.hasAttribute("test4"))
        assertEquals("", element.getAttribute("test4"))

        assertFalse(element.hasAttribute("test5"))

        assertTrue(element.hasAttribute("test6"))
        assertEquals("foo", element.getAttribute("test6"))
    }
}