/*******************************************************************************************************
 *
 * Particle.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.optimization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gaml.operators.Cast;

/**
 * Represents a particle from the Particle Swarm Optimization algorithm.
 */
class Particle {

	/** The position. */
	private final ParametersSet position; // Current position.

	/** The velocity. */
	private ParametersSet velocity;

	/** The best position. */
	private ParametersSet bestPosition; // Personal best solution.

	/** The best eval. */
	private double bestEval; // Personal best value.

	/** The tested solutions. */
	protected HashMap<ParametersSet, Double> testedSolutions;

	/** The current experiment. */
	BatchAgent currentExperiment;

	/** The parameters. */
	final Map<String, GamaPoint> parameters;

	/** The algo. */
	AOptimizationAlgorithm algo;

	/** The current val. */
	double currentVal;

	/**
	 * Construct a Particle with a random starting position.
	 *
	 * @param beginRange
	 *            the minimum xyz values of the position (inclusive)
	 * @param endRange
	 *            the maximum xyz values of the position (exclusive)
	 */
	Particle(final IScope scope, final BatchAgent agent, final AOptimizationAlgorithm algorithm,
			final HashMap<ParametersSet, Double> testedSolutionsMap) {
		currentExperiment = agent;
		algo = algorithm;
		this.testedSolutions = testedSolutionsMap;
		bestEval = algo.isMaximize() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		final List<IParameter.Batch> v = agent.getParametersToExplore();
		parameters = new HashMap<>();
		for (IParameter p : v) {
			GamaPoint minMax = new GamaPoint(
					p.getMinValue(scope) != null ? Cast.asFloat(scope, p.getMinValue(scope)) : Double.NEGATIVE_INFINITY,
					p.getMaxValue(scope) != null ? Cast.asFloat(scope, p.getMaxValue(scope))
							: Double.POSITIVE_INFINITY);
			parameters.put(p.getName(), minMax);
		}
		position = new ParametersSet(scope, v, true);
		velocity = new ParametersSet(scope, v, true);
		for (String key : velocity.keySet()) {
			velocity.put(key, Cast.asFloat(scope, velocity.get(key)) - Cast.asFloat(scope, position.get(key)));
		}
		bestPosition = new ParametersSet(position);
	}

	/**
	 * The evaluation of the current position.
	 *
	 * @return the evaluation
	 */
	public double eval() {
		Double fitness = testedSolutions.get(position);
		if (fitness == null) {
			fitness = algo.getFirstFitness(currentExperiment.launchSimulationsWithSolution(position));
			testedSolutions.put(position, fitness);
		}
		return fitness.doubleValue();
	}

	/**
	 * Update the personal best if the current evaluation is better.
	 */
	void updatePersonalBest() {
		// double eval = eval();
		if (algo.isMaximize() && currentVal > bestEval || !algo.isMaximize() && currentVal < bestEval) {
			bestEval = currentVal;
			bestPosition = new ParametersSet(position);
		}
	}

	/**
	 * Get the position of the particle.
	 *
	 * @return the x position
	 */
	ParametersSet getPosition() { return position; }

	/**
	 * Getthe velocity of the particle.
	 *
	 * @return the velocity
	 */
	ParametersSet getVelocity() { return velocity; }

	/**
	 * Get the personal best solution.
	 *
	 * @return the best position
	 */
	ParametersSet getBestPosition() { return bestPosition; }

	/**
	 * Get the value of the personal best solution.
	 *
	 * @return the evaluation
	 */
	double getBestEval() { return bestEval; }

	/**
	 * Update the position of a particle by adding its velocity to its position.
	 */
	void updatePosition(final IScope scope) {

		for (String key : position.keySet()) {
			GamaPoint p = parameters.get(key);
			double val = Cast.asFloat(scope, position.get(key)) + Cast.asFloat(scope, velocity.get(key));
			val = Math.min(Math.max(val, Cast.asFloat(scope, p.x)), p.y);
			position.put(key, val);
		}
	}

	/**
	 * Set the velocity of the particle.
	 *
	 * @param velocity
	 *            the new velocity
	 */
	void setVelocity(final ParametersSet velocity) { this.velocity = velocity; }

}
