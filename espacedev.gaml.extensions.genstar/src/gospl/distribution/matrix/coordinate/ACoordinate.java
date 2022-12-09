/*******************************************************************************************************
 *
 * ACoordinate.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.distribution.matrix.coordinate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represent the coordinate system of a coordinate matrix. Coordinates represent correlation between parametric aspect
 * {@code <A>}: there must be only one aspect per matrix dimension, but could have from 2 to all dimensions being
 * present.
 * <p>
 * Coordinates serve to access {@link InDimensionalMatrix} through {@link InDimensionalMatrix#getVal(ACoordinate)}. See
 * also {@link InDimensionalMatrix#getVal(Collection)} and {@link InDimensionalMatrix#getVal(Object)} that rely on
 * {@link ACoordinate}
 * <p>
 *
 * @author kevinchapuis
 *
 * @param <A>
 */
public abstract class ACoordinate<D, A> {

	/** The coordinate. */
	private final Map<D, A> coordinate;

	/** The hash index. */
	private int hashIndex = -1;

	/**
	 * Instantiates a new a coordinate.
	 *
	 * @param coordinate
	 *            the coordinate
	 */
	protected ACoordinate(final Map<D, A> coordinate) {
		if (!isCoordinateSetComplient(coordinate))
			throw new IllegalArgumentException("Coordinate must complies to the moto: One attribute, one value");
		this.coordinate = coordinate;
	}

	/**
	 * Check if coordinate respect the specified constraint:
	 * <p>
	 * For each dimension {@code D} represented by {@code A} values, they must only have one value. That is two values
	 * cannot refer to the same dimension (attribute)
	 *
	 * @param coordinateSet
	 * @return <code>true</code> if coordinate complies to the "one attribute, one value" moto, <code>false</code>
	 *         otherwise
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
	 * ask if the coordinate contains or not {@code coordAspect} argument. It is based on {@link Set#contains(Object)}
	 * implementations, so be specific about {@link #equals(Object)} method specification of parametric type {@code <A>}
	 *
	 * @param coordAspect
	 * @return <code>true</code> if this {@link ACoordinate} contains {@code coordAspect} and <code>false</code>
	 *         otherwise
	 */
	public boolean contains(final A coordAspect) {
		return coordinate.containsValue(coordAspect);
	}

	/**
	 * ask if the coordinate contains all the aspects passed in argument. It is based on
	 * {@link Set#containsAll(Collection)} implementations
	 *
	 * @param aspects
	 * @return <code>true</code> if this {@link ACoordinate} contains all {@code aspects} and <code>false</code>
	 *         otherwise
	 *
	 * @see ACoordinate#contains(Object)
	 */
	public boolean containsAll(final Collection<A> aspects) {
		return coordinate.values().containsAll(aspects);
	}

	/**
	 * Utility method to manage hash of coordinate to give each one a distinct id
	 *
	 * @param hashIndex
	 */
	public void setHashIndex(final int hashIndex) {
		if (hashIndex == -1) { this.hashIndex = hashIndex; }
	}

	/**
	 * Return the set of dimension this coordinate is bind with
	 *
	 * @return {@link Set} of dimension {@code <D>}
	 */
	public Set<D> getDimensions() { return Collections.unmodifiableSet(coordinate.keySet()); }

	/**
	 * Return the underlying coordinate: each dimension is associated with one and only one aspect
	 *
	 * @return {@link Map} that bind dimension to aspect
	 */
	public Map<D, A> getMap() { return Collections.unmodifiableMap(coordinate); }

	// -------------------------------------------------------------------

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (A aspect : coordinate.values()) {
			s.append(s.isEmpty() ? "[" : " - ").append("[").append(aspect).append("]");
		}
		return s.append("]").toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// return hashIndex;
		return Objects.hash(coordinate, hashIndex);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		@SuppressWarnings ("rawtypes") ACoordinate other = (ACoordinate) obj;
		return Objects.equals(coordinate, other.coordinate) || hashIndex == other.hashIndex;
	}

}
