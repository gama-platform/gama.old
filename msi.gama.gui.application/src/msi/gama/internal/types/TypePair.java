/**
 * Created by drogoul, 8 déc. 2011
 * 
 */
package msi.gama.internal.types;

import msi.gama.interfaces.IType;

public class TypePair {

	IType left, right;

	public TypePair(final IType l, final IType r) {
		left = l;
		right = r;
	}

	public boolean isCompatibleWith(final IType l, final IType r) {
		return left.isAssignableFrom(l) && right.isAssignableFrom(r);
	}

	public int distanceTo(final IType l, final IType r) {
		return left.distanceTo(l) + right.distanceTo(r);
	}

	public boolean equals(final IType l, final IType r) {
		return left == l && right == r;
	}

	public boolean equals(final TypePair p) {
		return equals(p.left, p.right);
	}

	@Override
	public boolean equals(final Object p) {
		if ( !(p instanceof TypePair) ) { return false; }
		return equals((TypePair) p);
	}

	@Override
	public String toString() {
		return "[" + left.toString() + " & " + right.toString() + "]";
	}

	public IType left() {
		return left;
	}

	public IType right() {
		return right;
	}
}