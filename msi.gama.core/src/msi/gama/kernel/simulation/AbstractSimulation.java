/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
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
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.*;
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

	protected AbstractScheduler scheduler;
	protected final IExperiment experiment;
	protected IPopulation worldPopulation;
	protected boolean isLoading;
	protected static int simulationNumber;
	protected int number;
	private final IScope executionStack, outputStack, globalStack;
	private final List<IScope> stackPool;

	public AbstractSimulation(final IExperiment exp) throws GamaRuntimeException {
		experiment = exp;
		number = simulationNumber++;
		stackPool = new GamaList();
		executionStack = obtainNewScope("Execution stack of " + exp.getName());
		outputStack = obtainNewScope("Output stack of " + exp.getName());
		globalStack = obtainNewScope("Global stack of " + exp.getName());
		// GUI.debug("Instanciating a new scheduler");
		initSchedulingPolicy();
		// scheduler = new Scheduler(this);
		// GUI.debug("Initializing the simulation with " + parameters);
		// initialize(parameters);
	}

	protected abstract void initSchedulingPolicy();

	@Override
	public IScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public IExperiment getExperiment() {
		return experiment;
	}

	@Override
	public boolean isAlive() {
		return scheduler.alive /* && !scheduler.paused */;
	}

	@Override
	public boolean isPaused() {
		// TODO Verify that the use of user_hold here does not harm the run
		return /* scheduler.alive && */scheduler.paused || scheduler.on_user_hold;
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
		// GisUtils.setTransformCRS(null);
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
	public WorldAgent getWorld() {
		return (WorldAgent) worldPopulation.getAgent(0);
	}

	protected void initializeWorldPopulation() {
		worldPopulation = new WorldPopulation(getModel().getWorldSpecies());
	}

	@Override
	public void initialize(final ParametersSet parameters) throws GamaRuntimeException,
		InterruptedException {
		isLoading = true;
		if ( worldPopulation == null ) {
			initializeWorldPopulation();
		}
		GuiUtils.waitStatus("Initializing the world");
		initializeWorld(parameters);
		GuiUtils.waitStatus(" Instantiating agents ");
		scheduler.enterInitSequence(getExecutionScope());
		isLoading = false;
	}

	protected void initializeWorld(final Map<String, Object> parameters)
		throws GamaRuntimeException {
		WorldPopulation g = (WorldPopulation) getWorldPopulation();
		g.initializeFor(getGlobalScope());
		// Here, the link is being made with the experimentator agent, which becomes the "host" of
		// the world population
		experiment.getAgent().addMicroPopulation(g);
		// Here
		List<? extends IAgent> newAgents =
			g.createAgents(getGlobalScope(), 1, GamaList.with(parameters), false);
		IAgent world = newAgents.get(0);
		experiment.getAgent().schedule(getGlobalScope());
		world.schedule(getGlobalScope());
		// TODO is it still necessary ?
		world.initializeMicroPopulations(getGlobalScope());
	}

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
		if ( stackPool.isEmpty() ) { return new RuntimeScope(this, "Pool runtime scope for " +
			getName()); }
		return stackPool.remove(stackPool.size() - 1);
	}

	public IScope obtainNewScope(final String name) {
		for ( IScope scope : stackPool ) {
			if ( scope.getName().equals(name) ) { return scope; }
		}
		return new RuntimeScope(this, name);
	}

	@Override
	public void releaseScope(final IScope scope) {
		scope.clear();
		stackPool.add(scope);
	}

	@Override
	public boolean isLoading() {
		return isLoading;
	}
}