package gospl.algo.sr.multilayer.ds;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import gospl.algo.sr.multilayer.ISynthethicReconstructionMultiLayerAlgo;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.GosplMultiLayerCoordinate;
import gospl.sampler.ISampler;
import gospl.sampler.multilayer.sr.GosplBiLayerReconstructionSampler;

public class DirectSamplingMultiLayerAlgo implements ISynthethicReconstructionMultiLayerAlgo<GosplBiLayerReconstructionSampler> {

	private Logger logger = LogManager.getLogger(DirectSamplingMultiLayerAlgo.class);
	
	@Override
	public ISampler<GosplMultiLayerCoordinate> inferSRMLSampler(
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> topMatrix, 
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> bottomMatrix,
			GosplBiLayerReconstructionSampler sampler)
			throws IllegalDistributionCreation {
		
		GSPerformanceUtil gspu = new GSPerformanceUtil("Compute hierachical sampler from conditional distribution", logger);
		gspu.getStempPerformance(0);

		if (topMatrix == null || bottomMatrix == null)
			throw new IllegalArgumentException("Matrix passed in parameter cannot be null or empty");
		
		if ((!topMatrix.isSegmented() && topMatrix.getMetaDataType().equals(GSSurveyType.LocalFrequencyTable))
				|| !bottomMatrix.isSegmented() && bottomMatrix.getMetaDataType().equals(GSSurveyType.LocalFrequencyTable))
		 	throw new IllegalDistributionCreation("Can't create a sampler using GosplMetaDataType#LocalFrequencyTable");
		
		sampler.setGroupLevelDistribution(GosplNDimensionalMatrixFactory.getFactory()
				.createDistribution(topMatrix,gspu));
		sampler.setEntityLevelDistribution(GosplNDimensionalMatrixFactory.getFactory()
				.createDistribution(bottomMatrix,gspu));
		
		return sampler;
	}


}
