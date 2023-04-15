/*******************************************************************************************************
 *
 * SimulateStatement.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.resource.GamlFile;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AbstractStatementSequence;

/**
 * The Class SimulateStatement.
 */
// @symbol(name = "simulate", kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {})
// @facets (
// value = { @facet (
// name = "comodel",
// type = { IType.FILE },
// optional = false),
// @facet (
// name = "with_experiment",
// type = { IType.STRING },
// optional = true),
// @facet (
// name = "share",
// type = { IType.LIST },
// optional = true),
// @facet (
// name = "with_input",
// type = { IType.MAP },
// optional = true),
// @facet (
// name = "with_output",
// type = { IType.MAP },
// optional = true),
// @facet (
// name = "reset",
// type = { IType.BOOL },
// optional = true),
// @facet (
// name = IKeyword.UNTIL,
// type = IType.BOOL,
// optional = true),
// @facet (
// name = IKeyword.REPEAT,
// type = { IType.INT },
// optional = true) },
// omissible = "comodel")
// @inside (
// kinds = { ISymbolKind.EXPERIMENT, ISymbolKind.SPECIES, ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT },
// symbols = IKeyword.CHART)
// @doc (
// value = "Allows an agent, the sender agent (that can be the [Sections161#global world agent]), to ask another (or
// other) agent(s) to perform a set of statements. "
// + "It obeys the following syntax, where the target attribute denotes the receiver agent(s):",
// examples = { @example (
// value = "ask receiver_agent(s) {",
// isExecutable = false),
// @example (
// value = " // [statements]",
// isExecutable = false),
// @example (
// value = "}",
// isExecutable = false) })
public class SimulateStatement extends AbstractStatementSequence {

	/** The sequence. */
	private AbstractStatementSequence sequence = null;

	/** The comodel. */
	private final IExpression comodel;

	/** The with exp. */
	// private IExperimentPlan exp;
	private IExpression with_exp;

	/** The param input. */
	// private IModel mm = null;
	private IExpression param_input = null;

	/** The param output. */
	private IExpression param_output = null;

	/** The reset. */
	private IExpression reset = null;

	/** The repeat. */
	// private final IOutput exp_output = null;
	private IExpression repeat = null;

	/** The stop condition. */
	private IExpression stopCondition = null;

	/** The shared resource. */
	private IExpression sharedResource = null;

	// private final Map in = new TOrderedHashMap();
	// private final Map out = new TOrderedHashMap();

	/**
	 * Instantiates a new simulate statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public SimulateStatement(final IDescription desc) {
		super(desc);
		comodel = getFacet("comodel");
		if (comodel == null) return;
		setName("simulate " + comodel.serialize(false));

		with_exp = getFacet("with_experiment");

		// exp = null;

		param_input = getFacet("with_input");

		param_output = getFacet("with_output");

		reset = getFacet("reset");

		repeat = getFacet(IKeyword.REPEAT);
		stopCondition = getFacet(IKeyword.UNTIL);
		sharedResource = getFacet("share");

	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) {
		final Object modelfile = comodel.value(scope);
		if (modelfile instanceof GamlFile) {
			((GamlFile) modelfile).execute(scope, with_exp, param_input, param_output, reset, repeat, stopCondition,
					sharedResource);
		}

		// exp.getCurrentSimulation().halt(exp.getAgent().getScope());
		return null;
	}

}