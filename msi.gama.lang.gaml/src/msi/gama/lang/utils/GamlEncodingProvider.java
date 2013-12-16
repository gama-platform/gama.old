package msi.gama.lang.utils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.parser.IEncodingProvider;

/**
 * Created by drogoul, 16 déc. 2013
 * 
 */

/**
 * Class GamlEncodingProvider.
 * 
 * @author drogoul
 * @since 16 déc. 2013
 * 
 */
public class GamlEncodingProvider implements IEncodingProvider {

	/**
	 * Method getEncoding()
	 * @see org.eclipse.xtext.parser.IEncodingProvider#getEncoding(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public String getEncoding(final URI uri) {
		return "UTF-8";
	}

}
