/*******************************************************************************************************
 *
 * CachedSegmentedNDimensionalMatrix.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.distribution.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;

/**
 * The Class CachedSegmentedNDimensionalMatrix.
 *
 * @param <T> the generic type
 */
public class CachedSegmentedNDimensionalMatrix<T extends Number> extends
		CachedNDimensionalMatrix<Attribute<? extends IValue>, IValue, T> implements ISegmentedNDimensionalMatrix<T> {

	/** The m seg. */
	@SuppressWarnings ("unused") private final ISegmentedNDimensionalMatrix<T> mSeg;

	/** The cached sub matrices. */
	private final Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>> cachedSubMatrices;

	/** The attribute 2 involvedmatrices. */
	private final Map<Attribute<? extends IValue>, Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>>> attribute2involvedmatrices =
			new HashMap<>();

	/**
	 * Instantiates a new cached segmented N dimensional matrix.
	 *
	 * @param originalMatrix the original matrix
	 */
	public CachedSegmentedNDimensionalMatrix(final ISegmentedNDimensionalMatrix<T> originalMatrix) {
		super(originalMatrix);

		mSeg = originalMatrix;

		// create list of cached sub matrices
		cachedSubMatrices = new ArrayList<>(originalMatrix.getMatrices().size());
		for (INDimensionalMatrix<Attribute<? extends IValue>, IValue, T> m : originalMatrix.getMatrices()) {
			cachedSubMatrices.add(new CachedNDimensionalMatrix<>(m));
		}

	}

	@Override
	public final Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>> getMatrices() {
		return cachedSubMatrices;
	}

	@Override
	public final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>>
			getMatricesInvolving(final Attribute<? extends IValue> att) {

		Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>> res = attribute2involvedmatrices.get(att);

		if (res == null) {
			res = this.cachedSubMatrices.stream().filter(matrix -> matrix.getDimensions().contains(att))
					.collect(Collectors.toSet());
			attribute2involvedmatrices.put(att, res);
		}

		return res;

	}

	@Override
	@SuppressWarnings ("unchecked")
	public long getHits() {

		long total = super.getHits();

		for (INDimensionalMatrix<Attribute<? extends IValue>, IValue, T> subM : cachedSubMatrices) {
			total += ((CachedNDimensionalMatrix<Attribute<? extends IValue>, IValue, Double>) subM).getHits();
		}

		return total;
	}

	@Override
	@SuppressWarnings ("unchecked")
	public long getMissed() {

		long total = super.getMissed();

		for (INDimensionalMatrix<Attribute<? extends IValue>, IValue, T> subM : cachedSubMatrices) {
			total += ((CachedNDimensionalMatrix<Attribute<? extends IValue>, IValue, Double>) subM).getMissed();
		}
		return total;
	}

}
