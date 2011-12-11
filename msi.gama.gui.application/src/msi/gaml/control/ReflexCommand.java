/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.control;

import java.util.List;

import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.CommandDescription;
import msi.gama.internal.descriptions.SpeciesDescription;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.commands.AbstractCommandSequence;

@symbol(name = { ISymbol.REFLEX, ISymbol.INIT }, kind = ISymbolKind.BEHAVIOR)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = ISymbol.WHEN, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.NAME, type = IType.ID, optional = true) })
public class ReflexCommand extends AbstractCommandSequence {

	private final IExpression when;

	public ReflexCommand(final IDescription desc) {
		super(desc);
		when = getFacet(ISymbol.WHEN);
		if ( hasFacet(ISymbol.NAME) ) {
			setName(getLiteral(ISymbol.NAME));
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( when == null || Cast.asBool(scope, when.value(scope)) ) { 
			return super.privateExecuteIn(scope); 
		}
		
		scope.setStatus(ExecutionStatus.condition_failed);
		return null;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		super.setChildren(commands);
		
		/*
		CommandDescription rd = (CommandDescription) this.getDescription();
		SpeciesDescription sd = (SpeciesDescription) rd.getSuperDescription();
		if (this.getDescription().getName().contains(ISymbol.INIT)) {
			System.out.println("name = " + rd.getName() + "; keyword = " + rd.getKeyword());
			System.out.println("Species name " + sd.getName() + "; keyword = " + sd.getKeyword());
			
			for (ISymbol c : commands) {
				CommandDescription cd = (CommandDescription) c.getDescription();
				System.out.println("ReflexCommand :: child :: name :: " + cd.getName() + "; keyword :: " + cd.getKeyword());
			}
		}
		*/
	}
}
