/*********************************************************************************************
 *
 *
 * 'MapExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.List;
import java.util.Map;

import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * ListValueExpr.
 *
 * @author drogoul 23 août 07
 */
public class MapExpression extends AbstractExpression {

	public static IExpression create(final List<? extends IExpression> elements) {
		final MapExpression u = new MapExpression(elements);
		// if ( u.isConst() && GamaPreferences.CONSTANT_OPTIMIZATION.getValue()
		// ) {
		// IExpression e =
		// GAML.getExpressionFactory().createConst(u.value(null), u.getType(),
		// u.serialize(false));
		// // System.out.println(" ==== Simplification of " + u.toGaml() + "
		// into " + e.toGaml());
		// return e;
		// }
		return u;
	}

	private final IExpression[] keys;
	private final IExpression[] vals;
	// private final GamaMap values;
	// private boolean isConst, computed;

	MapExpression(final List<? extends IExpression> pairs) {
		keys = new IExpression[pairs.size()];
		vals = new IExpression[pairs.size()];
		for (int i = 0, n = pairs.size(); i < n; i++) {
			final IExpression e = pairs.get(i);
			if (e instanceof BinaryOperator) {
				final BinaryOperator pair = (BinaryOperator) e;
				keys[i] = pair.exprs[0];
				vals[i] = pair.exprs[1];
			} else if (e instanceof ConstantExpression && e.getType().getType() == Types.PAIR) {
				final GamaPair pair = (GamaPair) e.value(null);
				final Object left = pair.key;
				final Object right = pair.value;
				keys[i] = GAML.getExpressionFactory().createConst(left, e.getType().getKeyType());
				vals[i] = GAML.getExpressionFactory().createConst(right, e.getType().getContentType());
			}
		}
		final IType keyType = GamaType.findCommonType(keys, GamaType.TYPE);
		final IType contentsType = GamaType.findCommonType(vals, GamaType.TYPE);
		// values = GamaMapFactory.create(keyType, contentsType, keys.length);
		// setName(pairs.toString());
		type = Types.MAP.of(keyType, contentsType);
	}

	MapExpression(final GamaMap<IExpression, IExpression> pairs) {
		keys = new IExpression[pairs.size()];
		vals = new IExpression[pairs.size()];
		int i = 0;
		for (final Map.Entry<IExpression, IExpression> entry : pairs.entrySet()) {
			keys[i] = entry.getKey();
			vals[i] = entry.getValue();
			i++;
		}
		final IType keyType = GamaType.findCommonType(keys, GamaType.TYPE);
		final IType contentsType = GamaType.findCommonType(vals, GamaType.TYPE);
		// values = GamaMapFactory.create(keyType, contentsType, keys.length);
		// setName(pairs.toString());
		type = Types.MAP.of(keyType, contentsType);
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		final GamaMap result = GamaMapFactory.create(type.getKeyType(), type.getContentType(), keys.length);
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null || vals[i] == null) {
				continue;
			}
			result.put(keys[i].resolveAgainst(scope), vals[i].resolveAgainst(scope));
		}
		final MapExpression copy = new MapExpression(getElements());
		return copy;
	}

	@Override
	public GamaMap value(final IScope scope) throws GamaRuntimeException {
		// if ( isConst && computed ) { return (GamaMap) values.clone(); }
		final GamaMap values = GamaMapFactory.create(type.getKeyType(), type.getContentType());
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null || vals[i] == null) {
				// computed = false;
				return GamaMapFactory.create();
			}
			values.put(keys[i].value(scope), vals[i].value(scope));
		}
		// computed = true;
		return values;
	}

	@Override
	public String toString() {
		return getElements().toString();
	}

	@Override
	public boolean isConst() {
		return false;
		// for ( int i = 0; i < keys.length; i++ ) {
		// // indicates an error in the compilation process of a former
		// expression
		// if ( keys[i] == null || vals[i] == null ) {
		// continue;
		// }
		// if ( !keys[i].isConst() || !vals[i].isConst() ) { return false; }
		// }
		// // isConst = true;
		// return true;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		sb.append(' ').append('[');
		for (int i = 0; i < keys.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			if (keys[i] == null || vals[i] == null) {
				sb.append("nill::nil");
			} else {
				sb.append(keys[i].serialize(includingBuiltIn));
				sb.append("::");
				sb.append(vals[i].serialize(includingBuiltIn));
			}
		}
		sb.append(']').append(' ');
		return sb.toString();
	}

	public IExpression[] keysArray() {
		return keys;
	}

	public IExpression[] valuesArray() {
		return vals;
	}

	public GamaMap<IExpression, IExpression> getElements() {
		// TODO Verify the key and content types in that case...
		final GamaMap result = GamaMapFactory.create(type.getKeyType(), type.getContentType(), keys.length);
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null) {
				continue;
			}
			result.put(keys[i], vals[i]);
		}
		return result;
	}

	@Override
	public String getTitle() {
		return "literal map of type " + getType().getTitle();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */

	@Override
	public String getDocumentation() {
		return "Constant " + isConst() + "<br>Contains elements of type " + type.getContentType().getTitle();
	}

	/**
	 * Method collectPlugins()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		for (final IExpression e : keys) {
			if (e != null) {
				e.collectMetaInformation(meta);
			}
		}

		for (final IExpression e : vals) {
			if (e != null) {
				e.collectMetaInformation(meta);
			}
		}
	}

}
