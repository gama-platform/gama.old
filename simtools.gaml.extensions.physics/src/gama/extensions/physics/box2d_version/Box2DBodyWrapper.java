/*******************************************************************************************************
 *
 * Box2DBodyWrapper.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extensions.physics.box2d_version;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import gama.extensions.physics.common.AbstractBodyWrapper;
import gama.extensions.physics.common.IBody;
import gama.extensions.physics.gaml.PhysicalSimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaPair;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class Box2DBodyWrapper.
 */
public class Box2DBodyWrapper extends AbstractBodyWrapper<World, Body, Shape, Vec2> implements IBox2DPhysicalEntity {

	static {
		DEBUG.OFF();
	}

	/** The tolerance. */
	static double TOLERANCE = 0.0000001;

	/** The def. */
	BodyDef def;

	/** The fixture def. */
	FixtureDef fixtureDef;

	/** The ms. */
	MassData ms;

	/** The scale. */
	private final float scale;

	/**
	 * Instantiates a new box 2 D body wrapper.
	 *
	 * @param agent
	 *            the agent
	 * @param world
	 *            the world
	 */
	public Box2DBodyWrapper(final IAgent agent, final Box2DPhysicalWorld world) {
		super(agent, world);
		scale = world.getScale();
		if (agent instanceof PhysicalSimulationAgent) {
			body.setAwake(false);
			body.setActive(false);
		}
		setLocation(agent.getLocation());

	}

	@Override
	public Body createAndInitializeBody(final Shape shape, final World world) {
		def = new BodyDef();
		fixtureDef = new FixtureDef();
		IBody previous = (IBody) agent.getAttribute(BODY);
		if (previous != null) {
			GamaPoint pointTransfer = new GamaPoint();
			def.type = isStatic ? BodyType.STATIC : BodyType.DYNAMIC;
			def.angularDamping = previous.getAngularDamping();
			def.angularVelocity = toBox2D(previous.getAngularVelocity(pointTransfer).norm());
			def.linearDamping = previous.getLinearDamping();
			toVector(previous.getLinearVelocity(pointTransfer), def.linearVelocity);
			def.allowSleep = false;
			def.userData = this;
			def.bullet = true;
		}
		Body newBody = world.createBody(def);
		if (previous != null) {
			fixtureDef.setDensity(1f);
			fixtureDef.setFriction(previous.getFriction());
			fixtureDef.setRestitution(previous.getRestitution());
			fixtureDef.setShape(shape);
			fixtureDef.setSensor(false);
		}
		newBody.createFixture(fixtureDef);
		ms = new MassData();
		if (previous != null) { ms.mass = previous.getMass(); }
		newBody.setMassData(ms);
		return newBody;
	}

	@Override
	public float getMass() { return body.m_mass; }

	@Override
	public float getFriction() { return body.getFixtureList().getFriction(); }

	@Override
	public float getRestitution() { return body.getFixtureList().getRestitution(); }

	@Override
	public float getLinearDamping() { return body.m_linearDamping; }

	@Override
	public float getAngularDamping() { return body.m_angularDamping; }

	@Override
	public float getContactDamping() {
		// Doesnt exist
		return 0;
	}

	@Override
	public GamaPoint getAngularVelocity(final GamaPoint v) {
		v.setLocation(0, 0, body.getAngularVelocity());
		return v;
	}

	@Override
	public GamaPoint getLinearVelocity(final GamaPoint v) {
		return toGamaPoint(body.getLinearVelocity(), v);
	}

	@Override
	public IShape getAABB() {
		AABB aabb = body.getFixtureList().getAABB(0);
		Vec2 v = aabb.getExtents();
		return GamaGeometryType.buildRectangle(toGama(v.x * 2), toGama(v.y * 2), toGamaPoint(body.getPosition()));
	}

	@Override
	public void setMass(final Double mass) {
		ms.mass = mass.floatValue();
		body.setMassData(ms);

	}

	@Override
	public void setCCD(final boolean v) {
		// Verify this
		body.setBullet(v);
	}

	@Override
	public void setFriction(final Double friction) {
		body.getFixtureList().setFriction(friction.floatValue());

	}

	@Override
	public void setRestitution(final Double restitution) {
		DEBUG.OUT("Restitution of " + agent.getName() + " " + restitution);
		body.getFixtureList().setRestitution(restitution.floatValue());
	}

	@Override
	public void setDamping(final Double damping) {
		body.setLinearDamping(damping.floatValue());
	}

	@Override
	public void setAngularDamping(final Double damping) {
		body.setAngularDamping(damping.floatValue());
	}

	@Override
	public void setContactDamping(final Double damping) {
		// Not available
	}

	@Override
	public void setAngularVelocity(final GamaPoint angularVelocity) {
		body.setAngularVelocity(toBox2D(angularVelocity.z));
	}

	@Override
	public void setLinearVelocity(final GamaPoint linearVelocity) {
		GamaPoint current = new GamaPoint();
		getLinearVelocity(current);
		if (!linearVelocity.equals2D(current, TOLERANCE)) { body.setLinearVelocity(toVector(linearVelocity)); }
	}

	@Override
	public void setLocation(final GamaPoint loc) {
		body.setTransform(toVector(loc), body.getAngle());
	}

	@Override
	public void clearForces() {
		body.setLinearVelocity(new Vec2(0, 0));
		body.setAngularVelocity(0);
	}

	@Override
	public void applyImpulse(final GamaPoint impulse) {
		body.applyLinearImpulse(toVector(impulse), body.getLocalCenter(), true);
	}

	@Override
	public void applyTorque(final GamaPoint torque) {
		body.applyTorque(toBox2D(torque.norm()));
	}

	@Override
	public void applyForce(final GamaPoint force) {
		body.applyForceToCenter(toVector(force));
	}

	@Override
	public void transferLocationAndRotationToAgent() {
		Vec2 vectorTransfer = body.getPosition();
		agent.setLocation(toGamaPoint(vectorTransfer));
		Rot bodyRotation = body.getTransform().q;
		@SuppressWarnings ("unchecked") var rot = (GamaPair<Double, GamaPoint>) agent.getAttribute(ROTATION);
		if (rot == null) {
			rot = new GamaPair<>(0d, new GamaPoint(0, 0, 1), Types.FLOAT, Types.POINT);
			agent.setAttribute(ROTATION, rot);
		}
		rot.key = Math.toDegrees(bodyRotation.getAngle());
	}

	@Override
	public float getScale() { return scale; }

}
