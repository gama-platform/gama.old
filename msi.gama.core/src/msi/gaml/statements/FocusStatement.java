/*******************************************************************************************************
 *
 * msi.gaml.statements.FocusStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@symbol(name = IKeyword.FOCUS_ON, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.DISPLAY, IConcept.GEOMETRY })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets(value = {
		@facet(name = IKeyword.VALUE, type = IType.NONE, optional = false, doc = @doc("The agent, list of agents, geometry to focus on")) }, omissible = IKeyword.VALUE)
@doc(value = "Allows to focus on the passed parameter in all available displays. Passing 'nil' for the parameter will make all screens return to their normal zoom", usages = {
		@usage(value = "Focuses on an agent, a geometry, a set of agents, etc...)", examples = {
				@example("focus_on my_species(0);") }) })
public class FocusStatement extends AbstractStatement {

	@Override
	public String getTrace(final IScope scope) {
		// We dont trace focus statements
		return "";
	}

	final IExpression value;

	public FocusStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = scope.getAgent();
		if (agent != null && !agent.dead()) {
			final IShape o = Cast.asGeometry(scope, value.value(scope));
			GAMA.getGui().setFocusOn(o);
		}
		return value.value(scope);
	}
}
