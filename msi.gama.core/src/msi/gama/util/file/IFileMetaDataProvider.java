/**
 * Created by drogoul, 11 févr. 2015
 *
 */
package msi.gama.util.file;

import java.io.File;

import org.eclipse.core.resources.IFile;
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

	/**
	 * Returns the suffix to use in the navigator for decorating the element.
	 *
	 * @param element
	 * @return a string describing the element or an empty string
	 */
	String getDecoratorSuffix(Object element);

	public void storeMetadata(final File f, final IGamaFileMetaData data);

	public void storeMetadata(final IResource file, final IGamaFileMetaData data, final boolean immediately);

	boolean isGAML(IFile resource);

}
