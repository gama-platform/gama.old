/*********************************************************************************************
 *
 * 'MatrixEditorDialog.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import ummisco.gama.ui.utils.WorkbenchHelper;

@SuppressWarnings ({ "rawtypes" })
public class MatrixEditorDialog extends Dialog {

	IMatrix data;

	Composite container = null;
	Table table = null;
	final IScope scope;

	final Color gray = WorkbenchHelper.getDisplay().getSystemColor(SWT.COLOR_GRAY);

	protected MatrixEditorDialog(final IScope scope, final Shell parentShell, final IMatrix paramValue) {
		super(parentShell);
		this.scope = scope;
		data = paramValue;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		container = (Composite) super.createDialogArea(parent);
		table = new Table(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		container.setLayout(new FillLayout());
		table.setLinesVisible(true);
		table.setHeaderVisible(false);

		/** Creation of the index column */
		final TableColumn columnIndex = new TableColumn(table, SWT.CENTER);
		columnIndex.setWidth(30);

		/** Creation of table columns */

		int index = 0;
		for (int i = 0; i < data.getCols(scope); i++) {
			final TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setWidth(90);
		}
		/** Creation of table rows */
		for (int i = 0; i < data.getRows(scope); i++) {
			final TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, String.valueOf(index));
			item.setBackground(0, gray);
			index++;
			for (int j = 0; j < data.getCols(scope); j++) {
				item.setText(j + 1, "" + data.get(scope, j, i));
			}
		}

		/** Get the table editable */
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDown, event -> {
			final Rectangle clientArea = table.getClientArea();
			final Point pt = new Point(event.x, event.y);
			int index1 = table.getTopIndex();
			while (index1 < table.getItemCount()) {
				boolean visible = false;
				final TableItem item = table.getItem(index1);
				/**
				 * We don't want to have the first column editable so from i=1
				 */
				for (int i = 1; i < table.getColumnCount(); i++) {
					final Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i;
						final Text text = new Text(table, SWT.NONE);
						final Listener textListener = e -> {
							switch (e.type) {
								case SWT.FocusOut:
									item.setText(column, text.getText());
									text.dispose();
									break;
								case SWT.Traverse:
									switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											item.setText(column, text.getText());
											//$FALL-THROUGH$
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
									}
									break;
							}
						};
						text.addListener(SWT.FocusOut, textListener);
						text.addListener(SWT.Traverse, textListener);
						editor.setEditor(text, item, i);
						text.setText(item.getText(i));
						text.selectAll();
						text.setFocus();
						return;
					}
					if (!visible && rect.intersects(clientArea)) {
						visible = true;
					}
				}
				if (!visible) { return; }
				index1++;
			}
		});

		/** Create and configure the "Add" button */
		final Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Add a row");
		final GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {

			/** Add a row and refresh the view */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final TableItem item;
				int nextIndex;
				final int currentIndex = table.getSelectionIndex();
				final int lastIndex = table.getItemCount();
				if (table.getSelectionIndices().length == 0) {
					/** nothing selected */
					nextIndex = lastIndex;
					item = new TableItem(table, SWT.CENTER);
					item.setText(0, String.valueOf(nextIndex));
				} else {
					nextIndex = currentIndex + 1;
					item = new TableItem(table, SWT.CENTER, nextIndex);
					item.setText(0, String.valueOf(nextIndex));
				}
				// item.setText(1,"New Data");
				item.setBackground(0, gray);
				table.deselect(currentIndex);
				table.select(nextIndex);
				refreshColumnIndex(nextIndex);
			}
		});

		/** Create and configure the "Delete" button */
		final Button del = new Button(parent, SWT.PUSH | SWT.CENTER);
		del.setText("Delete this row");
		del.setEnabled(false);

		del.setLayoutData(gridData);

		table.addListener(SWT.Selection, event -> {
			if (table.getSelectionIndices().length != 0) {
				/** nothing selected */
				del.setEnabled(true);
				add.setText("Add a row after this position");
			} else {
				del.setEnabled(false);
			}
		});
		del.addListener(SWT.Selection, event -> {
			final int nextIndex = table.getSelectionIndex();
			table.remove(table.getSelectionIndices());
			del.setEnabled(false);
			add.setText("Add a row");
			refreshColumnIndex(nextIndex);
		});
		container.addDisposeListener(e -> {
			try {
				data = getNewMatrix();
			} catch (final GamaRuntimeException e1) {
				GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			}
		});
		return container;
	}

	/** A refresh for the index column of the dialog box */
	public void refreshColumnIndex(final int index) {
		final int lastIndex = table.getItemCount();
		for (int i = index; i < lastIndex; i++) {
			final TableItem item = table.getItem(i);
			item.setText(0, String.valueOf(i));
		}
	}

	/** Return the new matrix edited in the dialog box */
	private IMatrix getNewMatrix() throws GamaRuntimeException {
		final int rows = table.getItemCount();
		final int cols = table.getColumnCount() - 1;

		final IMatrix m = createMatrix(rows, cols);

		for (int r = 0; r < rows; r++) {
			for (int c = 1; c < cols + 1; c++) {
				final TableItem item = table.getItem(r);
				m.set(scope, c - 1, r, item.getText(c));
			}
		}

		return m;
	}

	private IMatrix createMatrix(final int rows, final int cols) {
		if (data instanceof GamaIntMatrix) {
			return new GamaIntMatrix(cols, rows);
		} else if (data instanceof GamaFloatMatrix) {
			return new GamaFloatMatrix(cols, rows);
		} else {
			return new GamaObjectMatrix(cols, rows, data.getGamlType().getContentType());
		}

	}

	public IMatrix getMatrix() {
		return data;
	}
}
