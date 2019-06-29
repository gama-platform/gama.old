/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.AbstractTopology.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.geom.util.AffineTransformation;

import com.google.common.collect.Ordering;

import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.JavaUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.continuous.RootTopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.path.GamaSpatialPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.operators.Maths;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public abstract class AbstractTopology implements ITopology {

	@Override
	public IType<?> getGamlType() {
		return Types.TOPOLOGY;
	}

	protected IShape environment;
	protected RootTopology root;
	protected IContainer<?, IShape> places;
	protected List<ISpecies> speciesInserted;

	// VARIABLES USED IN TORUS ENVIRONMENT
	private double[][] adjustedXYVector = null;

	public AbstractTopology(final IScope scope, final IShape env, final RootTopology root) {
		setRoot(scope, root);
		speciesInserted = new ArrayList<>();
		environment = env;
	}

	@Override
	public void setRoot(final IScope scope, final RootTopology root) {
		this.root = root == null ? scope.getSimulation().getTopology() : root;
	}

	@Override
	public List<Geometry> listToroidalGeometries(final Geometry geom) {
		final Geometry copy = geom.copy();
		final List<Geometry> geoms = new ArrayList<>();
		final AffineTransformation at = new AffineTransformation();
		geoms.add(copy);
		for (int cnt = 0; cnt < 8; cnt++) {
			at.setToTranslation(getAdjustedXYVector()[cnt][0], getAdjustedXYVector()[cnt][1]);
			geoms.add(at.transform(copy));
		}
		return geoms;
	}

	public Geometry returnToroidalGeom(final GamaPoint loc) {
		final List<Geometry> geoms = new ArrayList<>();
		final Point pt = GeometryUtils.GEOMETRY_FACTORY.createPoint(loc);
		final AffineTransformation at = new AffineTransformation();
		geoms.add(pt);
		for (int cnt = 0; cnt < 8; cnt++) {
			at.setToTranslation(getAdjustedXYVector()[cnt][0], getAdjustedXYVector()[cnt][1]);
			geoms.add(at.transform(pt));
		}
		return GeometryUtils.GEOMETRY_FACTORY.buildGeometry(geoms);
	}

	public Geometry returnToroidalGeom(final IShape shape) {
		if (shape.isPoint()) { return returnToroidalGeom(shape.getLocation()); }
		return GeometryUtils.GEOMETRY_FACTORY.buildGeometry(listToroidalGeometries(shape.getInnerGeometry()));
	}

	public Map<Geometry, IAgent> toroidalGeoms(final IScope scope, final IContainer<?, ? extends IShape> shps) {
		final Map<Geometry, IAgent> geoms = new TOrderedHashMap<>();
		for (final IShape ag : shps.iterable(scope)) {
			final IAgent agent = ag.getAgent();
			if (agent != null) {
				geoms.put(GeometryUtils.GEOMETRY_FACTORY
						.buildGeometry(listToroidalGeometries(agent.getGeometry().getInnerGeometry())), agent);
			}
		}
		return geoms;
	}

	protected void createVirtualEnvironments() {
		adjustedXYVector = new double[8][2];
		final IEnvelope environmentEnvelope = environment.getEnvelope();

		final double environmentWidth = environmentEnvelope.getEnvWidth();
		final double environmentHeight = environmentEnvelope.getEnvHeight();

		// NORTH virtual environment
		adjustedXYVector[0][0] = 0.0;
		adjustedXYVector[0][1] = environmentHeight;

		// NORTH-WEST virtual environment
		adjustedXYVector[1][0] = environmentWidth;
		adjustedXYVector[1][1] = environmentHeight;

		// WEST virtual environment
		adjustedXYVector[2][0] = environmentWidth;
		adjustedXYVector[2][1] = 0.0;

		// SOUTH-WEST virtual environment
		adjustedXYVector[3][0] = environmentWidth;
		adjustedXYVector[3][1] = -environmentHeight;

		// SOUTH virtual environment
		adjustedXYVector[4][0] = 0.0;
		adjustedXYVector[4][1] = -environmentHeight;

		// SOUTH-EAST virtual environment
		adjustedXYVector[5][0] = -environmentWidth;
		adjustedXYVector[5][1] = -environmentHeight;

		// EAST virtual environment
		adjustedXYVector[6][0] = -environmentWidth;
		adjustedXYVector[6][1] = 0.0;

		// NORTH-EAST virtual environment
		adjustedXYVector[7][0] = -environmentWidth;
		adjustedXYVector[7][1] = environmentHeight;

	}

	protected boolean canCreateAgents() {
		return false;
	}

	/**
	 * @see msi.gama.environment.ITopology#initialize(msi.gama.interfaces.IPopulation)
	 */
	@Override
	public void initialize(final IScope scope, final IPopulation<? extends IAgent> pop) throws GamaRuntimeException {
		// Create the population from the places of the topology
		if (!canCreateAgents()) { return; }
		pop.createAgents(scope, places);

	}

	@Override
	public void removeAgent(final IAgent agent) {
		getSpatialIndex().remove(agent.getEnvelope(), agent);
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target)
			throws GamaRuntimeException {
		return PathFactory.newInstance(scope, this,
				GamaListFactory.create(scope, Types.POINT, new IShape[] { source.getLocation(), target.getLocation() }),
				0.0);
	}

	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final GamaPoint source, final GamaPoint target)
			throws GamaRuntimeException {
		return PathFactory.newInstance(scope, this, GamaListFactory.createWithoutCasting(Types.POINT, source, target),
				0.0);
	}

	@Override
	public IList<GamaSpatialPath> KpathsBetween(final IScope scope, final IShape source, final IShape target,
			final int k) {
		final IList<GamaSpatialPath> paths = GamaListFactory.create(Types.PATH);
		paths.add(pathBetween(scope, source, target));
		return paths;
	}

	@Override
	public IList<GamaSpatialPath> KpathsBetween(final IScope scope, final GamaPoint source, final GamaPoint target,
			final int k) {
		final IList<GamaSpatialPath> paths = GamaListFactory.create(Types.PATH);
		paths.add(pathBetween(scope, source, target));
		return paths;
	}

	@Override
	public void updateAgent(final IEnvelope previous, final IAgent agent) {
		if (GamaPreferences.External.QUADTREE_OPTIMIZATION.getValue()) {
			if (speciesInserted.contains(agent.getSpecies())) {
				updateAgentBase(previous, agent);
			}
		} else {
			updateAgentBase(previous, agent);
		}
	}

	public void updateAgentBase(final IEnvelope previous, final IAgent agent) {
		if (previous != null && !previous.isNull()) {
			getSpatialIndex().remove(previous, agent);
		}
		getSpatialIndex().insert(agent);
	}

	@Override
	public IShape getEnvironment() {
		return environment;
	}

	@Override
	public GamaPoint normalizeLocation(final GamaPoint point, final boolean nullIfOutside) {

		// TODO Subclass (or rewrite) this naive implementation to take care of
		// irregular
		// geometries.

		// TODO Take into account the fact that some topologies may consider
		// invalid locations.
		if (environment.getGeometry().covers(point)) { return point; }

		if (isTorus()) {
			final Point pt = GeometryUtils.GEOMETRY_FACTORY.createPoint(point);

			for (int cnt = 0; cnt < 8; cnt++) {
				final AffineTransformation at = new AffineTransformation();
				at.translate(getAdjustedXYVector()[cnt][0], getAdjustedXYVector()[cnt][1]);
				final GamaPoint newPt = GamaPoint.create(at.transform(pt).getCoordinate());
				if (environment.getGeometry().covers(newPt)) { return newPt; }
			}
		}
		// See if rounding errors of double do not interfere with the
		// computation.
		// In which case, the use of Maths.approxEquals(value1, value2,
		// tolerance) could help.

		// if ( envWidth == 0.0 ) {
		// xx = xx != envMinX ? nullIfOutside ? nil : envMinX : xx;
		// } else if ( xx < envMinX /* && xx > hostMinX - precision */) {
		// xx = /* !isTorus ? */nullIfOutside ? nil : envMinX /* : xx % envWidth
		// + envWidth */;
		// } else if ( xx >= envMaxX /*- precision*/) {
		// xx = /* !isTorus ? */nullIfOutside ? nil : envMaxX /* : xx % envWidth
		// */;
		// }
		// if ( xx == nil ) { return null; }
		// if ( envHeight == 0.0 ) {
		// yy = yy != envMinY ? nullIfOutside ? nil : envMinY : yy;
		// } else if ( yy < envMinY/* && yy > hostMinY - precision */) {
		// yy = /* !isTorus ? */nullIfOutside ? nil : envMinY /* : yy %
		// envHeight + envHeight */;
		// } else if ( yy >= envMaxY /*- precision*/) {
		// yy = /* !isTorus ? */nullIfOutside ? nil : envMaxY /* : yy %
		// envHeight */;
		// }
		// if ( yy == nil ) { return null; }
		// point.setLocation(xx, yy, point.getZ());

		return null;
	}

	@Override
	public GamaPoint getDestination(final GamaPoint source, final double direction, final double distance,
			final boolean nullIfOutside) {
		final double cos = distance * Maths.cos(direction);
		final double sin = distance * Maths.sin(direction);
		final GamaPoint result = source.plus(cos, sin, 0);
		return normalizeLocation(result, nullIfOutside);
	}

	@Override
	public GamaPoint getDestination3D(final GamaPoint source, final double heading, final double pitch,
			final double distance, final boolean nullIfOutside) {
		final double x = distance * Maths.cos(pitch) * Maths.cos(heading);
		final double y = distance * Maths.cos(pitch) * Maths.sin(heading);
		final double z = distance * Maths.sin(pitch);
		return normalizeLocation3D(GamaPoint.create(source.getX() + x, source.getY() + y, source.getZ() + z),
				nullIfOutside);
	}

	public GamaPoint normalizeLocation3D(final GamaPoint point, final boolean nullIfOutside) {
		final GamaPoint p = normalizeLocation(point, nullIfOutside);
		if (p == null) { return null; }
		final double z = p.getZ();
		if (z < 0) { return null; }
		if (((GamaShape) environment.getGeometry()).getDepth() != null) {
			if (z > ((GamaShape) environment.getGeometry()).getDepth()) { return null; }
			return point;
		}
		throw GamaRuntimeException.error("The environment must be a 3D environment (e.g shape <- cube(100).", null);

	}

	@Override
	public ITopology copy(final IScope scope) throws GamaRuntimeException {
		return _copy(scope);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return _toGaml(includingBuiltIn);
	}

	/**
	 * @return a gaml description of the construction of this topology.
	 */
	protected abstract String _toGaml(boolean includingBuiltIn);

	/**
	 * @throws GamaRuntimeException
	 * @return a copy of this topology
	 */
	protected abstract ITopology _copy(IScope scope) throws GamaRuntimeException;

	@Override
	public GamaPoint getRandomLocation(final IScope scope) {
		return GeometryUtils.pointInGeom(environment, scope.getRandom());
	}

	@Override
	public IContainer<?, IShape> getPlaces() {
		return places;
	}

	protected void insertSpecies(final IScope scope, final ISpecies species) {
		if (!this.speciesInserted.contains(species)) {
			this.speciesInserted.add(species);
			for (final IAgent ag : species.getPopulation(scope)) {
				getSpatialIndex().insert(ag);
			}
		}
	}

	protected void insertAgents(final IScope scope, final IAgentFilter filter) {
		if (GamaPreferences.External.QUADTREE_OPTIMIZATION.getValue()) {
			if (filter.getSpecies() != null) {
				insertSpecies(scope, filter.getSpecies());
			} else {
				for (final IAgent ag : filter.getPopulation(scope)) {
					insertSpecies(scope, ag.getSpecies());
				}
			}
		}
	}

	@Override
	public Collection<IAgent> getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter,
			final int number) {
		insertAgents(scope, filter);
		if (!isTorus()) {
			final Set<IAgent> agents = JavaUtils.SET_POOL.get();
			final Ordering<IShape> ordering = Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input));
			try {
				getSpatialIndex().allAtDistance(scope, source, 0, filter, agents);
				return ordering.leastOf(agents, number);
			} finally {
				JavaUtils.SET_POOL.release(agents);
			}
		}
		final Geometry g0 = returnToroidalGeom(source.getGeometry());
		final Map<Geometry, IAgent> agents = getTororoidalAgents(source, scope, filter);
		agents.remove(g0);
		if (agents.size() <= number) { return agents.values(); }
		final List<Geometry> ggs = new ArrayList<>(agents.keySet());
		scope.getRandom().shuffleInPlace(ggs);
		final Ordering<Geometry> ordering = Ordering.natural().onResultOf(input -> g0.distance(input));
		final IList<IAgent> shapes = GamaListFactory.create(Types.AGENT);
		for (final Geometry g : ordering.leastOf(ggs, number)) {
			shapes.add(agents.get(g));
		}
		return shapes;
	}

	@Override
	public IAgent getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter) {
		insertAgents(scope, filter);
		if (!isTorus()) { return getSpatialIndex().firstAtDistance(scope, source, 0, filter); }
		IAgent result = null;
		final Geometry g0 = returnToroidalGeom(source.getGeometry());
		final Map<Geometry, IAgent> agents = getTororoidalAgents(source, scope, filter);

		double distMin = Double.MAX_VALUE;
		for (final Geometry g1 : agents.keySet()) {
			final IAgent ag = agents.get(g1);
			if (source.getAgent() != null && ag == source.getAgent()) {
				continue;
			}
			final double dist = g0.distance(g1);
			if (dist < distMin) {
				distMin = dist;
				result = ag;
			}
		}
		return result;
	}

	@Override
	public IAgent getAgentFarthestTo(final IScope scope, final IShape source, final IAgentFilter filter) {
		if (!isTorus()) {
			IAgent result = null;
			double distMax = Double.MIN_VALUE;
			final IContainer<?, ? extends IShape> agents = getFilteredAgents(source, scope, filter);
			for (final IShape s : agents.iterable(scope)) {
				if (s instanceof IAgent) {
					final double dist = this.distanceBetween(scope, source, s);
					if (dist > distMax) {
						result = (IAgent) s;
						distMax = dist;
					}
				}
			}
			return result;
		}
		IAgent result = null;
		final Geometry g0 = returnToroidalGeom(source);
		final Map<Geometry, IAgent> agents = getTororoidalAgents(source, scope, filter);
		double distMax = Double.MIN_VALUE;
		for (final Geometry g1 : agents.keySet()) {
			final IAgent ag = agents.get(g1);
			if (source.getAgent() != null && ag == source.getAgent()) {
				continue;
			}
			final double dist = g0.distance(g1);
			if (dist > distMax) {
				distMax = dist;
				result = ag;
			}
		}
		return result;
	}

	public Map<Geometry, IAgent> getTororoidalAgents(final IShape source, final IScope scope,
			final IAgentFilter filter) {
		return toroidalGeoms(scope, getFilteredAgents(source, scope, filter));
	}

	@SuppressWarnings ("unchecked")
	public static IContainer<?, ? extends IShape> getFilteredAgents(final IShape source, final IScope scope,
			final IAgentFilter filter) {
		IContainer<?, ? extends IShape> shps;
		if (filter != null) {
			if (filter.hasAgentList()) {
				shps = filter.getAgents(scope);
			} else {
				shps = scope.getSimulation().getAgents(scope);
				filter.filter(scope, source, (Collection<? extends IShape>) shps);
			}
		} else {
			shps = scope.getSimulation().getAgents(scope);
		}
		return shps;
	}

	@Override
	public Collection<IAgent> getNeighborsOf(final IScope scope, final IShape source, final Double distance,
			final IAgentFilter filter) throws GamaRuntimeException {
		insertAgents(scope, filter);
		final Set<IAgent> agents = new THashSet<>();

		if (!isTorus()) {
			getSpatialIndex().allAtDistance(scope, source, distance, filter, agents);
		} else {

			// FOR TORUS ENVIRONMENTS ONLY

			final Geometry g0 = returnToroidalGeom(source.getGeometry());

			final Map<Geometry, IAgent> agentsMap = getTororoidalAgents(source, scope, filter);
			final IAgent sourceAgent = source.getAgent();
			for (final Geometry g1 : agentsMap.keySet()) {
				final IAgent ag = agentsMap.get(g1);
				if (sourceAgent != null && ag == sourceAgent) {
					continue;
				}
				final double dist = g0.distance(g1);
				if (dist <= distance) {
					agents.add(ag);
				}
			}
		}
		return agents;

	}

	@Override
	public double getWidth() {
		return environment.getEnvelope().getEnvWidth();
	}

	@Override
	public double getHeight() {
		return environment.getEnvelope().getEnvHeight();
	}

	@Override
	public void dispose() {
		// host = null;
		// scope = null;
	}

	private final PreparedGeometryFactory pgFact = new PreparedGeometryFactory();

	@Override
	public Collection<IAgent> getAgentsIn(final IScope scope, final IShape source, final IAgentFilter f,
			final boolean covered) {
		if (source == null) { return Collections.EMPTY_SET; }
		insertAgents(scope, f);
		if (!isTorus()) {
			final Envelope3D envelope = source.getEnvelope().intersection(environment.getEnvelope());
			final Set<IAgent> shapes = new TLinkedHashSet<>();
			getSpatialIndex().allInEnvelope(scope, source, envelope, f, covered, shapes);
			final PreparedGeometry pg = pgFact.create(source.getInnerGeometry());
			shapes.removeIf(each -> {
				if (each.dead()) { return true; }
				final Geometry geom = each.getInnerGeometry();
				return !(covered ? pg.covers(geom) : pg.intersects(geom));
			});
			return shapes;
		}
		final Set<IAgent> result = new THashSet<>();

		for (final IShape sourceSub : source.getGeometries()) {
			final Geometry sourceTo = returnToroidalGeom(sourceSub);
			final PreparedGeometry pg = pgFact.create(sourceTo);
			final Map<Geometry, IAgent> agentsMap = getTororoidalAgents(source, scope, f);
			for (final Geometry sh : agentsMap.keySet()) {
				final IAgent ag = agentsMap.get(sh);
				if (ag != null && !ag.dead()) {
					if (source.getAgent() != null && ag == source.getAgent()) {
						continue;
					}
					final Geometry geom = ag.getInnerGeometry();

					if (covered ? pg.covers(geom) : pg.intersects(geom)) {
						result.add(ag);
					}
				}
			}
		}
		return result;
	}

	@Override
	public ISpatialIndex getSpatialIndex() {
		return root.getSpatialIndex();
	}

	@Override
	public boolean isTorus() {
		return root.isTorus();
	}

	protected double[][] getAdjustedXYVector() {
		if (adjustedXYVector == null) {
			createVirtualEnvironments();
		}
		return adjustedXYVector;
	}

}
