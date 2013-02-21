package msi.gama.metamodel.agent;

import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.JavaConstExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;
import msi.gaml.types.IType;

// FIXME: Add all the necessary variables (degree, neighbours, edges)
@species(name = "graph_node")
public class AbstractGraphNode extends GamlAgent {

	public static class NodeRelation implements VertexRelationship<AbstractGraphNode> {

		IScope scope;

		public NodeRelation(final IScope scope) {
			this.scope = scope;
		}

		@Override
		public boolean related(final AbstractGraphNode p1, final AbstractGraphNode p2) {
			return p1.related_to(scope, p2);
		}

		@Override
		public boolean equivalent(final AbstractGraphNode p1, final AbstractGraphNode p2) {
			return p1 == p2;
		}

		// @Override
		// public Double distance(final AbstractGraphNode p1, final AbstractGraphNode p2) {
		// return p1.distance_to(scope, p2);
		// }

	};

	public AbstractGraphNode(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

	// TODO Think about giving this knowledge to nodes
	// public IGraph getGraph() {
	// return (IGraph) population.getTopology().getPlaces();
	// }

	// @action(name = "distance_to", virtual = true, args = { @arg(name = "other", optional = false,
	// type = { IType.AGENT_STR }) })
	// public Double distanceTo(final IScope scope) {
	// return 0d;
	// }

	@action(name = "related_to", virtual = true, args = { @arg(name = "other", optional = false, type = { IType.AGENT_STR }) })
	public Boolean relatedTo(final IScope scope) {
		return false;
	}

	// public Double distance_to(final IScope scope, final IAgent other) {
	// // concrete call to the redefined action in GAML so that it can be called from Java...
	// IStatement.WithArgs action = getSpecies().getAction("distance_to");
	// scope.addVarWithValue("other", other);
	// Object result = scope.execute(action, this);
	// return Cast.asFloat(scope, result);
	// }

	public Boolean related_to(final IScope scope, final IAgent other) {
		// concrete call to the redefined action in GAML so that it can be called from Java...
		IStatement.WithArgs action = getSpecies().getAction("related_to");
		Arguments args = new Arguments();
		args.put("other", new JavaConstExpression(other));
		action.setRuntimeArgs(args);
		Object result = scope.execute(action, this);
		return Cast.asBool(scope, result);
	}
}
