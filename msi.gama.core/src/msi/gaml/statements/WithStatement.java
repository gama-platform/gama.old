/*********************************************************************************************
 * 
 *
 * 'WithStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

@facets(value = { @facet(name = IKeyword.INIT, type = IType.NONE, optional = false) }, omissible = IKeyword.INIT)
@symbol(name = { IKeyword.WITH }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
public class WithStatement extends AbstractPlaceHolderStatement {

	// A placeholder for initializations of create
	public WithStatement(final IDescription desc) {
		super(desc);
	}

}