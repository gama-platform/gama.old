/*******************************************************************************************************
 *
 * DbvtAabbMm.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

// Dbvt implementation by Nathanael Presson

package com.bulletphysics.collision.broadphase;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
public class DbvtAabbMm {

	/** The mi. */
	private final Vector3f mi = new Vector3f();

	/** The mx. */
	private final Vector3f mx = new Vector3f();

	/**
	 * Instantiates a new dbvt aabb mm.
	 */
	public DbvtAabbMm() {}

	/**
	 * Instantiates a new dbvt aabb mm.
	 *
	 * @param o
	 *            the o
	 */
	public DbvtAabbMm(final DbvtAabbMm o) {
		set(o);
	}

	/**
	 * Sets the.
	 *
	 * @param o
	 *            the o
	 */
	public void set(final DbvtAabbMm o) {
		mi.set(o.mi);
		mx.set(o.mx);
	}

	/**
	 * Swap.
	 *
	 * @param p1
	 *            the p 1
	 * @param p2
	 *            the p 2
	 */
	public static void swap(final DbvtAabbMm p1, final DbvtAabbMm p2) {
		Vector3f tmp = VECTORS.get();

		tmp.set(p1.mi);
		p1.mi.set(p2.mi);
		p2.mi.set(tmp);

		tmp.set(p1.mx);
		p1.mx.set(p2.mx);
		p2.mx.set(tmp);
	}

	/**
	 * Center.
	 *
	 * @param out
	 *            the out
	 * @return the vector 3 f
	 */
	public Vector3f Center(final Vector3f out) {
		out.add(mi, mx);
		out.scale(0.5f);
		return out;
	}

	/**
	 * Lengths.
	 *
	 * @param out
	 *            the out
	 * @return the vector 3 f
	 */
	public Vector3f Lengths(final Vector3f out) {
		out.sub(mx, mi);
		return out;
	}

	/**
	 * Extents.
	 *
	 * @param out
	 *            the out
	 * @return the vector 3 f
	 */
	public Vector3f Extents(final Vector3f out) {
		out.sub(mx, mi);
		out.scale(0.5f);
		return out;
	}

	/**
	 * Mins.
	 *
	 * @return the vector 3 f
	 */
	public Vector3f Mins() {
		return mi;
	}

	/**
	 * Maxs.
	 *
	 * @return the vector 3 f
	 */
	public Vector3f Maxs() {
		return mx;
	}

	/**
	 * From CE.
	 *
	 * @param c
	 *            the c
	 * @param e
	 *            the e
	 * @param out
	 *            the out
	 * @return the dbvt aabb mm
	 */
	public static DbvtAabbMm FromCE(final Vector3f c, final Vector3f e, final DbvtAabbMm out) {
		DbvtAabbMm box = out;
		box.mi.sub(c, e);
		box.mx.add(c, e);
		return box;
	}

	/**
	 * From CR.
	 *
	 * @param c
	 *            the c
	 * @param r
	 *            the r
	 * @param out
	 *            the out
	 * @return the dbvt aabb mm
	 */
	public static DbvtAabbMm FromCR(final Vector3f c, final float r, final DbvtAabbMm out) {
		Vector3f tmp = VECTORS.get();
		tmp.set(r, r, r);
		return FromCE(c, tmp, out);
	}

	/**
	 * From MM.
	 *
	 * @param mi
	 *            the mi
	 * @param mx
	 *            the mx
	 * @param out
	 *            the out
	 * @return the dbvt aabb mm
	 */
	public static DbvtAabbMm FromMM(final Vector3f mi, final Vector3f mx, final DbvtAabbMm out) {
		DbvtAabbMm box = out;
		box.mi.set(mi);
		box.mx.set(mx);
		return box;
	}

	// public static DbvtAabbMm FromPoints( btVector3* pts,int n);
	// public static DbvtAabbMm FromPoints( btVector3** ppts,int n);

	/**
	 * Expand.
	 *
	 * @param e
	 *            the e
	 */
	public void Expand(final Vector3f e) {
		mi.sub(e);
		mx.add(e);
	}

	/**
	 * Signed expand.
	 *
	 * @param e
	 *            the e
	 */
	public void SignedExpand(final Vector3f e) {
		if (e.x > 0) {
			mx.x += e.x;
		} else {
			mi.x += e.x;
		}

		if (e.y > 0) {
			mx.y += e.y;
		} else {
			mi.y += e.y;
		}

		if (e.z > 0) {
			mx.z += e.z;
		} else {
			mi.z += e.z;
		}
	}

	/**
	 * Contain.
	 *
	 * @param a
	 *            the a
	 * @return true, if successful
	 */
	public boolean Contain(final DbvtAabbMm a) {
		return mi.x <= a.mi.x && mi.y <= a.mi.y && mi.z <= a.mi.z && mx.x >= a.mx.x && mx.y >= a.mx.y && mx.z >= a.mx.z;
	}

	/**
	 * Classify.
	 *
	 * @param n
	 *            the n
	 * @param o
	 *            the o
	 * @param s
	 *            the s
	 * @return the int
	 */
	public int Classify(final Vector3f n, final float o, final int s) {
		Vector3f pi = VECTORS.get();
		Vector3f px = VECTORS.get();

		switch (s) {
			case 0 + 0 + 0:
				px.set(mi.x, mi.y, mi.z);
				pi.set(mx.x, mx.y, mx.z);
				break;
			case 1 + 0 + 0:
				px.set(mx.x, mi.y, mi.z);
				pi.set(mi.x, mx.y, mx.z);
				break;
			case 0 + 2 + 0:
				px.set(mi.x, mx.y, mi.z);
				pi.set(mx.x, mi.y, mx.z);
				break;
			case 1 + 2 + 0:
				px.set(mx.x, mx.y, mi.z);
				pi.set(mi.x, mi.y, mx.z);
				break;
			case 0 + 0 + 4:
				px.set(mi.x, mi.y, mx.z);
				pi.set(mx.x, mx.y, mi.z);
				break;
			case 1 + 0 + 4:
				px.set(mx.x, mi.y, mx.z);
				pi.set(mi.x, mx.y, mi.z);
				break;
			case 0 + 2 + 4:
				px.set(mi.x, mx.y, mx.z);
				pi.set(mx.x, mi.y, mi.z);
				break;
			case 1 + 2 + 4:
				px.set(mx.x, mx.y, mx.z);
				pi.set(mi.x, mi.y, mi.z);
				break;
		}

		if (n.dot(px) + o < 0) return -1;
		if (n.dot(pi) + o >= 0) return +1;
		return 0;
	}

	/**
	 * Project minimum.
	 *
	 * @param v
	 *            the v
	 * @param signs
	 *            the signs
	 * @return the float
	 */
	public float ProjectMinimum(final Vector3f v, final int signs) {
		Vector3f[] b = { mx, mi };
		Vector3f p = VECTORS.get();
		p.set(b[signs >> 0 & 1].x, b[signs >> 1 & 1].y, b[signs >> 2 & 1].z);
		return p.dot(v);
	}

	/**
	 * Intersect.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return true, if successful
	 */
	public static boolean Intersect(final DbvtAabbMm a, final DbvtAabbMm b) {
		return a.mi.x <= b.mx.x && a.mx.x >= b.mi.x && a.mi.y <= b.mx.y && a.mx.y >= b.mi.y && a.mi.z <= b.mx.z
				&& a.mx.z >= b.mi.z;
	}

	/**
	 * Intersect.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param xform
	 *            the xform
	 * @return true, if successful
	 */
	public static boolean Intersect(final DbvtAabbMm a, final DbvtAabbMm b, final Transform xform) {
		Vector3f d0 = VECTORS.get();
		Vector3f d1 = VECTORS.get();
		Vector3f tmp = VECTORS.get();

		// JAVA NOTE: check
		b.Center(d0);
		xform.transform(d0);
		d0.sub(a.Center(tmp));

		MatrixUtil.transposeTransform(d1, d0, xform.basis);

		float[] s0 = { 0, 0 };
		float[] s1 = new float[2];
		s1[0] = xform.origin.dot(d0);
		s1[1] = s1[0];

		a.AddSpan(d0, s0, 0, s0, 1);
		b.AddSpan(d1, s1, 0, s1, 1);
		if ((s0[0] > s1[1]) || (s0[1] < s1[0])) return false;
		return true;
	}

	/**
	 * Intersect.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return true, if successful
	 */
	public static boolean Intersect(final DbvtAabbMm a, final Vector3f b) {
		return b.x >= a.mi.x && b.y >= a.mi.y && b.z >= a.mi.z && b.x <= a.mx.x && b.y <= a.mx.y && b.z <= a.mx.z;
	}

	/**
	 * Intersect.
	 *
	 * @param a
	 *            the a
	 * @param org
	 *            the org
	 * @param invdir
	 *            the invdir
	 * @param signs
	 *            the signs
	 * @return true, if successful
	 */
	public static boolean Intersect(final DbvtAabbMm a, final Vector3f org, final Vector3f invdir, final int[] signs) {
		Vector3f[] bounds = { a.mi, a.mx };
		float txmin = (bounds[signs[0]].x - org.x) * invdir.x;
		float txmax = (bounds[1 - signs[0]].x - org.x) * invdir.x;
		float tymin = (bounds[signs[1]].y - org.y) * invdir.y;
		float tymax = (bounds[1 - signs[1]].y - org.y) * invdir.y;
		if (txmin > tymax || tymin > txmax) return false;

		if (tymin > txmin) { txmin = tymin; }
		if (tymax < txmax) { txmax = tymax; }
		float tzmin = (bounds[signs[2]].z - org.z) * invdir.z;
		float tzmax = (bounds[1 - signs[2]].z - org.z) * invdir.z;
		if (txmin > tzmax || tzmin > txmax) return false;

		if (tzmin > txmin) { txmin = tzmin; }
		if (tzmax < txmax) { txmax = tzmax; }
		return txmax > 0;
	}

	/**
	 * Proximity.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the float
	 */
	public static float Proximity(final DbvtAabbMm a, final DbvtAabbMm b) {
		Vector3f d = VECTORS.get();
		Vector3f tmp = VECTORS.get();

		d.add(a.mi, a.mx);
		tmp.add(b.mi, b.mx);
		d.sub(tmp);
		return Math.abs(d.x) + Math.abs(d.y) + Math.abs(d.z);
	}

	/**
	 * Merge.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param r
	 *            the r
	 */
	public static void Merge(final DbvtAabbMm a, final DbvtAabbMm b, final DbvtAabbMm r) {
		for (int i = 0; i < 3; i++) {
			if (VectorUtil.getCoord(a.mi, i) < VectorUtil.getCoord(b.mi, i)) {
				VectorUtil.setCoord(r.mi, i, VectorUtil.getCoord(a.mi, i));
			} else {
				VectorUtil.setCoord(r.mi, i, VectorUtil.getCoord(b.mi, i));
			}

			if (VectorUtil.getCoord(a.mx, i) > VectorUtil.getCoord(b.mx, i)) {
				VectorUtil.setCoord(r.mx, i, VectorUtil.getCoord(a.mx, i));
			} else {
				VectorUtil.setCoord(r.mx, i, VectorUtil.getCoord(b.mx, i));
			}
		}
	}

	/**
	 * Not equal.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return true, if successful
	 */
	public static boolean NotEqual(final DbvtAabbMm a, final DbvtAabbMm b) {
		return a.mi.x != b.mi.x || a.mi.y != b.mi.y || a.mi.z != b.mi.z || a.mx.x != b.mx.x || a.mx.y != b.mx.y
				|| a.mx.z != b.mx.z;
	}

	/**
	 * Adds the span.
	 *
	 * @param d
	 *            the d
	 * @param smi
	 *            the smi
	 * @param smi_idx
	 *            the smi idx
	 * @param smx
	 *            the smx
	 * @param smx_idx
	 *            the smx idx
	 */
	private void AddSpan(final Vector3f d, final float[] smi, final int smi_idx, final float[] smx, final int smx_idx) {
		for (int i = 0; i < 3; i++) {
			if (VectorUtil.getCoord(d, i) < 0) {
				smi[smi_idx] += VectorUtil.getCoord(mx, i) * VectorUtil.getCoord(d, i);
				smx[smx_idx] += VectorUtil.getCoord(mi, i) * VectorUtil.getCoord(d, i);
			} else {
				smi[smi_idx] += VectorUtil.getCoord(mi, i) * VectorUtil.getCoord(d, i);
				smx[smx_idx] += VectorUtil.getCoord(mx, i) * VectorUtil.getCoord(d, i);
			}
		}
	}

}
