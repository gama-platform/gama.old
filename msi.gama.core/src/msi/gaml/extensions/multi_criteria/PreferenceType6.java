/*******************************************************************************************************
 *
 * PreferenceType6.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.multi_criteria;

/**
 * The Class PreferenceType6.
 */
public class PreferenceType6 implements FonctionPreference {

	/** The s. */
	private final double s;
	
	/** The val square. */
	private final double valSquare;

	@Override
	public double valeur(final double diff) {
		if (diff <= 0) { return 0; }

		return 1 - Math.exp(diff * diff / valSquare);
	}

	/**
	 * Instantiates a new preference type 6.
	 *
	 * @param s the s
	 */
	public PreferenceType6(final double s) {
		super();
		this.s = s;
		this.valSquare = -1 * (2 * s * s);
	}

	@Override
	public FonctionPreference copie() {
		return new PreferenceType6(s);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(s);
		result = prime * result + (int) (temp ^ temp >>> 32);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final PreferenceType6 other = (PreferenceType6) obj;
		return Double.doubleToLongBits(s) == Double.doubleToLongBits(other.s);
	}

}