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

import java.awt.Graphics2D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 1 déc. 2010
 * 
 * @todo Description
 * 
 */
@species(name = IKeyword.SIMULATION)
@vars({
	@var(name = IKeyword.STEP, type = IType.FLOAT, doc = @doc(value = "Represents the value of the interval, in model time, between two simulation cycles", comment = "If not set, its value is equal to 1.0 and, since the default time unit is the second, to 1 second")),
	@var(name = IKeyword.TIME, type = IType.FLOAT, doc = @doc(value = "Represents the total time passed, in model time, since the beginning of the simulation", comment = "Equal to cycle * step if the user does not arbitrarily initialize it.")),
	@var(name = GamlSimulation.CYCLE, type = IType.INT, doc = @doc("Returns the current cycle of the simulation")), })
public class GamlSimulation extends GamlAgent implements ISimulationAgent {

	public static final String CYCLE = "cycle";
	// FIXME Temporary. Should be in the topology of the world population
	private final GisUtils gis = new GisUtils();
	// FIXME Temporary. Should be in the topology of the world population
	private ISpatialIndex.Compound spatialIndex;
	// FIXME Temporary. Should be in the topology of the world population
	boolean isTorus = false;

	// protected AbstractScheduler scheduler;
	// protected final IExperimentSpecies experiment;

	protected static int simulationNumber;
	protected int number;

	// FIXME Actually a WorldPopulation
	public GamlSimulation(final IPopulation pop) throws GamaRuntimeException {
		super(pop);
		// experiment = (IExperimentSpecies) pop.getHost().getSpecies();
		number = simulationNumber++;
		// GUI.debug("Instanciating a new scheduler");
		// initSchedulingPolicy();
		// scheduler = new Scheduler(this);
		// GUI.debug("Initializing the simulation with " + parameters);
		// initialize(parameters);
	}

	// protected void initSchedulingPolicy() {
	// scheduler = new Scheduler(this, experiment.getAgent());
	// }

	// @Override
	// public IScheduler getScheduler() {
	// return scheduler;
	// }

	//
	// @Override
	// public IExperimentSpecies getExperiment() {
	// return experiment;
	// }

	// @Override
	// public boolean isAlive() {
	// return scheduler.alive /* && !scheduler.paused */;
	// }

	// @Override
	// public boolean isPaused() {
	// // TODO Verify that the use of user_hold here does not harm the run
	// return /* scheduler.alive && */scheduler.paused || scheduler.on_user_hold;
	// }
	//
	// @Override
	// public synchronized void dispose() {
	// GUI.debug("Simulation " + number + " of experiment " + experiment.getName() +
	// " being disposed");
	// if ( scheduler != null ) {
	// scheduler.dispose();
	// }
	// GUI.debug("Disposing the agent managers");
	// GisUtils.setTransformCRS(null);
	// GUI.debug("Disposing the environment");
	// if ( environment != null ) {
	// environment.dispose();
	// environment = null;
	// }
	// }

	// @Override
	// public void close() {
	// dispose();
	// // GUI.debug("Simulation disposed");
	// }
	//
	// @Override
	// public void step() {
	// scheduler.stepByStep();
	// }
	//
	// @Override
	// public void pause() {
	// scheduler.pause();
	// }

	// @Override
	// public void stop() {
	// scheduler.alive = false;
	// }

	// @Override
	// public void start() {
	// scheduler.start();
	// }

	//
	// @Override
	// public String getName() {
	// return experiment.getName();
	// }

	// @Override
	// public void initialize(final ParametersSet parameters) throws GamaRuntimeException {

	// initializeWorld(parameters);
	// GuiUtils.waitStatus(" Instantiating agents ");
	// scheduler.enterInitSequence(experiment.getExecutionScope());

	// }

	// FIXME Change everything and move it to Experiment and ExperimentAgent
	// protected void initializeWorld(final Map<String, Object> parameters) throws GamaRuntimeException {
	// IScope scope = experiment.getGlobalScope();
	// population.initializeFor(scope);
	// Here, the link is being made with the experimentator agent, which becomes the "host" of
	// the world population
	// experiment.getAgent().addMicroPopulation((WorldPopulation) population);
	// List<? extends IAgent> newAgents = population.createAgents(scope, 1, GamaList.with(parameters), false);
	// IAgent world = newAgents.get(0);
	// experiment.getAgent().schedule(scope);
	// world.schedule(scope);
	// }

	// @Override
	// public IModel getModel() {
	// return experiment.getModel();
	// }

	@Override
	public GisUtils getGisUtils() {
		return gis;
	}

	@Override
	public synchronized void setLocation(final ILocation newGlobalLoc) {}

	@Override
	public void setTorus(Boolean t) {
		isTorus = t;
	}

	public boolean isTorus() {
		return isTorus;
	}

	@Override
	public synchronized ILocation getLocation() {
		if ( geometry == null ) { return new GamaPoint(0, 0); }
		return super.getLocation();
	}

	@Override
	public synchronized void setGeometry(final IShape newGlobalGeometry) {
		Envelope env = newGlobalGeometry.getEnvelope();
		gis.init(env);
		GamaPoint p = new GamaPoint(-1 * env.getMinX(), -1 * env.getMinY());
		// FIXME Compute the geometry as an envelope here ?
		geometry = Transformations.translated_by(getScope(), newGlobalGeometry, p);
		Envelope bounds = geometry.getEnvelope();
		// bounds.translate(GisUtils.XMinComp, GisUtils.YMinComp);

		if ( spatialIndex != null ) {
			spatialIndex.dispose();
		}
		spatialIndex = new CompoundSpatialIndex(bounds);
		getPopulation().setTopology(getScope(), geometry, isTorus);
		for ( IAgent ag : getAgents() ) {
			ag.setGeometry(Transformations.translated_by(getScope(), ag.getGeometry(), p));
		}

	}

	@Override
	public ISpatialIndex.Compound getSpatialIndex() {
		return spatialIndex;
	}

	@Override
	public WorldPopulation getPopulation() {
		return (WorldPopulation) population;
	}

	@Override
	public void displaySpatialIndexOn(final Graphics2D g2, final int width, final int height) {
		if ( spatialIndex == null ) { return; }
		spatialIndex.drawOn(g2, width, height);
	}

	@Override
	// Special case for built-in species handled by the world (and not created before)
	public IPopulation getPopulationFor(final String speciesName) throws GamaRuntimeException {
		IPopulation pop = super.getPopulationFor(speciesName);
		if ( pop != null ) { return pop; }
		if ( Types.isBuiltIn(speciesName) ) {
			ISpecies microSpec = this.getSpecies().getMicroSpecies(speciesName);
			pop = new GamaPopulation(this, microSpec);
			microPopulations.put(microSpec, pop);
			pop.initializeFor(getScope());
			return pop;
		}
		return null;
	}

	@getter(CYCLE)
	public Integer getCycle(final IScope scope, final IAgent agent) {
		SimulationClock clock = scope.getClock();
		if ( clock != null ) { return clock.getCycle(); }
		return 0;
	}

	@getter(IKeyword.STEP)
	public double getTimeStep(final IScope scope, final IAgent agent) {
		SimulationClock clock = scope.getClock();
		if ( clock != null ) { return clock.getStep(); }
		return 1d;
	}

	@setter(IKeyword.STEP)
	public void setTimeStep(final IScope scope, final IAgent agent, final double t) throws GamaRuntimeException {
		SimulationClock clock = scope.getClock();
		if ( clock != null ) {
			clock.setStep(t);
		}
	}

	@getter(IKeyword.TIME)
	public double getTime(final IScope scope, final IAgent agent) {
		SimulationClock clock = scope.getClock();
		if ( clock != null ) { return clock.getTime(); }
		return 0d;
	}

	@setter(IKeyword.TIME)
	public void setTime(final IScope scope, final IAgent agent, final double t) throws GamaRuntimeException {
		SimulationClock clock = scope.getClock();
		if ( clock != null ) {
			clock.setTime(t);
		}
	}

	@action(name = "pause", doc = @doc("Allows to pause the current simulation. It can be set to continue with the manual intervention of the user."))
	@args(names = {})
	public Object pause(final IScope scope) {
		if ( !getExperiment().isPaused() ) {
			getExperiment().userPauseExperiment();
		}
		return null;
	}

	@Override
	public ISimulationAgent getSimulation() {
		return this;
	}

	@action(name = "halt", doc = @doc("Allows to stop the current simulation. It cannot be continued after"))
	@args(names = {})
	public Object halt(final IScope scope) {
		if ( getExperiment().isRunning() ) {
			getExperiment()./* stop() */userPauseExperiment();
		}
		return null;
	}

	// @action(name = "reload", doc = @doc("Allows to reload the current experiment. "))
	// @args(names = {})
	// public Object reload(final IScope scope) {
	// getExperiment().userReload();
	// return null;
	// }

}