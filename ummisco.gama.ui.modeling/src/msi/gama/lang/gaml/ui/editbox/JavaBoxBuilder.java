/*********************************************************************************************
 *
 * 'JavaBoxBuilder.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
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

public class JavaBoxBuilder extends BoxBuilderImpl {

	@Override
	protected void addLine(final int s, final int end, final int o, final boolean empty) {
		int start = s;
		int offset = o;
		// if (!empty && !startLineComment(start,end,offset,empty)) {
		if ( !empty ) {
			if ( startLineComment(start, end, offset, empty) ) {
				int n = 2;
				char c = 0;
				int newOffset = 0;
				while (start + n < end && Character.isWhitespace(c = text.charAt(start + n))) {
					newOffset += c == '\t' ? tabSize : 1;
					n++;
				}
				updateEnds(start, end, newOffset + 2);
				return;
			}
			if ( text.charAt(start) == '*' ) {
				emptyPrevLine = !commentStarts(currentbox.start, currentbox.end);
				if ( !emptyPrevLine ) {
					if ( currentbox.offset < offset ) {
						offset = currentbox.offset;
						start -= offset - currentbox.offset;
					}
				} else {
					start--;
					offset--;
				}
			} else if ( emptyPrevLine && isClosingToken(start, end) ) {
				emptyPrevLine = false; // block closing expands block
			} else if ( !emptyPrevLine && commentStarts(start, end) ) {
				emptyPrevLine = true; // block comment start
			}
			addbox0(start, end, offset);
			emptyPrevLine = commentEnds(start, end);
		} else {
			emptyPrevLine = true;
		}
	}

	private void updateEnds(final int start, final int end, final int n) {
		Box b = currentbox;
		final int len = end - start;
		while (b != null) {
			if ( b.offset <= n ) {
				if ( b.maxLineLen < len ) {
					b.maxLineLen = len;
					b.maxEndOffset = end;
				}
			}
			b = b.parent;
		}
	}

	private boolean startLineComment(final int start, final int end, final int offset, final boolean empty) {
		return offset == 0 && end - start > 1 && text.charAt(start) == '/' && text.charAt(start + 1) == '/';
	}

	private boolean commentStarts(final int start, final int end) {
		return end - start > 1 && text.charAt(start) == '/' && text.charAt(start + 1) == '*';
	}

	private boolean commentEnds(final int start, final int end) {
		for ( int i = start; i < end; i++ ) {
			if ( text.charAt(i) == '*' && text.charAt(i + 1) == '/' ) { return true; }
		}
		return false;
	}

	private boolean isClosingToken(final int start, final int end) {
		int open = 0;
		int close = 0;
		for ( int i = start; i <= end; i++ ) {
			if ( text.charAt(i) == '}' ) {
				if ( open > 0 ) {
					open--;
				} else {
					close++;
				}
			} else if ( text.charAt(i) == '}' ) {
				open++;
			}
		}
		return close > 0;
	}
}
