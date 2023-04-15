/*******************************************************************************************************
 *
 * MetaDataServiceFactory.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.util.file.IFileMetaDataProvider;
import ummisco.gama.ui.metadata.FileMetaDataProvider;

/**
 * A factory for creating MetaDataService objects.
 */
public class MetaDataServiceFactory extends AbstractServiceFactory {

	/**
	 * Instantiates a new meta data service factory.
	 */
	public MetaDataServiceFactory() {
	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		if (IFileMetaDataProvider.class.equals(serviceInterface))
			return FileMetaDataProvider.getInstance();
		return null;
	}

}
