package msi.gama.kernel.experiment;

import java.net.URL;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.types.*;
import org.eclipse.core.runtime.Platform;

/**
 * 
 * The class ExperimentAgent. Represents the support for the different experiment species
 * 
 * @author drogoul
 * @since 13 mai 2013
 * 
 */
@species(name = IKeyword.EXPERIMENT)
@vars({
	@var(name = IKeyword.SIMULATION, type = IType.AGENT, doc = @doc(value = "contains a reference to the current simulation being run by this experiment", comment = "will be nil if no simulation have been created. In case several simulations are launched, contains a reference to the latest one")),
	@var(name = GAMA._FATAL, type = IType.BOOL),
	@var(name = GAMA._WARNINGS, type = IType.BOOL, init = "false"),
	@var(name = ExperimentAgent.MODEL_PATH, type = IType.STRING, constant = true, doc = @doc(value = "Contains the absolute path to the folder in which the current model is located", comment = "Always terminated with a trailing separator")),
	@var(name = IKeyword.SEED, type = IType.FLOAT, doc = @doc(value = "The seed of the random number generator", comment = "Each time it is set, the random number generator is reinitialized")),

	@var(name = IKeyword.RNG, type = IType.STRING, init = "'" + IKeyword.MERSENNE + "'", doc = @doc("The random number generator to use for this simulation. Four different ones are at the disposal of the modeler: " +
		IKeyword.MERSENNE +
		" represents the default generator, based on the Mersenne-Twister algorithm. Very reliable; " +
		IKeyword.CELLULAR +
		" is a cellular automaton based generator that should be a bit faster, but less reliable; " +
		IKeyword.XOR +
		" is another choice. Much faster than the previous ones, but with short sequences; and " +
		IKeyword.JAVA +
		" invokes the standard Java generator")),

	// FIXME These variables should be part of the model agent instead
	@var(name = ExperimentAgent.MACHINE_TIME, type = IType.FLOAT, doc = @doc(value = "Returns the current system time in milliseconds", comment = "The return value is a float number")),
	@var(name = ExperimentAgent.DURATION, type = IType.STRING, doc = @doc("Returns a string containing the duration, in milliseconds, of the previous simulation cycle")),
	@var(name = ExperimentAgent.TOTAL_DURATION, type = IType.STRING, doc = @doc("Returns a string containing the total duration, in milliseconds, of the simulation since it has been launched ")),
	@var(name = ExperimentAgent.AVERAGE_DURATION, type = IType.STRING, doc = @doc("Returns a string containing the average duration, in milliseconds, of a simulation cycle.")),
	@var(name = ExperimentAgent.WORKSPACE_PATH, type = IType.STRING, constant = true, doc = @doc(value = "Contains the absolute path to the workspace of GAMA", comment = "Always terminated with a trailing separator")),
	@var(name = ExperimentAgent.PROJECT_PATH, type = IType.STRING, constant = true, doc = @doc(value = "Contains the absolute path to the project in which the current model is located", comment = "Always terminated with a trailing separator")) })
public class ExperimentAgent extends GamlAgent implements IExperimentAgent {

	public static final String DURATION = "duration";
	public static final String MACHINE_TIME = "machine_time";
	public static final String TOTAL_DURATION = "total_duration";
	public static final String AVERAGE_DURATION = "average_duration";
	public static final String MODEL_PATH = "model_path";
	public static final String WORKSPACE_PATH = "workspace_path";
	public static final String PROJECT_PATH = "project_path";

	private static final IShape SHAPE = GamaGeometryType.createPoint(new GamaPoint(-1, -1));

	private IScope scope;
	protected ISimulationAgent simulation;
	protected AgentScheduler scheduler;
	final Map<String, Object> extraParametersMap = new LinkedHashMap();
	protected RandomUtils random;
	protected boolean isLoading;

	public ExperimentAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
		super.setGeometry(SHAPE);
		// setIndex(0);
		reset();
	}

	public void reset() {
		// We close any simulation that might be running
		closeSimulation();
		// We set the "interrupted" flag to false
		// interrupted = false;
		// We create a fresh new scope
		GAMA.releaseScope(scope);
		scope = obtainNewScope();
		// We create a new scheduler if there isnt one already and unschedule the existing one if needed
		if ( scheduler != null ) {
			GAMA.controller.scheduler.unschedule(scheduler);
		}
		scheduler = new AgentScheduler(scope, this);
		// We initialize the population that will host the simulation
		createSimulationPopulation();
		// // We create a fake simulation (used until the "true" simulation is created in init())
		// simulation = new FakeSimulation();
		// We initialize a new random number generator
		random = new RandomUtils(getSpecies().getCurrentSeed());
		// And we schedule the agent in its own scheduler
		// schedule();
	}

	@Override
	public IScope obtainNewScope() {
		if ( dead ) { return null; }
		return new ExperimentAgentScope();
	}

	protected void closeSimulation() {
		// GuiUtils.debug("ExperimentAgent.closeSimulation");
		// // We dispose of any scheduler still running
		// if ( scheduler != null ) {
		// // We set the flag to interrupted
		// // interrupted = true;
		// // GuiUtils.debug("ExperimentAgent.closeSimulation MARKING INTERRUPTED AT TRUE");
		// scheduler.dispose();
		// scheduler = null;
		// }
		// We dispose of the simulation if any
		if ( simulation != null ) {
			simulation.dispose();
			simulation = null;
		}
	}

	@Override
	public void dispose() {
		// GuiUtils.debug("ExperimentAgent.dispose");
		if ( dead ) { return; }
		super.dispose();
		closeSimulation();
		// We dispose of any scheduler still running
		if ( scheduler != null ) {
			// GuiUtils.debug("ExperimentAgent.closeSimulation MARKING INTERRUPTED AT TRUE");
			scheduler.dispose();
			scheduler = null;
		}

	}

	@Override
	public boolean isLoading() {
		return isLoading;
	}

	/**
	 * Redefinition of the callback method
	 * @see msi.gama.metamodel.agent.GamlAgent#_init_(msi.gama.runtime.IScope)
	 */
	@Override
	public Object _init_(IScope scope) {
		// GuiUtils.debug("ExperimentAgent._init_");
		if ( scope.interrupted() ) { return null; }
		// We execute any behavior defined in GAML. The simulation is not yet defined (only the 'fake' one).
		super._init_(scope);
		// We gather the parameters set in the experiment
		ParametersSet parameters = new ParametersSet(getSpecies().getCurrentSolution());
		// Add the ones set during the "fake" simulation episode
		parameters.putAll(extraParametersMap);
		// This is where the simulation agent is created
		return createSimulationAgent(parameters, getSpecies().getCurrentSeed());

	}

	// Callback from SimulationPopulation.createAgents() once a new simulation is created
	public void scheduleSimulation(SimulationAgent sim) {
		// GuiUtils.debug("ExperimentAgent.scheduleSimulation in GLOBAL SCHEDULER");
		// The scheduler of the outputs is scheduled in the global scheduler in its own scope that is linked with the
		// simulation.
		// The launch could have been interrupted before, so we are careful to check if we still have a valid agent and
		// a valid scope
		GAMA.controller.scheduler.schedule(sim.getScheduler(), sim.getScope());
		IScope outputScope = sim.obtainNewScope();
		if ( outputScope != null ) {
			GAMA.controller.scheduler.schedule(getSpecies().getOutputManager(), outputScope);
		} else {
			GuiUtils.hideView(GuiUtils.PARAMETER_VIEW_ID);
			GuiUtils.hideMonitorView();
		}

	}

	@Override
	protected Object stepSubPopulations(IScope scope) {
		// The experiment DOES NOT step its subpopulations
		return this;
	}

	@Override
	public RandomUtils getRandomGenerator() {
		return random == null ? RandomUtils.getDefault() : random;
	}

	/**
	 * Building the simulation agent and its population
	 */

	protected void createSimulationPopulation() {
		SimulationPopulation pop = (SimulationPopulation) getMicroPopulation(getModel());
		if ( pop != null ) {
			pop.dispose();
		}
		pop = new SimulationPopulation(getModel());
		pop.initializeFor(scope);
		microPopulations.put(getModel(), pop);
		pop.setHost(this);
	}

	protected IAgent createSimulationAgent(final HashMap<String, Object> sol, final Double seed) {
		// GuiUtils.debug("ExperimentAgent.createSimulationAgent");
		GuiUtils.waitStatus("Initializing simulation");
		isLoading = true;
		IPopulation pop = getMicroPopulation(getModel());
		// 'simulation' is set by a callback call to setSimulation()
		pop.createAgents(scope, 1, GamaList.with(sol), false);
		if ( scope.interrupted() ) {
			isLoading = false;
			return null;
		}
		GuiUtils.waitStatus("Instantiating agents");
		if ( scope.interrupted() ) {
			isLoading = false;
			return null;
		}
		// GuiUtils.debug("ExperimentAgent.createSimulationAgent : Scheduling the simulation in its scheduler");
		// simulation.schedule();
		isLoading = false;
		GuiUtils.informStatus("Simulation Ready");
		return simulation;
	}

	public void setSimulation(ISimulationAgent sim) {
		simulation = sim;
	}

	/**
	 * Scope related utilities
	 * 
	 */

	@Override
	public IScope getScope() {
		return scope;
	}

	/**
	 * Overrides of GamlAgent
	 * @see msi.gama.metamodel.agent.GamlAgent#getSimulation()
	 */

	public ISimulationAgent getSimulation() {
		return simulation;
	}

	@Override
	public AgentScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public IModel getModel() {
		return getSpecies().getModel();
	}

	@Override
	public IExperimentAgent getExperiment() {
		return this;
	}

	@Override
	public IExperimentSpecies getSpecies() {
		return (IExperimentSpecies) super.getSpecies();
	}

	@Override
	public synchronized void setLocation(final ILocation newGlobalLoc) {}

	@Override
	public synchronized void setGeometry(final IShape newGlobalGeometry) {}

	/**
	 * GAML global variables
	 * 
	 */

	@getter(ExperimentAgent.MODEL_PATH)
	public String getModelPath(final IAgent agent) {
		return agent.getModel().getFolderPath() + "/";
	}

	@getter(value = WORKSPACE_PATH, initializer = true)
	public String getWorkspacePath(final IAgent agent) {
		URL url = Platform.getInstanceLocation().getURL();
		return url.getPath();
	}

	@getter(PROJECT_PATH)
	public String getProjectPath(final IAgent agent) {
		return agent.getModel().getProjectPath() + "/";
	}

	@getter(DURATION)
	public String getDuration(final IScope scope, final IAgent agent) {
		return Long.toString(simulation.getClock().getDuration());
	}

	@getter(TOTAL_DURATION)
	public String getTotalDuration(final IScope scope, final IAgent agent) {
		return Long.toString(simulation.getClock().getTotalDuration());
	}

	@getter(AVERAGE_DURATION)
	public String getAverageDuration(final IScope scope, final IAgent agent) {
		return Double.toString(simulation.getClock().getAverageDuration());
	}

	@getter(MACHINE_TIME)
	public Double getMachineTime(final IAgent agent) {
		return (double) System.currentTimeMillis();
	}

	@setter(MACHINE_TIME)
	public void setMachineTime(final IAgent agent, final Double t) throws GamaRuntimeException {
		// NOTHING
	}

	@getter(value = GAMA._FATAL, initializer = true)
	public Boolean getFatalErrors(final IAgent agent) {
		return GAMA.REVEAL_ERRORS_IN_EDITOR;
	}

	@getter(value = GAMA._WARNINGS, initializer = true)
	public Boolean getWarningsAsErrors(final IAgent agent) {
		return GAMA.TREAT_WARNINGS_AS_ERRORS;
	}

	@setter(GAMA._WARNINGS)
	public void setWarningsAsErrors(final IAgent agent, final boolean t) {
		GAMA.TREAT_WARNINGS_AS_ERRORS = t;
	}

	@getter(value = IKeyword.SEED, initializer = true)
	public Double getSeed(final IAgent agent) {
		return (double) GAMA.getRandom().getSeed();
	}

	@setter(IKeyword.SEED)
	public void setSeed(final IAgent agent, final Double s) {
		GAMA.getRandom().setSeed(s);
	}

	@getter(value = IKeyword.RNG, initializer = true)
	public String getRng(final IAgent agent) {
		return GAMA.getRandom().getGeneratorName();
	}

	@setter(IKeyword.RNG)
	public void setRng(final IAgent agent, final String newRng) {
		GAMA.getRandom().setGenerator(newRng);
	}

	@getter(IKeyword.SIMULATION)
	public IAgent getSimulation(IAgent agent) {
		return simulation;
	}

	@setter(IKeyword.SIMULATION)
	public void setSimulation(IAgent agent, final IAgent sim) {
		// TODO Nothing to do ? The 'simulation' variable is normally initialized automatically whenever a simulation is
		// created
	}

	/**
	 * 
	 * The class ExperimentAgentScope. A "pass through" class used when the simulation is not yet computed and when an
	 * experiment tries to have access to simulation attributes (which is the case in most of the models). It returns
	 * and sets the values of parameters when they are defined in the experiment, and also allows extra parameters to be
	 * set.
	 * TODO Allow this class to read the init values of global variables that are not defined as parameters.
	 * 
	 * @author drogoul
	 * @since 22 avr. 2013
	 * 
	 */
	private class ExperimentAgentScope extends Scope {

		@Override
		public Object getGlobalVarValue(String name) {
			if ( ExperimentAgent.this.hasAttribute(name) ) {
				return super.getGlobalVarValue(name);
			} else if ( getSimulation() != null ) {
				return getSimulation().getScope().getGlobalVarValue(name);
			} else if ( ExperimentAgent.this.getSpecies().hasParameter(name) ) { return ExperimentAgent.this
				.getSpecies().getParameterValue(name); }
			return extraParametersMap.get(name);
		}

		@Override
		public void setGlobalVarValue(String name, Object v) {
			if ( ExperimentAgent.this.hasAttribute(name) ) {
				super.setGlobalVarValue(name, v);
			} else if ( getSimulation() != null ) {
				getSimulation().getScope().setGlobalVarValue(name, v);
			} else if ( ExperimentAgent.this.getSpecies().hasParameter(name) ) {
				ExperimentAgent.this.getSpecies().setParameterValue(name, v);
				GuiUtils.updateParameterView();
			} else {
				extraParametersMap.put(name, v);
			}
		}

	}
	//
	// private class ExperimentAgentScheduler extends AgentScheduler {
	//
	// public ExperimentAgentScheduler(IScope scope, IStepable owner) {
	// super(scope, owner);
	// }
	//
	// }

}