package msi.gama.lang.gaml.ui.outline;

import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.xtext.resource.IResourceServiceProvider;

public class LabelProviderFactory extends AbstractServiceFactory {

	private static IResourceServiceProvider serviceProvider;

	public LabelProviderFactory() {
	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		if (serviceProvider == null) {
			// if (dependencyInjector != null)
			// return dependencyInjector.getInstance(c);
			try {
				serviceProvider = IResourceServiceProvider.Registry.INSTANCE
						.getResourceServiceProvider(URI.createPlatformResourceURI("dummy/dummy.gaml", false));
			} catch (final Exception e) {
				System.out.println("Exception in initializing injector: " + e.getMessage());
			}
		}
		return serviceProvider.get(serviceInterface);
	}

}
