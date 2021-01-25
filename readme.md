# atsee

Automated documentation insertion for source code

---

Copying over documentation from spec to code or codebase to codebase takes up time and energy you would rather spend on other things. Keeping those copies in sync even more so.

_atsee_ was designed to automate the insertion of documentation into source code.

Instead of writing:
```
/**
 * Returns the first element which …
 */
```
you write:
```
/**
 * @see docs:some-function
 */
```

Tools such as _Javadoc_ extract documentation from code, and in that way _atsee_ can be seen as its counterpart.

#### This framework does:
 * parse your source code ‒ finding comment blocks with `@see` in them ‒ and
 * replace those comment blocks with your documentation.

#### This framework does not:
 * retrieve the documentation to insert. You will have to supply that logic yourself.

## Limitations

 * Currently, only JavaScript and TypeScript are supported.
 * The `JavaScript` class does not handle nested templates correctly, such as:
```javascript
`Hello, ${get(`${id}.name`)}`
```
 * Replaced comment blocks which appear anywhere other than at the start of the line will not produce pretty-looking
   results:
```javascript
// @see docs:timeout
const timeout = 500;

/**
 * This looks good!
 */
const timeout = 500;
```
```javascript
const timeout = 500; // @see docs:timeout

const timeout = 500; /**
  * This doesn't look great…
  */
```
 * The `JavaScript` class parses in a naive way, causing it to recheck the same characters a few times. If performance
   proves to be an issue, perhaps the regular expression matching should be applied more economically.


# License (X11/MIT)
Copyright (c) 2020-2021 Pimm "de Chinchilla" Hogeling

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

**The Software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. in no event shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the Software or the use or other dealings in the Software.**