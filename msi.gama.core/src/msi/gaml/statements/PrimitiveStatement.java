/*********************************************************************************************
 *
 *
 * 'PrimitiveStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.compilation.IDescriptionValidator.NullValidator;
import msi.gaml.descriptions.*;
import msi.gaml.skills.ISkill;
import msi.gaml.types.IType;

/**
 * The Class ActionCommand.
 *
 * @author drogoul
 */
@symbol(name = IKeyword.PRIMITIVE, kind = ISymbolKind.BEHAVIOR, with_sequence = true, with_args = true, internal = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL }, symbols = IKeyword.CHART)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.VIRTUAL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true) }, omissible = IKeyword.NAME)
// Necessary to avoid running the validator from ActionStatement
@validator(NullValidator.class)
public class PrimitiveStatement extends ActionStatement {

	// Declaring a null validator because primites dont need to be checked

	private ISkill skill = null;
	private final GamaHelper helper;

	/**
	 * The Constructor.
	 *
	 * @param actionDesc the action desc
	 * @param sim the sim
	 */

	public PrimitiveStatement(final IDescription desc) {
		super(desc);
		helper = getDescription().getHelper();
		skill = desc.getSpeciesContext().getSkillFor(helper.getSkillClass());
		// skill = AbstractGamlAdditions.getSkillInstanceFor(helper.getSkillClass());
	}

	@Override
	public PrimitiveDescription getDescription() {
		return (PrimitiveDescription) description;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		Object result = null;
		scope.stackArguments(actualArgs);
		final IAgent agent = scope.getAgentScope();
		helper.run(scope, agent, skill == null ? agent : skill);
		return result;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		actualArgs = args;
	}
	//
	// @Override
	// public IType getType() {
	// return helper.getReturnType();
	// }

	// FIXME for the moment, only scarce information about primitives
	// @Override
	// public IType getContentType() {
	// return getType().getContentType();
	// }
	//
	// @Override
	// public IType getKeyType() {
	// return getType().getKeyType();
	// }

}
