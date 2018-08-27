/*******************************************************************************************************
 *
 * msi.gaml.expressions.NAryOperator.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.GAML;
import msi.gaml.descriptions.OperatorProto;

public class NAryOperator extends AbstractNAryOperator {

	public static IExpression create(final OperatorProto proto, final IExpression... child) {
		final NAryOperator u = new NAryOperator(proto, child);
		if (u.isConst() && GamaPreferences.External.CONSTANT_OPTIMIZATION.getValue()) {
			final IExpression e =
					GAML.getExpressionFactory().createConst(u.getConstValue(), u.getGamlType(), u.serialize(false));
			return e;
		}
		return u;
	}

	public NAryOperator(final OperatorProto proto, final IExpression... exprs) {
		super(proto, exprs);
	}

	@Override
	public NAryOperator copy() {
		final NAryOperator copy = new NAryOperator(prototype, exprs);
		return copy;
	}

}
