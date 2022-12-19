/*******************************************************************************************************
 *
 * DirectSamplingAlgo.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.sr.ds;

import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import gospl.algo.sr.ISyntheticReconstructionAlgo;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.IDistributionSampler;
import gospl.sampler.ISampler;

/**
 * Infer a complete distribution based on a n-dimension matrix (either partial or complete) and setup a sampler based on
 * it.
 * <p>
 * The algorithme make several assumptions:
 * <p>
 * <ul>
 * <li>All variables of two dimension with no relation in the {@link INDimensionalMatrix} are supposed to be independent
 * <li>When several dimensions refer to only one main dimension: Aggregated dimensions are disband following the same
 * principle. For ex., the value "75 and more" is broke down into several equals category like "75-79", "80-84",
 * "85-89", etc.
 * <li>When several dimensions refer to only one main dimension: unmentioned variable refer to as empty variable. For
 * ex., people under the age of 15' are usually not consider in job category, so in this algorithm they will be attached
 * with variable 'empty' for dimension 'job'
 * </ul>
 * <p>
 * <p>
 * According to these hypothesis, we refer to this algorithm as DS for Direct Sampling algorithm
 * <p>
 *
 * @see GosplNDimensionalMatrixFactory#createDistribution(INDimensionalMatrix, GSPerformanceUtil)
 *
 * @author kevinchapuis
 *
 */
public class DirectSamplingAlgo implements ISyntheticReconstructionAlgo<IDistributionSampler> {

	@Override
	public ISampler<ACoordinate<Attribute<? extends IValue>, IValue>> inferSRSampler(
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> matrix,
			final IDistributionSampler sampler) throws IllegalDistributionCreation {
		if (matrix == null || matrix.getMatrix().isEmpty())
			throw new IllegalArgumentException("matrix passed in parameter cannot be null or empty");
		if (!matrix.isSegmented() && GSSurveyType.LocalFrequencyTable.equals(matrix.getMetaDataType()))
			throw new IllegalDistributionCreation(
					"can't create a sampler using only one matrix of GosplMetaDataType#LocalFrequencyTable");

		// Begin the algorithm (and performance utility)
		int theoreticalSize = matrix.getDimensions().stream().mapToInt(d -> d.getValueSpace().getValues().size())
				.reduce(1, (i1, i2) -> i1 * i2);
		GSPerformanceUtil gspu = new GSPerformanceUtil(
				"Compute independant-hypothesis-joint-distribution from conditional distribution\nTheoretical size = "
						+ theoreticalSize,
				Level.DEBUG);
		gspu.setObjectif(theoreticalSize);
		gspu.sysoStempPerformance(0, this);

		sampler.setDistribution(GosplNDimensionalMatrixFactory.getFactory().createDistribution(matrix, gspu));

		return sampler;
	}

}
