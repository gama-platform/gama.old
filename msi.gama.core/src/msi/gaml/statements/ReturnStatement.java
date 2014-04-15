/*********************************************************************************************
 * 
 *
 * 'ReturnStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
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
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@symbol(name = IKeyword.RETURN, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, unique_in_context = true)
@inside(symbols = IKeyword.ACTION, kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = { @facet(name = IKeyword.VALUE, type = IType.NONE, optional = true) }, omissible = IKeyword.VALUE)
public class ReturnStatement extends AbstractStatement {

	final IExpression value;

	public ReturnStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final Object result = value == null ? null : value.value(scope);
		scope.interruptAction();
		return result;
	}
	//
	// @Override
	// public IType getType() {
	// return value == null ? Types.NO_TYPE : value.getType();
	// }
	//
	// @Override
	// public IType getContentType() {
	// return value == null ? Types.NO_TYPE : value.getContentType();
	// }
	//
	// @Override
	// public IType getKeyType() {
	// return value == null ? Types.NO_TYPE : value.getKeyType();
	// }

}
