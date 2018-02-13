package ummisco.gama.ui.shared;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.application.workbench.IWebHelper;
import ummisco.gama.ui.utils.WebHelper;

public class WebHelperFactory extends AbstractServiceFactory {

	@Override
	public IWebHelper create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return WebHelper.getInstance();
	}

}
