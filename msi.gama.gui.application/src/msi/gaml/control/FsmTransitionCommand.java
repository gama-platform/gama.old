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

import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.ExecutionContextDescription;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;
import msi.gaml.commands.AbstractCommandSequence;

@symbol(name = FsmTransitionCommand.TRANSITION, kind = ISymbolKind.SEQUENCE_COMMAND)
@inside(symbols = { FsmStateCommand.STATE })
@facets({ @facet(name = ISymbol.WHEN, type = IType.BOOL_STR, optional = true),
	@facet(name = FsmTransitionCommand.TO, type = IType.ID, optional = false) })
public class FsmTransitionCommand extends AbstractCommandSequence {

	// TODO En faire une sous classe de if ?

	final IExpression when;

	/** Constant field TRANSITION. */
	protected static final String TRANSITION = "transition";

	protected static final String TO = "to";

	public FsmTransitionCommand(final IDescription desc) throws GamlException {
		super(desc);
		String stateName = getLiteral(TO);
		
		ExecutionContextDescription context = desc.getSpeciesContext();
		if ( !context.hasBehavior(stateName) ) {
			throw new GamlException("Transition is not correct. State " + stateName + " does not exist. ");
		}
		setName(stateName);
		when = getFacet(ISymbol.WHEN);
	}

	public boolean evaluatesTrueOn(final IScope scope) throws GamaRuntimeException {
		return Cast.asBool(scope, when.value(scope));
		// Normally, the agent is still in the "currentState" scope.
	}

}
