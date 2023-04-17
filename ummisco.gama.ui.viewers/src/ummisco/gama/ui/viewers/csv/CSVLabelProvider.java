/*******************************************************************************************************
 *
 * CSVLabelProvider.java, in ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.csv;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.viewers.csv.model.CSVRow;
import ummisco.gama.ui.viewers.csv.text.SearchResultStyle;

/**
 *
 * @author fhenri
 *
 */
public class CSVLabelProvider extends StyledCellLabelProvider {

	// implements ITableLabelProvider

	/** The search text. */
	private String searchText;
	
	/** The search color. */
	private final Color searchColor;

	/**
	 *
	 */
	public CSVLabelProvider() {
		searchColor = WorkbenchHelper.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
	}

	/**
	 * @param element
	 * @param columnIndex
	 * @return
	 */
	public String getColumnText(final Object element, final int columnIndex) {
		final CSVRow row = (CSVRow) element;

		if (row.getEntries().size() > columnIndex) { return row.getEntries().get(columnIndex).toString(); }

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
		final CSVRow element = (CSVRow) cell.getElement();
		final int index = cell.getColumnIndex();
		final String columnText = getColumnText(element, index);
		cell.setText(columnText);
		cell.setImage(null);
		if (searchText != null && searchText.length() > 0) {
			final int intRangesCorrectSize[] = SearchResultStyle.getSearchTermOccurrences(searchText, columnText);
			final List<StyleRange> styleRange = new ArrayList<>();
			for (int i = 0; i < intRangesCorrectSize.length / 2; i++) {
				final StyleRange myStyleRange = new StyleRange(0, 0, null, searchColor);
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
