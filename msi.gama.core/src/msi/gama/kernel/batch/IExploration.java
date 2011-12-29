/**
 * Created by drogoul, 26 déc. 2011
 * 
 */
package msi.gama.kernel.batch;

import msi.gama.kernel.experiment.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;

/**
 * The class IExploration.
 * 
 * @author drogoul
 * @since 26 déc. 2011
 * 
 */
public interface IExploration extends ISymbol, Runnable {

	public final static short C_MAX = 0, C_MIN = 1, C_MEAN = 2;

	public abstract void initializeFor(final BatchExperiment f) throws GamaRuntimeException;

	public abstract String getCombinationName();

	public abstract void start();

	public abstract void addParametersTo(final BatchExperiment exp);

	public abstract Double getBestFitness();

	public abstract IExpression getFitnessExpression();

	public abstract ParametersSet getBestSolution();

	public abstract short getCombination();

}