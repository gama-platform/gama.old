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
package msi.gama.gui.viewers.csv.text;

import java.util.regex.*;
import msi.gama.gui.viewers.csv.model.CSVRow;
import org.eclipse.jface.viewers.*;

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