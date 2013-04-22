/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class MatrixEditorDialog extends Dialog {

	private IMatrix data;

	private Composite container = null;
	private Table table = null;

	private final Color gray = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);

	protected MatrixEditorDialog(final Shell parentShell, final IMatrix paramValue) {
		super(parentShell);
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
		TableColumn columnIndex = new TableColumn(table, SWT.CENTER);
		columnIndex.setWidth(30);

		int index = 0;
		/** Creation of table columns */
		for ( int i = 0; i < data.getCols(GAMA.getDefaultScope()); i++ ) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setWidth(90);
		}
		/** Creation of table rows */
		for ( int i = 0; i < data.getRows(GAMA.getDefaultScope()); i++ ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, String.valueOf(index));
			item.setBackground(0, gray);
			index++;
			for ( int j = 0; j < data.getCols(GAMA.getDefaultScope()); j++ ) {
				item.setText(j + 1, "" + data.get(GAMA.getDefaultScope(), j, i));
			}
		}
		/** Get the table editable */
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					/** We don't want to have the first column editable so from i=1 */
					for ( int i = 1; i < table.getColumnCount(); i++ ) {
						Rectangle rect = item.getBounds(i);
						if ( rect.contains(pt) ) {
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {

								@Override
								public void handleEvent(final Event e) {
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
						if ( !visible && rect.intersects(clientArea) ) {
							visible = true;
						}
					}
					if ( !visible ) { return; }
					index++;
				}
			}
		});

		/** Create and configure the "Add" button */
		final Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Add a row");
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
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
				if ( table.getSelectionIndices().length == 0 ) {
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

		table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if ( table.getSelectionIndices().length != 0 ) {
					/** nothing selected */
					del.setEnabled(true);
					add.setText("Add a row after this position");
				} else {
					del.setEnabled(false);
				}
			}
		});
		del.addListener(SWT.Selection, new Listener() {

			/** Remove the selection and refresh the view */
			@Override
			public void handleEvent(final Event event) {
				int nextIndex = table.getSelectionIndex();
				table.remove(table.getSelectionIndices());
				del.setEnabled(false);
				add.setText("Add a row");
				refreshColumnIndex(nextIndex);
			}
		});
		container.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				try {
					data = getNewMatrix();
				} catch (GamaRuntimeException e1) {
					GAMA.reportError(e1);
				}
			}
		});
		return container;
	}

	/** A refresh for the index column of the dialog box */
	public void refreshColumnIndex(final int index) {
		final int lastIndex = table.getItemCount();
		for ( int i = index; i < lastIndex; i++ ) {
			final TableItem item = table.getItem(i);
			item.setText(0, String.valueOf(i));
		}
	}

	/** Return the new matrix edited in the dialog box */
	private IMatrix getNewMatrix() throws GamaRuntimeException {
		final int rows = table.getItemCount();
		final int cols = table.getColumnCount() - 1;
		IMatrix m = null;
		if ( data instanceof GamaIntMatrix ) {
			m = new GamaIntMatrix(GAMA.getDefaultScope(), cols, rows);
		} else if ( data instanceof GamaFloatMatrix ) {
			m = new GamaFloatMatrix(GAMA.getDefaultScope(), cols, rows);
		} else if ( data instanceof GamaObjectMatrix ) {
			m = new GamaObjectMatrix(GAMA.getDefaultScope(), cols, rows);
		} else {
			return null;
		}
		for ( int r = 0; r < rows; r++ ) {
			for ( int c = 1; c < cols + 1; c++ ) {
				final TableItem item = table.getItem(r);
				m.set(GAMA.getDefaultScope(), c - 1, r, item.getText(c));
			}
		}
		return m;
	}

	public IMatrix getMatrix() {
		return data;
	}
}
