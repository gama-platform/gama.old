/*******************************************************************************************************
 *
 * msi.gaml.statements.UsingStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * "using" is a statement that allows to set the topology to use by its
 * sub-statements. They can gather it by asking the scope to provide it.
 * 
 * @author drogoul 19 janv. 13
 */
@symbol(name = IKeyword.USING, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, concept = {
		IConcept.TOPOLOGY })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER }, symbols = IKeyword.CHART)
@facets(value = {
		@facet(name = IKeyword.TOPOLOGY, type = IType.TOPOLOGY, optional = false, doc = @doc("the topology")) }, omissible = IKeyword.TOPOLOGY)
@doc(value = "`" + IKeyword.USING
		+ "` is a statement that allows to set the topology to use by its sub-statements. They can gather it by asking the scope to provide it.", usages = {
				@usage(value = "All the spatial operations are topology-dependent (e.g. neighbors are not the same in a continuous and in a grid topology). So `"
						+ IKeyword.USING
						+ "` statement allows modelers to specify the topology in which the spatial operation will be computed.", examples = {
								@example(value = "float dist <- 0.0;", isExecutable = false),
								@example(value = "using topology(grid_ant) {", isExecutable = false),
								@example(value = "	d (self.location distance_to target.location);", isExecutable = false),
								@example(value = "}", isExecutable = false) }) })
public class UsingStatement extends AbstractStatementSequence {

	final IExpression topology;
	final ThreadLocal<ITopology> previous = new ThreadLocal<>();

	/**
	 * Constructor.
	 * 
	 * @param desc,
	 *            the description of the statement.
	 */
	public UsingStatement(final IDescription desc) {
		super(desc);
		topology = getFacet(IKeyword.TOPOLOGY);
		setName("using " + topology.serialize(false));
	}

	/**
	 * When entering the scope, the statement pushes the topology (if not null)
	 * to it and remembers the one that was previously pushed.
	 * 
	 * @see msi.gaml.statements.AbstractStatementSequence#enterScope(msi.gama.runtime.IScope)
	 */
	@Override
	public void enterScope(final IScope scope) {
		super.enterScope(scope);
		final ITopology topo = Cast.asTopology(scope, topology.value(scope));
		if (topo != null) {
			previous.set(scope.setTopology(topo));
		}
	}

	/**
	 * When leaving the scope, the statement replaces its topology by the
	 * previous one.
	 * 
	 * @see msi.gaml.statements.AbstractStatementSequence#leaveScope(msi.gama.runtime.IScope)
	 */

	@Override
	public void leaveScope(final IScope scope) {
		scope.setTopology(previous.get());
		previous.set(null);
		super.leaveScope(scope);
	}

}