/*********************************************************************************************
 *
 * 'CSVTableFilter.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.csv.text;

import java.util.regex.*;

import org.eclipse.jface.viewers.*;

import ummisco.gama.ui.viewers.csv.model.CSVRow;

/**
 * Filter the elements given a pattern.
 * 
 * @author fhenri
 * 
 */
public class CSVTableFilter extends ViewerFilter {

	private String searchString;
	private Pattern searchPattern;

	/**
	 * Build a pattern. we use pattern so we can make non case sensitive search
	 * 
	 * @param s string to search
	 */
	public void setSearchText(final String s) {
		// Search must be a substring of the existing value
		this.searchString = ".*" + s + ".*";
		searchPattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);

	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {

		if ( searchString == null || searchString.length() == 0 ) { return true; }

		// loop on all column of the current row to find matches
		CSVRow row = (CSVRow) element;
		for ( String s : row.getEntries() ) {
			Matcher m = searchPattern.matcher(s);
			if ( m.matches() ) { return true; }
		}
		return false;
	}
}