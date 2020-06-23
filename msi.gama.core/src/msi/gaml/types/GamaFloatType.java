/*******************************************************************************************************
 *
 * msi.gaml.types.GamaFloatType.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaFont;
import msi.gaml.descriptions.IDescription;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings("unchecked")
@type(name = IKeyword.FLOAT, id = IType.FLOAT, wraps = { Double.class,
		double.class }, kind = ISymbolKind.Variable.NUMBER, doc = {
				@doc("Represents floating point numbers (equivalent to Double in Java)") }, concept = { IConcept.TYPE })
public class GamaFloatType extends GamaType<Double> {

	@Override
	public Double cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	public static Double staticCast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj instanceof Double) {
			return (Double) obj;
		}
		if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		}
		if (obj instanceof String) {
			try {
				return Double.valueOf((String) obj);
			} catch (final NumberFormatException e) {
				return 0d;
			}
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj ? 1d : 0d;
		}
		if (obj instanceof GamaShape) {
			return ((GamaShape) obj).getArea();
		}
		if (obj instanceof GamaFont) {
			return (double) ((GamaFont) obj).getSize();
		}
		if (obj instanceof GamaDate)
			return ((GamaDate) obj).floatValue(scope);
		return 0d;
	}

	@Override
	public Double getDefault() {
		return 0.0;
	}

	@Override
	public boolean isTranslatableInto(final IType<?> type) {
		return type.isNumber() || type == Types.NO_TYPE;
	}

	@Override
	public IType<?> coerce(final IType<?> type, final IDescription context) {
		if (type == this) {
			return null;
		}
		return this;
	}

	@Override
	public IType<? super Double> findCommonSupertypeWith(final IType<?> type) {
		return type.isNumber() ? this : Types.NO_TYPE;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public boolean isNumber() {
		return true;
	}
}
