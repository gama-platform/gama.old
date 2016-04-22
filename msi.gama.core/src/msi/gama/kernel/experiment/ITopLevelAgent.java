/**
 * Created by drogoul, 27 janv. 2016
 *
 */
package msi.gama.kernel.experiment;

import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.outputs.IOutputManager;
import msi.gama.util.GamaColor;
import msi.gaml.statements.IExecutable;

/**
 * Class ITopLevelAgent.
 *
 * @author drogoul
 * @since 27 janv. 2016
 *
 */
public interface ITopLevelAgent extends IMacroAgent {

	public SimulationClock getClock();

	public GamaColor getColor();

	public RandomUtils getRandomGenerator();

	public IOutputManager getOutputManager();

	public void postEndAction(IExecutable executable);

	public void postDisposeAction(IExecutable executable);

	public void postOneShotAction(IExecutable executable);

	public void executeAction(IExecutable executable);

	public boolean isOnUserHold();

	public void setOnUserHold(boolean state);

}
