/*******************************************************************************************************
 *
 * GamaPairType.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.DynamicLineString;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gama.util.IMap;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@type (
		name = IKeyword.PAIR,
		id = IType.PAIR,
		wraps = { GamaPair.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE, IConcept.CONTAINER },
		doc = @doc ("Represents a pair of 2 arbitrary elements"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaPairType extends GamaContainerType<GamaPair> {

	@Override
	public GamaPair cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) {
		return staticCast(scope, obj, keyType, contentsType, copy);
	}

	@Override
	public int getNumberOfParameters() {
		return 2;
	}

	/**
	 * Static cast.
	 *
	 * @param scope the scope
	 * @param obj the obj
	 * @param keyType the key type
	 * @param contentsType the contents type
	 * @param copy the copy
	 * @return the gama pair
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public static GamaPair staticCast(final IScope scope, final Object obj, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		Object key, value;
		if (obj instanceof GamaPair) {
			key = ((GamaPair) obj).key;
			value = ((GamaPair) obj).value;
		} else
		// 8/01/14: No more automatic casting between points and pairs (as
		// points can now have 3 coordinates
		// if ( obj instanceof GamaPoint ) { return new GamaPair(((GamaPoint)
		// obj).x, ((GamaPoint) obj).y); }
		if (obj instanceof GamaShape && ((GamaShape) obj).getInnerGeometry() instanceof DynamicLineString) {
			final DynamicLineString g = (DynamicLineString) ((GamaShape) obj).getInnerGeometry();
			key = g.getSource();
			value = g.getTarget();
		} else if (obj instanceof IMap) {
			final IMap m = (IMap) obj;
			key = GamaListFactory.create(scope, m.getGamlType().getKeyType(), m.keySet());
			value = GamaListFactory.create(scope, m.getGamlType().getContentType(), m.values());
		} else if (obj instanceof IList) {
			final IList l = (IList) obj;
			switch (l.size()) {
				case 0:
					key = null;
					value = null;
					break;
				case 1:
					key = l.get(0);
					value = l.get(0);
					break;
				case 2:
					key = l.get(0);
					value = l.get(1);
					break;
				default:
					key = l;
					value = l;
			}

		} else {
			// 8/01/14 : Change of behavior for the default pair: now returns a
			// pair object::object
			key = obj;
			value = obj;
		}
		final IType kt = keyType == null || keyType == Types.NO_TYPE ? GamaType.of(key) : keyType;
		final IType ct = contentsType == null || contentsType == Types.NO_TYPE ? GamaType.of(value) : contentsType;
		return new GamaPair(toType(scope, key, kt, copy), toType(scope, value, ct, copy), kt, ct);
	}

	@Override
	public GamaPair getDefault() {
		return new GamaPair(null, null, Types.NO_TYPE, Types.NO_TYPE);
	}

	@Override
	public IType getContentType() {
		return Types.get(NONE);
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}
	//
	// @Override
	// public boolean hasContents() {
	// return true;
	// }

}
