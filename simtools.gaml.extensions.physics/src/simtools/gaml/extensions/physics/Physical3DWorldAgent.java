/*********************************************************************************************
 *
 *
 * 'Physical3DWorldAgent.java', in plugin 'simtools.gaml.extensions.physics', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package simtools.gaml.extensions.physics;

import java.util.HashMap;
import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.RigidBody;
import com.vividsolutions.jts.geom.Coordinate;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/*
 * species: The PhysicalWorldAgent is defined in this class. PhysicalWorldAgent supports the action
 *
 * @author Javier Gil-Quijano - Arnaud Grignard - 18-Nov-2012 (Gama Winter School)
 * Last Modified: 23-Mar-2012
 */
@species(name = "Physical3DWorld")
@vars({
	@var(name = "gravity",
		type = IType.BOOL,
		init = "true",
		doc = @doc("Define if the physical world has a gravity or not") ),
	@var(name = "registeredAgents", type = IType.LIST, init = "[]") })
public class Physical3DWorldAgent extends GamlAgent {

	public final static String REGISTERED_AGENTS = "registeredAgents";
	private IList<IAgent> registeredAgents = null;
	private final HashMap<IAgent, RigidBody> registeredMap = new HashMap<IAgent, RigidBody>();
	private final PhysicsWorldJBullet world;

	public Physical3DWorldAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
		world = new PhysicsWorldJBullet(true);
	}

	@getter("registeredAgents")
	public IList<IAgent> getRegisteredAgents() {
		return registeredAgents;
	}

	@setter("registeredAgents")
	public void setRegisteredAgents(final IList<IAgent> agents) {
		cleanRegisteredAgents();
		registeredAgents = agents;
		setRegisteredAgentsToWorld();
	}

	@getter("gravity")
	public Boolean getGravity() {

		if ( this.getAttribute("gravity") == null ) {
			setGravity(true);
			return true;
		} else {
			return (Boolean) this.getAttribute("gravity");
		}
	}

	@setter("gravity")
	public void setGravity(final Boolean gravity) {
		this.setAttribute("gravity", gravity);
		if ( gravity ) {
			world.dynamicsWorld.setGravity(new Vector3f(0.0f, 0.0f, -9.81f));
		} else {
			world.dynamicsWorld.setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
		}

	}

	public void registerAgent(final IAgent _agent) {
		if ( registeredAgents == null ) {
			registeredAgents = GamaListFactory.create(Types.AGENT);
		}
		_registerAgent(_agent);
	}

	/*
	 * Read the value define in GAML of collisionBound to set the collisionShape of the JBullet
	 * world.
	 * If collisionBound is not define it will create a sphere of radius= 1 and mass =1.
	 *
	 * Once the CollisionShape is defined it is added in the JBulletPhysicWorld
	 */
	private RigidBody CollisionBoundToCollisionShape(final IAgent geom) {

		// Double mass = 1.0;
		CollisionShape shape = null;;

		// FIXME: As getLocation() is not working in 3D (hard to compute a 3D centroid)
		// e;G The location of a plan with a z value will be at 0.
		// Basic way to set the right z get the first coordinate of the shape, problem if the shape
		// is not in the z plan it is totally wrong.
		float computedZLocation;
		if ( Double.isNaN(geom.getInnerGeometry().getCoordinates()[0].z) == true ) {
			computedZLocation = (float) geom.getLocation().getZ();
		} else {
			computedZLocation = (float) geom.getInnerGeometry().getCoordinates()[0].z;
		}
		Vector3f position =
			new Vector3f((float) geom.getLocation().getX(), (float) geom.getLocation().getY(), computedZLocation);

		GamaList<Double> velocity = (GamaList<Double>) Cast.asList(null, geom.getAttribute("velocity"));
		Vector3f _velocity =
			new Vector3f(velocity.get(0).floatValue(), velocity.get(1).floatValue(), velocity.get(2).floatValue());

		Double mass = (Double) geom.getAttribute("mass");

		Object collBObj = geom.getAttribute("collisionBound");
		GamaMap collisionBound = Cast.asMap(null, collBObj, false);

		if ( collisionBound.isEmpty() ) { // Default collision shape is a sphere of radius 1.0;
			Double radius = 1.0;
			shape = new SphereShape(radius.floatValue());
		} else {

			String shapeType = (String) collisionBound.get("shape");

			if ( shapeType.equalsIgnoreCase("sphere") ) {
				Double radius = Cast.asFloat(null, collisionBound.get("radius"));
				shape = new SphereShape(radius.floatValue());
			}

			if ( shapeType.equalsIgnoreCase("floor") ) {
				double x = Cast.asFloat(null, collisionBound.get("x"));
				double y = Cast.asFloat(null, collisionBound.get("y"));
				double z = Cast.asFloat(null, collisionBound.get("z"));
				shape = new BoxShape(new Vector3f((float) x, (float) y, (float) z));
			}
		}
		return world.addCollisionObject(shape, mass.floatValue(), position, _velocity);
	}

	private void cleanRegisteredAgents() {
		if ( registeredAgents != null ) {
			while (registeredAgents.size() > 0) {
				IAgent ia = registeredAgents.remove(0);

				RigidBody bd = registeredMap.remove(ia);
				// _world.removeNode(bd);
			}
		}
	}

	private void setRegisteredAgentsToWorld() {
		if ( registeredAgents != null ) {
			for ( IAgent ia : registeredAgents ) {
				_registerAgent(ia);
			}
		}

	}

	private void _registerAgent(final IAgent ia) {
		RigidBody body = this.CollisionBoundToCollisionShape(ia);
		registeredMap.put(ia, body);
	}

	@action(name = "computeForces")
	@args(names = { "timeStep", "velocityIterations", "port", "dbName", "usrName", "password" })
	public Object primComputeForces(final IScope scope) throws GamaRuntimeException {

		Double timeStep = scope.hasArg("timeStep") ? (Double) scope.getArg("timeStep", IType.FLOAT) : 1.0;
		// System.out.println("Time step : "+ timeStep);
		world.update(timeStep.floatValue());

		for ( IAgent ia : registeredMap.keySet() ) {
			RigidBody node = registeredMap.get(ia);
			Vector3f _position = world.getNodePosition(node);
			GamaPoint position =
				new GamaPoint(new Double(_position.x), new Double(_position.y), new Double(_position.z));
			ia.setLocation(position);
			Coordinate[] coordinates = ia.getInnerGeometry().getCoordinates();
			for ( int i = 0; i < coordinates.length; i++ ) {
				coordinates[i].z = _position.z;
			}

		}
		return null;

	}

}
