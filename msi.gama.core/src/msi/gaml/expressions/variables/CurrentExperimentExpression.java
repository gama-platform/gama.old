/*******************************************************************************************************
 *
 * CurrentExperimentExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.variables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.Types;

/**
 * The Class MyselfExpression.
 */
public class CurrentExperimentExpression extends VariableExpression {

	/**
	 * Instantiates a new myself expression.
	 *
	 * @param type
	 *            the type
	 * @param definitionDescription
	 *            the definition description
	 */
	public CurrentExperimentExpression() {
		super(IKeyword.EXPERIMENT, Types.get(IKeyword.EXPERIMENT), true, null);
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {}

	@Override
	public String getTitle() { return "pseudo variable " + getName() + " of type " + getGamlType().getName(); }

	@Override
	public Doc getDocumentation() { return new ConstantDoc("Represents and gives acces to the current experiment"); }

	@Override
	protected Object _value(final IScope scope) {
		return scope == null ? null : scope.getExperiment();
	}

}