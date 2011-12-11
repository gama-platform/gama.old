/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.kernel;

import java.util.*;
import msi.gama.agents.AbstractPopulation;
import msi.gama.environment.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.GamaTopologyType;
import msi.gama.java.JavaConstExpression;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.agents.GamlAgent;
import msi.gaml.control.IControl;

/**
 * Written by drogoul Modified on 8 nov. 2010
 * 
 * @todo Description
 * 
 */
public class GamlPopulation extends AbstractPopulation implements IGamlPopulation {

	IExpression scheduleFrequency = new JavaConstExpression(1);

	public GamlPopulation(final IAgent macroAgent, final ISpecies species) {
		super(macroAgent, species);
		IExpression exp = species.getFrequency();
		if ( exp != null ) {
			scheduleFrequency = exp;
		}
	}

	@Override
	protected void computeTopology(final IScope scope) throws GamaRuntimeException {
		IExpression expr = species.getFacet(ISpecies.TOPOLOGY);
		if ( expr == null ) {
			super.computeTopology(scope);
		} else {
			topology = GamaTopologyType.staticCast(scope, scope.evaluate(expr, host), null);
		}
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		IControl c = species.getControl();
		c.executeOn(scope);
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		IControl control = species.getControl();
		control.init(scope);
	}

	@Override
	public void createVariablesFor(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		for ( final String s : orderedVarNames ) {
			final IVariable var = species.getVar(s);
			var.initializeWith(scope, agent, null);
		}
	}

	public static class WorldPopulation extends GamlPopulation {

		static class WorldAgent extends GamlAgent {

			private GamaPoint location;

			WorldAgent(final ISimulation sim, final IPopulation s) throws GamaRuntimeException {
				super(sim, s);
				index = 0;
			}

			@Override
			public synchronized GamaPoint getLocation() {
				return location;
			}

			@Override
			public synchronized GamaGeometry getGeometry() {
				return geometry;
			}

			@Override
			public synchronized void setLocation(final GamaPoint newGlobalLoc) {}

			@Override
			public synchronized void setGeometry(final GamaGeometry newGlobalGeometry) {}

			public void initializeLocationAndGeomtry(final IScope scope) {
				ModelEnvironment modelEnv =
					scope.getSimulationScope().getModel().getModelEnvironment();
				double width = modelEnv.getWidth();
				double height = modelEnv.getHeight();
				location = new GamaPoint(width / 2, height / 2);
				geometry = GamaGeometry.buildRectangle(width, height, location);
			}
		}

		public WorldPopulation(final ISpecies expr) {
			super(null, expr);
		}

		@Override
		public List<? extends IAgent> createAgents(final IScope scope, final int number,
			final List<Map<String, Object>> initialValues, final boolean isRestored)
			throws GamaRuntimeException {
			if ( size() == 0 ) {
				WorldAgent world = new WorldAgent(scope.getSimulationScope(), this);
				world.setIndex(0);
				add(world);
				createVariablesFor(scope, this, initialValues);
				// initialize the model environment
				ModelEnvironment modelEnv =
					scope.getSimulationScope().getModel().getModelEnvironment();
				modelEnv.initializeFor(scope);
				world.initializeLocationAndGeomtry(scope);
				topology = new ContinuousTopology(scope, world.getGeometry());
			}
			return this;
		}

		@Override
		public IAgent getAgent(final GamaPoint value) {
			return get(0);
		}

		@Override
		public IAgent getHost() {
			return null;
		}

		@Override
		public void computeTopology(final IScope scope) throws GamaRuntimeException {
			topology = new AmorphousTopology();
		}

	}

	/**
	 * 
	 * @see msi.gama.interfaces.IPopulation#computeAgentsToSchedule(msi.gama.interfaces.IScope,
	 *      msi.gama.util.GamaList)
	 */
	@Override
	public void computeAgentsToSchedule(final IScope scope, final GamaList list)
		throws GamaRuntimeException {
		int frequency = Cast.asInt(scheduleFrequency.value(scope));
		long step = scope.getSimulationScope().getScheduler().getCycle();
		IExpression ags = getSpecies().getSchedule();
		List<IAgent> agents = ags == null ? this.getAgentsList() : Cast.asList(ags.value(scope));
		if ( step % frequency == 0 ) {
			list.addAll(agents);
		}
		if ( species.hasMicroSpecies() ) {
			for ( IAgent agent : this.getAgentsList() ) {
				agent.computeAgentsToSchedule(scope, list);
			}
		}
	}
}
