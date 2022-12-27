/*******************************************************************************************************
 *
 * DisplayLayoutFactory.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.factories;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import ummisco.gama.ui.commands.ArrangeDisplayViews;
import ummisco.gama.ui.interfaces.IDisplayLayoutManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * A factory for creating DisplayLayout objects.
 */
public class DisplayLayoutFactory extends AbstractServiceFactory implements IDisplayLayoutManager {

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return this;
	}

	@Override
	public void applyLayout(final Object layout) {
		WorkbenchHelper.asyncRun(() -> ArrangeDisplayViews.execute(layout));
	}

}
