/*******************************************************************************************************
 *
 * msi.gaml.types.GamaRegressionType.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.stats;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;


@type(name = "regression", id = IType.REGRESSION, wraps = {
		GamaRegression.class }, kind = ISymbolKind.Variable.REGULAR, concept = { IConcept.TYPE },
				doc = {@doc(value = "Type of variables that enables to learn a regression function and to use it to predict new values")})
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
