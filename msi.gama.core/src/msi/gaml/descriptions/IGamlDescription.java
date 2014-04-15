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

/**
 * The class IGamlDescription.
 * 
 * @author drogoul
 * @since 27 avr. 2012
 * 
 */
public interface IGamlDescription {

	static String ln = "\n";
	static String tab = "\t";

	public String getTitle();

	public String getDocumentation();

	public String getName();

}
