/*********************************************************************************************
 *
 * 'FonctionPreference.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.extensions.multi_criteria;

public interface FonctionPreference {

	public double valeur(double diff);

	public FonctionPreference copie();
}
