package language

import data.ParsedFile

interface Language {
	/**
	 * Parses a file with the passed content.
	 */
	fun parse(fileContent: String): ParsedFile
}