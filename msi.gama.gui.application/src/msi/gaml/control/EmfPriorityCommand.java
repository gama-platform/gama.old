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
package msi.gaml.control;

import msi.gama.interfaces.IDescription;
import msi.gama.interfaces.ISymbol;
import msi.gama.interfaces.IType;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.commands.AbstractPlaceHolderCommand;

@facets({ @facet(name = ISymbol.WHEN, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.IF, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.VALUE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.ELSE, type = IType.FLOAT_STR, optional = true) })
@inside(symbols = { EmfTaskCommand.TASK })
@symbol(name = { EmfTaskCommand.PRIORITY, EmfTaskCommand.WEIGHT }, kind = ISymbolKind.SINGLE_COMMAND)
public class EmfPriorityCommand extends AbstractPlaceHolderCommand {

	public EmfPriorityCommand(final IDescription desc) {
		super(desc);
	}

}