/*******************************************************************************************************
 *
 * SliderConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

/*
 * Added by Roman Ponomarev (rponom@gmail.com) April 04, 2008
 *
 * TODO: - add clamping od accumulated impulse to improve stability - add conversion for ODE constraint solver
 */

package com.bulletphysics.dynamics.constraintsolver;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

// JAVA NOTE: SliderConstraint from 2.71

/**
 *
 * @author jezek2
 */
public class SliderConstraint extends TypedConstraint {

	/** The Constant SLIDER_CONSTRAINT_DEF_SOFTNESS. */
	public static final float SLIDER_CONSTRAINT_DEF_SOFTNESS = 1.0f;
	
	/** The Constant SLIDER_CONSTRAINT_DEF_DAMPING. */
	public static final float SLIDER_CONSTRAINT_DEF_DAMPING = 1.0f;
	
	/** The Constant SLIDER_CONSTRAINT_DEF_RESTITUTION. */
	public static final float SLIDER_CONSTRAINT_DEF_RESTITUTION = 0.7f;

	/** The frame in A. */
	protected final Transform frameInA = new Transform();
	
	/** The frame in B. */
	protected final Transform frameInB = new Transform();
	
	/** The use linear reference frame A. */
	// use frameA fo define limits, if true
	protected boolean useLinearReferenceFrameA;
	
	/** The lower lin limit. */
	// linear limits
	protected float lowerLinLimit;
	
	/** The upper lin limit. */
	protected float upperLinLimit;
	
	/** The lower ang limit. */
	// angular limits
	protected float lowerAngLimit;
	
	/** The upper ang limit. */
	protected float upperAngLimit;
	// softness, restitution and damping for different cases
	// DirLin - moving inside linear limits
	// LimLin - hitting linear limit
	// DirAng - moving inside angular limits
	// LimAng - hitting angular limit
	/** The softness dir lin. */
	// OrthoLin, OrthoAng - against constraint axis
	protected float softnessDirLin;
	
	/** The restitution dir lin. */
	protected float restitutionDirLin;
	
	/** The damping dir lin. */
	protected float dampingDirLin;
	
	/** The softness dir ang. */
	protected float softnessDirAng;
	
	/** The restitution dir ang. */
	protected float restitutionDirAng;
	
	/** The damping dir ang. */
	protected float dampingDirAng;
	
	/** The softness lim lin. */
	protected float softnessLimLin;
	
	/** The restitution lim lin. */
	protected float restitutionLimLin;
	
	/** The damping lim lin. */
	protected float dampingLimLin;
	
	/** The softness lim ang. */
	protected float softnessLimAng;
	
	/** The restitution lim ang. */
	protected float restitutionLimAng;
	
	/** The damping lim ang. */
	protected float dampingLimAng;
	
	/** The softness ortho lin. */
	protected float softnessOrthoLin;
	
	/** The restitution ortho lin. */
	protected float restitutionOrthoLin;
	
	/** The damping ortho lin. */
	protected float dampingOrthoLin;
	
	/** The softness ortho ang. */
	protected float softnessOrthoAng;
	
	/** The restitution ortho ang. */
	protected float restitutionOrthoAng;
	
	/** The damping ortho ang. */
	protected float dampingOrthoAng;

	/** The solve lin lim. */
	// for interlal use
	protected boolean solveLinLim;
	
	/** The solve ang lim. */
	protected boolean solveAngLim;

	/** The jac lin. */
	protected JacobianEntry[] jacLin =
			new JacobianEntry[/* 3 */] { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() };
	
	/** The jac lin diag AB inv. */
	protected float[] jacLinDiagABInv = new float[3];

	/** The jac ang. */
	protected JacobianEntry[] jacAng =
			new JacobianEntry[/* 3 */] { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() };

	/** The time step. */
	protected float timeStep;
	
	/** The calculated transform A. */
	protected final Transform calculatedTransformA = new Transform();
	
	/** The calculated transform B. */
	protected final Transform calculatedTransformB = new Transform();

	/** The slider axis. */
	protected final Vector3f sliderAxis = new Vector3f();
	
	/** The real pivot A in W. */
	protected final Vector3f realPivotAInW = new Vector3f();
	
	/** The real pivot B in W. */
	protected final Vector3f realPivotBInW = new Vector3f();
	
	/** The proj pivot in W. */
	protected final Vector3f projPivotInW = new Vector3f();
	
	/** The delta. */
	protected final Vector3f delta = new Vector3f();
	
	/** The depth. */
	protected final Vector3f depth = new Vector3f();
	
	/** The rel pos A. */
	protected final Vector3f relPosA = new Vector3f();
	
	/** The rel pos B. */
	protected final Vector3f relPosB = new Vector3f();

	/** The lin pos. */
	protected float linPos;

	/** The ang depth. */
	protected float angDepth;
	
	/** The k angle. */
	protected float kAngle;

	/** The powered lin motor. */
	protected boolean poweredLinMotor;
	
	/** The target lin motor velocity. */
	protected float targetLinMotorVelocity;
	
	/** The max lin motor force. */
	protected float maxLinMotorForce;
	
	/** The accumulated lin motor impulse. */
	protected float accumulatedLinMotorImpulse;

	/** The powered ang motor. */
	protected boolean poweredAngMotor;
	
	/** The target ang motor velocity. */
	protected float targetAngMotorVelocity;
	
	/** The max ang motor force. */
	protected float maxAngMotorForce;
	
	/** The accumulated ang motor impulse. */
	protected float accumulatedAngMotorImpulse;

	/**
	 * Instantiates a new slider constraint.
	 */
	public SliderConstraint() {
		super(TypedConstraintType.SLIDER_CONSTRAINT_TYPE);
		useLinearReferenceFrameA = true;
		initParams();
	}

	/**
	 * Instantiates a new slider constraint.
	 *
	 * @param rbA the rb A
	 * @param rbB the rb B
	 * @param frameInA the frame in A
	 * @param frameInB the frame in B
	 * @param useLinearReferenceFrameA the use linear reference frame A
	 */
	public SliderConstraint(final RigidBody rbA, final RigidBody rbB, final Transform frameInA,
			final Transform frameInB, final boolean useLinearReferenceFrameA) {
		super(TypedConstraintType.SLIDER_CONSTRAINT_TYPE, rbA, rbB);
		this.frameInA.set(frameInA);
		this.frameInB.set(frameInB);
		this.useLinearReferenceFrameA = useLinearReferenceFrameA;
		initParams();
	}

	/**
	 * Inits the params.
	 */
	protected void initParams() {
		lowerLinLimit = 1f;
		upperLinLimit = -1f;
		lowerAngLimit = 0f;
		upperAngLimit = 0f;
		softnessDirLin = SLIDER_CONSTRAINT_DEF_SOFTNESS;
		restitutionDirLin = SLIDER_CONSTRAINT_DEF_RESTITUTION;
		dampingDirLin = 0f;
		softnessDirAng = SLIDER_CONSTRAINT_DEF_SOFTNESS;
		restitutionDirAng = SLIDER_CONSTRAINT_DEF_RESTITUTION;
		dampingDirAng = 0f;
		softnessOrthoLin = SLIDER_CONSTRAINT_DEF_SOFTNESS;
		restitutionOrthoLin = SLIDER_CONSTRAINT_DEF_RESTITUTION;
		dampingOrthoLin = SLIDER_CONSTRAINT_DEF_DAMPING;
		softnessOrthoAng = SLIDER_CONSTRAINT_DEF_SOFTNESS;
		restitutionOrthoAng = SLIDER_CONSTRAINT_DEF_RESTITUTION;
		dampingOrthoAng = SLIDER_CONSTRAINT_DEF_DAMPING;
		softnessLimLin = SLIDER_CONSTRAINT_DEF_SOFTNESS;
		restitutionLimLin = SLIDER_CONSTRAINT_DEF_RESTITUTION;
		dampingLimLin = SLIDER_CONSTRAINT_DEF_DAMPING;
		softnessLimAng = SLIDER_CONSTRAINT_DEF_SOFTNESS;
		restitutionLimAng = SLIDER_CONSTRAINT_DEF_RESTITUTION;
		dampingLimAng = SLIDER_CONSTRAINT_DEF_DAMPING;

		poweredLinMotor = false;
		targetLinMotorVelocity = 0f;
		maxLinMotorForce = 0f;
		accumulatedLinMotorImpulse = 0f;

		poweredAngMotor = false;
		targetAngMotorVelocity = 0f;
		maxAngMotorForce = 0f;
		accumulatedAngMotorImpulse = 0f;
	}

	@Override
	public void buildJacobian() {
		if (useLinearReferenceFrameA) {
			buildJacobianInt(rbA, rbB, frameInA, frameInB);
		} else {
			buildJacobianInt(rbB, rbA, frameInB, frameInA);
		}
	}

	@Override
	public void solveConstraint(final float timeStep) {
		this.timeStep = timeStep;
		if (useLinearReferenceFrameA) {
			solveConstraintInt(rbA, rbB);
		} else {
			solveConstraintInt(rbB, rbA);
		}
	}

	/**
	 * Gets the calculated transform A.
	 *
	 * @param out the out
	 * @return the calculated transform A
	 */
	public Transform getCalculatedTransformA(final Transform out) {
		out.set(calculatedTransformA);
		return out;
	}

	/**
	 * Gets the calculated transform B.
	 *
	 * @param out the out
	 * @return the calculated transform B
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
	 * Gets the lower lin limit.
	 *
	 * @return the lower lin limit
	 */
	public float getLowerLinLimit() {
		return lowerLinLimit;
	}

	/**
	 * Sets the lower lin limit.
	 *
	 * @param lowerLimit the new lower lin limit
	 */
	public void setLowerLinLimit(final float lowerLimit) {
		this.lowerLinLimit = lowerLimit;
	}

	/**
	 * Gets the upper lin limit.
	 *
	 * @return the upper lin limit
	 */
	public float getUpperLinLimit() {
		return upperLinLimit;
	}

	/**
	 * Sets the upper lin limit.
	 *
	 * @param upperLimit the new upper lin limit
	 */
	public void setUpperLinLimit(final float upperLimit) {
		this.upperLinLimit = upperLimit;
	}

	/**
	 * Gets the lower ang limit.
	 *
	 * @return the lower ang limit
	 */
	public float getLowerAngLimit() {
		return lowerAngLimit;
	}

	/**
	 * Sets the lower ang limit.
	 *
	 * @param lowerLimit the new lower ang limit
	 */
	public void setLowerAngLimit(final float lowerLimit) {
		this.lowerAngLimit = lowerLimit;
	}

	/**
	 * Gets the upper ang limit.
	 *
	 * @return the upper ang limit
	 */
	public float getUpperAngLimit() {
		return upperAngLimit;
	}

	/**
	 * Sets the upper ang limit.
	 *
	 * @param upperLimit the new upper ang limit
	 */
	public void setUpperAngLimit(final float upperLimit) {
		this.upperAngLimit = upperLimit;
	}

	/**
	 * Gets the use linear reference frame A.
	 *
	 * @return the use linear reference frame A
	 */
	public boolean getUseLinearReferenceFrameA() {
		return useLinearReferenceFrameA;
	}

	/**
	 * Gets the softness dir lin.
	 *
	 * @return the softness dir lin
	 */
	public float getSoftnessDirLin() {
		return softnessDirLin;
	}

	/**
	 * Gets the restitution dir lin.
	 *
	 * @return the restitution dir lin
	 */
	public float getRestitutionDirLin() {
		return restitutionDirLin;
	}

	/**
	 * Gets the damping dir lin.
	 *
	 * @return the damping dir lin
	 */
	public float getDampingDirLin() {
		return dampingDirLin;
	}

	/**
	 * Gets the softness dir ang.
	 *
	 * @return the softness dir ang
	 */
	public float getSoftnessDirAng() {
		return softnessDirAng;
	}

	/**
	 * Gets the restitution dir ang.
	 *
	 * @return the restitution dir ang
	 */
	public float getRestitutionDirAng() {
		return restitutionDirAng;
	}

	/**
	 * Gets the damping dir ang.
	 *
	 * @return the damping dir ang
	 */
	public float getDampingDirAng() {
		return dampingDirAng;
	}

	/**
	 * Gets the softness lim lin.
	 *
	 * @return the softness lim lin
	 */
	public float getSoftnessLimLin() {
		return softnessLimLin;
	}

	/**
	 * Gets the restitution lim lin.
	 *
	 * @return the restitution lim lin
	 */
	public float getRestitutionLimLin() {
		return restitutionLimLin;
	}

	/**
	 * Gets the damping lim lin.
	 *
	 * @return the damping lim lin
	 */
	public float getDampingLimLin() {
		return dampingLimLin;
	}

	/**
	 * Gets the softness lim ang.
	 *
	 * @return the softness lim ang
	 */
	public float getSoftnessLimAng() {
		return softnessLimAng;
	}

	/**
	 * Gets the restitution lim ang.
	 *
	 * @return the restitution lim ang
	 */
	public float getRestitutionLimAng() {
		return restitutionLimAng;
	}

	/**
	 * Gets the damping lim ang.
	 *
	 * @return the damping lim ang
	 */
	public float getDampingLimAng() {
		return dampingLimAng;
	}

	/**
	 * Gets the softness ortho lin.
	 *
	 * @return the softness ortho lin
	 */
	public float getSoftnessOrthoLin() {
		return softnessOrthoLin;
	}

	/**
	 * Gets the restitution ortho lin.
	 *
	 * @return the restitution ortho lin
	 */
	public float getRestitutionOrthoLin() {
		return restitutionOrthoLin;
	}

	/**
	 * Gets the damping ortho lin.
	 *
	 * @return the damping ortho lin
	 */
	public float getDampingOrthoLin() {
		return dampingOrthoLin;
	}

	/**
	 * Gets the softness ortho ang.
	 *
	 * @return the softness ortho ang
	 */
	public float getSoftnessOrthoAng() {
		return softnessOrthoAng;
	}

	/**
	 * Gets the restitution ortho ang.
	 *
	 * @return the restitution ortho ang
	 */
	public float getRestitutionOrthoAng() {
		return restitutionOrthoAng;
	}

	/**
	 * Gets the damping ortho ang.
	 *
	 * @return the damping ortho ang
	 */
	public float getDampingOrthoAng() {
		return dampingOrthoAng;
	}

	/**
	 * Sets the softness dir lin.
	 *
	 * @param softnessDirLin the new softness dir lin
	 */
	public void setSoftnessDirLin(final float softnessDirLin) {
		this.softnessDirLin = softnessDirLin;
	}

	/**
	 * Sets the restitution dir lin.
	 *
	 * @param restitutionDirLin the new restitution dir lin
	 */
	public void setRestitutionDirLin(final float restitutionDirLin) {
		this.restitutionDirLin = restitutionDirLin;
	}

	/**
	 * Sets the damping dir lin.
	 *
	 * @param dampingDirLin the new damping dir lin
	 */
	public void setDampingDirLin(final float dampingDirLin) {
		this.dampingDirLin = dampingDirLin;
	}

	/**
	 * Sets the softness dir ang.
	 *
	 * @param softnessDirAng the new softness dir ang
	 */
	public void setSoftnessDirAng(final float softnessDirAng) {
		this.softnessDirAng = softnessDirAng;
	}

	/**
	 * Sets the restitution dir ang.
	 *
	 * @param restitutionDirAng the new restitution dir ang
	 */
	public void setRestitutionDirAng(final float restitutionDirAng) {
		this.restitutionDirAng = restitutionDirAng;
	}

	/**
	 * Sets the damping dir ang.
	 *
	 * @param dampingDirAng the new damping dir ang
	 */
	public void setDampingDirAng(final float dampingDirAng) {
		this.dampingDirAng = dampingDirAng;
	}

	/**
	 * Sets the softness lim lin.
	 *
	 * @param softnessLimLin the new softness lim lin
	 */
	public void setSoftnessLimLin(final float softnessLimLin) {
		this.softnessLimLin = softnessLimLin;
	}

	/**
	 * Sets the restitution lim lin.
	 *
	 * @param restitutionLimLin the new restitution lim lin
	 */
	public void setRestitutionLimLin(final float restitutionLimLin) {
		this.restitutionLimLin = restitutionLimLin;
	}

	/**
	 * Sets the damping lim lin.
	 *
	 * @param dampingLimLin the new damping lim lin
	 */
	public void setDampingLimLin(final float dampingLimLin) {
		this.dampingLimLin = dampingLimLin;
	}

	/**
	 * Sets the softness lim ang.
	 *
	 * @param softnessLimAng the new softness lim ang
	 */
	public void setSoftnessLimAng(final float softnessLimAng) {
		this.softnessLimAng = softnessLimAng;
	}

	/**
	 * Sets the restitution lim ang.
	 *
	 * @param restitutionLimAng the new restitution lim ang
	 */
	public void setRestitutionLimAng(final float restitutionLimAng) {
		this.restitutionLimAng = restitutionLimAng;
	}

	/**
	 * Sets the damping lim ang.
	 *
	 * @param dampingLimAng the new damping lim ang
	 */
	public void setDampingLimAng(final float dampingLimAng) {
		this.dampingLimAng = dampingLimAng;
	}

	/**
	 * Sets the softness ortho lin.
	 *
	 * @param softnessOrthoLin the new softness ortho lin
	 */
	public void setSoftnessOrthoLin(final float softnessOrthoLin) {
		this.softnessOrthoLin = softnessOrthoLin;
	}

	/**
	 * Sets the restitution ortho lin.
	 *
	 * @param restitutionOrthoLin the new restitution ortho lin
	 */
	public void setRestitutionOrthoLin(final float restitutionOrthoLin) {
		this.restitutionOrthoLin = restitutionOrthoLin;
	}

	/**
	 * Sets the damping ortho lin.
	 *
	 * @param dampingOrthoLin the new damping ortho lin
	 */
	public void setDampingOrthoLin(final float dampingOrthoLin) {
		this.dampingOrthoLin = dampingOrthoLin;
	}

	/**
	 * Sets the softness ortho ang.
	 *
	 * @param softnessOrthoAng the new softness ortho ang
	 */
	public void setSoftnessOrthoAng(final float softnessOrthoAng) {
		this.softnessOrthoAng = softnessOrthoAng;
	}

	/**
	 * Sets the restitution ortho ang.
	 *
	 * @param restitutionOrthoAng the new restitution ortho ang
	 */
	public void setRestitutionOrthoAng(final float restitutionOrthoAng) {
		this.restitutionOrthoAng = restitutionOrthoAng;
	}

	/**
	 * Sets the damping ortho ang.
	 *
	 * @param dampingOrthoAng the new damping ortho ang
	 */
	public void setDampingOrthoAng(final float dampingOrthoAng) {
		this.dampingOrthoAng = dampingOrthoAng;
	}

	/**
	 * Sets the powered lin motor.
	 *
	 * @param onOff the new powered lin motor
	 */
	public void setPoweredLinMotor(final boolean onOff) {
		this.poweredLinMotor = onOff;
	}

	/**
	 * Gets the powered lin motor.
	 *
	 * @return the powered lin motor
	 */
	public boolean getPoweredLinMotor() {
		return poweredLinMotor;
	}

	/**
	 * Sets the target lin motor velocity.
	 *
	 * @param targetLinMotorVelocity the new target lin motor velocity
	 */
	public void setTargetLinMotorVelocity(final float targetLinMotorVelocity) {
		this.targetLinMotorVelocity = targetLinMotorVelocity;
	}

	/**
	 * Gets the target lin motor velocity.
	 *
	 * @return the target lin motor velocity
	 */
	public float getTargetLinMotorVelocity() {
		return targetLinMotorVelocity;
	}

	/**
	 * Sets the max lin motor force.
	 *
	 * @param maxLinMotorForce the new max lin motor force
	 */
	public void setMaxLinMotorForce(final float maxLinMotorForce) {
		this.maxLinMotorForce = maxLinMotorForce;
	}

	/**
	 * Gets the max lin motor force.
	 *
	 * @return the max lin motor force
	 */
	public float getMaxLinMotorForce() {
		return maxLinMotorForce;
	}

	/**
	 * Sets the powered ang motor.
	 *
	 * @param onOff the new powered ang motor
	 */
	public void setPoweredAngMotor(final boolean onOff) {
		this.poweredAngMotor = onOff;
	}

	/**
	 * Gets the powered ang motor.
	 *
	 * @return the powered ang motor
	 */
	public boolean getPoweredAngMotor() {
		return poweredAngMotor;
	}

	/**
	 * Sets the target ang motor velocity.
	 *
	 * @param targetAngMotorVelocity the new target ang motor velocity
	 */
	public void setTargetAngMotorVelocity(final float targetAngMotorVelocity) {
		this.targetAngMotorVelocity = targetAngMotorVelocity;
	}

	/**
	 * Gets the target ang motor velocity.
	 *
	 * @return the target ang motor velocity
	 */
	public float getTargetAngMotorVelocity() {
		return targetAngMotorVelocity;
	}

	/**
	 * Sets the max ang motor force.
	 *
	 * @param maxAngMotorForce the new max ang motor force
	 */
	public void setMaxAngMotorForce(final float maxAngMotorForce) {
		this.maxAngMotorForce = maxAngMotorForce;
	}

	/**
	 * Gets the max ang motor force.
	 *
	 * @return the max ang motor force
	 */
	public float getMaxAngMotorForce() {
		return this.maxAngMotorForce;
	}

	/**
	 * Gets the linear pos.
	 *
	 * @return the linear pos
	 */
	public float getLinearPos() {
		return this.linPos;
	}

	// access for ODE solver

	/**
	 * Gets the solve lin limit.
	 *
	 * @return the solve lin limit
	 */
	public boolean getSolveLinLimit() {
		return solveLinLim;
	}

	/**
	 * Gets the lin depth.
	 *
	 * @return the lin depth
	 */
	public float getLinDepth() {
		return depth.x;
	}

	/**
	 * Gets the solve ang limit.
	 *
	 * @return the solve ang limit
	 */
	public boolean getSolveAngLimit() {
		return solveAngLim;
	}

	/**
	 * Gets the ang depth.
	 *
	 * @return the ang depth
	 */
	public float getAngDepth() {
		return angDepth;
	}

	// internal

	/**
	 * Builds the jacobian int.
	 *
	 * @param rbA the rb A
	 * @param rbB the rb B
	 * @param frameInA the frame in A
	 * @param frameInB the frame in B
	 */
	public void buildJacobianInt(final RigidBody rbA, final RigidBody rbB, final Transform frameInA,
			final Transform frameInB) {
		Transform tmpTrans = TRANSFORMS.get();
		Transform tmpTrans1 = TRANSFORMS.get();
		Transform tmpTrans2 = TRANSFORMS.get();
		Vector3f tmp = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();
		try {

			// calculate transforms
			calculatedTransformA.mul(rbA.getCenterOfMassTransform(tmpTrans), frameInA);
			calculatedTransformB.mul(rbB.getCenterOfMassTransform(tmpTrans), frameInB);
			realPivotAInW.set(calculatedTransformA.origin);
			realPivotBInW.set(calculatedTransformB.origin);
			calculatedTransformA.basis.getColumn(0, tmp);
			sliderAxis.set(tmp); // along X
			delta.sub(realPivotBInW, realPivotAInW);
			projPivotInW.scaleAdd(sliderAxis.dot(delta), sliderAxis, realPivotAInW);
			relPosA.sub(projPivotInW, rbA.getCenterOfMassPosition(tmp));
			relPosB.sub(realPivotBInW, rbB.getCenterOfMassPosition(tmp));
			Vector3f normalWorld = VECTORS.get();

			// linear part
			for (int i = 0; i < 3; i++) {
				calculatedTransformA.basis.getColumn(i, normalWorld);

				Matrix3f mat1 = rbA.getCenterOfMassTransform(tmpTrans1).basis;
				mat1.transpose();

				Matrix3f mat2 = rbB.getCenterOfMassTransform(tmpTrans2).basis;
				mat2.transpose();

				jacLin[i].init(mat1, mat2, relPosA, relPosB, normalWorld, rbA.getInvInertiaDiagLocal(tmp),
						rbA.getInvMass(), rbB.getInvInertiaDiagLocal(tmp2), rbB.getInvMass());
				jacLinDiagABInv[i] = 1f / jacLin[i].getDiagonal();
				VectorUtil.setCoord(depth, i, delta.dot(normalWorld));
			}
			testLinLimits();

			// angular part
			for (int i = 0; i < 3; i++) {
				calculatedTransformA.basis.getColumn(i, normalWorld);

				Matrix3f mat1 = rbA.getCenterOfMassTransform(tmpTrans1).basis;
				mat1.transpose();

				Matrix3f mat2 = rbB.getCenterOfMassTransform(tmpTrans2).basis;
				mat2.transpose();

				jacAng[i].init(normalWorld, mat1, mat2, rbA.getInvInertiaDiagLocal(tmp),
						rbB.getInvInertiaDiagLocal(tmp2));
			}
			testAngLimits();

			Vector3f axisA = VECTORS.get();
			calculatedTransformA.basis.getColumn(0, axisA);
			kAngle = 1f / (rbA.computeAngularImpulseDenominator(axisA) + rbB.computeAngularImpulseDenominator(axisA));
			// clear accumulator for motors
			accumulatedLinMotorImpulse = 0f;
			accumulatedAngMotorImpulse = 0f;
			VECTORS.release(axisA, normalWorld);
		} finally {
			TRANSFORMS.release(tmpTrans, tmpTrans1, tmpTrans2);
			VECTORS.release(tmp, tmp2);
		}
	}

	/**
	 * Solve constraint int.
	 *
	 * @param rbA the rb A
	 * @param rbB the rb B
	 */
	public void solveConstraintInt(final RigidBody rbA, final RigidBody rbB) {
		Vector3f tmp = VECTORS.get();

		// linear
		Vector3f velA = rbA.getVelocityInLocalPoint(relPosA, VECTORS.get());
		Vector3f velB = rbB.getVelocityInLocalPoint(relPosB, VECTORS.get());
		Vector3f vel = VECTORS.get();
		vel.sub(velA, velB);
		VECTORS.release(velA, velB);

		Vector3f impulse_vector = VECTORS.get();

		for (int i = 0; i < 3; i++) {
			Vector3f normal = jacLin[i].linearJointAxis;
			float rel_vel = normal.dot(vel);
			// calculate positional error
			float depth = VectorUtil.getCoord(this.depth, i);
			// get parameters
			float softness = i != 0 ? softnessOrthoLin : solveLinLim ? softnessLimLin : softnessDirLin;
			float restitution = i != 0 ? restitutionOrthoLin : solveLinLim ? restitutionLimLin : restitutionDirLin;
			float damping = i != 0 ? dampingOrthoLin : solveLinLim ? dampingLimLin : dampingDirLin;
			// calcutate and apply impulse
			float normalImpulse = softness * (restitution * depth / timeStep - damping * rel_vel) * jacLinDiagABInv[i];
			impulse_vector.scale(normalImpulse, normal);
			rbA.applyImpulse(impulse_vector, relPosA);
			tmp.negate(impulse_vector);
			rbB.applyImpulse(tmp, relPosB);

			if (poweredLinMotor && i == 0) {
				// apply linear motor
				if (accumulatedLinMotorImpulse < maxLinMotorForce) {
					float desiredMotorVel = targetLinMotorVelocity;
					float motor_relvel = desiredMotorVel + rel_vel;
					normalImpulse = -motor_relvel * jacLinDiagABInv[i];
					// clamp accumulated impulse
					float new_acc = accumulatedLinMotorImpulse + Math.abs(normalImpulse);
					if (new_acc > maxLinMotorForce) { new_acc = maxLinMotorForce; }
					float del = new_acc - accumulatedLinMotorImpulse;
					if (normalImpulse < 0f) {
						normalImpulse = -del;
					} else {
						normalImpulse = del;
					}
					accumulatedLinMotorImpulse = new_acc;
					// apply clamped impulse
					impulse_vector.scale(normalImpulse, normal);
					rbA.applyImpulse(impulse_vector, relPosA);
					tmp.negate(impulse_vector);
					rbB.applyImpulse(tmp, relPosB);
				}
			}
		}
		VECTORS.release(vel);

		// angular
		// get axes in world space
		Vector3f axisA = VECTORS.get();
		calculatedTransformA.basis.getColumn(0, axisA);
		Vector3f axisB = VECTORS.get();
		calculatedTransformB.basis.getColumn(0, axisB);

		Vector3f angVelA = rbA.getAngularVelocity(VECTORS.get());
		Vector3f angVelB = rbB.getAngularVelocity(VECTORS.get());

		Vector3f angVelAroundAxisA = VECTORS.get();
		angVelAroundAxisA.scale(axisA.dot(angVelA), axisA);
		Vector3f angVelAroundAxisB = VECTORS.get();
		angVelAroundAxisB.scale(axisB.dot(angVelB), axisB);

		Vector3f angAorthog = VECTORS.get();
		angAorthog.sub(angVelA, angVelAroundAxisA);
		Vector3f angBorthog = VECTORS.get();
		angBorthog.sub(angVelB, angVelAroundAxisB);
		Vector3f velrelOrthog = VECTORS.get();
		velrelOrthog.sub(angAorthog, angBorthog);

		// solve orthogonal angular velocity correction
		float len = velrelOrthog.length();
		if (len > 0.00001f) {
			Vector3f normal = VECTORS.get();
			normal.normalize(velrelOrthog);
			float denom = rbA.computeAngularImpulseDenominator(normal) + rbB.computeAngularImpulseDenominator(normal);
			velrelOrthog.scale(1f / denom * dampingOrthoAng * softnessOrthoAng);
			VECTORS.release(normal);
		}

		// solve angular positional correction
		Vector3f angularError = VECTORS.get();
		angularError.cross(axisA, axisB);
		angularError.scale(1f / timeStep);
		float len2 = angularError.length();
		if (len2 > 0.00001f) {
			Vector3f normal2 = VECTORS.get();
			normal2.normalize(angularError);
			float denom2 =
					rbA.computeAngularImpulseDenominator(normal2) + rbB.computeAngularImpulseDenominator(normal2);
			angularError.scale(1f / denom2 * restitutionOrthoAng * softnessOrthoAng);
			VECTORS.release(normal2);
		}

		// apply impulse
		tmp.negate(velrelOrthog);
		tmp.add(angularError);
		rbA.applyTorqueImpulse(tmp);
		tmp.sub(velrelOrthog, angularError);
		rbB.applyTorqueImpulse(tmp);
		float impulseMag;

		// solve angular limits
		if (solveAngLim) {
			tmp.sub(angVelB, angVelA);
			impulseMag = tmp.dot(axisA) * dampingLimAng + angDepth * restitutionLimAng / timeStep;
			impulseMag *= kAngle * softnessLimAng;
		} else {
			tmp.sub(angVelB, angVelA);
			impulseMag = tmp.dot(axisA) * dampingDirAng + angDepth * restitutionDirAng / timeStep;
			impulseMag *= kAngle * softnessDirAng;
		}
		Vector3f impulse = VECTORS.get();
		impulse.scale(impulseMag, axisA);
		rbA.applyTorqueImpulse(impulse);
		tmp.negate(impulse);
		rbB.applyTorqueImpulse(tmp);

		// apply angular motor
		if (poweredAngMotor) {
			if (accumulatedAngMotorImpulse < maxAngMotorForce) {
				Vector3f velrel = VECTORS.get();
				velrel.sub(angVelAroundAxisA, angVelAroundAxisB);
				float projRelVel = velrel.dot(axisA);

				float desiredMotorVel = targetAngMotorVelocity;
				float motor_relvel = desiredMotorVel - projRelVel;

				float angImpulse = kAngle * motor_relvel;
				// clamp accumulated impulse
				float new_acc = accumulatedAngMotorImpulse + Math.abs(angImpulse);
				if (new_acc > maxAngMotorForce) { new_acc = maxAngMotorForce; }
				float del = new_acc - accumulatedAngMotorImpulse;
				if (angImpulse < 0f) {
					angImpulse = -del;
				} else {
					angImpulse = del;
				}
				accumulatedAngMotorImpulse = new_acc;

				// apply clamped impulse
				Vector3f motorImp = VECTORS.get();
				motorImp.scale(angImpulse, axisA);
				rbA.applyTorqueImpulse(motorImp);
				tmp.negate(motorImp);
				rbB.applyTorqueImpulse(tmp);
			}
		}
		VECTORS.release(tmp, impulse, angularError, angVelA, angVelB, angVelAroundAxisA, angVelAroundAxisB, angAorthog,
				angBorthog, velrelOrthog, axisA, axisB);
	}

	// shared code used by ODE solver

	/**
	 * Calculate transforms.
	 */
	public void calculateTransforms() {
		Transform tmpTrans = TRANSFORMS.get();

		if (useLinearReferenceFrameA) {
			calculatedTransformA.mul(rbA.getCenterOfMassTransform(tmpTrans), frameInA);
			calculatedTransformB.mul(rbB.getCenterOfMassTransform(tmpTrans), frameInB);
		} else {
			calculatedTransformA.mul(rbB.getCenterOfMassTransform(tmpTrans), frameInB);
			calculatedTransformB.mul(rbA.getCenterOfMassTransform(tmpTrans), frameInA);
		}
		realPivotAInW.set(calculatedTransformA.origin);
		realPivotBInW.set(calculatedTransformB.origin);
		calculatedTransformA.basis.getColumn(0, sliderAxis); // along X
		delta.sub(realPivotBInW, realPivotAInW);
		projPivotInW.scaleAdd(sliderAxis.dot(delta), sliderAxis, realPivotAInW);
		Vector3f normalWorld = VECTORS.get();
		// linear part
		for (int i = 0; i < 3; i++) {
			calculatedTransformA.basis.getColumn(i, normalWorld);
			VectorUtil.setCoord(depth, i, delta.dot(normalWorld));
		}
		VECTORS.release(normalWorld);
		TRANSFORMS.release(tmpTrans);
	}

	/**
	 * Test lin limits.
	 */
	public void testLinLimits() {
		solveLinLim = false;
		linPos = depth.x;
		if (lowerLinLimit <= upperLinLimit) {
			if (depth.x > upperLinLimit) {
				depth.x -= upperLinLimit;
				solveLinLim = true;
			} else if (depth.x < lowerLinLimit) {
				depth.x -= lowerLinLimit;
				solveLinLim = true;
			} else {
				depth.x = 0f;
			}
		} else {
			depth.x = 0f;
		}
	}

	/**
	 * Test ang limits.
	 */
	public void testAngLimits() {
		angDepth = 0f;
		solveAngLim = false;
		if (lowerAngLimit <= upperAngLimit) {
			Vector3f axisA0 = VECTORS.get();
			calculatedTransformA.basis.getColumn(1, axisA0);
			Vector3f axisA1 = VECTORS.get();
			calculatedTransformA.basis.getColumn(2, axisA1);
			Vector3f axisB0 = VECTORS.get();
			calculatedTransformB.basis.getColumn(1, axisB0);

			float rot = (float) Math.atan2(axisB0.dot(axisA1), axisB0.dot(axisA0));
			if (rot < lowerAngLimit) {
				angDepth = rot - lowerAngLimit;
				solveAngLim = true;
			} else if (rot > upperAngLimit) {
				angDepth = rot - upperAngLimit;
				solveAngLim = true;
			}
			VECTORS.release(axisA0, axisA1, axisB0);
		}
	}

	// access for PE Solver

	/**
	 * Gets the ancor in A.
	 *
	 * @param out the out
	 * @return the ancor in A
	 */
	public Vector3f getAncorInA(final Vector3f out) {
		Transform tmpTrans = TRANSFORMS.get();

		Vector3f ancorInA = out;
		ancorInA.scaleAdd((lowerLinLimit + upperLinLimit) * 0.5f, sliderAxis, realPivotAInW);
		rbA.getCenterOfMassTransform(tmpTrans);
		tmpTrans.inverse();
		tmpTrans.transform(ancorInA);
		TRANSFORMS.release(tmpTrans);
		return ancorInA;
	}

	/**
	 * Gets the ancor in B.
	 *
	 * @param out the out
	 * @return the ancor in B
	 */
	public Vector3f getAncorInB(final Vector3f out) {
		Vector3f ancorInB = out;
		ancorInB.set(frameInB.origin);
		return ancorInB;
	}

}
