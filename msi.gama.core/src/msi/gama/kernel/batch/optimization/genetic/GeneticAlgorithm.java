/*******************************************************************************************************
 *
 * GeneticAlgorithm.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.kernel.batch.optimization.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.Neighborhood;
import msi.gama.kernel.batch.Neighborhood1Var;
import msi.gama.kernel.batch.optimization.AOptimizationAlgorithm;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParameterAdapter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class GeneticAlgorithm.
 */
@symbol (
		name = IKeyword.GENETIC,
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH, IConcept.ALGORITHM })
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				internal = true,
				doc = @doc ("The name of this method. For internal use only")),
				@facet (
						name = GeneticAlgorithm.POP_DIM,
						type = IType.INT,
						optional = true,
						doc = @doc ("size of the population (number of individual solutions)")),
				@facet (
						name = GeneticAlgorithm.CROSSOVER_PROB,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("crossover probability between two individual solutions")),
				@facet (
						name = GeneticAlgorithm.MUTATION_PROB,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("mutation probability for an individual solution")),
				@facet (
						name = GeneticAlgorithm.NB_GEN,
						type = IType.INT,
						optional = true,
						doc = @doc ("number of random populations used to build the initial population")),
				@facet (
						name = GeneticAlgorithm.MAX_GEN,
						type = IType.INT,
						optional = true,
						doc = @doc ("number of generations")),
				@facet (
						name = GeneticAlgorithm.IMPROVE_SOL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("if true, use a hill climbing algorithm to improve the solutions at each generation")),
				@facet (
						name = GeneticAlgorithm.STOCHASTIC_SEL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("if true, use a stochastic selection algorithm (roulette) rather a determistic one (keep the best solutions)")),
				@facet (
						name = IKeyword.MAXIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to maximize")),
				@facet (
						name = IKeyword.MINIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to minimize")),
				@facet (
						name = IKeyword.AGGREGATION,
						type = IType.LABEL,
						optional = true,
						values = { IKeyword.MIN, IKeyword.MAX, "avr" },
						doc = @doc ("the agregation method")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This is a simple implementation of Genetic Algorithms (GA). See the wikipedia article and [batch161 the batch dedicated page]. The principle of the GA is to search an optimal solution by applying evolution operators on an initial population of solutions. There are three types of evolution operators: crossover, mutation and selection. Different techniques can be applied for this selection. Most of them are based on the solution quality (fitness).",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the `genetic` statement uses `method genetic` instead of the expected `genetic name: id` : ",
				examples = { @example (
						value = "method genetic [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method genetic maximize: food_gathered pop_dim: 5 crossover_prob: 0.7 mutation_prob: 0.1 nb_prelim_gen: 1 max_gen: 20; ",
								isExecutable = false) }) })
public class GeneticAlgorithm extends AOptimizationAlgorithm {

	/** The population dim. */
	int populationDim = 3;
	
	/** The crossover prob. */
	double crossoverProb = 0.7;
	
	/** The mutation prob. */
	double mutationProb = 0.1;
	
	/** The nb prelim generations. */
	int nbPrelimGenerations = 1;
	
	/** The max generations. */
	int maxGenerations = 20;

	/** The init pop. */
	Initialization initPop;
	
	/** The cross over op. */
	CrossOver crossOverOp;
	
	/** The mutation op. */
	Mutation mutationOp;
	
	/** The selection op. */
	Selection selectionOp;
	
	/** The improve solution. */
	Boolean improveSolution;

	/** The Constant POP_DIM. */
	protected static final String POP_DIM = "pop_dim";
	
	/** The Constant CROSSOVER_PROB. */
	protected static final String CROSSOVER_PROB = "crossover_prob";
	
	/** The Constant MUTATION_PROB. */
	protected static final String MUTATION_PROB = "mutation_prob";
	
	/** The Constant NB_GEN. */
	protected static final String NB_GEN = "nb_prelim_gen";
	
	/** The Constant MAX_GEN. */
	protected static final String MAX_GEN = "max_gen";
	
	/** The Constant IMPROVE_SOL. */
	protected static final String IMPROVE_SOL = "improve_sol";
	
	/** The Constant STOCHASTIC_SEL. */
	protected static final String STOCHASTIC_SEL = "stochastic_sel";

	/** The neighborhood. */
	protected Neighborhood neighborhood;

	/**
	 * Instantiates a new genetic algorithm.
	 *
	 * @param species the species
	 */
	public GeneticAlgorithm(final IDescription species) {
		super(species);
		initParams();
	}

	@Override
	public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
		super.initializeFor(scope, agent);
		final IExpression impSol = getFacet(IMPROVE_SOL);
		if (impSol != null) {
			improveSolution = Cast.asBool(scope, impSol.value(scope));
		}
		if (improveSolution != null && improveSolution) {
			final List<IParameter.Batch> v = agent.getParametersToExplore();
			neighborhood = new Neighborhood1Var(v);
		}
	}

	@Override
	public void initParams(final IScope scope) {
		initPop = new InitializationUniform();
		crossOverOp = new CrossOver1Pt();
		mutationOp = new Mutation1Var();
		final IExpression sts = getFacet(STOCHASTIC_SEL);
		if (sts != null) {
			final Boolean useStoc = Cast.asBool(scope, sts.value(scope));
			if (useStoc != null && useStoc) {
				selectionOp = new SelectionRoulette();
			} else {
				selectionOp = new SelectionBest();
			}
		} else {
			selectionOp = new SelectionBest();
		}

		final IExpression popDim = getFacet(POP_DIM);
		if (popDim != null) {
			populationDim = Cast.asInt(scope, popDim.value(scope));
		}
		final IExpression crossOverPb = getFacet(CROSSOVER_PROB);
		if (crossOverPb != null) {
			crossoverProb = Cast.asFloat(scope, crossOverPb.value(scope));
		}
		final IExpression mutationPb = getFacet(MUTATION_PROB);
		if (mutationPb != null) {
			mutationProb = Cast.asFloat(scope, mutationPb.value(scope));
		}
		final IExpression nbprelimgen = getFacet(NB_GEN);
		if (nbprelimgen != null) {
			nbPrelimGenerations = Cast.asInt(scope, nbprelimgen.value(scope));
		}
		final IExpression maxgen = getFacet(MAX_GEN);
		if (maxgen != null) {
			maxGenerations = Cast.asInt(scope, maxgen.value(scope));
		}
	}

	@Override
	public ParametersSet findBestSolution(final IScope scope) throws GamaRuntimeException {
		final List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		setBestFitness(null);
		initializeTestedSolutions();
		List<Chromosome> population = initPop.initializePop(scope, variables, this);
		int nbGen = 1;
		while (nbGen <= maxGenerations) {
			try (ICollector<Chromosome> children = Collector.getSet()) {
				for (final Chromosome chromosome : population) {
					if (scope.getRandom().next() < crossoverProb && !variables.isEmpty()) {
						children.addAll(crossOverOp.crossOver(scope, chromosome,
								population.get(scope.getRandom().between(0, population.size() - 1))));
					}
				}
				population.addAll(children.items());
			}

			try (ICollector<Chromosome> mutatePop = Collector.getSet()) {
				for (final Chromosome chromosome : population) {
					if (scope.getRandom().next() < mutationProb && !variables.isEmpty()) {
						mutatePop.add(mutationOp.mutate(scope, chromosome, variables));
					}
				}
				population.addAll(mutatePop.items());
			}
			if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue())
				computePopFitnessAll(scope, population);
			else
				computePopFitness(scope, population);
			population = population.stream().distinct().toList();
			population = selectionOp.select(scope, population, populationDim, isMaximize());
			nbGen++;
		}
		// DEBUG.LOG("Best solution : " + bestSolution + " fitness : "
		// + bestFitness);
		return getBestSolution();
	}

	/**
	 * Compute pop fitness.
	 *
	 * @param scope the scope
	 * @param population the population
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void computePopFitness(final IScope scope, final List<Chromosome> population) throws GamaRuntimeException {
		for (final Chromosome chromosome : population) {
			computeChroFitness(scope, chromosome);
		}
		if (this.improveSolution != null && improveSolution) {
			for (final Chromosome chromosome : population) {
				ParametersSet sol = chromosome.convertToSolution(scope, currentExperiment.getParametersToExplore());
				sol = improveSolution(scope, sol, chromosome.getFitness());
				chromosome.update(scope, sol);
				final double fitness = testedSolutions.get(sol);
				chromosome.setFitness(fitness);
			}
		}
	}
	
	/**
	 * Compute pop fitness all.
	 *
	 * @param scope the scope
	 * @param population the population
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void computePopFitnessAll(final IScope scope, final List<Chromosome> population) throws GamaRuntimeException {
		List<ParametersSet> solTotest = new ArrayList<>();
		Map<Chromosome,ParametersSet> paramToCh = GamaMapFactory.create();
		for (final Chromosome chromosome : population) {
			ParametersSet sol = chromosome.convertToSolution(scope, currentExperiment.getParametersToExplore());
			paramToCh.put( chromosome, sol);
			if (!testedSolutions.containsKey(sol)) {
				solTotest.add(sol);
			}
		}
		Map<ParametersSet, Double> fitnessRes = currentExperiment.launchSimulationsWithSolution(solTotest).entrySet().stream()
				.collect(Collectors.toMap(
						e -> e.getKey(), 
						e -> (Double) e.getValue().get(IKeyword.FITNESS).get(0)
						));
		testedSolutions.putAll(fitnessRes);
		for (final Chromosome chromosome : population) {
			ParametersSet ps = paramToCh.get(chromosome);
			if (ps != null) {
				Double fitness = fitnessRes.get(ps);
				if (fitness != null) chromosome.setFitness(fitness);
				else  chromosome.setFitness(testedSolutions.get(ps));
			}
				
		}
		
		if (this.improveSolution != null && improveSolution) {
			for (final Chromosome chromosome : population) {
				ParametersSet sol = chromosome.convertToSolution(scope, currentExperiment.getParametersToExplore());
				sol = improveSolution(scope, sol, chromosome.getFitness());
				chromosome.update(scope, sol);
				final double fitness = testedSolutions.get(sol);
				chromosome.setFitness(fitness);
			}
		}
		
		
	}

	
	/**
	 * Compute chro fitness.
	 *
	 * @param scope the scope
	 * @param chromosome the chromosome
	 */
	public void computeChroFitness(final IScope scope, final Chromosome chromosome) {
		final ParametersSet sol = chromosome.convertToSolution(scope, currentExperiment.getParametersToExplore());
		Double fitness = testedSolutions.get(sol);
		if (fitness == null) {
			fitness = (Double) currentExperiment.launchSimulationsWithSolution(sol)
					.get(IKeyword.FITNESS).get(0);
		}
		testedSolutions.put(sol, fitness);
		chromosome.setFitness(fitness);
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		super.addParametersTo(params, agent);
		params.add(new ParameterAdapter("Mutation probability", IExperimentPlan.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return mutationProb;
			}

		});
		params.add(new ParameterAdapter("Crossover probability", IExperimentPlan.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return crossoverProb;
			}

		});
		params.add(new ParameterAdapter("Population dimension", IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return populationDim;
			}

		});
		params.add(new ParameterAdapter("Preliminary number of generations", IExperimentPlan.BATCH_CATEGORY_NAME,
				IType.FLOAT) {

			@Override
			public Object value() {
				return nbPrelimGenerations;
			}

		});
		params.add(
				new ParameterAdapter("Max. number of generations", IExperimentPlan.BATCH_CATEGORY_NAME, IType.FLOAT) {

					@Override
					public Object value() {
						return maxGenerations;
					}

				});
	}
	
	public Map<ParametersSet, Double>  testSolutions(List<ParametersSet> solutions) {
		Map<ParametersSet, Double> results = GamaMapFactory.create();
		solutions.removeIf(a -> a == null);
		List<ParametersSet> solTotest = new ArrayList<>();
		for (ParametersSet sol : solutions) {
			if (testedSolutions.containsKey(sol)) {
				results.put(sol, testedSolutions.get(sol));
			} else {
				solTotest.add(sol);
			}
		}
		Map<ParametersSet, Double> res = currentExperiment.launchSimulationsWithSolution(solTotest)
				.entrySet().stream().collect(Collectors.toMap(
						e -> e.getKey(), 
						e -> (Double) e.getValue().get(IKeyword.FITNESS).get(0))
						);
		testedSolutions.putAll(res);
		results.putAll(res);
		
		return results;
	}

	/**
	 * Improve solution.
	 *
	 * @param scope the scope
	 * @param solution the solution
	 * @param currentFitness the current fitness
	 * @return the parameters set
	 */
	private ParametersSet improveSolution(final IScope scope, final ParametersSet solution,
			final double currentFitness) {
		ParametersSet bestSol = solution;
		double bestFitness = currentFitness;
		while (true) {
			final List<ParametersSet> neighbors = neighborhood.neighbor(scope,solution);
			if (neighbors.isEmpty()) {
				break;
			}
			ParametersSet bestNeighbor = null;
			
			if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue() && ! currentExperiment.getParametersToExplore().isEmpty()) {
				Map<ParametersSet,Double> result = testSolutions(neighbors);
				for (ParametersSet p : result.keySet()) {
					Double neighborFitness = result.get(p);
					if (isMaximize() && neighborFitness > bestFitness || !isMaximize() && neighborFitness < bestFitness) {
						bestNeighbor = p;
						bestFitness = neighborFitness;
						bestSol = bestNeighbor;
					}	
				}
			} else {
				for (final ParametersSet neighborSol : neighbors) {
					if (neighborSol == null) {
						continue;
					}
					Double neighborFitness = testedSolutions.get(neighborSol);
					if (neighborFitness == null) {
						neighborFitness = (Double) currentExperiment.launchSimulationsWithSolution(neighborSol).get(IKeyword.FITNESS).get(0);
						testedSolutions.put(neighborSol, neighborFitness);
					}
					
					if (isMaximize() && neighborFitness > bestFitness || !isMaximize() && neighborFitness < bestFitness) {
						bestNeighbor = neighborSol;
						bestFitness = neighborFitness;
						bestSol = bestNeighbor;
					}
					
				}
			}
			if (bestNeighbor != null) {
				bestSol = bestNeighbor;
			} else {
				break;
			}
			
		}

		return bestSol;
	}

	/**
	 * Gets the population dim.
	 *
	 * @return the population dim
	 */
	public int getPopulationDim() {
		return populationDim;
	}

	/**
	 * Gets the mutation prob.
	 *
	 * @return the mutation prob
	 */
	public double getMutationProb() {
		return mutationProb;
	}

	/**
	 * Gets the nb prelim generations.
	 *
	 * @return the nb prelim generations
	 */
	public int getNbPrelimGenerations() {
		return nbPrelimGenerations;
	}

	/**
	 * Gets the max generations.
	 *
	 * @return the max generations
	 */
	public int getMaxGenerations() {
		return maxGenerations;
	}

	/**
	 * Gets the mutation op.
	 *
	 * @return the mutation op
	 */
	public Mutation getMutationOp() {
		return mutationOp;
	}

}
