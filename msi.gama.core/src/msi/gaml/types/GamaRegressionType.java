/*********************************************************************************************
 *
 * 'GamaRegressionType.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaRegression;

@SuppressWarnings("unchecked")
@type(name = "regression", id = IType.REGRESSION, wraps = {
		GamaRegression.class }, kind = ISymbolKind.Variable.REGULAR, concept = { IConcept.TYPE })
public class GamaRegressionType extends GamaType<GamaRegression> {

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public GamaRegression cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof GamaRegression) {
			return (GamaRegression) obj;
		}
		return null;
	}

	@Override
	public GamaRegression getDefault() {
		return null;
	}

}
