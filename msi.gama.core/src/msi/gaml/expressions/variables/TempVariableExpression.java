/*******************************************************************************************************
 *
 * TempVariableExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.variables;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class TempVariableExpression.
 */
public class TempVariableExpression extends VariableExpression {

	/**
	 * Instantiates a new temp variable expression.
	 *
	 * @param n
	 *            the n
	 * @param type
	 *            the type
	 * @param definitionDescription
	 *            the definition description
	 */
	public TempVariableExpression(final String n, final IType<?> type, final IDescription definitionDescription) {
		super(n, type, false, definitionDescription);
	}

	@Override
	public Object _value(final IScope scope) {
		return scope.getVarValue(getName());
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
		final Object val = type.cast(scope, v, null, false);
		if (create) {
			scope.addVarWithValue(getName(), val);
		} else {
			scope.setVarValue(getName(), val);
		}
	}

	@Override
	public String getTitle() { return "temporary variable " + getName() + " of type " + getGamlType().getTitle(); }

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public Doc getDocumentation() {
		final IDescription desc = getDefinitionDescription();
		return new ConstantDoc("temporary variable " + getName() + " of type " + getGamlType().getTitle()
				+ (desc == null ? "<br>Built In" : "<br>Defined in " + desc.getTitle()));
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return GAML.getExpressionFactory().createConst(value(scope), type, name);
	}
}
