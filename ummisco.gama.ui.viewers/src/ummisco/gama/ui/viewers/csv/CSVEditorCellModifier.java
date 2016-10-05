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

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import ummisco.gama.ui.viewers.csv.model.CSVRow;

/**
 *
 * @author fhenri
 *
 */
public class CSVEditorCellModifier implements ICellModifier {

	/**
	 * Checks whether the given property of the given element can be modified.
	 *
	 * @return true if the property can be modified, and false if it is not
	 *         modifiable
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public boolean canModify(final Object element, final String property) {
		return true;
	}

	/**
	 * Returns the value for the given property of the given element. Returns ""
	 * if the element does not have the given property.
	 *
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public Object getValue(final Object element, final String property) {
		final int elementIndex = Integer.parseInt(property);
		final CSVRow row = (CSVRow) element;

		if (elementIndex < row.getNumberOfElements()) {
			return row.getElementAt(elementIndex);
		}
		return "";
	}

	/**
	 * Modifies the value for the given property of the given element. Has no
	 * effect if the element does not have the given property, or if the
	 * property cannot be modified.
	 *
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public void modify(final Object element, final String property, final Object value) {
		final int elementIndex = Integer.parseInt(property);

		if (element instanceof TableItem) {
			final CSVRow row = (CSVRow) ((TableItem) element).getData();

			if (elementIndex < row.getNumberOfElements()) {
				row.setRowEntry(elementIndex, value.toString());
			} else {
				for (int i = row.getNumberOfElements(); i < elementIndex + 1; i++) {
					row.addElement("");
				}
				row.setRowEntry(elementIndex, value.toString());
			}
		}
	}
}
