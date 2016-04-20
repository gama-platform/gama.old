/*********************************************************************************************
 * 
 *
 * 'AbstractGraphNodeAgent.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.graph;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.AbstractGui;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;
import msi.gaml.types.IType;

// FIXME: Add all the necessary variables (degree, neighbors, edges)
@species(name = "graph_node", concept = { IConcept.GRAPH, IConcept.NODE })
@vars({ @var(name = IKeyword.MYGRAPH, type = IType.GRAPH) })
public class AbstractGraphNodeAgent extends GamlAgent {

	final static Arguments args = new Arguments();

	public static class NodeRelation implements VertexRelationship<AbstractGraphNodeAgent> {

		IStatement.WithArgs action;

		@Override
		public boolean related(final IScope scope, final AbstractGraphNodeAgent p1, final AbstractGraphNodeAgent p2) {
			args.put("other", ConstantExpressionDescription.create(p2));
			Object[] result = new Object[1];
			scope.execute(getAction(p1), p1, args, result);
			return Cast.asBool(scope, result[0]);
		}

		@Override
		public boolean equivalent(final IScope scope, final AbstractGraphNodeAgent p1, final AbstractGraphNodeAgent p2) {
			return p1 == p2;
		}

		IStatement.WithArgs getAction(final AbstractGraphNodeAgent a1) {
			if ( action == null ) {
				action = a1.getAction();
			}
			return action;
		}

	};

	public AbstractGraphNodeAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

	IStatement.WithArgs getAction() {
		return getSpecies().getAction("related_to");
	}

	@action(name = "related_to", virtual = true, args = { @arg(name = "other", optional = false, type = { IType.AGENT }) })
	public Boolean relatedTo(final IScope scope) {
		scope.getGui().debug("Should never be called !");
		return false;
	}

	@getter(IKeyword.MYGRAPH)
	public GamaGraph getGraph() {
		return (GamaGraph) getTopology().getPlaces();
	}
}
