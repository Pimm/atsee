package data

abstract class Comment(val content: String) {
	/**
	 * The width of the characters that would appear before the content if the content of this comment were to be replaced
	 * ‒ where a tab is twice as wide as any other character.
	 */
	val indentationWidth
		get() = this.calculateIndentationWidth()
	protected abstract fun calculateIndentationWidth(): Int
}