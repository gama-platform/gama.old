/*******************************************************************************************************
 *
 * msi.gama.util.path.PathFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.path;

import static msi.gama.common.geometry.GeometryUtils.getFirstPointOf;
import static msi.gama.common.geometry.GeometryUtils.getLastPointOf;


import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.metamodel.topology.continuous.ContinuousTopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.metamodel.topology.graph.GraphTopology;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class PathFactory {

	public static <V, E> GamaPath<V, E, IGraph<V, E>> newInstance(final IGraph<V, E> g,
			final IList<? extends V> nodes) {
		if (nodes.isEmpty() && g instanceof GamaSpatialGraph) {
			return (GamaPath) new GamaSpatialPath((GamaSpatialGraph) g, (IList<IShape>) nodes);
		} else if (nodes.get(0) instanceof ILocation || g instanceof GamaSpatialGraph) {
			return (GamaPath) new GamaSpatialPath((GamaSpatialGraph) g, (IList<IShape>) nodes);
		} else {
			return new GamaPath<>(g, nodes);
		}
	}

	public static <V, E> GamaPath<V, E, IGraph<V, E>> newInstance(final IGraph<V, E> g, final V start, final V target,
			final IList<E> edges) {
		if (g instanceof GamaSpatialGraph) {
			edges.removeIf(e -> e == null);
			return (GamaPath) new GamaSpatialPath((GamaSpatialGraph) g, (IShape) start, (IShape) target,
					(IList<IShape>) edges);
		} else {
			return new GamaPath<>(g, start, target, edges);
		}
	}

	public static <V, E> GamaPath<V, E, IGraph<V, E>> newInstance(final IGraph<V, E> g, final V start, final V target,
			final IList<E> edges, final boolean modify_edges) {
		if (g instanceof GamaSpatialGraph) {
			return (GamaPath) new GamaSpatialPath((GamaSpatialGraph) g, (IShape) start, (IShape) target,
					(IList<IShape>) edges, modify_edges);
		} else {
			return new GamaPath<>(g, start, target, edges, modify_edges);
		}
	}

	// With Topology
	public static GamaSpatialPath newInstance(final IScope scope, final ITopology g,
			final IList<? extends IShape> nodes, final double weight) {
		GamaSpatialPath path;
		if (g instanceof GraphTopology) {
			path = (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(), nodes);
		} else if (g instanceof ContinuousTopology || g instanceof AmorphousTopology) {
			path = new GamaSpatialPath(null, nodes);
		} else if (g instanceof GridTopology) {
			path = new GamaSpatialPath(null, nodes);
		} else {
			throw GamaRuntimeException.error("Topologies that are not Graph are not yet taken into account", scope);
		}
		path.setWeight(weight);
		return path;
	}

	public static GamaSpatialPath newInstance(final IScope scope, final ITopology g, final IShape start,
			final IShape target, final IList<IShape> edges) {
		if (g instanceof GraphTopology) {
			return (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(), start, target, edges);
		} else {// if (g instanceof ContinuousTopology || g instanceof AmorphousTopology) {
			return new GamaSpatialPath(start, target, edges);
		}
		/*
		 * } else { throw GamaRuntimeException.error("Topologies that are not Graph are not yet taken into account",
		 * scope); }
		 */
	}

	public static GamaSpatialPath newInstance(final IScope scope, final ITopology g, final IShape start,
			final IShape target, final IList<IShape> edges, final boolean modify_edges) {
		if (g instanceof GraphTopology) {
			return (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(), start, target, edges, modify_edges);
		} else {// if ( g instanceof ContinuousTopology || g instanceof
				// AmorphousTopology ) {
			return new GamaSpatialPath(null, start, target, edges, modify_edges);
		} /*
			 * else { throw GamaRuntimeException.error( "Topologies that are not Graph are not yet taken into account");
			 * }
			 */
	}

	public static IPath newInstance(final IScope scope, final IList<IShape> edgesNodes, final boolean isEdges) {
		if (isEdges) {
			final GamaShape shapeS = (GamaShape) edgesNodes.get(0).getGeometry();
			final GamaShape shapeT = (GamaShape) edgesNodes.get(edgesNodes.size() - 1).getGeometry();
			return new GamaSpatialPath(null, getFirstPointOf(shapeS), getLastPointOf(shapeT), edgesNodes, false);
		}
		return new GamaSpatialPath(edgesNodes);
	}

}
