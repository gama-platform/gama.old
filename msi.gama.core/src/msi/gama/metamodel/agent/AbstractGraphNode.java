package msi.gama.metamodel.agent;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.expressions.JavaConstExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;
import msi.gaml.types.IType;

// FIXME: Add all the necessary variables (degree, neighbours, edges)
@species(name = "graph_node")
@vars({ @var(name = IKeyword.MYGRAPH, type = IType.GRAPH_STR)})
public class AbstractGraphNode extends GamlAgent {

	final static Arguments args = new Arguments();

	public static class NodeRelation implements VertexRelationship<AbstractGraphNode> {

		IStatement.WithArgs action;

		@Override
		public boolean related(final IScope scope, final AbstractGraphNode p1,
			final AbstractGraphNode p2) {
			args.put("other", new JavaConstExpression(p2));
			return Cast.asBool(scope, scope.execute(getAction(p1), p1, args));
		}

		@Override
		public boolean equivalent(final IScope scope, final AbstractGraphNode p1,
			final AbstractGraphNode p2) {
			return p1 == p2;
		}

		IStatement.WithArgs getAction(final AbstractGraphNode a1) {
			if ( action == null ) {
				action = a1.getAction();
			}
			return action;
		}

	};

	public AbstractGraphNode(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

	IStatement.WithArgs getAction() {
		return getSpecies().getAction("related_to");
	}

	@action(name = "related_to", virtual = true, args = { @arg(name = "other", optional = false, type = { IType.AGENT_STR }) })
	public Boolean relatedTo(final IScope scope) {
		GuiUtils.debug("Should never be called !");
		return false;
	}
	

	@getter(IKeyword.MYGRAPH)
	public GamaGraph getGraph(){
		return (GamaGraph) getTopology().getPlaces();
	}
}
