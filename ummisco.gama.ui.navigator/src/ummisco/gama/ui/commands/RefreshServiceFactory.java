/*******************************************************************************************************
 *
 * RefreshServiceFactory.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import ummisco.gama.ui.interfaces.IRefreshHandler;

/**
 * A factory for creating RefreshService objects.
 */
public class RefreshServiceFactory extends AbstractServiceFactory {

	/**
	 * Instantiates a new refresh service factory.
	 */
	public RefreshServiceFactory() {}

	@Override
	public IRefreshHandler create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return new RefreshHandler();
	}

}
