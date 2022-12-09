/*******************************************************************************************************
 *
 * Mat33.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * A 3-by-3 matrix. Stored in column-major order.
 *
 * @author Daniel Murphy
 */
public class Mat33 implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2L;

	/** The Constant IDENTITY. */
	public static final Mat33 IDENTITY = new Mat33(new Vec3(1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, 1));

	/** The ez. */
	public final Vec3 ex, ey, ez;

	/**
	 * Instantiates a new mat 33.
	 */
	public Mat33() {
		ex = new Vec3();
		ey = new Vec3();
		ez = new Vec3();
	}

	/**
	 * Instantiates a new mat 33.
	 *
	 * @param exx the exx
	 * @param exy the exy
	 * @param exz the exz
	 * @param eyx the eyx
	 * @param eyy the eyy
	 * @param eyz the eyz
	 * @param ezx the ezx
	 * @param ezy the ezy
	 * @param ezz the ezz
	 */
	public Mat33(final float exx, final float exy, final float exz, final float eyx, final float eyy, final float eyz,
			final float ezx, final float ezy, final float ezz) {
		ex = new Vec3(exx, exy, exz);
		ey = new Vec3(eyx, eyy, eyz);
		ez = new Vec3(ezx, ezy, ezz);
	}

	/**
	 * Instantiates a new mat 33.
	 *
	 * @param argCol1 the arg col 1
	 * @param argCol2 the arg col 2
	 * @param argCol3 the arg col 3
	 */
	public Mat33(final Vec3 argCol1, final Vec3 argCol2, final Vec3 argCol3) {
		ex = argCol1.clone();
		ey = argCol2.clone();
		ez = argCol3.clone();
	}

	/**
	 * Sets the zero.
	 */
	public void setZero() {
		ex.setZero();
		ey.setZero();
		ez.setZero();
	}

	/**
	 * Sets the.
	 *
	 * @param exx the exx
	 * @param exy the exy
	 * @param exz the exz
	 * @param eyx the eyx
	 * @param eyy the eyy
	 * @param eyz the eyz
	 * @param ezx the ezx
	 * @param ezy the ezy
	 * @param ezz the ezz
	 */
	public void set(final float exx, final float exy, final float exz, final float eyx, final float eyy,
			final float eyz, final float ezx, final float ezy, final float ezz) {
		ex.x = exx;
		ex.y = exy;
		ex.z = exz;
		ey.x = eyx;
		ey.y = eyy;
		ey.z = eyz;
		ez.x = eyx;
		ez.y = eyy;
		ez.z = eyz;
	}

	/**
	 * Sets the.
	 *
	 * @param mat the mat
	 */
	public void set(final Mat33 mat) {
		Vec3 vec = mat.ex;
		ex.x = vec.x;
		ex.y = vec.y;
		ex.z = vec.z;
		Vec3 vec1 = mat.ey;
		ey.x = vec1.x;
		ey.y = vec1.y;
		ey.z = vec1.z;
		Vec3 vec2 = mat.ez;
		ez.x = vec2.x;
		ez.y = vec2.y;
		ez.z = vec2.z;
	}

	/**
	 * Sets the identity.
	 */
	public void setIdentity() {
		ex.x = 1;
		ex.y = 0;
		ex.z = 0;
		ey.x = 0;
		ey.y = 1;
		ey.z = 0;
		ez.x = 0;
		ez.y = 0;
		ez.z = 1;
	}

	/**
	 * Mul.
	 *
	 * @param A the a
	 * @param v the v
	 * @return the vec 3
	 */
	// / Multiply a matrix times a vector.
	public static final Vec3 mul(final Mat33 A, final Vec3 v) {
		return new Vec3(v.x * A.ex.x + v.y * A.ey.x + v.z + A.ez.x, v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y,
				v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z);
	}

	/**
	 * Mul 22.
	 *
	 * @param A the a
	 * @param v the v
	 * @return the vec 2
	 */
	public static final Vec2 mul22(final Mat33 A, final Vec2 v) {
		return new Vec2(A.ex.x * v.x + A.ey.x * v.y, A.ex.y * v.x + A.ey.y * v.y);
	}

	/**
	 * Mul 22 to out.
	 *
	 * @param A the a
	 * @param v the v
	 * @param out the out
	 */
	public static final void mul22ToOut(final Mat33 A, final Vec2 v, final Vec2 out) {
		final float tempx = A.ex.x * v.x + A.ey.x * v.y;
		out.y = A.ex.y * v.x + A.ey.y * v.y;
		out.x = tempx;
	}

	/**
	 * Mul 22 to out unsafe.
	 *
	 * @param A the a
	 * @param v the v
	 * @param out the out
	 */
	public static final void mul22ToOutUnsafe(final Mat33 A, final Vec2 v, final Vec2 out) {
		assert v != out;
		out.y = A.ex.y * v.x + A.ey.y * v.y;
		out.x = A.ex.x * v.x + A.ey.x * v.y;
	}

	/**
	 * Mul to out.
	 *
	 * @param A the a
	 * @param v the v
	 * @param out the out
	 */
	public static final void mulToOut(final Mat33 A, final Vec3 v, final Vec3 out) {
		final float tempy = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
		final float tempz = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
		out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
		out.y = tempy;
		out.z = tempz;
	}

	/**
	 * Mul to out unsafe.
	 *
	 * @param A the a
	 * @param v the v
	 * @param out the out
	 */
	public static final void mulToOutUnsafe(final Mat33 A, final Vec3 v, final Vec3 out) {
		assert out != v;
		out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
		out.y = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
		out.z = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
	}

	/**
	 * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse in one-shot cases.
	 * 
	 * @param b
	 * @return
	 */
	public final Vec2 solve22(final Vec2 b) {
		Vec2 x = new Vec2();
		solve22ToOut(b, x);
		return x;
	}

	/**
	 * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse in one-shot cases.
	 * 
	 * @param b
	 * @return
	 */
	public final void solve22ToOut(final Vec2 b, final Vec2 out) {
		final float a11 = ex.x, a12 = ey.x, a21 = ex.y, a22 = ey.y;
		float det = a11 * a22 - a12 * a21;
		if (det != 0.0f) { det = 1.0f / det; }
		out.x = det * (a22 * b.x - a12 * b.y);
		out.y = det * (a11 * b.y - a21 * b.x);
	}

	// djm pooling from below
	/**
	 * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse in one-shot cases.
	 * 
	 * @param b
	 * @return
	 */
	public final Vec3 solve33(final Vec3 b) {
		Vec3 x = new Vec3();
		solve33ToOut(b, x);
		return x;
	}

	/**
	 * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse in one-shot cases.
	 * 
	 * @param b
	 * @param out
	 *            the result
	 */
	public final void solve33ToOut(final Vec3 b, final Vec3 out) {
		assert b != out;
		Vec3.crossToOutUnsafe(ey, ez, out);
		float det = Vec3.dot(ex, out);
		if (det != 0.0f) { det = 1.0f / det; }
		Vec3.crossToOutUnsafe(ey, ez, out);
		final float x = det * Vec3.dot(b, out);
		Vec3.crossToOutUnsafe(b, ez, out);
		final float y = det * Vec3.dot(ex, out);
		Vec3.crossToOutUnsafe(ey, b, out);
		float z = det * Vec3.dot(ex, out);
		out.x = x;
		out.y = y;
		out.z = z;
	}

	/**
	 * Gets the inverse 22.
	 *
	 * @param M the m
	 * @return the inverse 22
	 */
	public void getInverse22(final Mat33 M) {
		float a = ex.x, b = ey.x, c = ex.y, d = ey.y;
		float det = a * d - b * c;
		if (det != 0.0f) { det = 1.0f / det; }

		M.ex.x = det * d;
		M.ey.x = -det * b;
		M.ex.z = 0.0f;
		M.ex.y = -det * c;
		M.ey.y = det * a;
		M.ey.z = 0.0f;
		M.ez.x = 0.0f;
		M.ez.y = 0.0f;
		M.ez.z = 0.0f;
	}

	/**
	 * Gets the sym inverse 33.
	 *
	 * @param M the m
	 * @return the sym inverse 33
	 */
	// / Returns the zero matrix if singular.
	public void getSymInverse33(final Mat33 M) {
		float bx = ey.y * ez.z - ey.z * ez.y;
		float by = ey.z * ez.x - ey.x * ez.z;
		float bz = ey.x * ez.y - ey.y * ez.x;
		float det = ex.x * bx + ex.y * by + ex.z * bz;
		if (det != 0.0f) { det = 1.0f / det; }

		float a11 = ex.x, a12 = ey.x, a13 = ez.x;
		float a22 = ey.y, a23 = ez.y;
		float a33 = ez.z;

		M.ex.x = det * (a22 * a33 - a23 * a23);
		M.ex.y = det * (a13 * a23 - a12 * a33);
		M.ex.z = det * (a12 * a23 - a13 * a22);

		M.ey.x = M.ex.y;
		M.ey.y = det * (a11 * a33 - a13 * a13);
		M.ey.z = det * (a13 * a12 - a11 * a23);

		M.ez.x = M.ex.z;
		M.ez.y = M.ey.z;
		M.ez.z = det * (a11 * a22 - a12 * a12);
	}

	/**
	 * Sets the scale transform.
	 *
	 * @param scale the scale
	 * @param out the out
	 */
	public final static void setScaleTransform(final float scale, final Mat33 out) {
		out.ex.x = scale;
		out.ey.y = scale;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ex, ey, ez);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		Mat33 other = (Mat33) obj;
		if (!Objects.equals(ex, other.ex)) {
			return false;
		}
		if (!Objects.equals(ey, other.ey)) {
			return false;
		}
		if (!Objects.equals(ez, other.ez)) {
			return false;
		}
		return true;
	}
}
