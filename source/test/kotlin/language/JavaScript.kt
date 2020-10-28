package language

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import kotlin.test.Test
import kotlin.test.assertEquals

class JavaScriptTest {
	companion object {
		private val utf8: Charset = Charset.forName("utf8")
		private fun readText(path: String) = File(path).readText(utf8)
	}
	@Test
	fun parseTest() {
		val javaScript = JavaScript()
		listOf(
			"mapsort", "tricky"
		).forEach {name ->
			val parsedFile = javaScript.parse(readText("source/test/resources/javascript/$name/input.js"))
			// Compare the parsed file to the expected comments from the JSON document. (Note that this actually compares JSON
			// as text, so it could provide false negatives if the comments file is edited manually.)
			assertEquals(
				readText("source/test/resources/javascript/$name/comments.json"),
				JSONArray().apply {
					parsedFile.comments.forEach {
						put(JSONObject(mapOf("content" to it.content, "indentationWidth" to it.indentationWidth)))
					}
				}.toString(2)
			)
			// Replace comments which start with @replace.
			parsedFile.comments.filter { it.content.startsWith("@replace") }.forEach {
				parsedFile.replace(it, "Replaced!")
			}
			// Compare the finalised file with the output JS file.
			assertEquals(
				readText("source/test/resources/javascript/$name/output.js"),
				parsedFile.finalize()
			)
		}
	}
}