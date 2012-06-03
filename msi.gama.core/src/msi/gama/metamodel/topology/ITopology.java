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

import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 4 juil. 2011
 * 
 * @todo Description
 * 
 */
@vars({ @var(name = IKeyword.ENVIRONMENT, type = IType.GEOM_STR),
	@var(name = IKeyword.PLACES, type = IType.CONTAINER_STR, of = IType.GEOM_STR),
// Could be replaced by "geometries"
/*
 * Normally not necessary as it is inherited from GamaGeometry @var(name = GamaPath.POINTS, type =
 * IType.LIST_STR, of = IType.POINT_STR)
 */
})
public interface ITopology extends IValue {

	public abstract void initialize(IPopulation pop) throws GamaRuntimeException;

	public abstract void updateAgent(final IAgent agent, final boolean previousShapeIsPoint,
		final ILocation previousLoc, final Envelope previousEnv);

	public abstract void removeAgent(final IAgent agent);

	public abstract IAgent getAgentClosestTo(final IShape source, IAgentFilter filter);

	public abstract IAgent getAgentClosestTo(final ILocation source, IAgentFilter filter);

	public abstract IList<IAgent> getNeighboursOf(final IShape source, final Double distance,
		IAgentFilter filter) throws GamaRuntimeException;

	public abstract IList<IAgent> getNeighboursOf(final ILocation source, final Double distance,
		IAgentFilter filter) throws GamaRuntimeException;

	public abstract IList<IAgent> getAgentsIn(final IShape source, final IAgentFilter f,
		boolean covered);

	/**
	 * @throws GamaRuntimeException
	 *             Distance between two geometries in this topology.
	 * 
	 * @param source the source geometry (cannot be null)
	 * @param target the target (cannot be null)
	 * @return a double representing the distance between the two geometries, or Double.MAX_VALUE if
	 *         either one of them is not reachable from this topology
	 */
	public abstract Double distanceBetween(final IShape source, final IShape target);

	public abstract Double distanceBetween(final ILocation source, final ILocation target);

	public abstract IPath pathBetween(final IShape source, final IShape target)
		throws GamaRuntimeException;

	public abstract IPath pathBetween(final ILocation source, final ILocation target)
		throws GamaRuntimeException;

	/**
	 * Return the location corresponding to a displacement from source, with an angle (in degrees)
	 * given by direction and a given distance.
	 * The returned point is a valid local location of this topology or null.
	 * 
	 * @param source the point from which the destination is computed
	 * @param direction an angle in degrees (between 0 and 360 -- other values will be normalized
	 * @param distance the distance that should separate the actual location and the destination
	 * @param nullIfOutside tells wether to return the destination point or null if
	 *            the destination is outside the topology
	 * @return a point or null if no random locations are available
	 */
	public abstract ILocation getDestination(final ILocation source, final int direction,
		final double distance, boolean nullIfOutside);

	/**
	 * Return a random location inside the bounds of the environment's shape.
	 * The returned point is a valid local location of this topology.
	 * 
	 * @return a point, or null if no random locations are available
	 */
	public abstract ILocation getRandomLocation();

	/**
	 * Return the collection of places (IGeometry) defined by this topology.
	 * For continuous topologies, it is a GamaList with their environment. For discrete topologies,
	 * it can be any of the container supporting the inclusion of geometries (GamaList,
	 * GamaSpatialGraph, GamaMap, GamaSpatialMatrix)
	 * 
	 * @return an instance of IGamaContainer, which geometries can be iterated.
	 */
	@getter( IKeyword.PLACES)
	public abstract IContainer<?, IShape> getPlaces();

	/**
	 * Return the environment of this topology (i.e. the IGeometry that defines its boundaries).
	 * 
	 * @return an instance of IGeometry.
	 */
	@getter( IKeyword.ENVIRONMENT)
	public abstract IShape getEnvironment();

	/**
	 * Normalizes a location so that the returned location is inside the bounds of the topology. The
	 * returned point is a valid local location of this topology.
	 * 
	 * @param p the location to normalize
	 * @param nullIfOutside tells whether to return
	 *            null or to coerce p if p is outside the bounds of the topology
	 * @return a valid point or null if nullIfOutside is true and the point is outside
	 */
	public abstract ILocation normalizeLocation(final ILocation p, boolean nullIfOutside);

	/**
	 * @throws GamaRuntimeException
	 *             Called by a population to tell this topology that the shape of its host has
	 *             changed. If the
	 *             environment of the topology depends on the shape of the host, the topology can
	 *             choose to
	 *             adapt in consequence.
	 * 
	 * @param pop the population to which this topology is attached.
	 */
	public abstract void shapeChanged(IPopulation pop);

	public abstract double getWidth();

	public abstract double getHeight();

	public abstract void dispose();

	public abstract boolean isValidLocation(ILocation p);

	public abstract boolean isValidGeometry(IShape g);

	/**
	 * @throws GamaRuntimeException
	 * @param source
	 * @param target
	 * @return the direction or null if one these two geometries are invalid in this topology
	 */
	public abstract Integer directionInDegreesTo(IShape source, IShape target);

}