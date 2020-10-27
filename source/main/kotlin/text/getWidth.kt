package text

/**
 * Returns the width of the passed input, where a tab is twice as wide as any other character. (The other characters are
 * all 1 wide, including the zero-width space.)
 */
fun getWidth(input: String): Int = getWidth(input, 0, input.length)
/**
 * Returns the width of the passed input, where a tab is twice as wide as any other character. (The other characters are
 * all 1 wide, including the zero-width space.)
 */
fun getWidth(input: String, start: Int, end: Int): Int {
	var result = end - start;
	for (index in start until end) {
		if ('\t' == input[index]) {
			result++
		}
	}
	return result
}