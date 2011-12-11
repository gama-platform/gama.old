/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.kernel;

import java.util.*;

import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.util.GamaList;
import msi.gaml.batch.Solution;

/**
 * Written by drogoul Modified on 1 déc. 2010
 * 
 * @todo Description
 * 
 */
public abstract class AbstractSimulation implements ISimulation {

	protected final Scheduler scheduler;
	protected final IExperiment experiment;
	protected IPopulation worldPopulation;
	protected boolean isLoading;
	protected static int simulationNumber;
	protected int number;
	public AbstractSimulation(final IExperiment exp, final Solution parameters)
		throws GamaRuntimeException, InterruptedException {
		experiment = exp;
		number = simulationNumber++;
		// GUI.debug("Instanciating a new scheduler");
		scheduler = new Scheduler(this);
		// GUI.debug("Initializing the simulation with " + parameters);
		initialize(parameters);
	}

	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}

	@Override
	public boolean isAlive() {
		return scheduler.alive /* && !scheduler.paused */;
	}

	@Override
	public boolean isPaused() {
		return /* scheduler.alive && */scheduler.paused;
	}

	@Override
	public void dispose() {
		// GUI.debug("Simulation " + number + " of experiment " + experiment.getName() +
		// " being disposed");
		if ( scheduler != null ) {
			scheduler.dispose();
		}
		// GUI.debug("Disposing the agent managers");
		worldPopulation.dispose();
		worldPopulation = null;
		// GUI.debug("Disposing the environment");
		// if ( environment != null ) {
		// environment.dispose();
		// environment = null;
		// }
	}

	@Override
	public void close() {
		dispose();
		// GUI.debug("Simulation disposed");
	}

	@Override
	public void step() {
		scheduler.stepByStep();
	}

	@Override
	public void pause() {
		scheduler.pause();
	}

	@Override
	public void stop() {
		scheduler.alive = false;
	}

	@Override
	public boolean isBatch() {
		return false;
	}

	@Override
	public void start() throws GamaRuntimeException {
		scheduler.start();
	}

	
	@Override
	public GamaList<IAgent> getAllAgents() {
		GamaList<IAgent> retVal = new GamaList<IAgent>();
		IAgent world = worldPopulation.getAgent(0);
		if (world != null) {
			retVal.addAll(world.getAgents());
			retVal.add(0, world);
		}
		
		return retVal;
	}

	@Override
	public IPopulation getWorldPopulation() {
		return worldPopulation;
	}

	@Override
	public String getName() {
		return experiment.getName();
	}

	@Override
	public IAgent getWorld() {
		return worldPopulation.getAgent(0);
	}
	
	protected abstract void initializeWorldPopulation();
	
	protected void initialize(final Solution parameters) throws GamaRuntimeException,
		InterruptedException {
		isLoading = true;
		if (worldPopulation == null) { initializeWorldPopulation(); }
		GUI.waitStatus("Initializing the world");
		initializeWorld(parameters);
		GUI.waitStatus(" Instantiating agents ");
		scheduler.enterInitSequence();
		isLoading = false;
	}

	protected abstract void initializeWorld(Map<String, Object> parameters)
		throws GamaRuntimeException, InterruptedException;

	@Override
	public IModel getModel() {
		return experiment.getModel();
	}

	@Override
	public IScope getGlobalScope() {
		return scheduler.getGlobalStack();
	}

	@Override
	public boolean isLoading() {
		return isLoading;
	}
}