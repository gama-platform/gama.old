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

import java.util.*;
import msi.gama.common.util.*;
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
import com.vividsolutions.jts.geom.util.AffineTransformation;

public abstract class AbstractTopology implements ITopology {

	protected IScope scope;
	protected IShape environment;
	protected ISpatialIndex.Compound spatialIndex;
	protected IContainer<?, IShape> places;
	protected double environmentWidth, environmentHeight;
	protected double environmentMinX, environmentMinY, environmentMaxX, environmentMaxY;

	protected Boolean isTorus = false;

	// VARIABLES USED IN TORUS ENVIRONMENT
	protected double[] adjustedXVector;
	protected double[] adjustedYVector;

	public AbstractTopology(final IScope scope, final IShape env, final Boolean isTorus) {
		environment = env;
		this.scope = scope;
		setEnvironmentBounds();
		spatialIndex =
			scope.getSimulationScope().getModel().getModelEnvironment().getSpatialIndex();
		this.isTorus = isTorus;
		if ( isTorus() ) {
			createVirtualEnvironments();
		}
	}

	@Override
	public Geometry returnToroidalGeom(final Geometry geom) {
		List<Geometry> geoms = new GamaList<Geometry>();
		geoms.add(geom);
		for ( int cnt = 0; cnt < 8; cnt++ ) {
			AffineTransformation at = new AffineTransformation();
			at.translate(adjustedXVector[cnt], adjustedYVector[cnt]);
			geoms.add(at.transform(geom));
		}
		return GeometryUtils.factory.buildGeometry(geoms);
	}

	public Geometry returnToroidalGeom(final GamaPoint loc) {
		List<Geometry> geoms = new GamaList<Geometry>();
		Point pt = GeometryUtils.factory.createPoint(loc);
		geoms.add(pt);
		for ( int cnt = 0; cnt < 8; cnt++ ) {
			AffineTransformation at = new AffineTransformation();
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
		Map<Geometry, IAgent> geoms = new GamaMap();
		for ( IAgent ag : scope.getWorldScope().getAgents() ) {
			geoms.put(returnToroidalGeom(ag.getGeometry().getInnerGeometry()), ag);
		}
		return geoms;
	}

	protected void createVirtualEnvironments() {
		adjustedXVector = new double[8];
		adjustedYVector = new double[8];
		Envelope environmentEnvelope = environment.getEnvelope();

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
		spatialIndex.remove(agent.getGeometry(), agent);
		// final IShape g = agent.getGeometry();
		// if ( g == null ) { return; }
		// if ( g.isPoint() ) {
		// spatialIndex.remove((GamaPoint) g.getLocation(), agent);
		// } else {
		// spatialIndex.remove(g.getEnvelope(), agent);
		// }
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
		// if ( environmentEnvelope == null ) {
		// // FIXME Move steps to the spatial index
		// steps = new double[] {};
		// return;
		// }

		environmentWidth = environmentEnvelope.getWidth();
		environmentHeight = environmentEnvelope.getHeight();
		environmentMinX = environmentEnvelope.getMinX();
		environmentMinY = environmentEnvelope.getMinY();
		environmentMaxX = environmentEnvelope.getMaxX();
		environmentMaxY = environmentEnvelope.getMaxY();
		// double biggest = Math.max(environmentWidth, environmentHeight);
		// steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest };
	}

	@Override
	public void updateAgent(final IShape previous, final IShape agent) {
		if ( previous != null ) {
			spatialIndex.remove(previous, agent);
		}
		spatialIndex.insert(agent);
	}

	// @Override
	// public void updateAgent(final IAgent agent, final boolean previousShapeIsPoint,
	// final ILocation previousLoc, final Envelope previousEnv) {
	// if ( previousShapeIsPoint && previousLoc != null ) {
	// spatialIndex.remove(previousLoc, agent);
	// } else if ( !previousShapeIsPoint && previousEnv != null ) {
	// spatialIndex.remove(previousEnv, agent);
	// }
	//
	// spatialIndex.insert(agent);
	// // IShape currentShape = agent.getGeometry();
	// // if ( currentShape == null || currentShape.isPoint() ) {
	// // spatialIndex.insert((GamaPoint) agent.getLocation(), agent);
	// // } else {
	// // spatialIndex.insert(currentShape.getEnvelope(), agent);
	// // }
	// }

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
		if ( environment.getGeometry().covers(point) ) { return point; }

		if ( isTorus() ) {
			Point pt = GeometryUtils.factory.createPoint(point.toCoordinate());

			for ( int cnt = 0; cnt < 8; cnt++ ) {
				AffineTransformation at = new AffineTransformation();
				at.translate(adjustedXVector[cnt], adjustedYVector[cnt]);
				GamaPoint newPt = new GamaPoint(at.transform(pt).getCoordinate());
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
		if ( !isTorus() ) {
			// for ( int i = 0; i < steps.length; i++ ) {
			IShape min_neighbour = spatialIndex.firstAtDistance(source, 0, filter);
			if ( min_neighbour != null ) {
				result = min_neighbour.getAgent();
				// break;
				// }
			} else {
				GuiUtils.debug("fdle");
			}
		} else {
			Geometry g0 = returnToroidalGeom(source.getGeometry());
			Map<Geometry, IAgent> agents = getAgents(filter);
			double distMin = Double.MAX_VALUE;
			for ( Geometry g1 : agents.keySet() ) {
				IAgent ag = agents.get(g1);
				if ( source.getAgent() != null && ag == source.getAgent() ) {
					continue;
				}
				double dist = g0.distance(g1);
				if ( dist < distMin ) {
					distMin = dist;
					result = ag;
				}
			}
		}

		if ( result == null ) {
			GuiUtils.debug("");
		}

		return result;
	}

	public Map<Geometry, IAgent> getAgents(final IAgentFilter filter) {
		Collection shps;
		if ( filter != null ) {
			shps = filter.getShapes();
		} else {
			shps = scope.getWorldScope().getAgents();
		}
		return toroidalGeoms(shps);
	}

	@Override
	public IAgent getAgentClosestTo(final ILocation source, final IAgentFilter filter) {
		IAgent result = null;
		if ( !isTorus() ) {
			// for ( int i = 0; i < steps.length; i++ ) {
			IShape min_neighbour = spatialIndex.firstAtDistance(source, 0, filter);
			if ( min_neighbour != null ) {
				result = min_neighbour.getAgent();
				// break;
				// }
			}
		} else {
			Map<Geometry, IAgent> agents = getAgents(filter);
			double distMin = Double.MAX_VALUE;
			Geometry g0 = returnToroidalGeom(source);
			for ( Geometry g1 : agents.keySet() ) {
				IAgent ag = agents.get(g1);
				if ( source.getAgent() != null && ag == source.getAgent() ) {
					continue;
				}
				double dist = g0.distance(g1);
				if ( dist < distMin ) {
					distMin = dist;
					result = ag;
				}
			}
		}
		return result;
	}

	@Override
	public IList<IAgent> getNeighboursOf(final IShape source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		IList<IAgent> agents;
		if ( !isTorus() ) {
			IList<IShape> shapes = spatialIndex.allAtDistance(source, distance, filter);
			agents = new GamaList(shapes.size());
			for ( IShape s : shapes ) {
				IAgent a = s.getAgent();
				if ( a != null && !a.dead() ) {
					agents.add(a);
				}
			}
		} else {
			Geometry g0 = returnToroidalGeom(source.getGeometry());
			agents = new GamaList<IAgent>();
			Map<Geometry, IAgent> agentsMap = getAgents(filter);
			for ( Geometry g1 : agentsMap.keySet() ) {
				IAgent ag = agentsMap.get(g1);
				if ( source.getAgent() != null && ag == source.getAgent() ) {
					continue;
				}
				double dist = g0.distance(g1);
				if ( dist <= distance ) {
					agents.add(ag);
				}
			}
		}
		return agents;
	}

	@Override
	public IList<IAgent> getNeighboursOf(final ILocation source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		IList<IAgent> agents;
		if ( !isTorus() ) {
			IList<IShape> shapes = spatialIndex.allAtDistance(source, distance, filter);
			agents = new GamaList(shapes.size());
			for ( IShape s : shapes ) {
				IAgent a = s.getAgent();
				if ( a != null && !a.dead() ) {
					agents.add(a);
				}
			}
		} else {
			Geometry g0 = returnToroidalGeom(source);
			agents = new GamaList<IAgent>();
			Map<Geometry, IAgent> agentsMap = getAgents(filter);
			for ( Geometry g1 : agentsMap.keySet() ) {
				IAgent ag = agentsMap.get(g1);
				if ( source.getAgent() != null && ag == source.getAgent() ) {
					continue;
				}
				double dist = g0.distance(g1);
				if ( dist <= distance ) {
					agents.add(ag);
				}
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
		if ( source == null ) { return GamaList.EMPTY_LIST; }
		if ( !isTorus() ) {
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
		Geometry sourceTo = returnToroidalGeom(source);
		PreparedGeometry pg = pgFact.create(sourceTo);
		Map<Geometry, IAgent> agentsMap = getAgents(f);
		GamaList<IAgent> result = new GamaList();
		for ( Geometry sh : agentsMap.keySet() ) {
			IAgent ag = agentsMap.get(sh);
			if ( ag != null && !ag.dead() ) {
				if ( source.getAgent() != null && ag == source.getAgent() ) {
					continue;
				}
				Geometry geom = ag.getInnerGeometry();
				if ( covered ? pg.covers(geom) : pg.intersects(geom) ) {
					result.add(ag);
				}
			}
		}
		return result;

	}

}
