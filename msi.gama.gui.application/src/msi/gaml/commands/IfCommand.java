/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;

/**
 * IfPrototype.
 * 
 * @author drogoul 14 nov. 07
 */
@symbol(name = ISymbol.IF, kind = ISymbolKind.SEQUENCE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets(value = { @facet(name = ISymbol.CONDITION, type = IType.BOOL_STR, optional = false) })
public class IfCommand extends AbstractCommandSequence {

	public ICommand alt;
	final IExpression cond;

	/**
	 * The Constructor.
	 * 
	 * @param sim the sim
	 */
	public IfCommand(final IDescription desc) {
		super(desc);
		cond = getFacet(ISymbol.CONDITION);
		setName("if " + cond.toGaml());

	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		for ( ISymbol c : commands ) {
			if ( c instanceof ElseCommand ) {
				alt = (ICommand) c;
			}
		}
		commands.remove(alt);
		super.setChildren(commands);
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		return Cast.asBool(stack, cond.value(stack)) ? super.privateExecuteIn(stack) : alt != null
			? alt.executeOn(stack) : null;
	}
}