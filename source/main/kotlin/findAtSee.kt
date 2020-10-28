import java.net.URI
import java.net.URISyntaxException

private val atSeePattern = Regex("^@see\\h++(.++)$", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
/**
 * Returns the URI following `"@see"` in the passed input. Returns `null` if there is no `"@see"` in the input, or it
 * does not follow a valid URL.
 *
 * (If multiple `"@see"`s exist in the input, all except the first one will be ignored.)
 */
fun findAtSee(input: String): URI? {
	return atSeePattern.find(input)?.let { matchResult ->
		try {
			URI(matchResult.groups[1]!!.value)
		} catch (exception: URISyntaxException) {
			null
		}
	}
}