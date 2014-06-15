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
import msi.gaml.types.IType;

public class NAryOperator extends AbstractNAryOperator {

	public static IExpression create(final OperatorProto proto, final IExpression ... child) {
		NAryOperator u = new NAryOperator(proto, child);
		if ( u.isConst() ) {
			IExpression e = GAML.getExpressionFactory().createConst(u.value(null), u.getType());
			// System.out.println("				==== Simplification of " + u.toGaml() + " into " + e.toGaml());
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
				kind_of_index = _type;
			} else if ( t >= CONTENT_TYPE_AT_INDEX ) {
				index = t - CONTENT_TYPE_AT_INDEX;
				kind_of_index = _content;
			} else if ( t >= KEY_TYPE_AT_INDEX ) {
				index = t - KEY_TYPE_AT_INDEX;
				kind_of_index = _key;
			}
			if ( index != -1 && index < exprs.length ) {
				IExpression expr = exprs[index];
				switch (kind_of_index) {
					case _type:
						return expr.getType();
					case _content:
						return expr.getType().getContentType();
					case _key:
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
			if ( prototype.lazy ) {
				for ( int i = 0; i < values.length - 1; i++ ) {
					values[i] = exprs[i].value(scope);
				}
				values[values.length - 1] = exprs[exprs.length - 1];
			} else {
				for ( int i = 0; i < values.length; i++ ) {
					values[i] = exprs[i].value(scope);
				}
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
	public String toGaml() {
		return literalValue() + parenthesize(exprs);
	}

	@Override
	public NAryOperator copy() {
		NAryOperator copy = new NAryOperator(prototype, exprs);
		return copy;
	}

}
