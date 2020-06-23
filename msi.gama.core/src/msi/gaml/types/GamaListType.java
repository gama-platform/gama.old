/*******************************************************************************************************
 *
 * msi.gaml.types.GamaListType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.awt.Color;
import java.util.Collection;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;

@type (
		name = IKeyword.LIST,
		id = IType.LIST,
		wraps = { IList.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.CONTAINER, IConcept.LIST },
		doc = @doc ("Ordered collection of values or agents"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaListType extends GamaContainerType<IList> {

	@Override
	public IList cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, contentsType, copy);
	}

	public static IList staticCast(final IScope scope, final Object obj, final IType ct, final boolean copy)
			throws GamaRuntimeException {
		final IType contentsType = ct == null ? Types.NO_TYPE : ct;
		// BG fix issue ##2338
		// if (obj == null) { return GamaListFactory.create(Types.NO_TYPE, 0); }
		if (obj == null) { return GamaListFactory.create(ct, 0); }

		if (obj instanceof GamaDate) { return ((GamaDate) obj).listValue(scope, ct); }
		if (obj instanceof IContainer) {
			if (obj instanceof IPopulation) {
				// Explicitly set copy to true if we deal with a population
				return ((IPopulation) obj).listValue(scope, contentsType, true);
			}
			return ((IContainer) obj).listValue(scope, contentsType, copy);
		}
		// Dont copy twice the collection
		if (obj instanceof Collection) { return GamaListFactory.create(scope, contentsType, (Collection) obj); }
		if (obj instanceof Color) {
			final Color c = (Color) obj;
			return GamaListFactory.create(scope, contentsType, new int[] { c.getRed(), c.getGreen(), c.getBlue() });
		}
		if (obj instanceof GamaPoint) {
			final GamaPoint point = (GamaPoint) obj;
			return GamaListFactory.create(scope, contentsType, new double[] { point.x, point.y, point.z });
		}
		if (obj instanceof String) {
			return GamaListFactory.create(scope, contentsType, StringUtils.tokenize((String) obj));
		}
		return GamaListFactory.create(scope, contentsType, new Object[] { obj });
	}

	@Override
	public IType getKeyType() {
		return Types.get(INT);
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression expr) {
		switch (expr.getGamlType().id()) {
			case COLOR:
			case DATE:
				return Types.get(INT);
			case POINT:
				return Types.get(FLOAT);
		}
		return super.contentsTypeIfCasting(expr);
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}
}
