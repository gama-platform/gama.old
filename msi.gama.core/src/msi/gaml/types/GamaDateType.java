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

import java.time.LocalDate;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.IContainer;
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

	public static final GamaDate DEFAULT_STARTING_DATE = GamaDate.absolute(LocalDate.ofEpochDay(0));

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
				return new GamaDate(scope, (GamaDate) obj);
			}
			return (GamaDate) obj;
		}
		if (obj instanceof IContainer) {
			return new GamaDate(scope, ((IContainer<?, ?>) obj).listValue(scope, Types.INT, false));
		}
		if (obj instanceof String) {
			return new GamaDate(scope, (String) obj);
		}
		final int i = Cast.asInt(scope, obj);
		return new GamaDate(scope, i);
	}

	@Override
	public GamaDate getDefault() {
		return null;
	}

	@Override
	public IType<?> getContentType() {
		return Types.get(FLOAT);
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public boolean isCompoundType() {
		return true;
	}

}
