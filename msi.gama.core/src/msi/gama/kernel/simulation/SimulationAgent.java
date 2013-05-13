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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.AgentScheduler;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Defines an instance of a model (a simulation). Serves as the support for model species (whose metaclass is
 * GamlModelSpecies)
 * Written by drogoul Modified on 1 déc. 2010, May 2013
 * 
 * @todo Description
 * 
 */
@species(name = IKeyword.MODEL)
@vars({
	@var(name = IKeyword.STEP, type = IType.FLOAT, doc = @doc(value = "Represents the value of the interval, in model time, between two simulation cycles", comment = "If not set, its value is equal to 1.0 and, since the default time unit is the second, to 1 second")),
	@var(name = IKeyword.TIME, type = IType.FLOAT, doc = @doc(value = "Represents the total time passed, in model time, since the beginning of the simulation", comment = "Equal to cycle * step if the user does not arbitrarily initialize it.")),
	@var(name = SimulationAgent.CYCLE, type = IType.INT, doc = @doc("Returns the current cycle of the simulation")), })
public class SimulationAgent extends GamlAgent implements ISimulationAgent {

	public static final String CYCLE = "cycle";
	final SimulationClock clock;
	AgentScheduler scheduler;
	IScope scope;
	private static int SIM_NUMBER = 0;
	private int number = 0;

	public SimulationAgent(final IPopulation pop) throws GamaRuntimeException {
		super(pop);
		number = SIM_NUMBER++;
		setName("Simulation " + number);
		clock = new SimulationClock();
		scope = obtainNewScope();
		scheduler = new AgentScheduler(scope, this);
	}

	@Override
	// TODO A redefinition of this method in GAML will lose all information regarding the clock and the advance of time,
	// which will have to be done manually (i.e. cycle <- cycle + 1; time <- time + step;)
	public Object _step_(IScope scope) {
		clock.beginCycle();
		// A simulation always runs in its own scope
		super._step_(this.scope);
		clock.step();
		return this;
	}

	@Override
	public Object _init_(IScope scope) {
		// A simulation always runs in its own scope
		return super._init_(this.scope);
	}

	/**
	 * Scope related utilities
	 * 
	 */

	@Override
	public IScope getScope() {
		return scope;
	}

	@Override
	public AgentScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public SimulationClock getClock() {
		return clock;
	}

	@Override
	public void dispose() {
		// GuiUtils.debug("SimulationAgent.dispose");
		if ( dead ) { return; }
		super.dispose();
		// We dispose of any scheduler still running
		if ( scheduler != null ) {
			scheduler.dispose();
			scheduler = null;
		}

	}

	@Override
	public synchronized void setLocation(final ILocation newGlobalLoc) {}

	@Override
	public synchronized ILocation getLocation() {
		if ( geometry == null ) { return new GamaPoint(0, 0); }
		return super.getLocation();
	}

	@Override
	public synchronized void setGeometry(final IShape geom) {
		Envelope env = geom.getEnvelope();
		GamaPoint p = new GamaPoint(-env.getMinX(), -env.getMinY());
		geometry = Transformations.translated_by(getScope(), geom, p);
		getPopulation().setTopology(getScope(), geom, geometry);
		for ( IAgent ag : getAgents() ) {
			ag.setGeometry(Transformations.translated_by(getScope(), ag.getGeometry(), p));
		}

	}

	@Override
	public SimulationPopulation getPopulation() {
		return (SimulationPopulation) population;
	}

	@Override
	public IPopulation getPopulationFor(final String speciesName) throws GamaRuntimeException {
		IPopulation pop = super.getPopulationFor(speciesName);
		if ( pop != null ) { return pop; }
		ISpecies microSpec = getSpecies().getMicroSpecies(speciesName);
		if ( microSpec == null ) { return null; }
		pop = new GamaPopulation(this, microSpec);
		microPopulations.put(microSpec, pop);
		pop.initializeFor(getScope());
		return pop;
	}

	@getter(CYCLE)
	public Integer getCycle(final IScope scope, final IAgent agent) {
		SimulationClock clock = getClock();
		if ( clock != null ) { return clock.getCycle(); }
		return 0;
	}

	@getter(IKeyword.STEP)
	public double getTimeStep(final IScope scope, final IAgent agent) {
		SimulationClock clock = getClock();
		if ( clock != null ) { return clock.getStep(); }
		return 1d;
	}

	@setter(IKeyword.STEP)
	public void setTimeStep(final IScope scope, final IAgent agent, final double t) throws GamaRuntimeException {
		SimulationClock clock = getClock();
		if ( clock != null ) {
			clock.setStep(t);
		}
	}

	@getter(IKeyword.TIME)
	public double getTime(final IScope scope, final IAgent agent) {
		SimulationClock clock = getClock();
		if ( clock != null ) { return clock.getTime(); }
		return 0d;
	}

	@setter(IKeyword.TIME)
	public void setTime(final IScope scope, final IAgent agent, final double t) throws GamaRuntimeException {
		SimulationClock clock = getClock();
		if ( clock != null ) {
			clock.setTime(t);
		}
	}

	@action(name = "pause", doc = @doc("Allows to pause the current simulation **ACTUALLY EXPERIMENT FOR THE MOMENT**. It can be set to continue with the manual intervention of the user."))
	@args(names = {})
	public Object pause(final IScope scope) {
		GAMA.controller.userPause();
		return null;
	}

	@action(name = "halt", doc = @doc("Allows to stop the current simulation. It cannot be continued after. All the behaviors and updates are stopped, and the simulation is disposed of, as well as all the other agents. "))
	@args(names = {})
	public Object halt(final IScope scope) {
		return primDie(scope);
	}

	// private class SimulationScheduler extends AgentScheduler{
	//
	// public SimulationScheduler(IScope scope, IStepable owner) {
	// super(scope, owner);
	// }
	//
	// @Override
	// public void step(IScope scope) throws GamaRuntimeException {
	// super.step(SimulationAgent.this.scope);
	// }
	//
	// @Override
	// public void init(IScope scope) throws GamaRuntimeException {
	// super.init(SimulationAgent.this.scope);
	// }
	//
	//
	//
	// }

}