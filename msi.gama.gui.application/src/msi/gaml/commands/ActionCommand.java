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

import static msi.gama.util.ExecutionStatus.*;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.Arguments;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.precompiler.*;

/**
 * The Class ActionCommand.
 * 
 * @author drogoul
 */
@symbol(name = ISymbol.ACTION, kind = ISymbolKind.ACTION)
@inside(kinds = { ISymbolKind.SPECIES })
@facets({ @facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = ISymbol.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = ISymbol.OF, type = IType.TYPE_ID, optional = true) })
@with_args
public class ActionCommand extends AbstractCommandSequence implements ICommand.WithArgs {

	Arguments	actualArgs	= new Arguments();
	Arguments	formalArgs;

	/**
	 * The Constructor.
	 * 
	 * @param actionDesc the action desc
	 * @param sim the sim
	 */
	public ActionCommand(final IDescription desc) {
		super(desc);
		if ( hasFacet(ISymbol.NAME) ) {
			name = getLiteral(ISymbol.NAME);
		}
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		actualArgs.stack(stack);
		Object result = super.privateExecuteIn(stack);
		if ( stack.getStatus() == interrupt ) {
			stack.setStatus(end);
		}
		return result;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		actualArgs.clear();
		for ( String arg : formalArgs.keySet() ) {
			actualArgs.put(arg, args.getExpr(arg, formalArgs.getExpr(arg)));
		}
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		formalArgs = args;
	}

	// TODO Defaults are not taken into account...

	protected void verifyArgs(final Arguments args) throws GamlException {
		for ( String arg : formalArgs.keySet() ) {
			if ( formalArgs.getExpr(arg) == null && !args.containsKey(arg) ) { throw new GamlException(
				"Missing argument " + arg + " in call to " + getName()); }
		}
		for ( String arg : args.keySet() ) {
			if ( !formalArgs.containsKey(arg) ) { throw new GamlException("Unknown argument" + arg +
				" in call to " + getName()); }
		}
	}

}
