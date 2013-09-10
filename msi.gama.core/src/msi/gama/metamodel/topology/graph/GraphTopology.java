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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.graph;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.path.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

/**
 * The class GraphTopology.
 * 
 * @author drogoul
 * @since 27 nov. 2011
 * 
 */
public class GraphTopology extends AbstractTopology {

	/**
	 * @param scope
	 * @param env
	 * @param torus
	 */
	public GraphTopology(final IScope scope, final IShape env, final GamaSpatialGraph graph) {
		super(scope, env, null);
		places = graph;
	}

	// The default topologies for graphs.
	public GraphTopology(final GamaSpatialGraph graph) {
		this(GAMA.obtainNewScope(), GAMA.getSimulation().getGeometry(), graph);
	}

	@Override
	protected boolean canCreateAgents() {
		return true;
	}

	/**
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target) {
		ISpatialGraph graph = getPlaces();
		boolean sourceNode = graph.containsVertex(source);
		boolean targetNode = graph.containsVertex(target);
		if ( sourceNode && targetNode ) { return (GamaSpatialPath) graph.computeShortestPathBetween(source, target); }

		IShape edgeS = null, edgeT = null;

		final IAgentFilter filter = In.edgesOf(getPlaces());

		if ( !sourceNode ) {
			edgeS =
				source instanceof ILocation ? getAgentClosestTo((ILocation) source, filter) : getAgentClosestTo(source,
					filter);
		}
		if ( !targetNode ) {
			edgeT =
				target instanceof ILocation ? getAgentClosestTo((ILocation) target, filter) : getAgentClosestTo(target,
					filter);
		}

		if ( edgeS == null && !sourceNode || edgeT == null && !targetNode ) { return null; }
		if ( getPlaces().isDirected() ) { return pathBetweenCommonDirected(edgeS, edgeT, source, target, sourceNode,
			targetNode); }
		return pathBetweenCommon(edgeS, edgeT, source, target, sourceNode, targetNode);

	}

	public GamaSpatialPath pathBetweenCommon(final IShape edgeS, final IShape edgeT, final IShape source,
		final IShape target, final boolean sourceNode, final boolean targetNode) {
		/*
		 * if ( edgeS == edgeT ) { return PathFactory.newInstance(this, source, target, GamaList.with(edgeS));
		 * // return new GamaPath(this, source, target, GamaList.with(edgeS));
		 * }
		 */

		IShape nodeS = source;
		IShape nodeT = target;

		if ( !targetNode ) {
			IShape t1 = null;
			IShape t2 = null;
			t1 = getPlaces().getEdgeSource(edgeT);
			t2 = getPlaces().getEdgeTarget(edgeT);
			if ( t1 == null || t2 == null ) { return null; }
			nodeT = t1;
			if ( t1.getLocation().euclidianDistanceTo(target.getLocation()) > t2.getLocation().euclidianDistanceTo(
				target.getLocation()) ) {
				nodeT = t2;
			}
		}
		if ( !sourceNode ) {
			IShape s1 = null;
			IShape s2 = null;
			s1 = getPlaces().getEdgeSource(edgeS);
			s2 = getPlaces().getEdgeTarget(edgeS);
			if ( s1 == null || s2 == null ) { return null; }
			nodeS = s1;
			if ( s1 == nodeT ||
				s2 != nodeT &&
				s1.getLocation().euclidianDistanceTo(source.getLocation()) > s2.getLocation().euclidianDistanceTo(
					source.getLocation()) ) {
				nodeS = s2;
			}
		}

		final IList<IShape> edges = getPlaces().computeBestRouteBetween(nodeS, nodeT);
		if ( edges.isEmpty() || edges.get(0) == null ) { return null; }
		if ( !sourceNode ) {
			final HashSet edgesSetInit = new HashSet(Arrays.asList(edges.get(0).getInnerGeometry().getCoordinates()));
			final HashSet edgesSetS = new HashSet(Arrays.asList(edgeS.getInnerGeometry().getCoordinates()));
			if ( !edgesSetS.equals(edgesSetInit) ) {
				edges.add(0, edgeS);
			}
		}
		if ( !targetNode ) {
			final HashSet edgesSetEnd =
				new HashSet(Arrays.asList(edges.get(edges.size() - 1).getInnerGeometry().getCoordinates()));
			final HashSet edgesSetT = new HashSet(Arrays.asList(edgeT.getInnerGeometry().getCoordinates()));

			if ( !edgesSetT.equals(edgesSetEnd) ) {
				edges.add(edgeT);
			}
		}

		// return new GamaPath(this, source, target, edges);
		return PathFactory.newInstance(this, source, target, edges);
	}

	public GamaSpatialPath pathBetweenCommonDirected(final IShape edgeS, final IShape edgeT, final IShape source,
		final IShape target, final boolean sourceNode, final boolean targetNode) {
		IList<IShape> edges;

		if ( edgeS == edgeT ) {
			GamaPoint ptS = new GamaPoint(edgeS.getInnerGeometry().getCoordinates()[0]);
			if ( source.euclidianDistanceTo(ptS) < target.euclidianDistanceTo(ptS) ) {
				edges = new GamaList<IShape>();
				edges.add(edgeS);
				return PathFactory.newInstance(this, source, target, edges);
			}
		}
		IShape nodeS = sourceNode ? source : getPlaces().getEdgeTarget(edgeS);
		IShape nodeT = targetNode ? target : getPlaces().getEdgeSource(edgeT);

		if ( nodeS == nodeT ) {
			edges = new GamaList<IShape>();
			edges.add(edgeS);
			edges.add(edgeT);
			return PathFactory.newInstance(this, source, target, edges);
		}
		edges = getPlaces().computeBestRouteBetween(nodeS, nodeT);
		if ( edges.isEmpty() || edges.get(0) == null ) { return null; }

		if ( !sourceNode ) {
			edges.add(0, edgeS);
		}
		if ( !targetNode ) {
			edges.add(edges.size(), edgeT);
		}

		// return new GamaPath(this, source, target, edges);
		return PathFactory.newInstance(this, source, target, edges);
	}

	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target) {
		IShape edgeS = null, edgeT = null;
		ISpatialGraph graph = getPlaces();
		boolean sourceNode = graph.containsVertex(source);
		boolean targetNode = graph.containsVertex(target);
		if ( sourceNode && targetNode ) { return (GamaSpatialPath) graph.computeShortestPathBetween(source, target); }

		if ( !graph.getEdges().isEmpty() ) {
			if ( graph instanceof GamaSpatialGraph && !((GamaSpatialGraph) graph).isAgentEdge() ) {

				double distMinT = Double.MAX_VALUE;
				double distMinS = Double.MAX_VALUE;
				for ( final IShape shp : this.getPlaces().getEdges() ) {
					if ( !sourceNode ) {
						final double distS = shp.euclidianDistanceTo(source);
						if ( distS < distMinS ) {
							distMinS = distS;
							edgeS = shp;
						}
					}
					if ( !targetNode ) {
						final double distT = shp.euclidianDistanceTo(target);
						if ( distT < distMinT ) {
							distMinT = distT;
							edgeT = shp;
						}
					}
				}
			} else {
				final IAgentFilter filter = In.edgesOf(getPlaces());
				if ( !sourceNode ) {
					edgeS = getAgentClosestTo(source, filter);
				}
				if ( !targetNode ) {
					edgeT = getAgentClosestTo(target, filter);
				}
			}
		}

		if ( graph.isDirected() ) { return pathBetweenCommonDirected(edgeS, edgeT, source, target, sourceNode,
			targetNode); }
		return pathBetweenCommon(edgeS, edgeT, source, target, sourceNode, targetNode);
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "GraphTopology";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return "GraphTopology";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(final IScope scope) {
		return new GraphTopology(scope, environment, (GamaSpatialGraph) places);
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#getRandomPlace()
	 */

	@Override
	public ISpatialGraph getPlaces() {
		return (GamaSpatialGraph) super.getPlaces();
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final ILocation p) {
		return isValidGeometry(p.getGeometry());
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IShape g) {
		// Geometry g2 = g.getInnerGeometry();
		for ( final IShape g1 : places ) {
			if ( g1.intersects(g) ) { return true; }
			// TODO covers or intersects ?
		}
		return false;
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry,
	 *      java.lang.Double)
	 */
	@Override
	public Double distanceBetween(final IScope scope, final IShape source, final IShape target) {
		final GamaSpatialPath path = this.pathBetween(scope, source, target);
		if ( path == null ) { return Double.MAX_VALUE; }
		return path.getDistance(scope);
	}

	@Override
	public Double distanceBetween(final IScope scope, final ILocation source, final ILocation target) {
		final GamaSpatialPath path = this.pathBetween(scope, source, target);
		if ( path == null ) { return Double.MAX_VALUE; }
		return path.getDistance(scope);
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(final IScope scope, final IShape source, final IShape target) {
		// WARNING As it is computed every time the location of an agent is set, and as the source and target in that
		// case do not correspond to existing nodes, it may be safer (and faster) to call the root topology
		return root.directionInDegreesTo(scope, source, target);
		// final GamaSpatialPath path = this.pathBetween(scope, source, target);
		// if ( path == null ) { return null; }
		// // LineString ls = (LineString) path.getEdgeList().first().getInnerGeometry();
		// // TODO Check this
		// final double dx = target.getLocation().getX() - source.getLocation().getX();
		// final double dy = target.getLocation().getY() - source.getLocation().getY();
		// final double result = Maths.atan2Opt(dy, dx);
		// return Maths.checkHeading((int) result);
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsIn(msi.gama.interfaces.IGeometry, msi.gama.environment.IAgentFilter,
	 *      boolean)
	 */
	@Override
	public Iterator<IAgent> getAgentsIn(final IShape source, final IAgentFilter f, final boolean covered) {
		return Iterators.filter(super.getAgentsIn(source, f, covered), new Predicate<IAgent>() {

			@Override
			public boolean apply(final IAgent ag) {
				return !ag.dead() && isValidGeometry(ag);
			}
		});
	}

	@Override
	public boolean isTorus() {
		// TODO Why is it the case ?
		return false;
	}
}
