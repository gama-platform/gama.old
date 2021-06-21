package gama.extensions.physics.java_version;

import static java.lang.Math.max;
import static msi.gaml.types.GamaGeometryType.buildBox;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import gama.extensions.physics.common.IBody;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
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
public class BodyWrapper implements IBody<RigidBody, CollisionShape> {

	public final boolean noNotification;
	private final Transform temp = new Transform();
	private final Vector3f vtemp = new Vector3f(), vtemp2 = new Vector3f();
	final AxisAngle4f axisAngleTransfer = new AxisAngle4f();
	Quat4f quatTransfer = new Quat4f();
	private final RigidBody body;
	private final IAgent agent;
	// Between GAMA coordinates and JBullet coordinates. Some discrepancies
	// exist (esp. on spheres, for instance)
	final Vector3f aabbTranslation = new Vector3f();
	final Vector3f visualTranslation = new Vector3f();

	public BodyWrapper(final IAgent agent, final PhysicalWorldGateway gateway) {
		assert agent != null;
		this.agent = agent;
		CollisionShape shape =
				gateway.getShapeConverter().convertAndTranslate(agent, aabbTranslation, visualTranslation);
		body = createAndInitializeBody(shape);
		noNotification = this.noContactNotificationWanted(agent);
		// here, we erase whatever temporary body was existing
		agent.setAttribute(BODY, this);
		body.setUserPointer(this);
	}

	@Override
	public boolean isNoNotification() {
		return noNotification;
	}

	@Override
	public IAgent getAgent() {
		return agent;
	}

	@Override
	public RigidBody createAndInitializeBody(final CollisionShape shape) {
		final Transform startTransform = new Transform();
		startTransform.setIdentity();
		ILocation p = agent.getLocation();
		startTransform.origin.set((float) p.getX(), (float) p.getY(), (float) p.getZ() + aabbTranslation.getZ());
		final MotionState state = new DefaultMotionState(startTransform);
		final RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(0f, state, shape);
		IBody previous = (IBody) agent.getAttribute(BODY);
		if (previous != null) {
			final float mass = previous.getMass();
			info.mass = mass;
			if (mass != 0f) { shape.calculateLocalInertia(mass, info.localInertia); }
			info.friction = previous.getFriction();
			info.restitution = previous.getRestitution();
			info.angularDamping = previous.getAngularDamping();
			info.linearDamping = previous.getLinearDamping();

		}
		RigidBody body = new RigidBody(info);
		if (!body.isStaticObject()) { body.setActivationState(CollisionObject.DISABLE_DEACTIVATION); }
		if (previous != null) {
			GamaPoint pointTransfer = new GamaPoint();
			body.setLinearVelocity(toVector(previous.getLinearVelocity(pointTransfer)));
			body.setAngularVelocity(toVector(previous.getAngularVelocity(pointTransfer)));
		}
		return body;
	}

	@Override
	public void setCCD(final boolean v) {
		if (v) {
			body.getAabb(vtemp, vtemp2);
			vtemp2.sub(vtemp);
			float ccd = max(max(vtemp2.x, vtemp2.y), vtemp2.z);
			body.setCcdMotionThreshold(ccd / 4);
			body.setCcdSweptSphereRadius(ccd / 2);
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
		body.setDamping(clamp(damping), getAngularDamping());
	}

	@Override
	public void setAngularDamping(final Double damping) {
		body.setDamping(getLinearDamping(), clamp(damping));
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
		// We synchronize both the world transform of the body (which holds the position at the end of last tick) and
		// the motion state.
		body.getWorldTransform(temp);
		temp.origin.set((float) loc.x, (float) loc.y, (float) loc.z + aabbTranslation.z);
		body.setWorldTransform(temp);
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

	private Vector3f toVector(final GamaPoint v) {
		return v == null ? new Vector3f() : new Vector3f((float) v.x, (float) v.y, (float) v.z);
	}

	@Override
	public float getMass() {
		float inverse = body.getInvMass();
		return inverse == 0f ? 0f : 1 / inverse;
	}

	@Override
	public void setMass(final Double mass) {
		body.getCollisionShape().calculateLocalInertia(mass.floatValue(), vtemp);
		body.setMassProps(mass.floatValue(), vtemp);
	}

	@Override
	public GamaPoint getAngularVelocity(final GamaPoint v) {
		GamaPoint result = v == null ? new GamaPoint() : v;
		body.getAngularVelocity(vtemp);
		result.setLocation(vtemp.x, vtemp.y, vtemp.z);
		return result;

	}

	@Override
	public GamaPoint getLinearVelocity(final GamaPoint v) {
		GamaPoint result = v == null ? new GamaPoint() : v;
		body.getLinearVelocity(vtemp);
		result.setLocation(vtemp.x, vtemp.y, vtemp.z);
		return result;
	}

	@Override
	public IShape getAABB() {
		body.getAabb(vtemp, vtemp2);
		return buildBox(vtemp2.x - vtemp.x, vtemp2.y - vtemp.y, vtemp2.z - vtemp.z,
				new GamaPoint(vtemp.x + (vtemp2.x - vtemp.x) / 2, vtemp.y + (vtemp2.y - vtemp.y) / 2,
						vtemp.z + (vtemp2.z - vtemp.z) / 2 + visualTranslation.z));
	}

	@Override
	public float getContactDamping() {
		return 0;
	}

	@Override
	public void setContactDamping(final Double damping) {
		// Not available
	}

	@Override
	public RigidBody getBody() {
		return body;
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
	public void clearForces() {
		body.clearForces();
	}

	@Override
	public void transferLocationAndRotationToAgent() {
		Vector3f vectorTransfer = body.getWorldTransform(temp).origin;
		agent.setLocation(new GamaPoint(vectorTransfer.x, vectorTransfer.y, vectorTransfer.z - aabbTranslation.z));
		temp.getRotation(quatTransfer);
		axisAngleTransfer.set(quatTransfer);
		var rot = (GamaPair<Double, GamaPoint>) agent.getAttribute(ROTATION);
		if (rot == null) {
			rot = new GamaPair<>(0d, new GamaPoint(0, 0, 1), Types.FLOAT, Types.POINT);
			agent.setAttribute(ROTATION, rot);
		}
		rot.key = Math.toDegrees(axisAngleTransfer.angle);
		rot.value.setLocation(axisAngleTransfer.x, axisAngleTransfer.y, axisAngleTransfer.z);
	}

}
