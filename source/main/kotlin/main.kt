import language.JavaScript
import language.Language
import java.io.File
import java.net.URI
import java.nio.charset.Charset

/**
 * Maps file extensions to the appropriate language.
 */
val languages = run {
	val javaScript = JavaScript()
	mapOf<String, Language>("js" to javaScript, "ts" to javaScript)
}
val utf8: Charset = Charset.forName("utf8")
fun main(arguments: Array<String>) {
	visit(File(arguments[0]))
}
/**
 * Processes the passed file, or all of the files in the directory if it is a directory.
 */
fun visit(file: File) {
	if (file.isDirectory) {
		return file.listFiles()!!.forEach(::visit)
	}
	process(
		file,
		languages[file.extension] ?: return println("Skipping ${file.name}")
	)
}
/**
 * Processes the passed file with the passed language.
 */
fun process(file: File, language: Language) {
	// Parse the file.
	val parsedFile = language.parse(file.readText(utf8))
	// Determine whether any of the comments contains "@see". Find an appropriate replacement for the comment if so.
	var bail = true
	parsedFile.comments.forEach { comment ->
		findAtSee(comment.content)?.let { atSeeUri ->
			getReplacement(atSeeUri, comment.content, comment.indentationWidth)?.let { replacement ->
				parsedFile.replace(comment, replacement)
				bail = false
			}
		}
	}
	// (Skip the rest if nothing has to be done for this file.)
	if (bail) return
	// Finalise and overwrite the file.
	file.writeText(parsedFile.finalize(), utf8)
}
/**
 * Returns the appropriate replacement for a comment which includes the passed @see URI.
 */
fun getReplacement(atSeeUri: URI, original: String, indentationWidth: Int): String? {
	// TODO Implement
	return null
}