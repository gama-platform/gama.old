/*********************************************************************************************
 *
 *
 * 'IGamlDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Set;
import msi.gama.common.interfaces.INamed;

/**
 * The class IGamlDescription.
 *
 * @author drogoul
 * @since 27 avr. 2012
 *
 */
public interface IGamlDescription extends INamed {

	public String getTitle();

	public String getDocumentation();

	public String getDefiningPlugin();

	public void collectPlugins(Set<String> plugins);

}
