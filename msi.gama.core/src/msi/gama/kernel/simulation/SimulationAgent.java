/*******************************************************************************************************
 *
 * msi.gama.kernel.simulation.SimulationAgent.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.simulation;

import static msi.gama.runtime.concurrent.GamaExecutorService.getParallelism;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ActionExecuter;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.continuous.RootTopology;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.metamodel.topology.projection.WorldProjection;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.IOutputManager;
import msi.gama.outputs.SimulationOutputManager;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.concurrent.GamaExecutorService.Caller;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IReference;
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
		name = IKeyword.MODEL,
		internal = true)
@vars ({ @variable (
		name = IKeyword.COLOR,
		type = IType.COLOR,
		doc = @doc (
				value = "The color used to identify this simulation in the UI",
				comment = "Can be set freely by the modeler")),
		@variable (
				name = IKeyword.SEED,
				type = IType.FLOAT,
				doc = @doc (
						value = "The seed of the random number generator",
						comment = "Each time it is set, the random number generator is reinitialized")),
		@variable (
				name = IKeyword.RNG,
				type = IType.STRING,
				doc = @doc ("The random number generator to use for this simulation. Three different ones are at the disposal of the modeler: "
						+ IKeyword.MERSENNE
						+ " represents the default generator, based on the Mersenne-Twister algorithm. Very reliable; "
						+ IKeyword.CELLULAR
						+ " is a cellular automaton based generator that should be a bit faster, but less reliable; and "
						+ IKeyword.JAVA + " invokes the standard Java generator")),
		@variable (
				name = IKeyword.EXPERIMENT,
				type = ITypeProvider.EXPERIMENT_TYPE,
				doc = { @doc ("Returns the current experiment agent") }),
		@variable (
				name = IKeyword.WORLD_AGENT_NAME,
				type = ITypeProvider.MODEL_TYPE,
				doc = @doc ("Represents the 'world' of the agents, i.e. the instance of the model in which they are instantiated. Equivalent to 'simulation' in experiments")),
		@variable (
				name = IKeyword.STEP,
				type = IType.FLOAT,
				doc = @doc (
						value = "Represents the value of the interval, in model time, between two simulation cycles",
						comment = "If not set, its value is equal to 1.0 and, since the default time unit is the second, to 1 second")),
		@variable (
				name = SimulationAgent.TIME,
				type = IType.FLOAT,
				doc = @doc (
						value = "Represents the total time passed, in model time, since the beginning of the simulation",
						comment = "Equal to cycle * step if the user does not arbitrarily initialize it.")),
		@variable (
				name = SimulationAgent.CYCLE,
				type = IType.INT,
				doc = @doc ("Returns the current cycle of the simulation")),
		@variable (
				name = SimulationAgent.USAGE,
				type = IType.INT,
				doc = @doc ("Returns the number of times the random number generator of the simulation has been drawn")),
		@variable (
				name = SimulationAgent.PAUSED,
				type = IType.BOOL,
				doc = @doc ("Returns the current pausing state of the simulation")),
		@variable (
				name = SimulationAgent.DURATION,
				type = IType.STRING,
				doc = @doc ("Returns a string containing the duration, in milliseconds, of the previous simulation cycle")),
		@variable (
				name = SimulationAgent.TOTAL_DURATION,
				type = IType.STRING,
				doc = @doc ("Returns a string containing the total duration, in milliseconds, of the simulation since it has been launched ")),
		@variable (
				name = SimulationAgent.AVERAGE_DURATION,
				type = IType.STRING,
				doc = @doc ("Returns a string containing the average duration, in milliseconds, of a simulation cycle.")),
		@variable (
				name = PlatformAgent.MACHINE_TIME,
				type = IType.FLOAT,
				doc = @doc (
						deprecated = "Use 'gama.machine_time' instead",
						value = "Returns the current system time in milliseconds",
						comment = "The return value is a float number")),
		@variable (
				name = SimulationAgent.CURRENT_DATE,
				depends_on = SimulationAgent.STARTING_DATE,
				type = IType.DATE,
				doc = @doc (
						value = "Returns the current date in the simulation",
						comment = "The return value is a date; the starting_date has to be initialized to use this attribute, which otherwise indicates a pseudo-date")),
		@variable (
				name = SimulationAgent.STARTING_DATE,
				type = IType.DATE,
				doc = @doc (
						value = "Represents the starting date of the simulation",
						comment = "If no starting_date is provided in the model, GAMA initializes it with a zero date: 1st of January, 1970 at 00:00:00")), })
public class SimulationAgent extends GamlAgent implements ITopLevelAgent {
	//
	// static {
	// DEBUG.ON();
	// }

	public static final String DURATION = "duration";
	public static final String TOTAL_DURATION = "total_duration";
	public static final String AVERAGE_DURATION = "average_duration";
	public static final String CYCLE = "cycle";
	public static final String TIME = "time";
	public static final String CURRENT_DATE = "current_date";
	public static final String STARTING_DATE = "starting_date";
	public static final String PAUSED = "paused";
	public static final String USAGE = "rng_usage";

	final SimulationClock ownClock;
	GamaColor color;

	final IScope ownScope = new ExecutionScope(this);
	private SimulationOutputManager outputs;
	final ProjectionFactory projectionFactory;
	private Boolean scheduled = false;
	private volatile boolean isOnUserHold;
	private RandomUtils random;
	private final ActionExecuter executer;
	private RootTopology topology;

	public SimulationAgent(final IPopulation<? extends IAgent> pop, final int index) {
		this((SimulationPopulation) pop, index);
	}

	public SimulationAgent(final SimulationPopulation pop, final int index) throws GamaRuntimeException {
		super(pop, index);
		ownClock = new SimulationClock(getScope());
		executer = new ActionExecuter(getScope());
		projectionFactory = new ProjectionFactory();
		// Random explicitely created with the seed of the experiment
		random = new RandomUtils(pop.getHost().getSeed(), pop.getHost().getRng());
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	@Override
	@getter (IKeyword.EXPERIMENT)
	public IExperimentAgent getExperiment() {
		final IMacroAgent agent = getHost();
		if (agent instanceof IExperimentAgent) return (IExperimentAgent) agent;
		return null;
	}

	@Override
	@getter (IKeyword.WORLD_AGENT_NAME)
	public SimulationAgent getSimulation() {
		return this;
	}

	public void setTopology(final RootTopology topology2) {
		if (topology != null) { topology.dispose(); }
		topology = topology2;

	}

	public void setTopology(final IScope scope, final IShape shape) {
		// A topology has already been computed. We update it and updates all
		// the agents present in the spatial index
		if (topology != null) {
			topology.updateEnvironment(shape);
		} else {
			final IExpression expr = getSpecies().getFacet(IKeyword.TORUS);
			final boolean torus = expr != null && Cast.asBool(scope, expr.value(scope));
			final boolean[] parallel = { GamaExecutorService.CONCURRENCY_SPECIES.getValue()
					|| GamaPreferences.External.QUADTREE_SYNCHRONIZATION.getValue() };
			if (!parallel[0]) {
				getSpecies().getDescription().visitMicroSpecies((s) -> {
					parallel[0] = getParallelism(scope, s.getFacetExpr(IKeyword.PARALLEL), Caller.SPECIES) > 0;
					return !parallel[0];
				});
			}
			setTopology(new RootTopology(scope, shape, torus, parallel[0]));
		}
	}

	@Override
	public void setName(final String name) {
		super.setName(name);
		final SimulationOutputManager m = getOutputManager();
		if (m != null) { m.updateDisplayOutputsName(this); }
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
			color = new GamaColor(GamaPreferences.Interface.SIMULATION_COLORS[getIndex() % 5].getValue());
		}
		return color;
	}

	@Override
	public RootTopology getTopology() {
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
		ownClock.beginCycle();
		executer.executeBeginActions();
		return super.preStep(scope);
	}

	@Override
	protected void postStep(final IScope scope) {
		super.postStep(scope);
		executer.executeEndActions();
		executer.executeOneShotActions();
		if (outputs != null) { outputs.step(this.getScope()); }
		ownClock.step(this.getScope());
	}

	@Override
	public Object _init_(final IScope scope) {
		super._init_(this.getScope());
		initOutputs();
		return this;
	}

	/**
	 * SimulationScope related utilities
	 *
	 */

	@Override
	public IScope getScope() {
		return ownScope;
	}

	public ProjectionFactory getProjectionFactory() {
		return projectionFactory;
	}

	@Override
	public SimulationClock getClock() {
		return ownClock;
	}

	@Override
	public void dispose() {
		if (dead) return;
		executer.executeDisposeActions();
		// hqnghi if simulation come from popultion extern, dispose pop first
		// and then their outputs

		if (externMicroPopulations != null) { externMicroPopulations.clear(); }

		if (outputs != null) {
			outputs.dispose();
			outputs = null;
		}
		if (topology != null) {
			if (!isMicroSimulation()) {
				topology.dispose();
				topology = null;
			} else {
				for (final IPopulation<? extends IAgent> pop : getMicroPopulations()) {
					topology.remove(pop);
				}
			}
		}

		GAMA.releaseScope(getScope());
		// scope = null;
		super.dispose();

	}

	public boolean isMicroSimulation() {
		return getSpecies().getDescription().belongsToAMicroModel();
	}

	@Override
	public void setLocation(final ILocation newGlobalLoc) {}

	@Override
	public ILocation getLocation() {
		if (geometry == null || geometry.getInnerGeometry() == null) return new GamaPoint(0, 0);
		return super.getLocation();
	}

	@Override
	public void setGeometry(final IShape g) {
		// FIXME : AD 5/15 Revert the commit by PT:
		// getProjectionFactory().setWorldProjectionEnv(geom.getEnvelope());
		// We systematically translate the geometry to {0,0}
		IShape geom = g;
		if (geom == null) {
			geom = GamaGeometryType.buildBox(100, 100, 100, new GamaPoint(50, 50, 50));
		} else {
			// See Issue #2787, #2795
			final Geometry gg = geom.getInnerGeometry();
			Object savedData = null;
			if (gg != null) { savedData = gg.getUserData(); }
			geom.setInnerGeometry(geom.getEnvelope().toGeometry());
			geom.getInnerGeometry().setUserData(savedData);
		}

		final Envelope3D env = geom.getEnvelope();
		if (getProjectionFactory().getWorld() == null) {
			projectionFactory.setWorldProjectionEnv(GAMA.getRuntimeScope(), env);
		}

		((WorldProjection) getProjectionFactory().getWorld()).updateTranslations(env);
		((WorldProjection) getProjectionFactory().getWorld()).updateUnit(getProjectionFactory().getUnitConverter());
		final GamaPoint p = new GamaPoint(-env.getMinX(), -env.getMinY(), -env.getMinZ());
		geometry.setGeometry(Transformations.translated_by(getScope(), geom, p));
		if (getProjectionFactory().getUnitConverter() != null) {
			((WorldProjection) getProjectionFactory().getWorld()).convertUnit(geometry.getInnerGeometry());

		}
		setTopology(getScope(), geometry);

	}

	@Override
	public SimulationPopulation getPopulation() {
		return (SimulationPopulation) population;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final String speciesName) throws GamaRuntimeException {
		IPopulation<? extends IAgent> pop = super.getPopulationFor(speciesName);
		if (pop != null) return pop;
		final ISpecies microSpec = getSpecies().getMicroSpecies(speciesName);
		if (microSpec == null) return null;
		pop = GamaPopulation.createPopulation(getScope(), this, microSpec);
		setAttribute(speciesName, pop);
		pop.initializeFor(getScope());
		return pop;
	}

	@getter (CYCLE)
	public Integer getCycle(final IScope scope) {
		final SimulationClock clock = getClock();
		if (clock != null) return clock.getCycle();
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
		if (clock != null) return clock.getStepInSeconds();
		return 1d;
	}

	@setter (IKeyword.STEP)
	public void setTimeStep(final IScope scope, final double t) throws GamaRuntimeException {
		final SimulationClock clock = getClock();
		if (clock != null) {
			clock.setStep(t);

		}
	}

	@getter (TIME)
	public double getTime(final IScope scope) {
		final SimulationClock clock = getClock();
		if (clock != null) return clock.getTimeElapsedInSeconds();
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

	@getter (PlatformAgent.MACHINE_TIME)
	public Double getMachineTime() {
		return GAMA.getPlatformAgent().getMachineTime();
	}

	@setter (PlatformAgent.MACHINE_TIME)
	public void setMachineTime(final Double t) throws GamaRuntimeException {
		// NOTHING
	}

	@setter (CURRENT_DATE)
	public void setCurrentDate(final GamaDate d) throws GamaRuntimeException {
		// NOTHING
	}

	@getter (CURRENT_DATE)
	public GamaDate getCurrentDate() {
		return ownClock.getCurrentDate();
	}

	@setter (STARTING_DATE)
	public void setStartingDate(final GamaDate d) throws GamaRuntimeException {
		ownClock.setStartingDate(d);
	}

	@getter (
			value = STARTING_DATE,
			initializer = true)
	public GamaDate getStartingDate() {
		return ownClock.getStartingDate();
	}

	@action (
			name = "pause",
			doc = @doc ("Allows to pause the current simulation **ACTUALLY EXPERIMENT FOR THE MOMENT**. It can be resumed with the manual intervention of the user or the 'resume' action."))

	public Object pause(final IScope scope) {
		if (!GAMA.isPaused()) { GAMA.pauseFrontmostExperiment(); }
		return null;
	}

	@action (
			name = "resume",
			doc = @doc ("Allows to resume the current simulation **ACTUALLY EXPERIMENT FOR THE MOMENT**. It can then be paused with the manual intervention of the user or the 'pause' action."))

	public Object resume(final IScope scope) {
		if (GAMA.isPaused()) { GAMA.resumeFrontmostExperiment(); }
		return null;
	}

	@action (
			name = "halt",
			doc = @doc (
					deprecated = "It is preferable to use 'die' instead to kill a simulation, or 'pause' to stop it temporarily",
					value = "Allows to stop the current simulation so that cannot be continued after. All the behaviors and updates are stopped. "))

	public Object halt(final IScope scope) {
		getExperiment().closeSimulation(this);
		return null;
	}

	public String getShortUserFriendlyName() {

		return getName();

	}

	public String buildPostfix() {
		final boolean noName = !GamaPreferences.Interface.CORE_SIMULATION_NAME.getValue();
		if (noName) {
			if (getPopulation().size() > 1)
				return " (S" + getIndex() + ")";
			else
				return "";
		} else
			return " (" + getName() + ")";

	}

	public void setOutputs(final IOutputManager iOutputManager) {
		if (iOutputManager == null) return;
		// hqnghi push outputManager down to Simulation level
		// create a copy of outputs from description
		if ( /* !scheduled && */ !getExperiment().getSpecies().isBatch()) {
			final IDescription des = ((ISymbol) iOutputManager).getDescription();
			if (des == null) return;
			outputs = (SimulationOutputManager) des.compile();
			final Map<String, IOutput> mm = GamaMapFactory.create();
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
		if (s == null) { usage = 0; }
		getRandomGenerator().setUsage(usage);
	}

	@getter (
			value = IKeyword.SEED,
			initializer = true)
	public Double getSeed() {
		final Double seed = random.getSeed();
		// DEBUG.LOG("simulation agent get seed: " + seed);
		return seed == null ? Double.valueOf(0d) : seed;
	}

	@setter (IKeyword.SEED)
	public void setSeed(final Double s) {
		// DEBUG.LOG("simulation agent set seed: " + s);
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

	public void setRandomGenerator(final RandomUtils rng) {
		random = rng;
	}

	public void prepareGuiForSimulation(final IScope s) {
		s.getGui().clearErrors(s);
	}

	public void initOutputs() {
		if (outputs != null) { outputs.init(this.getScope()); }
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

		// This list is updated during all the updateWith of the simulation.
		// When all the agents will be created (end of this updateWith),
		// all the references will be replaced by the corresponding agent.
		final List<IReference> list_ref = new ArrayList<>();

		// Update Attribute
		final Map<String, Object> attr = sa.getVariables();
		for (final String varName : attr.keySet()) {
			final Object attrValue = attr.get(varName);

			final boolean isReference = IReference.isReference(attrValue);

			// if( attrValue instanceof ReferenceAgent) {
			if (isReference) {
				((IReference) attrValue).setAgentAndAttrName(this, varName);
				if (!list_ref.contains(attrValue)) { list_ref.add((IReference) attrValue); }
			}

			this.setDirectVarValue(scope, varName, attrValue);
		}

		// Update Clock
		final Object cycle = sa.getAttributeValue("cycle");
		ownClock.setCycle((Integer) cycle);

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
				final IPopulation<? extends IAgent> simuMicroPop = getPopulationFor(savedAgentMicroPopName);
				// final IPopulation<? extends IAgent> simuMicroPop = getMicroPopulation(savedAgentMicroPopName);

				if (simuMicroPop != null) {
					// Build a map name::innerPopAgentSavedAgt :
					// For each agent from the simulation innerPop, it will be
					// updated from the corresponding agent
					final Map<String, SavedAgent> mapSavedAgtName = GamaMapFactory.createUnordered();
					for (final SavedAgent localSA : savedAgentInnerPop.get(savedAgentMicroPopName)) {
						mapSavedAgtName.put((String) localSA.getAttributeValue("name"), localSA);
					}

					final Map<String, IAgent> mapSimuAgtName = GamaMapFactory.createUnordered();

					for (final IAgent agt : simuMicroPop.toArray()) {
						mapSimuAgtName.put(agt.getName(), agt);
					}

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

							simuMicroPop.createAgentAt(scope, e.getValue().getIndex(), e.getValue().getVariables(),
									true, true);
						}

						// Find the agt and all the references
						final IAgent currentAgent = agt == null ? simuMicroPop.getAgent(e.getValue().getIndex()) : agt;

						for (final String name : e.getValue().keySet()) {
							final Object attrValue = e.getValue().get(name);
							final boolean isReference2 = IReference.isReference(attrValue);

							if (isReference2) {
								// if( attrValue instanceof ReferenceAgent) {
								((IReference) attrValue).setAgentAndAttrName(currentAgent, name);
								if (!list_ref.contains(attrValue)) { list_ref.add((IReference) attrValue); }
							}
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

		// Update all the references !
		updateReferences(scope, list_ref, this);
	}

	private void updateReferences(final IScope scope, final List<IReference> list_ref, final SimulationAgent sim) {
		// list_ref.stream().forEach(
		// ref -> ref.resolveReferences(scope, sim)
		// );

		for (final IReference ref : list_ref) {
			ref.resolveReferences(scope, sim);
		}
	}

	public void adoptTopologyOf(final SimulationAgent root) {
		final RootTopology rt = root.getTopology();
		rt.mergeWith(topology);
		setTopology(rt);
		for (final IPopulation<?> p : getMicroPopulations()) {
			p.getTopology().setRoot(root.getScope(), rt);
		}
	}

}
