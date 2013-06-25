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
package msi.gama.metamodel.topology;

import java.awt.Graphics2D;
import java.util.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.continuous.ContinuousTopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.path.*;
import msi.gaml.operators.Maths;
import com.google.common.base.*;
import com.google.common.collect.Iterators;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;

public abstract class AbstractTopology implements ITopology {

	public static class RootTopology extends ContinuousTopology {

		public RootTopology(final IScope scope, final IShape gisGeom, final IShape geom, final boolean isTorus) {
			super(scope, geom);
			final Envelope env = gisGeom.getEnvelope();
			gis.init(env);
			// GamaPoint p = new GamaPoint(-1 * env.getMinX(), -1 * env.getMinY());
			// IShape geometry = Transformations.translated_by(scope, geom, p);
			final Envelope bounds = geom.getEnvelope();
			spatialIndex = new CompoundSpatialIndex(bounds);
			this.isTorus = isTorus;
		}

		private final ISpatialIndex.Compound spatialIndex;
		private final boolean isTorus;
		private final GisUtils gis = new GisUtils();

		@Override
		public ISpatialIndex getSpatialIndex() {
			return spatialIndex;
		}

		@Override
		public boolean isTorus() {
			return isTorus;
		}

		@Override
		protected void setRoot(final RootTopology root) {}

		public RootTopology getRoot() {
			return this;
		}

		//
		// @Override
		// public void setTorus(boolean torus) {
		// isTorus = torus;
		// }

		@Override
		public GisUtils getGisUtils() {
			return gis;
		}

		@Override
		public void displaySpatialIndexOn(final Graphics2D g2, final int width, final int height) {
			if ( spatialIndex == null ) { return; }
			spatialIndex.drawOn(g2, width, height);
		}

		@Override
		public void dispose() {
			super.dispose();
			if ( spatialIndex != null ) {
				spatialIndex.dispose();
			}
		}

	}

	protected IScope scope;
	protected IShape environment;
	protected RootTopology root;
	protected IContainer<?, IShape> places;
	protected double environmentWidth, environmentHeight;
	protected double environmentMinX, environmentMinY, environmentMaxX, environmentMaxY;

	// VARIABLES USED IN TORUS ENVIRONMENT
	protected double[] adjustedXVector;
	protected double[] adjustedYVector;

	public AbstractTopology(final IScope scope, final IShape env, final RootTopology root) {
		this.scope = scope.copy();
		setRoot(root);
		environment = env;
		setEnvironmentBounds();
		if ( isTorus() ) {
			createVirtualEnvironments();
		}
	}

	protected void setRoot(final RootTopology root) {
		this.root = (RootTopology) (root == null ? scope.getSimulationScope().getTopology() : root);
	}

	@Override
	public Geometry returnToroidalGeom(final Geometry geom) {
		final List<Geometry> geoms = new GamaList<Geometry>();
		geoms.add(geom);
		for ( int cnt = 0; cnt < 8; cnt++ ) {
			final AffineTransformation at = new AffineTransformation();
			at.translate(adjustedXVector[cnt], adjustedYVector[cnt]);
			geoms.add(at.transform(geom));
		}
		return GeometryUtils.factory.buildGeometry(geoms);
	}

	public Geometry returnToroidalGeom(final GamaPoint loc) {
		final List<Geometry> geoms = new GamaList<Geometry>();
		final Point pt = GeometryUtils.factory.createPoint(loc);
		geoms.add(pt);
		for ( int cnt = 0; cnt < 8; cnt++ ) {
			final AffineTransformation at = new AffineTransformation();
			at.translate(adjustedXVector[cnt], adjustedYVector[cnt]);
			geoms.add(at.transform(pt));
		}
		return GeometryUtils.factory.buildGeometry(geoms);
	}

	public Geometry returnToroidalGeom(final IShape shape) {
		if ( shape.isPoint() ) { return returnToroidalGeom((GamaPoint) shape.getLocation()); }
		return returnToroidalGeom(shape.getInnerGeometry());
	}

	public Map<Geometry, IAgent> toroidalGeoms(final Collection shps) {
		final Map<Geometry, IAgent> geoms = new GamaMap();
		for ( final IAgent ag : scope.getSimulationScope().getAgents() ) {
			geoms.put(returnToroidalGeom(ag.getGeometry().getInnerGeometry()), ag);
		}
		return geoms;
	}

	protected void createVirtualEnvironments() {
		adjustedXVector = new double[8];
		adjustedYVector = new double[8];
		final Envelope environmentEnvelope = environment.getEnvelope();

		// shape host has not yet been initialized
		// if ( environmentEnvelope == null ) {
		// steps = new double[] {};
		// return;
		// }
		environmentWidth = environmentEnvelope.getWidth();
		environmentHeight = environmentEnvelope.getHeight();
		environmentMinX = environmentEnvelope.getMinX();
		environmentMinY = environmentEnvelope.getMinY();
		environmentMaxX = environmentEnvelope.getMaxX();
		environmentMaxY = environmentEnvelope.getMaxY();

		// NORTH virtual environment
		adjustedXVector[0] = 0.0;
		adjustedYVector[0] = environmentHeight;

		// NORTH-WEST virtual environment
		adjustedXVector[1] = environmentWidth;
		adjustedYVector[1] = environmentHeight;

		// WEST virtual environment
		adjustedXVector[2] = environmentWidth;
		adjustedYVector[2] = 0.0;

		// SOUTH-WEST virtual environment
		adjustedXVector[3] = environmentWidth;
		adjustedYVector[3] = -environmentHeight;

		// SOUTH virtual environment
		adjustedXVector[4] = 0.0;
		adjustedYVector[4] = -environmentHeight;

		// SOUTH-EAST virtual environment
		adjustedXVector[5] = -environmentWidth;
		adjustedYVector[5] = -environmentHeight;

		// EAST virtual environment
		adjustedXVector[6] = -environmentWidth;
		adjustedYVector[6] = 0.0;

		// NORTH-EAST virtual environment
		adjustedXVector[7] = -environmentWidth;
		adjustedYVector[7] = environmentHeight;

	}

	protected boolean canCreateAgents() {
		return false;
	}

	/**
	 * @see msi.gama.environment.ITopology#initialize(msi.gama.interfaces.IPopulation)
	 */
	@Override
	public void initialize(final IPopulation pop) throws GamaRuntimeException {
		// Create the population from the places of the topology
		if ( !canCreateAgents() ) { return; }
		pop.createAgents(scope, places);

	}

	@Override
	public void removeAgent(final IAgent agent) {
		getSpatialIndex().remove(agent.getGeometry(), agent);
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target)
		throws GamaRuntimeException {
		// return new GamaPath(this, GamaList.with(source.getLocation(), target.getLocation()));
		return PathFactory.newInstance(this, GamaList.with(source.getLocation(), target.getLocation()));
	}

	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target)
		throws GamaRuntimeException {
		// return new GamaPath(this, GamaList.with(source, target));
		return PathFactory.newInstance(this, GamaList.with(source, target));
	}

	private void setEnvironmentBounds() {
		final Envelope environmentEnvelope = environment.getEnvelope();
		// The host has not yet initialized its shape and/or its shape is dependent on the
		// population that uses this topology
		if ( environmentEnvelope == null ) { return; }
		environmentWidth = environmentEnvelope.getWidth();
		environmentHeight = environmentEnvelope.getHeight();
		environmentMinX = environmentEnvelope.getMinX();
		environmentMinY = environmentEnvelope.getMinY();
		environmentMaxX = environmentEnvelope.getMaxX();
		environmentMaxY = environmentEnvelope.getMaxY();
	}

	@Override
	public void updateAgent(final IShape previous, final IShape agent) {
		if ( previous != null ) {
			getSpatialIndex().remove(previous, agent);
		}
		getSpatialIndex().insert(agent);
	}

	@Override
	public IShape getEnvironment() {
		return environment;
	}

	@Override
	public ILocation normalizeLocation(final ILocation point, final boolean nullIfOutside) {

		// TODO Subclass (or rewrite) this naive implementation to take care of irregular
		// geometries.

		// TODO Take into account the fact that some topologies may consider invalid locations.
		if ( environment.getGeometry().covers(point) ) { return point; }

		if ( isTorus() ) {
			final Point pt = GeometryUtils.factory.createPoint(point.toCoordinate());

			for ( int cnt = 0; cnt < 8; cnt++ ) {
				final AffineTransformation at = new AffineTransformation();
				at.translate(adjustedXVector[cnt], adjustedYVector[cnt]);
				final GamaPoint newPt = new GamaPoint(at.transform(pt).getCoordinate());
				if ( environment.getGeometry().covers(newPt) ) { return newPt; }
			}
		}
		// See if rounding errors of double do not interfere with the computation.
		// In which case, the use of Maths.approxEquals(value1, value2, tolerance) could help.

		// if ( envWidth == 0.0 ) {
		// xx = xx != envMinX ? nullIfOutside ? nil : envMinX : xx;
		// } else if ( xx < envMinX /* && xx > hostMinX - precision */) {
		// xx = /* !isTorus ? */nullIfOutside ? nil : envMinX /* : xx % envWidth + envWidth */;
		// } else if ( xx >= envMaxX /*- precision*/) {
		// xx = /* !isTorus ? */nullIfOutside ? nil : envMaxX /* : xx % envWidth */;
		// }
		// if ( xx == nil ) { return null; }
		// if ( envHeight == 0.0 ) {
		// yy = yy != envMinY ? nullIfOutside ? nil : envMinY : yy;
		// } else if ( yy < envMinY/* && yy > hostMinY - precision */) {
		// yy = /* !isTorus ? */nullIfOutside ? nil : envMinY /* : yy % envHeight + envHeight */;
		// } else if ( yy >= envMaxY /*- precision*/) {
		// yy = /* !isTorus ? */nullIfOutside ? nil : envMaxY /* : yy % envHeight */;
		// }
		// if ( yy == nil ) { return null; }
		// point.setLocation(xx, yy, point.getZ());

		return null;
	}

	@Override
	public ILocation getDestination(final ILocation source, final int direction, final double distance,
		final boolean nullIfOutside) {
		final double cos = distance * Maths.cos(direction);
		final double sin = distance * Maths.sin(direction);
		return normalizeLocation(new GamaPoint(source.getX() + cos, source.getY() + sin), nullIfOutside);
	}

	@Override
	public ITopology copy(final IScope scope) throws GamaRuntimeException {
		return _copy(scope);
	}

	@Override
	public String toGaml() {
		return _toGaml();
	}

	/**
	 * @return a gaml description of the construction of this topology.
	 */
	protected abstract String _toGaml();

	/**
	 * @throws GamaRuntimeException
	 * @return a copy of this topology
	 */
	protected abstract ITopology _copy(IScope scope) throws GamaRuntimeException;

	@Override
	public GamaPoint getRandomLocation() {
		// IGeometry g = getRandomPlace();
		// return GeometricFunctions.pointInGeom(g.getInnerGeometry(), GAMA.getRandom());
		// FIXME temporary restriction as places can evolve (since they are agents).
		return GeometryUtils.pointInGeom(environment.getInnerGeometry(), GAMA.getRandom());
	}

	@Override
	public IContainer<?, IShape> getPlaces() {
		return places;
	}

	@Override
	public IAgent getAgentClosestTo(final IShape source, final IAgentFilter filter) {
		IAgent result = null;
		if ( !isTorus() ) {
			final IShape min_neighbour = getSpatialIndex().firstAtDistance(source, 0, filter);
			if ( min_neighbour != null ) {
				result = min_neighbour.getAgent();
			}
		} else {
			final Geometry g0 = returnToroidalGeom(source.getGeometry());
			final Map<Geometry, IAgent> agents = getTororoidalAgents(filter);
			double distMin = Double.MAX_VALUE;
			for ( final Geometry g1 : agents.keySet() ) {
				final IAgent ag = agents.get(g1);
				if ( source.getAgent() != null && ag == source.getAgent() ) {
					continue;
				}
				final double dist = g0.distance(g1);
				if ( dist < distMin ) {
					distMin = dist;
					result = ag;
				}
			}
		}

		return result;
	}

	public Map<Geometry, IAgent> getTororoidalAgents(final IAgentFilter filter) {
		Collection shps;
		if ( filter != null ) {
			shps = filter.getShapes(scope);
		} else {
			shps = scope.getSimulationScope().getAgents();
		}
		return toroidalGeoms(shps);
	}

	@Override
	public IAgent getAgentClosestTo(final ILocation source, final IAgentFilter filter) {
		IAgent result = null;
		if ( !isTorus() ) {
			// for ( int i = 0; i < steps.length; i++ ) {
			final IShape min_neighbour = getSpatialIndex().firstAtDistance(source, 0, filter);
			if ( min_neighbour != null ) {
				result = min_neighbour.getAgent();
				// break;
				// }
			}
		} else {
			final Map<Geometry, IAgent> agents = getTororoidalAgents(filter);
			double distMin = Double.MAX_VALUE;
			final Geometry g0 = returnToroidalGeom(source);
			for ( final Geometry g1 : agents.keySet() ) {
				final IAgent ag = agents.get(g1);
				if ( source.getAgent() != null && ag == source.getAgent() ) {
					continue;
				}
				final double dist = g0.distance(g1);
				if ( dist < distMin ) {
					distMin = dist;
					result = ag;
				}
			}
		}
		return result;
	}

	@Override
	public Iterator<IAgent> getNeighboursOf(final IShape source, final Double distance, final IAgentFilter filter)
		throws GamaRuntimeException {
		// GuiUtils.debug("AbstractTopology.getNeighboursOf");
		if ( !isTorus() ) {
			final Iterator<IShape> shapes = getSpatialIndex().allAtDistance(source, distance, filter);
			return toAgents(shapes);
		}
		final Geometry g0 = returnToroidalGeom(source.getGeometry());
		final IList<IAgent> agents = new GamaList<IAgent>();
		final Map<Geometry, IAgent> agentsMap = getTororoidalAgents(filter);
		for ( final Geometry g1 : agentsMap.keySet() ) {
			final IAgent ag = agentsMap.get(g1);
			if ( source.getAgent() != null && ag == source.getAgent() ) {
				continue;
			}
			final double dist = g0.distance(g1);
			if ( dist <= distance ) {
				agents.add(ag);
			}
		}
		return agents.iterator();

	}

	@Override
	public Iterator<IAgent> getNeighboursOf(final ILocation source, final Double distance, final IAgentFilter filter)
		throws GamaRuntimeException {
		// GuiUtils.debug("AbstractTopology.getNeighboursOf");
		if ( !isTorus() ) {
			final Iterator<IShape> shapes = getSpatialIndex().allAtDistance(source, distance, filter);
			return toAgents(shapes);

		}
		IList<IAgent> agents;
		final Geometry g0 = returnToroidalGeom(source);
		agents = new GamaList<IAgent>();
		final Map<Geometry, IAgent> agentsMap = getTororoidalAgents(filter);
		for ( final Geometry g1 : agentsMap.keySet() ) {
			final IAgent ag = agentsMap.get(g1);
			if ( source.getAgent() != null && ag == source.getAgent() ) {
				continue;
			}
			final double dist = g0.distance(g1);
			if ( dist <= distance ) {
				agents.add(ag);
			}
		}
		return agents.iterator();

	}

	@Override
	public double getWidth() {
		return environmentWidth;
	}

	@Override
	public double getHeight() {
		return environmentHeight;
	}

	@Override
	public void shapeChanged(final IPopulation pop) {
		setEnvironmentBounds();
		// TODO CHANGE THIS
		final Iterator<IAgent> it = pop.iterator();
		while (it.hasNext()) {
			it.next().hostChangesShape();
		}
	}

	@Override
	public void dispose() {
		// host = null;
		scope = null;
	}

	protected static class IsAgent implements Predicate<IShape> {

		@Override
		public boolean apply(final IShape input) {
			final IAgent a = input.getAgent();
			return a != null && !a.dead();
		}

	}

	protected static class GetAgent implements Function<IShape, IAgent> {

		@Override
		public IAgent apply(final IShape input) {
			return input.getAgent();
		}

	}

	public static Iterator<IAgent> toAgents(final Iterator<IShape> shapes) {
		// GuiUtils.debug("AbstractTopology.toAgents");
		return Iterators.transform(Iterators.filter(shapes, isAgent), getAgent);
	}

	protected static Predicate<IShape> isAgent = new IsAgent();
	protected static Function<IShape, IAgent> getAgent = new GetAgent();

	private final PreparedGeometryFactory pgFact = new PreparedGeometryFactory();

	@Override
	public Iterator<IAgent> getAgentsIn(final IShape source, final IAgentFilter f, final boolean covered) {
		// GuiUtils.debug("AbstractTopology.getAgentsIn");
		// if ( !isValidGeometry(source) ) { return GamaList.EMPTY_LIST; }
		if ( source == null ) { return Iterators.emptyIterator(); }
		if ( !isTorus() ) {
			final Envelope envelope = source.getEnvelope().intersection(environment.getEnvelope());
			final Iterator<IAgent> shapes = toAgents(getSpatialIndex().allInEnvelope(source, envelope, f, covered));
			final PreparedGeometry pg = pgFact.create(source.getInnerGeometry());
			return Iterators.<IAgent> filter(shapes, new Predicate<IAgent>() {

				@Override
				public boolean apply(final IAgent input) {
					final Geometry geom = input.getInnerGeometry();
					return covered ? pg.covers(geom) : pg.intersects(geom);

				}
			});
		}
		final Geometry sourceTo = returnToroidalGeom(source);
		final PreparedGeometry pg = pgFact.create(sourceTo);
		final Map<Geometry, IAgent> agentsMap = getTororoidalAgents(f);
		final GamaList<IAgent> result = new GamaList();
		for ( final Geometry sh : agentsMap.keySet() ) {
			final IAgent ag = agentsMap.get(sh);
			if ( ag != null && !ag.dead() ) {
				if ( source.getAgent() != null && ag == source.getAgent() ) {
					continue;
				}
				final Geometry geom = ag.getInnerGeometry();
				if ( covered ? pg.covers(geom) : pg.intersects(geom) ) {
					result.add(ag);
				}
			}
		}
		return result.iterator();

	}

	@Override
	public ISpatialIndex getSpatialIndex() {
		return root.getSpatialIndex();
	}

	@Override
	public boolean isTorus() {
		return root.isTorus();
	}

	@Override
	public GisUtils getGisUtils() {
		return root.getGisUtils();
	}

	@Override
	public void displaySpatialIndexOn(final Graphics2D g2, final int width, final int height) {
		root.displaySpatialIndexOn(g2, width, height);
	}

}
