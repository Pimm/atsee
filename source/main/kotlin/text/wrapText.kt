package text

import java.lang.IndexOutOfBoundsException
import java.text.BreakIterator
import java.util.*
import kotlin.math.roundToInt

/**
 * Wraps the passed text, inserting line breaks to try and make the text less wide than the passed width.
 */
val wrapText = run {
	val horizontalWhitespacePattern = Regex("\\h")
	val verticalWhitespacePattern = Regex("\\v")
	class Range(var start: Int, var endExclusive: Int) {
		fun copy(): Range = Range(start, endExclusive)
		/**
		 * Sets `start` to `endExclusive`, and `endExclusive` to the passed value.
		 */
		fun push(newEndExclusive: Int) {
			start = endExclusive
			endExclusive = newEndExclusive
		}
	}
	/**
	 * An implementation of `CharSequence` that points to a single character within another `CharSequence`.
	 */
	class CharPointer(val original: CharSequence) : CharSequence {
		var index: Int? = null
			private set
		override val length: Int
			get() = 1
		override fun get(index: Int): Char = original[this.index!!].also {
			if (0 != index) {
				throw IndexOutOfBoundsException("index must be 0")
			}
		}
		/**
		 * Moves the pointer to the passed index within the original `CharSequence`.
		 */
		fun move(index: Int): CharSequence {
			this.index = index
			return this
		}
		override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = this.also {
			if (0 != startIndex || 1 != endIndex) {
				throw IndexOutOfBoundsException("subSequence may only be called for 0‒1")
			}
		}
	}
	fun(input: String, width: Int, locale: Locale): String {
		val resultBuilder = StringBuilder((input.length * 1.1).roundToInt())
		val pointer = CharPointer(input)
		// Use a line iterator to find places where line breaks can be inserted.
		val lineIterator = BreakIterator.getLineInstance(locale).apply { setText(input) }
		val current = Range(lineIterator.first(), lineIterator.next())
		val beforeWhitespace = current.copy()
		var lineLength = 0
		while (BreakIterator.DONE != current.endExclusive) {
			// Find the index of the first character which is not part of the current segment.
			while (horizontalWhitespacePattern.matches(pointer.move(beforeWhitespace.endExclusive - 1))) {
				beforeWhitespace.endExclusive--
			}
			// If the current segment would push the current line over the edge, add a line break.
			var segmentWidth = getWidth(input, beforeWhitespace.start, beforeWhitespace.endExclusive)
			if (lineLength + segmentWidth > width) {
				resultBuilder.append('\n')
				beforeWhitespace.start = current.start
				segmentWidth = getWidth(input, beforeWhitespace.start, beforeWhitespace.endExclusive)
				lineLength = 0
			}
			// Add the current segment to the result.
			resultBuilder.append(input, beforeWhitespace.start, beforeWhitespace.endExclusive)
			// Set the line length accordingly (depending on whether the current segment ends in a line break).
			if (verticalWhitespacePattern.matches(pointer.move(current.endExclusive - 1))) {
				lineLength = 0
			} else {
				lineLength += segmentWidth
			}
			// Move to the next segment.
			current.push(lineIterator.next())
			beforeWhitespace.push(current.endExclusive)
		}
		return resultBuilder.toString()
	}
}