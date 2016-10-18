/*********************************************************************************************
 *
 *
 * 'GamaColorType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import java.time.LocalDateTime;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;

/**
 * Written by Patrick Tallandier
 *
 * @todo Description
 *
 */
@SuppressWarnings("unchecked")
@type(name = "date", id = IType.DATE, wraps = { GamaDate.class }, kind = ISymbolKind.Variable.REGULAR, concept = {
		IConcept.TYPE, IConcept.DATE, IConcept.TIME })
public class GamaDateType extends GamaType<GamaDate> {

	public static final GamaDate DEFAULT_STARTING_DATE = new GamaDate(LocalDateTime.of(0, 1, 1, 0, 0));

	@Override
	public GamaDate cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	public static GamaDate staticCast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof GamaDate) {
			if (copy) {
				return new GamaDate((GamaDate) obj);
			}
			return (GamaDate) obj;
		}
		if (obj instanceof IList) {
			return new GamaDate((IList<?>) obj);
		}
		if (obj instanceof IContainer) {
			return staticCast(scope, ((IContainer<?, ?>) obj).listValue(scope, Types.NO_TYPE, false), param, copy);
		}

		if (obj instanceof String) {
			return new GamaDate(scope, (String) obj);
		}
		if (obj instanceof Boolean) {
			return new GamaDate(0);
		}
		final int i = Cast.asInt(scope, obj);
		return new GamaDate(i);
	}

	@Override
	public GamaDate getDefault() {
		return null;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
