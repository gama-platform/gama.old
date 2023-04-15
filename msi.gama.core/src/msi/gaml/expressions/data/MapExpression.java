/*******************************************************************************************************
 *
 * MapExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.data;

import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;

import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.ICollector;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IVarDescriptionUser;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.AbstractExpression;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.operators.BinaryOperator;
import msi.gaml.expressions.operators.IOperator;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * ListValueExpr.
 *
 * @author drogoul 23 août 07
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class MapExpression extends AbstractExpression implements IOperator {

	/**
	 * Creates the.
	 *
	 * @param elements
	 *            the elements
	 * @return the i expression
	 */
	public static IExpression create(final Iterable<? extends IExpression> elements) {

		// if ( u.isConst() && GamaPreferences.CONSTANT_OPTIMIZATION.getValue()
		// ) {
		// IExpression e =
		// GAML.getExpressionFactory().createConst(u.getConstValue(), u.getType(),
		// u.serialize(false));
		// // DEBUG.LOG(" ==== Simplification of " + u.toGaml() + "
		// into " + e.toGaml());
		// return e;
		// }
		return new MapExpression(elements);
	}

	/** The keys. */
	private final IExpression[] keys;

	/** The vals. */
	private final IExpression[] vals;
	// private final GamaMap values;
	// private boolean isConst, computed;

	/**
	 * Instantiates a new map expression.
	 *
	 * @param pairs
	 *            the pairs
	 */
	MapExpression(final Iterable<? extends IExpression> pairs) {
		final int size = Iterables.size(pairs);
		keys = new IExpression[size];
		vals = new IExpression[size];
		int i = 0;
		for (final IExpression e : pairs) {
			if (e instanceof BinaryOperator pair) {
				keys[i] = pair.exprs[0];
				vals[i] = pair.exprs[1];
			} else if (e instanceof ConstantExpression && e.getGamlType().getGamlType() == Types.PAIR) {
				final GamaPair pair = (GamaPair) e.getConstValue();
				final Object left = pair.key;
				final Object right = pair.value;
				keys[i] = GAML.getExpressionFactory().createConst(left, e.getGamlType().getKeyType());
				vals[i] = GAML.getExpressionFactory().createConst(right, e.getGamlType().getContentType());
			}
			i++;
		}
		final IType keyType = GamaType.findCommonType(keys, GamaType.TYPE);
		final IType contentsType = GamaType.findCommonType(vals, GamaType.TYPE);
		// values = GamaMapFactory.create(keyType, contentsType, keys.length);
		// setName(pairs.toString());
		type = Types.MAP.of(keyType, contentsType);
	}

	/**
	 * Instantiates a new map expression.
	 *
	 * @param pairs
	 *            the pairs
	 */
	MapExpression(final IMap<IExpression, IExpression> pairs) {
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
		final IMap result = GamaMapFactory.create(type.getKeyType(), type.getContentType(), keys.length);
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null || vals[i] == null) { continue; }
			result.put(keys[i].resolveAgainst(scope), vals[i].resolveAgainst(scope));
		}
		return new MapExpression(getElements());
	}

	@Override
	public IMap _value(final IScope scope) throws GamaRuntimeException {
		// if ( isConst && computed ) { return (GamaMap) values.clone(); }
		final IMap values = GamaMapFactory.create(type.getKeyType(), type.getContentType());
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null || vals[i] == null) // computed = false;
				return GamaMapFactory.create();
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
		for (final IExpression expr : keys) { if (expr != null && !expr.isConst()) return false; }
		for (final IExpression expr : vals) { if (expr != null && !expr.isConst()) return false; }
		return true;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		sb.append(' ').append('[');
		for (int i = 0; i < keys.length; i++) {
			if (i > 0) { sb.append(','); }
			if (keys[i] == null || vals[i] == null) { continue; }
			sb.append(keys[i].serialize(includingBuiltIn));
			sb.append("::");
			sb.append(vals[i].serialize(includingBuiltIn));
		}
		sb.append(']').append(' ');
		return sb.toString();
	}

	/**
	 * Keys array.
	 *
	 * @return the i expression[]
	 */
	public IExpression[] keysArray() {
		return keys;
	}

	/**
	 * Values array.
	 *
	 * @return the i expression[]
	 */
	public IExpression[] valuesArray() {
		return vals;
	}

	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public IMap<IExpression, IExpression> getElements() {
		// TODO Verify the key and content types in that case...
		final IMap result = GamaMapFactory.create(type.getKeyType(), type.getContentType(), keys.length);
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null) { continue; }
			result.put(keys[i], vals[i]);
		}
		return result;
	}

	@Override
	public String getTitle() { return "literal map of type " + getGamlType().getTitle(); }

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */

	@Override
	public Doc getDocumentation() {
		return new ConstantDoc(
				"Constant " + isConst() + "<br>Contains elements of type " + type.getContentType().getTitle());
	}

	/**
	 * Method collectPlugins()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		for (final IExpression e : keys) { if (e != null) { e.collectMetaInformation(meta); } }
		for (final IExpression e : vals) { if (e != null) { e.collectMetaInformation(meta); } }
	}

	@Override
	public boolean isContextIndependant() {
		for (final IExpression e : keys) { if (e != null && !e.isContextIndependant()) return false; }
		for (final IExpression e : vals) { if (e != null && !e.isContextIndependant()) return false; }
		return true;
	}

	@Override
	public boolean isAllowedInParameters() {
		for (final IExpression e : keys) { if (e != null && !e.isAllowedInParameters()) return false; }
		for (final IExpression e : vals) { if (e != null && !e.isAllowedInParameters()) return false; }
		return true;
	}

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) return;
		alreadyProcessed.add(this);
		for (final IExpression e : keys) { if (e != null) { e.collectUsedVarsOf(species, alreadyProcessed, result); } }

		for (final IExpression e : vals) { if (e != null) { e.collectUsedVarsOf(species, alreadyProcessed, result); } }

	}

	@Override
	public void visitSuboperators(final IOperatorVisitor visitor) {
		for (final IExpression e : keys) { if (e instanceof IOperator) { visitor.visit((IOperator) e); } }

		for (final IExpression e : vals) { if (e instanceof IOperator) { visitor.visit((IOperator) e); } }

	}

	@Override
	public IExpression arg(final int i) {
		if (i < 0 || i > vals.length) return null;
		return vals[i];
	}

	@Override
	public OperatorProto getPrototype() { return null; }

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() { return keys.length == 0; }

	@Override
	public boolean findAny(final Predicate<IExpression> predicate) {
		if (predicate.test(this)) return true;
		if (keys != null) { for (final IExpression e : keys) { if (e.findAny(predicate)) return true; } }
		if (vals != null) { for (final IExpression e : vals) { if (e.findAny(predicate)) return true; } }
		return false;
	}

}
