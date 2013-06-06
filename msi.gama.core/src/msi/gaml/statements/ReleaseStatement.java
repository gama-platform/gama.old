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
package msi.gaml.statements;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.RELEASE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = { @facet(name = IKeyword.TARGET, type = { IType.AGENT, IType.LIST }, optional = false),
	@facet(name = IKeyword.AS, type = { IType.SPECIES }, optional = true),
	@facet(name = IKeyword.IN, type = { IType.AGENT }, optional = true),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = true) }, omissible = IKeyword.TARGET)
public class ReleaseStatement extends AbstractStatementSequence {

	private final IExpression target;
	private final IExpression asExpr;
	private final IExpression inExpr;
	private final String returnString;

	private AbstractStatementSequence sequence = null;

	public ReleaseStatement(final IDescription desc) {
		super(desc);
		target = getFacet(IKeyword.TARGET);
		asExpr = getFacet(IKeyword.AS);
		inExpr = getFacet(IKeyword.IN);
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
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IList<IAgent> microAgents = new GamaList<IAgent>();
		final Object t = target.value(scope);
		if ( t == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}

		if ( t instanceof IContainer ) {
			for ( final Object o : ((IContainer) t).iterable(scope) ) {
				if ( o instanceof IAgent ) {
					microAgents.add((IAgent) o);
				}
			}
		} else if ( t instanceof IAgent ) {
			microAgents.add((IAgent) t);
		}
		final IAgent macroAgent = scope.getAgentScope();
		final Iterator<IAgent> it = microAgents.iterator();
		while (it.hasNext()) {
			if ( !it.next().getHost().equals(macroAgent) ) {
				it.remove();
			}
		}

		IMacroAgent targetAgent;
		ISpecies microSpecies = null;

		List<IAgent> releasedMicroAgents = GamaList.EMPTY_LIST;
		if ( asExpr != null && inExpr != null ) {
			targetAgent = (IMacroAgent) inExpr.value(scope);
			if ( targetAgent != null && !targetAgent.equals(macroAgent) ) {
				microSpecies = (ISpecies) scope.evaluate(asExpr, targetAgent);
				releasedMicroAgents = targetAgent.captureMicroAgents(scope, microSpecies, microAgents);
			}
		} else if ( asExpr != null && inExpr == null ) {
			final String microSpeciesName = asExpr.literalValue();
			IMacroAgent macroOfMacro = macroAgent.getHost();
			while (macroOfMacro != null) {
				microSpecies = macroOfMacro.getSpecies().getMicroSpecies(microSpeciesName);
				if ( microSpecies != null ) {
					break;
				}

				macroOfMacro = macroOfMacro.getHost();
			}

			if ( microSpecies != null ) {
				releasedMicroAgents = macroOfMacro.captureMicroAgents(scope, microSpecies, microAgents);
			}

		} else if ( asExpr == null && inExpr != null ) {
			targetAgent = (IMacroAgent) inExpr.value(scope);
			if ( targetAgent != null && !targetAgent.equals(macroAgent) ) {
				releasedMicroAgents = new GamaList<IAgent>();
				for ( final IAgent m : microAgents ) {
					microSpecies = targetAgent.getSpecies().getMicroSpecies(m.getSpeciesName());
					if ( microSpecies != null ) {
						releasedMicroAgents.add(targetAgent.captureMicroAgent(scope, microSpecies, m));
					}
				}
			}
		} else if ( asExpr == null && inExpr == null ) {
			ISpecies microAgentSpec;
			IMacroAgent macroOfMacro;
			releasedMicroAgents = new GamaList<IAgent>();

			for ( final IAgent m : microAgents ) {
				microAgentSpec = m.getSpecies();
				macroOfMacro = macroAgent.getHost();
				while (macroOfMacro != null) {
					microSpecies = macroOfMacro.getSpecies().getMicroSpecies(microAgentSpec.getName());
					if ( microSpecies != null ) {
						break;
					}

					macroOfMacro = macroOfMacro.getHost();
				}

				if ( macroOfMacro != null && microSpecies != null ) {
					releasedMicroAgents.add(macroOfMacro.captureMicroAgent(scope, microSpecies, m));
				} else {
					// TODO throw exception when target population not found to release the agent
					// instead of silently failed lie this!!!
				}

			}
		}

		// TODO change the following code

		if ( !releasedMicroAgents.isEmpty() ) {
			scope.addVarWithValue(IKeyword.MYSELF, macroAgent);
			if ( !sequence.isEmpty() ) {
				for ( final IAgent releasedA : releasedMicroAgents ) {
					Object[] result = new Object[1];
					if ( !scope.execute(sequence, releasedA, null, result) ) {
						break;
					}
				}
			}
		}

		// GuiUtils.debug("ReleaseStatement.privateExecuteIn : " + macroAgent + " released " +
		// releasedMicroAgents.size() +
		// " agents");
		if ( returnString != null ) {
			scope.setVarValue(returnString, releasedMicroAgents);
		}

		return releasedMicroAgents;
	}

}