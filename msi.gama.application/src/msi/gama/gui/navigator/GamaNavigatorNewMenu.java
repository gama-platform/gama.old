/**
 * Created by drogoul, 8 mars 2015
 * 
 */
package msi.gama.gui.navigator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.*;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * Class GamaNavigatorMenus.
 * 
 * @author drogoul
 * @since 8 mars 2015
 * 
 */
public class GamaNavigatorNewMenu extends GamaNavigatorMenu {

	public GamaNavigatorNewMenu(final IStructuredSelection selection) {
		this.selection = selection;
	}

	IStructuredSelection selection;

	private final SelectionListener newModel = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("msi.gama.gui.wizards.NewFileWizard", selection);
		}

	};

	private final SelectionListener newProject = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("msi.gama.gui.wizards.newProjectWizard", selection);
		}

	};
	private final SelectionListener newFolder = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("org.eclipse.ui.wizards.new.folder", selection);
		}

	};

	// private final SelectionListener newOther = new SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// openWizard("org.eclipse.ui.internal.dialogs.NewWizard", selection);
	// }
	//
	// };

	@Override
	protected void fillMenu() {
		action("New model...", newModel, GamaIcons.create("navigator/new.model2").image());
		action("New project...", newProject, GamaIcons.create("navigator/new.project2").image());
		sep();
		action("New folder...", newFolder, GamaIcons.create("navigator/new.folder2").image());
		// sep();
		// action("Other...", newOther, GamaIcons.create("navigator/navigator.new2").image());
	}

}
