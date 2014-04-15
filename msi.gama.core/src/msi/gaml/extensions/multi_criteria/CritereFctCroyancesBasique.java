/*********************************************************************************************
 * 
 *
 * 'CritereFctCroyancesBasique.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.multi_criteria;



public class CritereFctCroyancesBasique extends CritereFonctionsCroyances {

	private double s1;
	private double v2Pour;
	private double v1Pour;
	private double v1Contre;
	private double v2Contre;
	private double s2;
	private double cdPour;
	private double rPour;
	private double cdContre;
	private double rContre;
	
	


	public CritereFctCroyancesBasique(String nom, double s1, double v2Pour,
			double v1Pour, double v1Contre, double v2Contre, double s2) {
		super(nom);
		this.s1 = s1;
		this.v2Pour = v2Pour;
		this.v1Pour = v1Pour;
		this.v1Contre = v1Contre;
		this.v2Contre = v2Contre;
		this.s2 = s2;
		cdPour = (s1 == s2) ? 0 : (v1Pour - v2Pour) / (s1 - s2);
		rPour = v1Pour - (cdPour * s1);
		cdContre = (s1 == s2) ? 0 : (v1Contre - v2Contre) / (s1 - s2);
		rContre = v1Contre - (cdContre * s1);
	}

	public double getS1() {
		return s1;
	}

	public void setS1(double s1) {
		this.s1 = s1;
	}

	public double getV2Pour() {
		return v2Pour;
	}

	public void setV2Pour(double v2Pour) {
		this.v2Pour = v2Pour;
	}

	public double getV1Pour() {
		return v1Pour;
	}

	public void setV1Pour(double v1Pour) {
		this.v1Pour = v1Pour;
	}

	public double getV1Contre() {
		return v1Contre;
	}

	public void setV1Contre(double v1Contre) {
		this.v1Contre = v1Contre;
	}

	public double getV2Contre() {
		return v2Contre;
	}

	public void setV2Contre(double v2Contre) {
		this.v2Contre = v2Contre;
	}

	public double getS2() {
		return s2;
	}

	public void setS2(double s2) {
		this.s2 = s2;
	}

	@Override
	public double masseCroyanceContre(double a) {
		if ((s2-s1) == 0)
			return v1Contre;
		if (a <= s1)
			return v1Contre;
		if (a >= s2)
			return v2Contre;
		return a * cdContre + rContre;
	}

	@Override
	public double masseCroyanceIgnorance(double a) {
		return Math.max(0, 1 - (masseCroyancePour(a) + masseCroyanceContre(a)));
	}

	@Override
	public double masseCroyancePour(double a) {
		if ((s2-s1) == 0)
			return v1Pour;
		if (a <= s1)
			return v1Pour;
		if (a >= s2)
			return v2Pour;
		return a * cdPour + rPour;
	}


}
