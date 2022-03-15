package gospl.distribution.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;

public class CachedSegmentedNDimensionalMatrix<T extends Number> 
		extends CachedNDimensionalMatrix<Attribute<? extends IValue>, IValue, T>
		implements ISegmentedNDimensionalMatrix<T> {

	@SuppressWarnings("unused")
	private final ISegmentedNDimensionalMatrix<T> mSeg;
	
	private final Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue,T>> cachedSubMatrices;
	
	private final Map<Attribute<? extends IValue>, 
		Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue,T>>> attribute2involvedmatrices = new HashMap<>();
	
	public CachedSegmentedNDimensionalMatrix(ISegmentedNDimensionalMatrix<T> originalMatrix) {
		super(originalMatrix);
		
		mSeg = originalMatrix;
		
		// create list of cached sub matrices
		cachedSubMatrices = new ArrayList<>(originalMatrix.getMatrices().size());
		for (INDimensionalMatrix<Attribute<? extends IValue>, IValue,T> m: originalMatrix.getMatrices()) {
			cachedSubMatrices.add(new CachedNDimensionalMatrix<Attribute<? extends IValue>, IValue, T>(m));
		}
		
	}

	@Override
	public final Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue,T>> getMatrices() {
		return cachedSubMatrices;
	}

	@Override
	public final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue,T>> getMatricesInvolving(Attribute<? extends IValue> att) {

		Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue,T>> res = attribute2involvedmatrices.get(att);
		
		if (res == null) {
			res = this.cachedSubMatrices.stream().filter(matrix -> matrix.getDimensions().contains(att)).collect(Collectors.toSet());
			attribute2involvedmatrices.put(att, res);
		}
				
		return res;

	}
	

	@SuppressWarnings("unchecked")
	public long getHits() {
		
		long total = super.getHits();
		
		for (INDimensionalMatrix<Attribute<? extends IValue>, IValue,T> subM: cachedSubMatrices) {
			total += ((CachedNDimensionalMatrix<Attribute<? extends IValue>, IValue, Double>)subM).getHits();
		}
		
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public long getMissed() {
		
		long total = super.getMissed();

		for (INDimensionalMatrix<Attribute<? extends IValue>, IValue,T> subM: cachedSubMatrices) {
			total += ((CachedNDimensionalMatrix<Attribute<? extends IValue>, IValue, Double>)subM).getMissed();
		}
		return total;
	}

}
