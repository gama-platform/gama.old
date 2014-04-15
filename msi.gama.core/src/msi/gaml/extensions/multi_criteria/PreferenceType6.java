/*********************************************************************************************
 * 
 *
 * 'PreferenceType6.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.multi_criteria;

public class PreferenceType6 implements FonctionPreference{

	private double s;
	private double valSquare;
	
	@Override
	public double valeur(double diff) {
		if (diff <= 0)
			return 0;
	
		return 1 - Math.exp(diff * diff / valSquare);
	}
	public PreferenceType6(double s) {
		super();
		this.s = s;
		this.valSquare = -1 * (2 * s *s);
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
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PreferenceType6 other = (PreferenceType6) obj;
		if (Double.doubleToLongBits(s) != Double.doubleToLongBits(other.s))
			return false;
		return true;
	}
	
}