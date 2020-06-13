/*********************************************************************************************
 *
 * 'ListEditorDialog.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import msi.gama.common.util.StringUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.GAML;

/**
 * The ListParameterDialog supply a window to help user to modify the list in the visual way.
 */
@SuppressWarnings ({ "rawtypes" })
public class ListEditorDialog extends Dialog {

	final ArrayList<String> data = new ArrayList<>();

	Button newElementButton = null;
	Button upButton = null;
	Button downButton = null;
	Button removeButton = null;
	List list = null;
	String listname = null;

	protected ListEditorDialog(final Shell parentShell, final IList list, final String listname) {
		super(parentShell);
		this.listname = listname;
		for (final Object o : list) {
			data.add(StringUtils.toGaml(o, false));
		}
		// final String tmpGamlList = list.substring(1, list.length() - 1);
		// final StringTokenizer elementTokenizer = new
		// StringTokenizer(tmpGamlList, ",");
		// while (elementTokenizer.hasMoreTokens()) {
		// final String tmp = elementTokenizer.nextToken().trim();
		// data.add(tmp);
		// }
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above the button bar).
	 *
	 * @param parent
	 *            the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout(3, false);
		container.setLayout(gridLayout);

		final Label dialogLabel = new Label(container, SWT.NONE);
		dialogLabel.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 3, 1));
		dialogLabel.setText("Modify the list \'" + listname + "\'");

		/** The Text widget containing the new element to be added. */
		final Text newElementText = new Text(container, SWT.BORDER);
		newElementText.addModifyListener(me -> {
			if (newElementText.getText() == null || newElementText.getText().trim().length() == 0) {
				newElementButton.setEnabled(false);
			}
			if (newElementText.getText().trim().length() > 0) {
				newElementButton.setEnabled(true);
			}
		});

		final GridData newElementTextGridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		newElementTextGridData.widthHint = 40;
		newElementText.setLayoutData(newElementTextGridData);

		/** The button used to add one new element. */
		newElementButton = new Button(container, SWT.PUSH);
		newElementButton.setText("Add");
		newElementButton.setToolTipText("Add new element");
		newElementButton.setEnabled(false);
		newElementButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				list.add(newElementText.getText());
				data.add(newElementText.getText());
				newElementText.setText("");
				newElementButton.setEnabled(false);
			}
		});

		/**
		 * The list widget containing all the elements of the corresponding GAML list.
		 */
		list = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		for (final String gamlElement : data) {
			list.add(gamlElement);
		}

		final GridData listGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 5);
		listGridData.widthHint = 60;
		listGridData.heightHint = 100;
		list.setLayoutData(listGridData);

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				if (list.getSelectionIndex() != -1) {
					if (list.getSelectionIndex() > 0) {
						upButton.setEnabled(true);
					} else {
						upButton.setEnabled(false);
					}

					if (list.getSelectionIndex() < data.size() - 1) {
						downButton.setEnabled(true);
					} else {
						downButton.setEnabled(false);
					}

					removeButton.setEnabled(true);
				} else {
					upButton.setEnabled(false);
					downButton.setEnabled(false);
					removeButton.setEnabled(false);
				}
			}
		});

		final Composite buttonBox = new Composite(container, SWT.NONE);

		final GridLayout buttonBoxgridLayout = new GridLayout();
		buttonBoxgridLayout.numColumns = 1;
		buttonBox.setLayout(buttonBoxgridLayout);

		final GridData buttonBoxGridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 5);
		buttonBoxGridData.heightHint = 100;
		buttonBox.setLayoutData(buttonBoxGridData);

		/** The Up button used to move an element up one position. */
		upButton = new Button(buttonBox, SWT.PUSH);
		upButton.setText("Up");
		upButton.setEnabled(false);

		final GridData upButtonGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		upButtonGridData.horizontalSpan = 1;
		upButton.setLayoutData(upButtonGridData);

		upButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				int selectionIndex = list.getSelectionIndex();

				final String currentSelectedElement = list.getItem(selectionIndex);
				list.remove(selectionIndex);
				data.remove(selectionIndex);

				if (selectionIndex > 0) {
					selectionIndex--;
				}
				list.add(currentSelectedElement, selectionIndex);
				data.add(selectionIndex, currentSelectedElement);

				list.setSelection(selectionIndex);

				if (selectionIndex == 0) {
					upButton.setEnabled(false);
				}

				if (selectionIndex < data.size() - 1) {
					downButton.setEnabled(true);
				}
			}
		});

		/** The Down button used to move an element down on position. */
		downButton = new Button(buttonBox, SWT.PUSH);
		downButton.setText("Down");
		downButton.setEnabled(false);

		final GridData downButtonGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		downButtonGridData.horizontalSpan = 1;
		downButton.setLayoutData(downButtonGridData);

		downButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				int selectionIndex = list.getSelectionIndex();

				final String currentSelectedElement = list.getItem(selectionIndex);
				list.remove(selectionIndex);
				data.remove(selectionIndex);

				if (selectionIndex < data.size()) {
					selectionIndex++;
				}

				list.add(currentSelectedElement, selectionIndex);
				data.add(selectionIndex, currentSelectedElement);

				list.setSelection(selectionIndex);

				if (selectionIndex >= data.size() - 1) {
					downButton.setEnabled(false);
				}

				if (selectionIndex > 0) {
					upButton.setEnabled(true);
				}
			}
		});

		/** The Remove button used to remove an element. */
		removeButton = new Button(buttonBox, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);

		final GridData removeButtonGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		removeButtonGridData.horizontalSpan = 1;
		removeButton.setLayoutData(removeButtonGridData);

		removeButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				int selectionIndex = list.getSelectionIndex();

				list.remove(selectionIndex);
				data.remove(selectionIndex);

				if (data.size() > 0) {
					if (selectionIndex >= data.size()) {
						selectionIndex--;
					}

					if (selectionIndex >= 0 && selectionIndex < data.size()) {
						list.setSelection(selectionIndex);
						removeButton.setEnabled(true);
					}

					if (selectionIndex >= data.size() - 1) {
						downButton.setEnabled(false);
					} else {
						downButton.setEnabled(true);
					}

					if (selectionIndex > 0) {
						upButton.setEnabled(true);
					} else {
						upButton.setEnabled(false);
					}
				} else {
					upButton.setEnabled(false);
					downButton.setEnabled(false);
					removeButton.setEnabled(false);
				}
			}
		});
		return container;
	}

	public IList getList(final ListEditor editor) {
		// GamaList result = new GamaList();

		boolean isFirstElement = true;
		final StringBuilder tmp = new StringBuilder("[");

		for (final String element : data) {
			if (isFirstElement) {
				isFirstElement = false;
				tmp.append(element);
			} else {
				tmp.append("," + element);
			}
		}
		tmp.append("]");
		try {
			return (IList) GAML.evaluateExpression(tmp.toString(), editor.getAgent());
		} catch (final GamaRuntimeException e) {
			return GamaListFactory.create();
		}
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
