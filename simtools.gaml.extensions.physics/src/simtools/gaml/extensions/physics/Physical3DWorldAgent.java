/*********************************************************************************************
 *
 *
 * 'Physical3DWorldAgent.java', in plugin 'simtools.gaml.extensions.physics', is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package simtools.gaml.extensions.physics;

import java.util.HashMap;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/*
 * species: The PhysicalWorldAgent is defined in this class. PhysicalWorldAgent supports the action
 *
 * @author Javier Gil-Quijano - Arnaud Grignard - 18-Nov-2012 (Gama Winter School) Last Modified: 23-Mar-2012
 */
@species (
		name = "physical_world")
@doc ("The base species for agents that act as a 3D physical world")
@vars ({ @variable (
		name = "gravity",
		type = IType.BOOL,
		init = "true",
		doc = @doc ("Define if the physical world has a gravity or not")),
		@variable (
				name = IKeyword.AGENTS,
				type = IType.LIST,
				of = IType.AGENT,
				init = "[]",
				doc = { @doc ("The list of agents registered in this physical world") }) })
public class Physical3DWorldAgent extends MinimalAgent {

	private final IList<IAgent> registeredAgents = GamaListFactory.create(Types.AGENT);
	private final HashMap<IAgent, RigidBody> registeredMap = new HashMap<>();
	private PhysicsWorldJBullet world;

	public Physical3DWorldAgent(final IPopulation<? extends IAgent> s, final int index) throws GamaRuntimeException {
		super(s, index);
		world = new PhysicsWorldJBullet(true);
	}

	@getter (IKeyword.AGENTS)
	public IList<IAgent> getRegisteredAgents() {
		return registeredAgents;
	}

	@setter (IKeyword.AGENTS)
	public void setRegisteredAgents(final IList<IAgent> agents) {
		if (agents.size() > PhysicsWorldJBullet.MAX_OBJECTS) {
			GamaRuntimeException.error("Physic engine cannot manage more than " + PhysicsWorldJBullet.MAX_OBJECTS + "agents", GAMA.getRuntimeScope());
		} else {

			world = new PhysicsWorldJBullet(true);
			cleanRegisteredAgents();
			registeredAgents.addAll(agents);
			setRegisteredAgentsToWorld();
		}
		
	}

	@getter ("gravity")
	public Boolean getGravity() {

		if (this.getAttribute("gravity") == null) {
			setGravity(true);
			return true;
		} else {
			return (Boolean) this.getAttribute("gravity");
		}
	}

	@setter ("gravity")
	public void setGravity(final Boolean gravity) {
		this.setAttribute("gravity", gravity);
		if (gravity) {
			world.dynamicsWorld.setGravity(new Vector3f(0.0f, 0.0f, -9.81f));
		} else {
			world.dynamicsWorld.setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
		}

	}

	@Override
	public void dispose() {
		cleanRegisteredAgents();
		super.dispose();
	}

	/*
	 * Read the value define in GAML of collisionBound to set the collisionShape of the JBullet world. If collisionBound
	 * is not define it will create a sphere of radius= 1 and mass =1.
	 *
	 * Once the CollisionShape is defined it is added in the JBulletPhysicWorld
	 */
	private RigidBody CollisionBoundToCollisionShape(final IAgent geom) {

		// Double mass = 1.0;
		CollisionShape shape = null;
		;

		// FIXME: As getLocation() is not working in 3D (hard to compute a 3D
		// centroid)
		// e;G The location of a plan with a z value will be at 0.
		// Basic way to set the right z get the first coordinate of the shape,
		// problem if the shape
		// is not in the z plan it is totally wrong.
		float computedZLocation;
		if (Double.isNaN(geom.getInnerGeometry().getCoordinates()[0].z) == true) {
			computedZLocation = (float) geom.getLocation().getZ();
		} else {
			computedZLocation = (float) geom.getInnerGeometry().getCoordinates()[0].z;
		}
		final Vector3f position =
				new Vector3f((float) geom.getLocation().getX(), (float) geom.getLocation().getY(), computedZLocation);

		final GamaList<Double> velocity = (GamaList<Double>) Cast.asList(null, geom.getAttribute("velocity"));
		final Vector3f _velocity =
				new Vector3f(velocity.get(0).floatValue(), velocity.get(1).floatValue(), velocity.get(2).floatValue());

		final Double mass = (Double) geom.getAttribute("mass");

		final Object collBObj = geom.getAttribute("collisionBound");
		final GamaMap<String, ?> collisionBound = Cast.asMap(null, collBObj, false);

		if (collisionBound.isEmpty()) { // Default collision shape is a sphere
			// of radius 1.0;
			final Double radius = 1.0;
			shape = new SphereShape(radius.floatValue());
		} else {

			final String shapeType = (String) collisionBound.get("shape");

			if (shapeType.equalsIgnoreCase("sphere")) {
				final Double radius = Cast.asFloat(null, collisionBound.get("radius"));
				shape = new SphereShape(radius.floatValue());
			}

			if (shapeType.equalsIgnoreCase("floor")) {
				final double x = Cast.asFloat(null, collisionBound.get("x"));
				final double y = Cast.asFloat(null, collisionBound.get("y"));
				final double z = Cast.asFloat(null, collisionBound.get("z"));
				shape = new BoxShape(new Vector3f((float) x, (float) y, (float) z));
			}
		}
		return world.addCollisionObject(shape, mass.floatValue(), position, _velocity);
	}

	private void cleanRegisteredAgents() {
		for (final IAgent ia : registeredAgents) {
			registeredMap.remove(ia);
		}
		registeredAgents.clear();
	}

	private void setRegisteredAgentsToWorld() {
		for (final IAgent ia : registeredAgents) {
			registerAgent(ia);
		}

	}

	public void registerAgent(final IAgent ia) {
		final RigidBody body = this.CollisionBoundToCollisionShape(ia);
		registeredMap.put(ia, body);
	}

	@action (
			name = "compute_forces",
			args = @arg (
					name = "step",
					type = IType.FLOAT,
					optional = true,
					doc = {}))
	@doc ("This action allows the world to compute the forces exerted on each agent")
	public Object primComputeForces(final IScope scope) throws GamaRuntimeException {

		final Double timeStep = scope.hasArg("step") ? (Double) scope.getArg("step", IType.FLOAT) : 1.0;
		// DEBUG.LOG("Time step : "+ timeStep);
		world.update(timeStep.floatValue());

		for (final IAgent ia : registeredMap.keySet()) {
			final RigidBody node = registeredMap.get(ia);
			final Vector3f _position = world.getNodePosition(node);
			final GamaPoint position =
					new GamaPoint(new Double(_position.x), new Double(_position.y), new Double(_position.z));
			ia.setLocation(position);
			final Coordinate[] coordinates = ia.getInnerGeometry().getCoordinates();
			for (int i = 0; i < coordinates.length; i++) {
				coordinates[i].z = _position.z;
			}

		}
		return null;

	}

}
