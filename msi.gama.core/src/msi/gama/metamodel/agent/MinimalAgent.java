package msi.gama.metamodel.agent;

import java.util.Set;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.GamaGeometryType;

@species(name = IKeyword.AGENT)
public class MinimalAgent extends AbstractAgent {

	/** The population that this agent belongs to. */
	protected final IPopulation population;
	protected String name;
	protected final IShape geometry;

	/**
	 * @param s the population used to prototype the agent.
	 */
	public MinimalAgent(final IPopulation s) {
		this(s, new GamaShape((Geometry) null));
	}

	protected MinimalAgent(final IPopulation population, final IShape geometry) {
		this.population = population;
		this.geometry = geometry;
	}

	@Override
	public IPopulation getPopulation() {
		return population;
	}

	@Override
	public IShape getGeometry() {
		return geometry;
	}

	@Override
	public/* synchronized */void setGeometry(final IShape newGeometry) {
		// Addition to address Issue 817: if the new geometry is exactly the one possessed by the agent, no need to
		// change anything.
		if ( newGeometry == geometry || newGeometry == null || newGeometry.getInnerGeometry() == null || dead() ||
			this.getSpecies().isGrid() ) { return; }

		final ITopology topology = population.getTopology();
		final ILocation newGeomLocation = newGeometry.getLocation().copy(getScope());

		// if the old geometry is "shared" with another agent, we create a new one.
		// otherwise, we copy it directly.
		final IAgent other = newGeometry.getAgent();
		final IShape newLocalGeom = other == null ? newGeometry : newGeometry.copy(getScope());
		topology.normalizeLocation(newGeomLocation, false);

		if ( !newGeomLocation.equals(newLocalGeom.getLocation()) ) {
			newLocalGeom.setLocation(newGeomLocation);
		}

		newLocalGeom.setAgent(this);
		final Envelope previous = geometry.getEnvelope();
		geometry.setGeometry(newLocalGeom);

		topology.updateAgent(previous, this);

		// update micro-agents' locations accordingly

		// TODO DOES NOT WORK FOR THE MOMENT
		// for ( final IPopulation pop : getMicroPopulations() ) {
		// pop.hostChangesShape();
		// }
	}

	@Override
	public String getName() {
		if ( name == null ) {
			name = super.getName();
		}
		if ( dead() ) {
			return name + " (dead)";
		} else {
			return name;
		}
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public/* synchronized */void setLocation(final ILocation point) {
		if ( point == null || dead() || this.getSpecies().isGrid() ) { return; }
		final ILocation newLocation = point.copy(getScope());
		final ITopology topology = population.getTopology();
		if ( topology == null ) { return; }
		topology.normalizeLocation(newLocation, false);

		if ( geometry == null || geometry.getInnerGeometry() == null ) {
			setGeometry(GamaGeometryType.createPoint(newLocation));
		} else {
			final ILocation previousPoint = geometry.getLocation();
			if ( newLocation.equals(previousPoint) ) { return; }
			final Envelope previous = geometry.getEnvelope();
			// Envelope previousEnvelope = geometry.getEnvelope();
			geometry.setLocation(newLocation);
			// final Integer newHeading = topology.directionInDegreesTo(getScope(), previousPoint, newLocation);
			// if ( newHeading != null && !getTopology().isTorus() ) {
			// setHeading(newHeading);
			// }
			topology.updateAgent(previous, this);

			// update micro-agents' locations accordingly
			// for ( final IPopulation pop : getMicroPopulations() ) {
			// // FIXME DOES NOT WORK FOR THE MOMENT
			// pop.hostChangesShape();
			// }
		}
		final GamaGraph graph = (GamaGraph) getAttribute("attached_graph");
		if ( graph != null ) {
			final Set edgesToModify = graph.edgesOf(this);
			for ( final Object obj : edgesToModify ) {
				if ( obj instanceof IAgent ) {
					final IShape ext1 = (IShape) graph.getEdgeSource(obj);
					final IShape ext2 = (IShape) graph.getEdgeTarget(obj);
					((IAgent) obj).setGeometry(GamaGeometryType.buildLine(ext1.getLocation(), ext2.getLocation()));
				}
			}

		}
	}

	@Override
	public/* synchronized */ILocation getLocation() {
		if ( geometry == null || geometry.getInnerGeometry() == null ) {
			IScope scope = this.getScope();
			final ILocation randomLocation = population.getTopology().getRandomLocation(scope);
			if ( randomLocation == null ) { return null; }
			setGeometry(GamaGeometryType.createPoint(randomLocation));
			return randomLocation;
		}
		return geometry.getLocation();
	}

	@Override
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		if ( s.getName().equals(IKeyword.AGENT) ) { return true; }
		return super.isInstanceOf(s, direct);
	}

	/**
	 * During the call to init, the agent will search for the action named _init_ and execute it. Its default
	 * implementation is provided in this class as well.
	 * @see GamlAgent#_init_()
	 * @see msi.gama.common.interfaces.IStepable#step(msi.gama.runtime.IScope)
	 * @warning This method should NOT be overriden (except for some rare occasions like in SimulationAgent). Always
	 *          override _init_(IScope) instead.
	 */
	@Override
	public boolean init(final IScope scope) {
		if ( !getSpecies().isInitOverriden() ) {
			_init_(scope);
		} else {
			executeCallbackAction(scope, getSpecies().getAction(ISpecies.initActionName));
		}
		return !scope.interrupted();
	}

	/**
	 * During the call to step, the agent will search for the action named _step_ and execute it. Its default
	 * implementation is provided in this class as well.
	 * @see GamlAgent#_step_()
	 * @see msi.gama.common.interfaces.IStepable#step(msi.gama.runtime.IScope)
	 * @warning This method should NOT be overriden (except for some rare occasions like in SimulationAgent). Always
	 *          override _step_(IScope) instead.
	 */
	@Override
	public boolean step(final IScope scope) {
		if ( !getSpecies().isStepOverriden() ) {
			_step_(scope);
		} else {
			executeCallbackAction(scope, getSpecies().getAction(ISpecies.stepActionName));
		}
		return !scope.interrupted();
	}

	/**
	 * Callback Actions
	 *
	 */

	protected Object executeCallbackAction(final IScope scope, final IStatement action) {
		Object[] callbackResult = new Object[1];
		scope.execute(action, this, null, callbackResult);
		return callbackResult[0];
	}

	@action(name = ISpecies.initActionName)
	public Object _init_(final IScope scope) {
		getSpecies().getArchitecture().init(scope);
		return this;
	}

	@action(name = ISpecies.stepActionName)
	public Object _step_(final IScope scope) {
		scope.update(this);
		// we ask the architecture to execute on this
		Object[] result = new Object[1];
		if ( scope.execute(getSpecies().getArchitecture(), this, null, result) ) {
			// we ask the sub-populations to step their agents if any
			return stepSubPopulations(scope);
		}
		return result[0];
	}

	/**
	 * @param scope
	 * @return
	 */
	protected Object stepSubPopulations(final IScope scope) {
		return this;
	}

}
