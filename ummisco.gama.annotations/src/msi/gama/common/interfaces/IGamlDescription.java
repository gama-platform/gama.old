/*********************************************************************************************
 *
 * 'IGamlDescription.java, in plugin ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.precompiler.GamlProperties;

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

	public void collectMetaInformation(GamlProperties meta);

}
