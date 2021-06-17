/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bulletphysics.demos.constraint;

import com.bulletphysics.linearmath.VectorUtil;
import javax.vecmath.Matrix3f;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.ConeTwistConstraint;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofSpringConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import org.lwjgl.LWJGLException;
import static com.bulletphysics.demos.opengl.IGL.*;

/**
 *
 * @author gideonk
 */
public class ConstraintDemo extends DemoApplication {

    // #define constants from bullet
    final static boolean ENABLE_ALL_DEMOS = true;
    final static boolean P2P = true;
    final static float CUBE_HALF_EXTENTS = 1.f;
    final static float SIMD_PI = (float) Math.PI;
    final static float SIMD_PI_2 = (float) ((Math.PI) * 0.5f);
    final static float SIMD_PI_4 = (float) ((Math.PI) * 0.25f);
    // @PORT_ISSUE these are from btScalar.h
    final static float SIMD_2_PI = 6.283185307179586232f;
    final static float SIMD_HALF_PI = SIMD_2_PI * 0.25f;
    final static float SIMD_RADS_PER_DEG    = (SIMD_2_PI / 360.0f);
    final static float SIMD_DEGS_PER_RAD    = (360.0f / SIMD_2_PI);

    // global variables
    Transform sliderTransform;
    Vector3f lowerSliderLimit = new Vector3f(-10, 0, 0);
    Vector3f hiSliderLimit = new Vector3f(10, 0, 0);
    RigidBody d6body0 = null;
    HingeConstraint spDoorHinge = null;
    HingeConstraint spHingeDynAB = null;
    Generic6DofConstraint spSlider6Dof = null;
    static boolean s_bTestConeTwistMotor = false;
    //keep track of variables to delete memory at the end
    private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
    private BroadphaseInterface overlappingPairCache;
    private CollisionDispatcher dispatcher;
    private ConstraintSolver constraintSolver;
    private DefaultCollisionConfiguration collisionConfiguration;
    // for cone-twist motor driving
    public float time;
    public ConeTwistConstraint ctc;

    void setupEmptyDynamicsWorld() {
        collisionConfiguration = new DefaultCollisionConfiguration();
        dispatcher = new CollisionDispatcher(collisionConfiguration);
        overlappingPairCache = new DbvtBroadphase();
        constraintSolver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver, collisionConfiguration);

    }

    public void initPhysics() {

        setCameraDistance(26.f);
        time = 0;

        setupEmptyDynamicsWorld();

        //CollisionShape* groundShape = new btBoxShape(Vector3f(btScalar(50.),btScalar(40.),btScalar(50.)));
        CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 40);

        collisionShapes.add(groundShape);
        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(new Vector3f(0, -56, 0));
        RigidBody groundBody = localCreateRigidBody(0, groundTransform, groundShape);



        CollisionShape shape = new BoxShape(new Vector3f(CUBE_HALF_EXTENTS, CUBE_HALF_EXTENTS, CUBE_HALF_EXTENTS));
        collisionShapes.add(shape);
        Transform trans = new Transform();
        trans.setIdentity();
        trans.origin.set(new Vector3f(0.f, 20.f, 0.f));

        float mass = 1.f;


        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            //point to point constraint with a breaking threshold
            {
                trans.setIdentity();
                trans.origin.set(new Vector3f(1.f, 30.f, -5.f));
                localCreateRigidBody(mass, trans, shape);
                trans.origin.set(new Vector3f(0.f, 0.f, -5.f));

                RigidBody body0 = localCreateRigidBody(mass, trans, shape);
                trans.origin.set(new Vector3f(2 * CUBE_HALF_EXTENTS, 20.f, 0.f));
                mass = 1.f;
                RigidBody body1 = null;//localCreateRigidBody( mass,trans,shape);
                Vector3f pivotInA = new Vector3f(CUBE_HALF_EXTENTS, CUBE_HALF_EXTENTS, 0);
                TypedConstraint p2p = new Point2PointConstraint(body0, pivotInA);
                dynamicsWorld.addConstraint(p2p);
                // p2p.setBreakingImpulseThreshold(10.2);   // @PORT_ISSUE : there is no breaking impulse threshhold in jbullet
                // p2p.setDbgDrawSize(5.f);                 // @PORT_ISSUE constraint debugging not yet implemented
            }
        }



        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            //point to point constraint (ball socket)
            {
                RigidBody body0 = localCreateRigidBody(mass, trans, shape);
                trans.origin.set(new Vector3f(2 * CUBE_HALF_EXTENTS, 20, 0));

                mass = 1.f;
                RigidBody body1 = null;//localCreateRigidBody( mass,trans,shape);
                //		RigidBody* body1 = localCreateRigidBody( 0.0,trans,0);
                //body1.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                //body1.setDamping(0.3,0.3);

                Vector3f pivotInA = new Vector3f(CUBE_HALF_EXTENTS, -CUBE_HALF_EXTENTS, -CUBE_HALF_EXTENTS);
                Vector3f axisInA = new Vector3f(0, 0, 1);


                // @PORT_ISSUE : the true states don't appear to be correctly determined
                //               hard coded to be false, so only implement false condition for now

//                    btVector3 pivotInB = body1 ? body1.getCenterOfMassTransform().inverse()(body0.getCenterOfMassTransform()(pivotInA)) : pivotInA;
//                    btVector3 axisInB  = body1 ?
//			(body1.getCenterOfMassTransform().getBasis().inverse()*(body1.getCenterOfMassTransform().getBasis() * axisInA)) :

                Vector3f pivotInB = pivotInA;

                Vector3f axisInB = new Vector3f();
                body0.getCenterOfMassTransform(new Transform()).basis.transform(axisInA, axisInB);
                //Vector3f  axisInB   = body0.getCenterOfMassTransform(new Transform()).basis.transform(axisInA);


                if (P2P) {
                    TypedConstraint p2p = new Point2PointConstraint(body0, pivotInA);
                    //TypedConstraint* p2p = new Point2PointConstraint(*body0,*body1,pivotInA,pivotInB);
                    //TypedConstraint* hinge = new HingeConstraint(*body0,*body1,pivotInA,pivotInB,axisInA,axisInB);
                    dynamicsWorld.addConstraint(p2p);
                    // p2p.setDbgDrawSize(5.f);     // @PORT_ISSUE constraint debugging not yet implemented
                } else {
                    HingeConstraint hinge = new HingeConstraint(body0, pivotInA, axisInA);

                    //use zero targetVelocity and a small maxMotorImpulse to simulate joint friction
                    //float	targetVelocity = 0.f;
                    //float	maxMotorImpulse = 0.01;
                    float targetVelocity = 1.f;
                    float maxMotorImpulse = 1.0f;
                    hinge.enableAngularMotor(true, targetVelocity, maxMotorImpulse);
                    dynamicsWorld.addConstraint(hinge);
                    // hinge.setDbgDrawSize(5.f);        // @PORT_ISSUE constraint debugging not yet implemented
                } //P2P




            }
        }

        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            //create a slider, using the generic D6 constraint
            {
                mass = 1.f;
                Vector3f sliderWorldPos = new Vector3f(0, 10, 0);
                Vector3f sliderAxis = new Vector3f(1, 0, 0);
                float angle = SIMD_RADS_PER_DEG * 10.f;
                Matrix3f sliderOrientation = new Matrix3f();
                MatrixUtil.setRotation(sliderOrientation, new Quat4f(sliderAxis.x, sliderAxis.y, sliderAxis.z, angle));
                trans.setIdentity();
                trans.origin.set(sliderWorldPos);
                // trans.basis.set(sliderOrientation);
                sliderTransform = trans;

                d6body0 = localCreateRigidBody(mass, trans, shape);
                d6body0.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                RigidBody fixedBody1 = localCreateRigidBody(0, trans, null);        // @PORT_ISSUE: using java null pointer instead of 0
                dynamicsWorld.addRigidBody(fixedBody1);


                Transform frameInA = new Transform();
                Transform frameInB = new Transform();
                frameInA.setIdentity();
                frameInB.setIdentity();
                frameInA.origin.set(new Vector3f(0.f, 5.f, 0.f));
                frameInB.origin.set(new Vector3f(0.f, 5.f, 0.f));

                //		bool useLinearReferenceFrameA = false;//use fixed frame B for linear llimits
                boolean useLinearReferenceFrameA = true;//use fixed frame A for linear llimits
                spSlider6Dof = new Generic6DofConstraint(fixedBody1, d6body0, frameInA, frameInB, useLinearReferenceFrameA);
                spSlider6Dof.setLinearLowerLimit(lowerSliderLimit);
                spSlider6Dof.setLinearUpperLimit(hiSliderLimit);

                //range should be small, otherwise singularities will 'explode' the constraint
                //		spSlider6Dof.setAngularLowerLimit(Vector3f(-1.5,0,0));
                //		spSlider6Dof.setAngularUpperLimit(Vector3f(1.5,0,0));
                //		spSlider6Dof.setAngularLowerLimit(Vector3f(0,0,0));
                //		spSlider6Dof.setAngularUpperLimit(Vector3f(0,0,0));
                spSlider6Dof.setAngularLowerLimit(new Vector3f(-SIMD_PI, 0, 0));
                spSlider6Dof.setAngularUpperLimit(new Vector3f(1.5f, 0, 0));

                spSlider6Dof.getTranslationalLimitMotor().enableMotor[0] = true;
                VectorUtil.setCoord(spSlider6Dof.getTranslationalLimitMotor().targetVelocity, 0, -5.0f);
                VectorUtil.setCoord(spSlider6Dof.getTranslationalLimitMotor().maxMotorForce, 0, 0.1f);


                dynamicsWorld.addConstraint(spSlider6Dof);
                // spSlider6Dof.setDbgDrawSize(5.f);        // @PORT_ISSUE constraint debugging not yet implemented

            }
        }
        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            { // create a door using hinge constraint attached to the world
                CollisionShape pDoorShape = new BoxShape(new Vector3f(2.0f, 5.0f, 0.2f));
                collisionShapes.add(pDoorShape);
                Transform doorTrans = new Transform();
                doorTrans.setIdentity();
                doorTrans.origin.set(new Vector3f(-5.0f, -2.0f, 0.0f));
                RigidBody pDoorBody = localCreateRigidBody((float) 1.0, doorTrans, pDoorShape);
                pDoorBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                final Vector3f btPivotA = new Vector3f(10.f + 2.1f, -2.0f, 0.0f); // right next to the door slightly outside
                Vector3f btAxisA = new Vector3f(0.0f, 1.0f, 0.0f); // pointing upwards, aka Y-axis

                spDoorHinge = new HingeConstraint(pDoorBody, btPivotA, btAxisA);

                //		spDoorHinge.setLimit( 0.0f, SIMD_PI_2 );
                // test problem values
                //		spDoorHinge.setLimit( -SIMD_PI, SIMD_PI*0.8f);

                //		spDoorHinge.setLimit( 1.f, -1.f);
                //		spDoorHinge.setLimit( -SIMD_PI*0.8f, SIMD_PI);
                //		spDoorHinge.setLimit( -SIMD_PI*0.8f, SIMD_PI, 0.9f, 0.3f, 0.0f);
                //		spDoorHinge.setLimit( -SIMD_PI*0.8f, SIMD_PI, 0.9f, 0.01f, 0.0f); // "sticky limits"
                spDoorHinge.setLimit(-SIMD_PI * 0.25f, SIMD_PI * 0.25f);
                //		spDoorHinge.setLimit( 0.0f, 0.0f );
                dynamicsWorld.addConstraint(spDoorHinge);
                // spDoorHinge.setDbgDrawSize(5.f);         // @PORT_ISSUE constraint debugging not yet implemented

                //doorTrans.origin.set(Vector3f(-5.0f, 2.0f, 0.0f));
                //RigidBody* pDropBody = localCreateRigidBody( 10.0, doorTrans, shape);
            }
        }
        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            { // create a generic 6DOF constraint

                Transform tr = new Transform();
                tr.setIdentity();
                tr.origin.set(new Vector3f(10.f, 6.f, 0.f));
                MatrixUtil.setEulerZYX(tr.basis, 0, 0, 0);
                //		RigidBody* pBodyA = localCreateRigidBody( mass, tr, shape);
                RigidBody pBodyA = localCreateRigidBody(0.0f, tr, shape);
                //		RigidBody* pBodyA = localCreateRigidBody( 0.0, tr, 0);
                pBodyA.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

                tr.setIdentity();
                tr.origin.set(new Vector3f(0.f, 6.f, 0.f));
                MatrixUtil.setEulerZYX(tr.basis, 0, 0, 0);
                RigidBody pBodyB = localCreateRigidBody(mass, tr, shape);
                //		RigidBody* pBodyB = localCreateRigidBody(0.f, tr, shape);
                pBodyB.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

                Transform frameInA = new Transform();
                Transform frameInB = new Transform();
                frameInA.setIdentity();
                frameInA.origin.set(new Vector3f(-5.f, 0.f, 0.f));
                frameInB.setIdentity();
                frameInB.origin.set(new Vector3f(5.f, 0.f, 0.f));

                Generic6DofConstraint pGen6DOF = new Generic6DofConstraint(pBodyA, pBodyB, frameInA, frameInB, true);
                //		Generic6DofConstraint* pGen6DOF = new Generic6DofConstraint(*pBodyA, *pBodyB, frameInA, frameInB, false);
                pGen6DOF.setLinearLowerLimit(new Vector3f(-10.f, -2.f, -1.f));
                pGen6DOF.setLinearUpperLimit(new Vector3f(10.f, 2.f, 1.f));
                //		pGen6DOF.setLinearLowerLimit(Vector3f(-10., 0., 0.));
                //		pGen6DOF.setLinearUpperLimit(Vector3f(10., 0., 0.));
                //		pGen6DOF.setLinearLowerLimit(Vector3f(0., 0., 0.));
                //		pGen6DOF.setLinearUpperLimit(Vector3f(0., 0., 0.));

                //		pGen6DOF.getTranslationalLimitMotor().enableMotor[0] = true;
                //		pGen6DOF.getTranslationalLimitMotor().targetVelocity[0] = 5.0f;
                //		pGen6DOF.getTranslationalLimitMotor().maxMotorForce[0] = 0.1f;


                //		pGen6DOF.setAngularLowerLimit(Vector3f(0., SIMD_HALF_PI*0.9, 0.));
                //		pGen6DOF.setAngularUpperLimit(Vector3f(0., -SIMD_HALF_PI*0.9, 0.));
                //		pGen6DOF.setAngularLowerLimit(Vector3f(0., 0., -SIMD_HALF_PI));
                //		pGen6DOF.setAngularUpperLimit(Vector3f(0., 0., SIMD_HALF_PI));

                pGen6DOF.setAngularLowerLimit(new Vector3f(-SIMD_HALF_PI * 0.5f, -0.75f, -SIMD_HALF_PI * 0.8f));
                pGen6DOF.setAngularUpperLimit(new Vector3f(SIMD_HALF_PI * 0.5f, 0.75f, SIMD_HALF_PI * 0.8f));
                //		pGen6DOF.setAngularLowerLimit(Vector3f(0.f, -0.75, SIMD_HALF_PI * 0.8f));
                //		pGen6DOF.setAngularUpperLimit(Vector3f(0.f, 0.75, -SIMD_HALF_PI * 0.8f));
                //		pGen6DOF.setAngularLowerLimit(Vector3f(0.f, -SIMD_HALF_PI * 0.8f, SIMD_HALF_PI * 1.98f));
                //		pGen6DOF.setAngularUpperLimit(Vector3f(0.f, SIMD_HALF_PI * 0.8f,  -SIMD_HALF_PI * 1.98f));



                //		pGen6DOF.setAngularLowerLimit(Vector3f(-0.75,-0.5, -0.5));
                //		pGen6DOF.setAngularUpperLimit(Vector3f(0.75,0.5, 0.5));
                //		pGen6DOF.setAngularLowerLimit(Vector3f(-0.75,0., 0.));
                //		pGen6DOF.setAngularUpperLimit(Vector3f(0.75,0., 0.));
                //		pGen6DOF.setAngularLowerLimit(Vector3f(0., -0.7,0.));
                //		pGen6DOF.setAngularUpperLimit(Vector3f(0., 0.7, 0.));
                //		pGen6DOF.setAngularLowerLimit(Vector3f(-1., 0.,0.));
                //		pGen6DOF.setAngularUpperLimit(Vector3f(1., 0., 0.));

                dynamicsWorld.addConstraint(pGen6DOF, true);
                // pGen6DOF.setDbgDrawSize(5.f);        // @PORT_ISSUE constraint debugging not yet implemented
            }
        }
        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            { // create a ConeTwist constraint

                Transform tr = new Transform();
                tr.setIdentity();
                tr.origin.set(new Vector3f(-10.f, 5.f, 0.f));
                MatrixUtil.setEulerZYX(tr.basis, 0, 0, 0);
                RigidBody pBodyA = localCreateRigidBody(1.0f, tr, shape);
                //		RigidBody* pBodyA = localCreateRigidBody( 0.0, tr, shape);
                pBodyA.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

                tr.setIdentity();
                tr.origin.set(new Vector3f(-10.f, -5.f, 0.f));
                MatrixUtil.setEulerZYX(tr.basis, 0, 0, 0);
                RigidBody pBodyB = localCreateRigidBody(0.0f, tr, shape);
                //		RigidBody* pBodyB = localCreateRigidBody(1.0, tr, shape);

                Transform frameInA = new Transform();
                Transform frameInB = new Transform();
                frameInA.setIdentity();
                MatrixUtil.setEulerZYX(frameInA.basis, 0, 0, SIMD_PI_2);
                frameInA.origin.set(new Vector3f(0.f, -5.f, 0.f));
                frameInB.setIdentity();
                MatrixUtil.setEulerZYX(frameInB.basis, 0, 0, SIMD_PI_2);
                frameInB.origin.set(new Vector3f(0.f, 5.f, 0.f));

                ctc = new ConeTwistConstraint(pBodyA, pBodyB, frameInA, frameInB);
                //		ctc.setLimit(btScalar(SIMD_PI_4), btScalar(SIMD_PI_4), btScalar(SIMD_PI) * 0.8f);
                //		ctc.setLimit(btScalar(SIMD_PI_4*0.6f), btScalar(SIMD_PI_4), btScalar(SIMD_PI) * 0.8f, 1.0f); // soft limit == hard limit
                ctc.setLimit(SIMD_PI_4 * 0.6f, SIMD_PI_4, SIMD_PI * 0.8f, 0.5f,
                        0.3f, 1.0f);        // note: these are default parameters in bullet
                dynamicsWorld.addConstraint(ctc, true);
                // ctc.setDbgDrawSize(5.f);         // @PORT_ISSUE constraint debugging not yet implemented
                // s_bTestConeTwistMotor = true; // use only with old solver for now
                s_bTestConeTwistMotor = false;
            }
        }
        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            { // Hinge connected to the world, with motor (to hinge motor with new and old constraint solver)
                Transform tr = new Transform();
                tr.setIdentity();
                tr.origin.set(new Vector3f(0, 0, 0));
                RigidBody pBody = localCreateRigidBody(1.0f, tr, shape);
                pBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                final Vector3f btPivotA = new Vector3f(10.0f, 0.0f, 0.0f);
                Vector3f btAxisA = new Vector3f(0.0f, 0.0f, 1.0f);

                HingeConstraint pHinge = new HingeConstraint(pBody, btPivotA, btAxisA);
                //		pHinge.enableAngularMotor(true, -1.0, 0.165); // use for the old solver
                pHinge.enableAngularMotor(true, -1.0f, 1.65f); // use for the new SIMD solver
                dynamicsWorld.addConstraint(pHinge);
                // pHinge.setDbgDrawSize(5.f);          // @PORT_ISSUE constraint debugging not yet implemented
            }
        }

        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            {
                // create a universal joint using generic 6DOF constraint
                // create two rigid bodies
                // static bodyA (parent) on top:
                Transform tr = new Transform();
                tr.setIdentity();
                tr.origin.set(new Vector3f(20.f, 4.f, 0.f));
                RigidBody pBodyA = localCreateRigidBody(0.0f, tr, shape);
                pBodyA.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                // dynamic bodyB (child) below it :
                tr.setIdentity();
                tr.origin.set(new Vector3f(20.f, 0.f, 0.f));
                RigidBody pBodyB = localCreateRigidBody(1.0f, tr, shape);
                pBodyB.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                // add some (arbitrary) data to build constraint frames
                Vector3f parentAxis = new Vector3f(1.f, 0.f, 0.f);
                Vector3f childAxis = new Vector3f(0.f, 0.f, 1.f);
                Vector3f anchor = new Vector3f(20.f, 2.f, 0.f);

// @PORT_ISSUE : UniversalConstraint not yet implemented
//                    UniversalConstraint pUniv = new UniversalConstraint(pBodyA, pBodyB, anchor, parentAxis, childAxis);
//                    pUniv.setLowerLimit(-SIMD_HALF_PI * 0.5f, -SIMD_HALF_PI * 0.5f);
//                    pUniv.setUpperLimit(SIMD_HALF_PI * 0.5f,  SIMD_HALF_PI * 0.5f);
//                    // add constraint to world
//                    dynamicsWorld.addConstraint(pUniv, true);
//                    // draw constraint frames and limits for debugging
//                    pUniv.setDbgDrawSize(5.f);
            }
        }

        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            { // create a generic 6DOF constraint with springs

                Transform tr = new Transform();
                tr.setIdentity();
                tr.origin.set(new Vector3f(-20.f, 16.f, 0.f));
                MatrixUtil.setEulerZYX(tr.basis, 0, 0, 0);
                RigidBody pBodyA = localCreateRigidBody(0.0f, tr, shape);
                pBodyA.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

                tr.setIdentity();
                tr.origin.set(new Vector3f(-10.f, 16.f, 0.f));
                MatrixUtil.setEulerZYX(tr.basis, 0, 0, 0);
                RigidBody pBodyB = localCreateRigidBody(1.0f, tr, shape);
                pBodyB.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

                Transform frameInA = new Transform();
                Transform frameInB = new Transform();
                frameInA.setIdentity();
                frameInA.origin.set(new Vector3f(10.f, 0.f, 0.f));
                frameInB.setIdentity();
                frameInB.origin.set(new Vector3f(0.f, 0.f, 0.f));

                Generic6DofSpringConstraint pGen6DOFSpring = new Generic6DofSpringConstraint(pBodyA, pBodyB, frameInA, frameInB, true);
                pGen6DOFSpring.setLinearUpperLimit(new Vector3f(5.f, 0.f, 0.f));
                pGen6DOFSpring.setLinearLowerLimit(new Vector3f(-5.f, 0.f, 0.f));

//                pGen6DOFSpring.setAngularLowerLimit(new Vector3f(0.f, 0.f, -1.5f));
//                pGen6DOFSpring.setAngularUpperLimit(new Vector3f(0.f, 0.f, 1.5f));

                dynamicsWorld.addConstraint(pGen6DOFSpring, true);
                // pGen6DOFSpring.setDbgDrawSize(5.f);      // @PORT_ISSUE constraint debugging not yet implemented

                pGen6DOFSpring.enableSpring(0, true);
                pGen6DOFSpring.setStiffness(0, 39.478f);
                pGen6DOFSpring.setDamping(0, 0.5f);
//                pGen6DOFSpring.enableSpring(5, true);
//                pGen6DOFSpring.setStiffness(5, 39.478f);
//                pGen6DOFSpring.setDamping(5, 0.3f);
//                pGen6DOFSpring.setEquilibriumPoint();
            }
        }

// @PORT_ISSUE : no such constraint yet in JBullet
//    if (ConstraintDemo.ENABLE_ALL_DEMOS) {
//            {
//                    // create a Hinge2 joint
//                    // create two rigid bodies
//                    // static bodyA (parent) on top:
//                    Transform tr;
//                    tr.setIdentity();
//                    tr.origin.set(new Vector3f(-20.f, 4.f, 0.f));
//                    RigidBody pBodyA = localCreateRigidBody(0.0f, tr, shape);
//                    pBodyA.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
//                    // dynamic bodyB (child) below it :
//                    tr.setIdentity();
//                    tr.origin.set(new Vector3f(-20.f, 0.f, 0.f));
//                    RigidBody pBodyB = localCreateRigidBody(1.0f, tr, shape);
//                    pBodyB.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
//                    // add some data to build constraint frames
//                    Vector3f parentAxis = new Vector3f(0.f, 1.f, 0.f);
//                    Vector3f childAxis = new Vector3f(1.f, 0.f, 0.f);
//                    Vector3f anchor = new Vector3f(-20.f, 0.f, 0.f);
//                    Hinge2Constraint pHinge2 = new Hinge2Constraint(pBodyA, pBodyB, anchor, parentAxis, childAxis);
//                    pHinge2.setLowerLimit(-SIMD_HALF_PI * 0.5f);
//                    pHinge2.setUpperLimit( SIMD_HALF_PI * 0.5f);
//                    // add constraint to world
//                    dynamicsWorld.addConstraint(pHinge2, true);
//                    // draw constraint frames and limits for debugging
//                    pHinge2.setDbgDrawSize(5.f);
//            }
//    }
        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            {
                // create a Hinge joint between two dynamic bodies
                // create two rigid bodies
                // static bodyA (parent) on top:
                Transform tr = new Transform();
                tr.setIdentity();
                tr.origin.set(new Vector3f(-20.f, -2.f, 0.f));
                RigidBody pBodyA = localCreateRigidBody(1.0f, tr, shape);
                pBodyA.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                // dynamic bodyB:
                tr.setIdentity();
                tr.origin.set(new Vector3f(-30.f, -2.f, 0.f));
                RigidBody pBodyB = localCreateRigidBody(10.0f, tr, shape);
                pBodyB.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                // add some data to build constraint frames
                Vector3f axisA = new Vector3f(0.f, 1.f, 0.f);
                Vector3f axisB = new Vector3f(0.f, 1.f, 0.f);
                Vector3f pivotA = new Vector3f(-5.f, 0.f, 0.f);
                Vector3f pivotB = new Vector3f(5.f, 0.f, 0.f);
                spHingeDynAB = new HingeConstraint(pBodyA, pBodyB, pivotA, pivotB, axisA, axisB);
                spHingeDynAB.setLimit(-SIMD_HALF_PI * 0.5f, SIMD_HALF_PI * 0.5f);
                // add constraint to world
                dynamicsWorld.addConstraint(spHingeDynAB, true);
                // draw constraint frames and limits for debugging
                // spHingeDynAB.setDbgDrawSize(5.f);        // @PORT_ISSUE constraint debugging not yet implemented
            }
        }

        if (ConstraintDemo.ENABLE_ALL_DEMOS) {
            { // 6DOF connected to the world, with motor
                Transform tr = new Transform();
                tr.setIdentity();
                tr.origin.set(new Vector3f(10.f, -15.f, 0.f));
                RigidBody pBody = localCreateRigidBody(1.0f, tr, shape);
                pBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                Transform frameB = new Transform();
                frameB.setIdentity();
                Generic6DofConstraint pGen6Dof = new Generic6DofConstraint(pBody, frameB, false);
                dynamicsWorld.addConstraint(pGen6Dof);
                // pGen6Dof.setDbgDrawSize(5.f);        // @PORT_ISSUE constraint debugging not yet implemented

                pGen6Dof.setAngularLowerLimit(new Vector3f(0, 0, 0));
                pGen6Dof.setAngularUpperLimit(new Vector3f(0, 0, 0));
                pGen6Dof.setLinearLowerLimit(new Vector3f(-10.f, 0, 0));
                pGen6Dof.setLinearUpperLimit(new Vector3f(10.f, 0, 0));

                pGen6Dof.getTranslationalLimitMotor().enableMotor[0] = true;
                VectorUtil.setCoord(pGen6Dof.getTranslationalLimitMotor().targetVelocity, 0, 5.0f);
                VectorUtil.setCoord(pGen6Dof.getTranslationalLimitMotor().maxMotorForce, 0, 0.1f);
            }
        }
    }

    public ConstraintDemo(IGL gl) {
        super(gl);
    }

    @Override
    public void clientMoveAndDisplay()
    {
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

 	float dt = (float)(getDeltaTimeMicroseconds()) * 0.000001f;
	//printf("dt = %f: ",dt);

	// drive cone-twist motor
	time += 0.03f;

        // @PORT_ISSUE motor not implemented for cone twist constraint

//	if (s_bTestConeTwistMotor)
//	{  // this works only for obsolete constraint solver for now
//		// build cone target
//		float t = 1.25f*time;
//		Vector3f axis = new Vector3f(0, (float)Math.sin(t), (float)Math.cos(t));
//		axis.normalize();
//
//                float tmpTpl[] = new float[4];
//                axis.get(tmpTpl);
//                tmpTpl[3] = 0.75f*SIMD_PI;
//		Quat4f q1 = new Quat4f(tmpTpl);
//
//		// build twist target
//		//btQuaternion q2(0,0,0);
//		//btQuaternion q2(btVehictor3(1,0,0), -0.3*sin(m_Time));
//		Quat4f q2 = new Quat4f(1.f, 0, 0, -1.49f*(float)Math.sin(1.5f*time));
//
//		// compose cone + twist and set target
//		q1.mul(q2); // q1 = q1 * q2;
//		ctc.enableMotor = true;
//		ctc.setMotorTargetInConstraintSpace = q1;
//	}

        
        // @PORT_ISSUE
        // these DEBUG modes not yet supported: use DebugDrawModes
//	{
//		static boolean once = true;
//		if (dynamicsWorld.getDebugDrawer() && once)
//		{
//			dynamicsWorld.getDebugDrawer().setDebugMode(IDebugDraw.DBG_DrawConstraints+btIDebugDraw.DBG_DrawConstraintLimits);
//			once=false;
//		}
//	}


	{
	 	//during idle mode, just run 1 simulation step maximum
		int maxSimSubSteps = idle ? 1 : 1;
		if (idle) {
                    dt = 1.0f/420.f;
                }

		int numSimSteps = dynamicsWorld.stepSimulation(dt, maxSimSubSteps);

		//optional but useful: debug drawing
		dynamicsWorld.debugDrawWorld();

		boolean verbose = false;
		if (verbose)
		{
                    if (numSimSteps == 0)
                    {
                        System.out.printf("Interpolated transforms\n");
                    }
                    else
                    {
                        if (numSimSteps > maxSimSubSteps)
                        {
                                //detect dropping frames
                                System.out.printf("Dropped (%i) simulation steps out of %i\n",numSimSteps - maxSimSubSteps,numSimSteps);
                        }
                        else
                        {
                                System.out.printf("Simulated (%i) steps\n",numSimSteps);
                        }
                    }
		}
	}
	renderme();

//	drawLimit();

//        glFlush();
//        swapBuffers();

    }


    @Override
    public void keyboardCallback(char key, int x, int y, int modifiers)
    {
	switch (key)
	{


        // @PORT_ISSUE : frame offsets not yet supported
//		case 'O' :
//			{
//				boolean offectOnOff;
//				if (spDoorHinge != null)
//				{
//					offectOnOff = spDoorHinge.getUseFrameOffset();
//					offectOnOff = !offectOnOff;
//					spDoorHinge.setUseFrameOffset(offectOnOff);
//					System.out.printf("DoorHinge %s frame offset\n", offectOnOff ? "uses" : "does not use");
//				}
//				if (spHingeDynAB != null)
//				{
//					offectOnOff = spHingeDynAB.getUseFrameOffset();
//					offectOnOff = !offectOnOff;
//					spHingeDynAB.setUseFrameOffset(offectOnOff);
//					System.out.("HingeDynAB %s frame offset\n", offectOnOff ? "uses" : "does not use");
//				}
//				if (spSlider6Dof != null)
//				{
//					offectOnOff = spSlider6Dof.getUseFrameOffset();
//					offectOnOff = !offectOnOff;
//					spSlider6Dof.setUseFrameOffset(offectOnOff);
//					System.out.printf("Slider6Dof %s frame offset\n", offectOnOff ? "uses" : "does not use");
//				}
//			}
//			break;
		default :
			{
				super.keyboardCallback(key, x, y, modifiers);
			}
			break;
	}
    }


    @Override
    public void displayCallback()
    {
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (dynamicsWorld != null) {
                dynamicsWorld.debugDrawWorld();
        }

    //	drawLimit();

        renderme();

        // glFlush();
        // swapBuffers();
    }



    public static void main(String[] args) throws LWJGLException {
        ConstraintDemo demoApp = new ConstraintDemo(LWJGL.getGL());
        demoApp.initPhysics();

        LWJGL.main(args, 800, 600, "Bullet Physics Demo. http://bullet.sf.net", demoApp);
    }

}
