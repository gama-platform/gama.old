/*******************************************************************************************************
 *
 * IconProviderFactory.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.shared;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.application.workbench.IIconProvider;
import ummisco.gama.ui.resources.GamaIcon;

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
		return new IIconProvider() {
			@Override
			public ImageDescriptor desc(final String name) {
				final GamaIcon icon = GamaIcon.named(name);
				return icon.descriptor();
			}

			@Override
			public ImageDescriptor disabled(final String name) {
				final GamaIcon icon = GamaIcon.named(name);
				return icon.disabledDescriptor();
			}
		};
	}

}
