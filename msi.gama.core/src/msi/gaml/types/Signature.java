/*********************************************************************************************
 * 
 * 
 * 'Signature.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.types;

import java.util.Arrays;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

public class Signature {

	final IType[] list;

	public Signature(final IType ... types) {
		list = types;
	}

	public Signature(final int[] types) {
		list = new IType[types.length];
		for ( int i = 0; i < list.length; i++ ) {
			list[i] = Types.get(types[i]);
		}
	}

	public Signature(final IExpression ... objects) {
		list = new IType[objects.length];
		for ( int i = 0; i < list.length; i++ ) {
			IExpression o = objects[i];
			list[i] = o == null ? Types.NO_TYPE : o.getType();
		}
	}

	public Signature(final Class ... objects) {
		list = new IType[objects.length];
		for ( int i = 0; i < list.length; i++ ) {
			list[i] = Types.get(objects[i]);
		}
	}

	public Signature simplified() {
		// returns a signature that does not contain any parametric types
		IType[] copy = Arrays.copyOf(list, list.length);
		for ( int i = 0; i < copy.length; i++ ) {
			copy[i] = copy[i].getType();
		}
		return new Signature(copy);
	}

	public boolean isCompatibleWith(final IType ... types) {
		if ( types.length != list.length ) { return false; }
		for ( int i = 0; i < list.length; i++ ) {
			if ( !list[i].isTranslatableInto(types[i]) ) { return false; }
		}
		return true;
	}

	public boolean isCompatibleWith(final Signature types) {
		return isCompatibleWith(types.list);
	}

	public int distanceTo(final IType ... types) {
		if ( types.length != list.length ) { return Integer.MAX_VALUE; }
		int dist = 0;
		for ( int i = 0; i < list.length; i++ ) {
			// 19/02/14 Now using the maximum distance between two types of the signature instead of addition.
			int d = types[i].distanceTo(list[i]);
			if ( d > dist ) {
				dist = d;
			}
			// dist += types[i].distanceTo(list[i]);
		}
		return dist;
	}

	public int distanceTo(final Signature types) {
		return distanceTo(types.list);
	}

	public boolean equals(final Signature p) {
		if ( p.list.length != list.length ) { return false; }
		for ( int i = 0; i < list.length; i++ ) {
			if ( p.list[i] != list[i] ) { return false; }
		}
		return true;
	}

	@Override
	public boolean equals(final Object p) {
		if ( !(p instanceof Signature) ) { return false; }
		return equals((Signature) p);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(list);
	}

	@Override
	public String toString() {
		String s = list.length < 2 ? "type " : "types [";
		for ( int i = 0; i < list.length; i++ ) {
			s += list[i].toString();
			if ( i != list.length - 1 ) {
				s += ", ";
			}
		}
		if ( list.length >= 2 ) {
			s += "]";
		}
		return s;
	}

	public IType get(final int i) {
		return list[i];
	}

	public IType[] coerce(final Signature originalSignature, final IDescription context) {
		IType[] result = new IType[list.length];
		for ( int i = 0; i < list.length; i++ ) {
			result[i] = list[i].coerce(originalSignature.get(i), context);
		}
		return result;
	}

	/**
	 * @return
	 */
	public boolean isUnary() {
		return list.length == 1;
	}

	/**
	 * @return
	 */
	public int size() {
		return list.length;
	}
}