/*******************************************************************************************************
 *
 * AGosplIPF.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.ipf;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import core.util.random.GenstarRandom;
import gospl.algo.ipf.margin.Margin;
import gospl.algo.ipf.margin.MarginDescriptor;
import gospl.algo.ipf.margin.MarginalsIPFBuilder;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.control.ControlFrequency;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.IDistributionSampler;
import gospl.sampler.IEntitySampler;
import ummisco.gama.dev.utils.DEBUG;

/**
 *
 * Higher order abstraction that contains basics principle calculation for IPF. <br>
 * Concrete classes should provide two type of outcomes:
 * <p>
 * <ul>
 * <li>one to draw from a distribution using a {@link IDistributionSampler}
 * <li>ont to draw from a samble using a {@link IEntitySampler}
 * </ul>
 * <p>
 * Two concerns must be cleared up for {@link AGosplIPF} to be fully and explicitly setup:
 * <p>
 * <ul>
 * <li>Convergence criteria: could be a number of maximum iteration {@link AGosplIPF#step}, a maximal error for any
 * objective {@link AGosplIPF#delta} or an increase in error fitting lower that delta itself
 * <li>zero-cell problem: As it is impossible to distinguish between structural 0 cell - i.e. impossible set of value,
 * like being age under 5 and retired - and conjonctural 0 cell - i.e. a set of value for which we do not have any
 * record, we should provide default as well as customizable behavior
 * <li>zero-margin problem: As we use sparse collection to store marginal records hence 0 margin is not a problem at all
 * </ul>
 * <p>
 * Usefull information could be found at {@link http://u.demog.berkeley.edu/~eddieh/datafitting.html}
 * <p>
 *
 * TODO: make it possible to choose the function to establish criteria - here is AAPD but could have been SRMSE <br>
 * TODO: make it possible to choose what IPF do when uncounter zero cell marginal
 *
 * @author kevinchapuis
 *
 */
public abstract class AGosplIPF<T extends Number> {

	/** The step. */
	private int step = 100;

	/** The delta. */
	private double delta = Math.pow(10, -4);

	/** The aapd. */
	// private double aapd = Double.MAX_VALUE;

	/** The sample seed. */
	protected IPopulation<ADemoEntity, Attribute<? extends IValue>> sampleSeed;

	/** The marginals. */
	protected INDimensionalMatrix<Attribute<? extends IValue>, IValue, T> marginals;

	/** The marginal processor. */
	protected MarginalsIPFBuilder<T> marginalProcessor;

	/**
	 * Instantiates a new a gospl IPF.
	 *
	 * @param sampleSeed
	 *            the sample seed
	 * @param marginalProcessor
	 *            the marginal processor
	 * @param step
	 *            the step
	 * @param delta
	 *            the delta
	 */
	protected AGosplIPF(final IPopulation<ADemoEntity, Attribute<? extends IValue>> sampleSeed,
			final MarginalsIPFBuilder<T> marginalProcessor, final int step, final double delta) {
		this.sampleSeed = sampleSeed;
		this.marginalProcessor = marginalProcessor;
		this.step = step;
		this.delta = delta;
	}

	/**
	 * Instantiates a new a gospl IPF.
	 *
	 * @param sampleSeed
	 *            the sample seed
	 * @param step
	 *            the step
	 * @param delta
	 *            the delta
	 */
	protected AGosplIPF(final IPopulation<ADemoEntity, Attribute<? extends IValue>> sampleSeed, final int step,
			final double delta) {
		this(sampleSeed, new MarginalsIPFBuilder<>(), step, delta);
	}

	/**
	 * Instantiates a new a gospl IPF.
	 *
	 * @param sampleSeed
	 *            the sample seed
	 * @param marginalProcessor
	 *            the marginal processor
	 */
	protected AGosplIPF(final IPopulation<ADemoEntity, Attribute<? extends IValue>> sampleSeed,
			final MarginalsIPFBuilder<T> marginalProcessor) {
		this.sampleSeed = sampleSeed;
		this.marginalProcessor = marginalProcessor;
	}

	/**
	 * Instantiates a new a gospl IPF.
	 *
	 * @param sampleSeed
	 *            the sample seed
	 */
	protected AGosplIPF(final IPopulation<ADemoEntity, Attribute<? extends IValue>> sampleSeed) {
		this(sampleSeed, new MarginalsIPFBuilder<>());
	}

	/**
	 * Setup the matrix that define marginal control. May be a full or segmented matrix: the first one will give actual
	 * marginal, while the second one will give estimate marginal
	 *
	 * @see INDimensionalMatrix#getVal(Collection)
	 * @param marginals
	 */
	protected void setMarginalMatrix(final INDimensionalMatrix<Attribute<? extends IValue>, IValue, T> marginals) {
		this.marginals = marginals;
	}

	/**
	 * Setup iteration number stop criteria
	 *
	 * @param maxStep
	 */
	protected void setMaxStep(final int maxStep) { this.step = maxStep; }

	/**
	 * Setup maximum delta (i.e. the relative absolute difference between actual and expected marginals) stop criteria
	 *
	 * @param delta
	 */
	protected void setMaxDelta(final double delta) { this.delta = delta; }

	//////////////////////////////////////////////////////////////
	// ------------------------- ALGO ------------------------- //
	//////////////////////////////////////////////////////////////

	/**
	 * Main estimation method: iteratively fit the distribution to marginal constraint using odd ratio procedure
	 *
	 * @return
	 */
	public abstract AFullNDimensionalMatrix<T> process();

	/**
	 * Main estimation method using parametrized delta threshold and maximum step iteration
	 *
	 * @see AGosplIPF#process()
	 *
	 * @param delta1
	 * @param step1
	 * @return
	 */
	public AFullNDimensionalMatrix<T> process(final double delta1, final int step1) {
		this.delta = delta1;
		this.step = step1;
		return process();
	}

	// ------------------------- GENERIC IPF ------------------------- //

	/**
	 * Describe the <i>estimation factor</i> IPF algorithm:
	 * <p>
	 * <ul>
	 * <li>Setup convergence criteria and iterate while criteria is not fulfill
	 * <li>Compute the factor to fit control
	 * <li>Adjust dimensional values to fit control
	 * <li>Update convergence criteria
	 * </ul>
	 * <p>
	 * There is other algorithm for IPF. This one is the most simple one and also the more adaptable to a n-dimensional
	 * matrix, because it does not include any matrix calculation
	 *
	 * @param seed
	 * @return
	 */
	protected AFullNDimensionalMatrix<T> process(final AFullNDimensionalMatrix<T> seed) {
		if (seed.getDimensions().stream()
				.noneMatch(dim -> marginals.getDimensions().contains(dim)
						|| marginals.getDimensions().contains(dim.getReferentAttribute())))
			throw new IllegalArgumentException(
					"Output distribution and sample seed does not have any matching dimensions\n" + "Distribution: "
							+ Arrays.toString(marginals.getDimensions().toArray()) + "\n" + "Sample seed: :"
							+ Arrays.toString(seed.getDimensions().toArray()));

		List<Attribute<? extends IValue>> unmatchSeedAttribute =
				seed.getDimensions().stream().filter(dim -> marginals.getDimensions().contains(dim)
						|| marginals.getDimensions().contains(dim.getReferentAttribute())).toList();

		GSPerformanceUtil gspu = new GSPerformanceUtil("*** IPF PROCEDURE ***", Level.INFO);
		gspu.sysoStempPerformance(0, this);

		gspu.sysoStempMessage(unmatchSeedAttribute.size() / (double) seed.getDimensions().size() * 100d
				+ "% of samples dimensions will be estimate with output controls");

		gspu.sysoStempMessage("Sample seed controls' dimension: " + seed.getDimensions().stream()
				.map(d -> d.getAttributeName() + " = " + d.getValueSpace().getValues().size())
				.collect(Collectors.joining(";")));

		Collection<Margin<T>> theMarginals = marginalProcessor.buildCompliantMarginals(this.marginals, seed);

		int stepIter = step;
		int totalNumberOfMargins = theMarginals.stream().mapToInt(Margin::size).sum();
		gspu.sysoStempMessage("Convergence criterias are: step = " + step + " | delta = " + delta);

		double total = this.marginals.getVal().getValue().doubleValue();
		double aapd = theMarginals.stream()
				.mapToDouble(m -> m.getMarginDescriptors().stream().mapToDouble(
						md -> Math.abs(seed.getVal(md.getSeed()).getDiff(m.getControl(md)).doubleValue()) / total)
						.sum())
				.sum() / totalNumberOfMargins;
		gspu.sysoStempMessage("Start fitting iterations with AAPD = " + aapd);

		double relativeIncrease = Double.MAX_VALUE;

		while (stepIter-- > 0 && aapd > delta || relativeIncrease < delta) {
			if (stepIter % (int) (step * 0.1) == 0d) {
				gspu.sysoStempMessage("Step = " + (step - stepIter) + " | average error = " + aapd, Level.DEBUG);
			}
			for (Margin<T> margin : theMarginals) {
				for (MarginDescriptor seedMarginalDescriptor : margin.getMarginDescriptors()) {
					double marginValue = margin.getControl(seedMarginalDescriptor).getValue().doubleValue();
					double actualValue = seed.getVal(seedMarginalDescriptor.getSeed()).getValue().doubleValue();

					AControl<Double> factor =
							new ControlFrequency(marginValue / (actualValue == 0d ? marginValue : actualValue)); // If
																													// zero
																													// seed
																													// marginal
																													// statu
																													// quo
					Collection<ACoordinate<Attribute<? extends IValue>, IValue>> relatedCoordinates =
							seed.getCoordinates(seedMarginalDescriptor.getSeed());
					for (ACoordinate<Attribute<? extends IValue>, IValue> coord : relatedCoordinates) {
						// When no data in seed but known marginal in control tables put atomic value in
						if (actualValue == 0d && marginValue > 0d) { seed.setValue(coord, seed.getAtomicVal()); }
						AControl<T> av = seed.getVal(coord);
						// Store value for debug
						double avbu = av.getValue().doubleValue();
						// Update value
						av.multiply(factor);

						// DEBUG ONLY
						if (DEBUG.IS_ON() && GenstarRandom.getInstance().nextDouble() < 0.01) {
							gspu.sysoStempMessage("Coord " + coord + ":\n AV = " + avbu + " | Factor = "
									+ factor.getValue().doubleValue() + " | UV = " + av.getValue().doubleValue(),
									Level.TRACE);
						}
					}
				}
			}

			double cachedAapd = theMarginals.stream()
					.mapToDouble(m -> m.getMarginDescriptors().stream().mapToDouble(
							md -> Math.abs(seed.getVal(md.getSeed()).getDiff(m.getControl(md)).doubleValue()) / total)
							.sum())
					.sum() / totalNumberOfMargins;
			relativeIncrease = Math.abs(aapd - cachedAapd);
			aapd = cachedAapd;
		}

		// WARNING: need to be verified theoretically : but in fact because IPF does not
		// guarantee convergence, normalization needs to be done but can disrupt validation process
		seed.normalize();

		gspu.sysoStempMessage(
				"IPF fitting ends with final " + aapd + " AAPD value and " + (this.step - stepIter) + " iteration(s)");
		return seed;
	}

}
