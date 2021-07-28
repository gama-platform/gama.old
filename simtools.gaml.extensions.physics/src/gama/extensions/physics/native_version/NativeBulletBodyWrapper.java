package gama.extensions.physics.native_version;

import static java.lang.Math.max;
import static msi.gaml.types.GamaGeometryType.buildBox;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import gama.extensions.physics.common.AbstractBodyWrapper;
import gama.extensions.physics.common.IBody;
import gama.extensions.physics.common.IShapeConverter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaPair;
import msi.gaml.types.Types;

/*
 * A rigid body "wrapper" dedicated to GAMA agents. Allows to translate information from/to the agents and their bodies,
 * to reconstruct shapes (from JTS geometries and GAMA 3D additions, but also from AABB envelopes) and to pass commands
 * to the bodies (velocity, forces, location...)
 *
 * @author Alexis Drogoul 2021
 */
public class NativeBulletBodyWrapper
		extends AbstractBodyWrapper<PhysicsSpace, PhysicsRigidBody, CollisionShape, Vector3f>
		implements INativeBulletPhysicalEntity {

	// Between GAMA coordinates and JBullet coordinates. Some discrepancies
	// exist (esp. on spheres, for instance)
	Quaternion quatTransfer = new Quaternion();

	public NativeBulletBodyWrapper(final IAgent agent, final NativeBulletPhysicalWorld gateway) {
		super(agent, gateway);
		setLocation(agent.getLocation());
		// We add the wrapper to both the body and the agent to enable their inter-communication
		agent.setAttribute(BODY, this);
		body.setUserObject(this);
	}

	@Override
	public PhysicsRigidBody createAndInitializeBody(final CollisionShape shape, final PhysicsSpace world) {
		PhysicsRigidBody body = new PhysicsRigidBody(shape);
		IBody previous = (IBody) agent.getAttribute(BODY);
		if (previous != null) {
			body.setMass(previous.getMass());
			body.setFriction(previous.getFriction());
			body.setRestitution(previous.getRestitution());
			body.setAngularDamping(previous.getAngularDamping());
			body.setContactDamping(previous.getContactDamping());
			body.setLinearDamping(previous.getLinearDamping());
			GamaPoint pointTransfer = new GamaPoint();
			body.setLinearVelocity(toVector(previous.getLinearVelocity(pointTransfer)));
			body.setAngularVelocity(toVector(previous.getAngularVelocity(pointTransfer)));
		}
		body.setEnableSleep(false);
		return body;

	}

	@Override
	public void setCCD(final boolean v) {
		if (v) {
			Vector3f min = new Vector3f();
			Vector3f max = new Vector3f();
			BoundingBox bb = new BoundingBox();
			body.boundingBox(bb);
			bb.getMax(max);
			bb.getMax(min);
			float ccd = max(max(max.x, max.y), max.z);
			body.setCcdSweptSphereRadius(ccd / 2);
			body.setCcdMotionThreshold(ccd / 4);
		} else {
			body.setCcdMotionThreshold(0f);
		}
	}

	// ====================================================
	// Transfer functions from the agent to the rigid body
	// ====================================================

	@Override
	public void setFriction(final Double friction) {
		body.setFriction(clamp(friction));
	}

	@Override
	public void setRestitution(final Double restitution) {
		body.setRestitution(clamp(restitution));
	}

	@Override
	public void setDamping(final Double damping) {
		body.setLinearDamping(clamp(damping));
	}

	@Override
	public void setAngularDamping(final Double damping) {
		body.setAngularDamping(clamp(damping));
	}

	@Override
	public void setContactDamping(final Double damping) {
		body.setContactDamping(clamp(damping));
	}

	@Override
	public void setAngularVelocity(final GamaPoint angularVelocity) {
		body.setAngularVelocity(toVector(angularVelocity));
	}

	@Override
	public void setLinearVelocity(final GamaPoint linearVelocity) {
		body.setLinearVelocity(toVector(linearVelocity));
	}

	@Override
	public void setLocation(final GamaPoint loc) {
		body.setPhysicsLocation(toVector(loc).addLocal(aabbTranslation));
	}

	@Override
	public void applyImpulse(final GamaPoint impulse) {
		body.applyCentralImpulse(toVector(impulse));

	}

	@Override
	public void applyTorque(final GamaPoint torque) {
		body.applyTorque(toVector(torque));

	}

	@Override
	public void applyForce(final GamaPoint force) {
		body.applyCentralForce(toVector(force));

	}

	@Override
	public void setMass(final Double mass) {
		body.setMass(mass.floatValue());
	}

	// ====================================================
	// Transfer functions from the body to the GAMA agent
	// ====================================================

	@Override
	public void transferLocationAndRotationToAgent() {
		Vector3f vectorTransfer = body.getPhysicsLocation(null);
		agent.setLocation(new GamaPoint(vectorTransfer.x, vectorTransfer.y, vectorTransfer.z - aabbTranslation.z));
		body.getPhysicsRotation(quatTransfer);
		var rot = (GamaPair<Double, GamaPoint>) agent.getAttribute(ROTATION);
		if (rot == null) {
			rot = new GamaPair<>(0d, new GamaPoint(0, 0, 1), Types.FLOAT, Types.POINT);
			agent.setAttribute(ROTATION, rot);
		}
		float qx = quatTransfer.getX();
		float qy = quatTransfer.getY();
		float qz = quatTransfer.getZ();
		double mag = qx * qx + qy * qy + qz * qz;
		if (mag > EPS) {
			mag = Math.sqrt(mag);
			double invMag = 1.0 / mag;
			rot.value.setLocation(qx * invMag, qy * invMag, qz * invMag);
			rot.key = Math.toDegrees(2.0 * Math.atan2(mag, quatTransfer.getW()));
		}
	}

	@Override
	public IShape getAABB() {
		Vector3f min = new Vector3f();
		Vector3f max = new Vector3f();
		BoundingBox bb = new BoundingBox();
		body.boundingBox(bb);
		bb.getMax(max);
		bb.getMin(min);
		return buildBox(max.x - min.x, max.y - min.y, max.z - min.z, new GamaPoint(min.x + (max.x - min.x) / 2,
				min.y + (max.y - min.y) / 2, min.z + (max.z - min.z) / 2 + visualTranslation.z));

	}

	public Vector3f getTranslation() {
		return aabbTranslation;
	}

	@Override
	public float getMass() {
		return body.getMass();
	}

	@Override
	public float getFriction() {
		return body.getFriction();
	}

	@Override
	public float getRestitution() {
		return body.getRestitution();
	}

	@Override
	public float getLinearDamping() {
		return body.getLinearDamping();
	}

	@Override
	public float getAngularDamping() {
		return body.getAngularDamping();
	}

	@Override
	public GamaPoint getAngularVelocity(final GamaPoint v) {
		Vector3f vectorTransfer = new Vector3f();
		body.getAngularVelocity(vectorTransfer);
		return toGamaPoint(vectorTransfer, v);
	}

	@Override
	public GamaPoint getLinearVelocity(final GamaPoint v) {
		GamaPoint result = v == null ? new GamaPoint() : v;
		Vector3f vectorTransfer = new Vector3f();
		body.getLinearVelocity(vectorTransfer);
		result.setLocation(vectorTransfer.x, vectorTransfer.y, vectorTransfer.z);
		return result;
	}

	@Override
	public void clearForces() {
		body.clearForces();
	}

	public void updateShape(final IShapeConverter<CollisionShape, Vector3f> converter) {
		CollisionShape shape = converter.convertAndTranslate(agent, aabbTranslation, visualTranslation);
		body.setCollisionShape(shape);
	}

	@Override
	public float getContactDamping() {
		return body.getContactDamping();
	}

}
