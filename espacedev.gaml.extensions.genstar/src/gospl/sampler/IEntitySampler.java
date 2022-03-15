package gospl.sampler;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.IGosplConcept;
import gospl.algo.IGosplConcept.EGosplGenerationConcept;
import gospl.distribution.matrix.INDimensionalMatrix;

/**
 * Sampler to be used to draw entity from a sample of entities. One can add objectives to drive
 * drawing process
 * 
 * @author kevinchapuis
 *
 */
public interface IEntitySampler<Population extends IPopulation<ADemoEntity, Attribute<? extends IValue>>> extends ISampler<ADemoEntity> {

	/**
	 * Set the sample to draw entity from
	 * @param sample
	 */
	public void setSample(Population sample, boolean weights);
	
	/**
	 * Add new objectives to drive entity drawing process
	 * @param objectives
	 */
	public void addObjectives(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives);
	
	@Override
	default EGosplGenerationConcept getConcept() {
		return IGosplConcept.EGosplGenerationConcept.CO;
	}
	
	
}
