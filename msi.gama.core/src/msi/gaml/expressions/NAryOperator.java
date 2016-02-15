/*********************************************************************************************
 *
 *
 * 'NAryOperator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import static msi.gama.precompiler.ITypeProvider.*;
import java.util.Arrays;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.types.*;

public class NAryOperator extends AbstractNAryOperator {

	public static IExpression create(final OperatorProto proto, final IExpression ... child) {
		NAryOperator u = new NAryOperator(proto, child);
		if ( u.isConst() ) {
			IExpression e = GAML.getExpressionFactory().createConst(u.value(null), u.getType(), u.serialize(false));
			// System.out.println(" ==== Simplification of " + u.toGaml() + " into " + e.toGaml());
			return e;
		}
		return u;
	}

	public NAryOperator(final OperatorProto proto, final IExpression ... exprs) {
		super(proto, exprs);
	}

	@Override
	protected IType computeType(final int t, final IType def, final int kind) {
		int index = -1;
		int kind_of_index = -1;
		if ( t < INDEXED_TYPES ) {
			if ( t >= TYPE_AT_INDEX ) {
				index = t - TYPE_AT_INDEX;
				kind_of_index = GamaType.TYPE;
			} else if ( t >= CONTENT_TYPE_AT_INDEX ) {
				index = t - CONTENT_TYPE_AT_INDEX;
				kind_of_index = GamaType.CONTENT;
			} else if ( t >= KEY_TYPE_AT_INDEX ) {
				index = t - KEY_TYPE_AT_INDEX;
				kind_of_index = GamaType.KEY;
			}
			if ( index != -1 && index < exprs.length ) {
				IExpression expr = exprs[index];
				switch (kind_of_index) {
					case GamaType.TYPE:
						return expr.getType();
					case GamaType.CONTENT:
						return expr.getType().getContentType();
					case GamaType.KEY:
						return expr.getType().getKeyType();
				}
			}
		}
		return super.computeType(t, def, kind);
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object[] values = new Object[exprs.length];
		try {
			for ( int i = 0; i < values.length; i++ ) {
				values[i] = prototype.lazy[i] ? exprs[i] : exprs[i].value(scope);
			}
			Object result = prototype.helper.run(scope, values);
			return result;
		} catch (GamaRuntimeException e1) {
			e1.addContext("when applying the " + literalValue() + " operator on " + Arrays.toString(values));
			throw e1;
		} catch (Exception e) {
			GamaRuntimeException ee = GamaRuntimeException.create(e, scope);
			ee.addContext("when applying the " + literalValue() + " operator on " + Arrays.toString(values));
			throw ee;
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		StringBuilder sb = new StringBuilder();
		sb.append(literalValue());
		parenthesize(sb, exprs);
		return sb.toString();
	}

	@Override
	public NAryOperator copy() {
		NAryOperator copy = new NAryOperator(prototype, exprs);
		return copy;
	}

}
