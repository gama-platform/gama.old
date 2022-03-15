package gospl.algo.sr.multilayer;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.GosplMultiLayerCoordinate;
import gospl.sampler.ISampler;
import gospl.sampler.multilayer.sr.ISRMultiLayerSampler;

public interface ISynthethicReconstructionMultiLayerAlgo<SamplerType extends ISRMultiLayerSampler> {

	/**
	 * This method must provide a way to build a Synthetic Reconstructive (SR) sampler for multi layer population. 
	 * SR is known in the literature as the method to generate synthetic population using probability distribution 
	 * and monte carlo draws. The specificities of multi layered sampler has to do with the management of several
	 * probability universe, ultimately connected to one another. For example, individual into household, where we
	 * know the distribution of attribute for both individual and household but lack of data to understand the 
	 * relationship between the two universe of probability
	 * 
	 * @param matrix
	 * @return
	 * @throws IllegalDistributionCreation
	 * @throws GosplSamplerException
	 */
	public ISampler<GosplMultiLayerCoordinate> inferSRMLSampler(
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> topMatrix,
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> bottomMatrix,
			SamplerType sampler) 
			throws IllegalDistributionCreation;
	
}
