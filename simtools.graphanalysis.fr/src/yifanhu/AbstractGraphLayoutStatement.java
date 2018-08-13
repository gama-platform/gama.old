package yifanhu;

import java.util.Iterator;
import java.util.Set;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.graph.IGraph;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatement;

public abstract class AbstractGraphLayoutStatement extends AbstractStatement {

	public AbstractGraphLayoutStatement(final IDescription desc) {
		super(desc);
		// TODO Auto-generated constructor stub
	}

	static ProjectController pc;
	static Workspace workspace;
	static GraphModel graph_model;
	static UndirectedGraph the_graph;
	static boolean initialized = false;
	static int node_index_diff = 0;

	// Initializing procedure
	public static void initializing() {
		if (!initialized) {
			pc = Lookup.getDefault().lookup(ProjectController.class);
			pc.newProject();
			workspace = pc.getCurrentWorkspace();
			graph_model = Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
			the_graph = graph_model.getUndirectedGraph();
		}
		the_graph.clear();
		// System.gc();
	}

	// Initializing (nodes's locations retrieval) the GraphModel (Gephi) from
	// the IGraph (Gama)
	@SuppressWarnings({ "rawtypes" })
	public static void initializing_GraphModel(final IGraph ig) {
		// show_the_graph();
		final Set<GamlAgent> ga = ig._internalVertexMap().keySet();
		final Iterator<GamlAgent> gi = ga.iterator();
		GamlAgent the_next;
		for (int i = 1; i <= the_graph.getNodeCount(); i++) {
			the_next = gi.next();
			the_graph.getNode(node_index_diff + i).getNodeData().setX((float) the_next.getLocation().getX());
			the_graph.getNode(node_index_diff + i).getNodeData().setY((float) the_next.getLocation().getY());
		}
	}

	// Update locations of agents in the IGraph (Game) from those calculated
	// (Gephi / Force-Directed algos)
	// without min and max values for locations
	@SuppressWarnings({ "rawtypes" })
	public static void Update_locations(final IGraph ig) {
		final Set<GamlAgent> ga = ig._internalVertexMap().keySet();
		final Iterator<GamlAgent> gi = ga.iterator();
		for (int i = 1; i <= ig.getVertices().size(); i++) {
			gi.next().setLocation(new GamaPoint(the_graph.getNode(node_index_diff + i).getNodeData().x(),
					the_graph.getNode(node_index_diff + i).getNodeData().y()));
		}
		update_node_index_diff();
	}

	// Update locations of agents in the IGraph (Game) from those calculated
	// (Gephi / Force-Directed algos)
	// with min and max values for locations
	@SuppressWarnings({ "rawtypes" })
	public static void Update_locations(final IGraph ig, final double max_x, final double max_y) {
		final Set<GamlAgent> ga = ig._internalVertexMap().keySet();
		final Iterator<GamlAgent> gi = ga.iterator();
		for (int i = 1; i <= ig.getVertices().size(); i++) {
			// DEBUG.LOG("i = "+i+" ; total = "+(node_index_diff+i)+
			// "<= ? "+ ig.getVertices().size());
			if (the_graph.getNode(node_index_diff + i).getNodeData().x() < 0) {
				the_graph.getNode(node_index_diff + i).getNodeData().setX((float) 0.0);
			}
			if (the_graph.getNode(node_index_diff + i).getNodeData().y() < 0) {
				the_graph.getNode(node_index_diff + i).getNodeData().setY((float) 0.0);
			}
			if (the_graph.getNode(node_index_diff + i).getNodeData().x() > max_x) {
				the_graph.getNode(node_index_diff + i).getNodeData().setX((float) max_x);
			}
			if (the_graph.getNode(node_index_diff + i).getNodeData().y() > max_y) {
				the_graph.getNode(node_index_diff + i).getNodeData().setY((float) max_y);
			}
		}
		for (int i = 1; i <= ig.getVertices().size(); i++) {
			gi.next().setLocation(new GamaPoint(the_graph.getNode(node_index_diff + i).getNodeData().x(),
					the_graph.getNode(node_index_diff + i).getNodeData().y()));
		}
		update_node_index_diff();
	}

	@SuppressWarnings("rawtypes")
	public static void Update_locations(final IGraph ig, final double min_x, final double min_y, final double max_x,
			final double max_y) {
		final Set<GamlAgent> ga = ig._internalVertexMap().keySet();
		final Iterator<GamlAgent> gi = ga.iterator();
		for (int i = 1; i <= ig.getVertices().size(); i++) {
			// DEBUG.LOG("i = "+i+" ; total = "+(node_index_diff+i)+
			// "<= ? "+ ig.getVertices().size());
			if (the_graph.getNode(node_index_diff + i).getNodeData().x() < min_x) {
				the_graph.getNode(node_index_diff + i).getNodeData().setX((float) min_x);
			}
			if (the_graph.getNode(node_index_diff + i).getNodeData().y() < min_y) {
				the_graph.getNode(node_index_diff + i).getNodeData().setY((float) min_y);
			}
			if (the_graph.getNode(node_index_diff + i).getNodeData().x() > max_x) {
				the_graph.getNode(node_index_diff + i).getNodeData().setX((float) max_x);
			}
			if (the_graph.getNode(node_index_diff + i).getNodeData().y() > max_y) {
				the_graph.getNode(node_index_diff + i).getNodeData().setY((float) max_y);
			}
		}
		for (int i = 1; i <= ig.getVertices().size(); i++) {
			gi.next().setLocation(new GamaPoint(the_graph.getNode(node_index_diff + i).getNodeData().x(),
					the_graph.getNode(node_index_diff + i).getNodeData().y()));
		}
		update_node_index_diff();
	}

	// Function that convert an IGraph Object in to a GraphModel Object
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void IGraph_to_GraphModel(final IGraph igraph) {
		for (int i = 0; i < igraph.getVertices().size(); i++) {
			final Node temp_node = graph_model.factory().newNode(igraph.getVertices().get(i).toString());
			temp_node.getNodeData().setLabel(igraph.getVertices().get(i).toString());
			the_graph.addNode(temp_node);
		}
		for (int i = 0; i < igraph.getEdges().size(); i++) {
			the_graph.addEdge(the_graph.getNode(igraph.getEdgeSource(igraph.getEdges().get(i)).toString()),
					the_graph.getNode(igraph.getEdgeTarget(igraph.getEdges().get(i)).toString()));
		}
	}

	//
	public static void update_node_index_diff() {
		node_index_diff = node_index_diff + the_graph.getNodeCount();
	}

}
