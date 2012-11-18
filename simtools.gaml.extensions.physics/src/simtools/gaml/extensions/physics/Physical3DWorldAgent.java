package simtools.gaml.extensions.physics;

import java.util.HashMap;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Spatial.Operators;
import msi.gaml.types.IType;


/*
 * species: The PhysicalWorldAgent is defined in this class. PhysicalWorldAgent supports the action
 * 
 * @author Javier Gil-Quijano 23-Mar-2012
 * Last Modified: 23-Mar-2012
 */
@species(name = "Physical3DWorld")
@vars({ @var(name = "gravity", type = IType.POINT_STR, init = "{0.0, 0.0}"),
	@var(name = "registeredAgents", type = IType.LIST_STR, init = "[]") })
public class Physical3DWorldAgent extends GamlAgent {

	public final static String REGISTERED_AGENTS = "registeredAgents";
	private GamaPoint gravity = new GamaPoint(0, 10);
	private IList<IAgent> registeredAgents = null;
	private final HashMap<IAgent, RigidBody> registeredMap = new HashMap<IAgent, RigidBody>();
//	private final World _world = new World(new Vec2((float) gravity.x, (float) gravity.y), true);

//	private final BulletAppState _world = new BulletAppState();
	private final PhysicsWorldJBullet _world = new PhysicsWorldJBullet();

	public Physical3DWorldAgent(final ISimulation sim, final IPopulation s)
		throws GamaRuntimeException {
		super(sim, s);
	}

	@getter("registeredAgents")
	public IList<IAgent> getRegisteredAgents() {
		return registeredAgents;
	}

	@setter("registeredAgents")
	public void setRegisteredAgents(final IList<IAgent> agents) {
		System.out.println("Registering agents : " + agents);
		cleanRegisteredAgents();
		registeredAgents = agents;
		setRegisteredAgentsToWorld();
	}

	public void registerAgent(final IAgent _agent) {
		if ( registeredAgents == null ) {
			registeredAgents = new GamaList<IAgent>();
		}
		_registerAgent(_agent);
	}

	private RigidBody CollisionBoundToCollisionShape(final IAgent geom){
		
		//Double mass = 1.0;
		CollisionShape shape = null;;

		Vector3f position = new Vector3f((float)(float)geom.getLocation().getX(), (float)geom.getLocation().getY(),(float)geom.getLocation().getZ());
		GamaPoint velocity = (GamaPoint) geom.getAttribute("velocity");
		Vector3f _velocity = new Vector3f((float)velocity.x, (float)velocity.y, (float)0.0);
		
		Double  mass = (Double) geom.getAttribute("mass");
		
		Object collBObj = geom.getAttribute("collisionBound");
		GamaMap collisionBound = Cast.asMap(null, collBObj);
		
		if (collisionBound.isEmpty()){ //Default collision shape is a sphere of radius 1.0;	
			Double radius = 1.0;
			System.out.println("agent " + geom.getAgent().getIndex() + "is define by a default sphere of radius " + radius);
			shape = new SphereShape(radius.floatValue());
		}
		else{
			
			String shapeType = (String)collisionBound.get("shape");
			
			if(shapeType.equalsIgnoreCase("sphere")){
				
				Double radius = Cast.asFloat(null, collisionBound.get("radius"));
				
				System.out.println("agent " + geom.getAgent().getIndex() + "is define by a sphere of radius " + radius + "mass:" + mass);

				shape = new SphereShape(radius.floatValue());
//			        rootNode.attachChild(physicsSphere);
			}
			
			if(shapeType.equalsIgnoreCase("floor")){
				
				double x = Cast.asFloat(null, collisionBound.get("x"));
				double y = Cast.asFloat(null, collisionBound.get("y"));
				double z = Cast.asFloat(null, collisionBound.get("z"));
				
				System.out.println("agent " + geom.getAgent().getIndex() + "is define by a floor of x " + x + "y:" + y +"z:" + z + "mass:" + mass);
				
				shape = new BoxShape(new Vector3f((float)x, (float)y, (float)z));

				//shape = new SphereShape(5);	
//			        rootNode.attachChild(physicsSphere);
			}
		}
		return _world.addCollisionObject(shape, mass.floatValue(), position, _velocity);
	}

	private void cleanRegisteredAgents() {
		if ( registeredAgents != null ) {
			while (registeredAgents.size() > 0) {
				IAgent ia = registeredAgents.remove(0);
				
				RigidBody bd = registeredMap.remove(ia);
//				_world.removeNode(bd);
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
		System.out.println("Registering : " + ia);
		// PolygonShape shape = GamaPolyToPolyPhysic(ia);
		// CircleShape shape = new CircleShape();
		// shape.m_radius = 1.0f;

		/**
		 * FixtureDef fd = new FixtureDef(); fd.shape = shape; fd.density =
		 * 1.0f;
		 **/
		// float restitution[] = {0.0f, 0.1f, 0.3f, 0.5f, 0.75f, 0.9f, 1.0f};

		/**		float density = ((Double) ia.getAttribute("density")).floatValue();

		BodyDef bd = new BodyDef();
		bd.position.set((float) ia.getLocation().getX(), (float) ia.getLocation().getY());
		bd.type = BodyType.DYNAMIC;
**/
		RigidBody body = this.CollisionBoundToCollisionShape(ia);


//		body.setLinearVelocity(new Vec2((float) velocity.getX(), (float) velocity.getY()));
		registeredMap.put(ia, body);

	}

	public GamaPoint getGravity() {
		return gravity;
	}

	@setter("gravity")
	public void setGravity(final GamaPoint _gravity) {
		gravity = _gravity;
//		_world.setGravity(new Vec2((float) gravity.x, (float) gravity.y));
	}

	

	@action(name = "computeForces")
	@args(names = { "timeStep", "velocityIterations", "port", "dbName", "usrName", "password" })
	public Object primComputeForces(final IScope scope) throws GamaRuntimeException {

		Double timeStep =
			scope.hasArg("timeStep") ? (Double) scope.getArg("timeStep", IType.FLOAT) :1.0;
			//System.out.println("Time step : "+ timeStep);
			_world.update(timeStep.floatValue());
/**			


		// TODO update positions of every agent to take into account external
		// movements
		// for machant
		_world.step(timeStep.floatValue(), velocityIterations, positionIterations);
		**/
		// Updates the location of the objects

		for ( IAgent ia : registeredMap.keySet() ) {
			RigidBody node = registeredMap.get(ia);
			Vector3f _position = _world.getNodePosition(node);
			GamaPoint position =
				new GamaPoint(new Double(_position.x), new Double(_position.y), new Double(_position.z));

			ia.setLocation(position);
			Coordinate[] coordinates = ia.getInnerGeometry().getCoordinates();
			((GamaPoint) ia.getLocation()).z = position.z;
			for (int i = 0; i < coordinates.length; i++) {
				coordinates[i].z = position.z;
			}

		}
		return null;

	}

}
