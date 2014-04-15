/*********************************************************************************************
 * 
 *
 * 'GraphTopology.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.topology.graph;

import gnu.trove.set.hash.THashSet;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.path.*;

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
	public GraphTopology(final IScope scope, final GamaSpatialGraph graph) {
		this(scope, scope.getSimulationScope().getGeometry(), graph);
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
		GamaSpatialGraph graph = (GamaSpatialGraph) getPlaces();
		boolean sourceNode = graph.getVertexMap().containsKey(source);
		boolean targetNode = graph.getVertexMap().containsKey(target);
		if ( sourceNode && targetNode ) { return (GamaSpatialPath) graph.computeShortestPathBetween(scope, source,
			target); }

		IShape edgeS = null, edgeT = null;

		final IAgentFilter filter = In.edgesOf(getPlaces());

		if ( !sourceNode ) {
			edgeS = getAgentClosestTo(scope, source, filter);
			// We avoid computing the target if we cannot find any source.
			if ( edgeS == null ) { return null; }
		}
		if ( !targetNode ) {
			edgeT = getAgentClosestTo(scope, target, filter);
			if ( edgeT == null ) { return null; }
		}

		if ( getPlaces().isDirected() ) { return pathBetweenCommonDirected(scope, edgeS, edgeT, source, target,
			sourceNode, targetNode); }

		return pathBetweenCommon(scope, graph, edgeS, edgeT, source, target, sourceNode, targetNode);
	}

	public GamaSpatialPath pathBetweenCommon(final IScope scope, final GamaSpatialGraph graph, final IShape edgeS,
		final IShape edgeT, final IShape source, final IShape target, final boolean sourceNode, final boolean targetNode) {
		IList<IShape> edges = new GamaList<IShape>();
		if ( sourceNode && !targetNode ) {
			IShape nodeT1 = (IShape) graph.getEdgeSource(edgeT);
			IShape nodeT2 = (IShape) graph.getEdgeTarget(edgeT);
			double l1 = 0;
			boolean computeOther = true;
			if ( nodeT1 == source ) {
				l1 = lengthEdge(edgeT, target, nodeT2, nodeT1);
			} else {
				edges = getPlaces().computeBestRouteBetween(scope, source, nodeT1);
				boolean isEmpty = edges.isEmpty();
				l1 = isEmpty ? Double.MAX_VALUE : pathlengthEdges(edges) + lengthEdge(edgeT, target, nodeT2, nodeT1);
				if ( !isEmpty ) {
					IShape el = edges.get(edges.size() - 1);
					if ( graph.getEdgeSource(el) == nodeT2 || graph.getEdgeTarget(el) == nodeT2 ) {
						// edges.remove(edges.size()-1);
						computeOther = false;
					}
				}
			}
			if ( computeOther ) {
				double l2 = 0;
				IList<IShape> edges2 = new GamaList<IShape>();
				if ( nodeT2 == source ) {
					l2 = lengthEdge(edgeT, target, nodeT1, nodeT2);
				} else {
					edges2 = getPlaces().computeBestRouteBetween(scope, source, nodeT2);
					l2 =
						edges2.isEmpty() ? Double.MAX_VALUE : pathlengthEdges(edges2) +
							lengthEdge(edgeT, target, nodeT1, nodeT2);
				}
				if ( l2 < l1 ) {
					edges = edges2;
					l1 = l2;
				}
			}
			if ( l1 == Double.MAX_VALUE ) { return null; }
			if ( edges.isEmpty() || edges.get(edges.size() - 1) != edgeT ) {
				edges.add(edgeT);
			}
		} else if ( !sourceNode && targetNode ) {
			IShape nodeS1 = (IShape) graph.getEdgeSource(edgeS);
			IShape nodeS2 = (IShape) graph.getEdgeTarget(edgeS);
			double l1 = 0;
			boolean computeOther = true;
			if ( nodeS1 == target ) {
				l1 = lengthEdge(edgeS, source, nodeS2, nodeS1);
			} else {
				edges = getPlaces().computeBestRouteBetween(scope, nodeS1, target);
				boolean isEmpty = edges.isEmpty();
				l1 = isEmpty ? Double.MAX_VALUE : pathlengthEdges(edges) + lengthEdge(edgeS, source, nodeS2, nodeS1);
				if ( !isEmpty ) {
					IShape e0 = edges.get(0);
					if ( graph.getEdgeSource(e0) == nodeS2 || graph.getEdgeTarget(e0) == nodeS2 ) {
						// edges.remove(0);
						computeOther = false;
					}
				}
			}
			if ( computeOther ) {
				double l2 = 0;
				IList<IShape> edges2 = new GamaList<IShape>();
				if ( nodeS2 == target ) {
					l2 = lengthEdge(edgeS, source, nodeS1, nodeS2);
				} else {
					edges2 = getPlaces().computeBestRouteBetween(scope, nodeS2, target);
					l2 =
						edges2.isEmpty() ? Double.MAX_VALUE : pathlengthEdges(edges2) +
							lengthEdge(edgeS, source, nodeS1, nodeS2);
				}
				if ( l2 < l1 ) {
					edges = edges2;
					l1 = l2;
				}
			}
			if ( l1 == Double.MAX_VALUE ) { return null; }
			if ( edges.isEmpty() || edges.get(0) != edgeS ) {
				edges.add(0, edgeS);
			}
		} else {
			IShape nodeS1 = (IShape) graph.getEdgeSource(edgeS);
			IShape nodeS2 = (IShape) graph.getEdgeTarget(edgeS);
			IShape nodeT1 = (IShape) graph.getEdgeSource(edgeT);
			IShape nodeT2 = (IShape) graph.getEdgeTarget(edgeT);
			/*
			 * System.out.println("*************************\nlocation : " + source);
			 * System.out.println("nodeS1 : " + nodeS1);
			 * System.out.println("nodeS2 : " + nodeS2);
			 * System.out.println("nodeT1 : " + nodeT1);
			 * System.out.println("nodeT2 : " + nodeT2);
			 */
			double lmin = Double.MAX_VALUE;

			boolean computeS1T2 = true;
			boolean computeS2T1 = true;
			boolean computeS2T2 = true;
			if ( nodeS1 == nodeT1 ) {
				lmin = lengthEdge(edgeS, source, nodeS2, nodeS1) + lengthEdge(edgeT, target, nodeT2, nodeT1);
			} else {
				edges = getPlaces().computeBestRouteBetween(scope, nodeS1, nodeT1);
				boolean isEmpty = edges.isEmpty();

				double els = lengthEdge(edgeS, source, nodeS2, nodeS1);
				double elt = lengthEdge(edgeT, target, nodeT2, nodeT1);
				lmin = isEmpty ? Double.MAX_VALUE : pathlengthEdges(edges) + els + elt;

				if ( !isEmpty ) {
					IShape e0 = edges.get(0);
					IShape el = edges.get(edges.size() - 1);
					if ( e0 != el ) {
						boolean ts1 = graph.getEdgeSource(e0) == nodeS2 || graph.getEdgeTarget(e0) == nodeS2;
						boolean ts2 = graph.getEdgeSource(el) == nodeT2 || graph.getEdgeTarget(el) == nodeT2;
						double valmin = lmin;
						if ( ts1 ) {
							computeS2T1 = false;
							double val = lmin - els - e0.getPerimeter() + lengthEdge(edgeS, source, nodeS1, nodeS2);
							if ( valmin > val ) {
								valmin = val;
							}
						}
						if ( ts2 ) {
							computeS1T2 = false;
							double val = lmin - elt - el.getPerimeter() + lengthEdge(edgeT, target, nodeT1, nodeT2);
							if ( valmin > val ) {
								valmin = val;
							}
						}
						if ( ts1 && ts2 ) {
							computeS2T2 = false;
							double val =
								lmin - els - e0.getPerimeter() - elt - el.getPerimeter() +
									lengthEdge(edgeS, source, nodeS1, nodeS2) +
									lengthEdge(edgeT, target, nodeT1, nodeT2);
							if ( valmin > val ) {
								valmin = val;
							}
						}
						lmin = valmin;
					}
				}
			}
			// System.out.println("edges : " + edges + " lmin : " + lmin);
			if ( computeS2T1 ) {
				double l2 = 0;
				IList<IShape> edges2 = new GamaList<IShape>();
				if ( nodeS2 == nodeT1 ) {
					l2 = lengthEdge(edgeS, source, nodeS1, nodeS2) + lengthEdge(edgeT, target, nodeT2, nodeT1);
				} else {
					edges2 = getPlaces().computeBestRouteBetween(scope, nodeS2, nodeT1);
					boolean isEmpty = edges2.isEmpty();
					double els = lengthEdge(edgeS, source, nodeS1, nodeS2);
					double elt = lengthEdge(edgeT, target, nodeT2, nodeT1);

					l2 = isEmpty ? Double.MAX_VALUE : pathlengthEdges(edges2) + els + elt;
					if ( !isEmpty ) {
						IShape e0 = edges2.get(0);
						IShape el = edges2.get(edges2.size() - 1);
						if ( e0 != el ) {
							boolean ts1 = graph.getEdgeSource(e0) == nodeS1 || graph.getEdgeTarget(e0) == nodeS1;

							boolean ts2 = graph.getEdgeSource(el) == nodeT2 || graph.getEdgeTarget(el) == nodeT2;
							double valmin = l2;
							if ( ts2 ) {
								computeS2T2 = false;
								double val = l2 - elt - el.getPerimeter() + lengthEdge(edgeT, target, nodeT1, nodeT2);
								if ( valmin > val ) {
									valmin = val;
								}
							}
							if ( ts1 && ts2 ) {
								computeS1T2 = false;
								double val =
									l2 - els - e0.getPerimeter() - elt - el.getPerimeter() +
										lengthEdge(edgeS, source, nodeS2, nodeS1) +
										lengthEdge(edgeT, target, nodeT1, nodeT2);
								if ( valmin > val ) {
									valmin = val;
								}
							}
							l2 = valmin;
						}

					}
				}
				// System.out.println("edges2 : " + edges2 + " l2 : " + l2);
				if ( l2 < lmin ) {
					edges = edges2;
					lmin = l2;
				}

			}
			if ( computeS1T2 ) {

				double l2 = 0;
				IList<IShape> edges2 = new GamaList<IShape>();
				if ( nodeS1 == nodeT2 ) {
					l2 = lengthEdge(edgeS, source, nodeS2, nodeS1) + lengthEdge(edgeT, target, nodeT1, nodeT2);
				} else {
					edges2 = getPlaces().computeBestRouteBetween(scope, nodeS1, nodeT2);
					boolean isEmpty = edges2.isEmpty();
					double els = lengthEdge(edgeS, source, nodeS2, nodeS1);
					double elt = lengthEdge(edgeT, target, nodeT1, nodeT2);
					l2 = isEmpty ? Double.MAX_VALUE : pathlengthEdges(edges2) + els + elt;
					if ( !isEmpty ) {
						IShape e0 = edges2.get(0);
						boolean ts1 = graph.getEdgeSource(e0) == nodeS2 || graph.getEdgeTarget(e0) == nodeS2;
						if ( ts1 ) {
							computeS2T2 = false;
							double val = l2 - els - e0.getPerimeter() + lengthEdge(edgeS, source, nodeS1, nodeS2);
							if ( l2 > val ) {
								l2 = val;
							}
						}

					}
				}
				// System.out.println("edges3 : " + edges2 + " l3 : " + l2);
				if ( l2 < lmin ) {
					edges = edges2;
					lmin = l2;
				}
			}
			if ( computeS2T2 ) {
				double l2 = 0;
				IList<IShape> edges2 = new GamaList<IShape>();
				if ( nodeS2 == nodeT2 ) {
					l2 = lengthEdge(edgeS, source, nodeS1, nodeS2) + lengthEdge(edgeT, target, nodeT1, nodeT2);
				} else {
					edges2 = getPlaces().computeBestRouteBetween(scope, nodeS2, nodeT2);
					l2 =
						edges2.isEmpty() ? Double.MAX_VALUE : pathlengthEdges(edges2) +
							lengthEdge(edgeS, source, nodeS1, nodeS2) + lengthEdge(edgeT, target, nodeT1, nodeT2);
				}

				// System.out.println("edges4 : " + edges2 + " l4 : " + l2);
				if ( l2 < lmin ) {
					edges = edges2;
					lmin = l2;
				}
			}
			if ( lmin == Double.MAX_VALUE ) { return null; }
			if ( edges.isEmpty() || edges.get(0) != edgeS ) {
				edges.add(0, edgeS);
			}

			if ( edges.get(edges.size() - 1) != edgeT ) {
				edges.add(edgeT);
				// System.out.println("lmin : " + lmin);
				// System.out.println("edges : " + edges);
			}

		}
		return PathFactory.newInstance(this, source, target, edges);
	}

	GamaSpatialPath pathFromEdgesUndirected(final IScope scope, IList<IShape> edges, final IShape edgeS,
		final IShape edgeT, final IShape source, final IShape target, final boolean sourceNode,
		final boolean targetNode, final IShape nodeS, final IShape nodeSbis, final IShape nodeT,
		final boolean computeOther) {
		if ( edges.isEmpty() || edges.get(0) == null ) { return null; }
		if ( !sourceNode ) {
			Set edgesSetInit = new THashSet(Arrays.asList(edges.get(0).getInnerGeometry().getCoordinates()));
			final Set edgesSetS = new THashSet(Arrays.asList(edgeS.getInnerGeometry().getCoordinates()));
			if ( !edgesSetS.equals(edgesSetInit) ) {
				double l1 = 0;
				double l2 = 1;
				IList<IShape> edgesbis = null;
				if ( computeOther ) {
					l1 = pathlengthEdges(edges) + lengthEdge(edgeS, source, nodeSbis, nodeS);
					edgesbis = getPlaces().computeBestRouteBetween(scope, nodeSbis, nodeT);
					l2 = pathlengthEdges(edgesbis) + lengthEdge(edgeS, source, nodeS, nodeSbis);
				}
				if ( l1 < l2 || edgesbis.isEmpty() || edgesbis.get(0) == null ) {
					edges.add(0, edgeS);
				} else {
					edges = edgesbis;
					edgesSetInit = new THashSet(Arrays.asList(edges.get(0).getInnerGeometry().getCoordinates()));
					if ( !edgesSetS.equals(edgesSetInit) ) {
						edges.add(0, edgeS);
					}
				}

			}
		}
		if ( !targetNode ) {
			final Set edgesSetEnd =
				new THashSet(Arrays.asList(edges.get(edges.size() - 1).getInnerGeometry().getCoordinates()));
			final Set edgesSetT = new THashSet(Arrays.asList(edgeT.getInnerGeometry().getCoordinates()));

			if ( !edgesSetT.equals(edgesSetEnd) ) {
				edges.add(edgeT);
			}
		}

		// return new GamaPath(this, source, target, edges);
		return PathFactory.newInstance(this, source, target, edges);
	}

	public double pathlengthEdges(final IList<IShape> edges) {
		double length = 0;
		for ( IShape sp : edges ) {
			length += sp.getPerimeter();
		}
		return length;
	}

	public double lengthEdge(final IShape edge, final IShape location, final IShape source, final IShape target) {
		return edge.getPerimeter() * location.euclidianDistanceTo(target) / source.euclidianDistanceTo(target);
	}

	public GamaSpatialPath pathBetweenCommonDirected(final IScope scope, final IShape edgeS, final IShape edgeT,
		final IShape source, final IShape target, final boolean sourceNode, final boolean targetNode) {
		IList<IShape> edges;

		if ( !sourceNode && !targetNode && edgeS.equals(edgeT) ) {
			GamaPoint ptS = new GamaPoint(edgeS.getInnerGeometry().getCoordinates()[0]);
			if ( source.euclidianDistanceTo(ptS) < target.euclidianDistanceTo(ptS) ) {
				edges = new GamaList<IShape>();
				edges.add(edgeS);
				return PathFactory.newInstance(this, source, target, edges);
			}
		}
		IShape nodeS = sourceNode ? source : getPlaces().getEdgeTarget(edgeS);
		IShape nodeT = targetNode ? target : getPlaces().getEdgeSource(edgeT);

		if ( nodeS.equals(nodeT) ) {
			edges = new GamaList<IShape>();
			if ( edgeS != null ) {
				edges.add(edgeS);
			}
			if ( edgeT != null ) {
				edges.add(edgeT);
			}
			return PathFactory.newInstance(this, source, target, edges);
		}
		edges = getPlaces().computeBestRouteBetween(scope, nodeS, nodeT);
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
		return pathBetween(scope, (IShape) source, (IShape) target);
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
	public boolean isValidLocation(final IScope scope, final ILocation p) {
		return isValidGeometry(scope, p.getGeometry());
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IScope scope, final IShape g) {
		// Geometry g2 = g.getInnerGeometry();
		for ( final IShape g1 : places.iterable(scope) ) {
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
	public Collection<IAgent> getAgentsIn(final IScope scope, final IShape source, final IAgentFilter f,
		final boolean covered) {
		Collection<IAgent> result = super.getAgentsIn(scope, source, f, covered);
		Iterator<IAgent> it = result.iterator();
		while (it.hasNext()) {
			IAgent ag = it.next();
			if ( ag.dead() || !isValidGeometry(scope, ag) ) {
				it.remove();
			}
		}
		return result;
	}

	@Override
	public boolean isTorus() {
		// TODO Why is it the case ?
		return false;
	}

	@Override
	public List KpathsBetween(final IScope scope, final IShape source, final IShape target, final int k) {
		ISpatialGraph graph = getPlaces();
		boolean sourceNode = graph.containsVertex(source);
		boolean targetNode = graph.containsVertex(target);
		if ( sourceNode && targetNode ) { return graph.computeKShortestPathsBetween(scope, source, target, k); }

		IShape edgeS = null, edgeT = null;

		final IAgentFilter filter = In.edgesOf(getPlaces());

		if ( !sourceNode ) {
			edgeS = getAgentClosestTo(scope, source, filter);
			// We avoid computing the target if we cannot find any source.
			if ( edgeS == null ) { return null; }
		}
		if ( !targetNode ) {
			edgeT = getAgentClosestTo(scope, target, filter);
			if ( edgeT == null ) { return null; }
		}

		if ( getPlaces().isDirected() ) { return KpathsBetweenCommonDirected(scope, edgeS, edgeT, source, target,
			sourceNode, targetNode, k); }

		return KpathsBetweenCommon(scope, edgeS, edgeT, source, target, sourceNode, targetNode, k);
	}

	@Override
	public List<GamaSpatialPath> KpathsBetween(final IScope scope, final ILocation source, final ILocation target,
		final int k) {
		return KpathsBetween(scope, source.getGeometry(), target.getGeometry(), k);
	}

	public List KpathsBetweenCommon(final IScope scope, final IShape edgeS, final IShape edgeT, final IShape source,
		final IShape target, final boolean sourceNode, final boolean targetNode, final int k) {
		IShape nodeS = source;
		IShape nodeSbis = source;
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
			nodeSbis = s2;
			if ( s1.equals(nodeT) ||
				!s2.equals(nodeT) &&
				s1.getLocation().euclidianDistanceTo(source.getLocation()) > s2.getLocation().euclidianDistanceTo(
					source.getLocation()) ) {
				nodeS = s2;
				nodeSbis = s1;
			}
		}
		List<IList<IShape>> edgesList = getPlaces().computeKBestRoutesBetween(scope, nodeS, nodeT, k);
		List results = new GamaList();
		for ( IList<IShape> edges : edgesList ) {
			GamaSpatialPath pp =
				pathFromEdgesUndirected(scope, edges, edgeS, edgeT, source, target, sourceNode, targetNode, nodeS,
					nodeSbis, nodeT, false);
			if ( pp != null ) {
				results.add(pp);
			}

		}
		Collections.sort(results);
		return results;
	}

	public List KpathsBetweenCommonDirected(final IScope scope, final IShape edgeS, final IShape edgeT,
		final IShape source, final IShape target, final boolean sourceNode, final boolean targetNode, final int k) {
		List results = new GamaList();
		if ( edgeS.equals(edgeT) ) {
			GamaPoint ptS = new GamaPoint(edgeS.getInnerGeometry().getCoordinates()[0]);
			if ( source.euclidianDistanceTo(ptS) < target.euclidianDistanceTo(ptS) ) {
				IList<IShape> edges = new GamaList<IShape>();
				edges.add(edgeS);
				results.add(PathFactory.newInstance(this, source, target, edges));
				return results;
			}
		}
		IShape nodeS = sourceNode ? source : getPlaces().getEdgeTarget(edgeS);
		IShape nodeT = targetNode ? target : getPlaces().getEdgeSource(edgeT);

		if ( nodeS.equals(nodeT) ) {
			IList<IShape> edges = new GamaList<IShape>();
			edges.add(edgeS);
			edges.add(edgeT);
			results.add(PathFactory.newInstance(this, source, target, edges));
			return results;
		}
		List<IList<IShape>> edgesList = getPlaces().computeKBestRoutesBetween(scope, nodeS, nodeT, k);
		for ( IList<IShape> edges : edgesList ) {
			edges = getPlaces().computeBestRouteBetween(scope, nodeS, nodeT);
			if ( edges.isEmpty() || edges.get(0) == null ) {
				continue;
			}

			if ( !sourceNode ) {
				edges.add(0, edgeS);
			}
			if ( !targetNode ) {
				edges.add(edges.size(), edgeT);
			}

			// return new GamaPath(this, source, target, edges);
			GamaSpatialPath pp = PathFactory.newInstance(this, source, target, edges);
			if ( pp != null ) {
				results.add(pp);
			}
		}
		Collections.sort(results);
		return results;

	}

}
