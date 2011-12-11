/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.commands;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.ExecutionStatus;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@symbol(name = ISymbol.RETURN, kind = ISymbolKind.SINGLE_COMMAND)
@inside(symbols = ISymbol.ACTION, kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets(value = { @facet(name = ISymbol.VALUE, type = IType.NONE_STR, optional = false) })
public class ReturnCommand extends AbstractCommand {

	final IExpression	value;

	public ReturnCommand(final IDescription desc) {
		super(desc);
		value = getFacet(VALUE);
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		stack.setStatus(ExecutionStatus.interrupt);
		return value.value(stack);
	}

	@Override
	public IType getReturnType() {
		return value.type();
	}

}
