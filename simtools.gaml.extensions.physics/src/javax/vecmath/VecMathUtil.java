/*******************************************************************************************************
 *
 * VecMathUtil.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;

/**
 * Utility vecmath class used when computing the hash code for vecmath
 * objects containing float or double values. This fixes Issue 36.
 */
class VecMathUtil {
/**
 * Do not construct an instance of this class.
 */
private VecMathUtil() {}

	/**
	 * Hash long bits.
	 *
	 * @param hash the hash
	 * @param l the l
	 * @return the long
	 */
	static final long hashLongBits(long hash, long l) {
		hash *= 31L;
		return hash + l;
	}

	/**
	 * Hash float bits.
	 *
	 * @param hash the hash
	 * @param f the f
	 * @return the long
	 */
	static final long hashFloatBits(long hash, float f) {
		hash *= 31L;
		// Treat 0.0d and -0.0d the same (all zero bits)
		if (f == 0.0f)
			return hash;

		return hash + Float.floatToIntBits(f);
	}

	/**
	 * Hash double bits.
	 *
	 * @param hash the hash
	 * @param d the d
	 * @return the long
	 */
	static final long hashDoubleBits(long hash, double d) {
		hash *= 31L;
		// Treat 0.0d and -0.0d the same (all zero bits)
		if (d == 0.0d)
			return hash;

		return hash + Double.doubleToLongBits(d);
	}

	/**
	 * Return an integer hash from a long by mixing it with itself.
	 */
	static final int hashFinish(long hash) {
		return (int)(hash ^ (hash >> 32));
	}
}
