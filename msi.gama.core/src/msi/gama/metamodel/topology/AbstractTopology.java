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

import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Maths;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;

public abstract class AbstractTopology implements ITopology {

	protected IScope scope;
	protected IShape environment;
	protected ISpatialIndex spatialIndex;
	protected IContainer<?, IShape> places;
	protected double environmentWidth, environmentHeight;
	protected double environmentMinX, environmentMinY, environmentMaxX, environmentMaxY;
	protected double[] steps;
	protected Boolean isTorus = false; 
	
	public AbstractTopology(final IScope scope, final IShape env, final Boolean isTorus) {
		environment = env;
		this.scope = scope;
		setEnvironmentBounds();
		spatialIndex =
			scope.getSimulationScope().getModel().getModelEnvironment().getSpatialIndex();
		this.isTorus = isTorus;
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
		final IShape g = agent.getGeometry();
		if ( g == null ) { return; }
		if ( g.isPoint() ) {
			spatialIndex.remove((GamaPoint) g.getLocation(), agent);
		} else {
			spatialIndex.remove(g.getEnvelope(), agent);
		}
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public IPath pathBetween(final IShape source, final IShape target) throws GamaRuntimeException {
		return new GamaPath(this, GamaList.with(source.getLocation(), target.getLocation()));
	}

	@Override
	public IPath pathBetween(final ILocation source, final ILocation target)
		throws GamaRuntimeException {
		return new GamaPath(this, GamaList.with(source, target));
	}

	private void setEnvironmentBounds() {
		Envelope environmentEnvelope = environment.getEnvelope();

		// shape host has not yet been initialized
		if ( environmentEnvelope == null ) {
			steps = new double[] {};
			return;
		}

		environmentWidth = environmentEnvelope.getWidth();
		environmentHeight = environmentEnvelope.getHeight();
		environmentMinX = environmentEnvelope.getMinX();
		environmentMinY = environmentEnvelope.getMinY();
		environmentMaxX = environmentEnvelope.getMaxX();
		environmentMaxY = environmentEnvelope.getMaxY();
		double biggest = Math.max(environmentWidth, environmentHeight);
		steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest };
	}

	@Override
	public void updateAgent(final IAgent agent, final boolean previousShapeIsPoint,
		final ILocation previousLoc, final Envelope previousEnv) {
		if ( previousShapeIsPoint && previousLoc != null ) {
			spatialIndex.remove((GamaPoint) previousLoc, agent);
		} else if ( !previousShapeIsPoint && previousEnv != null ) {
			spatialIndex.remove(previousEnv, agent);
		}

		IShape currentShape = agent.getGeometry();
		if ( currentShape == null || currentShape.isPoint() ) {
			spatialIndex.insert((GamaPoint) agent.getLocation(), agent);
		} else {
			spatialIndex.insert(currentShape.getEnvelope(), agent);
		}
	}

	@Override
	public IShape getEnvironment() {
		return environment;
	}

	@Override
	public ILocation normalizeLocation(final ILocation point, final boolean nullIfOutside) {
		/* localized for a bit more efficiency */

		// TODO Subclass (or rewrite) this naive implementation to take care of irregular
		// geometries.

		// TODO Take into account the fact that some topologies may consider invalid locations.

		double nil = Double.MAX_VALUE;
		double xx = point.getX();
		double yy = point.getY();
		double envMinX = this.environmentMinX;
		double envMinY = this.environmentMinY;
		double envMaxX = this.environmentMaxX;
		double envMaxY = this.environmentMaxY;
		double envWidth = this.environmentWidth;
		double envHeight = this.environmentHeight;

		// See if rounding errors of double do not interfere with the computation.
		// In which case, the use of Maths.approxEquals(value1, value2, tolerance) could help.

		if ( envWidth == 0.0 ) {
			xx = xx != envMinX ? nullIfOutside ? nil : envMinX : xx;
		} else if ( xx < envMinX /* && xx > hostMinX - precision */) {
			xx = /* !isTorus ? */nullIfOutside ? nil : envMinX /* : xx % envWidth + envWidth */;
		} else if ( xx >= envMaxX /*- precision*/) {
			xx = /* !isTorus ? */nullIfOutside ? nil : envMaxX /* : xx % envWidth */;
		}
		if ( xx == nil ) { return null; }
		if ( envHeight == 0.0 ) {
			yy = yy != envMinY ? nullIfOutside ? nil : envMinY : yy;
		} else if ( yy < envMinY/* && yy > hostMinY - precision */) {
			yy = /* !isTorus ? */nullIfOutside ? nil : envMinY /* : yy % envHeight + envHeight */;
		} else if ( yy >= envMaxY /*- precision*/) {
			yy = /* !isTorus ? */nullIfOutside ? nil : envMaxY /* : yy % envHeight */;
		}
		if ( yy == nil ) { return null; }
		point.setLocation(xx, yy, point.getZ());
		if ( environment.getGeometry().covers(point) ) { return point; }
		return null;
	}

	@Override
	public ILocation getDestination(final ILocation source, final int direction,
		final double distance, final boolean nullIfOutside) {
		double cos = distance * Maths.cos(direction);
		double sin = distance * Maths.sin(direction);
		return normalizeLocation(new GamaPoint(source.getX() + cos, source.getY() + sin),
			nullIfOutside);
	}

	@Override
	public ITopology copy() throws GamaRuntimeException {
		return _copy();
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
	protected abstract ITopology _copy() throws GamaRuntimeException;

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
		for ( int i = 0; i < steps.length; i++ ) {
			IShape min_neighbour = spatialIndex.firstAtDistance(source, steps[i], filter);
			if ( min_neighbour != null ) {
				result = min_neighbour.getAgent();
				break;
			}
		}
		return result;
	}

	@Override
	public IAgent getAgentClosestTo(final ILocation source, final IAgentFilter filter) {
		IAgent result = null;
		for ( int i = 0; i < steps.length; i++ ) {
			IShape min_neighbour = spatialIndex.firstAtDistance(source, steps[i], filter);
			if ( min_neighbour != null ) {
				result = min_neighbour.getAgent();
				break;
			}
		}
		return result;
	}

	@Override
	public IList<IAgent> getNeighboursOf(final IShape source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		IList<IShape> shapes = spatialIndex.allAtDistance(source, distance, filter);
		IList<IAgent> agents = new GamaList(shapes.size());
		for ( IShape s : shapes ) {
			IAgent a = s.getAgent();
			if ( a != null && !a.dead() ) {
				agents.add(a);
			}
		}
		return agents;
	}

	@Override
	public IList<IAgent> getNeighboursOf(final ILocation source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		IList<IShape> shapes = spatialIndex.allAtDistance(source, distance, filter);
		IList<IAgent> agents = new GamaList(shapes.size());
		for ( IShape s : shapes ) {
			IAgent a = s.getAgent();
			if ( a != null && !a.dead() ) {
				agents.add(a);
			}
		}
		return agents;
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
		for ( IAgent a : pop.getAgentsList() ) {
			a.hostChangesShape();
		}
	}

	@Override
	public void dispose() {
		// host = null;
		spatialIndex = null;
		scope = null;
	}

	/**
	 * @see msi.gama.interfaces.IValue#type()
	 */
	@Override
	public IType type() {
		return Types.get(IType.TOPOLOGY);
	}

	private static PreparedGeometryFactory pgFact = new PreparedGeometryFactory();

	/**
	 * @see msi.gama.environment.ITopology#getAgentsIn(msi.gama.interfaces.IGeometry,
	 *      msi.gama.environment.IAgentFilter, boolean)
	 */
	// @Override
	// @Override
	public IList<IAgent> getAgentsInOld(final IShape source, final IAgentFilter f,
		final boolean covered) {
		GamaList<IAgent> result = new GamaList();
		if ( !isValidGeometry(source) ) { return result; }
		Envelope envelope = source.getEnvelope().intersection(environment.getEnvelope());
		IList<IShape> shapes = spatialIndex.allInEnvelope(source, envelope, f, covered);
		PreparedGeometry pg = pgFact.create(source.getInnerGeometry());
		PreparedGeometry penv = pgFact.create(environment.getInnerGeometry());
		for ( IShape sh : shapes ) {
			IAgent ag = sh.getAgent();
			// Geometry g = ag.getInnerGeometry();
			if ( ag != null && !ag.dead() ) {
				Geometry geom = ag.getInnerGeometry();
				if ( covered ? pg.covers(geom) && penv.covers(geom) : pg.intersects(geom) &&
					penv.intersects(geom) ) {
					result.add(ag);
				}
			}
		}
		return result;
	}

	// @Override
	@Override
	public IList<IAgent> getAgentsIn(final IShape source, final IAgentFilter f,
		final boolean covered) {
		// if ( !isValidGeometry(source) ) { return GamaList.EMPTY_LIST; }
		if (source == null)  { return GamaList.EMPTY_LIST; }
		Envelope envelope = source.getEnvelope().intersection(environment.getEnvelope());
		IList<IShape> shapes = spatialIndex.allInEnvelope(source, envelope, f, covered);
		PreparedGeometry pg = pgFact.create(source.getInnerGeometry());
		GamaList<IAgent> result = new GamaList(shapes.size());
		for ( IShape sh : shapes ) {
			IAgent ag = sh.getAgent();
			if ( ag != null && !ag.dead() ) {
				Geometry geom = ag.getInnerGeometry();
				if ( covered ? pg.covers(geom) : pg.intersects(geom) ) {
					result.add(ag);
				}
			}
		}
		return result;
	}

}
