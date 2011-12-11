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



public class CritereFctCroyancesBasique extends CritereFonctionsCroyances {

	private double s1;
	private double v2Pour;
	private double v1Pour;
	private double v1Contre;
	private double v2Contre;
	private double s2;
	


	public CritereFctCroyancesBasique(String nom, double s1, double v2Pour,
			double v1Pour, double v1Contre, double v2Contre, double s2) {
		super(nom);
		this.s1 = s1;
		this.v2Pour = v2Pour;
		this.v1Pour = v1Pour;
		this.v1Contre = v1Contre;
		this.v2Contre = v2Contre;
		this.s2 = s2;
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
		if (a <= s1)
			return v1Contre;
		if (a <= s2)
			return v2Contre;
		if ((s2-s1) == 0)
			return v2Contre;
		double cd = (v2Contre - v1Contre) / (s2 - s1);
		double b = v2Contre - a * cd;
		return a * cd + b;
	}

	@Override
	public double masseCroyanceIgnorance(double a) {
		return Math.max(0, 1 - (masseCroyancePour(a) + masseCroyanceContre(a)));
	}

	@Override
	public double masseCroyancePour(double a) {
		if (a <= s1)
			return v2Pour;
		if (a <= s2)
			return v1Pour;
		if ((s2-s1) == 0)
			return v1Pour;
		double cd = (v1Pour - v2Pour) / (s2 - s1);
		double b = v1Pour - a * cd;
		return a * cd + b;
	}


}
