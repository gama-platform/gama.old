/*******************************************************************************************************
 *
 * IFileMetaDataProvider.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/**
	 * Gets the meta data.
	 *
	 * @param element the element
	 * @param includeOutdated the include outdated
	 * @param immediately the immediately
	 * @return the meta data
	 */
	IGamaFileMetaData getMetaData(Object element, boolean includeOutdated, boolean immediately);

	/**
	 * Store meta data.
	 *
	 * @param file the file
	 * @param data the data
	 * @param immediately the immediately
	 */
	void storeMetaData(final IResource file, final IGamaFileMetaData data, final boolean immediately);

}
