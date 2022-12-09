/*******************************************************************************************************
 *
 * Transform.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The tmp. */
	private final Vector3f tmp = new Vector3f();

	/**
	 * Instantiates a new transform.
	 */
	public Transform() {}

	/**
	 * Instantiates a new transform.
	 *
	 * @param mat the mat
	 */
	public Transform(final Matrix3f mat) {
		basis.set(mat);
	}

	/**
	 * Instantiates a new transform.
	 *
	 * @param mat the mat
	 */
	public Transform(final Matrix4f mat) {
		set(mat);
	}

	/**
	 * Instantiates a new transform.
	 *
	 * @param tr the tr
	 */
	public Transform(final Transform tr) {
		set(tr);
	}

	/**
	 * Sets the.
	 *
	 * @param tr the tr
	 */
	public void set(final Transform tr) {
		basis.set(tr.basis);
		origin.set(tr.origin);
	}

	/**
	 * Sets the.
	 *
	 * @param mat the mat
	 */
	public void set(final Matrix3f mat) {
		basis.set(mat);
		origin.set(0f, 0f, 0f);
	}

	/**
	 * Sets the.
	 *
	 * @param mat the mat
	 */
	public void set(final Matrix4f mat) {
		mat.getRotationScale(basis);
		origin.set(mat.m03, mat.m13, mat.m23);
	}

	/**
	 * Transform.
	 *
	 * @param v the v
	 */
	public void transform(final Vector3f v) {
		basis.transform(v);
		v.add(origin);
	}

	/**
	 * Sets the identity.
	 */
	public void setIdentity() {
		basis.setIdentity();
		origin.set(0f, 0f, 0f);
	}

	/**
	 * Inverse.
	 */
	public void inverse() {
		basis.transpose();
		origin.scale(-1f);
		basis.transform(origin);
	}

	/**
	 * Inverse.
	 *
	 * @param tr the tr
	 */
	public void inverse(final Transform tr) {
		set(tr);
		inverse();
	}

	/**
	 * Mul.
	 *
	 * @param tr the tr
	 */
	public void mul(final Transform tr) {
		// Vector3f tmp = VECTORS.get(tr.origin);
		tmp.set(tr.origin);
		transform(tmp);

		basis.mul(tr.basis);
		origin.set(tmp);
		// VECTORS.release(tmp);
	}

	/**
	 * Mul.
	 *
	 * @param tr1 the tr 1
	 * @param tr2 the tr 2
	 */
	public void mul(final Transform tr1, final Transform tr2) {
		// Vector3f vec = VECTORS.get(tr2.origin);
		tmp.set(tr2.origin);
		tr1.transform(tmp);

		basis.mul(tr1.basis, tr2.basis);
		origin.set(tmp);
		// VECTORS.release(vec);
	}

	/**
	 * Inv xform.
	 *
	 * @param inVec the in vec
	 * @param out the out
	 */
	public void invXform(final Vector3f inVec, final Vector3f out) {
		out.sub(inVec, origin);
		Matrix3f mat = MATRICES.get(basis);
		mat.transpose();
		mat.transform(out);
		MATRICES.release(mat);
	}

	/**
	 * Gets the rotation.
	 *
	 * @param out the out
	 * @return the rotation
	 */
	public Quat4f getRotation(final Quat4f out) {
		MatrixUtil.getRotation(basis, out);
		return out;
	}

	/**
	 * Sets the rotation.
	 *
	 * @param q the new rotation
	 */
	public void setRotation(final Quat4f q) {
		MatrixUtil.setRotation(basis, q);
	}

	/**
	 * Sets the from open GL matrix.
	 *
	 * @param m the new from open GL matrix
	 */
	public void setFromOpenGLMatrix(final float[] m) {
		MatrixUtil.setFromOpenGLSubMatrix(basis, m);
		origin.set(m[12], m[13], m[14]);
	}

	/**
	 * Gets the open GL matrix.
	 *
	 * @param m the m
	 * @return the open GL matrix
	 */
	public void getOpenGLMatrix(final float[] m) {
		MatrixUtil.getOpenGLSubMatrix(basis, m);
		m[12] = origin.x;
		m[13] = origin.y;
		m[14] = origin.z;
		m[15] = 1f;
	}

	/**
	 * Gets the matrix.
	 *
	 * @param out the out
	 * @return the matrix
	 */
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
