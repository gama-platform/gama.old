/*******************************************************************************************************
 *
 * Signature.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class Signature.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })

public class Signature {

	static {
		DEBUG.ON();
	}

	/** The empty classes. */
	static Class[] EMPTY_CLASSES = {};

	/** The list. */
	final IType[] list;

	/**
	 * Var arg from.
	 *
	 * @param sig
	 *            the sig
	 * @return the signature
	 */
	public static Signature varArgFrom(final Signature sig) {
		return new Signature(Types.LIST.of(GamaType.findCommonType(sig.list)));
	}

	/**
	 * Instantiates a new signature.
	 *
	 * @param method
	 *            the method
	 */
	public Signature(final Executable method) {
		if (method == null) {
			list = new IType[] {};
		} else {
			List<IType<?>> types = new ArrayList();
			Class[] classes = method.getParameterTypes();
			boolean isStatic = Modifier.isStatic(method.getModifiers());
			boolean isConstructor = method instanceof Constructor;
			if (!isStatic && !isConstructor) { types.add(Types.get(method.getDeclaringClass())); }
			for (Class c : classes) { if (c != IScope.class) { types.add(Types.get(c)); } }
			list = types.toArray(new IType[types.size()]);
		}
		// DEBUG.OUT("Signature from " + method + " = " + this.toString());
	}

	/**
	 * Instantiates a new signature.
	 *
	 * @param types
	 *            the types
	 */
	public Signature(final IType... types) {
		list = types;
	}

	/**
	 * Instantiates a new signature.
	 *
	 * @param types
	 *            the types
	 */
	public Signature(final int[] types) {
		list = new IType[types.length];
		for (int i = 0; i < list.length; i++) { list[i] = Types.get(types[i]); }
	}

	/**
	 * Instantiates a new signature.
	 *
	 * @param objects
	 *            the objects
	 */
	public Signature(final IExpression... objects) {
		list = new IType[objects.length];
		for (int i = 0; i < list.length; i++) {
			final IExpression o = objects[i];
			list[i] = o == null ? Types.NO_TYPE : o.getGamlType();
		}
	}

	/**
	 * Instantiates a new signature.
	 *
	 * @param classes
	 *            the classes
	 */
	public Signature(final Class... classes) {
		list = new IType[classes.length];
		for (int i = 0; i < list.length; i++) { list[i] = Types.get(classes[i]); }

	}

	/**
	 * Simplified.
	 *
	 * @return the signature
	 */
	public Signature simplified() {
		// returns a signature that does not contain any parametric types
		final IType[] copy = Arrays.copyOf(list, list.length);
		for (int i = 0; i < copy.length; i++) { copy[i] = copy[i].getGamlType(); }
		return new Signature(copy);
	}

	/**
	 * Matches desired signature.
	 *
	 * @param types
	 *            the types
	 * @return true, if successful
	 */
	public boolean matchesDesiredSignature(final IType... types) {
		if (types.length != list.length) return false;
		for (int i = 0; i < list.length; i++) {
			final IType ownType = list[i];
			final IType desiredType = types[i];
			if (Types.intFloatCase(ownType, desiredType) || desiredType.isAssignableFrom(ownType) || (!desiredType.isNumber() && ownType == Types.NO_TYPE)) { continue; }
			return false;
		}
		return true;
	}

	// public boolean isCompatibleWith(final IType... types) {
	// if (types.length != list.length) {
	// return false;
	// }
	// for (int i = 0; i < list.length; i++) {
	// if (!list[i].isTranslatableInto(types[i])) {
	// return false;
	// }
	// }
	// return true;
	// }

	/**
	 * Matches desired signature.
	 *
	 * @param types
	 *            the types
	 * @return true, if successful
	 */
	public boolean matchesDesiredSignature(final Signature types) {
		return matchesDesiredSignature(types.list);
	}

	/**
	 * Distance to.
	 *
	 * @param types
	 *            the types
	 * @return the int
	 */
	public int distanceTo(final IType... types) {
		if (types.length != list.length) return Integer.MAX_VALUE;
		// int dist = 0;
		int max = 0;
		int min = Integer.MAX_VALUE;
		// for (int i = 0; i < list.length; i++) {
		// // 19/02/14 Now using the maximum distance between two types of the
		// // signature instead of addition.
		// final int d = types[i].distanceTo(list[i]);
		// if (d > dist) {
		// dist = d;
		// }
		// // dist += types[i].distanceTo(list[i]);
		// }
		// We now take into account the min and the max (see #2266 and the case where [unknown, geometry, geometry] was
		// preffered to [topology, geometry, geometry] for an input of [topology, a_species, a_species])
		for (int i = 0; i < list.length; i++) {
			final int d = types[i].distanceTo(list[i]);
			if (max < d) { max = d; }
			if (min > d) { min = d; }
		}
		return min + max;
	}

	/**
	 * Distance to.
	 *
	 * @param types
	 *            the types
	 * @return the int
	 */
	public int distanceTo(final Signature types) {
		return distanceTo(types.list);
	}

	/**
	 * Equals.
	 *
	 * @param p
	 *            the p
	 * @return true, if successful
	 */
	public boolean equals(final Signature p) {
		if (p.list.length != list.length) return false;
		for (int i = 0; i < list.length; i++) { if (p.list[i] != list[i]) return false; }
		return true;
	}

	@Override
	public boolean equals(final Object p) {
		if (!(p instanceof Signature)) return false;
		return equals((Signature) p);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(list);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder().append(list.length < 2 ? "type " : "types [");
		for (int i = 0; i < list.length; i++) {
			s.append(list[i].toString());
			if (i != list.length - 1) { s.append(", "); }
		}
		if (list.length >= 2) { s.append("]"); }
		return s.toString();
	}

	/**
	 * Gets the.
	 *
	 * @param i
	 *            the i
	 * @return the i type
	 */
	public IType get(final int i) {
		return list[i];
	}

	/**
	 * Coerce.
	 *
	 * @param originalSignature
	 *            the original signature
	 * @param context
	 *            the context
	 * @return the i type[]
	 */
	public IType[] coerce(final Signature originalSignature, final IDescription context) {
		final IType[] result = new IType[list.length];
		for (int i = 0; i < list.length; i++) { result[i] = list[i].coerce(originalSignature.get(i), context); }
		return result;
	}

	/**
	 * @return
	 */
	public boolean isUnary() { return list.length == 1; }

	/**
	 * @return
	 */
	public int size() {
		return list.length;
	}

	/**
	 * @return
	 */
	public String asPattern(final boolean withVariables) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.length; i++) {
			sb.append(withVariables ? list[i].asPattern() : list[i].serialize(true));
			if (i < list.length - 1) { sb.append(','); }
		}
		return sb.toString();
	}

}