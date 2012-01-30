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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.IEnvironment;
import msi.gama.metamodel.topology.continuous.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.expressions.*;
import msi.gaml.factories.ModelFactory;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import msi.gaml.variables.IVariable;

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
		IExpression expr = species.getFacet(IKeyword.TOPOLOGY);
		if ( expr == null ) {
			super.computeTopology(scope);
		} else {
			topology = GamaTopologyType.staticCast(scope, scope.evaluate(expr, host), null);
		}
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		IArchitecture c = species.getArchitecture();
		c.executeOn(scope);
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		IArchitecture control = species.getArchitecture();
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
			public synchronized IShape getGeometry() {
				return geometry;
			}

			@Override
			public synchronized void setLocation(final ILocation newGlobalLoc) {}

			@Override
			public synchronized void setGeometry(final IShape newGlobalGeometry) {}

			public void initializeLocationAndGeomtry(final IScope scope) {
				IEnvironment modelEnv = scope.getSimulationScope().getModel().getModelEnvironment();
				double width = modelEnv.getWidth();
				double height = modelEnv.getHeight();
				location = new GamaPoint(width / 2, height / 2);
				geometry = GamaGeometryType.buildRectangle(width, height, location);
			}

			@Override
			// Special case for built-in species handled by the world (and not created before)
			public IPopulation getPopulationFor(final String speciesName)
				throws GamaRuntimeException {
				IPopulation pop = super.getPopulationFor(speciesName);
				if ( pop != null ) { return pop; }
				if ( ModelFactory.isBuiltIn(speciesName) ) {
					ISpecies microSpec = this.getVisibleSpecies(speciesName);
					pop = new GamlPopulation(this, microSpec);
					microPopulations.put(microSpec, pop);
					pop.initializeFor(this.getSimulation().getExecutionScope());
					return pop;
				}
				throw new GamaRuntimeException("The population of " + speciesName +
					" is not accessible from " + this);
			}

		}

		public WorldPopulation(final ISpecies expr) {
			super(null, expr);
		}

		@Override
		public IList<? extends IAgent> createAgents(final IScope scope, final int number,
			final List<Map<String, Object>> initialValues, final boolean isRestored)
			throws GamaRuntimeException {
			if ( size() == 0 ) {
				WorldAgent world = new WorldAgent(scope.getSimulationScope(), this);
				world.setIndex(0);
				add(world);
				createVariablesFor(scope, this, initialValues);
				// initialize the model environment
				IEnvironment modelEnv = scope.getSimulationScope().getModel().getModelEnvironment();
				modelEnv.initializeFor(scope);
				world.initializeLocationAndGeomtry(scope);
				topology = new ContinuousTopology(scope, world.getGeometry());
			}
			return this;
		}

		@Override
		public IAgent getAgent(final ILocation value) {
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
	public void computeAgentsToSchedule(final IScope scope, final IList list)
		throws GamaRuntimeException {
		int frequency = Cast.asInt(scope, scheduleFrequency.value(scope));
		int step = SimulationClock.getCycle();
		IExpression ags = getSpecies().getSchedule();
		List<IAgent> agents =
			ags == null ? this.getAgentsList() : Cast.asList(scope, ags.value(scope));
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
