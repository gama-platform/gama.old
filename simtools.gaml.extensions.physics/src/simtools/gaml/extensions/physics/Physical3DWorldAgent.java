package simtools.gaml.extensions.physics;

import java.util.HashMap;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
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
 * @author Javier Gil-Quijano  - Arnaud Grignard -  18-Nov-2012 (Gama Winter School)
 * Last Modified: 23-Mar-2012
 */
@species(name = "Physical3DWorld")
@vars({ @var(name = "gravity", type = IType.BOOL_STR, init = "true",doc = @doc("Define if the physical world has a gravity or not")),
	@var(name = "registeredAgents", type = IType.LIST_STR, init = "[]") })
public class Physical3DWorldAgent extends GamlAgent {

	public final static String REGISTERED_AGENTS = "registeredAgents";
	//private GamaPoint gravity = new GamaPoint(0, 10);
	private IList<IAgent> registeredAgents = null;
	Boolean gravity;
	private final HashMap<IAgent, RigidBody> registeredMap = new HashMap<IAgent, RigidBody>();
	private final PhysicsWorldJBullet world;
	

	public Physical3DWorldAgent(final ISimulation sim, final IPopulation s)
		throws GamaRuntimeException {
		super(sim, s);
		//FIXME: Does not work
		//gravity = this.getGravity();
		gravity = true;
		world = new PhysicsWorldJBullet(gravity);
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
	public Boolean getGravity(){
		return (Boolean) this.getAttribute("gravity");
	}
	
	@setter ("gravity")
	public void setGravity(Boolean gravity){
		this.setAttribute("grabity", gravity);	
	}

	public void registerAgent(final IAgent _agent) {
		if ( registeredAgents == null ) {
			registeredAgents = new GamaList<IAgent>();
		}
		_registerAgent(_agent);
	}

	
	/*
	 * Read the value define in GAML of collisionBound to set the collisionShape of the JBullet world.
	 * If collisionBound is not define it will create a sphere of radius= 1 and mass =1.
	 * 
	 * Once the CollisionShape is defined it is added in the JBulletPhysicWorld
	 */
	private RigidBody CollisionBoundToCollisionShape(final IAgent geom){
		
		//Double mass = 1.0;
		CollisionShape shape = null;;

		
		//FIXME: As getLocation() is not working in 3D (hard to compute a 3D centroid)
		//e;G The location of a plan with a z value will be at 0.
		//Basic way to set the right z get the first coordinate of the shape, problem if the shape is not in the z plan it is totally wrong.
		float computedZLocation;
		if(String.valueOf(geom.getInnerGeometry().getCoordinates()[0].z).equals("NaN") == true){
			computedZLocation=(float)geom.getLocation().getZ();
		}
		else{
			computedZLocation = (float)geom.getInnerGeometry().getCoordinates()[0].z;
		}
		Vector3f position = new Vector3f((float)(float)geom.getLocation().getX(), (float)geom.getLocation().getY(),computedZLocation);
		
		GamaList<Double> velocity = (GamaList<Double>) Cast.asList(null,  geom.getAttribute("velocity"));
		Vector3f _velocity = new Vector3f(velocity.get(0).floatValue(), velocity.get(1).floatValue(), velocity.get(2).floatValue());
		
		Double  mass = (Double) geom.getAttribute("mass");
		
		Object collBObj = geom.getAttribute("collisionBound");
		GamaMap collisionBound = Cast.asMap(null, collBObj);
		
		if (collisionBound.isEmpty()){ //Default collision shape is a sphere of radius 1.0;	
			Double radius = 1.0;
			shape = new SphereShape(radius.floatValue());
		}
		else{
			
			String shapeType = (String)collisionBound.get("shape");
			
			if(shapeType.equalsIgnoreCase("sphere")){
				Double radius = Cast.asFloat(null, collisionBound.get("radius"));
				shape = new SphereShape(radius.floatValue());
			}
			
			if(shapeType.equalsIgnoreCase("floor")){
				double x = Cast.asFloat(null, collisionBound.get("x"));
				double y = Cast.asFloat(null, collisionBound.get("y"));
				double z = Cast.asFloat(null, collisionBound.get("z"));
				shape = new BoxShape(new Vector3f((float)x, (float)y, (float)z));
			}
		}
		return world.addCollisionObject(shape, mass.floatValue(), position, _velocity);
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
		RigidBody body = this.CollisionBoundToCollisionShape(ia);
        registeredMap.put(ia, body);
	}

	@action(name = "computeForces")
	@args(names = { "timeStep", "velocityIterations", "port", "dbName", "usrName", "password" })
	public Object primComputeForces(final IScope scope) throws GamaRuntimeException {

		Double timeStep =
			scope.hasArg("timeStep") ? (Double) scope.getArg("timeStep", IType.FLOAT) :1.0;
			//System.out.println("Time step : "+ timeStep);
			world.update(timeStep.floatValue());
			
			
			for ( IAgent ia : registeredMap.keySet() ) {
				RigidBody node = registeredMap.get(ia);
				Vector3f _position = world.getNodePosition(node);
	
				GamaPoint position =
					new GamaPoint(new Double(_position.x), new Double(_position.y), new Double(_position.z));
				ia.setLocation(position);
				Coordinate[] coordinates = ia.getInnerGeometry().getCoordinates();
				for (int i = 0; i < coordinates.length; i++) {
					coordinates[i].z = _position.z ;
				}
				
			}
		return null;

	}

}
