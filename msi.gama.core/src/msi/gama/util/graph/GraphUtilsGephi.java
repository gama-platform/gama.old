package msi.gama.util.graph;

import java.util.Map;

import msi.gama.metamodel.agent.IAgent;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.MixedGraph;
import org.gephi.graph.api.Node;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

public class GraphUtilsGephi {

	public static Object gephiStaticLocker = new Object();
	private static ProjectController gephiProjectController = null;
	

	public static ProjectController getGephiProjectController () {
		
		synchronized (gephiStaticLocker) {
			if (gephiProjectController == null) {
				gephiProjectController = Lookup.getDefault().lookup(ProjectController.class);
				gephiProjectController.startup();
			}
			return gephiProjectController;
		}
		
	}
	
	
	public static Workspace loadIntoAGephiWorkspace(IGraph gamaGraph) {
		
		Project gephiCurrentProject = null;
		Workspace gephiCurrentWorkspace = null;
		GraphModel graphModel ;
		AttributeModel attributeModel;
		
		// init gephi project
		synchronized (gephiStaticLocker) {
			
			ProjectController pc = getGephiProjectController();
			pc.newProject();
			gephiCurrentProject = pc.getCurrentProject();

			if (gephiCurrentProject == null)
				throw new RuntimeException("unable to init a novel Gephi project, sorry.");
			
			gephiCurrentWorkspace = pc.newWorkspace(gephiCurrentProject);

			if (gephiCurrentWorkspace == null)
				throw new RuntimeException("unable to init a novel Gephi workspace, sorry.");
			
		}
		
		// init attributes
		synchronized (gephiStaticLocker) {
			graphModel = Lookup.getDefault().lookup(GraphController.class).getModel(gephiCurrentWorkspace);
			attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel(gephiCurrentWorkspace);
		}
         
		// configure the resulting graph
		MixedGraph gephiGraph = graphModel.getMixedGraph();
        
		// ... explore the gama graph vertices, and create columns for their attributes
		Map<Object,AttributeColumn> vertexGamaId2gephiColumn = new java.util.HashMap<Object, AttributeColumn>();
		for (Object gamaV : gamaGraph._internalVertexMap().keySet() ) {
			_Vertex gamaVertex = (_Vertex)gamaGraph._internalVertexMap().get(gamaV);
			
			if (gamaV instanceof IAgent) {
				IAgent gamaAgent = (IAgent)gamaV;
				for (Object gamaAttributeName : gamaAgent.getAttributes().keySet()) {
					AttributeColumn gephiColumn = vertexGamaId2gephiColumn.get(gamaAttributeName.toString());
					if (gephiColumn == null) {
				        gephiColumn = attributeModel.getNodeTable().addColumn(gamaAttributeName.toString(), AttributeType.STRING);
				        // TODO everything is string ???
				        vertexGamaId2gephiColumn.put(gamaAttributeName.toString(),gephiColumn);
					}
				}
			}

		}
		
		// add edges
		Map<Object,AttributeColumn> edgeGamaId2gephiColumn = new java.util.HashMap<Object, AttributeColumn>();
		for (Object edgeObj : gamaGraph._internalEdgeMap().keySet() ) {
			_Edge edge = (_Edge)gamaGraph._internalEdgeMap().get(edgeObj);
			
			if (edgeObj instanceof IAgent) {
				IAgent gamaAgent = (IAgent)edgeObj;
				for (Object gamaAttributeName : gamaAgent.getAttributes().keySet()) {
					AttributeColumn gephiColumn = edgeGamaId2gephiColumn.get(gamaAttributeName.toString());
					if (gephiColumn == null) {
				        gephiColumn = attributeModel.getEdgeTable().addColumn(gamaAttributeName.toString(), AttributeType.STRING);
				        // TODO everything is string ???
				        edgeGamaId2gephiColumn.put(gamaAttributeName.toString(),gephiColumn);
					}
				}
			}

			
		}
		
		
		// now add nodes
		for (Object v : gamaGraph._internalVertexMap().keySet() ) {
			_Vertex vertex = (_Vertex)gamaGraph._internalVertexMap().get(v);
			
			final String nameId = v.toString();
			
			// create node
	        Node createdNode = graphModel.factory().newNode(nameId);
	        
	        // set node properties
	        createdNode.getNodeData().setLabel(nameId);
	        
			// actually add the node to the network
			gephiGraph.addNode(createdNode);
	
			if (v instanceof IAgent) {
				IAgent a = (IAgent)v;
				for (Object key : a.getAttributes().keySet()) {
					Object value = a.getAttributes().get(key);
					AttributeColumn gephiColumn = vertexGamaId2gephiColumn.get(key.toString());
					createdNode.getNodeData().getAttributes().setValue(
		        			key.toString(),
		        			value.toString()
		        			);	
				}
			}

		}
		
		// add edges
		for (Object edgeObj : gamaGraph._internalEdgeMap().keySet() ) {
			_Edge edge = (_Edge)gamaGraph._internalEdgeMap().get(edgeObj);
			
			Edge createdEdge = graphModel.factory().newEdge(
					gephiGraph.getNode(edge.getSource().toString()),
					gephiGraph.getNode(edge.getTarget().toString())
					);
			
			// actually add the edge to the network
			gephiGraph.addEdge(createdEdge);
				
			// TODO add attributes			

			
		}
       
        
        return gephiCurrentWorkspace;
	}
}
