/*********************************************************************************************
 * 
 *
 * 'TestStatement.java', in plugin 'irit.gaml.extensions.test', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package irit.gaml.extensions.test.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@symbol(name = { "test" }, kind = ISymbolKind.BEHAVIOR, with_sequence = true, unique_name = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = true) }, omissible = IKeyword.NAME)
public class TestStatement extends AbstractStatementSequence {

	// We keep the setup in memory to avoid looking for it every time step
	SetUpStatement setup = null;

	// true if the setup has already been looked for, false otherwise
	boolean setupLookedFor = false;

	public TestStatement(final IDescription desc) {
		super(desc);
		if ( hasFacet(IKeyword.NAME) ) {
			setName("test " + getLiteral(IKeyword.NAME));
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( setup == null && !setupLookedFor ) {
			setupLookedFor = true;
			setup = scope.getAgentScope().getSpecies().getStatement(SetUpStatement.class, null);
		}

		if ( setup != null ) {
			setup.setup(scope);
		}
		return super.privateExecuteIn(scope);
	}

}
