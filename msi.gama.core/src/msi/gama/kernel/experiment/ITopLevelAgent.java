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

	public ActionExecuter getActionExecuter();

	public IOutputManager getOutputManager();

}
