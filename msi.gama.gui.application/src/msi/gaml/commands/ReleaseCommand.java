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

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;

@symbol(name = { ISymbol.RELEASE }, kind = ISymbolKind.SINGLE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets({
	@facet(name = ISymbol.TARGET, type = { IType.AGENT_STR, IType.LIST_STR }, optional = false),
	@facet(name = ISymbol.RETURNS, type = IType.NEW_TEMP_ID, optional = true) })
public class ReleaseCommand extends AbstractCommandSequence {

	private final IExpression	target;
	private final String		returnString;
	private GamaList<IAgent>	microAgents	= new GamaList<IAgent>();

	private AbstractCommandSequence sequence = null;

	public ReleaseCommand(final IDescription desc) {
		super(desc);
		target = getFacet(ISymbol.TARGET);
		returnString = getLiteral(ISymbol.RETURNS);
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

			if (!releasedMicroAgents.isEmpty()) {
				stack.addVarWithValue(ISymbol.MYSELF, host);
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