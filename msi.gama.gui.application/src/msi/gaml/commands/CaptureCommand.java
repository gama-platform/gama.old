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
package msi.gaml.commands;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.SpeciesDescription;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.agents.IGamlAgent;

@symbol(name = { ISymbol.CAPTURE }, kind = ISymbolKind.SINGLE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets({
	@facet(name = ISymbol.TARGET, type = { IType.AGENT_STR, IType.LIST_STR }, optional = false),
	@facet(name = ISymbol.AS, type = IType.ID, optional = false),
	@facet(name = ISymbol.RETURNS, type = IType.NEW_TEMP_ID, optional = true) })
public class CaptureCommand extends AbstractCommandSequence {

	private IExpression		target;
	private final String	returnString;
	private String			microSpeciesName	= null;
	private List<IAgent>	microAgents			= new GamaList<IAgent>();

	private AbstractCommandSequence sequence = null;

	public CaptureCommand(final IDescription desc) throws GamlException {
		super(desc);
		target = getFacet(ISymbol.TARGET);
		microSpeciesName = getLiteral(ISymbol.AS);
		returnString = getLiteral(ISymbol.RETURNS);
		verifyMicroSpecies();
		setName(ISymbol.CAPTURE + " " + getFacet(ISymbol.TARGET).toGaml());
	}

	private void verifyMicroSpecies() throws GamlException {
		SpeciesDescription macroSpecies = this.getDescription().getSpeciesContext();

		SpeciesDescription microSpecies = macroSpecies.getMicroSpecies(microSpeciesName);
		if ( microSpecies == null ) { throw new GamlException(macroSpecies.getName() +
			" species doesn't contain " + microSpeciesName + " as micro-species", this
			.getDescription().getSourceInformation()); }
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
		IAgent macroAgent = stack.getAgentScope();

		Object t = target.value(stack);
		if ( t == null ) {
			stack.setStatus(ExecutionStatus.failure);
			return null;
		}

		if ( t instanceof List ) {
			for ( Object o : (List) t ) {
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
			ISpecies microSpecies = macroAgent.getSpecies().getMicroSpecies(microSpeciesName);

			if ( microSpecies != null ) {
				for ( IAgent c : microAgents ) {
					if ( !macroAgent.canCapture(c, microSpecies) ) {
						removedComponents.add(c);
					}
				}

				if ( !removedComponents.isEmpty() ) {
					microAgents.removeAll(removedComponents);
					removedComponents.clear();
				}
			}

			if ( !microAgents.isEmpty() ) {
				capturedAgents = macroAgent.captureMicroAgents(microSpecies, microAgents);
				microAgents.clear();

				if (!capturedAgents.isEmpty()) {
					stack.addVarWithValue(ISymbol.MYSELF, macroAgent);
					if ( !sequence.isEmpty() ) {
						for ( IAgent capturedA : capturedAgents ) {
							stack.execute(sequence, capturedA);
						}
					}
				}
			}
		}
		
		if ( returnString != null ) {
			stack.setVarValue(returnString, capturedAgents);
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