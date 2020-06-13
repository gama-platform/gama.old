/*******************************************************************************************************
 *
 * msi.gaml.statements.MigrateStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.MigrateStatement.MigrateValidator;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * This command permits agents to migrate from one population/species to another population/species and stay in the same
 * host after the migration.
 *
 * It has two mandatory parameters: + source: can be an agent, a list of agents, a agent's population to be migrated +
 * target: target species/population that source agent(s) migrate to.
 *
 * Species of source agents and target species respect the following constraints: + they are "peer" species (sharing the
 * same direct macro-species) + they have sub-species vs. parent-species relationship.
 */
@symbol (
		name = { IKeyword.MIGRATE },
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		remote_context = true,
		concept = { IConcept.MULTI_LEVEL })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets (
		value = { @facet (
				name = IKeyword.SOURCE,
				type = { IType.AGENT, IType.SPECIES, IType.CONTAINER, IType.ID },
				of = IType.AGENT,
				optional = false,
				doc = @doc ("can be an agent, a list of agents, a agent's population to be migrated")), // workaround
				@facet (
						name = IKeyword.TARGET,
						type = IType.SPECIES,
						optional = false,
						doc = @doc ("target species/population that source agent(s) migrate to.")),
				@facet (
						name = IKeyword.RETURNS,
						type = IType.NEW_TEMP_ID,
						optional = true,
						doc = @doc ("the list of returned agents in a new local variable")) },
		omissible = IKeyword.SOURCE)
@validator (MigrateValidator.class)
@doc (
		value = "This command permits agents to migrate from one population/species to another population/species and stay in the same host after the migration. Species of source agents and target species respect the following constraints: (i) they are \"peer\" species (sharing the same direct macro-species), (ii) they have sub-species vs. parent-species relationship.",
		usages = { @usage (
				value = "It can be used in a 3-levels model, in case where individual agents can be captured into group meso agents and groups into clouds macro agents. migrate is used to allows agents captured by groups to migrate into clouds. See the model 'Balls, Groups and Clouds.gaml' in the library.",
				examples = { @example (
						value = "migrate ball_in_group target: ball_in_cloud;",
						isExecutable = false) }) },
		see = { IKeyword.CAPTURE, IKeyword.RELEASE })
public class MigrateStatement extends AbstractStatementSequence {

	public static class MigrateValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription cd) {
			final String microSpeciesName = cd.getLitteral(TARGET);
			if (microSpeciesName != null) {
				final SpeciesDescription macroSpecies = cd.getSpeciesContext();
				final TypeDescription microSpecies = macroSpecies.getMicroSpecies(microSpeciesName);
				if (microSpecies == null) {
					cd.error(macroSpecies.getName() + " species doesn't contain " + microSpeciesName
							+ " as micro-species", IGamlIssue.UNKNOWN_SPECIES, TARGET, microSpeciesName);
				}
			}

		}
	}

	// private IExpression source;
	private final String source;
	private final String target;
	private final String returnString;
	private AbstractStatementSequence sequence = null;

	public MigrateStatement(final IDescription desc) {
		super(desc);

		source = getLiteral(IKeyword.SOURCE);

		target = getLiteral(IKeyword.TARGET);

		returnString = getLiteral(IKeyword.RETURNS);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		// TODO Verify it is a macro agent
		final IMacroAgent executor = (IMacroAgent) stack.getAgent();
		final IList<IAgent> immigrants = GamaListFactory.create(Types.AGENT);

		final ISpecies targetMicroSpecies = executor.getSpecies().getMicroSpecies(target);
		final ISpecies sourceMicroSpecies = executor.getSpecies().getMicroSpecies(source);

		immigrants.addAll(executor.migrateMicroAgents(stack, sourceMicroSpecies, targetMicroSpecies));

		/*
		 * Object immigrantCandidates = source.value(stack);
		 *
		 * if (immigrantCandidates instanceof ISpecies) { immigrants.addAll(executor.migrateMicroAgent((ISpecies)
		 * immigrantCandidates, targetMicroSpecies)); } else if (immigrantCandidates instanceof IList) {
		 * immigrants.addAll(executor.migrateMicroAgents((IList) immigrantCandidates, targetMicroSpecies)); } else if
		 * (immigrantCandidates instanceof IAgent) { IList<IAgent> m = GamaListFactory.create(Types.AGENT);
		 * m.add((IAgent) immigrantCandidates); immigrants.addAll(executor.migrateMicroAgents(m, targetMicroSpecies)); }
		 */

		if (returnString != null) {
			stack.setVarValue(returnString, immigrants);
		}

		return null;
	}
}
