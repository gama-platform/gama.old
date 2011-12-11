/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.environment;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;
import msi.gama.util.graph.GamaPath;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 4 juil. 2011
 * 
 * @todo Description
 * 
 */
@vars({ @var(name = ISymbol.ENVIRONMENT, type = IType.GEOM_STR),
	@var(name = ITopology.PLACES, type = IType.CONTAINER_STR, of = IType.GEOM_STR),
// Could be replaced by "geometries"
/*
 * Normally not necessary as it is inherited from GamaGeometry @var(name = GamaPath.POINTS, type =
 * IType.LIST_STR, of = IType.POINT_STR)
 */
})
public interface ITopology extends IValue {

	public static final String BOUNDS = "bounds";
	public static final String TORUS = "torus";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String PLACES = "places";

	public abstract void initialize(IPopulation pop) throws GamaRuntimeException;

	public abstract void updateAgent(final IAgent agent, final boolean previousShapeIsPoint,
		final GamaPoint previousLoc, final Envelope previousEnv);

	public abstract void removeAgent(final IAgent agent);

	public abstract IAgent getAgentClosestTo(final IGeometry source, IAgentFilter filter);

	public abstract GamaList<IAgent> getNeighboursOf(final IGeometry source, final Double distance,
		IAgentFilter filter) throws GamaRuntimeException;

	public abstract GamaList<IAgent> getAgentsIn(final IGeometry source, final IAgentFilter f,
		boolean covered);

	/**
	 * Distance between two geometries in this topology.
	 * 
	 * @param source the source geometry (cannot be null)
	 * @param target the target (cannot be null)
	 * @return a double representing the distance between the two geometries, or Double.MAX_VALUE if
	 *         either one of them is not reachable from this topology
	 */
	public abstract Double distanceBetween(final IGeometry source, final IGeometry target);

	public abstract GamaPath pathBetween(final IGeometry source, final IGeometry target)
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
	public abstract GamaPoint getDestination(final GamaPoint source, final int direction,
		final double distance, boolean nullIfOutside);

	/**
	 * Return a random location inside the bounds of the environment's shape.
	 * The returned point is a valid local location of this topology.
	 * 
	 * @return a point, or null if no random locations are available
	 */
	public abstract GamaPoint getRandomLocation();

	/**
	 * Return the collection of places (IGeometry) defined by this topology.
	 * For continuous topologies, it is a GamaList with their environment. For discrete topologies,
	 * it can be any of the container supporting the inclusion of geometries (GamaList,
	 * GamaSpatialGraph, GamaMap, GamaSpatialMatrix)
	 * 
	 * @return an instance of IGamaContainer, which geometries can be iterated.
	 */
	@getter(var = PLACES)
	public abstract IGamaContainer<?, IGeometry> getPlaces();

	/**
	 * Return the environment of this topology (i.e. the IGeometry that defines its boundaries).
	 * 
	 * @return an instance of IGeometry.
	 */
	@getter(var = ISymbol.ENVIRONMENT)
	public abstract IGeometry getEnvironment();

	/**
	 * Normalizes a location so that the returned location is inside the bounds of the topology. The
	 * returned point is a valid local location of this topology.
	 * 
	 * @param p the location to normalize
	 * @param nullIfOutside tells whether to return
	 *            null or to coerce p if p is outside the bounds of the topology
	 * @return a valid point or null if nullIfOutside is true and the point is outside
	 */
	public abstract GamaPoint normalizeLocation(final GamaPoint p, boolean nullIfOutside);

	/**
	 * Called by a population to tell this topology that the shape of its host has changed. If the
	 * environment of the topology depends on the shape of the host, the topology can choose to
	 * adapt in consequence.
	 * 
	 * @param pop the population to which this topology is attached.
	 */
	public abstract void shapeChanged(IPopulation pop);

	public abstract double getWidth();

	public abstract double getHeight();

	public abstract void dispose();

	public abstract boolean isValidLocation(GamaPoint p);

	public abstract boolean isValidGeometry(IGeometry g);

	/**
	 * @param source
	 * @param target
	 * @return the direction or null if one these two geometries are invalid in this topology
	 */
	public abstract Integer directionInDegreesTo(IGeometry source, IGeometry target);

}