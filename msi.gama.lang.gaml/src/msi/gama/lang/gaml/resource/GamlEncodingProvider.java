/*********************************************************************************************
 * 
 *
 * 'GamlEncodingProvider.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

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
