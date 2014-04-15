/*********************************************************************************************
 * 
 * 
 * 'GeneticAlgorithm.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.batch;

import gnu.trove.set.hash.THashSet;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = IKeyword.GENETIC, kind = ISymbolKind.BATCH_METHOD, with_sequence = false)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = GeneticAlgorithm.POP_DIM, type = IType.INT, optional = true),
	@facet(name = GeneticAlgorithm.CROSSOVER_PROB, type = IType.FLOAT, optional = true),
	@facet(name = GeneticAlgorithm.MUTATION_PROB, type = IType.FLOAT, optional = true),
	@facet(name = GeneticAlgorithm.NB_GEN, type = IType.INT, optional = true),
	@facet(name = GeneticAlgorithm.MAX_GEN, type = IType.INT, optional = true),
	@facet(name = IKeyword.MAXIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.MINIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.AGGREGATION, type = IType.LABEL, optional = true, values = { IKeyword.MIN, IKeyword.MAX }) }, omissible = IKeyword.NAME)
public class GeneticAlgorithm extends ParamSpaceExploAlgorithm {

	private int populationDim = 3;
	private double crossoverProb = 0.7;
	private double mutationProb = 0.1;
	private int nbPrelimGenerations = 1;
	private int maxGenerations = 20;

	private Initialization initPop;
	private CrossOver crossOverOp;
	private Mutation mutationOp;
	private Selection selectionOp;

	protected static final String POP_DIM = "pop_dim";
	protected static final String CROSSOVER_PROB = "crossover_prob";
	protected static final String MUTATION_PROB = "mutation_prob";
	protected static final String NB_GEN = "nb_prelim_gen";
	protected static final String MAX_GEN = "max_gen";

	public GeneticAlgorithm(final IDescription species) {
		super(species);
	}

	@Override
	public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
		super.initializeFor(scope, agent);

		initPop = new InitializationUniform();
		crossOverOp = new CrossOver1Pt();
		mutationOp = new Mutation1Var();
		selectionOp = new SelectionRoulette();
		final IExpression popDim = getFacet(POP_DIM);
		if ( popDim != null ) {
			populationDim = Cast.asInt(scope, popDim.value(scope));
		}
		final IExpression crossOverPb = getFacet(CROSSOVER_PROB);
		if ( crossOverPb != null ) {
			crossoverProb = Cast.asFloat(scope, crossOverPb.value(scope));
		}
		final IExpression mutationPb = getFacet(MUTATION_PROB);
		if ( mutationPb != null ) {
			mutationProb = Cast.asFloat(scope, mutationPb.value(scope));
		}
		final IExpression nbprelimgen = getFacet(NB_GEN);
		if ( nbprelimgen != null ) {
			nbPrelimGenerations = Cast.asInt(scope, nbprelimgen.value(scope));
		}
		final IExpression maxgen = getFacet(MAX_GEN);
		if ( maxgen != null ) {
			maxGenerations = Cast.asInt(scope, maxgen.value(scope));
		}

	}

	@Override
	public ParametersSet findBestSolution() throws GamaRuntimeException {
		List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		initializeTestedSolutions();
		List<Chromosome> population =
			initPop.initializePop(variables, currentExperiment, populationDim, nbPrelimGenerations, isMaximize());
		setBestFitness(isMaximize() ? Double.MIN_VALUE : Double.MAX_VALUE);
		int nbGen = 1;
		while (nbGen <= maxGenerations) {
			Set<Chromosome> children = new THashSet<Chromosome>();
			for ( final Chromosome chromosome : population ) {
				if ( getRandUniform().nextValue() < crossoverProb && !variables.isEmpty() ) {
					children.addAll(crossOverOp.crossOver(scope, chromosome,
						population.get(scope.getRandom().between(0, population.size() - 1))));
				}
			}
			population.addAll(children);
			children = null;

			Set<Chromosome> mutatePop = new THashSet<Chromosome>();
			for ( final Chromosome chromosome : population ) {
				if ( getRandUniform().nextValue() < mutationProb && !variables.isEmpty() ) {
					mutatePop.add(mutationOp.mutate(scope, chromosome, variables));
				}
			}
			population.addAll(mutatePop);
			mutatePop = null;
			computePopFitness(population);
			population = selectionOp.select(scope, population, populationDim, isMaximize());
			nbGen++;
		}
		// System.out.println("Best solution : " + bestSolution + "  fitness : "
		// + bestFitness);
		return getBestSolution();
	}

	private void computePopFitness(final List<Chromosome> population) throws GamaRuntimeException {
		for ( final Chromosome chromosome : population ) {
			final ParametersSet sol = chromosome.convertToSolution(currentExperiment.getParametersToExplore());
			double fitness = testedSolutions.get(sol);
			if ( fitness == Double.MAX_VALUE ) {
				fitness = currentExperiment.launchSimulationsWithSolution(sol);
			}
			testedSolutions.put(sol, fitness);

			chromosome.setFitness(fitness);
			if ( isMaximize() && fitness > getBestFitness() || !isMaximize() && fitness < getBestFitness() ) {
				setBestFitness(fitness);
				setBestSolution(sol);
			}
		}
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		super.addParametersTo(params, agent);
		params.add(new ParameterAdapter("Mutation probability", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return mutationProb;
			}

		});
		params.add(new ParameterAdapter("Crossover probability", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return crossoverProb;
			}

		});
		params.add(new ParameterAdapter("Population dimension", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return populationDim;
			}

		});
		params.add(new ParameterAdapter("Preliminary number of generations", IExperimentSpecies.BATCH_CATEGORY_NAME,
			IType.FLOAT) {

			@Override
			public Object value() {
				return nbPrelimGenerations;
			}

		});
		params.add(new ParameterAdapter("Max. number of generations", IExperimentSpecies.BATCH_CATEGORY_NAME,
			IType.FLOAT) {

			@Override
			public Object value() {
				return maxGenerations;
			}

		});
	}

}
