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

package com.bulletphysics.linearmath;

import static com.bulletphysics.Pools.MATRICES;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.UniformScalingShape;

/**
 * Transform represents translation and rotation (rigid transform). Scaling and shearing is not supported.
 * <p>
 *
 * You can use local shape scaling or {@link UniformScalingShape} for static rescaling of collision objects.
 *
 * @author jezek2
 */
public class Transform {

	// protected BulletStack stack;

	/** Rotation matrix of this Transform. */
	public final Matrix3f basis = new Matrix3f();

	/** Translation vector of this Transform. */
	public final Vector3f origin = new Vector3f();

	private final Vector3f tmp = new Vector3f();

	public Transform() {}

	public Transform(final Matrix3f mat) {
		basis.set(mat);
	}

	public Transform(final Matrix4f mat) {
		set(mat);
	}

	public Transform(final Transform tr) {
		set(tr);
	}

	public void set(final Transform tr) {
		basis.set(tr.basis);
		origin.set(tr.origin);
	}

	public void set(final Matrix3f mat) {
		basis.set(mat);
		origin.set(0f, 0f, 0f);
	}

	public void set(final Matrix4f mat) {
		mat.getRotationScale(basis);
		origin.set(mat.m03, mat.m13, mat.m23);
	}

	public void transform(final Vector3f v) {
		basis.transform(v);
		v.add(origin);
	}

	public void setIdentity() {
		basis.setIdentity();
		origin.set(0f, 0f, 0f);
	}

	public void inverse() {
		basis.transpose();
		origin.scale(-1f);
		basis.transform(origin);
	}

	public void inverse(final Transform tr) {
		set(tr);
		inverse();
	}

	public void mul(final Transform tr) {
		// Vector3f tmp = VECTORS.get(tr.origin);
		tmp.set(tr.origin);
		transform(tmp);

		basis.mul(tr.basis);
		origin.set(tmp);
		// VECTORS.release(tmp);
	}

	public void mul(final Transform tr1, final Transform tr2) {
		// Vector3f vec = VECTORS.get(tr2.origin);
		tmp.set(tr2.origin);
		tr1.transform(tmp);

		basis.mul(tr1.basis, tr2.basis);
		origin.set(tmp);
		// VECTORS.release(vec);
	}

	public void invXform(final Vector3f inVec, final Vector3f out) {
		out.sub(inVec, origin);
		Matrix3f mat = MATRICES.get(basis);
		mat.transpose();
		mat.transform(out);
		MATRICES.release(mat);
	}

	public Quat4f getRotation(final Quat4f out) {
		MatrixUtil.getRotation(basis, out);
		return out;
	}

	public void setRotation(final Quat4f q) {
		MatrixUtil.setRotation(basis, q);
	}

	public void setFromOpenGLMatrix(final float[] m) {
		MatrixUtil.setFromOpenGLSubMatrix(basis, m);
		origin.set(m[12], m[13], m[14]);
	}

	public void getOpenGLMatrix(final float[] m) {
		MatrixUtil.getOpenGLSubMatrix(basis, m);
		m[12] = origin.x;
		m[13] = origin.y;
		m[14] = origin.z;
		m[15] = 1f;
	}

	public Matrix4f getMatrix(final Matrix4f out) {
		out.set(basis);
		out.m03 = origin.x;
		out.m13 = origin.y;
		out.m23 = origin.z;
		return out;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Transform)) return false;
		Transform tr = (Transform) obj;
		return basis.equals(tr.basis) && origin.equals(tr.origin);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 41 * hash + basis.hashCode();
		hash = 41 * hash + origin.hashCode();
		return hash;
	}

}
