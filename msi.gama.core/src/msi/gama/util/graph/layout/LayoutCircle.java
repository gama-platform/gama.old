package msi.gama.util.graph.layout;

import java.util.Collections;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
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

	public void applyLayout(IScope scope, boolean shuffle) {
        
        double radius = envelopeGeometry.getCentroid().euclidianDistanceTo(
        		Spatial.Punctal._closest_point_to(envelopeGeometry.getCentroid(), envelopeGeometry.getExteriorRing(scope))); 
        
        // Optimize node ordering
        List<IShape> orderedNodes = this.minimizeEdgeLength(graph, shuffle);
        
        int i = 0;
        for (IShape v : orderedNodes) {
            double angle = (360 * i++) / graph.vertexSet().size();
            double x = Maths.cos(angle)*radius + envelopeGeometry.getCentroid().x;
            double y = Maths.sin(angle)*radius + envelopeGeometry.getCentroid().x;
            v.setLocation(new GamaPoint(x,y));
        }

	}
	
	private List<IShape> minimizeEdgeLength(IGraph<IShape,IShape> graph, boolean shuffle) {
		/*
		List<IShape> orderedNode = graph.vertexSet().stream().sorted((v1,v2) -> 
			graph.degreeOf(v1) < graph.degreeOf(v2) ? 1 : 
				(v1.getAgent().getIndex() < v2.getAgent().getIndex() ? -1 : 1))
				.collect(Collectors.toList());
				*/
		
		// Not find a simple to implement algorithm
		
		List<IShape> nodes = graph.getVertices();
		if(shuffle) {
			Collections.shuffle(nodes);
		}
		return nodes;
	}
	
}
