/**
 * Created by drogoul, 11 févr. 2015
 * 
 */
package msi.gama.util.file;


/**
 * Class IFileMetaDataProvider.
 * 
 * @author drogoul
 * @since 11 févr. 2015
 * 
 */
public interface IFileMetaDataProvider {

	IGamaFileMetaData getMetaData(Object element);

	/**
	 * Returns the suffix to use in the navigator for decorating the element.
	 * 
	 * @param element
	 * @return a string describing the element or an empty string
	 */
	String getDecoratorSuffix(Object element);

}
