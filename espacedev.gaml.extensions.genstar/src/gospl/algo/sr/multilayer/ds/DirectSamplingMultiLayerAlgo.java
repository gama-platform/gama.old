/*******************************************************************************************************
 *
 * DirectSamplingMultiLayerAlgo.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.sr.multilayer.ds;

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

/**
 * The Class DirectSamplingMultiLayerAlgo.
 */
public class DirectSamplingMultiLayerAlgo
		implements ISynthethicReconstructionMultiLayerAlgo<GosplBiLayerReconstructionSampler> {

	@Override
	public ISampler<GosplMultiLayerCoordinate> inferSRMLSampler(
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> topMatrix,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> bottomMatrix,
			final GosplBiLayerReconstructionSampler sampler) throws IllegalDistributionCreation {

		GSPerformanceUtil gspu = new GSPerformanceUtil("Compute hierachical sampler from conditional distribution");
		gspu.getStempPerformance(0);

		if (topMatrix == null || bottomMatrix == null)
			throw new IllegalArgumentException("Matrix passed in parameter cannot be null or empty");

		if (!topMatrix.isSegmented() && GSSurveyType.LocalFrequencyTable.equals(topMatrix.getMetaDataType())
				|| !bottomMatrix.isSegmented()
						&& GSSurveyType.LocalFrequencyTable.equals(bottomMatrix.getMetaDataType()))
			throw new IllegalDistributionCreation("Can't create a sampler using GosplMetaDataType#LocalFrequencyTable");

		sampler.setGroupLevelDistribution(
				GosplNDimensionalMatrixFactory.getFactory().createDistribution(topMatrix, gspu));
		sampler.setEntityLevelDistribution(
				GosplNDimensionalMatrixFactory.getFactory().createDistribution(bottomMatrix, gspu));

		return sampler;
	}

}
