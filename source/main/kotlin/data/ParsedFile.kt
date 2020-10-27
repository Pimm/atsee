package data

import java.lang.IllegalArgumentException

abstract class ParsedFile(val comments: List<out Comment>) {
	protected val jobs: MutableList<Job> = mutableListOf()
	protected class Job(val comment: Comment, val replacement: String)
	/**
	 * Finalises the content, with the comments replaced.
	 */
	abstract fun finalize(): String
	/**
	 * Replaces the content of the comment, which should exist in `comments`.
	 */
	open fun replace(comment: Comment, replacement: String) {
		if (-1 == comments.indexOf(comment)) {
			throw IllegalArgumentException("Passed comment does not exist in the list")
		}
		jobs.add(Job(comment, replacement))
	}
}