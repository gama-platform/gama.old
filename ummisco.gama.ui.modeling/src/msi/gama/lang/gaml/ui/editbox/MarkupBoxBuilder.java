/*********************************************************************************************
 *
 * 'MarkupBoxBuilder.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.LinkedList;

public class MarkupBoxBuilder extends BoxBuilderImpl {

	protected void addLine(int start, int end, int offset, boolean empty) {
		if (!empty) {
			addbox0(start, end, offset);
			emptyPrevLine = isClosingTag0(start, end);
		} else
			emptyPrevLine = empty;
	}

	protected boolean isClosingTag0(int start, int end) {
		String line = text.substring(start, end).trim();
		return countCloseOpen(line) < 0;
	}

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

	protected boolean isOpenTag(int start, int end) {
		String line = text.substring(start, end).trim();
		if (line.length() < 3)
			return false;
		char c = line.charAt(1);
		return (line.startsWith("<") && c != '/' && c != '?' && c != '!' && c != '%' && countCloseOpen(line) > 0);
	}


	protected int countCloseOpen(String line) {
		TokenStack stack = processTags(line);
		return stack.result();
	}

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

	class TokenStack {

		private static final String CLOSING = ">";
		LinkedList<String> tokens = new LinkedList<String>();

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

		public int result() {
			if (tokens.isEmpty())
				return 0;
			if (tokens.getLast().equals(CLOSING))
				return -1;
			return 1;
		}

		public void addOpening(String token) {
			tokens.add(token);
		}

	}
}
