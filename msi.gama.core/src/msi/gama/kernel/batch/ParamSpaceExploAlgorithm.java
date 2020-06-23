/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.ParamSpaceExploAlgorithm.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParameterAdapter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class ParamSpaceExploAlgorithm.
 */
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
public abstract class ParamSpaceExploAlgorithm extends Symbol implements IExploration {

	public final static String[] COMBINATIONS = new String[] { "maximum", "minimum", "average" };
	@SuppressWarnings ("rawtypes") public static final Class[] CLASSES =
			{ GeneticAlgorithm.class, SimulatedAnnealing.class, HillClimbing.class, TabuSearch.class,
					TabuSearchReactive.class, ExhaustiveSearch.class };

	static {
		AbstractGamlAdditions._constants(COMBINATIONS);
	}

	// private ContinuousUniformGenerator randUniform;
	protected HashMap<ParametersSet, Double> testedSolutions;
	protected IExpression fitnessExpression;
	protected boolean isMaximize;
	protected BatchAgent currentExperiment;
	// protected IScope scope;
	private ParametersSet bestSolution = null;
	private Double bestFitness = null;
	protected short combination;

	protected abstract ParametersSet findBestSolution(IScope scope) throws GamaRuntimeException;

	@Override
	public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
		currentExperiment = agent;
		// this.scope = scope;
	}

	// protected ContinuousUniformGenerator getRandUniform() {
	// if ( randUniform == null ) {
	// randUniform = scope.getRandom().createUniform(0., 1.);
	// }
	// return randUniform;
	// }

	protected void initializeTestedSolutions() {
		testedSolutions = new HashMap<ParametersSet, Double>();
	}

	void initParams() {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				initParams(scope);
			}
		});
	}

	void initParams(final IScope scope) {}

	public ParamSpaceExploAlgorithm(final IDescription desc) {
		super(desc);
		initializeTestedSolutions();
		fitnessExpression = getFacet(IKeyword.MAXIMIZE, IKeyword.MINIMIZE);
		isMaximize = hasFacet(IKeyword.MAXIMIZE);
		final String ag = getLiteral(IKeyword.AGGREGATION);
		combination = IKeyword.MAX.equals(ag) ? C_MAX : IKeyword.MIN.equals(ag) ? C_MIN : C_MEAN;

	}

	@Override
	public String getCombinationName() {
		return COMBINATIONS[combination];
	}

	@Override
	public void run(final IScope scope) {
		try {
			findBestSolution(scope);
		} catch (final GamaRuntimeException e) {
			GAMA.reportError(scope, e, false);
		}
	}

	// @Override
	// public void start() {
	// new Thread(this, getName() + " thread").start();
	// }

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {}

	protected boolean isMaximize() {
		return isMaximize;
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		params.add(new ParameterAdapter("Exploration method", IExperimentPlan.BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				@SuppressWarnings ("rawtypes") final List<Class> classes = Arrays.asList(CLASSES);
				final String methodName = IKeyword.METHODS[classes.indexOf(ParamSpaceExploAlgorithm.this.getClass())];
				final String fit = fitnessExpression == null ? "" : "fitness = "
						+ (isMaximize ? " maximize " : " minimize ") + fitnessExpression.serialize(false);
				final String sim = fitnessExpression == null ? ""
						: (combination == C_MAX ? " max " : combination == C_MIN ? " min " : " average ") + "of "
								+ agent.getSeeds().length + " simulations";
				return "Method " + methodName + " | " + fit + " | " + "compute the" + sim + " for each solution";
			}

		});
	}

	@Override
	public Double getBestFitness() {
		return bestFitness;
	}

	@Override
	public IExpression getFitnessExpression() {
		return fitnessExpression;
	}

	@Override
	public ParametersSet getBestSolution() {
		return bestSolution;
	}

	@Override
	public short getCombination() {
		return combination;
	}

	protected void setBestSolution(final ParametersSet bestSolution) {
		// scope.getGui().debug("ParamSpaceExploAlgorithm.setBestSolution : " +
		// bestSolution);
		this.bestSolution = new ParametersSet(bestSolution);
	}

	protected void setBestFitness(final Double bestFitness) {
		// scope.getGui().debug("ParamSpaceExploAlgorithm.setBestFitness : " +
		// bestFitness);
		this.bestFitness = bestFitness;
	}

	@Override
	public void updateBestFitness(final ParametersSet solution, final Double fitness) {
		if (fitness == null)
			return;
		Double best = getBestFitness();
		if (best == null)
			best = 0d;
		if (bestSolution == null || (isMaximize() ? fitness > best : fitness < best)) {
			setBestFitness(fitness);
			setBestSolution(solution);
		}
	}
}
