/**
 * Created by drogoul, 8 mars 2015
 * 
 */
package ummisco.gama.ui.navigator;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.commands.GamaMenu;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * Class GamaNavigatorMenu.
 * 
 * @author drogoul
 * @since 8 mars 2015
 * 
 */
public abstract class GamaNavigatorMenu extends GamaMenu {

	public void open(final Control c, final SelectionEvent trigger) {

		if ( mainMenu == null ) {
			mainMenu = new Menu(SwtGui.getWindow().getShell(), SWT.POP_UP);
			fillMenu();
		}

		Point point = c.toDisplay(new Point(trigger.x, trigger.y));
		mainMenu.setLocation(point.x, point.y);
		mainMenu.setVisible(true);
	}

	/**
	 * 
	 */
	protected abstract void fillMenu();

	public static void openWizard(final String id, final IStructuredSelection selection) {
		// First see if this is a "new wizard".
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
		// If not check if it is an "import wizard".
		if ( descriptor == null ) {
			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
		}
		// Or maybe an export wizard
		if ( descriptor == null ) {
			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
		}
		try {
			// Then if we have a wizard, open it.
			if ( descriptor != null ) {
				IWorkbenchWizard wizard = descriptor.createWizard();
				wizard.init(PlatformUI.getWorkbench(), selection);
				WizardDialog wd = new WizardDialog(SwtGui.getDisplay().getActiveShell(), wizard);
				wd.setTitle(wizard.getWindowTitle());
				wd.open();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
