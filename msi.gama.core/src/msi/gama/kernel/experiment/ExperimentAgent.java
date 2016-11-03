/*********************************************************************************************
 *
 * 'ExperimentAgent.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.kernel.simulation.SimulationClock.ExperimentClock;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IOutputManager;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.ContainerHelper;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 *
 * The class ExperimentAgent. Represents the support for the different experiment species
 *
 * @author drogoul
 * @since 13 mai 2013
 *
 */
@species (
		name = IKeyword.EXPERIMENT)
@vars ({ @var (
		name = IKeyword.SIMULATIONS,
		type = IType.LIST,
		of = ITypeProvider.MODEL_TYPE,
		doc = @doc (
				value = "contains the list of currently running simulations")),
		@var (
				name = IKeyword.SIMULATION,
				type = ITypeProvider.MODEL_TYPE,
				doc = @doc (
						value = "contains a reference to the current simulation being run by this experiment",
						comment = "will be nil if no simulation have been created. In case several simulations are launched, contains a reference to the latest one")),
		// @var(name = GAMA._FATAL, type = IType.BOOL),
		@var (
				name = GAMA._WARNINGS,
				type = IType.BOOL),
		@var (
				name = ExperimentAgent.MODEL_PATH,
				type = IType.STRING,
				constant = true,
				doc = @doc (
						value = "Contains the absolute path to the folder in which the current model is located",
						comment = "Always terminated with a trailing separator")),
		@var (
				name = IKeyword.SEED,
				type = IType.FLOAT,
				doc = @doc (
						value = "The seed of the random number generator",
						comment = "Each time it is set, the random number generator is reinitialized")),
		@var (
				name = IKeyword.RNG,
				type = IType.STRING,
				doc = @doc ("The random number generator to use for this simulation. Three different ones are at the disposal of the modeler: "
						+ IKeyword.MERSENNE
						+ " represents the default generator, based on the Mersenne-Twister algorithm. Very reliable; "
						+ IKeyword.CELLULAR
						+ " is a cellular automaton based generator that should be a bit faster, but less reliable; and "
						+ IKeyword.JAVA + " invokes the standard Java generator")),
		@var (
				name = SimulationAgent.USAGE,
				type = IType.INT,
				doc = @doc ("Returns the number of times the random number generator of the experiment has been drawn")),
		@var (
				name = ExperimentAgent.MINIMUM_CYCLE_DURATION,
				type = IType.FLOAT,
				doc = @doc (
						value = "The minimum duration (in seconds) a simulation cycle should last. Default is 0. Units can be used to pass values smaller than a second (for instance '10 °msec')",
						comment = "Useful to introduce slow_downs to fast simulations or to synchronize the simulation on some other process")),
		@var (
				name = PlatformAgent.WORKSPACE_PATH,
				type = IType.STRING,
				constant = true,
				doc = @doc (
						deprecated = "Use 'gama.workspace_path' or 'gama.workspace' instead",
						value = "Contains the absolute path to the workspace of GAMA",
						comment = "Always terminated with a trailing separator")),
		@var (
				name = ExperimentAgent.PROJECT_PATH,
				type = IType.STRING,
				constant = true,
				doc = @doc (
						value = "Contains the absolute path to the project in which the current model is located",
						comment = "Always terminated with a trailing separator")) })
@experiment (IKeyword.GUI_)
public class ExperimentAgent extends GamlAgent implements IExperimentAgent {

	public static final String MODEL_PATH = "model_path";

	public static final String PROJECT_PATH = "project_path";
	public static final String MINIMUM_CYCLE_DURATION = "minimum_cycle_duration";

	private final IScope scope;
	final ActionExecuter executer;
	final Map<String, Object> extraParametersMap = new TOrderedHashMap<>();
	protected RandomUtils random;
	protected Double initialMinimumDuration = null;
	protected Double currentMinimumDuration = 0d;
	final protected ExperimentClock clock;
	protected boolean warningsAsErrors = GamaPreferences.CORE_WARNINGS.getValue();
	protected String ownModelPath;
	// protected SimulationPopulation populationOfSimulations;
	private Boolean scheduled = false;
	private volatile boolean isOnUserHold = false;

	public ExperimentAgent(final IPopulation<? extends IAgent> s) throws GamaRuntimeException {
		super(s);
		super.setGeometry(GamaGeometryType.createPoint(new GamaPoint(-1, -1)));
		scope = new ExperimentAgentScope();
		clock = new ExperimentClock(scope);
		executer = new ActionExecuter(scope);
		reset();
	}

	@Override
	public SimulationClock getClock() {
		return clock;
	}

	public void reset() {
		clock.reset();
		// We close any simulation that might be running
		closeSimulations();
		// We initialize the population that will host the simulation
		createSimulationPopulation();
		// We initialize a new random number generator
		if (random == null) {
			random = new RandomUtils();
		} else {
			random = new RandomUtils(getDefinedSeed(), getDefinedRng());
		}
	}

	public String getDefinedRng() {
		if (GamaPreferences.CORE_RND_EDITABLE.getValue()) { return (String) ((ExperimentPlan) getSpecies()).parameters
				.get(IKeyword.RNG).value(null); }
		return GamaPreferences.CORE_RNG.getValue();
	}

	public Double getDefinedSeed() {
		if (GamaPreferences.CORE_RND_EDITABLE.getValue()) {
			final IParameter.Batch p = (Batch) ((ExperimentPlan) getSpecies()).parameters.get(IKeyword.SEED);
			final Double result = p.isDefined() ? (Double) p.value(null) : null;
			return result;
		}
		return GamaPreferences.CORE_SEED_DEFINED.getValue() ? GamaPreferences.CORE_SEED.getValue() : (Double) null;
	}

	@Override
	public void closeSimulations() {
		// We unschedule the simulation if any
		executer.executeDisposeActions();
		if (getSimulationPopulation() != null) {
			getSimulationPopulation().dispose();
		}
		if (!getSpecies().isBatch()) {
			scope.getGui().setSelectedAgent(null);
			scope.getGui().setHighlightedAgent(null);
			scope.getGui().getStatus().resumeStatus();
			// AD: Fix for issue #1342 -- verify that it does not break
			// something
			// else in the dynamics of closing/opening
			scope.getGui().closeDialogs();
		}
		// simulation = null;
		// populationOfSimulations = null;
	}

	@Override
	public void dispose() {
		if (dead) { return; }
		closeSimulations();
		GAMA.releaseScope(scope);
		super.dispose();
	}

	/**
	 * Redefinition of the callback method
	 * 
	 * @see msi.gama.metamodel.agent.GamlAgent#_init_(msi.gama.runtime.IScope)
	 */
	@Override
	public Object _init_(final IScope scope) {
		if (scope.interrupted()) { return null; }

		createSimulation(getParameterValues(), scheduled);
		// We execute any behavior defined in GAML.
		super._init_(scope);

		return this;
	}

	@Override
	public boolean init(final IScope scope) {
		super.init(scope);
		final IOutputManager outputs = getOutputManager();
		if (outputs != null) {
			outputs.init(scope);
		}
		scope.getGui().getStatus().informStatus("Experiment ready");
		scope.getGui().updateExperimentState();
		return true;
	}

	/**
	 * Method primDie()
	 * 
	 * @see msi.gama.metamodel.agent.MinimalAgent#primDie(msi.gama.runtime.IScope)
	 */
	@Override
	public Object primDie(final IScope scope) throws GamaRuntimeException {
		if (dying)
			return null;
		dying = true;
		getSpecies().getArchitecture().abort(scope);
		GAMA.closeExperiment(getSpecies());
		GAMA.getGui().closeSimulationViews(true, false);
		return null;
	}

	public void createSimulation(final ParametersSet parameters, final boolean scheduleIt) {
		final IPopulation<? extends IAgent> pop = getSimulationPopulation();
		if (pop == null) { return; }
		final ParametersSet ps = getParameterValues();
		ps.putAll(parameters);
		final IList<Map<String, Object>> list = GamaListFactory.create(Types.MAP);
		list.add(ps);
		pop.createAgents(scope, 1, list, false, scheduleIt);
	}

	public ParametersSet getParameterValues() {
		final Map<String, IParameter> parameters = getSpecies().getParameters();
		final ParametersSet ps = new ParametersSet(scope, parameters, false);
		ps.putAll(extraParametersMap);
		return ps;
	}

	@Override
	public RandomUtils getRandomGenerator() {
		return random;
	}

	@Override
	public void schedule(final IScope scope) {
		scheduled = true;
		// The experiment agent is scheduled in the global scheduler
		final ExperimentScheduler sche = getSpecies().getController().getScheduler();
		sche.schedule(this, this.scope);
	}

	/**
	 * Building the simulation agent and its population
	 */

	protected void createSimulationPopulation() {
		final IModel model = getModel();
		SimulationPopulation pop = (SimulationPopulation) this.getMicroPopulation(model);
		if (pop == null) {
			pop = new SimulationPopulation(this, model);
			setAttribute(model.getName(), pop);
			pop.initializeFor(scope);
		}
	}

	@Override
	public IScope getScope() {
		return scope;
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
	public IExperimentPlan getSpecies() {
		return (IExperimentPlan) super.getSpecies();
	}

	@Override
	public void setLocation(final ILocation newGlobalLoc) {}

	@Override
	public void setGeometry(final IShape newGlobalGeometry) {}

	/**
	 * GAML global variables
	 *
	 */

	public List<? extends IParameter.Batch> getDefaultParameters() {
		if (!GamaPreferences.CORE_RND_EDITABLE.getValue()) { return new ArrayList<>(); }
		final List<ExperimentParameter> params = new ArrayList<>();
		final String cat = getExperimentParametersCategory();
		ExperimentParameter p = new ExperimentParameter(getScope(), getSpecies().getVar(IKeyword.RNG),
				"Random number generator", cat, GamaPreferences.GENERATOR_NAMES, false);

		params.add(p);
		p = new ExperimentParameter(getScope(), getSpecies().getVar(IKeyword.SEED), "Default random seed", cat,
				"(current seed)", null, true) {

			@Override
			Object getValue(final IScope scope) {
				// tryToInit(scope);
				return getSeed();
			}
		};
		p.setDefined(GamaPreferences.CORE_SEED_DEFINED.getValue());
		params.add(p);
		return params;
	}

	protected String getExperimentParametersCategory() {
		return IExperimentPlan.SYSTEM_CATEGORY_PREFIX;
	}

	@Override
	@getter (
			value = ExperimentAgent.MINIMUM_CYCLE_DURATION,
			initializer = true)
	public Double getMinimumDuration() {
		return currentMinimumDuration;
	}

	@Override
	@setter (ExperimentAgent.MINIMUM_CYCLE_DURATION)
	public void setMinimumDuration(final Double d) {
		// d is in seconds, but the slider expects milleseconds
		// System.out.println("Minimum duration set to " + d);
		setMinimumDurationExternal(d);
		if (initialMinimumDuration == null) {
			initialMinimumDuration = d;
		}
		scope.getGui().updateSpeedDisplay(currentMinimumDuration * 1000, false);
	}

	/**
	 * Called normally from UI directly. Does not notify the GUI.
	 * 
	 * @param d
	 */
	public void setMinimumDurationExternal(final Double d) {
		currentMinimumDuration = d;
	}

	public Double getInitialMinimumDuration() {
		return initialMinimumDuration;
	}

	@Override
	@getter (
			value = ExperimentAgent.MODEL_PATH,
			initializer = true)
	public String getWorkingPath() {
		if (ownModelPath == null) {
			ownModelPath = getModel().getWorkingPath() + "/";
		}
		return ownModelPath;
	}

	@Override
	public List<String> getWorkingPaths() {
		final List<String> result = new ArrayList<>();
		result.add(getWorkingPath());
		result.addAll(getModel().getImportedPaths());
		return result;
	}

	@setter (ExperimentAgent.MODEL_PATH)
	public void setWorkingPath(final String p) {
		if (p.endsWith("/")) {
			ownModelPath = p;
		} else {
			ownModelPath = p + "/";
		}
	}

	@getter (
			value = PlatformAgent.WORKSPACE_PATH,
			initializer = true)
	public String getWorkspacePath() {
		return GAMA.getPlatformAgent().getWorkspacePath();
	}

	@getter (PROJECT_PATH)
	public String getProjectPath() {
		return getModel().getProjectPath() + "/";
	}

	@action (
			name = "update_outputs",
			doc = { @doc ("Forces all outputs to refresh, optionally recomputing their values") },
			args = { @arg (
					name = "recompute",
					type = IType.BOOL,
					doc = { @doc ("Whether or not to force the outputs to make a computation step") }) })
	public Object updateDisplays(final IScope scope) {
		final Boolean force = scope.getBoolArg("recompute");
		if (force)
			getSpecies().recomputeAndRefreshAllOutputs();
		else
			getSpecies().refreshAllOutputs();
		return this;
	}

	@Override
	@getter (
			value = GAMA._WARNINGS,
			initializer = true)
	public Boolean getWarningsAsErrors() {
		return warningsAsErrors;
	}

	@setter (GAMA._WARNINGS)
	public void setWarningsAsErrors(final boolean t) {
		warningsAsErrors = t;
	}

	@getter (
			value = IKeyword.SEED,
			initializer = true)
	public Double getSeed() {
		final Double seed = random.getSeed();
		// System.out.println("experiment agent get seed: " + seed);
		return seed == null ? Double.valueOf(0d) : seed;
	}

	@setter (IKeyword.SEED)
	public void setSeed(final Double s) {
		// System.out.println("experiment agent set seed: " + s);
		Double seed;
		if (s == null) {
			seed = null;
		} else if (s.doubleValue() == 0d) {
			seed = null;
		} else {
			seed = s;
		}
		getRandomGenerator().setSeed(seed, true);
	}

	@getter (
			value = SimulationAgent.USAGE,
			initializer = false)
	public Integer getUsage() {
		final Integer usage = random.getUsage();
		return usage == null ? 0 : usage;
	}

	@setter (SimulationAgent.USAGE)
	public void setUsage(final Integer s) {
		Integer usage = s;
		if (s == null) {
			usage = 0;
		}
		getRandomGenerator().setUsage(usage);
	}

	@getter (
			value = IKeyword.RNG,
			initializer = true)
	public String getRng() {
		return getRandomGenerator().getRngName();
	}

	@setter (IKeyword.RNG)
	public void setRng(final String newRng) {
		getRandomGenerator().setGenerator(newRng, true);
	}

	@Override
	public SimulationPopulation getSimulationPopulation() {
		// Lazy initialization of the population, in case
		// createSimulationPopulation();
		return (SimulationPopulation) getMicroPopulation(getModel());
	}

	@getter (IKeyword.SIMULATIONS)
	public IList<? extends IAgent> getSimulations() {
		return getSimulationPopulation().copy(scope);
	}

	@setter (IKeyword.SIMULATIONS)
	public void setSimulations(final IList<IAgent> simulations) {
		// Forbidden
	}

	@Override
	@getter (IKeyword.SIMULATION)
	public SimulationAgent getSimulation() {
		if (getSimulationPopulation() != null)
			return getSimulationPopulation().lastSimulationCreated();
		return null;
	}

	@setter (IKeyword.SIMULATION)
	public void setSimulation(final IAgent sim) {}

	@Override
	public boolean isOnUserHold() {
		return isOnUserHold;
	}

	@Override
	public void setOnUserHold(final boolean state) {
		isOnUserHold = state;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final ISpecies species) {
		if (species == getModel()) { return getSimulationPopulation(); }
		return this.getSimulation().getPopulationFor(species.getName());

	}

	@Override
	protected boolean preStep(final IScope scope) {
		clock.beginCycle();
		executer.executeBeginActions();
		return super.preStep(scope);
	}

	@Override
	protected void postStep(final IScope scope) {
		super.postStep(scope);
		executer.executeEndActions();
		executer.executeOneShotActions();
		final IOutputManager outputs = getOutputManager();
		if (outputs != null) {
			outputs.step(scope);
		}
		clock.step(this.scope);
		informStatus();
	}

	@Override
	public void informStatus() {
		if (!getSpecies().isBatch() && getSimulation() != null) {
			getScope().getGui().getStatus().informStatus(null, "status.clock");
		}
	}

	public boolean backward(final IScope scope) {
		throw new NullPointerException();
	}

	/**
	 *
	 * The class ExperimentAgentScope. A "pass through" class used when the simulation is not yet computed and when an
	 * experiment tries to have access to simulation attributes (which is the case in most of the models). It returns
	 * and sets the values of parameters when they are defined in the experiment, and also allows extra parameters to be
	 * set. TODO Allow this class to read the init values of global variables that are not defined as parameters.
	 *
	 * @author drogoul
	 * @since 22 avr. 2013
	 *
	 */
	public class ExperimentAgentScope extends ExecutionScope {

		volatile boolean interrupted = false;

		@Override
		protected boolean _root_interrupted() {
			return interrupted || ExperimentAgent.this.dead();
		}

		@Override
		public void setInterrupted() {
			this.interrupted = true;
		}

		/**
		 * Method getRandom()
		 * 
		 * @see msi.gama.runtime.IScope#getRandom()
		 */
		@Override
		public RandomUtils getRandom() {
			return ExperimentAgent.this.random;
		}

		/**
		 * @param agent
		 */
		public ExperimentAgentScope() {
			super(ExperimentAgent.this);
		}

		public ExperimentAgentScope(final String name) {
			super(ExperimentAgent.this, name);
		}

		@Override
		public IScope copy(final String additionalName) {
			return new ExperimentAgentScope(additionalName);
		}

		@Override
		public SimulationAgent getSimulation() {
			return ExperimentAgent.this.getSimulation();
		}

		@Override
		public IExperimentAgent getExperiment() {
			return ExperimentAgent.this;
		}

		@Override
		public Object getGlobalVarValue(final String name) {
			if (ExperimentAgent.this.hasAttribute(name) || getSpecies().hasVar(name)) {
				return super.getGlobalVarValue(name);
			} else if (getSimulation() != null && !getSimulation().dead()) {
				return getSimulation().getScope().getGlobalVarValue(name);
			} else if (getSpecies()
					.hasParameter(name)) { return getSpecies().getExperimentScope().getGlobalVarValue(name); }
			return extraParametersMap.get(name);
		}

		@Override
		public void setGlobalVarValue(final String name, final Object v) {
			if (getSpecies().hasVar(name)) {
				super.setGlobalVarValue(name, v);
			} else if (getSimulation() != null && !getSimulation().dead()
					&& getSimulation().getSpecies().hasVar(name)) {
				getSimulation().getScope().setGlobalVarValue(name, v);
			} else {
				extraParametersMap.put(name, v);
			}
		}

		@Override
		public void setAgentVarValue(final IAgent a, final String name, final Object value) {
			if (a == ExperimentAgent.this) {
				setGlobalVarValue(name, value);
			} else {
				super.setAgentVarValue(a, name, value);
			}
		}

		@Override
		public Object getAgentVarValue(final IAgent a, final String name) {
			if (a == ExperimentAgent.this) { return getGlobalVarValue(name); }
			return super.getAgentVarValue(a, name);
		}

		@Override
		public IGui getGui() {
			if (getSpecies().isHeadless()) { return GAMA.getHeadlessGui(); }
			return GAMA.getRegularGui();
		}

	}

	/**
	 * @return
	 */
	public Iterable<IOutputManager> getAllSimulationOutputs() {
		return Iterables.concat(
				Iterables.filter(Iterables.transform(getSimulationPopulation(), each -> each.getOutputManager()),
						ContainerHelper.NOT_NULL),
				Collections.singletonList(getOutputManager()));
	}

	/**
	 * @return
	 */
	public boolean isScheduled() {
		return scheduled;
	}

	/**
	 * Method closeSimulation()
	 * 
	 * @see msi.gama.kernel.experiment.IExperimentAgent#closeSimulation(msi.gama.kernel.simulation.SimulationAgent)
	 */
	@Override
	public void closeSimulation(final SimulationAgent simulationAgent) {
		simulationAgent.dispose();
	}

	@Override
	public GamaColor getColor() {
		return new GamaColor(0, 0, 0, 0);
	}

	/**
	 * Method getOutputManager()
	 * 
	 * @see msi.gama.kernel.experiment.ITopLevelAgent#getOutputManager()
	 */
	@Override
	public IOutputManager getOutputManager() {
		return getSpecies().getExperimentOutputs();
	}

	@Override
	public void postEndAction(final IExecutable executable) {
		executer.insertEndAction(executable);

	}

	@Override
	public void postDisposeAction(final IExecutable executable) {
		executer.insertDisposeAction(executable);

	}

	@Override
	public void postOneShotAction(final IExecutable executable) {
		executer.insertOneShotAction(executable);

	}

	@Override
	public void executeAction(final IExecutable executable) {
		executer.executeOneAction(executable);

	}

	@Override
	public boolean isMemorize() {
		return false;
	}

	@Override
	public boolean canStepBack() {
		return false;
	}

}