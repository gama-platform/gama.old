/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 f√©vr. 2010
 * 
 * @todo Description
 * 
 */

// FIXME Progressively remove unused facets
@facets(value = { @facet(name = IKeyword.VAR, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.VALUE, type = { IType.NONE_STR }, optional = false) }, combinations = {
	@combination({ IKeyword.VAR, IKeyword.VALUE }), @combination({ IKeyword.NAME, IKeyword.VALUE }) }, omissible = IKeyword.VAR)
@symbol(name = { IKeyword.SET }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
public class SetStatement extends AbstractStatement {

	protected final IVarExpression varExpr;
	protected final IExpression value;

	public SetStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
		varExpr = (IVarExpression) getFacet(IKeyword.VAR, getFacet(IKeyword.NAME));
		setName(IKeyword.SET + getVarName());
	}

	@Override
	protected Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		Object val = value.value(stack);
		varExpr.setVal(stack, val, isLet());
		stack.setStatus(ExecutionStatus.skipped);
		return val;
	}

	protected boolean isLet() {
		return false;
	}

	public String getVarName() {
		if ( varExpr == null ) { return "unknown variable"; }
		return varExpr.literalValue();
	}

}
