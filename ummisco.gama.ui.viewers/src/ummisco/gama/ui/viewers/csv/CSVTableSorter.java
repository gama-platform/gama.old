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
package ummisco.gama.ui.viewers.csv;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;

import ummisco.gama.ui.viewers.csv.model.CSVRow;

/**
 *
 * @author fhenri
 *
 */
public class CSVTableSorter extends ViewerSorter {

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
    public void setColumn(int column, int dir) {
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
    public int compare(Viewer viewer, Object e1, Object e2) {
        // this is necessary at opening of csv file so column are not sorted.
        if (propertyIndex == -1 || noSort) return 0;

        String row1 = ((CSVRow) e1).getElementAt(propertyIndex);
        String row2 = ((CSVRow) e2).getElementAt(propertyIndex);

        int rc = row1.compareTo(row2);

        // If descending order, flip the direction
        if (direction == DESCENDING) {
            rc = -rc;
        }
        return rc;
    }
}
