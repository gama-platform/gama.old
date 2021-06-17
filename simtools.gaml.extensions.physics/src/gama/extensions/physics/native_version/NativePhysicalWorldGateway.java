package gama.extensions.physics.native_version;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;

import gama.extensions.physics.common.AbstractPhysicalWorldGateway;
import gama.extensions.physics.common.IBody;
import gama.extensions.physics.common.IShapeConverter;
import gama.extensions.physics.gaml.PhysicalSimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

public class NativePhysicalWorldGateway extends AbstractPhysicalWorldGateway<PhysicsSpace, CollisionShape, Vector3f> {

	final ContactListener contactListener = new ContactListener();

	public NativePhysicalWorldGateway(final PhysicalSimulationAgent physicalSimulationAgent) {
		super(physicalSimulationAgent);
	}

	@Override
	public void updateEngine(final Double timeStep, final int maxSubSteps) {
		getWorld().update(timeStep.floatValue(), maxSubSteps);
	}

	@Override
	protected IShapeConverter<CollisionShape, Vector3f> createShapeConverter() {
		return new NativeGeometryToShapeConverter();
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
		NativeBodyWrapper b = new NativeBodyWrapper(agent, this);
		getWorld().addCollisionObject(b.getBody());
		b.setCCD(simulation.getCCD(simulation.getScope()));
	}

	@Override
	public void unregisterAgent(final IAgent agent) {
		NativeBodyWrapper wrapper = (NativeBodyWrapper) agent.getAttribute(BODY);
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
		if (world != null) { world.setGravity(new Vector3f((float) g.x, (float) g.y, (float) g.z)); }
	}

	@Override
	public void dispose() {
		// world.removeCollisionListener(this);
		if (world == null) return;
		world.getRigidBodyList().forEach(world::removeCollisionObject);
		world = null;
	}

	@Override
	public void updatePositionsAndRotations() {
		world.getRigidBodyList().forEach((b) -> {
			NativeBodyWrapper bw = (NativeBodyWrapper) b.getUserObject();
			if (b.isActive() && !b.isStatic()) { bw.transferLocationAndRotationToAgent(); }
		});
	}

	@Override
	protected void updateAgentsShape() {
		// We update the agents
		for (IAgent a : updatableAgents) {
			NativeBodyWrapper body = (NativeBodyWrapper) a.getAttribute(BODY);
			if (body == null) return;
			body.updateShape(getShapeConverter());
		}
		updatableAgents.clear();
	}

	@Override
	public void collectContacts(final Multimap<IBody, IBody> newContacts) {
		world.distributeEvents();
		newContacts.putAll(contactListener.newContacts);
		contactListener.newContacts.clear();
	}

	private class ContactListener implements PhysicsCollisionListener {

		Multimap<IBody, IBody> newContacts = MultimapBuilder.hashKeys().hashSetValues().build();

		@Override
		public void collision(final PhysicsCollisionEvent event) {
			IBody w0 = (IBody) event.getObjectA().getUserObject();
			IBody w1 = (IBody) event.getObjectB().getUserObject();
			if (w0.isNoNotification() && w1.isNoNotification()) return;
			// System.out.println("" + w0.getAgent() + " collides " + w1.getAgent() + " at " + event.getDistance1());
			newContacts.put(w0, w1);
		}

	}

}
