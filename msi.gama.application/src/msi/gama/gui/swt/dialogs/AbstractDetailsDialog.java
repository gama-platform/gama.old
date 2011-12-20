/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.dialogs;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * An abstract dialog with a details section that can be shown or hidden by the user. Subclasses are
 * responsible for providing the content of the details section.
 */
public abstract class AbstractDetailsDialog extends Dialog {

	/** The title. */
	private final String title;

	/** The message. */
	private final String message;

	/** The image. */
	private final Image image;

	/** The details button. */
	private Button detailsButton;

	/** The details area. */
	private Control detailsArea;

	/** The cached window size. */
	private Point cachedWindowSize;

	// TODO UCdetector: Remove unused code:
	// /**
	// * Construct a new instance with the specified elements. Note that the window will have no
	// * visual representation (no widgets) until it is told to open. By default, <code>open</code>
	// * blocks for dialogs.
	// *
	// * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
	// * @param title the title for the dialog or <code>null</code> for none
	// * @param image the image to be displayed
	// * @param message the message to be displayed
	// */
	// public AbstractDetailsDialog(final Shell parentShell, final String title, final Image image,
	// final String message) {
	// this(new SameShellProvider(parentShell), title, image, message);
	// }

	/**
	 * Construct a new instance with the specified elements. Note that the window will have no
	 * visual representation (no widgets) until it is told to open. By default, <code>open</code>
	 * blocks for dialogs.
	 * 
	 * @param parentShell the parent shell provider (not <code>null</code>)
	 * @param title the title for the dialog or <code>null</code> for none
	 * @param image the image to be displayed
	 * @param message the message to be displayed
	 */
	protected AbstractDetailsDialog(final IShellProvider parentShell, final String title,
		final Image image, final String message) {
		super(parentShell);

		this.title = title;
		this.image = image;
		this.message = message;

		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}

	/**
	 * Configures the given shell in preparation for opening this window in it. In our case, we set
	 * the title if one was provided.
	 * 
	 * @param shell the shell
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if ( title != null ) {
			shell.setText(title);
		}
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above the button bar).
	 * This includes an image, if specified, and a message.
	 * 
	 * @param parent the parent composite to contain the dialog area
	 * 
	 * @return the dialog area control
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if ( image != null ) {
			((GridLayout) composite.getLayout()).numColumns = 2;
			final Label label = new Label(composite, 0);
			image.setBackground(label.getBackground());
			label.setImage(image);
			label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER |
				GridData.VERTICAL_ALIGN_BEGINNING));
		}

		final Label label = new Label(composite, SWT.WRAP);
		if ( message != null ) {
			label.setText(message);
		} else {
			label.setText("Java error in Gama. Please refer to the details below");
		}
		final GridData data =
			new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		return composite;
	}

	/**
	 * Adds OK and Details buttons to this dialog's button bar.
	 * 
	 * @param parent the button bar composite
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		detailsButton =
			createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL,
				false);
	}

	/**
	 * The buttonPressed() method is called when either the OK or Details buttons is pressed. We
	 * override this method to alternately show or hide the details area if the Details button is
	 * pressed.
	 * 
	 * @param id the id
	 */
	@Override
	protected void buttonPressed(final int id) {
		if ( id == IDialogConstants.DETAILS_ID ) {
			toggleDetailsArea();
		} else {
			super.buttonPressed(id);
		}
	}

	/**
	 * Toggles the unfolding of the details area. This is triggered by the user pressing the Details
	 * button.
	 */
	private void toggleDetailsArea() {
		final Point oldWindowSize = getShell().getSize();
		Point newWindowSize = cachedWindowSize;
		cachedWindowSize = oldWindowSize;

		// Show the details area.
		if ( detailsArea == null ) {
			detailsArea = createDetailsArea((Composite) getContents());
			detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
		}

		// Hide the details area.
		else {
			detailsArea.dispose();
			detailsArea = null;
			detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
		}

		/*
		 * Must be sure to call getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT) before calling
		 * getShell().setSize(newWindowSize) since controls have been added or removed.
		 */

		// Compute the new window size.
		final Point oldSize = getContents().getSize();
		final Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if ( newWindowSize == null ) {
			newWindowSize = new Point(oldWindowSize.x, oldWindowSize.y + newSize.y - oldSize.y);
		}

		// Crop new window size to screen.
		final Point windowLoc = getShell().getLocation();
		final Rectangle screenArea = getContents().getDisplay().getClientArea();
		if ( newWindowSize.y > screenArea.height - (windowLoc.y - screenArea.y) ) {
			newWindowSize.y = screenArea.height - (windowLoc.y - screenArea.y);
		}

		getShell().setSize(newWindowSize);
		((Composite) getContents()).layout();
	}

	/**
	 * subclasses must implement createDetailsArea to provide content for the area of the dialog
	 * made visible when the Details button is clicked.
	 * 
	 * @param parent the details area parent
	 * 
	 * @return the details area
	 */
	protected abstract Control createDetailsArea(Composite parent);
}
