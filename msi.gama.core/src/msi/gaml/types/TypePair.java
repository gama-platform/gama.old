/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

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
	public int hashCode() {
		return left.hashCode() + 2 * right.hashCode();
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