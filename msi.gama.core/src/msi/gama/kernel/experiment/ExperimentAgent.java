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
import msi.gama.util.*;
import msi.gaml.types.*;
import org.eclipse.core.runtime.Platform;

@species(name = IKeyword.EXPERIMENT)
@vars({
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

	private IScope executionStack;
	private List<IScope> stackPool;
	protected ISimulationAgent currentSimulation;
	protected AbstractScheduler scheduler;
	final Map<String, Object> extraParametersMap = new LinkedHashMap();
	protected RandomUtils random;

	protected boolean isLoading;

	public ExperimentAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
		super.setGeometry(SHAPE);
		index = 0;
		buildScheduler();
		reset();
	}

	public void buildScheduler() {
		if ( scheduler != null ) {
			scheduler.dispose();
		}
		scheduler = new Scheduler(this);
	}

	public void reset() {
		extraParametersMap.clear();
		// executionStack.clear();
		stackPool = new GamaList();
		executionStack = obtainNewScope("Execution stack of " + getName());

		initializeWorldPopulation();
		if ( currentSimulation != null ) {
			currentSimulation.dispose();
		}
		currentSimulation = new FakeSimulation();
		random = new RandomUtils(getSpecies().getCurrentSeed());
		schedule(executionStack);
	}

	protected void initializeWorldPopulation() {
		WorldPopulation pop = (WorldPopulation) getMicroPopulation(getModel());
		if ( pop != null ) {
			pop.dispose();
		}
		pop = new WorldPopulation(getModel());
		pop.initializeFor(executionStack);
		microPopulations.put(getModel(), pop);
		pop.setHost(this);
	}

	@Override
	public synchronized void dispose() {
		if ( currentSimulation != null ) {
			currentSimulation.dispose();
			currentSimulation = null;
		}
		if ( scheduler != null ) {
			scheduler.dispose();
		}
		stackPool.clear();
		super.dispose();
	}

	@Override
	public boolean isRunning() {
		return scheduler.alive;
	}

	@Override
	public boolean isLoading() {
		return isLoading;
	}

	@Override
	public boolean isPaused() {
		return scheduler.paused || scheduler.on_user_hold;
	}

	@Override
	public void startSimulation() {
		GuiUtils.debug("ExperimentAgent.startSimulation");
		if ( currentSimulation != null && isPaused() ) {
			scheduler.start();
		}
	}

	@Override
	public ISimulationAgent getSimulation() {
		return currentSimulation;
	}

	@Override
	public IScheduler getScheduler() {
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
	public synchronized void setLocation(final ILocation newGlobalLoc) {}

	@Override
	public synchronized void setGeometry(final IShape newGlobalGeometry) {}

	@Override
	public void step(final IScope scope) {
		// GuiUtils.debug("ExperimentAgent.step");
		getSpecies().getOutputManager().step(scope);
		getSpecies().getOutputManager().updateOutputs();
		super.step(scope);
	}

	@Override
	public void init(IScope scope) {
		GuiUtils.debug("ExperimentAgent.init");
		// TODO If has init...
		super.init(scope);
		// TODO Else..
		ParametersSet parameters = new ParametersSet(getSpecies().getCurrentSolution());
		parameters.putAll(extraParametersMap);
		initializeNewSimulation(parameters, getSpecies().getCurrentSeed());

	}

	@Override
	public IScope getExecutionScope() {
		return executionStack;
	}

	@Override
	public IScope obtainNewScope() {
		if ( stackPool.isEmpty() ) { return new RuntimeScope(this, "Pool runtime scope for " + getName()); }
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
	public RandomUtils getRandomGenerator() {
		return random == null ? RandomUtils.getDefault() : random;
	}

	public void userReloadExperiment() {
		GuiUtils.debug("ExperimentAgent.userReloadExperiment");
		boolean wasRunning = isRunning() && !isPaused();
		buildScheduler();
		getSpecies().desynchronizeOutputs();
		closeSimulation();
		reset();
		getSpecies().buildOutputs();
		userInitExperiment();

		// getSpecies().getOutputManager().init(executionStack);
		if ( wasRunning ) {
			startSimulation();
		}
	}

	public void userInitExperiment() {
		GuiUtils.debug("ExperimentAgent.userInitExperiment");
		try {
			scheduler.init(executionStack);
		} catch (GamaRuntimeException e) {
			// TODO
			e.printStackTrace();
		}
		getSpecies().getOutputManager().init(executionStack);
	}

	public void initializeNewSimulation(final ParametersSet sol, final Double seed) {
		GuiUtils.debug("ExperimentAgent.initializeNewSimulation");
		GuiUtils.waitStatus("Initializing simulation");
		isLoading = true;
		WorldPopulation pop = (WorldPopulation) getMicroPopulation(getModel());
		// Necessary to create it first, and then to finalize the creation (since this creation can trigger the creation
		// of other agents, which may rely on the value of getSimulation()
		currentSimulation = new GamlSimulation(pop);
		pop.finishInitializeWorld(executionStack, currentSimulation, GamaList.with(sol));
		GuiUtils.waitStatus(" Instantiating agents ");
		currentSimulation.schedule(executionStack);
		isLoading = false;
	}

	@Override
	public IExperimentSpecies getSpecies() {
		return (IExperimentSpecies) super.getSpecies();
	}

	@Override
	public void userStepExperiment() {
		GuiUtils.debug("ExperimentAgent.userStepExperiment");
		if ( !isLoading() ) {
			scheduler.stepByStep();
		}
	}

	@Override
	public void userStopExperiment() {
		GuiUtils.debug("ExperimentAgent.userStopExperiment");
		scheduler.alive = false;
	}

	@Override
	public void userPauseExperiment() {
		GuiUtils.debug("ExperimentAgent.userPauseExperiment");
		if ( !isLoading() ) {
			scheduler.pause();
		}
	}

	protected void closeSimulation() {
		GuiUtils.debug("ExperimentAgent.closeSimulation");
		userStopExperiment();
		// Hack. Should be put somewhere else but the dialogs may
		// block the execution thread.
		GuiUtils.closeDialogs();
		currentSimulation.dispose();
	}

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
		return Long.toString(scope.getClock().getDuration());
	}

	@getter(TOTAL_DURATION)
	public String getTotalDuration(final IScope scope, final IAgent agent) {
		return Long.toString(scope.getClock().getTotalDuration());
	}

	@getter(AVERAGE_DURATION)
	public String getAverageDuration(final IScope scope, final IAgent agent) {
		return Double.toString(scope.getClock().getAverageDuration());
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
		return GAMA.TREAT_ERRORS_AS_FATAL;
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

	/**
	 * 
	 * The class FakeSimulation. A "pass through" class used when the simulation is not yet computed. It returns and
	 * sets the values of parameters when they are defined in the experiment, and also allows extra parameters to be
	 * set.
	 * TODO Allow this class to read the init values of global variables that are not defined as parameters.
	 * 
	 * @author drogoul
	 * @since 22 avr. 2013
	 * 
	 */
	private class FakeSimulation extends GamlSimulation {

		public FakeSimulation() throws GamaRuntimeException {
			super(ExperimentAgent.this.getMicroPopulation(ExperimentAgent.this.getModel()));
			// TODO Init the local dictionary with the variables ? Parameters ?
		}

		@Override
		public Object getDirectVarValue(IScope scope, String n) throws GamaRuntimeException {
			if ( ExperimentAgent.this.getSpecies().hasParameter(n) ) { return ExperimentAgent.this.getSpecies()
				.getParameterValue(n); }
			return extraParametersMap.get(n);
		}

		@Override
		public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
			if ( ExperimentAgent.this.getSpecies().hasParameter(s) ) {
				ExperimentAgent.this.getSpecies().setParameterValue(s, v);
				// FIXME Verify this
				GuiUtils.updateParameterView();
			} else {
				extraParametersMap.put(s, v);
			}
		}

		@Override
		public void computeAgentsToSchedule(IScope scope, IList list) throws GamaRuntimeException {
			return;
		}

		@Override
		public IExperimentAgent getExperiment() {
			return ExperimentAgent.this;
		}

		@Override
		public IAgent getHost() {
			return ExperimentAgent.this;
		}

	}

}