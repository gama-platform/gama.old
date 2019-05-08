package msi.gama.util.graph.layout;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Spatial;

public class LayoutCircle {
	
	private IGraph<IShape, IShape> graph;
	
	private IShape envelopeGeometry;
	
	public LayoutCircle(IGraph<IShape, IShape> graph, IShape envelopeGeometry) {
		this.graph = graph;
		this.envelopeGeometry = envelopeGeometry;
	}

	public void applyLayout(IScope scope) {
        
        double radius = envelopeGeometry.getCentroid().euclidianDistanceTo(
        		Spatial.Punctal._closest_point_to(envelopeGeometry.getCentroid(), envelopeGeometry.getExteriorRing(scope))); 
        
        int i = 0;
        for (IShape v : graph.vertexSet()) {
            double angle = (360 * i++) / graph.vertexSet().size();
            double x = Maths.cos(angle)*radius + envelopeGeometry.getCentroid().x;
            double y = Maths.sin(angle)*radius + envelopeGeometry.getCentroid().x;
            v.setLocation(new GamaPoint(x,y));
        }
		
		/*
		Circular<V,E> circular = new Circular<V,E>(graph);
		List<V> ordering = graph.getVertices();
		if ((boolean) layoutProperties.getProperty(CircleProperties.OPTIMIZE_CROSSINGS))
			
		try{
			//optimize crossings
		//	ordering = circular.circularOrdering();
			graph.setVertices(ordering);
		}
		catch(Exception ex){
			//ex.printStackTrace();
		}
		
		
		Integer distance= 0;
		if (layoutProperties.getProperty(CircleProperties.DISTANCE) != null)
			distance =  (Integer) layoutProperties.getProperty(CircleProperties.DISTANCE);
		
		CircleLayoutCalc<V> calc = new CircleLayoutCalc<V>();
		
		
		double radius = calc.calculateRadius(graph.getVertices(), distance);

		Map<V, Point2D> vertexPositions = calc.calculatePosition(ordering, radius, new Point2D.Double(0,0));
		
		Drawing<V, E> drawing = new Drawing<>();
		drawing.setVertexMappings(vertexPositions);
		
		drawing.positionEdges(graph.getEdges());
		
		return drawing;
		*/
	}
	
//	/**
//	 * Finds the ordering which minimizes the number of edge crossings in a circular
//	 * drawing
//	 * @return A list of vertices in the calculated order
//	 */
//	public IList<IShape> circularOrdering(IScope scope, IGraph<IShape, IShape> graph){
//
//		//log.info("Executing circular ordering");
//		/*
//		 * Define a wave front node to be adjacent to the last node processed; see Figure 9.3.
//		A wave center node is adjacent to some other node that has already been processed.
//		 */
//
//		//TODO Efikasnije ovo implementirati
//		//Mozda izbaciti graph copy i triangulated
//		//samo cuvati listu triangulated grana
//		//a kreirati novu obicnu refleksijom
//		//najnormalnije ubaciti
//
//		List<IShape> waveFrontNodes = new ArrayList<IShape>();
//		List<IShape> waveCenterNodes = new ArrayList<IShape>();
//		List<IShape> processedNodes = new ArrayList<IShape>();
//		List<IShape> removalList = new ArrayList<IShape>();
//
//		//step one - sort the nodes by ascending degree
//		List<IShape> nodes = new ArrayList<>();
//		graph.vertexSet().stream().sorted((v1,v2) -> 
//			graph.degreeOf(v1) < graph.degreeOf(v2) ? 1 : 
//				(v1.getAgent().getIndex() < v2.getAgent().getIndex() ? -1 : 1));
//
//		//step two set counter to 1
//		int counter = 1;
//
//		//step three - while counter <= n-3
//		int n = nodes.size();
//
//		IShape currentNode = null;
//		
//		IGraph<IShape,IShape> copyGraph = (IGraph<IShape, IShape>) graph.copy(scope);
//
//		while (counter <= n-3){
//
//			//find current node
//
//			if (currentNode == null)
//				currentNode = nodes.get(0);
//			else{
//
//				int lowestDegree = copyGraph.degreeOf(nodes.get(0));
//
//				int currentDegree = lowestDegree;
//				IShape waveCenterNode = null, waveFrontNode = null;
//				IShape testNode = nodes.get(0);
//				int index = 0;
//				
//				while (currentDegree == lowestDegree){
//
//					if (waveFrontNodes.contains(testNode)){
//						waveFrontNode = testNode;
//						break;
//					}
//
//					//currentNode will be set to this if no wave front node has the lowest degree
//					if (waveCenterNodes.contains(testNode))
//						waveCenterNode = testNode;
//
//					testNode = nodes.get(++ index);
//					currentDegree = copyGraph.degreeOf(testNode);
//
//				}
//
//				//step 4 - If a wave front node u has lowest degree, then currentNode = u.
//				if (waveFrontNode!= null)
//					currentNode = waveFrontNode;
//				//step 5 - Else If a wave center node v has lowest degree, then currentNode = v.
//				else if (waveCenterNode != null)
//					currentNode = waveCenterNode;
//				//Else set currentNode to be some node with lowest degree
//				else 
//					currentNode = nodes.get(0);
//			}
//
//			processedNodes.add(currentNode);
//			
//			//Define a wave front node to be adjacent to the last node processed
//			//A wave center node is adjacent to some other node that has already been processed.
//			
//			//adding processed neighbours of the previous current node
//			waveCenterNodes.addAll(waveFrontNodes);
//			waveFrontNodes.clear();
//			List<IShape> currentAdjacent = Graphs.neighborListOf(graph, currentNode); 
//			waveFrontNodes.addAll(currentAdjacent);
//			
//			//step 7 - Visit the adjacent nodes consecutively
//			List<IShape> adjacentVertices = Graphs.neighborListOf(graph,currentNode);  //copyGraph.adjacentVerticesWithTriangulated(currentNode);
//			for (int i = 0; i < adjacentVertices.size() - 1; i++){
//
//				//for each two nodes:
//				IShape v1 = adjacentVertices.get(i);
//				IShape v2 = adjacentVertices.get(i+1);
//
//				//step 8 - If a pair edge exists place the edge into removalList.
//				
//				if (copyGraph.adjacentVertices(v1).contains(v2)){
//					//removalList.add(copyGraph.edgeesBetweenWithTriangulated(v1, v2).get(0));
//					//original graph can't contain triangulated edges, so no need to remove them afterwards
//					//E e = copyGraph.edgeBetween(v1, v2);
////					if (e == null){
////						System.out.println("ima u ajdacent, nema edge");
////						System.out.println(v1 + " " + v2);
////					}
//					
//					removalList.add(copyGraph.edgeBetween(v1, v2));
//				}
//				//step 9 - Else place a triangulation edge between the current pair
//				//of neighbors and also into removalList.
//				else if (!copyGraph.adjacentVerticesWithTriangulated(v1).contains(v2)){
//					//no need to create multiple triangulated edges
//					 copyGraph.addTriangulatedEdge(v1, v2);
//				}
//			}
//			
//
//			
//			//step 10 - Update the location of currentNode’s neighbors in T
//			nodes.clear();
//			nodes.addAll(copyGraph.getVertices());
//
//			//step 11 - Remove currentNode and incident edges from G
//			nodes.remove(currentNode);
//			copyGraph.removeVertex(currentNode);
//			
//			Collections.sort(nodes, new VertexDegreeComparator<V,E>(copyGraph));
//
//			for (Edge<V> e : copyGraph.allAdjacentEdgesWithTriangulated(currentNode))
//				copyGraph.removeEdgeWithTriangulated(e);
//
//			//step 12 - Increment counter by 1
//			counter++;
//
//		}
//		
//		
//		//step 13 - Restore G to its original topology.
//		//create a copy of the original graph, leave the real one intact
//		
//		Graph<V,E> originalGraphCopy = new Graph<V,E>(graph.getVertices(), graph.getEdges());
//		for (E e : removalList){
//			//log.info("Removing edge " + e.getOrigin() + " - " + e.getDestination());
//			originalGraphCopy.removeEdge(e);
//		}
//		
//		
//		//step 14 - Perform a DFS (or a longest path heuristic) on G (copy in this case)
//		Path<V,E> longestPath = GraphTraversal.findLongestPath(originalGraphCopy);
//		
//		List<V> embeddingOrder = longestPath.pathVertivesWithoutDuplicates();
//		//System.out.println(embeddingOrder);
//		
//		/*step 17 - If there are any nodes that have not been placed, then place the remaining nodes
//		into the embedding order with the following priority:
//			(i) between two neighbors, (ii) next to one neighbor, (iii) next to
//			zero neighbors.*/
//		
//			
//		if (embeddingOrder.size() < graph.getVertices().size()){
//			
//			for (V v : graph.getVertices()){
//				
//				if (embeddingOrder.contains(v))
//					continue;
//				
//				//else find where to insert
//				int insertPositionNextToANeighbour = -1;
//				boolean inserted = false;
//				
//				for (int i = 0; i < embeddingOrder.size() - 1; i++){
//					V v1 = embeddingOrder.get(i);
//					V v2 = embeddingOrder.get(i+1);
//					
//					E e1 = graph.edgeBetween(v, v1);
//					E e2 = graph.edgeBetween(v, v2);
//					
//					if (e1 != null && e2 == null)
//						insertPositionNextToANeighbour = i;
//					else if (e1 == null && e2 != null)
//						insertPositionNextToANeighbour = i + 1;
//					else {
//						embeddingOrder.add(i, v);
//						inserted = true;
//						break;
//					}
//				}
//				if (!inserted){
//					if (insertPositionNextToANeighbour != -1)
//						embeddingOrder.add(insertPositionNextToANeighbour, v);
//					else
//						embeddingOrder.add(v);
//				}
//			}
//		}
//		
//		return embeddingOrder;
//
//	}
}
