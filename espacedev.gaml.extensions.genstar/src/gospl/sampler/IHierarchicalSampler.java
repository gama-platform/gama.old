package gospl.sampler;

import java.util.Collection;
import java.util.List;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import gospl.algo.IGosplConcept;
import gospl.algo.IGosplConcept.EGosplGenerationConcept;
import gospl.distribution.matrix.ASegmentedNDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;

/**
 * Main abstraction that represents a {@link EGosplGenerationConcept#SR}
 *
 * TODO : more doc
 *
 * @author kevinchapuis
 *
 */
public interface IHierarchicalSampler extends ISampler<ACoordinate<Attribute<? extends IValue>, IValue>> {

	/**
	 * Define the higher order hierarchical distribution to sample entity from
	 * 
	 * @param explorationOrder
	 * @param segmentedMatrix
	 */
	public void setDistribution(
			Collection<List<Attribute<? extends IValue>>> explorationOrder, 
			ASegmentedNDimensionalMatrix<Double> segmentedMatrix
			);
	
	@Override
	default EGosplGenerationConcept getConcept() {
		return IGosplConcept.EGosplGenerationConcept.SR;
	}
	
}
