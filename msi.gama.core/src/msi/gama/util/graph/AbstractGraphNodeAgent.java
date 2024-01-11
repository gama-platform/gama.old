/*******************************************************************************************************
 *
 * AbstractGraphNodeAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.graph;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Cast;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class AbstractGraphNodeAgent.
 */
// FIXME: Add all the necessary variables (degree, neighbors, edges)
@species (
		name = "graph_node",
		concept = { IConcept.GRAPH, IConcept.NODE },
		doc = @doc ("A base species to use as a parent for species representing agents that are nodes of a graph"))
@vars ({ @variable (
		name = IKeyword.MYGRAPH,
		type = IType.GRAPH,
		doc = @doc ("A reference to the graph containing the agent")) })
@doc ("A base species to use as a parent for species representing agents that are nodes of a graph")
public class AbstractGraphNodeAgent extends GamlAgent {

	/** The Constant args. */
	final static Arguments args = new Arguments();

	/**
	 * The Class NodeRelation.
	 */
	public static class NodeRelation implements VertexRelationship<AbstractGraphNodeAgent> {

		/** The action. */
		IStatement.WithArgs action;

		@Override
		public boolean related(final IScope scope, final AbstractGraphNodeAgent p1, final AbstractGraphNodeAgent p2) {
			args.put("other", ConstantExpressionDescription.create(p2));
			final ExecutionResult result = scope.execute(getAction(p1), p1, args);
			return Cast.asBool(scope, result.getValue());
		}

		@Override
		public boolean equivalent(final IScope scope, final AbstractGraphNodeAgent p1,
				final AbstractGraphNodeAgent p2) {
			return p1 == p2;
		}

		/**
		 * Gets the action.
		 *
		 * @param a1
		 *            the a 1
		 * @return the action
		 */
		IStatement.WithArgs getAction(final AbstractGraphNodeAgent a1) {
			if (action == null) { action = a1.getAction(); }
			return action;
		}

	}

	/**
	 * Instantiates a new abstract graph node agent.
	 *
	 * @param s
	 *            the s
	 * @param index
	 *            the index
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public AbstractGraphNodeAgent(final IPopulation<? extends IAgent> s, final int index) throws GamaRuntimeException {
		super(s, index);
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	IStatement.WithArgs getAction() { return getSpecies().getAction("related_to"); }

	/**
	 * Related to.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 */
	@action (
			doc = @doc ("This operator should never be called"),
			name = "related_to",
			virtual = true,
			args = { @arg (
					doc = @doc ("The other agent"),
					name = "other",
					optional = false,
					type = IType.AGENT) })
	public Boolean relatedTo(final IScope scope) {
		DEBUG.LOG("Should never be called !");
		return false;
	}

	/**
	 * Gets the graph.
	 *
	 * @return the graph
	 */
	@SuppressWarnings ("rawtypes")
	@getter (IKeyword.MYGRAPH)
	public GamaGraph getGraph() { return (GamaGraph) getTopology().getPlaces(); }
}
