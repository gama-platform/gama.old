/*********************************************************************************************
 *
 *
 * 'AffineTransform3D.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/

package msi.gama.metamodel.shape;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;

import msi.gaml.operators.fastmaths.FastMath;

/**
 * @author dlegland
 * @adapted to GAMA by A. Drogoul, Jan 2014
 */
public class AffineTransform3D implements CoordinateSequenceFilter {

	// coefficients for x coordinate.
	protected double m00, m01, m02, m03;

	// coefficients for y coordinate.
	protected double m10, m11, m12, m13;

	// coefficients for z coordinate.
	protected double m20, m21, m22, m23;

	// ===================================================================
	// public static methods

	public final static AffineTransform3D createTranslation(final double x, final double y, final double z) {
		return new AffineTransform3D(1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z);
	}

	public final static AffineTransform3D createRotationOx(final double theta) {
		final double cot = FastMath.cos(theta);
		final double sit = FastMath.sin(theta);
		return new AffineTransform3D(1, 0, 0, 0, 0, cot, -sit, 0, 0, sit, cot, 0);
	}

	public final static AffineTransform3D createRotationOy(final double theta) {
		final double cot = FastMath.cos(theta);
		final double sit = FastMath.sin(theta);
		return new AffineTransform3D(cot, 0, sit, 0, 0, 1, 0, 0, -sit, 0, cot, 0);
	}

	public final static AffineTransform3D createRotationOz(final double theta) {
		final double cot = FastMath.cos(theta);
		final double sit = FastMath.sin(theta);
		return new AffineTransform3D(cot, -sit, 0, 0, sit, cot, 0, 0, 0, 0, 1, 0);
	}

	public final static AffineTransform3D createRotationOz(final double theta, final double x, final double y) {
		// The matrix representing the returned transform is:
		// [ cos(theta) -sin(theta) x-x*cos+y*sin ]
		// [ sin(theta) cos(theta) y-x*sin-y*cos ]
		// [ 0 0 1 ]

		final double cot = FastMath.cos(theta);
		final double sit = FastMath.sin(theta);
		return new AffineTransform3D(cot, -sit, 0, x - x * cot + y * sit, sit, cot, 0, y - x * sit - y * cot, 0, 0, 1,
				0);
	}

	public final static AffineTransform3D createRotationVector(final double theta, final double xI, final double yI,
			final double zI) {
		final double cot = FastMath.cos(theta);
		final double sit = FastMath.sin(theta);
		final double sum = FastMath.sqrt(xI * xI + yI * yI + zI * zI);
		if (sum == 0) {
			return null;
		}
		final double x = xI / sum;
		final double y = yI / sum;
		final double z = zI / sum;
		return new AffineTransform3D(cot + x * x * (1 - cot), x * y * (1 - cot) - z * sit, x * z * (1 - cot) + y * sit,
				0.0, y * x * (1 - cot) + z * sit, cot + y * y * (1 - cot), y * z * (1 - cot) - x * sit, 0.0,
				z * x * (1 - cot) - y * sit, z * y * (1 - cot) + x * sit, cot + z * z * (1 - cot), 0.0);
	}

	public final static AffineTransform3D createScaling(final double s) {
		return createScaling(s, s, s);
	}

	public final static AffineTransform3D createScaling(final double sx, final double sy, final double sz) {
		return new AffineTransform3D(sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, 0);
	}

	// ===================================================================
	// constructors

	/** Creates a new affine transform3D set to identity */
	public AffineTransform3D() {
		// init to identity matrix
		m00 = m11 = m22 = 1;
		m01 = m02 = m03 = 0;
		m10 = m12 = m13 = 0;
		m20 = m21 = m23 = 0;
	}

	public AffineTransform3D(final double[] coefs) {
		if (coefs.length == 9) {
			m00 = coefs[0];
			m01 = coefs[1];
			m02 = coefs[2];
			m10 = coefs[3];
			m11 = coefs[4];
			m12 = coefs[5];
			m20 = coefs[6];
			m21 = coefs[7];
			m22 = coefs[8];
		} else if (coefs.length == 12) {
			m00 = coefs[0];
			m01 = coefs[1];
			m02 = coefs[2];
			m03 = coefs[3];
			m10 = coefs[4];
			m11 = coefs[5];
			m12 = coefs[6];
			m13 = coefs[7];
			m20 = coefs[8];
			m21 = coefs[9];
			m22 = coefs[10];
			m23 = coefs[11];
		}
	}

	public AffineTransform3D(final double xx, final double yx, final double zx, final double tx, final double xy,
			final double yy, final double zy, final double ty, final double xz, final double yz, final double zz,
			final double tz) {
		m00 = xx;
		m01 = yx;
		m02 = zx;
		m03 = tx;
		m10 = xy;
		m11 = yy;
		m12 = zy;
		m13 = ty;
		m20 = xz;
		m21 = yz;
		m22 = zz;
		m23 = tz;
	}

	// ===================================================================
	// accessors

	public boolean isIdentity() {
		if (m00 != 1) {
			return false;
		}
		if (m11 != 1) {
			return false;
		}
		if (m22 != 0) {
			return false;
		}
		if (m01 != 0) {
			return false;
		}
		if (m02 != 0) {
			return false;
		}
		if (m03 != 0) {
			return false;
		}
		if (m10 != 0) {
			return false;
		}
		if (m12 != 0) {
			return false;
		}
		if (m13 != 0) {
			return false;
		}
		if (m20 != 0) {
			return false;
		}
		if (m21 != 0) {
			return false;
		}
		if (m23 != 0) {
			return false;
		}
		return true;
	}

	/**
	 * Computes the determinant of this transform. Can be zero.
	 *
	 * @return the determinant of the transform.
	 */
	private double determinant() {
		return m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m20 * m12) + m02 * (m10 * m21 - m20 * m11);
	}

	/**
	 * Computes the inverse affine transform.
	 */
	public AffineTransform3D inverse() {
		final double det = this.determinant();
		return new AffineTransform3D((m11 * m22 - m21 * m12) / det, (m21 * m01 - m01 * m22) / det,
				(m01 * m12 - m11 * m02) / det,
				(m01 * (m22 * m13 - m12 * m23) + m02 * (m11 * m23 - m21 * m13) - m03 * (m11 * m22 - m21 * m12)) / det,
				(m20 * m12 - m10 * m22) / det, (m00 * m22 - m20 * m02) / det, (m10 * m02 - m00 * m12) / det,
				(m00 * (m12 * m23 - m22 * m13) - m02 * (m10 * m23 - m20 * m13) + m03 * (m10 * m22 - m20 * m12)) / det,
				(m10 * m21 - m20 * m11) / det, (m20 * m01 - m00 * m21) / det, (m00 * m11 - m10 * m01) / det,
				(m00 * (m21 * m13 - m11 * m23) + m01 * (m10 * m23 - m20 * m13) - m03 * (m10 * m21 - m20 * m11)) / det);
	}

	// ===================================================================
	// general methods

	// TODO: add methods to concatenate affine transforms.

	/**
	 * Combine this transform with another AffineTransform.
	 */
	public void compose(final AffineTransform3D m) {
		final double a00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20;
		final double a01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21;
		final double a02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22;
		final double a03 = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03;

		final double a10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20;
		final double a11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21;
		final double a12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22;
		final double a13 = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13;

		final double a20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20;
		final double a21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21;
		final double a22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22;
		final double a23 = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23;

		m00 = a00;
		m01 = a01;
		m02 = a02;
		m03 = a03;

		m10 = a10;
		m11 = a11;
		m12 = a12;
		m13 = a13;

		m20 = a20;
		m21 = a21;
		m22 = a22;
		m23 = a23;
	}

	/**
	 * Combine this transform with another AffineTransform.
	 */
	public void composeBefore(final AffineTransform3D m) {
		final double a00 = m.m00 * m00 + m.m01 * m10 + m.m02 * m20;
		final double a01 = m.m00 * m01 + m.m01 * m11 + m.m02 * m21;
		final double a02 = m.m00 * m02 + m.m01 * m12 + m.m02 * m22;
		final double a03 = m.m00 * m03 + m.m01 * m13 + m.m02 * m23 + m.m03;

		final double a10 = m.m10 * m00 + m.m11 * m10 + m.m12 * m20;
		final double a11 = m.m10 * m01 + m.m11 * m11 + m.m12 * m21;
		final double a12 = m.m10 * m02 + m.m11 * m12 + m.m12 * m22;
		final double a13 = m.m10 * m03 + m.m11 * m13 + m.m12 * m23 + m.m13;

		final double a20 = m.m20 * m00 + m.m21 * m10 + m.m22 * m20;
		final double a21 = m.m20 * m01 + m.m21 * m11 + m.m22 * m21;
		final double a22 = m.m20 * m02 + m.m21 * m12 + m.m22 * m22;
		final double a23 = m.m20 * m03 + m.m21 * m13 + m.m22 * m23 + m.m23;

		m00 = a00;
		m01 = a01;
		m02 = a02;
		m03 = a03;

		m10 = a10;
		m11 = a11;
		m12 = a12;
		m13 = a13;

		m20 = a20;
		m21 = a21;
		m22 = a22;
		m23 = a23;

	}

	/**
	 * Compares two transforms. Returns true if all inner fields are equal up to
	 * the precision given by accuracy.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof AffineTransform3D)) {
			return false;
		}
		final AffineTransform3D t = (AffineTransform3D) obj;
		final double accuracy = 1e-14;
		if (FastMath.abs(t.m00 - m00) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m01 - m01) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m02 - m02) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m03 - m03) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m10 - m10) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m11 - m11) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m12 - m12) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m13 - m13) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m20 - m20) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m21 - m21) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m22 - m22) > accuracy) {
			return false;
		}
		if (FastMath.abs(t.m23 - m23) > accuracy) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return (int) (m00 + m01 + m02 + m03 + 10 * (m10 + m11 + m12 + m13) + 100 * (m20 + m21 + m22 + m23));
	}

	/**
	 * Transforms the i'th coordinate in the input sequence
	 *
	 * @param seq
	 *            a <code>CoordinateSequence</code>
	 * @param i
	 *            the index of the coordinate to transform
	 */
	@Override
	public void filter(final CoordinateSequence seq, final int i) {
		final double x = seq.getOrdinate(i, 0);
		final double y = seq.getOrdinate(i, 1);
		final double z = seq.getOrdinate(i, 2);
		final double xp = m00 * x + m01 * y + m02 * z + m03;
		final double yp = m10 * x + m11 * y + m12 * z + m13;
		final double zp = m20 * x + m21 * y + m22 * z + m23;
		seq.setOrdinate(i, 0, xp);
		seq.setOrdinate(i, 1, yp);
		seq.setOrdinate(i, 2, zp);
	}

	@Override
	public boolean isGeometryChanged() {
		return true;
	}

	/**
	 * Reports that this filter should continue to be executed until all
	 * coordinates have been transformed.
	 *
	 * @return false
	 */
	@Override
	public boolean isDone() {
		return false;
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTranslation(final double x, final double y, final double z) {
		m03 = x;
		m13 = y;
		m23 = z;
	}

	public GamaPoint getTranslation() {
		return new GamaPoint(m03, m13, m23);
	}

}
