package msi.gama.metamodel.agent;

import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

@species(name = "graph_node")
public class GraphNode extends GamlAgent {

	public static class NodeRelation implements VertexRelationship<GraphNode> {

		IScope scope;

		public NodeRelation(final IScope scope) {
			this.scope = scope;
		}

		@Override
		public boolean related(final GraphNode p1, final GraphNode p2) {
			return p1.distance_to(scope, p2) > 0;
		}

		@Override
		public boolean equivalent(final GraphNode p1, final GraphNode p2) {
			return p1.distance_to(scope, p2) <= 0;
		}

	};

	public GraphNode(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

	@action(name = "distance_to", virtual = true, args = { @arg(name = "other", optional = false, type = { IType.AGENT_STR }) })
	public Integer distanceTo(final IScope scope) {

		return 0;
	}

	public Integer distance_to(final IScope scope, final IAgent other) {
		// concrete call to the redefined action in GAML so that it can be called from Java...
		IStatement.WithArgs action = getSpecies().getAction("distance_to");
		scope.addVarWithValue("other", other);
		Object result = scope.execute(action, this);
		return (Integer) result;
	}

}
