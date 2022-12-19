/*******************************************************************************************************
 *
 * MarkupBoxBuilder.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.LinkedList;

/**
 * The Class MarkupBoxBuilder.
 */
public class MarkupBoxBuilder extends BoxBuilderImpl {

	protected void addLine(int start, int end, int offset, boolean empty) {
		if (!empty) {
			addbox0(start, end, offset);
			emptyPrevLine = isClosingTag0(start, end);
		} else
			emptyPrevLine = empty;
	}

	/**
	 * Checks if is closing tag 0.
	 *
	 * @param start the start
	 * @param end the end
	 * @return true, if is closing tag 0
	 */
	protected boolean isClosingTag0(int start, int end) {
		String line = text.substring(start, end).trim();
		return countCloseOpen(line) < 0;
	}

	/**
	 * Checks if is closing current tag.
	 *
	 * @param start the start
	 * @param end the end
	 * @return true, if is closing current tag
	 */
	// add comment tag support
	protected boolean isClosingCurrentTag(int start, int end) {
		String line = text.substring(start, end).trim();
		if (line.startsWith("</") && line.endsWith(">")) {
			String token = "<" + line.substring(2, line.length() - 1);
			for (int i = 0; i < token.length(); i++)
				if (text.charAt(currentbox.start + i) != token.charAt(i)) {
					return false;
				}
			return true;
		}

		return false;
	}

	@Override
	protected void addbox0(int start, int end, int offset) {
		if (!emptyPrevLine && isOpenTag(start, end))
			emptyPrevLine = true;
		else if (emptyPrevLine && isClosingCurrentTag(start, end))
			emptyPrevLine = false;
		super.addbox0(start, end, offset);
	}

	/**
	 * Checks if is open tag.
	 *
	 * @param start the start
	 * @param end the end
	 * @return true, if is open tag
	 */
	protected boolean isOpenTag(int start, int end) {
		String line = text.substring(start, end).trim();
		if (line.length() < 3)
			return false;
		char c = line.charAt(1);
		return (line.startsWith("<") && c != '/' && c != '?' && c != '!' && c != '%' && countCloseOpen(line) > 0);
	}


	/**
	 * Count close open.
	 *
	 * @param line the line
	 * @return the int
	 */
	protected int countCloseOpen(String line) {
		TokenStack stack = processTags(line);
		return stack.result();
	}

	/**
	 * Process tags.
	 *
	 * @param line the line
	 * @return the token stack
	 */
	protected TokenStack processTags(String line) {
		TokenStack stack = new TokenStack();
		String token = null;
		for (int i = 0; i < line.length() -1; i++) {
			if (line.startsWith("</", i)) {
				token = getWord(line, i + 2);
				stack.addClosing(token);
			} else if (line.startsWith("/>", i))
				stack.addClosing(null);
			else if (line.startsWith("<", i) && Character.isLetterOrDigit(line.charAt(i + 1))) {
				token = getWord(line, i + 1);
				stack.addOpening(token);
			}
		}
		return stack;
	}

	/**
	 * Gets the word.
	 *
	 * @param line the line
	 * @param n the n
	 * @return the word
	 */
	protected String getWord(String line, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = n; i < line.length(); i++) {
			char c = line.charAt(i);
			if (Character.isWhitespace(c) || c == '>' || c =='/')
				break;
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * The Class TokenStack.
	 */
	class TokenStack {

		/** The Constant CLOSING. */
		private static final String CLOSING = ">";
		
		/** The tokens. */
		LinkedList<String> tokens = new LinkedList<String>();

		/**
		 * Adds the closing.
		 *
		 * @param token the token
		 */
		public void addClosing(String token) {
			if (token == null) {
				if (!tokens.isEmpty() && !tokens.getLast().equals(CLOSING))
					tokens.removeLast();
				else
					tokens.add(CLOSING);
			} else {
				if (!tokens.contains(token))
					tokens.add(CLOSING);
				else
					while (!token.equals(tokens.removeLast()))
						;

			}
		}

		/**
		 * Result.
		 *
		 * @return the int
		 */
		public int result() {
			if (tokens.isEmpty())
				return 0;
			if (tokens.getLast().equals(CLOSING))
				return -1;
			return 1;
		}

		/**
		 * Adds the opening.
		 *
		 * @param token the token
		 */
		public void addOpening(String token) {
			tokens.add(token);
		}

	}
}
