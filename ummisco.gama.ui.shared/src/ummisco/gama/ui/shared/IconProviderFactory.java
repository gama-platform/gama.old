/*******************************************************************************************************
 *
 * IconProviderFactory.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.shared;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.application.workbench.IIconProvider;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * A factory for creating IconProvider objects.
 */
public class IconProviderFactory extends AbstractServiceFactory {

	/**
	 * Instantiates a new icon provider factory.
	 */
	public IconProviderFactory() {}

	@Override
	public IIconProvider create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return GamaIcons.getInstance();
	}

}
