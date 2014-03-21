package msi.gaml.expressions;

import static msi.gama.precompiler.ITypeProvider.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.*;

public class NAryOperator extends BinaryOperator {

	public NAryOperator(final IType ret, final GamaHelper exec, final boolean canBeConst, final int tProv,
		final int ctProv, final int iProv, final boolean lazy, final int[] expectedContentType,
		final Signature signature) {
		super(ret, exec, canBeConst, tProv, ctProv, iProv, lazy, expectedContentType, signature);
	}

	@Override
	public boolean isConst() {
		if ( !canBeConst ) { return false; }
		for ( int i = 0; i < exprs.length; i++ ) {
			if ( !exprs[i].isConst() ) { return false; }
		}
		return true;
	}

	@Override
	protected IType computeType(final IDescription context, final int t, final IType def, final int kind) {
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
		return super.computeType(context, t, def, kind);
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object[] values = new Object[exprs.length];
		try {
			if ( lazy ) {
				for ( int i = 0; i < values.length - 1; i++ ) {
					values[i] = exprs[i].value(scope);
				}
				values[values.length - 1] = exprs[exprs.length - 1];
			} else {
				for ( int i = 0; i < values.length; i++ ) {
					values[i] = exprs[i].value(scope);
				}
			}
			Object result = helper.run(scope, values);
			return result;
		} catch (GamaRuntimeException e1) {
			e1.addContext("when applying the " + literalValue() + " operator on " + values);
			throw e1;
		} catch (Exception e) {
			GamaRuntimeException ee = GamaRuntimeException.create(e);
			ee.addContext("when applying the " + literalValue() + " operator on " + values);
			throw ee;
		}
	}

	@Override
	public String toGaml() {
		return literalValue() + parenthesize(exprs);
	}

	@Override
	public NAryOperator copy() {
		// FIXME Use prototypes not copies like this...
		NAryOperator copy =
			new NAryOperator(type, helper, canBeConst, typeProvider, contentTypeProvider, keyTypeProvider, lazy,
				expectedContentType, signature);
		copy.doc = doc;
		return copy;
	}

}
