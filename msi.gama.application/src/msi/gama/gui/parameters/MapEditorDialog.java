/*********************************************************************************************
 * 
 *
 * 'MapEditorDialog.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.ArrayList;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.types.Types;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * The MapParameterDialog supply a window to help user to modify the map in the visual way.
 */
public class MapEditorDialog extends Dialog {

	// TODO
	// FIXME
	// Revoir compl�tement pour �diter de vraies maps.
	// Erreur laiss�e intentionnellement car classe non fonctionnelle

	private final ArrayList<Object> data = new ArrayList();

	private Text elementText1 = null;
	private Text elementText2 = null;
	private Button newElementButton = null;
	private Button upButton = null;
	private Button downButton = null;
	private Button removeButton = null;
	private List list = null;

	protected MapEditorDialog(final IScope scope, final Shell parentShell, final GamaMap list) {
		super(parentShell);
		GamaList<GamaPair> l = list.listValue(scope, Types.NO_TYPE);
		for ( GamaPair p : l ) {
			data.add(p.first());
			data.add(p.last());
		}
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above the button bar).
	 * 
	 * @param parent the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {

		final Composite container = (Composite) super.createDialogArea(parent);

		final GridLayout gridLayout = new GridLayout(3, false);
		container.setLayout(gridLayout);

		final Label dialogLabel = new Label(container, SWT.NONE);
		dialogLabel.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 3, 1));
		dialogLabel.setText("Modify the map");

		/**
		 * The Texts widgets containing elements to be added. First Element
		 **/
		elementText1 = new Text(container, SWT.BORDER);
		elementText1.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent me) {
				if ( elementText1.getText() == null || elementText1.getText().trim().length() == 0 ) {
					newElementButton.setEnabled(false);
				}
				if ( elementText1.getText().trim().length() > 0 && elementText2.getText().trim().length() > 0 ) {
					newElementButton.setEnabled(true);
				}
			}
		});

		final GridData elementTextGridData1 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		elementTextGridData1.widthHint = 40;
		elementText1.setLayoutData(elementTextGridData1);

		/** Separator */
		final Label separatorLabel = new Label(container, SWT.NONE);
		separatorLabel.setText("::");

		/** Second Element */
		elementText2 = new Text(container, SWT.BORDER);
		elementText2.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent me) {
				if ( elementText2.getText() == null || elementText2.getText().trim().length() == 0 ) {
					newElementButton.setEnabled(false);
				}
				if ( elementText2.getText().trim().length() > 0 && elementText1.getText().trim().length() > 0 ) {
					newElementButton.setEnabled(true);
				}
			}
		});

		final GridData elementTextGridData2 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		elementTextGridData2.widthHint = 40;
		elementText2.setLayoutData(elementTextGridData2);

		/** The button used to new element to the map. */
		newElementButton = new Button(container, SWT.PUSH);
		newElementButton.setText("Add");
		newElementButton.setToolTipText("Add new element");
		newElementButton.setEnabled(false);
		newElementButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				if ( !existKey(elementText1.getText()) ) {
					list.add(elementText1.getText() + " :: " + elementText2.getText());
					data.add(elementText1.getText() + " :: " + elementText2.getText());
					elementText1.setText("");
					elementText2.setText("");
					newElementButton.setEnabled(false);
				} else {
					/** Create the required Status object */
					Status status = new Status(IStatus.ERROR, "GAMA", 0, "This key already exist", null);
					/** Display the error dialog */
					ErrorDialog.openError(Display.getCurrent().getActiveShell(), null, null, status);
				}
			}
		});

		/**
		 * The list widget containing all the elements of the corresponding GAML list.
		 */
		list = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		for ( final Object gamlElement : data ) {
			list.add(gamlElement.toString());
		}

		final GridData listGridData = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 5);
		listGridData.widthHint = 60;
		listGridData.heightHint = 100;
		list.setLayoutData(listGridData);

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				if ( list.getSelectionIndex() != -1 ) {
					if ( list.getSelectionIndex() > 0 ) {
						upButton.setEnabled(true);
					} else {
						upButton.setEnabled(false);
					}

					if ( list.getSelectionIndex() < data.size() - 1 ) {
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

		final GridData buttonBoxGridData = new GridData(GridData.FILL, GridData.CENTER, true, true, 1, 5);
		buttonBoxGridData.heightHint = 100;
		buttonBox.setLayoutData(buttonBoxGridData);

		/** The Up button used to move an element up one position. */
		upButton = new Button(buttonBox, SWT.PUSH);
		upButton.setText("Up");
		upButton.setEnabled(false);

		final GridData upButtonGridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		upButtonGridData.horizontalSpan = 1;
		upButton.setLayoutData(upButtonGridData);

		upButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				int selectionIndex = list.getSelectionIndex();

				final String currentSelectedElement = list.getItem(selectionIndex);
				list.remove(selectionIndex);
				data.remove(selectionIndex);

				if ( selectionIndex > 0 ) {
					selectionIndex--;
				}
				list.add(currentSelectedElement, selectionIndex);
				data.add(selectionIndex, currentSelectedElement);

				list.setSelection(selectionIndex);

				if ( selectionIndex == 0 ) {
					upButton.setEnabled(false);
				}

				if ( selectionIndex < data.size() - 1 ) {
					downButton.setEnabled(true);
				}
			}
		});

		/** The Down button used to move an element down the position. */
		downButton = new Button(buttonBox, SWT.PUSH);
		downButton.setText("Down");
		downButton.setEnabled(false);

		final GridData downButtonGridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		downButtonGridData.horizontalSpan = 1;
		downButton.setLayoutData(downButtonGridData);

		downButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				int selectionIndex = list.getSelectionIndex();

				final String currentSelectedElement = list.getItem(selectionIndex);
				list.remove(selectionIndex);
				data.remove(selectionIndex);

				if ( selectionIndex < data.size() ) {
					selectionIndex++;
				}
				list.add(currentSelectedElement, selectionIndex);
				data.add(selectionIndex, currentSelectedElement);

				list.setSelection(selectionIndex);

				if ( selectionIndex >= data.size() - 1 ) {
					downButton.setEnabled(false);
				}

				if ( selectionIndex > 0 ) {
					upButton.setEnabled(true);
				}
			}
		});

		/** The Remove button used to remove an element. */
		removeButton = new Button(buttonBox, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);

		final GridData removeButtonGridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		removeButtonGridData.horizontalSpan = 1;
		removeButton.setLayoutData(removeButtonGridData);

		removeButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				int selectionIndex = list.getSelectionIndex();

				list.remove(selectionIndex);
				data.remove(selectionIndex);

				if ( data.size() > 0 ) {
					if ( selectionIndex >= data.size() ) {
						selectionIndex--;
					}

					if ( selectionIndex >= 0 && selectionIndex < data.size() ) {
						list.setSelection(selectionIndex);
						removeButton.setEnabled(true);
					}

					if ( selectionIndex >= data.size() - 1 ) {
						downButton.setEnabled(false);
					} else {
						downButton.setEnabled(true);
					}

					if ( selectionIndex > 0 ) {
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

	public boolean existKey(final String elem) {
		for ( final Object element : data ) {
			String tmp = ((String) element).substring(0, ((String) element).indexOf(" ::"));
			if ( tmp.equals(elem) ) { return true; }
		}
		return false;
	}

	public GamaMap getMap() {
		boolean isFirstElement = true;
		Object first = null;
		GamaMap map = new GamaMap();

		for ( final Object element : data ) {
			if ( isFirstElement ) {
				isFirstElement = false;
				first = element;
			} else {
				isFirstElement = true;
				map.put(first, element);
			}
		}
		return map;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
