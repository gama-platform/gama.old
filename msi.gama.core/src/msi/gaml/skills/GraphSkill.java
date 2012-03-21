package msi.gaml.skills;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;

@skill({ IKeyword.GRAPH_SKILL })
public class GraphSkill extends Skill {

	@action("load_graph_from")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFile(final IScope scope) throws GamaRuntimeException {
		
		return null;
		
	}
}
