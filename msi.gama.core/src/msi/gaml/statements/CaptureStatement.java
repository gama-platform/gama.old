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

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.CAPTURE }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = false, remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
	@facet(doc = @doc("The agent or agents that are bound to be captured"), name = IKeyword.TARGET, type = {
		IType.AGENT_STR, IType.CONTAINER_STR }, optional = false),
	@facet(name = IKeyword.AS, type = IType.SPECIES_STR, optional = true),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = true) }, omissible = IKeyword.TARGET)
public class CaptureStatement extends AbstractStatementSequence {

	private IExpression target;
	private final String returnString;
	private String microSpeciesName = null;
	private IList<IAgent> microAgents = new GamaList<IAgent>();

	private AbstractStatementSequence sequence = null;

	public CaptureStatement(final IDescription desc) {
		super(desc);
		target = getFacet(IKeyword.TARGET);
		microSpeciesName = getLiteral(IKeyword.AS);
		returnString = getLiteral(IKeyword.RETURNS);
		if ( hasFacet(IKeyword.TARGET) ) {
			setName(IKeyword.CAPTURE + " " + getFacet(IKeyword.TARGET).toGaml());
		}
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
		IAgent macroAgent = scope.getAgentScope();
		ISpecies macroSpecies = macroAgent.getSpecies();

		Object t = target.value(scope);
		if ( t == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}

		if ( t instanceof IContainer ) {
			for ( Object o : ((IContainer) t).iterable(scope) ) {
				if ( o instanceof IGamlAgent ) {
					microAgents.add((IGamlAgent) o);
				}
			}
		} else {
			if ( t instanceof IGamlAgent ) {
				microAgents.add((IGamlAgent) t);
			}
		}

		List<IAgent> removedComponents = new GamaList<IAgent>();
		List<IAgent> capturedAgents = GamaList.EMPTY_LIST;

		if ( !microAgents.isEmpty() ) {
			if ( microSpeciesName != null ) { // micro-species name is specified in the "as" facet.
				ISpecies microSpecies = macroSpecies.getMicroSpecies(microSpeciesName);

				if ( microSpecies == null ) { throw new GamaRuntimeException(this.name +
					" can't capture other agents as members of " + microSpeciesName +
					" population because the " + microSpeciesName +
					" population is not visible or doesn't exist."); }

				IPopulation microPopulation = macroAgent.getPopulationFor(microSpecies);
				if ( microPopulation == null ) { throw new GamaRuntimeException(this.name +
					" can't capture other agents as members of " + microSpeciesName +
					" population because the " + microSpeciesName +
					" population is not visible or doesn't exist."); }

				for ( IAgent c : microAgents ) {
					if ( !macroAgent.canCapture(c, microSpecies) ) {
						removedComponents.add(c);
					}
				}

				if ( !removedComponents.isEmpty() ) {
					microAgents.removeAll(removedComponents);
				}

				if ( !microAgents.isEmpty() ) {
					capturedAgents =
						macroAgent.captureMicroAgents(scope, microSpecies, microAgents);
					microAgents.clear();

					if ( !capturedAgents.isEmpty() ) {
						scope.addVarWithValue(IKeyword.MYSELF, macroAgent);
						if ( sequence != null && !sequence.isEmpty() ) {
							for ( IAgent capturedA : capturedAgents ) {
								scope.execute(sequence, capturedA);
							}
						}
					}
				}
			} else { // micro-species name is not specified in the "as" facet.
				ISpecies microSpecies;
				IAgent capturedAgent;

				for ( IAgent c : microAgents ) {
					microSpecies = macroSpecies.getMicroSpecies(c.getSpeciesName());

					if ( microSpecies != null ) {
						capturedAgent = macroAgent.captureMicroAgent(scope, microSpecies, c);
						scope.addVarWithValue(IKeyword.MYSELF, macroAgent);
						if ( !sequence.isEmpty() ) {
							scope.execute(sequence, capturedAgent);
						}

						capturedAgents.add(capturedAgent);
					} else {
						removedComponents.add(c);
					}
				}
			}
		}

		if ( returnString != null ) {
			scope.setVarValue(returnString, capturedAgents);
		}

		// throw GamaRuntimeException if necessary
		if ( !removedComponents.isEmpty() ) {
			List<String> raStr = new GamaList<String>();
			for ( IAgent ra : removedComponents ) {
				raStr.add(ra.getName());
				raStr.add(", ");
			}
			raStr.remove(raStr.size() - 1);

			StringBuffer raB = new StringBuffer();
			for ( String s : raStr ) {
				raB.append(s);
			}

			if ( microSpeciesName != null ) { throw new GamaRuntimeException(macroAgent.getName() +
				" can't capture " + raStr.toString() + " as " + microSpeciesName + " agent"); }
			throw new GamaRuntimeException(macroAgent.getName() + " can't capture " +
				raStr.toString() +
				" as micro-agents because no appripriate micro-population is found to welcome these agents.");
		}

		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		target = null;
		microSpeciesName = null;
		microAgents.clear();
		microAgents = null;
	}
}