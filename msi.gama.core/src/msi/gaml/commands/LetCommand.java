/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.commands;

import msi.gama.common.interfaces.*;


import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 f√©vr. 2010
 * 
 * @todo Description
 * 
 */

@facets(value = { @facet(name = IKeyword.VAR, type = IType.NEW_TEMP_ID, optional = true),
	@facet(name = IKeyword.NAME, type = IType.NEW_TEMP_ID, optional = true),
	@facet(name = IKeyword.VALUE, type = { IType.NONE_STR }, optional = true),
	@facet(name = IKeyword.OF, type = { IType.TYPE_ID }, optional = true),
	@facet(name = IKeyword.TYPE, type = { IType.TYPE_ID }, optional = true) },

combinations = { @combination({ IKeyword.VAR, IKeyword.VALUE }),
	@combination({ IKeyword.NAME, IKeyword.VALUE }), })
@symbol(name = { IKeyword.LET }, kind = ISymbolKind.SINGLE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
public class LetCommand extends SetCommand {

	public LetCommand(final IDescription desc) throws GamlException {
		super(desc);
		setName(IKeyword.LET + " " + varExpr.literalValue());
		varExpr.setType(hasFacet(IKeyword.TYPE) ? desc.getTypeOf(getLiteral(IKeyword.TYPE)) : value
			.type());
		varExpr.setContentType(hasFacet(IKeyword.OF) ? desc.getTypeOf(getLiteral(IKeyword.OF))
			: value.getContentType());
	}

	@Override
	protected Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		Object val = value.value(stack);
		varExpr.setVal(stack, val, true);
		stack.setStatus(ExecutionStatus.skipped);
		return val;
	}

}
