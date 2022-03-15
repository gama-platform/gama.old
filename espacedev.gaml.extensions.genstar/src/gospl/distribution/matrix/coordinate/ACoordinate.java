package gospl.distribution.matrix.coordinate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Represent the coordinate system of a coordinate matrix. Coordinates represent correlation between 
 * parametric aspect {@code <A>}: there must be only one aspect per matrix dimension, but could have from 2 to
 * all dimensions being present.
 * <p>
 * Coordinates serve to access {@link InDimensionalMatrix} through {@link InDimensionalMatrix#getVal(ACoordinate)}. 
 * See also {@link InDimensionalMatrix#getVal(Collection)} and {@link InDimensionalMatrix#getVal(Object)} that rely 
 * on {@link ACoordinate}
 * <p>
 * 
 * @author kevinchapuis
 *
 * @param <A>
 */
public abstract class ACoordinate<D, A> {

	private Map<D, A> coordinate;
	private int hashIndex = -1;
	
	protected ACoordinate(Map<D, A> coordinate) {
		if(!isCoordinateSetComplient(coordinate))
			throw new IllegalArgumentException("Coordinate must complies to the moto: One attribute, one value");
		this.coordinate = coordinate;
	}
	
	/**
	 * Check if coordinate respect the specified constraint: <p>
	 * For each dimension {@code D} represented by {@code A} values, they must only have one value. That is two values cannot
	 * refer to the same dimension (attribute)  
	 * 
	 * @param coordinateSet
	 * @return <code>true</code> if coordinate complies to the "one attribute, one value" moto, <code>false</code> otherwise
	 */
	protected abstract boolean isCoordinateSetComplient(Map<D, A> coordinateSet);
	
	/**
	 * Gives the collection of aspect (of parametric type {@code A}) this {@link ACoordinate} contains. 
	 * 
	 * @return {@link Set}
	 */
	public Collection<A> values() {
		return Collections.unmodifiableCollection(coordinate.values());
	}

	/**
	 * The number of aspect this coordinate contains
	 * 
	 * @return
	 */
	public int size() {
		return coordinate.size();
	}
	
	/**
	 * ask if the coordinate contains or not {@code coordAspect} argument. It is based on 
	 * {@link Set#contains(Object)} implementations, so be specific about {@link #equals(Object)}
	 * method specification of parametric type {@code <A>}
	 * 
	 * @param coordAspect
	 * @return <code>true</code> if this {@link ACoordinate} contains {@code coordAspect} and <code>false</code> otherwise
	 */
	public boolean contains(A coordAspect){
		return coordinate.containsValue(coordAspect);
	}

	/**
	 * ask if the coordinate contains all the aspects passed in argument. It is based on
	 * {@link Set#containsAll(Collection)} implementations
	 * 
	 * @param aspects
	 * @return <code>true</code> if this {@link ACoordinate} contains all {@code aspects} and <code>false</code> otherwise
	 * 
	 * @see ACoordinate#contains(Object)
	 */
	public boolean containsAll(Collection<A> aspects) {
		return coordinate.values().containsAll(aspects);
	}

	/**
	 * Utility method to manage hash of coordinate to give each one
	 * a distinct id
	 * 
	 * @param hashIndex
	 */
	public void setHashIndex(int hashIndex) {
		if(hashIndex == -1)
			this.hashIndex = hashIndex;
	}
	
	/**
	 * Return the set of dimension this coordinate is bind with 
	 * 
	 * @return {@link Set} of dimension {@code <D>}
	 */
	public Set<D> getDimensions(){
		return Collections.unmodifiableSet(coordinate.keySet());
	}
	
	/**
	 * Return the underlying coordinate: each dimension is
	 * associated with one and only one aspect
	 * 
	 * @return {@link Map} that bind dimension to aspect
	 */
	public Map<D, A> getMap(){
		return Collections.unmodifiableMap(coordinate);
	}

// -------------------------------------------------------------------
	
	@Override
	public String toString(){
		String s = "";
		for(A aspect : coordinate.values())
			if(s.isEmpty())
				s+= "[["+aspect.toString()+"]";
			else
				s += " - ["+aspect.toString()+"]";
		return s+"]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
//		return hashIndex;
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + hashIndex;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		ACoordinate other = (ACoordinate) obj;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (hashIndex != other.hashIndex)
			return false;
		return true;
	}

}
