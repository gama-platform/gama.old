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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import msi.gama.gui.swt.SwtGui;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.window.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * A dialog to display one or more errors to the user, as contained in an <code>IStatus</code>
 * object along with the plug-in identifier, name, version and provider. If an error contains
 * additional detailed information then a Details button is automatically supplied, which shows or
 * hides an error details viewer when pressed by the user.
 * 
 * @see org.eclipse.core.runtime.IStatus
 */
public class ExceptionDetailsDialog extends AbstractDetailsDialog {

	/**
	 * The details to be shown ({@link Exception}, {@link IStatus}, or <code>null</code> if no
	 * details).
	 */
	private final Object details;

	/**
	 * Construct a new instance with the specified elements. Note that the window will have no
	 * visual representation (no widgets) until it is told to open. By default, <code>open</code>
	 * blocks for dialogs.
	 * 
	 * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
	 * @param title the title for the dialog or <code>null</code> for none
	 * @param image the image to be displayed
	 * @param message the message to be displayed
	 * @param details an object whose content is to be displayed in the details area, or
	 *            <code>null</code> for none
	 * @param plugin The plugin triggering this deatils dialog and whose information is to be shown
	 *            in the details area or <code>null</code> if no plugin details should be shown.
	 */
	public ExceptionDetailsDialog(final Shell parentShell, final String title, final Image image,
		final String message, final Object details) {
		this(new SameShellProvider(parentShell), title, image, message, details);
	}

	/**
	 * Construct a new instance with the specified elements. Note that the window will have no
	 * visual representation (no widgets) until it is told to open. By default, <code>open</code>
	 * blocks for dialogs.
	 * 
	 * @param parentShell the parent shell provider (not <code>null</code>)
	 * @param title the title for the dialog or <code>null</code> for none
	 * @param image the image to be displayed
	 * @param message the message to be displayed
	 * @param details an object whose content is to be displayed in the details area, or
	 *            <code>null</code> for none
	 * @param plugin The plugin triggering this deatils dialog and whose information is to be shown
	 *            in the details area or <code>null</code> if no plugin details should be shown.
	 */
	private ExceptionDetailsDialog(final IShellProvider parentShell, final String title,
		final Image image, final String message, final Object details) {
		super(parentShell, getTitle(title, details), getImage(image, details), message);

		this.details = details;
	}

	/**
	 * Build content for the area of the dialog made visible when the Details button is clicked.
	 * 
	 * @param parent the details area parent
	 * 
	 * @return the details area
	 */
	@Override
	protected Control createDetailsArea(final Composite parent) {

		// Create the details area.
		final Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		panel.setLayout(layout);

		// Create the details content.
		// createProductInfoArea(panel);
		createDetailsViewer(panel);

		return panel;
	}

	/**
	 * Create the details field based upon the details object. Do nothing if the details object is
	 * not specified.
	 * 
	 * @param parent the details area in which the fields are created
	 * 
	 * @return the details field
	 */
	private Control createDetailsViewer(final Composite parent) {
		if ( details == null ) { return null; }

		final Text text =
			new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create the content.
		final StringWriter writer = new StringWriter(1000);
		if ( details instanceof Throwable ) {
			appendException(new PrintWriter(writer), (Throwable) details);
		} else if ( details instanceof IStatus ) {
			appendCommandStatus(new PrintWriter(writer), (IStatus) details, 0);
		}
		text.setText(writer.toString());

		return text;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Utility methods for building content
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Answer the title based on the provided title and details object.
	 * 
	 * @param title the title
	 * @param details the details
	 * 
	 * @return the title
	 */
	private static String getTitle(final String title, final Object details) {
		if ( title != null ) { return title; }
		if ( details instanceof Throwable ) {
			Throwable e = (Throwable) details;
			while (e instanceof InvocationTargetException) {
				e = ((InvocationTargetException) e).getTargetException();
			}
			final String name = e.getClass().getName();
			return name.substring(name.lastIndexOf('.') + 1);
		}
		return "Exception";
	}

	/**
	 * Answer the image based on the provided image and details object.
	 * 
	 * @param image the image
	 * @param details the details
	 * 
	 * @return the image
	 */
	private static Image getImage(final Image image, final Object details) {
		if ( image != null ) { return image; }
		final Display display = SwtGui.getDisplay();
		if ( details instanceof IStatus ) {
			switch (((IStatus) details).getSeverity()) {
				case IStatus.ERROR:
					return display.getSystemImage(SWT.ICON_ERROR);
				case IStatus.WARNING:
					return display.getSystemImage(SWT.ICON_WARNING);
				case IStatus.INFO:
					return display.getSystemImage(SWT.ICON_INFORMATION);
				case IStatus.OK:
					return null;
			}
		}
		return display.getSystemImage(SWT.ICON_ERROR);
	}

	// TODO UCdetector: Remove unused code:
	// /**
	// * Answer the message based on the provided message and details object.
	// *
	// * @param message
	// * the message
	// * @param details
	// * the details
	// *
	// * @return the message
	// */
	// public static String getMessage(final String message, final Object details) {
	// if (details instanceof Throwable) {
	// Throwable e = (Throwable) details;
	// while (e instanceof InvocationTargetException) {
	// e = ((InvocationTargetException) e).getTargetException();
	// }
	// if (message == null) {
	// return e.toString();
	// }
	// return MessageFormat.format(message, new Object[] { e.toString() });
	// }
	// if (details instanceof IStatus) {
	// final String CommandStatusMessage = ((IStatus) details)
	// .getMessage();
	// if (message == null) {
	// return CommandStatusMessage;
	// }
	// return MessageFormat.format(message,
	// new Object[] { CommandStatusMessage });
	// }
	// if (message != null) {
	// return message;
	// }
	// return "An Exception occurred.";
	// }

	/**
	 * Append exception.
	 * 
	 * @param writer the writer
	 * @param ex the ex
	 */
	private static void appendException(final PrintWriter writer, final Throwable ex) {
		if ( ex instanceof CoreException ) {
			appendCommandStatus(writer, ((CoreException) ex).getStatus(), 0);
			writer.println();
		}
		appendStackTrace(writer, ex);
		if ( ex instanceof InvocationTargetException ) {
			appendException(writer, ((InvocationTargetException) ex).getTargetException());
		}
	}

	/**
	 * Append CommandStatus.
	 * 
	 * @param writer the writer
	 * @param CommandStatus the CommandStatus
	 * @param nesting the nesting
	 */
	private static void appendCommandStatus(final PrintWriter writer, final IStatus CommandStatus,
		final int nesting) {
		for ( int i = 0; i < nesting; i++ ) {
			writer.print("  ");
		}
		writer.println(CommandStatus.getMessage());
		final IStatus[] children = CommandStatus.getChildren();
		for ( int i = 0; i < children.length; i++ ) {
			appendCommandStatus(writer, children[i], nesting + 1);
		}
	}

	/**
	 * Append stack trace.
	 * 
	 * @param writer the writer
	 * @param ex the ex
	 */
	private static void appendStackTrace(final PrintWriter writer, final Throwable ex) {
		ex.printStackTrace(writer);
	}
}
