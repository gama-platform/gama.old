package msi.gama.util.graph;

import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
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
	
	public static final String FIELD_AGENT_TYPE = "_agentType";
	public static final String FIELD_VALUE = "_value";
	public static final String FIELD_EDGE_ID = "id" ;
	

	public static ProjectController getGephiProjectController () {
		
		synchronized (gephiStaticLocker) {
			if (gephiProjectController == null) {
				gephiProjectController = Lookup.getDefault().lookup(ProjectController.class);
				gephiProjectController.startup();
			}
			return gephiProjectController;
		}
		
	}
	
	private static void processGamaAttribute(String gamaKey, Object gamaValue, Node node, Edge edge) {
		
		
		if (gamaValue instanceof GamaColor) {
			
			GamaColor gamaColor = (GamaColor)gamaValue;

			if (node != null)
				node.getNodeData().setColor(
						(float)(gamaColor.getRed()/255.0), 
						(float)(gamaColor.getGreen()/255.0), 
						(float)(gamaColor.getBlue()/255.0)
						);
			

			if (edge != null)
				edge.getEdgeData().setColor(
						(float)(gamaColor.getRed()/255.0), 
						(float)(gamaColor.getGreen()/255.0), 
						(float)(gamaColor.getBlue()/255.0)
						);
		} else {
			if (node != null)
				node.getAttributes().setValue(gamaKey, gamaValue.toString());
			if (edge != null)
				edge.getAttributes().setValue(gamaKey, gamaValue.toString());
			
		}
	}
	
	/**
	 * When the gama graphs contains agents, then columns for each attribute of the specy are added in the 
	 * gephi graph (several different species per graph are accepted). Else the data stored into the graph is stored.
	 * 
	 * @param gamaGraph
	 * @return
	 */
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
		Graph gephiGraph = null;
		if (gamaGraph.isDirected()) {
			gephiGraph = graphModel.getDirectedGraph();
		} else {
			gephiGraph = graphModel.getUndirectedGraph();
		}
	
		// graphModel.getMixedGraph();
		// TODO !!!
		
		// ... always export agent type
        attributeModel.getNodeTable().addColumn(FIELD_AGENT_TYPE, AttributeType.STRING);

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
				
			} else {
				AttributeColumn gephiColumn = vertexGamaId2gephiColumn.get(FIELD_VALUE);
				if (gephiColumn == null) {
			        gephiColumn = attributeModel.getNodeTable().addColumn(FIELD_VALUE, AttributeType.STRING);
			        vertexGamaId2gephiColumn.put(FIELD_VALUE,gephiColumn);
				}
				
			}

		}
		
		// ...configure edges columns
		// ... always export agent type
        attributeModel.getEdgeTable().addColumn(FIELD_AGENT_TYPE, AttributeType.STRING);
		// ... always add an edge id (that would raise errors during reading in some formats
        // not required: does already exists implicitely : 
        // attributeModel.getEdgeTable().addColumn(FIELD_EDGE_ID, AttributeType.STRING);


        // ... and the attributes of agents in edges
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
			} else {
				AttributeColumn gephiColumn = edgeGamaId2gephiColumn.get(FIELD_VALUE);
				if (gephiColumn == null) {
			        gephiColumn = attributeModel.getEdgeTable().addColumn(FIELD_VALUE, AttributeType.STRING);
			        edgeGamaId2gephiColumn.put(FIELD_VALUE,gephiColumn);
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
	
			if (v instanceof IShape) {
				IShape s = (IShape)v;
				ILocation l = s.getLocation();
				createdNode.getNodeData().setX((float)l.getX());
				createdNode.getNodeData().setY((float)l.getY());
				createdNode.getNodeData().setZ((float)l.getZ());
				
			}
			if (v instanceof IAgent) {
				IAgent a = (IAgent)v;
				
				// process specy name
				createdNode.getNodeData().getAttributes().setValue(FIELD_AGENT_TYPE, a.getSpeciesName());
			
				// process generic fields
				for (Object key : a.getAttributes().keySet()) {
					Object value = a.getAttributes().get(key);
					
					processGamaAttribute(
							key.toString(), 
							value, 
							createdNode,
							null
							);
				}
			} else {
				// store the object itself
				createdNode.getNodeData().getAttributes().setValue(FIELD_VALUE, v.toString());

			}

		}
		
		// add edges
		for (Object edgeObj : gamaGraph._internalEdgeMap().keySet() ) {
			_Edge edge = (_Edge)gamaGraph._internalEdgeMap().get(edgeObj);
			
			Edge createdEdge = graphModel.factory().newEdge(
					gephiGraph.getNode(edge.getSource().toString()),
					gephiGraph.getNode(edge.getTarget().toString())
					);
			
			// always add an id for the edge (required for some formats)
	        createdEdge.getAttributes().setValue(FIELD_EDGE_ID, edge.getSource().toString()+"_to_"+edge.getTarget().toString());
			
	        // add all other attributes
			if (edgeObj instanceof IAgent) {
				IAgent a = (IAgent)edgeObj;
				
				createdEdge.getAttributes().setValue(FIELD_AGENT_TYPE, a.getSpeciesName());
				
				for (Object key : a.getAttributes().keySet()) {
					Object value = a.getAttributes().get(key);
					
					processGamaAttribute(
							key.toString(), 
							value, 
							null,
							createdEdge
							);
				}
			} else {
				// store the object itself
				createdEdge.getAttributes().setValue(FIELD_VALUE, edgeObj.toString());

			}
			
			// actually add the edge to the network
			if (!gephiGraph.addEdge(createdEdge)) {
				GAMA.reportError(GamaRuntimeException.error("an edge was ignored, probably because some edges are redondant"));
				continue;
			}

		}
       
        
        return gephiCurrentWorkspace;
	}
}
