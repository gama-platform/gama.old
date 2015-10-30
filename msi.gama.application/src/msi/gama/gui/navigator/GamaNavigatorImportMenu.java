/**
 * Created by drogoul, 8 mars 2015
 * 
 */
package msi.gama.gui.navigator;

import msi.gama.gui.swt.GamaIcons;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.*;

/**
 * Class GamaNavigatorMenus.
 * 
 * @author drogoul
 * @since 8 mars 2015
 * 
 */
public class GamaNavigatorImportMenu extends GamaNavigatorMenu {

	public GamaNavigatorImportMenu(final IStructuredSelection selection) {
		this.selection = selection;
	}

	IStructuredSelection selection;

	private final SelectionListener fromDisk = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("org.eclipse.ui.wizards.import.FileSystem", selection);
		}

	};

	private final SelectionListener fromArchive = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("org.eclipse.ui.wizards.import.ZipFile", selection);
		}

	};
	private final SelectionListener project = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("org.eclipse.ui.wizards.import.ExternalProject", selection);
		}

	};

	@Override
	protected void fillMenu() {
		action("Import project...", project, GamaIcons.create("navigator/navigator.import.project2").image());
		sep();
		action("From disk...", fromDisk, GamaIcons.create("navigator/navigator.import.disk2").image());
		action("From archive...", fromArchive, GamaIcons.create("navigator/navigator.import.archive2").image());

		// sep();
		// action("Other...", newOther, GamaIcons.create("navigator/navigator.new2").image());
	}

}
