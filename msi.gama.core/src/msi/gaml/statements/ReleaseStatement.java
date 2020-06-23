/*******************************************************************************************************
 *
 * msi.gaml.statements.ReleaseStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements;

import java.util.Collections;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@symbol (
		name = { IKeyword.RELEASE },
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		remote_context = true,
		concept = { IConcept.MULTI_LEVEL })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets (
		value = { @facet (
				name = IKeyword.TARGET,
				type = { IType.AGENT, IType.LIST, IType.ATTRIBUTES },
				of = IType.AGENT,
				optional = false,
				doc = @doc ("an expression that is evaluated as an agent/a list of the agents to be released or an agent saved as a map")),
				@facet (
						name = IKeyword.AS,
						type = { IType.SPECIES },
						optional = true,
						doc = @doc ("an expression that is evaluated as a species in which the micro-agent will be released")),
				@facet (
						name = IKeyword.IN,
						type = { IType.AGENT },
						optional = true,
						doc = @doc ("an expression that is evaluated as an agent that will be the macro-agent in which micro-agent will be released, i.e. their new host")),
				@facet (
						name = IKeyword.RETURNS,
						type = IType.NEW_TEMP_ID,
						optional = true,
						doc = @doc ("a new variable containing a list of the newly released agent(s)")) },
		omissible = IKeyword.TARGET)
@doc (
		value = "Allows an agent to release its micro-agent(s). The preliminary for an agent to release its micro-agents is that species of these micro-agents are sub-species of other species (cf. [Species161#Nesting_species Nesting species]). The released agents won't be micro-agents of the calling agent anymore. Being released from a macro-agent, the micro-agents will change their species and host (macro-agent).",
		usages = { @usage (
				value = "We consider the following species. Agents of \"C\" species can be released from a \"B\" agent to become agents of \"A\" species. Agents of \"D\" species cannot be released from the \"A\" agent because species \"D\" has no parent species.",
				examples = { @example (
						value = "species A {",
						isExecutable = false),
						@example (
								value = "...",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false),
						@example (
								value = "species B {",
								isExecutable = false),
						@example (
								value = "...",
								isExecutable = false),
						@example (
								value = "   species C parent: A {",
								isExecutable = false),
						@example (
								value = "   ...",
								isExecutable = false),
						@example (
								value = "   }",
								isExecutable = false),
						@example (
								value = "   species D {",
								isExecutable = false),
						@example (
								value = "   ...",
								isExecutable = false),
						@example (
								value = "   }",
								isExecutable = false),
						@example (
								value = "...",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }),
				@usage (
						value = "To release all \"C\" agents from a \"B\" agent, agent \"C\" has to execute the following statement. The \"C\" agent will change to \"A\" agent. The won't consider \"B\" agent as their macro-agent (host) anymore. Their host (macro-agent) will the be the host (macro-agent) of the \"B\" agent.",
						examples = { @example (
								value = "release list(C);",
								isExecutable = false) }),
				@usage (
						value = "The modeler can specify the new host and the new species of the released agents:",
						examples = @example (
								value = "release list (C) as: new_species in: new host;",
								isExecutable = false)) },
		see = "capture")
@SuppressWarnings ({ "rawtypes" })
public class ReleaseStatement extends AbstractStatementSequence {

	private final IExpression target;
	private final IExpression asExpr;
	private final IExpression inExpr;
	private final String returnString;

	private RemoteSequence sequence = null;

	public ReleaseStatement(final IDescription desc) {
		super(desc);
		target = getFacet(IKeyword.TARGET);
		asExpr = getFacet(IKeyword.AS);
		inExpr = getFacet(IKeyword.IN);
		returnString = getLiteral(IKeyword.RETURNS);
	}

	@Override
	public void enterScope(final IScope stack) {
		if (returnString != null) {
			stack.addVarWithValue(returnString, null);
		}
		super.enterScope(stack);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence = new RemoteSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		final Object t = target.value(scope);
		List<IAgent> releasedMicroAgents;
		final IAgent macroAgent = scope.getAgent();
		if (t instanceof SavedAgent) {
			releasedMicroAgents = releaseAgentPrototype(scope, macroAgent, (SavedAgent) t);
		} else {
			final IList<IAgent> microAgents = GamaListFactory.create(Types.AGENT);
			if (t instanceof IContainer) {
				for (final Object o : ((IContainer) t).iterable(scope)) {
					if (o instanceof IAgent) {
						microAgents.add((IAgent) o);
					}
				}
			} else if (t instanceof IAgent) {
				microAgents.add((IAgent) t);
			}
			microAgents.removeIf(each -> !each.getHost().equals(macroAgent));
			releasedMicroAgents = releaseExistingAgents(scope, macroAgent, microAgents);
		}
		if (!releasedMicroAgents.isEmpty()) {
			// scope.addVarWithValue(IKeyword.MYSELF, macroAgent);
			if (!sequence.isEmpty()) {
				for (final IAgent releasedA : releasedMicroAgents) {
					if (!scope.execute(sequence, releasedA, null).passed()) {
						break;
					}
				}
			}
		}

		if (returnString != null) {
			scope.setVarValue(returnString, releasedMicroAgents);
		}

		return releasedMicroAgents;
	}

	private List<IAgent> releaseAgentPrototype(final IScope scope, final IAgent macroAgent, final SavedAgent saved) {
		if (asExpr == null) {
			GAMA.reportError(scope, GamaRuntimeException
					.error("Cannot release agent as its destination species is not specified", scope), true);
			return Collections.EMPTY_LIST;
		}
		IMacroAgent targetAgent = null;
		ISpecies microSpecies = null;
		if (inExpr == null) {
			final String microSpeciesName = asExpr.literalValue();
			targetAgent = macroAgent.getHost();
			while (targetAgent != null) {
				microSpecies = targetAgent.getSpecies().getMicroSpecies(microSpeciesName);
				if (microSpecies != null) {
					break;
				}
				targetAgent = targetAgent.getHost();
			}
		} else {
			targetAgent = (IMacroAgent) inExpr.value(scope);
			microSpecies = (ISpecies) scope.evaluate(asExpr, targetAgent).getValue();
		}
		if (microSpecies == null) {
			GAMA.reportError(scope,
					GamaRuntimeException.error(
							"Cannot release agent as " + asExpr + " cannot be interpreted as a destination population",
							scope),
					true);
			return Collections.EMPTY_LIST;
		}
		if (targetAgent == null) {
			GAMA.reportError(scope, GamaRuntimeException
					.error("Cannot release agent as the host of its destination population is nil", scope), true);
			return Collections.EMPTY_LIST;
		}
		final IPopulation<? extends IAgent> microSpeciesPopulation = macroAgent.getPopulationFor(microSpecies);
		final IAgent released = saved.restoreTo(scope, microSpeciesPopulation);
		return GamaListFactory.create(scope, Types.AGENT, released);
	}

	public List<IAgent> releaseExistingAgents(final IScope scope, final IAgent macroAgent,
			final IList<IAgent> microAgents) {
		List<IAgent> releasedMicroAgents = GamaListFactory.create();
		IMacroAgent targetAgent;
		ISpecies microSpecies = null;

		if (asExpr != null && inExpr != null) {
			targetAgent = (IMacroAgent) inExpr.value(scope);
			if (targetAgent != null && !targetAgent.equals(macroAgent)) {
				microSpecies = (ISpecies) scope.evaluate(asExpr, targetAgent).getValue();
				releasedMicroAgents = targetAgent.captureMicroAgents(scope, microSpecies, microAgents);
			}
		} else if (asExpr != null && inExpr == null) {
			final String microSpeciesName = asExpr.literalValue();
			targetAgent = macroAgent.getHost();
			while (targetAgent != null) {
				microSpecies = targetAgent.getSpecies().getMicroSpecies(microSpeciesName);
				if (microSpecies != null) {
					break;
				}
				targetAgent = targetAgent.getHost();
			}
			if (microSpecies != null && targetAgent != null) {
				releasedMicroAgents = targetAgent.captureMicroAgents(scope, microSpecies, microAgents);
			}
		} else if (asExpr == null && inExpr != null) {
			targetAgent = (IMacroAgent) inExpr.value(scope);
			if (targetAgent != null && !targetAgent.equals(macroAgent)) {
				releasedMicroAgents = GamaListFactory.create(Types.AGENT);
				for (final IAgent m : microAgents) {
					microSpecies = targetAgent.getSpecies().getMicroSpecies(m.getSpeciesName());
					if (microSpecies != null) {
						releasedMicroAgents.add(targetAgent.captureMicroAgent(scope, microSpecies, m));
					}
				}
			}
		} else if (asExpr == null && inExpr == null) {
			ISpecies microAgentSpec;
			IMacroAgent macroOfMacro;
			releasedMicroAgents = GamaListFactory.create(Types.AGENT);

			for (final IAgent m : microAgents) {
				microAgentSpec = m.getSpecies();
				macroOfMacro = macroAgent.getHost();
				while (macroOfMacro != null) {
					microSpecies = macroOfMacro.getSpecies().getMicroSpecies(microAgentSpec.getName());
					if (microSpecies != null) {
						break;
					}

					macroOfMacro = macroOfMacro.getHost();
				}

				if (macroOfMacro != null && microSpecies != null) {
					releasedMicroAgents.add(macroOfMacro.captureMicroAgent(scope, microSpecies, m));
				} else {
					// TODO throw exception when target population not found to
					// release the agent
					// instead of silently failed lie this!!!
				}

			}
		}

		// TODO change the following code
		return releasedMicroAgents;
	}

}