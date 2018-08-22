/*********************************************************************************************
 *
 * 'DisplayLayoutFactory.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.factories;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.application.workbench.PerspectiveHelper.SimulationPerspectiveDescriptor;
import ummisco.gama.ui.commands.ArrangeDisplayViews;
import ummisco.gama.ui.interfaces.IDisplayLayoutManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class DisplayLayoutFactory extends AbstractServiceFactory implements IDisplayLayoutManager {

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return this;
	}

	@Override
	public void applyLayout(final Object layout, final boolean keepTabs, final boolean keepToolbars,
			final boolean showEditors) {
		WorkbenchHelper.run(() -> {
			WorkbenchHelper.getPage().setEditorAreaVisible(showEditors);
		});
		WorkbenchHelper.runInUI("Arranging views", 0, (m) -> {
			final SimulationPerspectiveDescriptor sd = PerspectiveHelper.getActiveSimulationPerspective();
			if (sd != null) {
				sd.keepTabs(keepTabs);
				sd.keepToolbars(keepToolbars);
			}
			ArrangeDisplayViews.execute(layout);
		});

	}

	@Override
	public void hideScreen() {
		WorkbenchHelper.asyncRun(() -> ArrangeDisplayViews.hideScreen());
	}

	@Override
	public void showScreen() {
		WorkbenchHelper.asyncRun(() -> ArrangeDisplayViews.showScreen());
	}

}
