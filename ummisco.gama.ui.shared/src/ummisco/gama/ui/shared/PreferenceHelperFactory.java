/*******************************************************************************************************
 *
 * PreferenceHelperFactory.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.shared;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.application.workbench.IPreferenceHelper;
import ummisco.gama.ui.views.GamaPreferencesView;

/**
 * A factory for creating PreferenceHelper objects.
 */
public class PreferenceHelperFactory extends AbstractServiceFactory implements IPreferenceHelper {

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return this;
	}

	@Override
	public void openPreferences() {
		GamaPreferencesView.show();
	}

}
