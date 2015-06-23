/*********************************************************************************************
 *
 *
 * 'ParamSpaceExploAlgorithm.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.*;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class ParamSpaceExploAlgorithm.
 */
@inside(kinds = { ISymbolKind.EXPERIMENT })
public abstract class ParamSpaceExploAlgorithm extends Symbol implements IExploration {

	public final static String[] COMBINATIONS = new String[] { "maximum", "minimum", "average" };
	public static final Class[] CLASSES = { GeneticAlgorithm.class, SimulatedAnnealing.class, HillClimbing.class,
		TabuSearch.class, TabuSearchReactive.class, ExhaustiveSearch.class };

	static {
		AbstractGamlAdditions._constants(COMBINATIONS);
	}

	// private ContinuousUniformGenerator randUniform;
	protected HashMap<ParametersSet, Double> testedSolutions;
	protected IExpression fitnessExpression;
	protected boolean isMaximize;
	protected BatchAgent currentExperiment;
	protected IScope scope;
	private ParametersSet bestSolution;
	private Double bestFitness;
	protected short combination;

	protected abstract ParametersSet findBestSolution() throws GamaRuntimeException;

	@Override
	public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
		currentExperiment = agent;
		this.scope = scope;
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

	public ParamSpaceExploAlgorithm(final IDescription desc) {
		super(desc);
		initializeTestedSolutions();
		fitnessExpression = getFacet(IKeyword.MAXIMIZE, IKeyword.MINIMIZE);
		isMaximize = hasFacet(IKeyword.MAXIMIZE);
		String ag = getLiteral(IKeyword.AGGREGATION);
		combination = IKeyword.MAX.equals(ag) ? C_MAX : IKeyword.MIN.equals(ag) ? C_MIN : C_MEAN;

	}

	@Override
	public String getCombinationName() {
		return COMBINATIONS[combination];
	}

	@Override
	public void run() {
		try {
			findBestSolution();
		} catch (GamaRuntimeException e) {
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
		}
	}

	// @Override
	// public void start() {
	// new Thread(this, getName() + " thread").start();
	// }

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

	protected boolean isMaximize() {
		return isMaximize;
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		params.add(new ParameterAdapter("Exploration method", IExperimentPlan.BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				List<Class> classes = Arrays.asList(CLASSES);
				String name = IKeyword.METHODS[classes.indexOf(ParamSpaceExploAlgorithm.this.getClass())];
				String fit =
					fitnessExpression == null ? "" : "fitness = " + (isMaximize ? " maximize " : " minimize ") +
						fitnessExpression.serialize(false);
				String sim =
					fitnessExpression == null ? "" : (combination == C_MAX ? " max " : combination == C_MIN ? " min "
						: " average ") + "of " + agent.getSeeds().length + " simulations";
				return "Method " + name + " | " + fit + " | " + "compute the" + sim + " for each solution";
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
		// GuiUtils.debug("ParamSpaceExploAlgorithm.setBestSolution : " + bestSolution);
		this.bestSolution = new ParametersSet(bestSolution);
	}

	protected void setBestFitness(final Double bestFitness) {
		// GuiUtils.debug("ParamSpaceExploAlgorithm.setBestFitness : " + bestFitness);
		this.bestFitness = bestFitness;
	}
}
