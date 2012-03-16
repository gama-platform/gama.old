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

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.RELEASE }, kind = ISymbolKind.SEQUENCE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets(value = {
	@facet(name = IKeyword.TARGET, type = { IType.AGENT_STR, IType.LIST_STR }, optional = false),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = true) }, omissible = IKeyword.TARGET)
public class ReleaseCommand extends AbstractCommandSequence {

	private final IExpression target;
	private final String returnString;
	private GamaList<IAgent> microAgents = new GamaList<IAgent>();

	private AbstractCommandSequence sequence = null;

	public ReleaseCommand(final IDescription desc) {
		super(desc);
		verifyFacetType(IKeyword.TARGET);
		target = getFacet(IKeyword.TARGET);
		returnString = getLiteral(IKeyword.RETURNS);
	}

	@Override
	public void enterScope(final IScope stack) {
		if ( returnString != null ) {
			stack.addVarWithValue(returnString, null);
		}
		super.enterScope(stack);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new AbstractCommandSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		Object t = target.value(stack);
		if ( t == null ) {
			stack.setStatus(ExecutionStatus.failure);
			return null;
		}

		if ( t instanceof List ) {
			for ( Object o : (List) t ) {
				if ( o instanceof IAgent ) {
					microAgents.add((IAgent) o);
				}
			}
		} else {
			microAgents.add((IAgent) t);
		}

		List<IAgent> releasedMicroAgents = GamaList.EMPTY_LIST;
		if ( !microAgents.isEmpty() ) {
			IAgent host = stack.getAgentScope();
			releasedMicroAgents = host.releaseMicroAgents(microAgents);
			microAgents.clear();

			if ( !releasedMicroAgents.isEmpty() ) {
				stack.addVarWithValue(IKeyword.MYSELF, host);
				if ( !sequence.isEmpty() ) {
					for ( IAgent releasedA : releasedMicroAgents ) {
						stack.execute(sequence, releasedA);
					}
				}
			}
		}

		if ( returnString != null ) {
			stack.setVarValue(returnString, releasedMicroAgents);
		}

		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		microAgents.clear();
		microAgents = null;
	}
}