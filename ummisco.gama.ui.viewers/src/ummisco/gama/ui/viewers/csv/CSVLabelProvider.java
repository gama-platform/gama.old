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
package ummisco.gama.ui.viewers.csv;

import java.util.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import ummisco.gama.ui.viewers.csv.model.CSVRow;
import ummisco.gama.ui.viewers.csv.text.SearchResultStyle;

/**
 *
 * @author fhenri
 *
 */
public class CSVLabelProvider extends StyledCellLabelProvider {

	// implements ITableLabelProvider

	private String searchText;
	private final Color searchColor;

	/**
	 *
	 */
	public CSVLabelProvider() {
		searchColor = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	}

	/**
	 * @param element
	 * @param columnIndex
	 * @return
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * @param element
	 * @param columnIndex
	 * @return
	 */
	public String getColumnText(final Object element, final int columnIndex) {
		CSVRow row = (CSVRow) element;

		if ( row.getEntries().size() > columnIndex ) { return row.getEntries().get(columnIndex).toString(); }

		return "";
	}

	/**
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(final ILabelProviderListener listener) {}

	/**
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return true;
	}

	/**
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(final ILabelProviderListener listener) {}

	/**
	 * @param searchText
	 */
	public void setSearchText(final String searchText) {
		this.searchText = searchText;
	}

	/**
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#dispose()
	 */
	@Override
	public void dispose() {}

	/**
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
	 */
	@Override
	public void update(final ViewerCell cell) {
		CSVRow element = (CSVRow) cell.getElement();
		int index = cell.getColumnIndex();
		String columnText = getColumnText(element, index);
		cell.setText(columnText);
		cell.setImage(null);
		if ( searchText != null && searchText.length() > 0 ) {
			int intRangesCorrectSize[] = SearchResultStyle.getSearchTermOccurrences(searchText, columnText);
			List<StyleRange> styleRange = new ArrayList<StyleRange>();
			for ( int i = 0; i < intRangesCorrectSize.length / 2; i++ ) {
				StyleRange myStyleRange = new StyleRange(0, 0, null, searchColor);
				myStyleRange.start = intRangesCorrectSize[i];
				myStyleRange.length = intRangesCorrectSize[++i];
				styleRange.add(myStyleRange);
			}
			cell.setStyleRanges(styleRange.toArray(new StyleRange[styleRange.size()]));
		} else {
			cell.setStyleRanges(null);
		}

		super.update(cell);
	}
}
