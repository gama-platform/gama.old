package msi.gama.kernel.batch;

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

    private ParametersSet position;        // Current position.
    private ParametersSet velocity;
    private ParametersSet bestPosition;    // Personal best solution.
    private double bestEval;        // Personal best value.

	protected HashMap<ParametersSet, Double> testedSolutions;

	BatchAgent currentExperiment;
	final Map<String, GamaPoint> parameters;
	 
	ParamSpaceExploAlgorithm algo;
    /**
     * Construct a Particle with a random starting position.
     * @param beginRange    the minimum xyz values of the position (inclusive)
     * @param endRange      the maximum xyz values of the position (exclusive)
     */
    Particle (IScope scope, BatchAgent agent, ParamSpaceExploAlgorithm algorithm, HashMap<ParametersSet, Double> testedSolutionsMap) {
        currentExperiment = agent;
        algo = algorithm;
        this.testedSolutions = testedSolutionsMap;
        final List<IParameter.Batch> v = agent.getParametersToExplore();
        parameters = new HashMap<>();
        for (IParameter p : v ) {
        	GamaPoint minMax = new GamaPoint(
        			p.getMinValue(scope) != null ? Cast.asFloat(scope,p.getMinValue(scope)) : Double.NEGATIVE_INFINITY,
        			p.getMaxValue(scope) != null ? Cast.asFloat(scope,p.getMaxValue(scope)) : Double.POSITIVE_INFINITY);
        	parameters.put(p.getName(),minMax);
        }
		position = new ParametersSet(scope, v, true);
        velocity = new ParametersSet(scope, v, true); 
        bestPosition = new ParametersSet(velocity);
        bestEval = eval();
    }

    /**
     * The evaluation of the current position.
     * @return      the evaluation
     */
    private double eval () {
    	Double fitness = testedSolutions.get(position);
		if (fitness == null) {
			fitness = currentExperiment.launchSimulationsWithSolution(position);
		}
		testedSolutions.put(position, fitness);

		/**/
		
		return fitness.doubleValue();
    }
    
    /**
     * Update the personal best if the current evaluation is better.
     */
    void updatePersonalBest () {
        double eval = eval();
        if (algo.isMaximize() && eval > bestEval
				|| !algo.isMaximize() && eval < bestEval) {
        	bestEval = eval;
        	bestPosition = new ParametersSet(position);
        }
    }

    /**
     * Get the position of the particle.
     * @return  the x position
     */
    ParametersSet getPosition () {
        return position;
    }

    /**
     * Getthe velocity of the particle.
     * @return  the velocity
     */
    ParametersSet getVelocity () {
        return velocity;
    }

    /**
     * Get  the personal best solution.
     * @return  the best position
     */
    ParametersSet getBestPosition() {
        return bestPosition;
    }

    /**
     * Get the value of the personal best solution.
     * @return  the evaluation
     */
    double getBestEval () {
        return bestEval;
    }

    /**
     * Update the position of a particle by adding its velocity to its position.
     */
    void updatePosition (IScope scope) {
    	
      	for (String key : position.keySet()) {
      		GamaPoint p = parameters.get(key);
      		double val =  Cast.asFloat(scope, position.get(key)) + Cast.asFloat(scope, velocity.get(key));
      		val = Math.min(Math.max(val, Cast.asFloat(scope, p.x)), p.y);
      		position.put(key, val );
      	}
    }

    /**
     * Set the velocity of the particle.
     * @param velocity  the new velocity
     */
    void setVelocity (ParametersSet velocity) {
        this.velocity = velocity;
    }

  
}
