/*
 * Copyright 2011 csvedit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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