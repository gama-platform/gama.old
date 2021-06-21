/*******************************************************************************************************
 *
 * msi.gama.metamodel.agent.MinimalAgent.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.Objects;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.species.GamlSpecies;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;

@species (
		name = IKeyword.AGENT,
		doc = @doc ("The species parent of all agent species"))
/**
 * A concrete implementation of AbstractAgent that declares its own population, geometry and name. Base of most of the
 * concrete subclasses of GAMA agents
 *
 * @author drogoul
 *
 */
public class MinimalAgent extends AbstractAgent {

	/** The population that this agent belongs to. */
	protected final IPopulation<? extends IAgent> population;
	protected String name;
	protected final IShape geometry;
	private final int hashCode;

	/**
	 * @param s
	 *            the population used to prototype the agent.
	 */
	public MinimalAgent(final IPopulation<? extends IAgent> s, final int index) {
		this(s, index, new GamaShape((Geometry) null));
	}

	protected MinimalAgent(final IPopulation<? extends IAgent> population, final int index, final IShape geometry) {
		super(index);
		this.population = population;
		this.hashCode = Objects.hash(getPopulation(), index);
		this.geometry = geometry;
		geometry.setAgent(this);
	}

	@Override
	public void setGeometricalType(final Type t) {
		geometry.setGeometricalType(t);
	}

	@Override
	public IPopulation<? extends IAgent> getPopulation() {
		return population;
	}

	@Override
	public IShape getGeometry() {
		return geometry;
	}

	@Override
	public/* synchronized */void setGeometry(final IShape newGeometry) {
		// Addition to address Issue 817: if the new geometry is exactly the one
		// possessed by the agent, no need to change anything.
		if (newGeometry == geometry || newGeometry == null || newGeometry.getInnerGeometry() == null || dead()
				|| this.getSpecies().isGrid() && ((GamlSpecies) this.getSpecies()).belongsToAMicroModel())
			return;

		final ITopology topology = getTopology();
		final ILocation newGeomLocation = newGeometry.getLocation().copy(getScope());

		// if the old geometry is "shared" with another agent, we create a new
		// one. otherwise, we copy it directly.
		final IAgent other = newGeometry.getAgent();
		IShape newLocalGeom;
		if (other == null) {
			newLocalGeom = newGeometry;
		} else {
			// If the agent is different, we do not copy the attributes present in the shape passed as argument (see
			// Issue #2053).
			newLocalGeom = new GamaShape((Geometry) newGeometry.getInnerGeometry().clone());
			newLocalGeom.copyShapeAttributesFrom(newGeometry);
		}
		topology.normalizeLocation(newGeomLocation, false);

		if (!newGeomLocation.equals(newLocalGeom.getLocation())) { newLocalGeom.setLocation(newGeomLocation); }

		newLocalGeom.setAgent(this);
		final Envelope3D previous = Envelope3D.of(geometry);
		geometry.setGeometry(newLocalGeom);

		topology.updateAgent(previous, this);

		// update micro-agents' locations accordingly

		// TODO DOES NOT WORK FOR THE MOMENT
		// for ( final IPopulation pop : getMicroPopulations() ) {
		// pop.hostChangesShape();
		// }

		notifyVarValueChange(IKeyword.SHAPE, newLocalGeom);
	}

	@Override
	public String getName() {
		if (name == null) { name = super.getName(); }
		if (dead())
			return name + " (dead)";
		else
			return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
		notifyVarValueChange(IKeyword.NAME, name);
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public/* synchronized */void setLocation(final ILocation point) {
		if (point == null || dead() || this.getSpecies().isGrid()) return;
		final ILocation newLocation = point.copy(getScope());
		final ITopology topology = getTopology();
		if (topology == null) return;
		topology.normalizeLocation(newLocation, false);

		if (geometry == null || geometry.getInnerGeometry() == null) {
			setGeometry(GamaGeometryType.createPoint(newLocation));
		} else {
			final ILocation previousPoint = geometry.getLocation();
			if (newLocation.equals(previousPoint)) return;
			final Envelope3D previous = geometry.getEnvelope();
			geometry.setLocation(newLocation);
			topology.updateAgent(previous, this);

			// update micro-agents' locations accordingly
			// for ( final IPopulation pop : getMicroPopulations() ) {
			// // FIXME DOES NOT WORK FOR THE MOMENT
			// pop.hostChangesShape();
			// }
		}
		final GamaGraph graph = (GamaGraph) getAttribute("attached_graph");
		if (graph != null) {
			final Set edgesToModify = graph.edgesOf(this);
			for (final Object obj : edgesToModify) {
				if (obj instanceof IAgent) {
					final IShape ext1 = (IShape) graph.getEdgeSource(obj);
					final IShape ext2 = (IShape) graph.getEdgeTarget(obj);
					((IAgent) obj).setGeometry(GamaGeometryType.buildLine(ext1.getLocation(), ext2.getLocation()));
				}
			}

		}
		notifyVarValueChange(IKeyword.LOCATION, newLocation);
	}

	@Override
	public/* synchronized */ILocation getLocation() {
		if (geometry == null || geometry.getInnerGeometry() == null) {
			final IScope scope = this.getScope();
			final ITopology t = getTopology();
			final ILocation randomLocation = t == null ? null : t.getRandomLocation(scope);
			if (randomLocation == null) return null;
			setGeometry(GamaGeometryType.createPoint(randomLocation));
			return randomLocation;
		}
		return geometry.getLocation();
	}

	@Override
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		// TODO and direct ?
		if (s.getName().equals(IKeyword.AGENT)) return true;
		return super.isInstanceOf(s, direct);
	}

	/**
	 * During the call to init, the agent will search for the action named _init_ and execute it. Its default
	 * implementation is provided in this class as well (equivalent to a super.init())
	 *
	 * @see GamlAgent#_init_()
	 * @see msi.gama.common.interfaces.IStepable#step(msi.gama.runtime.IScope)
	 * @warning This method should NOT be overriden (except for some rare occasions like in SimulationAgent). Always
	 *          override _init_(IScope) instead.
	 */
	@Override
	public boolean init(final IScope scope) {
		if (!getPopulation().isInitOverriden()) {
			_init_(scope);
		} else {
			scope.execute(getSpecies().getAction(ISpecies.initActionName), this, null);
		}
		return !scope.interrupted();
	}

	/**
	 * During the call to doStep(), the agent will search for the action named _step_ and execute it. Its default
	 * implementation is provided in this class as well (equivalent to a super.doStep());
	 *
	 * @see GamlAgent#_step_()
	 * @see msi.gama.common.interfaces.IStepable#step(msi.gama.runtime.IScope)
	 * @warning This method should NOT be overriden (except for some rare occasions like in SimulationAgent). Always
	 *          override _step_(IScope) instead.
	 */
	@Override
	public boolean doStep(final IScope scope) {
		if (!getPopulation().isStepOverriden()) {
			super.doStep(scope);
			return !scope.interrupted();
		} else
			return scope.execute(getSpecies().getAction(ISpecies.stepActionName), this, null).passed();
	}

	/**
	 * The default init of agents consists in calling the super implementation of init() in order to realize the default
	 * init sequence
	 *
	 * @param scope
	 * @return
	 */
	@action (
			name = ISpecies.initActionName)
	public Object _init_(final IScope scope) {
		return super.init(scope);
	}

	/**
	 * The default step of agents consists in calling the super implementation of doStep() in order to realize the
	 * default step sequence
	 *
	 * @param scope
	 * @return
	 */
	@action (
			name = ISpecies.stepActionName)
	public Object _step_(final IScope scope) {
		return super.step(scope);
	}

	/**
	 * Method getArea(). Simply delegates to the geometry
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getArea()
	 */
	@Override
	public Double getArea() {
		return geometry.getArea();
	}

	/**
	 * Method getVolume(). Simply delegates to the geometry
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getVolume()
	 */
	@Override
	public Double getVolume() {
		return geometry.getVolume();
	}

	/**
	 * Method getPerimeter()
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return geometry.getPerimeter();
	}

	/**
	 * Method getHoles()
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getHoles()
	 */
	@Override
	public IList<GamaShape> getHoles() {
		return geometry.getHoles();
	}

	/**
	 * Method getCentroid()
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getCentroid()
	 */
	@Override
	public GamaPoint getCentroid() {
		return geometry.getCentroid();
	}

	/**
	 * Method getExteriorRing()
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getExteriorRing()
	 */
	@Override
	public GamaShape getExteriorRing(final IScope scope) {
		return geometry.getExteriorRing(scope);
	}

	/**
	 * Method getWidth()
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getWidth()
	 */
	@Override
	public Double getWidth() {
		return geometry.getWidth();
	}

	/**
	 * Method getHeight()
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getDepth()
	 */
	@Override
	public Double getHeight() {
		return geometry.getHeight();
	}

	/**
	 * Method getDepth()
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getDepth()
	 */
	@Override
	public Double getDepth() {
		return geometry.getDepth();
	}

	/**
	 * Method getGeometricEnvelope()
	 *
	 * @see msi.gama.metamodel.shape.IGeometricalShape#getGeometricEnvelope()
	 */
	@Override
	public GamaShape getGeometricEnvelope() {
		return geometry.getGeometricEnvelope();
	}

	@Override
	public IList<? extends IShape> getGeometries() {
		return geometry.getGeometries();
	}

	/**
	 * Method isMultiple()
	 *
	 * @see msi.gama.metamodel.shape.IShape#isMultiple()
	 */
	@Override
	public boolean isMultiple() {
		return geometry.isMultiple();
	}

	@Override
	public final int hashCode() {
		return hashCode;
	}
}
