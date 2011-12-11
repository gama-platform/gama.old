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
import msi.gama.precompiler.GamlAnnotations.remote_context;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;

// A group of commands that can be executed on remote agents.

@symbol(name = ISymbol.ASK, kind = ISymbolKind.SEQUENCE_COMMAND)
@facets({
	@facet(name = ISymbol.TARGET, type = { IType.LIST_STR, IType.AGENT_STR }, optional = false),
	@facet(name = ISymbol.AS, type = { IType.SPECIES_STR }, optional = true) })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@remote_context
public class AskCommand extends AbstractCommandSequence {

	private AbstractCommandSequence sequence = null;
	private final GamaList targets = new GamaList();
	private final IExpression target;

	public AskCommand(final IDescription desc) {
		super(desc);
		setName("ask " + getFacet(ISymbol.TARGET).toGaml());
		target = getFacet(ISymbol.TARGET);
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
			targets.addAll((List) t);
		} else {
			targets.add(t);
		}

		IAgent scopeAgent = stack.getAgentScope();
		stack.addVarWithValue(ISymbol.MYSELF, scopeAgent);
		for ( int i = 0, n = targets.size(); i < n; i++ ) {
			final IAgent remoteAgent = (IAgent) targets.get(i);
			if ( !remoteAgent.dead() ) {
				stack.execute(sequence, remoteAgent);
			}
			stack.setStatus(ExecutionStatus.skipped);
		}
		targets.clear();
		return null;
	}

}