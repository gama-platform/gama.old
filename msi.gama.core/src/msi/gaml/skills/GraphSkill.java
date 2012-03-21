package msi.gaml.skills;


import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;
import msi.gaml.types.IType;


import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;



@skill({ IKeyword.GRAPH_SKILL })
public class GraphSkill extends Skill {

	@action("load_graph_from")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFile(final IScope scope) throws GamaRuntimeException {		
		return null;
	}
	
	@action("generate_barabasi_graph")
	@args({ "nb_links", "nb_nodes"})
	public IGraph primGenerateBarabasiGraph(final IScope scope) throws GamaRuntimeException {
		Graph graph = new SingleGraph("Barabˆsi-Albert");
		// Between 1 and 3 new links per node added.
		int v= (Integer) scope.getArg("nb_links", IType.INT);
		int nb_nodes= (Integer) scope.getArg("nb_nodes", IType.INT);	
		Generator gen = new BarabasiAlbertGenerator(v);
		// Generate nb_nodes nodes:
		gen.addSink(graph);
		gen.begin();
		for(int i=0; i<nb_nodes; i++) {
		    gen.nextEvents();
		}
		gen.end();
		graph.display();
		return null;
	}
	
	
}
