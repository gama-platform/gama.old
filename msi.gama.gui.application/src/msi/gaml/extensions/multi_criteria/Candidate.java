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

import java.util.Map;

public class Candidate {

	private int index;
	private Map<String, Double> valCriteria;

	protected Candidate(final int index, final Map<String, Double> valCriteria) {
		super();
		this.index = index;
		this.valCriteria = valCriteria;
	}

	@Override
	public String toString() {
		return index + " -> " + valCriteria;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + (valCriteria == null ? 0 : valCriteria.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		Candidate other = (Candidate) obj;
		if ( index != other.index ) { return false; }
		if ( valCriteria == null ) {
			if ( other.valCriteria != null ) { return false; }
		} else if ( !valCriteria.equals(other.valCriteria) ) { return false; }
		return true;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public Map<String, Double> getValCriteria() {
		return valCriteria;
	}

	public void setValCriteria(final Map<String, Double> valCriteria) {
		this.valCriteria = valCriteria;
	}

}
