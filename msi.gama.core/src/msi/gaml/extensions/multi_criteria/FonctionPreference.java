/*******************************************************************************************************
 *
 * msi.gaml.extensions.multi_criteria.FonctionPreference.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.multi_criteria;

public interface FonctionPreference {

	public double valeur(double diff);

	public FonctionPreference copie();
}
