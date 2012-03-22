package msi.gama.util.graph;

import java.awt.List;
import java.util.Collection;

import org.jgrapht.Graphs;

import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;


public class AStarShortestPath {

	private IList<IShape> edgeList;
	
	public double hValue(IShape source, IShape target) {
		return source.euclidianDistanceTo(target);
	}
	
	/*public AStarShortestPath(GamaGraph graph, IShape source, IShape target) {
		GamaList<IShape> closedSet = new GamaList<IShape>();    // The set of nodes already evaluated.
		GamaList<IShape> openSet = new GamaList<IShape>();
		openSet.add(source);// The set of tentative nodes to be evaluated, initially containing the start node
		GamaMap cameFrom = new GamaMap();    // The map of navigated nodes.
		GamaMap gScore = new GamaMap();  
		GamaMap hScore = new GamaMap();  
		GamaMap fScore = new GamaMap();  
		gScore.put(source, 0.0);
		double hStart =  hValue(source, target);
		hScore.put(source,hStart);
		fScore.put(source, hStart);
		boolean finish = false;	 
		edgeList = new GamaList();
		 while (! openSet.isEmpty()) {
			 IShape current = null;
			 Double valMax = Double.MAX_VALUE;
			 for (IShape node: openSet) {
				 Double val = (Double) fScore.get(node);
				 if (val < valMax) {
					 valMax = val;
					 current = node;
				 }
			 }
			 if (current == target) {
				 reconstructPath(cameFrom, target, graph);
				 finish = true;
				 break;
			 }
 
			 openSet.remove(current);
			 closedSet.add(current);
			 Double valCurr = (Double) gScore.get(current);
			 for (Object neighEdge : graph.getVertex(current).getEdges()) {
				 _Edge edge = graph.getEdge(neighEdge);
				 Object neighNode = edge.getOther(current);
				 if (closedSet.contains(neighNode))
					 continue;
				 Double tentativeGscore = valCurr +  graph.getEdgeWeight(neighEdge);
				 boolean tentative_is_better = false;
				 IShape neigh = (IShape) neighNode;
				 if (! openSet.contains(neighNode)) {	
					openSet.add(neigh);
					tentative_is_better = true;	
				 } else if (tentativeGscore <(Double) gScore.get(neighNode)) {
					 tentative_is_better = true;	
				 } else {
					 tentative_is_better = false;	
				 }
				 if (tentative_is_better) {
					 cameFrom.put(neighNode, current);
					 gScore.put(neighNode, tentativeGscore);
					 fScore.put(neighNode, tentativeGscore + hValue(neigh, target));
				 }
			 }
			 if (finish)
				 break;
		 }
	}*/
	
	public AStarShortestPath(GamaGraph graph, IShape source, IShape target) {
		GamaList<IShape> closedSet = new GamaList<IShape>();    // The set of nodes already evaluated.
		GamaList<IShape> openSet = new GamaList<IShape>();
		openSet.add(source);// The set of tentative nodes to be evaluated, initially containing the start node
		GamaMap cameFrom = new GamaMap();    // The map of navigated nodes.
		GamaMap gScore = new GamaMap();  
		GamaMap hScore = new GamaMap();  
		GamaMap fScore = new GamaMap();  
		gScore.put(source, 0.0);
		double hStart =  hValue(source, target);
		hScore.put(source,hStart);
		fScore.put(source, hStart);
		boolean finish = false;	 
		edgeList = new GamaList();
		 while (! openSet.isEmpty()) {
			 IShape current = null;
			 Double valBest = Double.MAX_VALUE;
			 for (IShape node: openSet) {
				 Double val = (Double) fScore.get(node);
				 if (val < valBest) {
					 valBest = val;
					 current = node;
				 }
			 }
			 if (current == target) {
				 reconstructPath(cameFrom, target, graph);
				 finish = true;
				 break;
			 }
 
			 openSet.remove(current);
			 closedSet.add(current);
			 Double valCurr = (Double) gScore.get(current);
			 java.util.List<Object> neighs = Graphs.neighborListOf(graph, current);
			 valBest = Double.MAX_VALUE;
			 for (Object neighNode :  neighs) {
				 if (closedSet.contains(neighNode))
					 continue;
				 Double tentativeGscore = valCurr + graph.getEdgeWeight( graph.getEdge(current, neighNode));
				 if (! openSet.contains(neighNode)) 
					 openSet.add((IShape) neighNode);
			 	
				cameFrom.put(neighNode, current);
				gScore.put(neighNode, tentativeGscore);
				fScore.put(neighNode, tentativeGscore + hValue((IShape)neighNode, target));
				// }
			 }
			 if (finish)
				 break;
		 }
	}

	 
 private void reconstructPath(GamaMap cameFrom, Object currentNode, GamaGraph graph) {
	 Object obj = cameFrom.get(currentNode);
	 if (obj != null) {
		 reconstructPath(cameFrom, obj, graph);
		 edgeList.add((IShape) graph.getEdge(obj, currentNode));
	 } 
	
}



	public Collection getPathEdgeList() {
		return edgeList;
	}
	

}
