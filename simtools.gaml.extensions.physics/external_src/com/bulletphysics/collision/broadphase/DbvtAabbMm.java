/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

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

	private final Vector3f mi = new Vector3f();
	private final Vector3f mx = new Vector3f();

	public DbvtAabbMm() {}

	public DbvtAabbMm(final DbvtAabbMm o) {
		set(o);
	}

	public void set(final DbvtAabbMm o) {
		mi.set(o.mi);
		mx.set(o.mx);
	}

	public static void swap(final DbvtAabbMm p1, final DbvtAabbMm p2) {
		Vector3f tmp = VECTORS.get();

		tmp.set(p1.mi);
		p1.mi.set(p2.mi);
		p2.mi.set(tmp);

		tmp.set(p1.mx);
		p1.mx.set(p2.mx);
		p2.mx.set(tmp);
	}

	public Vector3f Center(final Vector3f out) {
		out.add(mi, mx);
		out.scale(0.5f);
		return out;
	}

	public Vector3f Lengths(final Vector3f out) {
		out.sub(mx, mi);
		return out;
	}

	public Vector3f Extents(final Vector3f out) {
		out.sub(mx, mi);
		out.scale(0.5f);
		return out;
	}

	public Vector3f Mins() {
		return mi;
	}

	public Vector3f Maxs() {
		return mx;
	}

	public static DbvtAabbMm FromCE(final Vector3f c, final Vector3f e, final DbvtAabbMm out) {
		DbvtAabbMm box = out;
		box.mi.sub(c, e);
		box.mx.add(c, e);
		return box;
	}

	public static DbvtAabbMm FromCR(final Vector3f c, final float r, final DbvtAabbMm out) {
		Vector3f tmp = VECTORS.get();
		tmp.set(r, r, r);
		return FromCE(c, tmp, out);
	}

	public static DbvtAabbMm FromMM(final Vector3f mi, final Vector3f mx, final DbvtAabbMm out) {
		DbvtAabbMm box = out;
		box.mi.set(mi);
		box.mx.set(mx);
		return box;
	}

	// public static DbvtAabbMm FromPoints( btVector3* pts,int n);
	// public static DbvtAabbMm FromPoints( btVector3** ppts,int n);

	public void Expand(final Vector3f e) {
		mi.sub(e);
		mx.add(e);
	}

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

	public boolean Contain(final DbvtAabbMm a) {
		return mi.x <= a.mi.x && mi.y <= a.mi.y && mi.z <= a.mi.z && mx.x >= a.mx.x && mx.y >= a.mx.y && mx.z >= a.mx.z;
	}

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

	public float ProjectMinimum(final Vector3f v, final int signs) {
		Vector3f[] b = new Vector3f[] { mx, mi };
		Vector3f p = VECTORS.get();
		p.set(b[signs >> 0 & 1].x, b[signs >> 1 & 1].y, b[signs >> 2 & 1].z);
		return p.dot(v);
	}

	public static boolean Intersect(final DbvtAabbMm a, final DbvtAabbMm b) {
		return a.mi.x <= b.mx.x && a.mx.x >= b.mi.x && a.mi.y <= b.mx.y && a.mx.y >= b.mi.y && a.mi.z <= b.mx.z
				&& a.mx.z >= b.mi.z;
	}

	public static boolean Intersect(final DbvtAabbMm a, final DbvtAabbMm b, final Transform xform) {
		Vector3f d0 = VECTORS.get();
		Vector3f d1 = VECTORS.get();
		Vector3f tmp = VECTORS.get();

		// JAVA NOTE: check
		b.Center(d0);
		xform.transform(d0);
		d0.sub(a.Center(tmp));

		MatrixUtil.transposeTransform(d1, d0, xform.basis);

		float[] s0 = new float[] { 0, 0 };
		float[] s1 = new float[2];
		s1[0] = xform.origin.dot(d0);
		s1[1] = s1[0];

		a.AddSpan(d0, s0, 0, s0, 1);
		b.AddSpan(d1, s1, 0, s1, 1);
		if (s0[0] > s1[1]) return false;
		if (s0[1] < s1[0]) return false;
		return true;
	}

	public static boolean Intersect(final DbvtAabbMm a, final Vector3f b) {
		return b.x >= a.mi.x && b.y >= a.mi.y && b.z >= a.mi.z && b.x <= a.mx.x && b.y <= a.mx.y && b.z <= a.mx.z;
	}

	public static boolean Intersect(final DbvtAabbMm a, final Vector3f org, final Vector3f invdir, final int[] signs) {
		Vector3f[] bounds = new Vector3f[] { a.mi, a.mx };
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

	public static float Proximity(final DbvtAabbMm a, final DbvtAabbMm b) {
		Vector3f d = VECTORS.get();
		Vector3f tmp = VECTORS.get();

		d.add(a.mi, a.mx);
		tmp.add(b.mi, b.mx);
		d.sub(tmp);
		return Math.abs(d.x) + Math.abs(d.y) + Math.abs(d.z);
	}

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

	public static boolean NotEqual(final DbvtAabbMm a, final DbvtAabbMm b) {
		return a.mi.x != b.mi.x || a.mi.y != b.mi.y || a.mi.z != b.mi.z || a.mx.x != b.mx.x || a.mx.y != b.mx.y
				|| a.mx.z != b.mx.z;
	}

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
