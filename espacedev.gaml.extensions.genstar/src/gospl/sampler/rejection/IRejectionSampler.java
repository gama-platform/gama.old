package gospl.sampler.rejection;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.random.GenstarRandomUtils;
import gospl.algo.IGosplConcept;
import gospl.algo.IGosplConcept.EGosplGenerationConcept;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.sampler.IEntitySampler;

/**
 * Based on the original idea of <i>L. Sun et al. 2018</i>, this sampler consist in drawing with replacement from a known population
 * of entity taking into account know information of probability of appearance on those given entities. This method is called
 * <b>rejection sampling</b> and may serve to adapt a generated population to another circumstances, e.g. population of a country
 * to a sub-space (city, region, province) or to new knowledge on population attribute, like age difference between individual
 * in household
 * <p>
 * TODO: code a dedicated generator that can apply rejection on <i>proxy attribute</i>, like for example the age gap between two people of a couple or
 * between parent and child
 * 
 * @author kevinchapuis
 *
 * @see Sun, Lijun, Alexander Erath, and Ming Cai. “A Hierarchical Mixture Modeling Framework for Population Synthesis.” 
 * Transportation Research Part B: Methodological 114 (August 2018): 199–212. https://doi.org/10.1016/j.trb.2018.06.002.
 *
 */
public class IRejectionSampler implements IEntitySampler<IPopulation<ADemoEntity, Attribute<? extends IValue>>> {

	INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> rejectionDistribution;
	IPopulation<ADemoEntity, Attribute<? extends IValue>> basePopulation;
	
	@Override
	public ADemoEntity draw() {
		ADemoEntity entity = null;
		while(entity == null) {
		 ADemoEntity tmpEntity = GenstarRandomUtils.oneOf(basePopulation);
		 if(GenstarRandomUtils.flip(rejectionDistribution.getVal(tmpEntity.getValues()).getValue())) 
			 entity = tmpEntity; 
		}
		return entity;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: make use of {@link Stream#parallel()}
	 */
	@Override
	public Collection<ADemoEntity> draw(int numberOfDraw) {
		return IntStream.range(0,numberOfDraw).parallel().mapToObj(i -> this.draw()).toList();
	}

	@Override
	public String toCsv(String csvSeparator) {
		
		return null;
	}

	@Override
	public void setSample(IPopulation<ADemoEntity, Attribute<? extends IValue>> sample, boolean withWeights) {
		this.basePopulation = sample;
	}

	@Override
	public void addObjectives(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives) {
		GosplNDimensionalMatrixFactory gndmf = GosplNDimensionalMatrixFactory.getFactory();
		if(rejectionDistribution == null) {
			this.rejectionDistribution = gndmf.createDistribution(objectives);
		} else {
			AFullNDimensionalMatrix<Double> dToAdd = gndmf.createDistribution((AFullNDimensionalMatrix<Integer>)objectives);
			AFullNDimensionalMatrix<Double> rejectD = gndmf.createDistribution(rejectionDistribution, new GSPerformanceUtil("")); 
			try {
				this.rejectionDistribution = gndmf.createDistributionFromDistributions(Stream.of(dToAdd,rejectD).collect(Collectors.toSet()));
			} catch (IllegalDistributionCreation e) {
				
				e.printStackTrace();
			}
		}
	}

	@Override
	public EGosplGenerationConcept getConcept() {
		return IGosplConcept.EGosplGenerationConcept.MIXTURE;
	}

}
