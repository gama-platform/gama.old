/*********************************************************************************************
 *
 * 'SearchResultStyle.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.csv.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;

import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 *
 * @author fhenri
 *
 */
public class SearchResultStyle {

	/**
	 * @param searchTerm
	 * @param content
	 * @return
	 */
	public static int[] getSearchTermOccurrences(final String searchTerm, final String content) {
		List<StyleRange> styleRange;
		List<Integer> ranges;
		final Display disp = WorkbenchHelper.getDisplay();
		final StyleRange myStyleRange = new StyleRange(0, 0, null, disp.getSystemColor(SWT.COLOR_YELLOW));

		// reset the StyleRange-Array for each new field
		styleRange = new ArrayList<StyleRange>();
		ranges = new ArrayList<Integer>(); // reset the ranges-array
		if (searchTerm.equals("")) { return new int[] {}; }

		// determine all occurrences of the searchText and write the beginning
		// and length of each occurrence into an array
		for (int i = 0; i < content.length(); i++) {
			if (i + searchTerm.length() <= content.length()
					&& content.substring(i, i + searchTerm.length()).equalsIgnoreCase(searchTerm)) {
				// ranges format: n->start of the range, n+1->length of the
				// range
				ranges.add(i);
				ranges.add(searchTerm.length());
			}
		}
		// convert the list into an int[] and make sure that overlapping
		// search term occurrences are are merged
		final int[] intRanges = new int[ranges.size()];
		int arrayIndexCounter = 0;
		for (int listIndexCounter = 0; listIndexCounter < ranges.size(); listIndexCounter++) {
			if (listIndexCounter % 2 == 0) {
				if (searchTerm.length() > 1 && listIndexCounter != 0 && ranges.get(listIndexCounter - 2)
						+ ranges.get(listIndexCounter - 1) >= ranges.get(listIndexCounter)) {
					intRanges[arrayIndexCounter - 1] = 0 - ranges.get(listIndexCounter - 2)
							+ ranges.get(listIndexCounter) + ranges.get(++listIndexCounter);
				} else {
					intRanges[arrayIndexCounter++] = ranges.get(listIndexCounter);
				}
			} else {
				intRanges[arrayIndexCounter++] = ranges.get(listIndexCounter);
				styleRange.add(myStyleRange);
			}
		}
		// if there have been any overlappings we need to reduce the size of
		// the array to avoid conflicts in the setStyleRanges method
		final int[] intRangesCorrectSize = new int[arrayIndexCounter];
		System.arraycopy(intRanges, 0, intRangesCorrectSize, 0, arrayIndexCounter);

		return intRangesCorrectSize;
	}
}
