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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.commands;

import static msi.gama.runtime.ExecutionStatus.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbolKind;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

/**
 * The Class ActionCommand.
 * 
 * @author drogoul
 */
@symbol(name = IKeyword.ACTION, kind = ISymbolKind.ACTION)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.OF, type = IType.TYPE_ID, optional = true) }, omissible = IKeyword.NAME)
@with_args
public class ActionCommand extends AbstractCommandSequence implements ICommand.WithArgs {

	Arguments actualArgs = new Arguments();
	Arguments formalArgs;

	/**
	 * The Constructor.
	 * 
	 * @param actionDesc the action desc
	 * @param sim the sim
	 */
	public ActionCommand(final IDescription desc) {
		super(desc);
		if ( hasFacet(IKeyword.NAME) ) {
			name = getLiteral(IKeyword.NAME);
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

	protected void verifyArgs(final Arguments args) {
		for ( String arg : formalArgs.keySet() ) {
			if ( formalArgs.getExpr(arg) == null && !args.containsKey(arg) ) {
				error("Missing argument " + arg + " in call to " + getName());
			}
		}
		for ( String arg : args.keySet() ) {
			if ( !formalArgs.containsKey(arg) ) {
				error("Unknown argument" + arg + " in call to " + getName());
			}
		}
	}

}
