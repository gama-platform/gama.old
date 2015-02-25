package idees.gama.features;

import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ExampleUtil {

	/**
	 * Opens an simple input dialog with OK and Cancel buttons.
	 * <p>
	 * 
	 * @param dialogTitle
	 *            the dialog title, or <code>null</code> if none
	 * @param dialogMessage
	 *            the dialog mainTaskName, or <code>null</code> if none
	 * @param initialValue
	 *            the initial input value, or <code>null</code> if none
	 *            (equivalent to the empty string)
	 * @return the string
	 */
	public static String askString(String dialogTitle, String dialogMessage, String initialValue) {
		String ret = null;
		Shell shell = getShell();
		InputDialog inputDialog = new InputDialog(shell, dialogTitle, dialogMessage, initialValue, null);
		int retDialog = inputDialog.open();
		if (retDialog == Window.OK) {
			ret = inputDialog.getValue();
		}
		return ret;
	}

	/**
	 * Opens a dialog to change the color.
	 * 
	 * @param color
	 *            the color to change
	 * @return the changed color
	 */
	public static Color editColor(Color color) {
		if (color != null && color.eContainer() instanceof Diagram) {
			Shell shell = getShell();
			ColorDialog colorDialog = new ColorDialog(shell);
			colorDialog.setText("Choose color");
			colorDialog.setRGB(new RGB(color.getRed(), color.getGreen(), color.getBlue()));

			RGB retRgb = colorDialog.open();
			if (retRgb == null) {
				return null;
			}

			Diagram diagram = (Diagram) color.eContainer();
			Color newColor = Graphiti.getGaService().manageColor(diagram, retRgb.red, retRgb.green, retRgb.blue);
			return newColor;

		}

		return null;
	}

	/**
	 * Returns the currently active Shell.
	 * 
	 * @return The currently active Shell.
	 */
	private static Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}
