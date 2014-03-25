package ssps.skills;

import java.util.List;

import ssps.algorithm.Dijkstra;
import ssps.graph.Graph;
import ssps.util.P;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.metamodel.topology.graph.GraphTopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph._Edge;
import msi.gama.util.graph._Vertex;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.IPath;
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;

@skill(name = "SSPSGPUSkill")
public class SSPSearchSkill extends Skill {
	public final static String LIVING_SPACE = "living_space";
	public final static String TOLERANCE = "tolerance";
	public final static String LANES_ATTRIBUTE = "lanes_attribute";
	public final static String OBSTACLE_SPECIES = "obstacle_species";

	@getter(LIVING_SPACE)
	public double getLivingSpace(final IAgent agent) {
		return (Double) agent.getAttribute(LIVING_SPACE);
	}

	@setter(LANES_ATTRIBUTE)
	public void setLanesAttribute(final IAgent agent, final String latt) {
		agent.setAttribute(LANES_ATTRIBUTE, latt);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter(LANES_ATTRIBUTE)
	public String getLanesAttribute(final IAgent agent) {
		return (String) agent.getAttribute(LANES_ATTRIBUTE);
	}

	@setter(LIVING_SPACE)
	public void setLivingSpace(final IAgent agent, final double ls) {
		agent.setAttribute(LIVING_SPACE, ls);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter(TOLERANCE)
	public double getTolerance(final IAgent agent) {
		return (Double) agent.getAttribute(TOLERANCE);
	}

	@setter(TOLERANCE)
	public void setTolerance(final IAgent agent, final double t) {
		agent.setAttribute(TOLERANCE, t);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}

	@getter(OBSTACLE_SPECIES)
	public GamaList<ISpecies> getObstacleSpecies(final IAgent agent) {
		return (GamaList<ISpecies>) agent.getAttribute(OBSTACLE_SPECIES);
	}

	@setter(OBSTACLE_SPECIES)
	public void setObstacleSpecies(final IAgent agent,
			final GamaList<ISpecies> os) {
		agent.setAttribute(OBSTACLE_SPECIES, os);
	}

	@action(name = "goto_gpu", args = {
//			@arg(name = "target", type = { IType.POINT, IType.GEOMETRY,
//					IType.AGENT }, optional = false, doc = @doc("the location or entity towards which to move.")),
//			@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
//			@arg(name = "on", type = { IType.LIST, IType.AGENT, IType.GRAPH,
//					IType.GEOMETRY }, optional = true, doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")),
//			@arg(name = "return_path", type = IType.BOOL, optional = true, doc = @doc("if true, return the path followed (by default: false)")),
//			@arg(name = "weigths", type = IType.MAP, optional = true, doc = @doc("Weigths used for the moving.")),
//			@arg(name = LIVING_SPACE, type = IType.FLOAT, optional = true, doc = @doc("min distance between the agent and an obstacle (replaces the current value of living_space)")),
//			@arg(name = TOLERANCE, type = IType.FLOAT, optional = true, doc = @doc("tolerance distance used for the computation (replaces the current value of tolerance)")),
//			@arg(name = LANES_ATTRIBUTE, type = IType.STRING, optional = true, doc = @doc("the name of the attribut of the road agent that determine the number of road lanes (replaces the current value of lanes_attribute)"))
//
			}, doc = @doc(value = "moves the agent towards the target passed in the arguments while considering the other agents in the network (only for graph topology)", returns = "optional: the path followed by the agent.", examples = { @example("do gotoTraffic target: one_of (list (species (self))) speed: speed * 2 on: road_network living_space: 2.0;") }))
	public IPath dijkstraGPUGotoTraffic(final IScope scope)
			throws GamaRuntimeException {
		GamaGraph on = (GamaGraph) scope.getArg("on", IType.GRAPH); // Recuperer
		// /Commencer a construire des vertexs et des arcs
		int vertex_count = 0;
		int edge_count = 0;
		boolean stop_add_vertex = false;
		/*IList<IAgent> edge_list = on.getEdges();
		IList<IAgent> vertex_list = on.getVertices();
		int vertexs[] = new int[on.getVertices().size()];
		int edges[] = new int[on.getEdges().size()];
		float weighs[] = new float[on.getEdges().size()];

		for (IAgent from : vertex_list) {
			for (IAgent to : vertex_list) {
				if (on.getEdge(from, to) != null) {
					if (!stop_add_vertex) {
						vertexs[vertex_count++] = edge_count;
						stop_add_vertex = true;
					}
					weighs[edge_count] = (float) on.getEdgeWeight(on.getEdge(
							from, to));
					edges[edge_count++] = to.getIndex();
				}
			}
			stop_add_vertex = false;
		}
		*/
		
		// Fini de construire des vertexs et des arcs

		/*
		 * Tableau D[] est pour la distance de la source vers chaque sommet
		 */
		float D[] = new float[P.V_MAX];
		Graph graph = new Graph() ;// vertexs, edges, weighs, vertex_count,
	//			edge_count); //
		
		graph.generateRandomGraph(10, 20);
		//Ce graph est pour mon algorithme. Il est
								// construit a partir du graph de GAMA
		
		// For GPU
		Dijkstra algo = new Dijkstra();
		D = algo.searchSPGPU(graph, 0);
		System.out.println("Result GPU: " + java.util.Arrays.toString(D));
		
		return null;
	}

	protected ITopology computeTopology(final IScope scope, final IAgent agent)
			throws GamaRuntimeException {
		final Object on = scope.getArg("on", IType.NONE);
		if (on instanceof GamaGraph) {
			java.lang.System.out.println("graph"
					+ ((GamaGraph) on).getEdges().get(0).getClass().getName());

		} else {
			java.lang.System.out.println("pas un gama graph");
		}

		ITopology topo = Cast.asTopology(scope, on);

		if (topo == null) {
			return scope.getTopology();
		}
		return topo;
	}
}
