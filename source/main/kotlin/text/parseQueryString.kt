package text

import java.nio.ByteBuffer

private val encodedCharacterPattern = Regex("(?:%[\\da-f]{2})+", RegexOption.IGNORE_CASE)
/**
 * Returns a version of the string with escaped sequences decoded to the characters they represent. For example:
 * `"Europe%2FBerlin"` is converted `"Europe/Berlin"`.
 *
 * Escaped sequences which are malformed (anything other than exactly one `'%'` followed by exactly two hexadecimal
 * characters) are not decoded and are left as-is. Escaped sequences which do have the correct format but are not valid
 * UTF-8 are replaced by the replacement character (`'�'`).
 */
private fun CharSequence.decodeAsUriComponent(): String {
	val resultBuilder = StringBuilder(length)
	var bytes: ByteBuffer? = null
	// Find all encoded characters.
	var index = 0;
	var matchResult = encodedCharacterPattern.find(this, index)
	while (null != matchResult) {
		// Add the substring before the matched escaped sequence to the result.
		resultBuilder.append(this, index, matchResult.range.first)
		// Collect the bytes which form the escaped sequence.
		bytes = (matchResult.value.length / 3).let { capacity ->
			// (Either allocate a new buffer or recycle the previous one if it has the correct capacity.)
			if (bytes?.capacity() == capacity) bytes!!.rewind() else ByteBuffer.allocate(capacity)
		}
		index = matchResult.range.first
		do {
			bytes!!.put(Integer.parseInt(substring(index + 1, index + 3), 0x10).toByte())
			index += 3
		} while (index < matchResult.range.last)
		// Decode those bytes, and add them to the result.
		resultBuilder.append(Charsets.UTF_8.decode(bytes.rewind()))
		// Find the next escaped sequence.
		matchResult = encodedCharacterPattern.find(this, index)
	}
	// Return the result, after adding the substring after the last matched escaped sequence.
	return resultBuilder.append(this, index, length)
	                    .toString()
}
/**
 * Parses the passed query string to a map which maps the keys to the values.
 */
fun parseQueryString(input: String): Map<String, String?> {
	// If you plan on copying this function to another project, note that it creates n + 1 unnecessary short-lived lists
	// and can be optimised.
	val pairs = input.split('&')
	return HashMap<String, String?>(pairs.size).apply {
		pairs.forEach { pair ->
			val parts = pair.split('=', limit = 2)
			put(parts[0].decodeAsUriComponent(), if (2 == parts.size) parts[1].decodeAsUriComponent() else null)
		}
	}
}