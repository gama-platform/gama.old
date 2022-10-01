/*******************************************************************************************************
 *
 * MyselfExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.variables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class MyselfExpression.
 */
public class MyselfExpression extends TempVariableExpression {

	/**
	 * Instantiates a new myself expression.
	 *
	 * @param type
	 *            the type
	 * @param definitionDescription
	 *            the definition description
	 */
	public MyselfExpression(final IType<?> type, final IDescription definitionDescription) {
		super(IKeyword.MYSELF, type, definitionDescription);
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {}

	@Override
	public String getTitle() { return "pseudo variable " + getName() + " of type " + getGamlType().getTitle(); }

	@Override
	public Doc getDocumentation() {
		final IDescription desc = getDefinitionDescription();
		return new ConstantDoc("pseudo variable " + getName() + " of type " + getGamlType().getTitle()
				+ (desc == null ? "<br>Built in" : "<br>Defined in " + desc.getTitle()));
	}

}