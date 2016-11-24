/*********************************************************************************************
 *
 * 'MatchDefaultStatement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

@symbol(name = { IKeyword.DEFAULT }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, unique_in_context = true, concept = { IConcept.CONDITION })
@inside(symbols = IKeyword.SWITCH)
@doc(value="Used in a switch match structure, the block prefixed by default is executed only if no other block has matched (otherwise it is not).", see= {"switch","match"})
public class MatchDefaultStatement extends MatchStatement {

	public MatchDefaultStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public boolean matches(final IScope scope, final Object switchValue)
		throws GamaRuntimeException {
		return false;
	}

}