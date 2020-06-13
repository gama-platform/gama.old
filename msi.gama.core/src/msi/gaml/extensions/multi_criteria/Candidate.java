/*******************************************************************************************************
 *
 * msi.gaml.extensions.multi_criteria.Candidate.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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
