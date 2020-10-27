class Tricky {
	// @replace Single-line comment
	thingOne() {
	}
	/* @replace Multiline comment */
	thingTwo() {
	}
	/**
	 * @replace JavaDoc comment
	 */
	thingThree() {
	}
	tricky() {
		// Test strings which could be mistaken for comments.
		var string;
		string = 'This is // not a comment';
		string = 'This is /* not a comment either */';
		string = '\
		// Even this is not a comment\
		'
		string = `This is
		// not a comment`;
		string = `This is
		/**
		 * not a comment either
		 */`;
		// Test regular expressions which could be mistaken for comments.
		var regexp;
		regexp = /\d{4}[A-Z]{2}/i;
		regexp = /\d\//g;
		regexp = /[A-Z]\/* */;
		// Test a comment which could be mistaken for a regular expression.
		/*/i */
	}
}