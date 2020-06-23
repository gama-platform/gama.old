/*******************************************************************************************************
 *
 * msi.gama.util.file.IFileMetaDataProvider.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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
