package text

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
			put(parts[0], if (2 == parts.size) parts[1] else null)
		}
	}
}