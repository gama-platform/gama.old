package msi.gaml.types;

import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaRegression;

@type(name = "regression",
id = IType.REGRESSION,
wraps = {GamaRegression.class },
kind = ISymbolKind.Variable.REGULAR)
public class GamaRegressionType extends GamaType<GamaRegression> {

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public GamaRegression cast(IScope scope, Object obj, Object param,
			boolean copy) throws GamaRuntimeException {
		if ( obj instanceof GamaRegression ) { return (GamaRegression) obj; }
		return null;
	}

	@Override
	public GamaRegression getDefault() {
		return null;
	}

}
