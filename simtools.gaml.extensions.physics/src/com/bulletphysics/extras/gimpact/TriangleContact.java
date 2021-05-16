/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371.
 * email: projectileman@yahoo.com
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.util.ArrayPool;
import com.bulletphysics.util.ObjectArrayList;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 *
 * @author jezek2
 */
public class TriangleContact {
	
	private final ArrayPool<int[]> intArrays = ArrayPool.get(int.class);
	
	public static final int MAX_TRI_CLIPPING = 16;

    public float penetration_depth;
    public int point_count;
    public final Vector4f separating_normal = new Vector4f();
    public Vector3f[] points = new Vector3f[MAX_TRI_CLIPPING];

	public TriangleContact() {
		for (int i=0; i<points.length; i++) {
			points[i] = new Vector3f();
		}
	}

	public TriangleContact(TriangleContact other) {
		copy_from(other);
	}

	public void set(TriangleContact other) {
		copy_from(other);
	}
	
	public void copy_from(TriangleContact other) {
		penetration_depth = other.penetration_depth;
		separating_normal.set(other.separating_normal);
		point_count = other.point_count;
		int i = point_count;
		while ((i--) != 0) {
			points[i].set(other.points[i]);
		}
	}
	
	/**
	 * Classify points that are closer.
	 */
	public void merge_points(Vector4f plane, float margin, ObjectArrayList<Vector3f> points, int point_count) {
		this.point_count = 0;
		penetration_depth = -1000.0f;

		int[] point_indices = intArrays.getFixed(MAX_TRI_CLIPPING);

		for (int _k = 0; _k < point_count; _k++) {
			float _dist = -ClipPolygon.distance_point_plane(plane, points.getQuick(_k)) + margin;

			if (_dist >= 0.0f) {
				if (_dist > penetration_depth) {
					penetration_depth = _dist;
					point_indices[0] = _k;
					this.point_count = 1;
				}
				else if ((_dist + BulletGlobals.SIMD_EPSILON) >= penetration_depth) {
					point_indices[this.point_count] = _k;
					this.point_count++;
				}
			}
		}

		for (int _k = 0; _k < this.point_count; _k++) {
			this.points[_k].set(points.getQuick(point_indices[_k]));
		}
		
		intArrays.release(point_indices);
	}

}
