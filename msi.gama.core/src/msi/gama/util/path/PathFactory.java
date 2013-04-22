package msi.gama.util.path;

import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.metamodel.topology.continuous.ContinuousTopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.metamodel.topology.graph.GraphTopology;
import msi.gama.metamodel.topology.graph.ISpatialGraph;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.IGraph;

public class PathFactory {

	 public static <V,E> GamaPath<V,E> newInstance(final IGraph<V,E> g, final IList<V> nodes){
		if(nodes.isEmpty() && (g instanceof GamaSpatialGraph)){
			return (GamaPath<V,E>) new GamaSpatialPath((GamaSpatialGraph)g,(IList<IShape>) nodes);
		}
		else if((nodes.get(0) instanceof ILocation) || (g instanceof GamaSpatialGraph)){
			return (GamaPath<V,E>) new GamaSpatialPath((GamaSpatialGraph)g,(IList<IShape>) nodes);
		}
		else {
			return new GamaPath<V,E>(g,nodes);
		}		
	 }
	 
	 public static <V,E> GamaPath<V,E> newInstance(final IGraph<V,E> g, final V start, final V target, final IList<E> edges){
		 if(g instanceof GamaSpatialGraph){
			 return (GamaPath<V,E>) new GamaSpatialPath((GamaSpatialGraph)g, (IShape)start, (IShape)target, (IList<IShape>) edges);			 			 
		 }
		 else {
			 return new GamaPath<V,E>(g, start, target, edges);			 
		 }
	 }

	 public static <V,E> GamaPath<V,E> newInstance(final IGraph<V,E> g, final V start, final V target, final IList<E> edges, final boolean modify_edges){
		 if(g instanceof GamaSpatialGraph){
			 return (GamaPath<V,E>) new GamaSpatialPath((GamaSpatialGraph)g, (IShape)start, (IShape)target, (IList<IShape>) edges, modify_edges);			 			 
		 }
		 else {
			 return new GamaPath<V,E>(g, start, target, edges, modify_edges);			 
		 }		 
	 }	 
	 
	 // With Topology 
	 public static GamaSpatialPath newInstance(final ITopology g, final IList<IShape> nodes){
		 if(g instanceof GraphTopology){
			 return (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(),(IList<IShape>) nodes);
		 }
		 else if(g instanceof ContinuousTopology || g instanceof AmorphousTopology){
			 return new GamaSpatialPath(null, nodes);			 			 
		 } else {
			 throw new ClassCastException("Topologies that are not Graph are not yet taken into account");
		 }
	 }
	 
	 public static GamaSpatialPath newInstance(final ITopology g, final IShape start, final IShape target, final IList<IShape> edges){
		 if(g instanceof GraphTopology){
			 return (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(), start, target, edges);
		 } else if(g instanceof ContinuousTopology || g instanceof AmorphousTopology){
		 	 return new GamaSpatialPath(start, target, edges);			 			 
	   } else {
			 throw new ClassCastException("Topologies that are not Graph are not yet taken into account");
		 }	
	 }

	 public static GamaSpatialPath newInstance(final ITopology g, final IShape start, final IShape target, final IList<IShape> edges, 
			 final boolean modify_edges){
		 if(g instanceof GraphTopology){
			 return (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(), start, target, edges, modify_edges);
		 } else if(g instanceof ContinuousTopology || g instanceof AmorphousTopology){
				 return new GamaSpatialPath(null, start, target, edges, modify_edges);			 			 
		 } else {
			 throw new ClassCastException("Topologies that are not Graph are not yet taken into account");
		 }		 
	 }	 	 
	 
}
