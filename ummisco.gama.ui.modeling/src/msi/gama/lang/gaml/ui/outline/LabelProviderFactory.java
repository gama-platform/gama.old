/*********************************************************************************************
 *
 * 'LabelProviderFactory.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.outline;

import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import utils.DEBUG;

public class LabelProviderFactory extends AbstractServiceFactory {

	private static IResourceServiceProvider serviceProvider;

	public LabelProviderFactory() {}

	@SuppressWarnings ("unchecked")
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
				DEBUG.ERR("Exception in initializing injector: " + e.getMessage());
			}
		}
		return serviceProvider.get(serviceInterface);
	}

}
