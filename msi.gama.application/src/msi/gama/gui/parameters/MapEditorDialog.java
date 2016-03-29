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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import msi.gaml.types.Types;

/**
 * The MapParameterDialog supply a window to help user to modify the map in the visual way.
 */
public class MapEditorDialog extends Dialog {

	// TODO
	// FIXME
	// Revoir compl�tement pour �diter de vraies maps.
	// Erreur laiss�e intentionnellement car classe non fonctionnelle

	// private final ArrayList<GamaPair> data = new ArrayList();

	GamaMap data;

	private Text elementText1 = null;
	private Text elementText2 = null;
	private Button newElementButton = null;
	private Button removeButton = null;
	private List list = null;
	private final IScope scope;

	protected MapEditorDialog(final IScope scope, final Shell parentShell, final GamaMap list) {
		super(parentShell);
		this.scope = scope;
		data = list;
		// IList<GamaPair> l = list.getPairs();
		// for ( GamaPair p : l ) {
		// data.add(p);
		// }
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
					list.add(elementText1.getText() + "::" + elementText2.getText());
					data.addValue(scope,
						new GamaPair(elementText1.getText(), elementText2.getText(), Types.STRING, Types.STRING));
					elementText1.setText("");
					elementText2.setText("");
					newElementButton.setEnabled(false);
				} else {
					/** Create the required Status object */
					final Status status = new Status(IStatus.ERROR, "GAMA", 0, "This key already exist", null);
					/** Display the error dialog */
					ErrorDialog.openError(Display.getCurrent().getActiveShell(), null, null, status);
				}
			}
		});

		/**
		 * The list widget containing all the elements of the corresponding GAML list.
		 */
		list = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		for ( final Object gamlElement : data.getPairs() ) {
			if ( gamlElement instanceof GamaPair ) {
				list.add(((GamaPair) gamlElement).serialize(false));
			}
		}

		final GridData listGridData = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 5);
		listGridData.widthHint = 60;
		listGridData.heightHint = 100;
		list.setLayoutData(listGridData);

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent me) {
				if ( list.getSelectionIndex() != -1 ) {
					removeButton.setEnabled(true);
				} else {
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

				} else {
					removeButton.setEnabled(false);
				}
			}
		});
		return container;
	}

	public boolean existKey(final String elem) {
		for ( final Object element : data.keySet() ) {
			final String tmp = element.toString();
			if ( tmp.equals(elem) ) { return true; }
		}
		return false;
	}

	public GamaMap getMap() {
		final boolean isFirstElement = true;
		final Object first = null;
		return data;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
