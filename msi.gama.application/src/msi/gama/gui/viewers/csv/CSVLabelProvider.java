/* Copyright 2011 csvedit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package msi.gama.gui.viewers.csv;

import java.util.ArrayList;
import java.util.List;

import msi.gama.gui.viewers.csv.model.CSVRow;
import msi.gama.gui.viewers.csv.text.SearchResultStyle;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 *
 * @author fhenri
 *
 */
public class CSVLabelProvider extends StyledCellLabelProvider {
//implements ITableLabelProvider

    private String searchText;
    private Color searchColor;

    /**
     *
     */
    public CSVLabelProvider () {
        searchColor = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
    }

    /**
     * @param element
     * @param columnIndex
     * @return
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /**
     * @param element
     * @param columnIndex
     * @return
     */
    public String getColumnText(Object element, int columnIndex) {
        CSVRow row = (CSVRow) element;

        if(row.getEntries().size() > columnIndex) {
            return row.getEntries().get(columnIndex).toString();
        }

        return "";
    }

    /**
     * @see org.eclipse.jface.viewers.BaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /**
     * @see org.eclipse.jface.viewers.BaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    /**
     * @see org.eclipse.jface.viewers.BaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {
    }

    /**
     * @param searchText
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * @see org.eclipse.jface.viewers.StyledCellLabelProvider#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
     */
    @Override
    public void update(ViewerCell cell) {
        CSVRow element = (CSVRow) cell.getElement();
        int index = cell.getColumnIndex();
        String columnText = getColumnText(element, index);
        cell.setText(columnText);
        cell.setImage(getColumnImage(element, index));
        if (searchText != null && searchText.length() > 0) {
            int intRangesCorrectSize[] =
                SearchResultStyle.getSearchTermOccurrences(searchText, columnText);
            List<StyleRange> styleRange = new ArrayList<StyleRange>();
            for (int i = 0; i < intRangesCorrectSize.length / 2; i++) {
                StyleRange myStyleRange = new StyleRange(0, 0, null, searchColor);
                myStyleRange.start = intRangesCorrectSize[i];
                myStyleRange.length = intRangesCorrectSize[++i];
                styleRange.add(myStyleRange);
            }
            cell.setStyleRanges(styleRange.toArray(new StyleRange[styleRange
                    .size()]));
        } else {
            cell.setStyleRanges(null);
        }

        super.update(cell);
    }
}
