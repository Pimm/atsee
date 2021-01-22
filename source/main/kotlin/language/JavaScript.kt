package language

import data.ParsedFile
import text.getWidth

class JavaScript : Language {
	private class LiveMatchResult(val pattern: Regex, private val content: String, index: Int) {
		var current: MatchResult? = pattern.find(content, index)
			private set
		/**
		 * Executes a new find (updating `current`) starting at the passed index, unless the current match starts at or
		 * after said passed index already (in which case this method does nothing).
		 */
		fun advance(index: Int) {
			if (current?.range?.first ?: Integer.MAX_VALUE >= index) return
			current = pattern.find(content, index)
		}
	}
	private class Comment(content: String, val indentation: String, val range: IntRange) : data.Comment(content) {
		companion object {
			private val leadingWhitespacePattern = Regex("^\\h*+")
			private val leadingWhitespaceAsteriskPattern = Regex("^\\h*+(?:\\*\\h*+)?")
			private val verticalWhitespacePattern = Regex("\\v++")
			/**
			 * Builds a comment from a single line match result.
			 */
			fun createFromSingleLine(matchResult: MatchResult): Comment {
				val content = matchResult.groups[2]!!.value
				// Trim off any leading whitespace (between "//" and the first non-space character).
				val trimmedContent = content.substring(leadingWhitespacePattern.find(content)!!.range.last + 1)
				return Comment(trimmedContent, matchResult.groups[1]!!.value, matchResult.range)
			}
			/**
			 * Builds a comment from a multiline match result, JavaDoc style or not.
			 */
			fun createFromMultiline(matchResult: MatchResult): Comment {
				val content = matchResult.groups[2]!!.value
				// Trim off any leading whitespace and asterisk (such as " * ").
				val trimmedContent = content.split(verticalWhitespacePattern).mapNotNull { line ->
					val matchResult = leadingWhitespaceAsteriskPattern.find(line)!!
					if (matchResult.value.length == line.length) {
						null
					} else {
						line.substring(matchResult.range.last + 1)
					}
				}.joinToString("\n")
				return Comment(trimmedContent, matchResult.groups[1]!!.value, matchResult.range)
			}
		}
		override fun calculateIndentationWidth(): Int = getWidth(indentation) + " * ".length
		override fun toString(): String = "Comment(\"$content\" range=$range)"
	}
	companion object {
		// Matches: // This code is amazing.
		private val singleLineCommentPattern = Regex("(\\h*+)//(.*)")
		// Matches: /* Who wrote this? A monkey? */.
		private val multiLineCommentPattern = Regex("(\\h*+)/\\*((?:[^*]|\\*(?!/))*+)\\*/")
		// Matches: 'Arnhem'.
		private val stringLiteralPattern = Regex("(['\"])(?:(?!\\1)[^\\\\\\v]|\\\\.)*+\\1", RegexOption.DOT_MATCHES_ALL)
		// Matches: `Arnhem`. TODO Correctly handle nested template literals: `Hello, ${get(`${id}.name`)}`
		private val templateLiteralPattern = Regex("`(?:(?!`)[^\\\\]|\\\\.)*+`", RegexOption.DOT_MATCHES_ALL)
		// Matches: /\d{4}[A-Z]{2}/. (The multiline comment pattern takes priority, to ensure /**/ is not mistaken for a
		// regexp.)
		private val regexpPattern = Regex("/(?:\\\\/|[^/])++/[\$_\\u200C\\u200D\\p{javaUnicodeIdentifierPart}]*")
	}
	override fun parse(fileContent: String): ParsedFile {
		val comments = mutableListOf<Comment>()
		val matchResults = listOf(
			LiveMatchResult(stringLiteralPattern, fileContent, 0),
			LiveMatchResult(templateLiteralPattern, fileContent, 0),
			LiveMatchResult(singleLineCommentPattern, fileContent, 0),
			LiveMatchResult(multiLineCommentPattern, fileContent, 0),
			LiveMatchResult(regexpPattern, fileContent, 0)
		)
		while (true) {
			// Find the first match result (with the lowest index).
			val firstMatchResult = matchResults.filter { null != it.current }.minByOrNull {
				it.current!!.range.first
				// (If there is no match, the input does not contain anything of interest anymore.)
			} ?: break
			// If the first match is a comment, parse it and add it to the result.
			when (firstMatchResult.pattern) {
				singleLineCommentPattern -> comments.add(Comment.createFromSingleLine(firstMatchResult.current!!))
				multiLineCommentPattern -> comments.add(Comment.createFromMultiline(firstMatchResult.current!!))
			}
			// Update all of the match results, ensuring they all start after the part of the input that was just processed.
			(firstMatchResult.current!!.range.last + 1).let { index -> matchResults.forEach { it.advance(index) } }
		}
		// Return an object which contains the comments and is capable of replacing the content of said comments.
		return object : ParsedFile(comments) {
			private val verticalWhitespacePattern = Regex("\\v")
			private val horizontalWhitespaceLinePattern = Regex("^\\h*+$")
			override fun finalize(): String {
				// Sort the jobs by the place the comment has in the content.
				jobs.sortBy { (it.comment as Comment).range.first }
				// Apply the jobs added through the replace method.
				val resultBuilder = StringBuilder(fileContent.length shl 1)
				var index = 0
				jobs.forEach { job ->
					val comment = (job.comment as Comment)
					// Add the substring to the result before this comment.
					resultBuilder.append(fileContent, index, comment.range.first)
					// Add the replacement for this comment.
					             .append(comment.indentation).append("/**\n")
					job.replacement.split(verticalWhitespacePattern).forEach { replacementLine ->
						if (horizontalWhitespaceLinePattern.matches(replacementLine)) {
							resultBuilder.append(comment.indentation).append(" *\n")
						} else {
							resultBuilder.append(comment.indentation).append(" * ").append(replacementLine).append('\n')
						}
					}
					resultBuilder.append(comment.indentation).append(" */")
					// Increase the index.
					index = comment.range.last + 1
				}
				// Add the substring to the result that comes after the last replaced comment.
				resultBuilder.append(fileContent, index, fileContent.length)
				return resultBuilder.toString()
			}
		}
	}
}