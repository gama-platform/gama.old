/*******************************************************************************************************
 *
 * CritereFctCroyancesBasique.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.multi_criteria;

/**
 * The Class CritereFctCroyancesBasique.
 */
public class CritereFctCroyancesBasique extends CritereFonctionsCroyances {

	/** The s 1. */
	private double s1;
	
	/** The v 2 pour. */
	private double v2Pour;
	
	/** The v 1 pour. */
	private double v1Pour;
	
	/** The v 1 contre. */
	private double v1Contre;
	
	/** The v 2 contre. */
	private double v2Contre;
	
	/** The s 2. */
	private double s2;
	
	/** The cd pour. */
	private final double cdPour;
	
	/** The r pour. */
	private final double rPour;
	
	/** The cd contre. */
	private final double cdContre;
	
	/** The r contre. */
	private final double rContre;

	/**
	 * Instantiates a new critere fct croyances basique.
	 *
	 * @param nom the nom
	 * @param s1 the s 1
	 * @param v2Pour the v 2 pour
	 * @param v1Pour the v 1 pour
	 * @param v1Contre the v 1 contre
	 * @param v2Contre the v 2 contre
	 * @param s2 the s 2
	 */
	public CritereFctCroyancesBasique(final String nom, final double s1, final double v2Pour, final double v1Pour,
			final double v1Contre, final double v2Contre, final double s2) {
		super(nom);
		this.s1 = s1;
		this.v2Pour = v2Pour;
		this.v1Pour = v1Pour;
		this.v1Contre = v1Contre;
		this.v2Contre = v2Contre;
		this.s2 = s2;
		cdPour = s1 == s2 ? 0 : (v1Pour - v2Pour) / (s1 - s2);
		rPour = v1Pour - cdPour * s1;
		cdContre = s1 == s2 ? 0 : (v1Contre - v2Contre) / (s1 - s2);
		rContre = v1Contre - cdContre * s1;
	}

	/**
	 * Gets the s1.
	 *
	 * @return the s1
	 */
	public double getS1() {
		return s1;
	}

	/**
	 * Sets the s1.
	 *
	 * @param s1 the new s1
	 */
	public void setS1(final double s1) {
		this.s1 = s1;
	}

	/**
	 * Gets the v 2 pour.
	 *
	 * @return the v 2 pour
	 */
	public double getV2Pour() {
		return v2Pour;
	}

	/**
	 * Sets the v 2 pour.
	 *
	 * @param v2Pour the new v 2 pour
	 */
	public void setV2Pour(final double v2Pour) {
		this.v2Pour = v2Pour;
	}

	/**
	 * Gets the v 1 pour.
	 *
	 * @return the v 1 pour
	 */
	public double getV1Pour() {
		return v1Pour;
	}

	/**
	 * Sets the v 1 pour.
	 *
	 * @param v1Pour the new v 1 pour
	 */
	public void setV1Pour(final double v1Pour) {
		this.v1Pour = v1Pour;
	}

	/**
	 * Gets the v 1 contre.
	 *
	 * @return the v 1 contre
	 */
	public double getV1Contre() {
		return v1Contre;
	}

	/**
	 * Sets the v 1 contre.
	 *
	 * @param v1Contre the new v 1 contre
	 */
	public void setV1Contre(final double v1Contre) {
		this.v1Contre = v1Contre;
	}

	/**
	 * Gets the v 2 contre.
	 *
	 * @return the v 2 contre
	 */
	public double getV2Contre() {
		return v2Contre;
	}

	/**
	 * Sets the v 2 contre.
	 *
	 * @param v2Contre the new v 2 contre
	 */
	public void setV2Contre(final double v2Contre) {
		this.v2Contre = v2Contre;
	}

	/**
	 * Gets the s2.
	 *
	 * @return the s2
	 */
	public double getS2() {
		return s2;
	}

	/**
	 * Sets the s2.
	 *
	 * @param s2 the new s2
	 */
	public void setS2(final double s2) {
		this.s2 = s2;
	}

	@Override
	public double masseCroyanceContre(final double a) {
		if (s2 - s1 == 0) { return v1Contre; }
		if (a <= s1) { return v1Contre; }
		if (a >= s2) { return v2Contre; }
		return a * cdContre + rContre;
	}

	@Override
	public double masseCroyanceIgnorance(final double a) {
		return Math.max(0, 1 - (masseCroyancePour(a) + masseCroyanceContre(a)));
	}

	@Override
	public double masseCroyancePour(final double a) {
		if (s2 - s1 == 0) { return v1Pour; }
		if (a <= s1) { return v1Pour; }
		if (a >= s2) { return v2Pour; }
		return a * cdPour + rPour;
	}

}
