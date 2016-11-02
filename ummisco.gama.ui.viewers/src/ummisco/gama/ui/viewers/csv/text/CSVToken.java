/*********************************************************************************************
 *
 * 'CSVToken.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.csv.text;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;

import ummisco.gama.ui.resources.IGamaColors;

public class CSVToken extends Token {

	/**
	 * Different CSV tokens
	 * @author japg
	 */
	public enum CSVTokenType {
		ODD_COLUMN(new TextAttribute(IGamaColors.GRAY_LABEL.color(), null, SWT.BOLD)), EVEN_COLUMN(new TextAttribute(
			IGamaColors.ERROR.darker(), null, SWT.BOLD)), SEPARATOR(new TextAttribute(IGamaColors.BLUE.darker(), null,
			SWT.BOLD));

		/** Text decoration */
		private final TextAttribute m_textDecoration;

		/**
		 * Constructor
		 * @param attrs
		 */
		private CSVTokenType(final TextAttribute attrs) {
			m_textDecoration = attrs;
		}

		/**
		 * Get text attributes for this token type
		 * @return
		 */
		TextAttribute getTextAttribute() {
			return m_textDecoration;
		}
	}

	/** Column index */
	private final int m_columnIndex;

	/**
	 * Constructor
	 * @param type
	 * @param column
	 */
	public CSVToken(final CSVTokenType type, final int column) {
		super(type.getTextAttribute());
		m_columnIndex = column;
	}

	/**
	 * Get column index where this token is located
	 * @return
	 */
	public int getColumnIndex() {
		return m_columnIndex;
	}
}