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
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * IfPrototype.
 * 
 * @author drogoul 14 nov. 07
 */
@symbol(name = IKeyword.SWITCH, kind = ISymbolKind.SEQUENCE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets(value = { @facet(name = IKeyword.VALUE, type = IType.NONE_STR, optional = false) }, omissible = IKeyword.VALUE)
public class SwitchCommand extends AbstractCommandSequence {

	public MatchCommand[] matches;
	public MatchCommand defaultMatch;
	final IExpression value;

	/**
	 * The Constructor.
	 * 
	 * @param sim the sim
	 */
	public SwitchCommand(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
		setName("switch" + value.toGaml());

	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		List<MatchCommand> cases = new ArrayList();
		for ( ISymbol c : commands ) {
			if ( c instanceof MatchCommand ) {
				if ( ((MatchCommand) c).getLiteral(IKeyword.KEYWORD).equals(IKeyword.DEFAULT) ) {
					defaultMatch = (MatchCommand) c;
				} else {
					cases.add((MatchCommand) c);
				}
			}
		}
		commands.removeAll(cases);
		commands.remove(defaultMatch);
		matches = cases.toArray(new MatchCommand[0]);
		super.setChildren(commands);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		ExecutionStatus statusBeforeSkipped = success;
		boolean allSkipped = true;
		boolean hasMatched = false;
		Object switchValue = value.value(scope);
		Object lastResult = null;
		for ( int i = 0; i < matches.length; i++ ) {
			if ( matches[i].matches(scope, switchValue) ) {
				lastResult = matches[i].executeOn(scope);
				hasMatched = true;
				ExecutionStatus status = scope.getStatus();
				if ( status != skipped ) {
					if ( status == interrupt ) {
						scope.setStatus(interrupt);
						return lastResult;
					} else if ( status == interrupt_loop_and_switch ) {
						scope.setStatus(success);
						return lastResult;
					}
					allSkipped = false;
					statusBeforeSkipped = status;
				}
			}
			scope.setStatus(allSkipped ? skipped : statusBeforeSkipped);
		}
		if ( !hasMatched && defaultMatch != null ) { return defaultMatch.executeOn(scope); }
		return lastResult;
	}
}