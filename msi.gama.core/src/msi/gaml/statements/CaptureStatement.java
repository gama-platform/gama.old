/*********************************************************************************************
 *
 *
 * 'CaptureStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.statements;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.CaptureStatement.CaptureValidator;
import msi.gaml.types.*;

@symbol(name = { IKeyword.CAPTURE },
	kind = ISymbolKind.SEQUENCE_STATEMENT,
	with_sequence = false,
	remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(
	value = {
		@facet(doc = @doc("an expression that is evaluated as an agent or a list of the agent to be captured"),
			name = IKeyword.TARGET,
			type = { IType.AGENT, IType.CONTAINER },
			of = IType.AGENT,
			optional = false),
		@facet(name = IKeyword.AS,
			type = IType.SPECIES,
			optional = true,
			doc = @doc("the species that the captured agent(s) will become, this is a micro-species of the calling agent's species")),
		@facet(name = IKeyword.RETURNS,
			type = IType.NEW_TEMP_ID,
			optional = true,
			doc = @doc("a list of the newly captured agent(s)")) },
	omissible = IKeyword.TARGET)
@doc(value = "Allows an agent to capture other agent(s) as its micro-agent(s).",
	usages = {

		@usage(
			value = "The preliminary for an agent A to capture an agent B as its micro-agent is that the A's species must defined a micro-species which is a sub-species of B's species (cf. [Species161#Nesting_species Nesting species]).",
			examples = { @example(value = "species A {", isExecutable = false),
				@example(value = "...", isExecutable = false), @example(value = "}", isExecutable = false),
				@example(value = "species B {", isExecutable = false), @example(value = "...", isExecutable = false),
				@example(value = "   species C parent: A {", isExecutable = false),
				@example(value = "   ...", isExecutable = false), @example(value = "   }", isExecutable = false),
				@example(value = "...", isExecutable = false), @example(value = "}", isExecutable = false) }),
		@usage(
			value = "To capture all \"A\" agents as \"C\" agents, we can ask an \"B\" agent to execute the following statement:",
			examples = { @example(value = "capture list(B) as: C;", isExecutable = false) }),
		@usage(value = "Deprecated writing:",
			examples = { @example(value = "capture target: list (B) as: C;", isExecutable = false) }) },
	see = { "release" })
@validator(CaptureValidator.class)
public class CaptureStatement extends AbstractStatementSequence {

	public static class CaptureValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {
			final String microSpeciesName = cd.getFacets().getLabel(AS);
			if ( microSpeciesName != null ) {
				final SpeciesDescription macroSpecies = cd.getSpeciesContext();
				final TypeDescription microSpecies = macroSpecies.getMicroSpecies(microSpeciesName);
				if ( microSpecies == null ) {
					cd.error(
						macroSpecies.getName() + " species doesn't contain " + microSpeciesName + " as micro-species",
						IGamlIssue.UNKNOWN_SUBSPECIES, AS, microSpeciesName);
				}
			}

		}
	}

	private IExpression target;
	private final String returnString;
	private String microSpeciesName = null;

	private RemoteSequence sequence = null;

	public CaptureStatement(final IDescription desc) {
		super(desc);
		target = getFacet(IKeyword.TARGET);
		microSpeciesName = getLiteral(IKeyword.AS);
		returnString = getLiteral(IKeyword.RETURNS);
		if ( hasFacet(IKeyword.TARGET) ) {
			setName(IKeyword.CAPTURE + " " + getFacet(IKeyword.TARGET).serialize(false));
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
		sequence = new RemoteSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IList<IAgent> microAgents = GamaListFactory.create(Types.AGENT);
		final IMacroAgent macroAgent = (IMacroAgent) scope.getAgentScope();
		final ISpecies macroSpecies = macroAgent.getSpecies();

		final Object t = target.value(scope);

		if ( t == null ) { return null; }

		if ( t instanceof IContainer ) {
			for ( final Object o : ((IContainer) t).iterable(scope) ) {
				if ( o instanceof IAgent ) {
					microAgents.add((IAgent) o);
				}
			}
		} else {
			if ( t instanceof IAgent ) {
				microAgents.add((IAgent) t);
			}
		}

		final List<IAgent> removedComponents = GamaListFactory.create(Types.AGENT);
		List<IAgent> capturedAgents = GamaListFactory.EMPTY_LIST;

		if ( !microAgents.isEmpty() ) {
			if ( microSpeciesName != null ) { // micro-species name is specified in the "as" facet.
				final ISpecies microSpecies = macroSpecies.getMicroSpecies(microSpeciesName);

				if ( microSpecies == null ) { throw GamaRuntimeException.error(
					this.name + " can't capture other agents as members of " + microSpeciesName +
						" population because the " + microSpeciesName + " population is not visible or doesn't exist.",
					scope); }

				final IPopulation microPopulation = macroAgent.getPopulationFor(microSpecies);
				if ( microPopulation == null ) { throw GamaRuntimeException.error(
					this.name + " can't capture other agents as members of " + microSpeciesName +
						" population because the " + microSpeciesName + " population is not visible or doesn't exist.",
					scope); }

				for ( final IAgent c : microAgents ) {
					if ( !macroAgent.canCapture(c, microSpecies) ) {
						removedComponents.add(c);
					}
				}

				if ( !removedComponents.isEmpty() ) {
					microAgents.removeAll(removedComponents);
				}

				if ( !microAgents.isEmpty() ) {
					capturedAgents = macroAgent.captureMicroAgents(scope, microSpecies, microAgents);
					microAgents.clear();

					if ( !capturedAgents.isEmpty() ) {
						// scope.addVarWithValue(IKeyword.MYSELF, macroAgent);
						if ( sequence != null && !sequence.isEmpty() ) {
							for ( final IAgent capturedA : capturedAgents ) {
								Object[] result = new Object[1];
								if ( !scope.execute(sequence, capturedA, null, result) ) {
									break;
								}
							}
						}
					}
				}
			} else { // micro-species name is not specified in the "as" facet.
				ISpecies microSpecies;
				IAgent capturedAgent;
				// scope.addVarWithValue(IKeyword.MYSELF, macroAgent);
				for ( final IAgent c : microAgents ) {
					microSpecies = macroSpecies.getMicroSpecies(c.getSpeciesName());

					if ( microSpecies != null ) {
						capturedAgent = macroAgent.captureMicroAgent(scope, microSpecies, c);

						if ( sequence != null && !sequence.isEmpty() ) {
							Object[] result = new Object[1];
							scope.execute(sequence, capturedAgent, null, result);
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
			final List<String> raStr = new ArrayList<String>();
			for ( final IAgent ra : removedComponents ) {
				raStr.add(ra.getName());
				raStr.add(", ");
			}
			raStr.remove(raStr.size() - 1);

			final StringBuilder raB = new StringBuilder();
			for ( final String s : raStr ) {
				raB.append(s);
			}

			if ( microSpeciesName != null ) { throw GamaRuntimeException.error(
				macroAgent.getName() + " can't capture " + raStr.toString() + " as " + microSpeciesName + " agent",
				scope); }
			throw GamaRuntimeException.error(
				macroAgent.getName() + " can't capture " + raStr.toString() +
					" as micro-agents because no appropriate micro-population is found to welcome these agents.",
				scope);
		}

		return capturedAgents;
	}

	@Override
	public void dispose() {
		super.dispose();
		target = null;
		microSpeciesName = null;
	}
}