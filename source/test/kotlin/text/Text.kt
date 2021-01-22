package text

import java.io.File
import java.nio.charset.Charset
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class Text {
	companion object {
		private val utf8: Charset = Charset.forName("utf8")
		private fun readText(path: String) = File(path).readText(utf8)
	}
	@Test
	fun wrapTextTest() {
		listOf(
			"lipsum" to 30
		).forEach { (name, width) ->
			assertEquals(
				readText("source/test/resources/text/$name.output.txt"),
				wrapText(readText("source/test/resources/text/$name.input.txt"), 30, Locale.ENGLISH)
			)
		}
	}
	@Test
	fun parseQueryStringTest() {
		assertEquals(
			mapOf("timezone" to "Europe/Berlin"),
			parseQueryString("timezone=Europe/Berlin")
		)
		assertEquals(
			mapOf("timezone" to ""),
			parseQueryString("timezone=")
		)
		assertEquals(
			mapOf("timezone" to null),
			parseQueryString("timezone")
		)
		assertEquals(
			mapOf("x" to "5", "y" to "-4"),
			parseQueryString("x=5&y=-4")
		)
		assertEquals(
			mapOf("query" to "l0v="),
			parseQueryString("query=l0v=")
		)
	}
}