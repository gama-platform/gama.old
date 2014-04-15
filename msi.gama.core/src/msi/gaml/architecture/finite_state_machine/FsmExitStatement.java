/*********************************************************************************************
 * 
 *
 * 'FsmExitStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.finite_state_machine;

import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;

@symbol(name = FsmStateStatement.EXIT, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, with_scope = false, unique_in_context = true)
@inside(symbols = { FsmStateStatement.STATE })
public class FsmExitStatement extends AbstractStatementSequence {

	public FsmExitStatement(/* final ISymbol enclosingScope, */final IDescription desc) {
		super(/* enclosingScope, */desc);
	}

	@Override
	public void leaveScope(final IScope scope) {
		// no scope

		// TODO : do the contrary in the future : have the no_scope property looked at by the scope
		// itself
	}

	@Override
	public void enterScope(final IScope scope) {
		// no scope
	}
}