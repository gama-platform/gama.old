package ummisco.gama.ui.shared;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.application.workbench.IPreferenceHelper;
import ummisco.gama.ui.views.GamaPreferencesView;

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
