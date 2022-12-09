/*******************************************************************************************************
 *
 * Generic6DofConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

/*
 * 2007-09-09 btGeneric6DofConstraint Refactored by Francisco Le�n email: projectileman@yahoo.com http://gimpact.sf.net
 */

package com.bulletphysics.dynamics.constraintsolver;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
///
import com.bulletphysics.linearmath.VectorUtil;

/*
 * !
 *
 */
/**
 * Generic6DofConstraint between two rigidbodies each with a pivot point that descibes the axis location in local space.
 * <p>
 *
 * Generic6DofConstraint can leave any of the 6 degree of freedom "free" or "locked". Currently this limit supports
 * rotational motors.<br>
 *
 * <ul>
 * <li>For linear limits, use {@link #setLinearUpperLimit}, {@link #setLinearLowerLimit}. You can set the parameters
 * with the {@link TranslationalLimitMotor} structure accsesible through the {@link #getTranslationalLimitMotor} method.
 * At this moment translational motors are not supported. May be in the future.</li>
 *
 * <li>For angular limits, use the {@link RotationalLimitMotor} structure for configuring the limit. This is accessible
 * through {@link #getRotationalLimitMotor} method, this brings support for limit parameters and motors.</li>
 *
 * <li>Angulars limits have these possible ranges:
 * <table border="1">
 * <tr>
 * <td><b>AXIS</b></td>
 * <td><b>MIN ANGLE</b></td>
 * <td><b>MAX ANGLE</b></td>
 * </tr>
 * <tr>
 * <td>X</td>
 * <td>-PI</td>
 * <td>PI</td>
 * </tr>
 * <tr>
 * <td>Y</td>
 * <td>-PI/2</td>
 * <td>PI/2</td>
 * </tr>
 * <tr>
 * <td>Z</td>
 * <td>-PI/2</td>
 * <td>PI/2</td>
 * </tr>
 * </table>
 * </li>
 * </ul>
 *
 * @author jezek2
 */
public class Generic6DofConstraint extends TypedConstraint {

	/** The frame in A. */
	protected final Transform frameInA = new Transform(); // !< the constraint space w.r.t body A
	
	/** The frame in B. */
	protected final Transform frameInB = new Transform(); // !< the constraint space w.r.t body B

	/** The jac linear. */
	protected final JacobianEntry[] jacLinear/* [3] */ =
			new JacobianEntry[] { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() }; // !< 3 orthogonal
																									// linear
																									/** The jac ang. */
																									// constraints
	protected final JacobianEntry[] jacAng/* [3] */ =
			new JacobianEntry[] { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() }; // !< 3 orthogonal
																									// angular
																									// constraints

	/** The linear limits. */
																									protected final TranslationalLimitMotor linearLimits = new TranslationalLimitMotor();

	/** The angular limits. */
	protected final RotationalLimitMotor[] angularLimits/* [3] */ = new RotationalLimitMotor[] {
			new RotationalLimitMotor(), new RotationalLimitMotor(), new RotationalLimitMotor() };

	/** The time step. */
	protected float timeStep;
	
	/** The calculated transform A. */
	protected final Transform calculatedTransformA = new Transform();
	
	/** The calculated transform B. */
	protected final Transform calculatedTransformB = new Transform();
	
	/** The calculated axis angle diff. */
	protected final Vector3f calculatedAxisAngleDiff = new Vector3f();
	
	/** The calculated axis. */
	protected final Vector3f[] calculatedAxis/* [3] */ =
			new Vector3f[] { new Vector3f(), new Vector3f(), new Vector3f() };

	/** The anchor pos. */
	protected final Vector3f anchorPos = new Vector3f(); // point betwen pivots of bodies A and B to solve linear axes

	/** The use linear reference frame A. */
	protected boolean useLinearReferenceFrameA;

	/**
	 * Instantiates a new generic 6 dof constraint.
	 */
	public Generic6DofConstraint() {
		super(TypedConstraintType.D6_CONSTRAINT_TYPE);
		useLinearReferenceFrameA = true;
	}

	/**
	 * Instantiates a new generic 6 dof constraint.
	 *
	 * @param rbA the rb A
	 * @param rbB the rb B
	 * @param frameInA the frame in A
	 * @param frameInB the frame in B
	 * @param useLinearReferenceFrameA the use linear reference frame A
	 */
	public Generic6DofConstraint(final RigidBody rbA, final RigidBody rbB, final Transform frameInA,
			final Transform frameInB, final boolean useLinearReferenceFrameA) {
		super(TypedConstraintType.D6_CONSTRAINT_TYPE, rbA, rbB);
		this.frameInA.set(frameInA);
		this.frameInB.set(frameInB);
		this.useLinearReferenceFrameA = useLinearReferenceFrameA;
	}

	/**
	 * Gets the matrix elem.
	 *
	 * @param mat the mat
	 * @param index the index
	 * @return the matrix elem
	 */
	private static float getMatrixElem(final Matrix3f mat, final int index) {
		int i = index % 3;
		int j = index / 3;
		return mat.getElement(i, j);
	}

	/**
	 * MatrixToEulerXYZ from http://www.geometrictools.com/LibFoundation/Mathematics/Wm4Matrix3.inl.html
	 */
	private static boolean matrixToEulerXYZ(final Matrix3f mat, final Vector3f xyz) {
		// // rot = cy*cz -cy*sz sy
		// // cz*sx*sy+cx*sz cx*cz-sx*sy*sz -cy*sx
		// // -cx*cz*sy+sx*sz cz*sx+cx*sy*sz cx*cy
		//

		if (getMatrixElem(mat, 2) < 1.0f) {
			if (getMatrixElem(mat, 2) > -1.0f) {
				xyz.x = (float) Math.atan2(-getMatrixElem(mat, 5), getMatrixElem(mat, 8));
				xyz.y = (float) Math.asin(getMatrixElem(mat, 2));
				xyz.z = (float) Math.atan2(-getMatrixElem(mat, 1), getMatrixElem(mat, 0));
				return true;
			} else {
				// WARNING. Not unique. XA - ZA = -atan2(r10,r11)
				xyz.x = -(float) Math.atan2(getMatrixElem(mat, 3), getMatrixElem(mat, 4));
				xyz.y = -BulletGlobals.SIMD_HALF_PI;
				xyz.z = 0.0f;
				return false;
			}
		} else {
			// WARNING. Not unique. XAngle + ZAngle = atan2(r10,r11)
			xyz.x = (float) Math.atan2(getMatrixElem(mat, 3), getMatrixElem(mat, 4));
			xyz.y = BulletGlobals.SIMD_HALF_PI;
			xyz.z = 0.0f;
		}

		return false;
	}

	/**
	 * Calcs the euler angles between the two bodies.
	 */
	protected void calculateAngleInfo() {
		Matrix3f mat = MATRICES.get();

		Matrix3f relative_frame = MATRICES.get();
		mat.set(calculatedTransformA.basis);
		MatrixUtil.invert(mat);
		relative_frame.mul(mat, calculatedTransformB.basis);

		matrixToEulerXYZ(relative_frame, calculatedAxisAngleDiff);

		// in euler angle mode we do not actually constrain the angular velocity
		// along the axes axis[0] and axis[2] (although we do use axis[1]) :
		//
		// to get constrain w2-w1 along ...not
		// ------ --------------------- ------
		// d(angle[0])/dt = 0 ax[1] x ax[2] ax[0]
		// d(angle[1])/dt = 0 ax[1]
		// d(angle[2])/dt = 0 ax[0] x ax[1] ax[2]
		//
		// constraining w2-w1 along an axis 'a' means that a'*(w2-w1)=0.
		// to prove the result for angle[0], write the expression for angle[0] from
		// GetInfo1 then take the derivative. to prove this for angle[2] it is
		// easier to take the euler rate expression for d(angle[2])/dt with respect
		// to the components of w and set that to 0.

		Vector3f axis0 = VECTORS.get();
		calculatedTransformB.basis.getColumn(0, axis0);

		Vector3f axis2 = VECTORS.get();
		calculatedTransformA.basis.getColumn(2, axis2);

		calculatedAxis[1].cross(axis2, axis0);
		calculatedAxis[0].cross(calculatedAxis[1], axis2);
		calculatedAxis[2].cross(axis0, calculatedAxis[1]);

		// if(m_debugDrawer)
		// {
		//
		// char buff[300];
		// sprintf(buff,"\n X: %.2f ; Y: %.2f ; Z: %.2f ",
		// m_calculatedAxisAngleDiff[0],
		// m_calculatedAxisAngleDiff[1],
		// m_calculatedAxisAngleDiff[2]);
		// m_debugDrawer->reportErrorWarning(buff);
		// }

		MATRICES.release(mat, relative_frame);
	}

	/**
	 * Calcs global transform of the offsets.
	 * <p>
	 * Calcs the global transform for the joint offset for body A an B, and also calcs the agle differences between the
	 * bodies.
	 *
	 * See also: Generic6DofConstraint.getCalculatedTransformA, Generic6DofConstraint.getCalculatedTransformB,
	 * Generic6DofConstraint.calculateAngleInfo
	 */
	public void calculateTransforms() {
		rbA.getCenterOfMassTransform(calculatedTransformA);
		calculatedTransformA.mul(frameInA);

		rbB.getCenterOfMassTransform(calculatedTransformB);
		calculatedTransformB.mul(frameInB);

		calculateAngleInfo();
	}

	/**
	 * Builds the linear jacobian.
	 *
	 * @param jacLinear_index the jac linear index
	 * @param normalWorld the normal world
	 * @param pivotAInW the pivot A in W
	 * @param pivotBInW the pivot B in W
	 */
	protected void buildLinearJacobian(/* JacobianEntry jacLinear */final int jacLinear_index,
			final Vector3f normalWorld, final Vector3f pivotAInW, final Vector3f pivotBInW) {
		Transform tmp = TRANSFORMS.get();
		Matrix3f mat1 = rbA.getCenterOfMassTransform(tmp).basis;
		mat1.transpose();

		Matrix3f mat2 = rbB.getCenterOfMassTransform(tmp).basis;
		mat2.transpose();

		Vector3f tmpVec = VECTORS.get();

		Vector3f tmp1 = VECTORS.get();
		tmp1.sub(pivotAInW, rbA.getCenterOfMassPosition(tmpVec));

		Vector3f tmp2 = VECTORS.get();
		tmp2.sub(pivotBInW, rbB.getCenterOfMassPosition(tmpVec));

		jacLinear[jacLinear_index].init(mat1, mat2, tmp1, tmp2, normalWorld, rbA.getInvInertiaDiagLocal(VECTORS.get()),
				rbA.getInvMass(), rbB.getInvInertiaDiagLocal(VECTORS.get()), rbB.getInvMass());
		TRANSFORMS.release(tmp);
		VECTORS.release(tmpVec, tmp1, tmp2);

	}

	/**
	 * Builds the angular jacobian.
	 *
	 * @param jacAngular_index the jac angular index
	 * @param jointAxisW the joint axis W
	 */
	protected void buildAngularJacobian(/* JacobianEntry jacAngular */final int jacAngular_index,
			final Vector3f jointAxisW) {
		Transform tmp = TRANSFORMS.get();
		Matrix3f mat1 = rbA.getCenterOfMassTransform(tmp).basis;
		mat1.transpose();

		Matrix3f mat2 = rbB.getCenterOfMassTransform(tmp).basis;
		mat2.transpose();

		jacAng[jacAngular_index].init(jointAxisW, mat1, mat2, rbA.getInvInertiaDiagLocal(VECTORS.get()),
				rbB.getInvInertiaDiagLocal(VECTORS.get()));
		TRANSFORMS.release(tmp);
	}

	/**
	 * Test angular limit.
	 * <p>
	 * Calculates angular correction and returns true if limit needs to be corrected.
	 * Generic6DofConstraint.buildJacobian must be called previously.
	 */
	public boolean testAngularLimitMotor(final int axis_index) {
		float angle = VectorUtil.getCoord(calculatedAxisAngleDiff, axis_index);

		// test limits
		angularLimits[axis_index].testLimitValue(angle);
		return angularLimits[axis_index].needApplyTorques();
	}

	@Override
	public void buildJacobian() {
		// Clear accumulated impulses for the next simulation step
		linearLimits.accumulatedImpulse.set(0f, 0f, 0f);
		for (int i = 0; i < 3; i++) {
			angularLimits[i].accumulatedImpulse = 0f;
		}

		// calculates transform
		calculateTransforms();

		// Vector3f tmpVec = VECTORS.get();

		// const btVector3& pivotAInW = m_calculatedTransformA.getOrigin();
		// const btVector3& pivotBInW = m_calculatedTransformB.getOrigin();
		calcAnchorPos();
		Vector3f pivotAInW = VECTORS.get(anchorPos);
		Vector3f pivotBInW = VECTORS.get(anchorPos);

		// not used here
		// btVector3 rel_pos1 = pivotAInW - m_rbA.getCenterOfMassPosition();
		// btVector3 rel_pos2 = pivotBInW - m_rbB.getCenterOfMassPosition();

		Vector3f normalWorld = VECTORS.get();
		// linear part
		for (int i = 0; i < 3; i++) {
			if (linearLimits.isLimited(i)) {
				if (useLinearReferenceFrameA) {
					calculatedTransformA.basis.getColumn(i, normalWorld);
				} else {
					calculatedTransformB.basis.getColumn(i, normalWorld);
				}

				buildLinearJacobian(/* jacLinear[i] */i, normalWorld, pivotAInW, pivotBInW);

			}
		}

		// angular part
		for (int i = 0; i < 3; i++) {
			// calculates error angle
			if (testAngularLimitMotor(i)) {
				this.getAxis(i, normalWorld);
				// Create angular atom
				buildAngularJacobian(/* jacAng[i] */i, normalWorld);
			}
		}
		VECTORS.release(pivotAInW, pivotBInW);
	}

	@Override
	public void solveConstraint(final float timeStep) {
		this.timeStep = timeStep;

		// calculateTransforms();

		int i;

		// linear

		Vector3f pointInA = VECTORS.get(calculatedTransformA.origin);
		Vector3f pointInB = VECTORS.get(calculatedTransformB.origin);

		float jacDiagABInv;
		Vector3f linear_axis = VECTORS.get();
		for (i = 0; i < 3; i++) {
			if (linearLimits.isLimited(i)) {
				jacDiagABInv = 1f / jacLinear[i].getDiagonal();

				if (useLinearReferenceFrameA) {
					calculatedTransformA.basis.getColumn(i, linear_axis);
				} else {
					calculatedTransformB.basis.getColumn(i, linear_axis);
				}

				linearLimits.solveLinearAxis(this.timeStep, jacDiagABInv, rbA, pointInA, rbB, pointInB, i, linear_axis,
						anchorPos);

			}
		}

		// angular
		Vector3f angular_axis = VECTORS.get();
		float angularJacDiagABInv;
		for (i = 0; i < 3; i++) {
			if (angularLimits[i].needApplyTorques()) {
				// get axis
				getAxis(i, angular_axis);

				angularJacDiagABInv = 1f / jacAng[i].getDiagonal();

				angularLimits[i].solveAngularLimits(this.timeStep, angular_axis, angularJacDiagABInv, rbA, rbB);
			}
		}
		VECTORS.release(pointInA, pointInB, angular_axis, linear_axis);
	}

	/**
	 * Update RHS.
	 *
	 * @param timeStep the time step
	 */
	public void updateRHS(final float timeStep) {}

	/**
	 * Get the rotation axis in global coordinates. Generic6DofConstraint.buildJacobian must be called previously.
	 */
	public Vector3f getAxis(final int axis_index, final Vector3f out) {
		out.set(calculatedAxis[axis_index]);
		return out;
	}

	/**
	 * Get the relative Euler angle. Generic6DofConstraint.buildJacobian must be called previously.
	 */
	public float getAngle(final int axis_index) {
		return VectorUtil.getCoord(calculatedAxisAngleDiff, axis_index);
	}

	/**
	 * Gets the global transform of the offset for body A.
	 * <p>
	 * See also: Generic6DofConstraint.getFrameOffsetA, Generic6DofConstraint.getFrameOffsetB,
	 * Generic6DofConstraint.calculateAngleInfo.
	 */
	public Transform getCalculatedTransformA(final Transform out) {
		out.set(calculatedTransformA);
		return out;
	}

	/**
	 * Gets the global transform of the offset for body B.
	 * <p>
	 * See also: Generic6DofConstraint.getFrameOffsetA, Generic6DofConstraint.getFrameOffsetB,
	 * Generic6DofConstraint.calculateAngleInfo.
	 */
	public Transform getCalculatedTransformB(final Transform out) {
		out.set(calculatedTransformB);
		return out;
	}

	/**
	 * Gets the frame offset A.
	 *
	 * @param out the out
	 * @return the frame offset A
	 */
	public Transform getFrameOffsetA(final Transform out) {
		out.set(frameInA);
		return out;
	}

	/**
	 * Gets the frame offset B.
	 *
	 * @param out the out
	 * @return the frame offset B
	 */
	public Transform getFrameOffsetB(final Transform out) {
		out.set(frameInB);
		return out;
	}

	/**
	 * Sets the linear lower limit.
	 *
	 * @param linearLower the new linear lower limit
	 */
	public void setLinearLowerLimit(final Vector3f linearLower) {
		linearLimits.lowerLimit.set(linearLower);
	}

	/**
	 * Sets the linear upper limit.
	 *
	 * @param linearUpper the new linear upper limit
	 */
	public void setLinearUpperLimit(final Vector3f linearUpper) {
		linearLimits.upperLimit.set(linearUpper);
	}

	/**
	 * Sets the angular lower limit.
	 *
	 * @param angularLower the new angular lower limit
	 */
	public void setAngularLowerLimit(final Vector3f angularLower) {
		angularLimits[0].loLimit = angularLower.x;
		angularLimits[1].loLimit = angularLower.y;
		angularLimits[2].loLimit = angularLower.z;
	}

	/**
	 * Sets the angular upper limit.
	 *
	 * @param angularUpper the new angular upper limit
	 */
	public void setAngularUpperLimit(final Vector3f angularUpper) {
		angularLimits[0].hiLimit = angularUpper.x;
		angularLimits[1].hiLimit = angularUpper.y;
		angularLimits[2].hiLimit = angularUpper.z;
	}

	/**
	 * Retrieves the angular limit informacion.
	 */
	public RotationalLimitMotor getRotationalLimitMotor(final int index) {
		return angularLimits[index];
	}

	/**
	 * Retrieves the limit informacion.
	 */
	public TranslationalLimitMotor getTranslationalLimitMotor() {
		return linearLimits;
	}

	/**
	 * first 3 are linear, next 3 are angular
	 */
	public void setLimit(final int axis, final float lo, final float hi) {
		if (axis < 3) {
			VectorUtil.setCoord(linearLimits.lowerLimit, axis, lo);
			VectorUtil.setCoord(linearLimits.upperLimit, axis, hi);
		} else {
			angularLimits[axis - 3].loLimit = lo;
			angularLimits[axis - 3].hiLimit = hi;
		}
	}

	/**
	 * Test limit.
	 * <p>
	 * - free means upper &lt; lower,<br>
	 * - locked means upper == lower<br>
	 * - limited means upper &gt; lower<br>
	 * - limitIndex: first 3 are linear, next 3 are angular
	 */
	public boolean isLimited(final int limitIndex) {
		if (limitIndex < 3) return linearLimits.isLimited(limitIndex);
		return angularLimits[limitIndex - 3].isLimited();
	}

	/**
	 * Calc anchor pos.
	 */
	// overridable
	public void calcAnchorPos() {
		float imA = rbA.getInvMass();
		float imB = rbB.getInvMass();
		float weight;
		if (imB == 0f) {
			weight = 1f;
		} else {
			weight = imA / (imA + imB);
		}
		Vector3f pA = calculatedTransformA.origin;
		Vector3f pB = calculatedTransformB.origin;

		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();

		tmp1.scale(weight, pA);
		tmp2.scale(1f - weight, pB);
		anchorPos.add(tmp1, tmp2);
		VECTORS.release(tmp1, tmp2);
	}

}
