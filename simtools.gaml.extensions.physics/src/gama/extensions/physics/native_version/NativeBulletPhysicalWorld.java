package gama.extensions.physics.native_version;

import com.google.common.collect.Multimap;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;

import gama.extensions.physics.common.AbstractPhysicalWorld;
import gama.extensions.physics.common.IBody;
import gama.extensions.physics.common.IShapeConverter;
import gama.extensions.physics.gaml.PhysicalSimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

public class NativeBulletPhysicalWorld extends AbstractPhysicalWorld<PhysicsSpace, CollisionShape, Vector3f>
		implements INativeBulletPhysicalEntity {

	public NativeBulletPhysicalWorld(final PhysicalSimulationAgent physicalSimulationAgent) {
		super(physicalSimulationAgent);
	}

	@Override
	public void updateEngine(final Double timeStep, final int maxSubSteps) {
		getWorld().update(timeStep.floatValue(), maxSubSteps);
	}

	@Override
	protected IShapeConverter<CollisionShape, Vector3f> createShapeConverter() {
		return new NativeBulletShapeConverter();
	}

	@Override
	public PhysicsSpace createWorld() {
		world = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
		world.addCollisionListener(contactListener);
		setGravity(simulation.getGravity(simulation.getScope()));
		setCCD(simulation.getCCD(simulation.getScope()));
		return world;
	}

	@Override
	public void registerAgent(final IAgent agent) {
		NativeBulletBodyWrapper b = new NativeBulletBodyWrapper(agent, this);
		getWorld().addCollisionObject(b.getBody());
		b.setCCD(simulation.getCCD(simulation.getScope()));
	}

	@Override
	public void unregisterAgent(final IAgent agent) {
		NativeBulletBodyWrapper wrapper = (NativeBulletBodyWrapper) agent.getAttribute(BODY);
		getWorld().remove(wrapper.getBody());
	}

	@Override
	public void setCCD(final boolean ccd) {
		if (world != null) {
			world.getRigidBodyList().forEach(b -> {
				if (b.isStatic()) return;
				Object o = b.getUserObject();
				if (o instanceof IBody) { ((IBody) o).setCCD(ccd); }
			});
		}
	}

	@Override
	public void setGravity(final GamaPoint g) {
		if (world != null) { world.setGravity(toVector(g)); }
	}

	@Override
	public void dispose() {
		if (world == null) return;
		world.getRigidBodyList().forEach(world::removeCollisionObject);
		world = null;
	}

	@Override
	public void updatePositionsAndRotations() {
		for (PhysicsRigidBody b : world.getRigidBodyList()) {
			NativeBulletBodyWrapper bw = (NativeBulletBodyWrapper) b.getUserObject();
			if (b.isActive() && !b.isStatic()) { bw.transferLocationAndRotationToAgent(); }
		}
	}

	@Override
	protected void updateAgentsShape() {
		// We update the agents
		for (IAgent a : updatableAgents) {
			NativeBulletBodyWrapper body = (NativeBulletBodyWrapper) a.getAttribute(BODY);
			if (body == null) return;
			body.updateShape(getShapeConverter());
		}
		updatableAgents.clear();
	}

	@Override
	public void collectContacts(final Multimap<IBody, IBody> newContacts) {
		world.distributeEvents();
		super.collectContacts(newContacts);
	}

}
