/*********************************************************************************************
 *
 * 'IFileMetaDataProvider.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.file;

import org.eclipse.core.resources.IResource;

/**
 * Class IFileMetaDataProvider.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public interface IFileMetaDataProvider {

	IGamaFileMetaData getMetaData(Object element, boolean includeOutdated, boolean immediately);

	void storeMetaData(final IResource file, final IGamaFileMetaData data, final boolean immediately);

}
