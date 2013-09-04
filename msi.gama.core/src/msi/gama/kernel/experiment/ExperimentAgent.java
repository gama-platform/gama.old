package msi.gama.kernel.experiment;

import java.net.URL;
import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.kernel.simulation.SimulationClock.ExperimentClock;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.IOutputManager;
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
	// @var(name = GAMA._FATAL, type = IType.BOOL),
	@var(name = GAMA._WARNINGS, type = IType.BOOL),
	@var(name = ExperimentAgent.MODEL_PATH, type = IType.STRING, constant = true, doc = @doc(value = "Contains the absolute path to the folder in which the current model is located", comment = "Always terminated with a trailing separator")),
	@var(name = IKeyword.SEED, type = IType.FLOAT, doc = @doc(value = "The seed of the random number generator", comment = "Each time it is set, the random number generator is reinitialized")),
	@var(name = IKeyword.RNG, type = IType.STRING, doc = @doc("The random number generator to use for this simulation. Four different ones are at the disposal of the modeler: " +
		IKeyword.MERSENNE +
		" represents the default generator, based on the Mersenne-Twister algorithm. Very reliable; " +
		IKeyword.CELLULAR +
		" is a cellular automaton based generator that should be a bit faster, but less reliable; " +
		IKeyword.XOR +
		" is another choice. Much faster than the previous ones, but with short sequences; and " +
		IKeyword.JAVA +
		" invokes the standard Java generator")),

	@var(name = ExperimentAgent.WORKSPACE_PATH, type = IType.STRING, constant = true, doc = @doc(value = "Contains the absolute path to the workspace of GAMA", comment = "Always terminated with a trailing separator")),
	@var(name = ExperimentAgent.PROJECT_PATH, type = IType.STRING, constant = true, doc = @doc(value = "Contains the absolute path to the project in which the current model is located", comment = "Always terminated with a trailing separator")) })
public class ExperimentAgent extends GamlAgent implements IExperimentAgent {

	public static final String MODEL_PATH = "model_path";
	public static final String WORKSPACE_PATH = "workspace_path";
	public static final String PROJECT_PATH = "project_path";

	private static final IShape SHAPE = GamaGeometryType.createPoint(new GamaPoint(-1, -1));

	private final IScope scope;
	protected SimulationAgent simulation;
	final Map<String, Object> extraParametersMap = new LinkedHashMap();
	protected RandomUtils random;
	protected Double seed = GamaPreferences.CORE_SEED_DEFINED.getValue() ? GamaPreferences.CORE_SEED.getValue()
		: (Double) null;
	// protected boolean isLoading;
	protected ExperimentClock clock = new ExperimentClock();
	// protected boolean revealAndStop = GamaPreferences.CORE_REVEAL_AND_STOP.getValue();
	protected boolean warningsAsErrors = GamaPreferences.CORE_WARNINGS.getValue();

	public ExperimentAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
		super.setGeometry(SHAPE);
		scope = obtainNewScope();
		reset();
	}

	@Override
	public SimulationClock getClock() {
		return clock;
	}

	public void reset() {
		// We close any simulation that might be running
		closeSimulation();
		// We create a fresh new scope
		// GAMA.releaseScope(scope);
		// scope = obtainNewScope();
		// We initialize the population that will host the simulation
		createSimulationPopulation();
		// We initialize a new random number generator
		// random = new RandomUtils(getSpecies().getCurrentSeed());
		random = new RandomUtils(seed, getRng());
	}

	@Override
	public IScope obtainNewScope() {
		if ( dead ) { return null; }
		return new ExperimentAgentScope();
	}

	@Override
	public void closeSimulation() {
		// We unschedule the simulation if any
		if ( getSimulation() != null ) {
			GAMA.controller.getScheduler().unschedule(getSimulation().getScheduler());
			// TODO Should better be in SimulationOutputManager
			if ( !getSpecies().isBatch() ) {
				GuiUtils.cleanAfterSimulation();
			}
		}
	}

	@Override
	public void dispose() {
		if ( dead ) { return; }
		super.dispose();
		// GAMA.releaseScope(scope);
		closeSimulation();
		if ( getSimulation() != null ) {
			getSimulation().dispose();
		}
	}

	// @Override
	// public boolean isLoading() {
	// return isLoading;
	// }

	/**
	 * Redefinition of the callback method
	 * @see msi.gama.metamodel.agent.GamlAgent#_init_(msi.gama.runtime.IScope)
	 */
	@Override
	public Object _init_(final IScope scope) {
		// GuiUtils.debug("ExperimentAgent._init_");
		if ( scope.interrupted() ) { return null; }
		// We execute any behavior defined in GAML. The simulation is not yet defined (only the 'fake' one).
		super._init_(scope);
		// We gather the parameters set in the experiment
		// final ParametersSet parameters = new ParametersSet(getSpecies().getCurrentSolution());
		// Add the ones set during the "fake" simulation episode
		// parameters.putAll(extraParametersMap);
		// This is where the simulation agent is created
		// isLoading = true;
		// final IPopulation pop = getMicroPopulation(getModel());
		// 'simulation' is set by a callback call to setSimulation()
		// pop.createAgents(scope, 1, GamaList.with(getParameterValues()), false);
		// isLoading = false;
		createSimulation(getParameterValues(), true);
		return this;
	}

	public SimulationAgent createSimulation(final ParametersSet parameters, final boolean scheduleIt) {
		final IPopulation pop = getMicroPopulation(getModel());
		// 'simulation' is set by a callback call to setSimulation()
		ParametersSet ps = getParameterValues();
		ps.putAll(parameters);
		return (SimulationAgent) pop.createAgents(scope, 1, GamaList.with(ps), scheduleIt).get(0);
	}

	public ParametersSet getParameterValues() {
		Map<String, IParameter> parameters = getSpecies().getParameters();
		ParametersSet ps = new ParametersSet(parameters, false);
		ps.putAll(extraParametersMap);
		return ps;
	}

	@Override
	protected Object stepSubPopulations(final IScope scope) {
		// The experiment DOES NOT step its subpopulations
		return this;
	}

	@Override
	public RandomUtils getRandomGenerator() {
		return random == null ? RandomUtils.getDefault() : random;
	}

	@Override
	public void schedule() {
		// The experiment agent is scheduled in the global scheduler
		IOutputManager outputs = getSpecies().getExperimentOutputs();
		if ( outputs != null ) {
			GAMA.controller.getScheduler().schedule(outputs, getScope());
		}
		GAMA.controller.getScheduler().schedule(this, getScope());

	}

	/**
	 * Building the simulation agent and its population
	 */

	protected void createSimulationPopulation() {
		SimulationPopulation pop = getSimulationPopulation();
		if ( pop != null ) {
			pop.dispose();
		}
		pop = new SimulationPopulation(getModel());
		pop.initializeFor(scope);
		attributes.put(getModel().getName(), pop);
		pop.setHost(this);
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

	public List<? extends IParameter.Batch> getDefaultParameters() {
		if ( !GamaPreferences.CORE_RND_EDITABLE.getValue() ) { return Collections.EMPTY_LIST; }
		List<ExperimentParameter> params = new ArrayList();
		final String cat = getExperimentParametersCategory();
		params.add(new ExperimentParameter(getScope(), getSpecies().getVar(IKeyword.RNG), "Random number generator",
			cat, GamaPreferences.GENERATOR_NAMES, false));
		params.add(new ExperimentParameter(getScope(), getSpecies().getVar(IKeyword.SEED), "Random seed", cat, null,
			true));
		return params;
	}

	protected String getExperimentParametersCategory() {
		return "Model " + getModel().getName() + ItemList.SEPARATION_CODE + ItemList.INFO_CODE +
			IExperimentSpecies.SYSTEM_CATEGORY_PREFIX + " '" + getSpecies().getName() + "'";
	}

	@getter(ExperimentAgent.MODEL_PATH)
	public String getModelPath() {
		return getModel().getFolderPath() + "/";
	}

	@getter(value = WORKSPACE_PATH, initializer = true)
	public String getWorkspacePath() {
		final URL url = Platform.getInstanceLocation().getURL();
		return url.getPath();
	}

	@getter(PROJECT_PATH)
	public String getProjectPath() {
		return getModel().getProjectPath() + "/";
	}

	// @getter(value = GAMA._FATAL, initializer = true)
	// public Boolean getFatalErrors() {
	// return revealAndStop;
	// }

	@Override
	@getter(value = GAMA._WARNINGS, initializer = true)
	public Boolean getWarningsAsErrors() {
		return warningsAsErrors;
	}

	@setter(GAMA._WARNINGS)
	public void setWarningsAsErrors(final boolean t) {
		warningsAsErrors = t;
	}

	@getter(value = IKeyword.SEED, initializer = true)
	public Double getSeed() {
		return seed == null ? 0d : seed;
	}

	@setter(IKeyword.SEED)
	public void setSeed(final Double s) {
		seed = s == 0d ? null : s;
		getRandomGenerator().setSeed(seed);
	}

	@getter(value = IKeyword.RNG, initializer = true)
	public String getRng() {
		return getRandomGenerator().getGeneratorName();
	}

	@setter(IKeyword.RNG)
	public void setRng(final String newRng) {
		// GuiUtils.debug("ExperimentAgent.setRng" + newRng);
		getRandomGenerator().setGenerator(newRng);
	}

	private SimulationPopulation getSimulationPopulation() {
		return (SimulationPopulation) getMicroPopulation(getModel());
	}

	@getter(IKeyword.SIMULATION)
	public IAgent getSimulation() {
		// if ( simulation == null || simulation.getScope().interrupted() ) {
		// SimulationPopulation pop = getSimulationPopulation();
		// if ( pop != null ) {
		// setSimulation(pop.last(scope));
		// }
		// }
		return simulation;
	}

	@setter(IKeyword.SIMULATION)
	public void setSimulation(final IAgent sim) {
		if ( sim instanceof SimulationAgent ) {
			if ( simulation != null && simulation.getScope().interrupted() ) {
				simulation.dispose();
			}
			simulation = (SimulationAgent) sim;
			simulation.setOutputs(getSpecies().getSimulationOutputs());
		}
	}

	@Override
	public boolean step(final IScope scope) {
		clock.beginCycle();
		boolean result;
		// A simulation always runs in its own scope
		try {
			result = super.step(this.scope);
		} finally {
			clock.step();
		}
		return result;
	}

	// TODO A redefinition of this method in GAML will lose all information regarding the clock and the advance of time,
	// which will have to be done manually (i.e. cycle <- cycle + 1; time <- time + step;)
	// @Override
	// public Object _step_(final IScope scope) {
	// clock.beginCycle();
	// // A simulation always runs in its own scope
	// try {
	// super._step_(this.scope);
	// } finally {
	// clock.step();
	// }
	// return this;
	// }

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
		public IScope copy() {
			return new ExperimentAgentScope();
		}

		@Override
		public Object getGlobalVarValue(final String name) {
			if ( ExperimentAgent.this.hasAttribute(name) ) {
				return super.getGlobalVarValue(name);
			} else if ( getSimulation() != null ) {
				return getSimulation().getScope().getGlobalVarValue(name);
			} else if ( getSpecies().hasParameter(name) ) { return getSpecies().getExperimentScope().getGlobalVarValue(
				name); }
			return extraParametersMap.get(name);
		}

		@Override
		public void setGlobalVarValue(final String name, final Object v) {
			// if ( name.equals(IKeyword.SEED) ) {
			// GuiUtils.debug("ExperimentAgent.ExperimentAgentScope.setGlobalVarValue");
			// }
			if ( getSpecies().hasVar(name) ) {
				super.setGlobalVarValue(name, v);
			} else if ( getSimulation() != null && getSimulation().getSpecies().hasVar(name) ) {
				getSimulation().getScope().setGlobalVarValue(name, v);
			} else if ( getSpecies().hasParameter(name) ) {
				getSpecies().getExperimentScope().setGlobalVarValue(name, v);// GuiUtils.updateParameterView(getSpecies());
			} else {
				extraParametersMap.put(name, v);
			}
		}

		@Override
		public void setAgentVarValue(final IAgent a, final String name, final Object value) {
			if ( a == ExperimentAgent.this ) {
				setGlobalVarValue(name, value);
			} else {
				super.setAgentVarValue(a, name, value);
			}
		}

		@Override
		public Object getAgentVarValue(final IAgent a, final String name) {
			if ( a == ExperimentAgent.this ) { return getGlobalVarValue(name); }
			return super.getAgentVarValue(a, name);
		}

	}

}