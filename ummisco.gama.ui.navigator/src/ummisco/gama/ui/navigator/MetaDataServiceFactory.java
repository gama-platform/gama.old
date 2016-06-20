package ummisco.gama.ui.navigator;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.util.file.IFileMetaDataProvider;
import ummisco.gama.ui.metadata.FileMetaDataProvider;

public class MetaDataServiceFactory extends AbstractServiceFactory {

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
