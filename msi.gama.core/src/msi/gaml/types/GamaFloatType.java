/*******************************************************************************************************
 *
 * GamaFloatType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ("unchecked")
@type (
		name = IKeyword.FLOAT,
		id = IType.FLOAT,
		wraps = { Double.class, double.class },
		kind = ISymbolKind.Variable.NUMBER,
		doc = { @doc ("Represents floating point numbers (equivalent to Double in Java)") },
		concept = { IConcept.TYPE })
public class GamaFloatType extends GamaType<Double> {

	@Override
	@doc ("Cast the argument into a float number. If the argument is a float, returns it; if it is an int, returns it as a float; if it is a string, tries to extract a double from it; if it is a bool, return 1.0 if true and 0.0 if false; if it is a shape (or an agent) returns its area; if it is a font, returns its size; if it is a date, returns the number of milliseconds since the starting date and time of the simulation ")
	public Double cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	/**
	 * Static cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param param
	 *            the param
	 * @param copy
	 *            the copy
	 * @return the double
	 */
	public static Double staticCast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj instanceof Double) return (Double) obj;
		if (obj instanceof Number) return ((Number) obj).doubleValue();
		if (obj instanceof String) {
			try {
				return Double.valueOf((String) obj);
			} catch (final NumberFormatException e) {
				return 0d;
			}
		}
		if (obj instanceof Boolean) return (Boolean) obj ? 1d : 0d;
		if (obj instanceof IValue v) return v.floatValue(scope);
		return 0d;
	}

	@Override
	public Double getDefault() { return 0.0; }

	@Override
	public boolean isTranslatableInto(final IType<?> type) {
		return type.isNumber() || type == Types.NO_TYPE;
	}

	@Override
	public IType<?> coerce(final IType<?> type, final IDescription context) {
		if (type == this) return null;
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
	public boolean isNumber() { return true; }
}
