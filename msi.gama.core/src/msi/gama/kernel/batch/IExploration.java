/*******************************************************************************************************
 *
 * IExploration.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.List;

import msi.gama.kernel.batch.exploration.ExhaustiveSearch;
import msi.gama.kernel.batch.exploration.ExplicitExploration;
import msi.gama.kernel.batch.exploration.betadistribution.BetaExploration;
import msi.gama.kernel.batch.exploration.morris.MorrisExploration;
import msi.gama.kernel.batch.exploration.sobol.SobolExploration;
import msi.gama.kernel.batch.exploration.stochanalysis.StochanalysisExploration;
import msi.gama.kernel.batch.optimization.HillClimbing;
import msi.gama.kernel.batch.optimization.SimulatedAnnealing;
import msi.gama.kernel.batch.optimization.Swarm;
import msi.gama.kernel.batch.optimization.TabuSearch;
import msi.gama.kernel.batch.optimization.TabuSearchReactive;
import msi.gama.kernel.batch.optimization.genetic.GeneticAlgorithm;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;

/**
 * The class IExploration.
 *
 * @author drogoul
 * @since 26 d�c. 2011
 *
 */
public interface IExploration extends ISymbol {// , Runnable {

	/** The Constant CLASSES. */
@SuppressWarnings ("rawtypes") public static final Class[] CLASSES =
			{ GeneticAlgorithm.class, SimulatedAnnealing.class, HillClimbing.class, TabuSearch.class,
					TabuSearchReactive.class, ExhaustiveSearch.class, Swarm.class, ExplicitExploration.class,
					SobolExploration.class,MorrisExploration.class,StochanalysisExploration.class,BetaExploration.class};

	/**
	 * TODO
	 * 
	 * @param scope
	 * @param agent
	 * @throws GamaRuntimeException
	 */
	public abstract void initializeFor(IScope scope, final BatchAgent agent) throws GamaRuntimeException;

	/**
	 * TODO
	 * 
	 * @param exp
	 * @param agent
	 */
	public abstract void addParametersTo(final List<IParameter.Batch> exp, BatchAgent agent);

	/**
	 * TODO
	 * 
	 * @param scope
	 */
	public abstract void run(IScope scope);
	
	/**
	 * If the exploration is based on the optimization of a fitness or not
	 * 
	 * @return {@link Boolean}, true if based on fitness, false otherwise
	 */
	public boolean isFitnessBased();
	
	/**
	 * The expression that represents the requested outputs
	 * 
	 * @return
	 */
	public IExpression getOutputs();

}