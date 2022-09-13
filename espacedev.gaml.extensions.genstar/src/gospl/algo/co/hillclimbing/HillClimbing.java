package gospl.algo.co.hillclimbing;

import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import gospl.GosplPopulation;
import gospl.algo.co.metamodel.AOptimizationAlgorithm;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.algo.co.metamodel.neighbor.PopulationEntityNeighborSearch;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;

/**
 * Implement random search optimization algorithm for CO based synthetic population generation
 * <p/>
 * Algorithm includes buffer sized neighboring to explore 'far solutions' when fitness is poor and narrowing
 * neighborhood process when fitness gets better. This means that the number of predicate to asses population neighbor
 * is a linear function of fitness, see {@link IPopulationNeighborSearch}
 *
 * @author kevinchapuis
 *
 */
public class HillClimbing extends AOptimizationAlgorithm<GosplPopulation> {

	private final int nbIteration;

	public HillClimbing(final double fitnessThreshold, final int nbIteration) {
		this(new PopulationEntityNeighborSearch(), fitnessThreshold, nbIteration);
	}

	public HillClimbing(final IPopulationNeighborSearch<GosplPopulation, ?> neighborSearch,
			final double fitnessThreshold, final int nbIteration) {
		super(neighborSearch, fitnessThreshold);
		this.nbIteration = nbIteration;
	}

	@Override
	public ISyntheticPopulationSolution<GosplPopulation>
			run(final ISyntheticPopulationSolution<GosplPopulation> initialSolution) {

		GSPerformanceUtil gspu = new GSPerformanceUtil(
				"Start HIll Climbing Algorithm\n" + "Population size = " + initialSolution.getSolution().size()
						+ "\nSample size = " + super.getSample().size() + "\nMax iteration = " + nbIteration
						+ "\nNeighbor search = " + super.getNeighborSearchAlgorithm().getClass().getSimpleName()
						+ "\nSolution = " + initialSolution.getClass().getSimpleName(),
				Level.DEBUG);
		gspu.setObjectif(nbIteration);

		ISyntheticPopulationSolution<GosplPopulation> bestSolution = initialSolution;
		double bestFitness = bestSolution.getFitness(this.getObjectives());
		super.getNeighborSearchAlgorithm().updatePredicates(initialSolution.getSolution());

		int iter = 0;
		int buffer = this.computeBuffer(bestFitness, initialSolution);

		while (iter++ < nbIteration && bestFitness > this.getFitnessThreshold()) {
			ISyntheticPopulationSolution<GosplPopulation> candidateState =
					bestSolution.getRandomNeighbor(super.getNeighborSearchAlgorithm(), buffer);
			double currentFitness = candidateState.getFitness(this.getObjectives());
			if (currentFitness < bestFitness) {
				bestSolution = candidateState;
				bestFitness = currentFitness;
				super.getNeighborSearchAlgorithm().updatePredicates(bestSolution.getSolution());
				buffer = this.computeBuffer(bestFitness, bestSolution);
			}
			if (iter % (nbIteration / 10) == 0) {
				gspu.sysoStempPerformance(iter / gspu.getObjectif(), this);
				gspu.sysoStempMessage("Best fitness = " + bestFitness + " (buffer = " + buffer + ") | Pop size = "
						+ bestSolution.getSolution().size());
				// System.out.println("BF = "+bestFitness+" | CF = "+currentFitness);
			}
		}

		return bestSolution;
	}

}
