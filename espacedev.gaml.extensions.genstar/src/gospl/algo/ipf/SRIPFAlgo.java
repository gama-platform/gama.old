package gospl.algo.ipf;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.sr.ISyntheticReconstructionAlgo;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.IDistributionSampler;
import gospl.sampler.ISampler;

public class SRIPFAlgo extends AGosplIPF<Double> implements ISyntheticReconstructionAlgo<IDistributionSampler> {

	public SRIPFAlgo(IPopulation<ADemoEntity, Attribute<? extends IValue>> seed) {
		super(seed);
	}
	
	public SRIPFAlgo(IPopulation<ADemoEntity, Attribute<? extends IValue>> seed,
			int step, double delta) {
		super(seed, step, delta);
	}

	@Override
	public ISampler<ACoordinate<Attribute<? extends IValue>, IValue>> inferSRSampler(
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> matrix, 
			IDistributionSampler sampler)
			throws IllegalDistributionCreation {
		
		super.setMarginalMatrix(matrix);
		sampler.setDistribution(process());
		
		return sampler;
	}

	@Override
	public AFullNDimensionalMatrix<Double> process() {
		if(this.marginals == null || this.marginals.getMatrix().isEmpty()) 
			throw new IllegalArgumentException(this.getClass().getSimpleName()+" must define a matrix to setup marginals");	
		return process(new GosplNDimensionalMatrixFactory().createDistribution(sampleSeed));
	}

}
