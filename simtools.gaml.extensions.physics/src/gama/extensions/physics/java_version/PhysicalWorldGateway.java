package gama.extensions.physics.java_version;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.google.common.collect.Multimap;

import gama.extensions.physics.common.AbstractPhysicalWorldGateway;
import gama.extensions.physics.common.IBody;
import gama.extensions.physics.common.IShapeConverter;
import gama.extensions.physics.gaml.PhysicalSimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

public class PhysicalWorldGateway
		extends AbstractPhysicalWorldGateway<DiscreteDynamicsWorld, CollisionShape, Vector3f> {

	private final CollisionConfiguration config = new DefaultCollisionConfiguration();
	private final CollisionDispatcher dispatcher = new CollisionDispatcher(config);

	public PhysicalWorldGateway(final PhysicalSimulationAgent physicalSimulationAgent) {
		super(physicalSimulationAgent);
	}

	@Override
	protected IShapeConverter<CollisionShape, Vector3f> createShapeConverter() {
		return new GeometryToShapeConverter();
	}

	@Override
	public DiscreteDynamicsWorld createWorld() {
		final BroadphaseInterface pairCache = new DbvtBroadphase();
		final SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		world = new DiscreteDynamicsWorld(dispatcher, pairCache, solver, config);
		setGravity(simulation.getGravity(simulation.getScope()));
		setCCD(simulation.getCCD(simulation.getScope()));
		return world;
	}

	@Override
	public void updateEngine(final Double timeStep, final int maxSubSteps) {
		getWorld().stepSimulation(timeStep.floatValue(), maxSubSteps);
	}

	@Override
	public void registerAgent(final IAgent agent) {
		BodyWrapper body = new BodyWrapper(agent, this);
		getWorld().addRigidBody(body.getBody());
		body.setCCD(simulation.getCCD(simulation.getScope()));
	}

	@Override
	public void unregisterAgent(final IAgent agent) {
		BodyWrapper b = (BodyWrapper) agent.getAttribute(BODY);
		getWorld().removeRigidBody(b.getBody());
	}

	@Override
	public void updateAgentsShape() {
		// We update the agents
		for (IAgent a : updatableAgents) {
			unregisterAgent(a);
		}
		for (IAgent a : updatableAgents) {
			registerAgent(a);
		}
		updatableAgents.clear();
	}

	@Override
	public void collectContacts(final Multimap<IBody, IBody> newContacts) {
		for (PersistentManifold pm : dispatcher.getInternalManifoldPointer()) {
			if (pm == null) { continue; }
			IBody b0 = (IBody) ((RigidBody) pm.getBody0()).getUserPointer();
			IBody b1 = (IBody) ((RigidBody) pm.getBody1()).getUserPointer();
			if (b0.isNoNotification() && b1.isNoNotification()) { continue; }
			int n = pm.getNumContacts();
			for (int i = 0; i < n; i++) {
				ManifoldPoint pt = pm.getContactPoint(i);
				if (pt.getDistance() < 0.1) {
					newContacts.put(b0, b1);
					break;
				}
			}
		}
	}

	@Override
	public void setCCD(final boolean ccd) {
		if (world != null) {
			world.getCollisionObjectArray().forEach(b -> {
				if (b.isStaticObject()) return;
				Object o = b.getUserPointer();
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
		if (world != null) {
			world.destroy();
			world = null;
		}
	}

	@Override
	public void updatePositionsAndRotations() {
		world.getCollisionObjectArray().forEach((b) -> {
			RigidBody rb = (RigidBody) b;
			IBody bw = (IBody) rb.getUserPointer();
			if (rb.isActive() && !rb.isStaticObject()) { bw.transferLocationAndRotationToAgent(); }
		});
	}

}
