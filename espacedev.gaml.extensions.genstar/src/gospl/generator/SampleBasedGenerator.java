package gospl.generator;

import core.metamodel.entity.ADemoEntity;
import gospl.GosplPopulation;
import gospl.sampler.IEntitySampler;
import gospl.sampler.ISampler;
import gospl.sampler.co.CombinatorialOptimizationSampler;
import gospl.sampler.co.MicroDataSampler;

/**
 * Generator based on sample based growth methods: randomly draw individual entity from a sample
 * Optionally drive by an optimization process
 * <p>
 * {@code Gospl} provides {@link MicroDataSampler} and {@link CombinatorialOptimizationSampler}
 * 
 * @see IEntitySampler
 * 
 * @author kevinchapuis
 *
 */
public class SampleBasedGenerator implements ISyntheticGosplPopGenerator {

	private ISampler<ADemoEntity> sampler;
	
	/**
	 * Must be created using a sampler of entity
	 * 
	 * @param sampler
	 */
	public SampleBasedGenerator(ISampler<ADemoEntity> sampler) {
		this.sampler = sampler;
	}
	
	@Override
	public GosplPopulation generate(int numberOfIndividual) {
		return new GosplPopulation(sampler.draw(numberOfIndividual));
	}

}
