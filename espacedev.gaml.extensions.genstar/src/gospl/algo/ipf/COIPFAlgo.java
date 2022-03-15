package gospl.algo.ipf;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.co.ICombinatorialOptimizationAlgo;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.sampler.IEntitySampler;
import gospl.sampler.ISampler;

public class COIPFAlgo extends AGosplIPF<Integer> implements ICombinatorialOptimizationAlgo<IPopulation<ADemoEntity, Attribute<? extends IValue>>, 
	IEntitySampler<IPopulation<ADemoEntity, Attribute<? extends IValue>>>> {

	public COIPFAlgo(IPopulation<ADemoEntity, Attribute<? extends IValue>> seed,
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> matrix) {
		super(seed);
		super.setMarginalMatrix(matrix);
	}
	
	public COIPFAlgo(IPopulation<ADemoEntity, Attribute<? extends IValue>> seed,
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> matrix,
			int step, double delta) {
		super(seed, step, delta);
		super.setMarginalMatrix(matrix);
	}
	
	@Override
	public ISampler<ADemoEntity> setupCOSampler(
			IPopulation<ADemoEntity, Attribute<? extends IValue>> sample,
			boolean withWeights, IEntitySampler<IPopulation<ADemoEntity, Attribute<? extends IValue>>> sampler) {
		
		sampler.setSample(sample, withWeights);
		sampler.addObjectives(process());
		
		return sampler;
	}

	@Override
	public AFullNDimensionalMatrix<Integer> process() {
		if(this.marginals == null || this.marginals.getMatrix().isEmpty()) 
			throw new IllegalArgumentException(this.getClass().getSimpleName()+" must define a matrix to setup marginals");
		return process(new GosplNDimensionalMatrixFactory().createContingency(sampleSeed));
	}

}
