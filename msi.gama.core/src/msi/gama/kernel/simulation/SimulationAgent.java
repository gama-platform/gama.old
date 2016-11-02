/*********************************************************************************************
 *
 * 'SimulationAgent.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.kernel.simulation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ActionExecuter;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.continuous.RootTopology;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.metamodel.topology.projection.WorldProjection;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.IOutputManager;
import msi.gama.outputs.SimulationOutputManager;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
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
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;

/**
 * Defines an instance of a model (a simulation). Serves as the support for model species (whose metaclass is
 * GamlModelSpecies) Written by drogoul Modified on 1 d�c. 2010, May 2013
 *
 * @todo Description
 *
 */
@species (
		name = IKeyword.MODEL)
@vars ({ @var (
		name = IKeyword.COLOR,
		type = IType.COLOR,
		doc = @doc (
				value = "The color used to identify this simulation in the UI",
				comment = "Can be set freely by the modeler")),
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
				name = IKeyword.EXPERIMENT,
				type = ITypeProvider.EXPERIMENT_TYPE,
				doc = { @doc ("Returns the current experiment agent") }),
		@var (
				name = IKeyword.WORLD_AGENT_NAME,
				type = ITypeProvider.MODEL_TYPE,
				doc = @doc ("Represents the 'world' of the agents, i.e. the instance of the model in which they are instantiated. Equivalent to 'simulation' in experiments")),
		@var (
				name = IKeyword.STEP,
				type = IType.FLOAT,
				doc = @doc (
						value = "Represents the value of the interval, in model time, between two simulation cycles",
						comment = "If not set, its value is equal to 1.0 and, since the default time unit is the second, to 1 second")),
		@var (
				name = SimulationAgent.TIME,
				type = IType.FLOAT,
				doc = @doc (
						value = "Represents the total time passed, in model time, since the beginning of the simulation",
						comment = "Equal to cycle * step if the user does not arbitrarily initialize it.")),
		@var (
				name = SimulationAgent.CYCLE,
				type = IType.INT,
				doc = @doc ("Returns the current cycle of the simulation")),
		@var (
				name = SimulationAgent.USAGE,
				type = IType.INT,
				doc = @doc ("Returns the number of times the random number generator of the simulation has been drawn")),
		@var (
				name = SimulationAgent.PAUSED,
				type = IType.BOOL,
				doc = @doc ("Returns the current pausing state of the simulation")),
		@var (
				name = SimulationAgent.DURATION,
				type = IType.STRING,
				doc = @doc ("Returns a string containing the duration, in milliseconds, of the previous simulation cycle")),
		@var (
				name = SimulationAgent.TOTAL_DURATION,
				type = IType.STRING,
				doc = @doc ("Returns a string containing the total duration, in milliseconds, of the simulation since it has been launched ")),
		@var (
				name = SimulationAgent.AVERAGE_DURATION,
				type = IType.STRING,
				doc = @doc ("Returns a string containing the average duration, in milliseconds, of a simulation cycle.")),
		@var (
				name = SimulationAgent.MACHINE_TIME,
				type = IType.FLOAT,
				doc = @doc (
						value = "Returns the current system time in milliseconds",
						comment = "The return value is a float number")),
		@var (
				name = SimulationAgent.CURRENT_DATE,
				depends_on = SimulationAgent.STARTING_DATE,
				type = IType.DATE,
				doc = @doc (
						value = "Returns the current date in the simulation",
						comment = "The return value is a date; the starting_date have to be initialized to use this attribute, which otherwise indicates a pseudo-date")),
		@var (
				name = SimulationAgent.STARTING_DATE,
				type = IType.DATE,
				doc = @doc (
						value = "Represents the starting date of the simulation",
						comment = "If no starting_date is provided in the model, GAMA initializes it with a zero date: 1st of January, 0000 at 00:00:00")), })
public class SimulationAgent extends GamlAgent implements ITopLevelAgent {

	public static final String DURATION = "duration";
	public static final String MACHINE_TIME = "machine_time";
	public static final String TOTAL_DURATION = "total_duration";
	public static final String AVERAGE_DURATION = "average_duration";
	public static final String CYCLE = "cycle";
	public static final String TIME = "time";
	public static final String CURRENT_DATE = "current_date";
	public static final String STARTING_DATE = "starting_date";
	public static final String PAUSED = "paused";
	public static final String USAGE = "rng_usage";

	final SimulationClock clock;
	GamaColor color;

	final IScope scope = new ExecutionScope(this);
	private SimulationOutputManager outputs;
	final ProjectionFactory projectionFactory;
	private Boolean scheduled = false;
	private volatile boolean isOnUserHold;
	private final RandomUtils random;
	private final ActionExecuter executer;
	private RootTopology topology;
	// Added here to be sure to

	public SimulationAgent(final IPopulation<? extends IAgent> pop) {
		this((SimulationPopulation) pop);
	}

	public SimulationAgent(final SimulationPopulation pop) throws GamaRuntimeException {
		super(pop);
		// scope = new SimulationScope();
		clock = new SimulationClock(getScope());
		executer = new ActionExecuter(getScope());
		projectionFactory = new ProjectionFactory();
		random = new RandomUtils(pop.getHost().getSeed(), pop.getHost().getRng());
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	@Override
	@getter (IKeyword.EXPERIMENT)
	public IExperimentAgent getExperiment() {
		final IMacroAgent agent = getHost();
		if (agent instanceof IExperimentAgent)
			return (IExperimentAgent) agent;
		return null;
	}

	@Override
	@getter (IKeyword.WORLD_AGENT_NAME)
	public SimulationAgent getSimulation() {
		return this;
	}

	public void setTopology(final IScope scope, final IShape shape) {
		// A topology has already been computed. We update it and updates all
		// the agents present in the spatial index
		if (topology != null) {
			topology.updateEnvironment(shape);
		} else {
			final IExpression expr = getSpecies().getFacet(IKeyword.TORUS);
			final boolean torus = expr == null ? false : Cast.as(expr.value(scope), Boolean.class, false);
			topology = new RootTopology(scope, shape, torus);
		}
	}

	@Override
	public void setName(final String name) {
		super.setName(name);
		final SimulationOutputManager m = getOutputManager();
		if (m != null)
			m.updateDisplayOutputsName(this);
	}

	public void setScheduled(final Boolean scheduled) {
		this.scheduled = scheduled;
	}

	@Override
	@getter (
			value = IKeyword.COLOR,
			initializer = true)
	public GamaColor getColor() {
		if (color == null) {
			color = new GamaColor(GamaPreferences.SIMULATION_COLORS[getIndex() % 5].getValue());
		}
		return color;
	}

	@Override
	public ITopology getTopology() {
		return topology;
	}

	@setter (IKeyword.COLOR)
	public void setColor(final GamaColor color) {
		this.color = color;
	}

	@Override
	public void schedule(final IScope scope) {
		super.schedule(this.getScope());
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
		if (outputs != null) {
			outputs.step(this.getScope());
		}
		clock.step(this.getScope());
	}

	@Override
	public Object _init_(final IScope scope) {
		super._init_(this.getScope());
		if (outputs != null) {
			outputs.init(this.getScope());
		}
		return this;
	}

	/**
	 * SimulationScope related utilities
	 *
	 */

	@Override
	public IScope getScope() {
		return scope;
	}

	public ProjectionFactory getProjectionFactory() {
		return projectionFactory;
	}

	@Override
	public SimulationClock getClock() {
		return clock;
	}

	@Override
	public void dispose() {
		if (dead) { return; }
		executer.executeDisposeActions();
		// hqnghi if simulation come from popultion extern, dispose pop first
		// and then their outputs

		if (externMicroPopulations != null)
			externMicroPopulations.clear();

		if (outputs != null) {
			outputs.dispose();
			outputs = null;
		}
		if (topology != null) {
			topology.dispose();
			topology = null;
		}

		GAMA.releaseScope(getScope());
		// scope = null;
		super.dispose();

	}

	@Override
	public void setLocation(final ILocation newGlobalLoc) {}

	@Override
	public ILocation getLocation() {
		if (geometry == null || geometry.getInnerGeometry() == null) { return new GamaPoint(0, 0); }
		return super.getLocation();
	}

	@Override
	public void setGeometry(final IShape g) {
		// FIXME : AD 5/15 Revert the commit by PT:
		// getProjectionFactory().setWorldProjectionEnv(geom.getEnvelope());
		// We systematically translate the geometry to {0,0}
		IShape geom = g;
		if (geom == null)
			geom = GamaGeometryType.buildBox(100, 100, 100, new GamaPoint(50, 50, 50));
		final Envelope3D env = geom.getEnvelope();
		if (getProjectionFactory().getWorld() != null) {
			((WorldProjection) getProjectionFactory().getWorld()).updateTranslations(env);
		}
		final GamaPoint p = new GamaPoint(-env.getMinX(), -env.getMinY(), -env.getMinZ());
		geometry.setGeometry(Transformations.translated_by(getScope(), geom, p));
		setTopology(getScope(), geometry);

	}

	@Override
	public SimulationPopulation getPopulation() {
		return (SimulationPopulation) population;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final String speciesName) throws GamaRuntimeException {
		IPopulation<? extends IAgent> pop = super.getPopulationFor(speciesName);
		if (pop != null) { return pop; }
		final ISpecies microSpec = getSpecies().getMicroSpecies(speciesName);
		if (microSpec == null) { return null; }
		pop = GamaPopulation.createPopulation(getScope(), this, microSpec);
		setAttribute(microSpec, pop);
		pop.initializeFor(getScope());
		return pop;
	}

	@getter (CYCLE)
	public Integer getCycle(final IScope scope) {
		final SimulationClock clock = getClock();
		if (clock != null) { return clock.getCycle(); }
		return 0;
	}

	@getter (PAUSED)
	public boolean isPaused(final IScope scope) {
		// The second case is mostly useless for the moment as it corresponds
		// to the global pause of the experiment...
		return getScope().isPaused();
	}

	@setter (PAUSED)
	public void setPaused(final IScope scope, final boolean state) {
		// Not used for the moment, but it might allow to set this state
		// explicitly (ie pause a simulation without pausing the experiment)
		// For that, however, we need to check the condition in the step of the
		// simulation and maybe skip it (or put the thread on sleep ?) when its
		// scope is on user hold... The question of what to do with the
		// experiment
		// is also important: should the experiment continue stepping while its
		// simulations are paused ?
		// getScope().setOnUserHold(state);
	}

	@Override
	public boolean isOnUserHold() {
		return isOnUserHold;
	}

	@Override
	public void setOnUserHold(final boolean state) {
		isOnUserHold = state;
	}

	@getter (
			value = IKeyword.STEP,
			initializer = true)
	public double getTimeStep(final IScope scope) {
		final SimulationClock clock = getClock();
		if (clock != null) { return clock.getStepInSeconds(); }
		return 1d;
	}

	@setter (IKeyword.STEP)
	public void setTimeStep(final IScope scope, final double t) throws GamaRuntimeException {
		// getClock().setStep(getSpecies().getVar(IKeyword.STEP).);
		final SimulationClock clock = getClock();
		if (clock != null) {
			clock.setStep(t);

		}
	}

	@getter (TIME)
	public double getTime(final IScope scope) {
		final SimulationClock clock = getClock();
		if (clock != null) { return clock.getTimeElapsedInSeconds(); }
		return 0d;
	}

	@setter (TIME)
	public void setTime(final IScope scope, final double t) throws GamaRuntimeException {

		// final SimulationClock clock = getClock();
		// if (clock != null) {
		// clock.setTime(t);
		// }
	}

	@getter (DURATION)
	public String getDuration() {
		return Long.toString(getClock().getDuration());
	}

	@getter (TOTAL_DURATION)
	public String getTotalDuration() {
		return Long.toString(getClock().getTotalDuration());
	}

	@getter (AVERAGE_DURATION)
	public String getAverageDuration() {
		return Double.toString(getClock().getAverageDuration());
	}

	@getter (MACHINE_TIME)
	public Double getMachineTime() {
		return (double) System.currentTimeMillis();
	}

	@setter (MACHINE_TIME)
	public void setMachineTime(final Double t) throws GamaRuntimeException {
		// NOTHING
	}

	@setter (CURRENT_DATE)
	public void setCurrentDate(final GamaDate d) throws GamaRuntimeException {
		// NOTHING
	}

	@getter (CURRENT_DATE)
	public GamaDate getCurrentDate() {
		return clock.getCurrentDate();
	}

	@setter (STARTING_DATE)
	public void setStartingDate(final GamaDate d) throws GamaRuntimeException {
		clock.setStartingDate(d);
	}

	@getter (
			value = STARTING_DATE,
			initializer = true)
	public GamaDate getStartingDate() {
		return clock.getStartingDate();
	}

	@action (
			name = "pause",
			doc = @doc ("Allows to pause the current simulation **ACTUALLY EXPERIMENT FOR THE MOMENT**. It can be set to continue with the manual intervention of the user."))
	@args (
			names = {})
	public Object pause(final IScope scope) {
		final IExperimentController controller = scope.getExperiment().getSpecies().getController();
		controller.directPause();
		return null;
	}

	@action (
			name = "halt",
			doc = @doc (
					deprecated = "It is preferable to use 'die' instead to kill a simulation, or 'pause' to stop it temporarily",
					value = "Allows to stop the current simulation so that cannot be continued after. All the behaviors and updates are stopped. "))
	@args (
			names = {})
	public Object halt(final IScope scope) {
		getExperiment().closeSimulation(this);
		return null;
	}

	public String getShortUserFriendlyName() {

		return getName();

	}

	public String buildPostfix() {
		final boolean noName = !GamaPreferences.CORE_SIMULATION_NAME.getValue();
		if (noName) {
			if (getPopulation().size() > 1) {
				return " (S" + getIndex() + ")";
			} else {
				return "";
			}
		} else {
			return " (" + getName() + ")";
		}

	}

	public void setOutputs(final IOutputManager iOutputManager) {
		if (iOutputManager == null) { return; }
		// hqnghi push outputManager down to Simulation level
		// create a copy of outputs from description
		if ( /* !scheduled && */ !getExperiment().getSpecies().isBatch()) {
			final IDescription des = ((ISymbol) iOutputManager).getDescription();
			if (des == null) { return; }
			outputs = (SimulationOutputManager) des.compile();
			final Map<String, IOutput> mm = new TOrderedHashMap<>();
			for (final Map.Entry<String, ? extends IOutput> entry : outputs.getOutputs().entrySet()) {
				final IOutput output = entry.getValue();
				String keyName, newOutputName;
				if (!scheduled) {
					keyName = output.getName() + "#"
							+ this.getSpecies().getDescription().getModelDescription().getAlias() + "#"
							+ this.getExperiment().getSpecies().getName() + "#" + this.getExperiment().getIndex();
					newOutputName = keyName;
				} else {
					final String postfix = buildPostfix();
					keyName = entry.getKey() + postfix;
					newOutputName = output.getName() + postfix;
				}
				mm.put(keyName, output);
				output.setName(newOutputName);
			}
			outputs.clear();
			outputs.putAll(mm);
		} else {
			outputs = (SimulationOutputManager) iOutputManager;
		}
		// end-hqnghi
	}

	@Override
	public SimulationOutputManager getOutputManager() {
		return outputs;
	}

	/**
	 * @param inspectDisplayOutput
	 */
	public void addOutput(final IOutput output) {
		outputs.add(output);
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
			value = IKeyword.SEED,
			initializer = true)
	public Double getSeed() {
		final Double seed = random.getSeed();
		// System.out.println("simulation agent get seed: " + seed);
		return seed == null ? Double.valueOf(0d) : seed;
	}

	@setter (IKeyword.SEED)
	public void setSeed(final Double s) {
		// System.out.println("simulation agent set seed: " + s);
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
			value = IKeyword.RNG,
			initializer = true)
	public String getRng() {
		return getRandomGenerator().getRngName();
	}

	@setter (IKeyword.RNG)
	public void setRng(final String newRng) {

		// rng = newRng;
		// scope.getGui().debug("ExperimentAgent.setRng" + newRng);
		getRandomGenerator().setGenerator(newRng, true);
	}

	// @Override
	@Override
	public RandomUtils getRandomGenerator() {
		return random;
	}

	public void prepareGuiForSimulation(final IScope s) {
		s.getGui().clearErrors();
	}

	public void initOutputs() {
		if (outputs != null) {
			outputs.init(this.getScope());
		}
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
	public void updateWith(final IScope scope, final SavedAgent sa) {
		// Update Clock
		final Object cycle = sa.getAttributeValue("cycle");
		clock.setCycle((Integer) cycle);

		// Update Attribute
		final Map<String, Object> attr = sa.getVariables();
		for (final String name : attr.keySet()) {
			this.setDirectVarValue(scope, name, attr.get(name));
			// this.setAttribute(name, attr.get(name));
		}

		// TODO
		// Update GUI of the Experiment
		// this.getExperiment().

		// Update innerPopulations
		// TODO tout mettre dans une methode :
		// Add a boolean to this one :
		// public void restoreMicroAgents(final IScope scope, final IAgent host)
		// throws GamaRuntimeException {

		final Map<String, List<SavedAgent>> savedAgentInnerPop = sa.getInnerPopulations();

		if (savedAgentInnerPop != null) {
			for (final String savedAgentMicroPopName : savedAgentInnerPop.keySet()) {
				final IPopulation<? extends IAgent> simuMicroPop = getMicroPopulation(savedAgentMicroPopName);

				if (simuMicroPop != null) {
					// Build a map name::innerPopAgentSavedAgt :
					// For each agent from the simulation innerPop, it will be
					// updated from the corresponding agent
					final Map<String, SavedAgent> mapSavedAgtName = new THashMap<>();
					for (final SavedAgent localSA : savedAgentInnerPop.get(savedAgentMicroPopName)) {
						mapSavedAgtName.put((String) localSA.getAttributeValue("name"), localSA);
					}

					final Map<String, IAgent> mapSimuAgtName = new THashMap<>();

					// IAgent[] microPopArray = simuMicroPop.toArray();

					for (final IAgent agt : simuMicroPop.toArray()) {
						mapSimuAgtName.put(agt.getName(), agt);
					}

					// for(int i = 0 ; i < simuMicroPop.size() ; i ++){
					// IAgent agt = simuMicroPop.getAgent(i);
					// String t = agt.getName();
					// mapSimuAgtName.put((String)
					// (simuMicroPop.getAgent(i).getName()),
					// simuMicroPop.getAgent(i));
					// }

					for (final Entry<String, SavedAgent> e : mapSavedAgtName.entrySet()) {
						final IAgent agt = mapSimuAgtName.get(e.getKey());
						if (agt != null) { // the savedAgent is in the
											// simulation, update it, and remove
											// it from the map mapSimuAgtName
							// TODO : implement it for GamlAgent...
							agt.updateWith(scope, e.getValue());
							mapSimuAgtName.remove(e.getKey());
						} else { // the SavedAgent is not in the Simulation,
									// then create it
							// List<Map<String,Object>> initialValues = new
							// ArrayList<>();
							// initialValues.add(e.getValue().getVariables());
							// simuMicroPop.createAgents(s, 1, initialValues,
							// true, true);
							simuMicroPop.createAgentAt(scope, e.getValue().getIndex(), e.getValue().getVariables(),
									true, true);
						}
					}

					// For all remaining agents in the mapSimuAgtName, kill them
					for (final IAgent remainingAgent : mapSimuAgtName.values()) {
						// Kill them all
						remainingAgent.dispose();
						// simuMicroPop.killMembers();
						// microPop.clear();
						// microPop.firePopulationCleared();
					}
				}
			}
		}

	}

}
