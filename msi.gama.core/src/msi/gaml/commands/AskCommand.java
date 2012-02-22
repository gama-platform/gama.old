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
import msi.gama.precompiler.GamlAnnotations.remote_context;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

// A group of commands that can be executed on remote agents.

@symbol(name = IKeyword.ASK, kind = ISymbolKind.SEQUENCE_COMMAND)
@facets(value = {
	@facet(name = IKeyword.TARGET, type = { IType.CONTAINER_STR, IType.AGENT_STR }, optional = false),
	@facet(name = IKeyword.AS, type = { IType.SPECIES_STR }, optional = true) }, omissible = IKeyword.TARGET)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@remote_context
public class AskCommand extends AbstractCommandSequence {

	private AbstractCommandSequence sequence = null;
	private final GamaList targets = new GamaList();
	private final IExpression target;

	public AskCommand(final IDescription desc) {
		super(desc);
		setName("ask " + getFacet(IKeyword.TARGET).toGaml());
		target = getFacet(IKeyword.TARGET);
		verifyFacetType(IKeyword.TARGET);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) throws GamlException {
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
		stack.addVarWithValue(IKeyword.MYSELF, scopeAgent);
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