/*******************************************************************************************************
 *
 * TextDocument.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class TextDocument {
	
	/** The paragraphs. */
	protected List<StyledTextParagraph> paragraphs = new ArrayList<>();

	/**
	 * Return the pure text content.
	 *
	 * @return the text content
	 */
	public String getText() {
		Iterator i = this.paragraphs.iterator();
		StringBuilder buf = new StringBuilder();

		while (i.hasNext()) {
			StyledTextParagraph para = (StyledTextParagraph) i.next();
			buf.append(para.getText());

			if (para.isNewline()) { buf.append('\n'); }
		}

		return buf.toString();
	}

	/**
	 * Adds the styled paragraph.
	 *
	 * @param para the para
	 */
	public void addStyledParagraph(final StyledTextParagraph para) {
		this.paragraphs.add(para);
	}

	/**
	 * Gets the styled paragraph iterator.
	 *
	 * @return the styled paragraph iterator
	 */
	public Iterator getStyledParagraphIterator() { return this.paragraphs.iterator(); }

	/**
	 * Gets the paragraph count.
	 *
	 * @return the paragraph count
	 */
	public int getParagraphCount() { return this.paragraphs.size(); }

	/**
	 * Gets the style text paragraph.
	 *
	 * @param i the i
	 * @return the style text paragraph
	 */
	public StyledTextParagraph getStyleTextParagraph(final int i) {
		return this.paragraphs.get(i);
	}

	/**
	 * Gets the line count.
	 *
	 * @return the line count
	 */
	public int getLineCount() {
		int count = 1;
		Iterator i = this.paragraphs.iterator();

		while (i.hasNext()) {
			StyledTextParagraph para = (StyledTextParagraph) i.next();

			if (para.isNewline()) { count++; }
		}

		return count;
	}

	/**
	 * Gets the maximum line length.
	 *
	 * @return the maximum line length
	 */
	public int getMaximumLineLength() {
		int count = 0;
		int max = 0;
		Iterator i = paragraphs.iterator();

		while (i.hasNext()) {
			StyledTextParagraph para = (StyledTextParagraph) i.next();

			if (!para.isNewline()) {
				count += para.getLength();
			} else {
				if (count > max) { max = count; }

				count = para.getLength();
			}
		}

		return max;
	}
}
