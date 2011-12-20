/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.simulation;

import java.util.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

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
	private final IScope executionStack, outputStack, globalStack;
	private final List<IScope> stackPool;

	public AbstractSimulation(final IExperiment exp, final ParametersSet parameters)
		throws GamaRuntimeException, InterruptedException {
		experiment = exp;
		number = simulationNumber++;
		stackPool = new GamaList();
		executionStack = obtainNewScope();
		outputStack = obtainNewScope();
		globalStack = obtainNewScope();
		// GUI.debug("Instanciating a new scheduler");
		scheduler = new Scheduler(this);
		// GUI.debug("Initializing the simulation with " + parameters);
		initialize(parameters);
	}

	@Override
	public IScheduler getScheduler() {
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

	protected void initialize(final ParametersSet parameters) throws GamaRuntimeException,
		InterruptedException {
		isLoading = true;
		if ( worldPopulation == null ) {
			initializeWorldPopulation();
		}
		GuiUtils.waitStatus("Initializing the world");
		initializeWorld(parameters);
		GuiUtils.waitStatus(" Instantiating agents ");
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
	public IScope getExecutionScope() {
		return executionStack;
	}

	public IScope getOutputStack() {
		return outputStack;
	}

	@Override
	public IScope getGlobalScope() {
		return globalStack;
	}

	@Override
	public IScope obtainNewScope() {
		if ( stackPool.isEmpty() ) { return new RuntimeScope(this); }
		return stackPool.remove(stackPool.size() - 1);
	}

	@Override
	public void releaseScope(final IScope scope) {
		stackPool.add(scope);
	}

	@Override
	public boolean isLoading() {
		return isLoading;
	}
}