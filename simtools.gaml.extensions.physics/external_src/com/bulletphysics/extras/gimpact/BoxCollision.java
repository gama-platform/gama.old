/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371. email: projectileman@yahoo.com
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

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.AABBS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
public class BoxCollision {

	public static final float BOX_PLANE_EPSILON = 0.000001f;

	public static boolean BT_GREATER(final float x, final float y) {
		return Math.abs(x) > y;
	}

	public static float BT_MAX3(final float a, final float b, final float c) {
		return Math.max(a, Math.max(b, c));
	}

	public static float BT_MIN3(final float a, final float b, final float c) {
		return Math.min(a, Math.min(b, c));
	}

	public static boolean TEST_CROSS_EDGE_BOX_MCR(final Vector3f edge, final Vector3f absolute_edge,
			final Vector3f pointa, final Vector3f pointb, final Vector3f _extend, final int i_dir_0, final int i_dir_1,
			final int i_comp_0, final int i_comp_1) {
		float dir0 = -VectorUtil.getCoord(edge, i_dir_0);
		float dir1 = VectorUtil.getCoord(edge, i_dir_1);
		float pmin = VectorUtil.getCoord(pointa, i_comp_0) * dir0 + VectorUtil.getCoord(pointa, i_comp_1) * dir1;
		float pmax = VectorUtil.getCoord(pointb, i_comp_0) * dir0 + VectorUtil.getCoord(pointb, i_comp_1) * dir1;
		if (pmin > pmax) {
			// BT_SWAP_NUMBERS(pmin,pmax);
			pmin = pmin + pmax;
			pmax = pmin - pmax;
			pmin = pmin - pmax;
		}
		float abs_dir0 = VectorUtil.getCoord(absolute_edge, i_dir_0);
		float abs_dir1 = VectorUtil.getCoord(absolute_edge, i_dir_1);
		float rad =
				VectorUtil.getCoord(_extend, i_comp_0) * abs_dir0 + VectorUtil.getCoord(_extend, i_comp_1) * abs_dir1;
		if (pmin > rad || -rad > pmax) return false;
		return true;
	}

	public static boolean TEST_CROSS_EDGE_BOX_X_AXIS_MCR(final Vector3f edge, final Vector3f absolute_edge,
			final Vector3f pointa, final Vector3f pointb, final Vector3f _extend) {
		return TEST_CROSS_EDGE_BOX_MCR(edge, absolute_edge, pointa, pointb, _extend, 2, 1, 1, 2);
	}

	public static boolean TEST_CROSS_EDGE_BOX_Y_AXIS_MCR(final Vector3f edge, final Vector3f absolute_edge,
			final Vector3f pointa, final Vector3f pointb, final Vector3f _extend) {
		return TEST_CROSS_EDGE_BOX_MCR(edge, absolute_edge, pointa, pointb, _extend, 0, 2, 2, 0);
	}

	public static boolean TEST_CROSS_EDGE_BOX_Z_AXIS_MCR(final Vector3f edge, final Vector3f absolute_edge,
			final Vector3f pointa, final Vector3f pointb, final Vector3f _extend) {
		return TEST_CROSS_EDGE_BOX_MCR(edge, absolute_edge, pointa, pointb, _extend, 1, 0, 0, 1);
	}

	/**
	 * Returns the dot product between a vec3f and the col of a matrix.
	 */
	public static float bt_mat3_dot_col(final Matrix3f mat, final Vector3f vec3, final int colindex) {
		return vec3.x * mat.getElement(0, colindex) + vec3.y * mat.getElement(1, colindex)
				+ vec3.z * mat.getElement(2, colindex);
	}

	/**
	 * Compairison of transformation objects.
	 */
	public static boolean compareTransformsEqual(final Transform t1, final Transform t2) {
		return t1.equals(t2);
	}

	////////////////////////////////////////////////////////////////////////////

	public static class BoxBoxTransformCache {
		public final Vector3f T1to0 = new Vector3f(); // Transforms translation of model1 to model 0
		public final Matrix3f R1to0 = new Matrix3f(); // Transforms Rotation of model1 to model 0, equal to R0' * R1
		public final Matrix3f AR = new Matrix3f(); // Absolute value of m_R1to0

		public void set(final BoxBoxTransformCache cache) {
			throw new UnsupportedOperationException();
		}

		public void calc_absolute_matrix() {
			// static const btVector3 vepsi(1e-6f,1e-6f,1e-6f);
			// m_AR[0] = vepsi + m_R1to0[0].absolute();
			// m_AR[1] = vepsi + m_R1to0[1].absolute();
			// m_AR[2] = vepsi + m_R1to0[2].absolute();

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					AR.setElement(i, j, 1e-6f + Math.abs(R1to0.getElement(i, j)));
				}
			}
		}

		/**
		 * Calc the transformation relative 1 to 0. Inverts matrics by transposing.
		 */
		public void calc_from_homogenic(final Transform trans0, final Transform trans1) {
			Transform temp_trans = TRANSFORMS.get();
			temp_trans.inverse(trans0);
			temp_trans.mul(trans1);

			T1to0.set(temp_trans.origin);
			R1to0.set(temp_trans.basis);

			calc_absolute_matrix();
			TRANSFORMS.release(temp_trans);
		}

		/**
		 * Calcs the full invertion of the matrices. Useful for scaling matrices.
		 */
		public void calc_from_full_invert(final Transform trans0, final Transform trans1) {
			R1to0.invert(trans0.basis);
			T1to0.negate(trans0.origin);
			R1to0.transform(T1to0);

			Vector3f tmp = VECTORS.get();
			tmp.set(trans1.origin);
			R1to0.transform(tmp);
			T1to0.add(tmp);

			R1to0.mul(trans1.basis);

			calc_absolute_matrix();
			VECTORS.release(tmp);
		}

		public Vector3f transform(Vector3f point, final Vector3f out) {
			if (point == out) { point = VECTORS.get(point); } // AD to release ?

			Vector3f tmp = VECTORS.get();
			R1to0.getRow(0, tmp);
			out.x = tmp.dot(point) + T1to0.x;
			R1to0.getRow(1, tmp);
			out.y = tmp.dot(point) + T1to0.y;
			R1to0.getRow(2, tmp);
			out.z = tmp.dot(point) + T1to0.z;
			VECTORS.release(tmp);
			return out;
		}
	}

	////////////////////////////////////////////////////////////////////////////

	public static class AABB {
		public final Vector3f min = new Vector3f();
		public final Vector3f max = new Vector3f();

		public AABB() {}

		public AABB(final Vector3f V1, final Vector3f V2, final Vector3f V3) {
			calc_from_triangle(V1, V2, V3);
		}

		public AABB(final Vector3f V1, final Vector3f V2, final Vector3f V3, final float margin) {
			calc_from_triangle_margin(V1, V2, V3, margin);
		}

		public AABB(final AABB other) {
			set(other);
		}

		public AABB(final AABB other, final float margin) {
			this(other);
			min.x -= margin;
			min.y -= margin;
			min.z -= margin;
			max.x += margin;
			max.y += margin;
			max.z += margin;
		}

		public void init(final Vector3f V1, final Vector3f V2, final Vector3f V3, final float margin) {
			calc_from_triangle_margin(V1, V2, V3, margin);
		}

		public void set(final AABB other) {
			min.set(other.min);
			max.set(other.max);
		}

		public void invalidate() {
			min.set(BulletGlobals.SIMD_INFINITY, BulletGlobals.SIMD_INFINITY, BulletGlobals.SIMD_INFINITY);
			max.set(-BulletGlobals.SIMD_INFINITY, -BulletGlobals.SIMD_INFINITY, -BulletGlobals.SIMD_INFINITY);
		}

		public void increment_margin(final float margin) {
			min.x -= margin;
			min.y -= margin;
			min.z -= margin;
			max.x += margin;
			max.y += margin;
			max.z += margin;
		}

		public void copy_with_margin(final AABB other, final float margin) {
			min.x = other.min.x - margin;
			min.y = other.min.y - margin;
			min.z = other.min.z - margin;

			max.x = other.max.x + margin;
			max.y = other.max.y + margin;
			max.z = other.max.z + margin;
		}

		public void calc_from_triangle(final Vector3f V1, final Vector3f V2, final Vector3f V3) {
			min.x = BT_MIN3(V1.x, V2.x, V3.x);
			min.y = BT_MIN3(V1.y, V2.y, V3.y);
			min.z = BT_MIN3(V1.z, V2.z, V3.z);

			max.x = BT_MAX3(V1.x, V2.x, V3.x);
			max.y = BT_MAX3(V1.y, V2.y, V3.y);
			max.z = BT_MAX3(V1.z, V2.z, V3.z);
		}

		public void calc_from_triangle_margin(final Vector3f V1, final Vector3f V2, final Vector3f V3,
				final float margin) {
			calc_from_triangle(V1, V2, V3);
			min.x -= margin;
			min.y -= margin;
			min.z -= margin;
			max.x += margin;
			max.y += margin;
			max.z += margin;
		}

		/**
		 * Apply a transform to an AABB.
		 */
		public void appy_transform(final Transform trans) {
			Vector3f tmp = VECTORS.get();

			Vector3f center = VECTORS.get();
			center.add(max, min);
			center.scale(0.5f);

			Vector3f extends_ = VECTORS.get();
			extends_.sub(max, center);

			// Compute new center
			trans.transform(center);

			Vector3f textends = VECTORS.get();

			trans.basis.getRow(0, tmp);
			tmp.absolute();
			textends.x = extends_.dot(tmp);

			trans.basis.getRow(1, tmp);
			tmp.absolute();
			textends.y = extends_.dot(tmp);

			trans.basis.getRow(2, tmp);
			tmp.absolute();
			textends.z = extends_.dot(tmp);

			min.sub(center, textends);
			max.add(center, textends);
			VECTORS.release(tmp, center, extends_, textends);
		}

		/**
		 * Apply a transform to an AABB.
		 */
		public void appy_transform_trans_cache(final BoxBoxTransformCache trans) {
			Vector3f tmp = VECTORS.get();

			Vector3f center = VECTORS.get();
			center.add(max, min);
			center.scale(0.5f);

			Vector3f extends_ = VECTORS.get();
			extends_.sub(max, center);

			// Compute new center
			trans.transform(center, center);

			Vector3f textends = VECTORS.get();

			trans.R1to0.getRow(0, tmp);
			tmp.absolute();
			textends.x = extends_.dot(tmp);

			trans.R1to0.getRow(1, tmp);
			tmp.absolute();
			textends.y = extends_.dot(tmp);

			trans.R1to0.getRow(2, tmp);
			tmp.absolute();
			textends.z = extends_.dot(tmp);

			min.sub(center, textends);
			max.add(center, textends);
			VECTORS.release(tmp, center, extends_, textends);
		}

		/**
		 * Merges a Box.
		 */
		public void merge(final AABB box) {
			min.x = Math.min(min.x, box.min.x);
			min.y = Math.min(min.y, box.min.y);
			min.z = Math.min(min.z, box.min.z);

			max.x = Math.max(max.x, box.max.x);
			max.y = Math.max(max.y, box.max.y);
			max.z = Math.max(max.z, box.max.z);
		}

		/**
		 * Merges a point.
		 */
		public void merge_point(final Vector3f point) {
			min.x = Math.min(min.x, point.x);
			min.y = Math.min(min.y, point.y);
			min.z = Math.min(min.z, point.z);

			max.x = Math.max(max.x, point.x);
			max.y = Math.max(max.y, point.y);
			max.z = Math.max(max.z, point.z);
		}

		/**
		 * Gets the extend and center.
		 */
		public void get_center_extend(final Vector3f center, final Vector3f extend) {
			center.add(max, min);
			center.scale(0.5f);

			extend.sub(max, center);
		}

		/**
		 * Finds the intersecting box between this box and the other.
		 */
		public void find_intersection(final AABB other, final AABB intersection) {
			intersection.min.x = Math.max(other.min.x, min.x);
			intersection.min.y = Math.max(other.min.y, min.y);
			intersection.min.z = Math.max(other.min.z, min.z);

			intersection.max.x = Math.min(other.max.x, max.x);
			intersection.max.y = Math.min(other.max.y, max.y);
			intersection.max.z = Math.min(other.max.z, max.z);
		}

		public boolean has_collision(final AABB other) {
			if (min.x > other.max.x || max.x < other.min.x || min.y > other.max.y || max.y < other.min.y
					|| min.z > other.max.z || max.z < other.min.z)
				return false;
			return true;
		}

		/**
		 * Finds the Ray intersection parameter.
		 *
		 * @param aabb
		 *            aligned box
		 * @param vorigin
		 *            a vec3f with the origin of the ray
		 * @param vdir
		 *            a vec3f with the direction of the ray
		 */
		public boolean collide_ray(final Vector3f vorigin, final Vector3f vdir) {
			Vector3f extents = VECTORS.get(), center = VECTORS.get();
			get_center_extend(center, extents);
			try {
				float Dx = vorigin.x - center.x;
				if (BT_GREATER(Dx, extents.x) && Dx * vdir.x >= 0.0f) return false;

				float Dy = vorigin.y - center.y;
				if (BT_GREATER(Dy, extents.y) && Dy * vdir.y >= 0.0f) return false;

				float Dz = vorigin.z - center.z;
				if (BT_GREATER(Dz, extents.z) && Dz * vdir.z >= 0.0f) return false;

				float f = vdir.y * Dz - vdir.z * Dy;
				if (Math.abs(f) > extents.y * Math.abs(vdir.z) + extents.z * Math.abs(vdir.y)) return false;

				f = vdir.z * Dx - vdir.x * Dz;
				if (Math.abs(f) > extents.x * Math.abs(vdir.z) + extents.z * Math.abs(vdir.x)) return false;

				f = vdir.x * Dy - vdir.y * Dx;
				if (Math.abs(f) > extents.x * Math.abs(vdir.y) + extents.y * Math.abs(vdir.x)) return false;
			} finally {
				VECTORS.release(center, extents);
			}
			return true;
		}

		public void projection_interval(final Vector3f direction, final float[] vmin, final float[] vmax) {
			Vector3f tmp = VECTORS.get();

			Vector3f center = VECTORS.get();
			Vector3f extend = VECTORS.get();
			get_center_extend(center, extend);

			float _fOrigin = direction.dot(center);
			tmp.absolute(direction);
			float _fMaximumExtent = extend.dot(tmp);
			vmin[0] = _fOrigin - _fMaximumExtent;
			vmax[0] = _fOrigin + _fMaximumExtent;
			VECTORS.release(tmp, center, extend);
		}

		public PlaneIntersectionType plane_classify(final Vector4f plane) {
			Vector3f tmp = VECTORS.get();
			try {
				float[] _fmin = new float[1], _fmax = new float[1];
				tmp.set(plane.x, plane.y, plane.z);
				projection_interval(tmp, _fmin, _fmax);

				if (plane.w > _fmax[0] + BOX_PLANE_EPSILON) return PlaneIntersectionType.BACK_PLANE; // 0

				if (plane.w + BOX_PLANE_EPSILON >= _fmin[0]) return PlaneIntersectionType.COLLIDE_PLANE; // 1

				return PlaneIntersectionType.FRONT_PLANE; // 2
			} finally {
				VECTORS.release(tmp);
			}
		}

		public boolean overlapping_trans_conservative(final AABB box, final Transform trans1_to_0) {
			AABB tbox = AABBS.get(box);
			tbox.appy_transform(trans1_to_0);
			AABBS.release(tbox);
			return has_collision(tbox);
		}

		public boolean overlapping_trans_conservative2(final AABB box, final BoxBoxTransformCache trans1_to_0) {
			AABB tbox = AABBS.get(box);
			tbox.appy_transform_trans_cache(trans1_to_0);
			AABBS.release(tbox);
			return has_collision(tbox);
		}

		/**
		 * transcache is the transformation cache from box to this AABB.
		 */
		public boolean overlapping_trans_cache(final AABB box, final BoxBoxTransformCache transcache,
				final boolean fulltest) {
			Vector3f tmp = VECTORS.get();

			// Taken from OPCODE
			Vector3f ea = VECTORS.get(), eb = VECTORS.get(); // extends
			Vector3f ca = VECTORS.get(), cb = VECTORS.get(); // extends
			get_center_extend(ca, ea);
			box.get_center_extend(cb, eb);

			Vector3f T = VECTORS.get();
			float t, t2;

			try {
				// Class I : A's basis vectors
				for (int i = 0; i < 3; i++) {
					transcache.R1to0.getRow(i, tmp);
					VectorUtil.setCoord(T, i,
							tmp.dot(cb) + VectorUtil.getCoord(transcache.T1to0, i) - VectorUtil.getCoord(ca, i));

					transcache.AR.getRow(i, tmp);
					t = tmp.dot(eb) + VectorUtil.getCoord(ea, i);
					if (BT_GREATER(VectorUtil.getCoord(T, i), t)) return false;
				}
				// Class II : B's basis vectors
				for (int i = 0; i < 3; i++) {
					t = bt_mat3_dot_col(transcache.R1to0, T, i);
					t2 = bt_mat3_dot_col(transcache.AR, ea, i) + VectorUtil.getCoord(eb, i);
					if (BT_GREATER(t, t2)) return false;
				}
				// Class III : 9 cross products
				if (fulltest) {
					int m, n, o, p, q, r;
					for (int i = 0; i < 3; i++) {
						m = (i + 1) % 3;
						n = (i + 2) % 3;
						o = i == 0 ? 1 : 0;
						p = i == 2 ? 1 : 2;
						for (int j = 0; j < 3; j++) {
							q = j == 2 ? 1 : 2;
							r = j == 0 ? 1 : 0;
							t = VectorUtil.getCoord(T, n) * transcache.R1to0.getElement(m, j)
									- VectorUtil.getCoord(T, m) * transcache.R1to0.getElement(n, j);
							t2 = VectorUtil.getCoord(ea, o) * transcache.AR.getElement(p, j)
									+ VectorUtil.getCoord(ea, p) * transcache.AR.getElement(o, j)
									+ VectorUtil.getCoord(eb, r) * transcache.AR.getElement(i, q)
									+ VectorUtil.getCoord(eb, q) * transcache.AR.getElement(i, r);
							if (BT_GREATER(t, t2)) return false;
						}
					}
				}
				return true;
			} finally {
				VECTORS.release(ea, eb, ca, cb, tmp, T);
			}
		}

		/**
		 * Simple test for planes.
		 */
		public boolean collide_plane(final Vector4f plane) {
			PlaneIntersectionType classify = plane_classify(plane);
			return classify == PlaneIntersectionType.COLLIDE_PLANE;
		}

		/**
		 * Test for a triangle, with edges.
		 */
		public boolean collide_triangle_exact(final Vector3f p1, final Vector3f p2, final Vector3f p3,
				final Vector4f triangle_plane) {
			if (!collide_plane(triangle_plane)) return false;
			Vector3f center = VECTORS.get(), extends_ = VECTORS.get();
			get_center_extend(center, extends_);

			Vector3f v1 = VECTORS.get();
			v1.sub(p1, center);
			Vector3f v2 = VECTORS.get();
			v2.sub(p2, center);
			Vector3f v3 = VECTORS.get();
			v3.sub(p3, center);

			// First axis
			Vector3f diff = VECTORS.get();
			diff.sub(v2, v1);
			Vector3f abs_diff = VECTORS.get();
			abs_diff.absolute(diff);

			// Test With X axis
			TEST_CROSS_EDGE_BOX_X_AXIS_MCR(diff, abs_diff, v1, v3, extends_);
			// Test With Y axis
			TEST_CROSS_EDGE_BOX_Y_AXIS_MCR(diff, abs_diff, v1, v3, extends_);
			// Test With Z axis
			TEST_CROSS_EDGE_BOX_Z_AXIS_MCR(diff, abs_diff, v1, v3, extends_);

			diff.sub(v3, v2);
			abs_diff.absolute(diff);

			// Test With X axis
			TEST_CROSS_EDGE_BOX_X_AXIS_MCR(diff, abs_diff, v2, v1, extends_);
			// Test With Y axis
			TEST_CROSS_EDGE_BOX_Y_AXIS_MCR(diff, abs_diff, v2, v1, extends_);
			// Test With Z axis
			TEST_CROSS_EDGE_BOX_Z_AXIS_MCR(diff, abs_diff, v2, v1, extends_);

			diff.sub(v1, v3);
			abs_diff.absolute(diff);

			// Test With X axis
			TEST_CROSS_EDGE_BOX_X_AXIS_MCR(diff, abs_diff, v3, v2, extends_);
			// Test With Y axis
			TEST_CROSS_EDGE_BOX_Y_AXIS_MCR(diff, abs_diff, v3, v2, extends_);
			// Test With Z axis
			TEST_CROSS_EDGE_BOX_Z_AXIS_MCR(diff, abs_diff, v3, v2, extends_);
			VECTORS.release(v1, v2, v3, diff, abs_diff, center, extends_);
			return true;
		}
	}

}
