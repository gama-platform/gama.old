/*********************************************************************************************
 *
 * 'CSVTableSorter.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.csv;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import ummisco.gama.ui.viewers.csv.model.CSVRow;

/**
 *
 * @author fhenri
 *
 */
public class CSVTableSorter extends ViewerComparator {

	private int propertyIndex;
	private static final int DESCENDING = 1;
	private static final int ASCENDING = 0;

	private int direction = DESCENDING;

	private boolean noSort = true;

	/**
	 * Public Constructor
	 */
	public CSVTableSorter() {
		this.propertyIndex = -1;
		direction = DESCENDING;
	}

	/**
	 * Set the column on which the user wants to sort table.
	 *
	 * @param column columnId selected by the user.
	 */
	public void setColumn(final int column, final int dir) {
		if (dir == SWT.NONE) {
			noSort = true;
			return;
		}
		noSort = false;
		if (column != this.propertyIndex) {
			// New column; do an ascending sort
			this.propertyIndex = column;
		}
		this.direction = dir == SWT.UP ? ASCENDING : DESCENDING;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		// this is necessary at opening of csv file so column are not sorted.
		if (propertyIndex == -1 || noSort) {
			return 0;
		}

		final String row1 = ((CSVRow) e1).getElementAt(propertyIndex);
		final String row2 = ((CSVRow) e2).getElementAt(propertyIndex);

		int rc = row1.compareTo(row2);

		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
}
