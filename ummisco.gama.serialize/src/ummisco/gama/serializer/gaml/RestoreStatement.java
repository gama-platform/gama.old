/*******************************************************************************************************
 *
 * RestoreStatement.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gaml;

import static msi.gama.common.interfaces.IKeyword.FROM;
import static msi.gama.common.interfaces.IKeyword.RESTORE;
import static msi.gama.common.interfaces.IKeyword.TARGET;
import static msi.gama.precompiler.ISymbolKind.SEQUENCE_STATEMENT;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.IGamaFile;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import ummisco.gama.serializer.implementations.SerialisedAgentReader;

/**
 * This command is used to restore agents from a file or a string in which they have been saved/serialized
 */

/**
 * The Class CreateStatement.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
@symbol (
		name = IKeyword.RESTORE,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		with_args = false,
		breakable = false,
		concept = { IConcept.SPECIES, IConcept.SERIALIZE },
		remote_context = false)
@inside (
		kinds = { ISymbolKind.BEHAVIOR, SEQUENCE_STATEMENT })
@facets (
		value = { @facet (
				name = TARGET,
				type = { IType.AGENT },
				optional = false,
				doc = @doc ("The agent to restore. Its attributes will be replaced by the ones stored in the file or string. No verification is done regarding the compatibility ")),
				@facet (
						name = FROM,
						type = { IType.STRING, IType.FILE },
						optional = false,
						doc = @doc ("The file or the string from which to restore the agent")) },
		omissible = IKeyword.TARGET)
@doc ("Allows to restore any agent that has been previously serialised or saved to a file, e.g. `string s <- serialize(a, 'json'); ...  restore a from: s; `"
		+ " or `save simulation to: 'sim.gsim' format: `binary`; ... restore simulation from: file('sim.gsim') `")
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class RestoreStatement extends AbstractStatement {

	/** The header. */
	private final IExpression from, target;

	/**
	 * Instantiates a new statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public RestoreStatement(final IDescription desc) {
		super(desc);
		from = getFacet(FROM);
		target = getFacet(TARGET);
		setName(RESTORE);
	}

	/**
	 * Private execute in.
	 *
	 * @param scope
	 *            the scope
	 * @return the i list<? extends I agent>
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public IAgent privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		IAgent agent = Cast.asAgent(scope, target.value(scope));
		if (agent == null) return null;
		if (from == null) return agent;
		Object source = from.value(scope);
		if (source instanceof String string) {
			SerialisedAgentReader.getInstance().restoreFromString(agent, string);
		} else if (source instanceof IGamaFile file) {
			SerialisedAgentReader.getInstance().restoreFromFile(agent, file.getPath(scope));
		}
		return agent;
	}

}