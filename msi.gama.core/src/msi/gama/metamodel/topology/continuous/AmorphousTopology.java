/*********************************************************************************************
 *
 *
 * 'AmorphousTopology.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.continuous;

import java.awt.Graphics2D;
import java.util.*;
import com.vividsolutions.jts.geom.Geometry;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.path.*;
import msi.gaml.operators.*;
import msi.gaml.types.*;

/**
 * The class ExpandableTopology.
 *
 * @author drogoul
 * @since 2 d�c. 2011
 *
 */
public class AmorphousTopology implements ITopology {

	IShape expandableEnvironment = GamaGeometryType.createPoint(new GamaPoint(0, 0));
	ISpatialIndex index = new GamaQuadTree(expandableEnvironment.getEnvelope());

	/**
	 * @see msi.gama.interfaces.IValue#type()
	 */
	// @Override
	// public IType type() {
	// return Types.TOPOLOGY;
	// }

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "Expandable topology";
	}

	@Override
	public IType getType() {
		return Types.TOPOLOGY;
	}

	/**
	 * @see msi.gama.interfaces.IValue#toGaml()
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "topology({0,0})";
	}

	/**
	 * @see msi.gama.interfaces.IValue#copy()
	 */
	@Override
	public ITopology copy(final IScope scope) throws GamaRuntimeException {
		return new AmorphousTopology();
	}

	/**
	 * @see msi.gama.environment.ITopology#initialize(msi.gama.interfaces.IPopulation)
	 */
	@Override
	public void initialize(final IScope scope, final IPopulation pop) throws GamaRuntimeException {}

	/**
	 * @see msi.gama.environment.ITopology#updateAgent(msi.gama.interfaces.IAgent, boolean, msi.gama.util.GamaPoint, com.vividsolutions.jts.geom.Envelope)
	 */
	// @Override
	// public void updateAgent(final IAgent agent, final boolean previousShapeIsPoint,
	// final ILocation previousLoc, final Envelope previousEnv) {
	// IShape ng =
	// Spatial.Operators.union(expandableEnvironment.getGeometry(), agent.getGeometry());
	// expandableEnvironment.setGeometry(new GamaShape(ng.getInnerGeometry().getEnvelope()));
	// }
	//
	@Override
	public void updateAgent(final IShape previous, final IAgent agent) {
		final IShape ng =
			Spatial.Operators.union(agent.getScope(), expandableEnvironment.getGeometry(), agent.getGeometry());
		expandableEnvironment.setGeometry(new GamaShape(ng.getInnerGeometry().getEnvelope()));
	}

	/**
	 * @see msi.gama.environment.ITopology#removeAgent(msi.gama.interfaces.IAgent)
	 */
	@Override
	public void removeAgent(final IAgent agent) {}

	/**
	 * @see msi.gama.environment.ITopology#getAgentClosestTo(msi.gama.interfaces.IGeometry, msi.gama.environment.IAgentFilter)
	 */
	@Override
	public IAgent getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter) {
		return null;
	}

	/**
	 * @see msi.gama.environment.ITopology#getNeighboursOf(msi.gama.interfaces.IGeometry, java.lang.Double, msi.gama.environment.IAgentFilter)
	 */
	@Override
	public Set<IAgent> getNeighboursOf(final IScope scope, final IShape source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		return Collections.EMPTY_SET;
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsIn(msi.gama.interfaces.IGeometry, msi.gama.environment.IAgentFilter, boolean)
	 */
	@Override
	public Set<IAgent> getAgentsIn(final IScope scope, final IShape source, final IAgentFilter f,
		final boolean covered) {
		return Collections.EMPTY_SET;
	}

	/**
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Double distanceBetween(final IScope scope, final IShape source, final IShape target) {
		return source.euclidianDistanceTo(target);
	}

	@Override
	public Double distanceBetween(final IScope scope, final ILocation source, final ILocation target) {
		return source.euclidianDistanceTo(target);
	}

	/**
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target)
		throws GamaRuntimeException {
		// return new GamaPath(this, GamaList.with(source, target));
		return PathFactory.newInstance(scope, this,
			GamaListFactory.createWithoutCasting(Types.GEOMETRY, new IShape[] { source, target }));
	}

	/**
	 * @see msi.gama.environment.ITopology#getDestination(msi.gama.util.GamaPoint, int, double, boolean)
	 */
	@Override
	public ILocation getDestination(final ILocation source, final int direction, final double distance,
		final boolean nullIfOutside) {
		final double cos = distance * Maths.cos(direction);
		final double sin = distance * Maths.sin(direction);
		return new GamaPoint(source.getX() + cos, source.getY() + sin);

	}

	/**
	 * @see msi.gama.environment.ITopology#getDestination(msi.gama.util.GamaPoint, int, double, boolean)
	 */
	@Override
	public ILocation getDestination3D(final ILocation source, final int heading, final int pitch, final double distance,
		final boolean nullIfOutside) {
		final double x = distance * Maths.cos(pitch) * Maths.cos(heading);
		final double y = distance * Maths.cos(pitch) * Maths.sin(heading);
		final double z = distance * Maths.sin(pitch);
		return new GamaPoint(source.getX() + x, source.getY() + y, source.getZ() + z);
	}

	/**
	 * @see msi.gama.environment.ITopology#getRandomLocation()
	 */
	@Override
	public GamaPoint getRandomLocation(final IScope scope) {
		return new GamaPoint(scope.getRandom().next(), scope.getRandom().next());
	}

	/**
	 * @see msi.gama.environment.ITopology#getPlaces()
	 */
	@Override
	public IContainer<?, IShape> getPlaces() {
		IList result = GamaListFactory.create(Types.GEOMETRY);
		result.add(expandableEnvironment);
		return result;
	}

	/**
	 * @see msi.gama.environment.ITopology#getEnvironment()
	 */
	@Override
	public IShape getEnvironment() {
		return expandableEnvironment;
	}

	/**
	 * @see msi.gama.environment.ITopology#normalizeLocation(msi.gama.util.GamaPoint, boolean)
	 */
	@Override
	public ILocation normalizeLocation(final ILocation p, final boolean nullIfOutside) {
		return p;
	}

	/**
	 * @see msi.gama.environment.ITopology#shapeChanged(msi.gama.interfaces.IPopulation)
	 */
	// @Override
	// public void shapeChanged(final IPopulation pop) {}

	/**
	 * @see msi.gama.environment.ITopology#getWidth()
	 */
	@Override
	public double getWidth() {
		return expandableEnvironment.getEnvelope().getWidth();
	}

	/**
	 * @see msi.gama.environment.ITopology#getHeight()
	 */
	@Override
	public double getHeight() {
		return expandableEnvironment.getEnvelope().getHeight();
	}

	/**
	 * @see msi.gama.environment.ITopology#dispose()
	 */
	@Override
	public void dispose() {}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final IScope scope, final ILocation p) {
		return true;
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IScope scope, final IShape g) {
		return true;
	}

	/**
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(final IScope scope, final IShape g1, final IShape g2) {
		final ILocation source = g1.getLocation();
		final ILocation target = g2.getLocation();
		final double x2 = /* translateX(source.x, target.x); */target.getX();
		final double y2 = /* translateY(source.y, target.y); */target.getY();
		final double dx = x2 - source.getX();
		final double dy = y2 - source.getY();
		final double result = Maths.atan2(dy, dx);
		return Maths.checkHeading((int) result);
	}

	/**
	 * @see msi.gama.metamodel.topology.ITopology#getAgentClosestTo(msi.gama.metamodel.shape.ILocation, msi.gama.metamodel.topology.filter.IAgentFilter)
	 */
	// @Override
	// public IAgent getAgentClosestTo(final IScope scope, final ILocation source, final IAgentFilter filter) {
	// return null;
	// }

	/**
	 * @see msi.gama.metamodel.topology.ITopology#getNeighboursOf(msi.gama.metamodel.shape.ILocation, java.lang.Double, msi.gama.metamodel.topology.filter.IAgentFilter)
	 */
	// @Override
	// protected Iterator<IAgent> getNeighboursOf(final ILocation source, final Double distance, final IAgentFilter
	// filter)
	// throws GamaRuntimeException {
	// return Iterators.emptyIterator();
	// }

	/**
	 * @see msi.gama.metamodel.topology.ITopology#pathBetween(msi.gama.metamodel.shape.ILocation, msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target)
		throws GamaRuntimeException {
		// return new GamaPath(this, GamaList.with(source, target));
		return PathFactory.newInstance(scope, this,
			GamaListFactory.create(scope, Types.POINT, new IShape[] { source, target }));
	}

	@Override
	public List<Geometry> listToroidalGeometries(final Geometry geom) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean isTorus() {
		return false;
	}

	//
	// @Override
	// public GisUtils getGisUtils() {
	// return new GisUtils();
	// }

	@Override
	public ISpatialIndex getSpatialIndex() {
		return index;
	}

	@Override
	public void displaySpatialIndexOn(final Graphics2D g2, final int width, final int height) {}

	@Override
	public List<GamaSpatialPath> KpathsBetween(final IScope scope, final IShape source, final IShape target,
		final int k) {
		List<GamaSpatialPath> paths = GamaListFactory.create(Types.PATH);
		paths.add(pathBetween(scope, source, target));
		return paths;
	}

	@Override
	public List<GamaSpatialPath> KpathsBetween(final IScope scope, final ILocation source, final ILocation target,
		final int k) {
		List<GamaSpatialPath> paths = GamaListFactory.create(Types.PATH);
		paths.add(pathBetween(scope, source, target));
		return paths;
	}
}
