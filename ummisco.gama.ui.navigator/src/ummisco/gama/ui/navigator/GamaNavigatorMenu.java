/**
 * Created by drogoul, 8 mars 2015
 * 
 */
package ummisco.gama.ui.navigator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;

import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class GamaNavigatorMenu.
 * 
 * @author drogoul
 * @since 8 mars 2015
 * 
 */
public abstract class GamaNavigatorMenu extends GamaMenu {

	@Override
	public void open(final Control c, final SelectionEvent trigger) {

		if (mainMenu == null) {
			mainMenu = new Menu(WorkbenchHelper.getShell(), SWT.POP_UP);
			fillMenu();
		}

		final Point point = c.toDisplay(new Point(trigger.x, trigger.y));
		mainMenu.setLocation(point.x, point.y);
		mainMenu.setVisible(true);
	}

	/**
	 * 
	 */
	@Override
	protected abstract void fillMenu();

	public static void openWizard(final String id, final IStructuredSelection selection) {
		// First see if this is a "new wizard".
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
		// If not check if it is an "import wizard".
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
		}
		// Or maybe an export wizard
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
		}
		try {
			// Then if we have a wizard, open it.
			if (descriptor != null) {
				final IWorkbenchWizard wizard = descriptor.createWizard();
				wizard.init(PlatformUI.getWorkbench(), selection);
				final WizardDialog wd = new WizardDialog(WorkbenchHelper.getDisplay().getActiveShell(), wizard);
				wd.setTitle(wizard.getWindowTitle());
				wd.open();
			}
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}
}
