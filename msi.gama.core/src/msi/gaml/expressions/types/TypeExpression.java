/*******************************************************************************************************
 *
 * TypeExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.types;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.AbstractExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class TypeExpression.
 *
 * @author drogoul
 * @since 7 sept. 2013
 *
 */
public class TypeExpression extends AbstractExpression {

	/**
	 * Instantiates a new type expression.
	 *
	 * @param type
	 *            the type
	 */
	@SuppressWarnings ("rawtypes")
	public TypeExpression(final IType type) {
		this.type = type;
	}

	@Override
	public IType<?> _value(final IScope scope) throws GamaRuntimeException {
		// Normally never evaluated
		return getDenotedType();
	}

	@Override
	public String getDefiningPlugin() { return type.getDefiningPlugin(); }

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

	@Override
	public boolean isConst() { return type.canCastToConst(); }

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return type.serialize(includingBuiltIn);
	}

	@Override
	public String getTitle() { return type.getTitle(); }

	/**
	 * Method getDocumentation()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#getDocumentation()
	 */
	@Override
	public Doc getDocumentation() { return new ConstantDoc("Represents the data type " + type.getTitle()); }

	@Override
	public IType<?> getGamlType() { return Types.TYPE; }

	@Override
	public IType<?> getDenotedType() { return type; }

	@Override
	public String literalValue() {
		return type.serialize(false);
	}

	@Override
	public boolean isContextIndependant() { return isConst(); }

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

}
