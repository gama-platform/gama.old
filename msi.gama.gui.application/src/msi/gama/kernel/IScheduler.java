package msi.gama.kernel;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

/**
 * The associated scheduler of an agent/host.
 */
public interface IScheduler {

	/**
	 * Return the host of this scheduler.
	 * 
	 * @return
	 */
	public abstract IAgent getHost();

	/**
	 * Each simulation step, the host agent executes the following things:
	 * 1. Update its attributes;
	 * 2. Executes its behaviors;
	 * 3. Asks agents in the "scheduling" list to step themselves.
	 */
	public abstract void step(IScope scope) throws GamaRuntimeException;

	public abstract void computeAgentsToSchedule(final IScope scope, final GamaList list)
		throws GamaRuntimeException;
}
