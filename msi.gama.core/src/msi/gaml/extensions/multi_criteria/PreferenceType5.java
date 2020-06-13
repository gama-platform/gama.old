/*******************************************************************************************************
 *
 * msi.gaml.extensions.multi_criteria.PreferenceType5.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.multi_criteria;

public class PreferenceType5 implements FonctionPreference{

	private double q;
	private double p;
	@Override
	public double valeur(double diff) {
		if (diff <= q)
			return 0;
		if (diff <= p)
			return (diff - q) / (p - q);
		return 1;
	}
	public PreferenceType5(double q, double p) {
		super();
		this.q = q;
		this.p = p;
	}
	
	@Override
	public FonctionPreference copie() {
		return new PreferenceType5(q,p);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(p);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(q);
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
		PreferenceType5 other = (PreferenceType5) obj;
		if (Double.doubleToLongBits(p) != Double.doubleToLongBits(other.p))
			return false;
		if (Double.doubleToLongBits(q) != Double.doubleToLongBits(other.q))
			return false;
		return true;
	}
	
	

	
}
