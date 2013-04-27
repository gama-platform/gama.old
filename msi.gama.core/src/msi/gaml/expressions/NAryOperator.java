package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.types.IType;

public class NAryOperator extends BinaryOperator {

	public NAryOperator(final IType ret, final GamaHelper exec, final boolean canBeConst, final int tProv,
		final int ctProv, final int iProv, final boolean lazy, int[] expectedContentType) {
		super(ret, exec, canBeConst, tProv, ctProv, iProv, lazy, expectedContentType);
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
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object[] values = new Object[exprs.length];
		try {
			for ( int i = 0; i < values.length; i++ ) {
				values[i] = exprs[i].value(scope);
			}
			Object result = helper.run(scope, values);
			return result;
		} catch (GamaRuntimeException e1) {
			e1.addContext("when applying the " + literalValue() + " operator on " + values);
			throw e1;
		} catch (Exception e) {
			GamaRuntimeException ee = new GamaRuntimeException(e);
			ee.addContext("when applying the " + literalValue() + " operator on " + values);
			throw ee;
		}
	}

	@Override
	public NAryOperator copy() {
		// FIXME Use prototypes not copies like this...
		NAryOperator copy =
			new NAryOperator(type, helper, canBeConst, typeProvider, contentTypeProvider, keyTypeProvider, lazy,
				expectedContentType);
		copy.doc = doc;
		return copy;
	}

}
