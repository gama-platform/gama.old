/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.multi_criteria;

/**
 * @author PTaillandier
 * Critère destiné à la méthode de décision multicritère basée sur les fonctions de croyance (voir thèse, Chap E.)
 */
public abstract class CritereFonctionsCroyances {

	//nom du critère
	private String nom;

	protected CritereFonctionsCroyances(final String nom) {
		this.nom = nom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(final String nom) {
		this.nom = nom;
	}

	@Override
	public String toString() {
		return nom;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (nom == null ? 0 : nom.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		final CritereFonctionsCroyances other = (CritereFonctionsCroyances) obj;
		if ( nom == null ) {
			if ( other.nom != null ) { return false; }
		} else if ( !nom.equals(other.nom) ) { return false; }
		return true;
	}

	/**
	 * Méthode d'initialisation de la masse de croyance pour ce critère de "ce candidat est le meilleur"
	 * @param a : valeur courante du critère
	 * @return la valeur de la masse de croyance
	 */
	public abstract double masseCroyancePour(double a);

	/**
	 * Méthode d'initialisation de la masse de croyance pour ce critère de "ce candidat n'est pas le meilleur"
	 * @param a : valeur courante du critère
	 * @return la valeur de la masse de croyance
	 */
	public abstract double masseCroyanceContre(double a);

	/**
	 * Méthode d'initialisation de la masse de croyance pour ce critère de "je ne sais pas si ce candidate est le meilleur"
	 * @param a : valeur courante du critère
	 * @return la valeur de la masse de croyance
	 */
	public abstract double masseCroyanceIgnorance(double a);

}
