/*******************************************************************************************************
 *
 * CachedNDimensionalMatrix.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.distribution.matrix;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.LRUMap;

import core.metamodel.io.GSSurveyType;
import core.util.data.GSDataParser;
import gospl.distribution.exception.IllegalNDimensionalMatrixAccess;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;

/**
 * Provides a cached access version to an NDimensionalMatrix, so every time you call getVal() on it, the answer will be
 * cached if possible.
 *
 * If the underlying matrix is modified in the meantime, the result becomes inconsistent.
 *
 * @author Samuel Thiriot
 *
 * @param <D>
 * @param <A>
 * @param <T>
 */
public class CachedNDimensionalMatrix<D, A, T extends Number> implements INDimensionalMatrix<D, A, T> {

	/**
	 * The maximal count of values to keep in cache
	 */
	public static final int MAX_SIZE = 100000;

	/** The m. */
	private final INDimensionalMatrix<D, A, T> m;

	/** The cached aspect 2 value. */
	private final LRUMap<Object, AControl<T>> cachedAspect2value;

	/** The hits. */
	private long hits = 0;

	/** The missed. */
	private long missed = 0;

	/**
	 * Instantiates a new cached N dimensional matrix.
	 *
	 * @param originalMatrix
	 *            the original matrix
	 */
	public CachedNDimensionalMatrix(final INDimensionalMatrix<D, A, T> originalMatrix) {
		this.m = originalMatrix;
		this.cachedAspect2value = new LRUMap<>(Math.min(MAX_SIZE, originalMatrix.size())); // too big ?
	}

	/**
	 * Gets the hits.
	 *
	 * @return the hits
	 */
	public long getHits() { return hits; }

	/**
	 * Gets the missed.
	 *
	 * @return the missed
	 */
	public long getMissed() { return missed; }

	@Override
	public final AControl<T> getVal(final ACoordinate<D, A> coordinate) {
		// search cache
		AControl<T> res = cachedAspect2value.get(coordinate);

		// add cache if miss
		if (res == null) {
			missed++;
			res = m.getVal(coordinate);
			cachedAspect2value.put(coordinate, res);
		} else {
			hits++;
		}

		return res;
	}

	@Override
	public final AControl<T> getVal(final A aspect) throws IllegalNDimensionalMatrixAccess {

		return this.getVal(aspect, false);
	}

	@Override
	public AControl<T> getVal(final A aspect, final boolean defaultToNul) {

		// search cache
		AControl<T> res = cachedAspect2value.get(aspect);

		// add cache if miss
		if (res == null) {
			missed++;
			res = m.getVal(aspect);
			cachedAspect2value.put(aspect, res);
		} else {
			hits++;
		}

		return res;
	}

	@Override
	public final AControl<T> getVal(final Collection<A> aspects) {

		return this.getVal(aspects, false);
	}

	@Override
	public final AControl<T> getVal(final Collection<A> aspects, final boolean defaultToNul) {

		// search cache
		AControl<T> res = cachedAspect2value.get(aspects);

		// add cache if miss
		if (res == null) {
			missed++;
			res = m.getVal(aspects);
			cachedAspect2value.put(aspects, res);
		} else {
			hits++;
		}

		return res;
	}

	@Override
	public final AControl<T> getVal(final String... coordinates) {

		// search cache
		AControl<T> res = cachedAspect2value.get(coordinates);

		// add cache if miss
		if (res == null) {
			missed++;
			res = m.getVal(coordinates);
			cachedAspect2value.put(coordinates, res);
		} else {
			hits++;
		}

		return res;
	}

	@SafeVarargs
	@Override
	public final AControl<T> getVal(final A... aspects) {

		// search cache
		AControl<T> res = cachedAspect2value.get(aspects);

		// add cache if miss
		if (res == null) {
			missed++;
			res = m.getVal(aspects);
			cachedAspect2value.put(aspects, res);
		} else {
			hits++;
		}

		return res;
	}

	@Override
	public final AControl<T> getVal() {

		// search cache
		AControl<T> res = cachedAspect2value.get(null);

		// add cache if miss
		if (res == null) {
			missed++;
			res = m.getVal();
			cachedAspect2value.put(null, res);
		} else {
			hits++;
		}

		return res;
	}

	@Override
	public final Set<A> getValues(final String... keyAndVal) throws IllegalArgumentException {
		return m.getValues(keyAndVal);
	}

	@Override
	public final boolean addValue(final ACoordinate<D, A> coordinates, final AControl<? extends Number> value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean addValue(final ACoordinate<D, A> coordinates, final T value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean addValue(final T value, final String... coordinates) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean setValue(final ACoordinate<D, A> coordinate, final AControl<? extends Number> value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean setValue(final ACoordinate<D, A> coordinate, final T value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean setValue(final T value, final String... coordinates) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Map<ACoordinate<D, A>, AControl<T>> getMatrix() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final LinkedHashMap<ACoordinate<D, A>, AControl<T>> getOrderedMatrix() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final ACoordinate<D, A> getEmptyCoordinate() { return m.getEmptyCoordinate(); }

	@Override
	public final Set<D> getDimensions() { return m.getDimensions(); }

	@Override
	public final D getDimension(final String name) throws IllegalArgumentException {
		return m.getDimension(name);
	}

	@Override
	public final Map<D, Set<? extends A>> getDimensionsAsAttributesAndValues() {
		return m.getDimensionsAsAttributesAndValues();
	}

	@Override
	public final D getDimension(final A aspect) {
		return m.getDimension(aspect);
	}

	@Override
	public final Set<A> getAspects() { return m.getAspects(); }

	@Override
	public final Set<A> getAspects(final D dimension) {
		return m.getAspects(dimension);
	}

	@Override
	public final int size() {
		return m.size();
	}

	@Override
	public final int getDegree() { return m.getDegree(); }

	@Override
	public final boolean isSegmented() { return m.isSegmented(); }

	@Override
	public final GSSurveyType getMetaDataType() { return m.getMetaDataType(); }

	@Override
	public final boolean isCoordinateCompliant(final ACoordinate<D, A> coordinate) {
		return m.isCoordinateCompliant(coordinate);
	}

	@Override
	public Set<A> getEmptyReferentCorrelate(final ACoordinate<D, A> aspect) {
		return m.getEmptyReferentCorrelate(aspect);
	}

	@Override
	public final Collection<ACoordinate<D, A>> getCoordinates(final Set<A> values) {
		return m.getCoordinates(values);
	}

	@Override
	public Collection<ACoordinate<D, A>> getOrCreateCoordinates(final Set<A> values) {
		return m.getOrCreateCoordinates(values);
	}

	@Override
	public final Collection<ACoordinate<D, A>> getCoordinates(final String... keyAndVal)
			throws IllegalArgumentException {
		return m.getCoordinates(keyAndVal);
	}

	@Override
	public ACoordinate<D, A> getCoordinate(final Set<A> values) throws NullPointerException {
		return m.getCoordinate(values);
	}

	@Override
	public final ACoordinate<D, A> getCoordinate(final String... keyAndVal) throws IllegalArgumentException {
		return m.getCoordinate(keyAndVal);
	}

	@Override
	public final boolean checkAllCoordinatesHaveValues() {
		return m.checkAllCoordinatesHaveValues();
	}

	@Override
	public boolean checkGlobalSum() {
		return m.checkGlobalSum();
	}

	@Override
	public final String toCsv(final char csvSeparator) {
		return m.toCsv(csvSeparator);
	}

	@Override
	public final AControl<T> getNulVal() { return m.getNulVal(); }

	@Override
	public final AControl<T> getIdentityProductVal() { return m.getIdentityProductVal(); }

	@Override
	public AControl<T> getAtomicVal() { return m.getAtomicVal(); }

	@Override
	public final AControl<T> parseVal(final GSDataParser parser, final String val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void normalize() throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final String getLabel() { return m.getLabel(); }

	@Override
	public final String getGenesisAsString() { return m.getGenesisAsString(); }

	@Override
	public final void inheritGenesis(final AFullNDimensionalMatrix<?> o) {
		throw new UnsupportedOperationException();

	}

	@Override
	public final void addGenesis(final String step) {
		throw new UnsupportedOperationException();

	}

}
