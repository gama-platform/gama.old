/*******************************************************************************************************
 *
 * BoxBuilderImpl.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.LinkedList;
import java.util.List;


/**
 * The Class BoxBuilderImpl.
 */
public class BoxBuilderImpl extends AbstractBoxBuilder {

	/** The boxes. */
	protected List<Box> boxes;
	
	/** The currentbox. */
	protected Box currentbox;
	
	/** The empty prev line. */
	protected boolean emptyPrevLine;
	
	/** The line has start tab. */
	protected boolean lineHasStartTab;
	
	public List<Box> build() {
		boxes = new LinkedList<Box>();
		int len = text.length() - 1;
		currentbox = newbox(0, len, -1, null);
		boxes.clear(); // skip root box

		emptyPrevLine = false;
		int start = 0;
		int offset = 0;
		boolean startline = true;
		lineHasStartTab = false;
		boolean empty = true;
		
		checkCarret();
		
		for (int i = 0; i <= len; i++) {
			char c = text.charAt(i);
			boolean isWhitespace = Character.isWhitespace(c) && i != caretOffset;
			empty = empty && isWhitespace;
			if (c == '\n' || i == len) {
				if (startline) start = i;
				addLine(start, i, offset, empty);
				startline = true;
				offset = 0;
				start = i;
				lineHasStartTab = false;
				empty = true;
			} else {
				if (startline) {
					if (isWhitespace) {
						if (c == '\t'){
							offset += tabSize;
							lineHasStartTab = true;
						}else
							offset++;
					} else {
						start = i;
						startline = false;
					}
				}
			}
		}
		return boxes;
	}

	/**
	 * Check carret.
	 */
	private void checkCarret() {
		if (caretOffset < 0) return;
		
		if (caretOffset > 0 && text.charAt(caretOffset - 1) == '\n'){
			caretOffset = -1;
			return;
		}
			
		int end = text.length();
		for (int i= caretOffset; i<end;i++ ){
			char c = text.charAt(i);
			if (!Character.isWhitespace(c)){
				caretOffset = -1;
				return;
			}
			if (c == '\n')
				return;
		}
	}

	/**
	 * Adds the line.
	 *
	 * @param start the start
	 * @param end the end
	 * @param offset the offset
	 * @param empty the empty
	 */
	protected void addLine(int start, int end, int offset, boolean empty) {
		if (!empty) {
			addbox0(start, end, offset);
		}
		emptyPrevLine = empty;
	}

	/**
	 * Addbox 0.
	 *
	 * @param start the start
	 * @param end the end
	 * @param offset the offset
	 */
	protected void addbox0(int start, int end, int offset) {

		if (offset == currentbox.offset) {
			if ((emptyPrevLine && currentbox.parent != null)) {
				currentbox = newbox(start, end, offset, currentbox.parent);
				updateParentEnds(currentbox);
			} else if (end > currentbox.end) {
				currentbox.end = end;
				if (currentbox.tabsStart < 0 && lineHasStartTab)
					currentbox.tabsStart = start;
				updateMaxEndOffset(start, currentbox);
				updateParentEnds(currentbox);
			}
		} else if (offset > currentbox.offset) {
			currentbox = newbox(start, end, offset, currentbox);
			updateParentEnds(currentbox);
		} else if (currentbox.parent != null) {
			currentbox = currentbox.parent;
			addbox0(start, end, offset);
		}

	}


	/**
	 * Update max end offset.
	 *
	 * @param start the start
	 * @param b the b
	 */
	protected void updateMaxEndOffset(int start, Box b) {
		int n = b.end - start + b.offset;
		if (n >= b.maxLineLen) {
			b.maxLineLen = n;
			b.maxEndOffset = b.end;
		}
	}

	/**
	 * Update parent ends.
	 *
	 * @param box the box
	 */
	protected void updateParentEnds(Box box) {
		Box b = box.parent;
		while (b != null && b.end < box.end) {
			b.end = box.end;
			if (b.maxLineLen <= box.maxLineLen) {
				b.maxEndOffset = box.maxEndOffset;
				b.maxLineLen = box.maxLineLen;
			}
			b = b.parent;
		}

	}

	/**
	 * Newbox.
	 *
	 * @param start the start
	 * @param end the end
	 * @param offset the offset
	 * @param parent the parent
	 * @return the box
	 */
	protected Box newbox(int start, int end, int offset, Box parent) {
		Box box = new Box();
		box.end = end;
		box.start = start;
		box.offset = offset;
		box.parent = parent;
		box.maxLineLen = end - start + offset;
		box.maxEndOffset = end;
		box.level = parent!=null ? parent.level + 1 : -1;
		if (lineHasStartTab)
			box.tabsStart = start;
		if (parent!=null)
			parent.hasChildren = true;
		boxes.add(box);
		return box;
	}

}
