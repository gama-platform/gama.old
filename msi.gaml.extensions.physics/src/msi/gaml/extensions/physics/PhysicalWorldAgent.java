package msi.gaml.extensions.physics;

import java.util.HashMap;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;

/*
 * species: The PhysicalWorldAgent  is defined in this class. PhysicalWorldAgent supports the action
 * 
 * @author  Javier Gil-Quijano  23-Mar-2012
 * Last Modified: 23-Mar-2012
 */
@species("PhysicalWorld")
@vars({@var(name = "gravityX", type = IType.FLOAT_STR, init = "0.0"),
	@var(name = "gravityY", type = IType.FLOAT_STR, init = "10.0"),
	@var(name = "registeredAgents", type = IType.LIST_STR, init="[]")
	})
public class PhysicalWorldAgent extends GamlAgent{

	public final static String GRAVITY_X = "gravityX";
	public final static String GRAVITY_Y = "gravityY";
	public final static String REGISTERED_AGENTS = "registeredAgents";
	private GamaPoint gravity = new GamaPoint(0, 10);
	private IList<IAgent> registeredAgents = null;
	private HashMap<IAgent, Body> registeredMap = new HashMap<IAgent, Body>();
	private World _world = new World(new Vec2((float)gravity.x, (float)gravity.y), true);
	public PhysicalWorldAgent(final ISimulation sim, final IPopulation s) throws GamaRuntimeException {
		super(sim, s);	
	}

	public IList<IAgent> getRegisteredAgents(){
		return registeredAgents;
	}
	@setter("registeredAgents")
	public void setRegisteredAgents(final IList<IAgent> agents){
		System.out.println("Registering agents : " + agents);
		cleanRegisteredAgents();
		registeredAgents = agents;
		setRegisteredAgentsToWorld();
	}
	
	public PolygonShape GamaPolyToPolyPhysic(IShape geom){
		PolygonShape poly = new PolygonShape();
		Geometry g = geom.getInnerGeometry().convexHull();
		Coordinate[] coords = g.getCoordinates();
		Vec2[] vecs = new Vec2[coords.length - 1];
		for (int i = 0; i < coords.length -1; i++) {
			Coordinate coord = coords[i];
			Vec2 v = new Vec2((float)coord.x, (float)coord.y);
			vecs[i] = v;
		}
		System.out.println("Vertex number : " + vecs.length);
		poly.set(vecs, vecs.length);
		return poly;
	}

	private void cleanRegisteredAgents(){
		if(registeredAgents != null){
			while(registeredAgents.size() >0){
				IAgent ia = registeredAgents.remove(0);
				Body bd = registeredMap.remove(ia);
				_world.destroyBody(bd);
			}
		}
	}
	private void setRegisteredAgentsToWorld(){
		if(registeredAgents != null){
			for(IAgent ia : registeredAgents){
				System.out.println("Registering : " + ia);
				PolygonShape shape = GamaPolyToPolyPhysic(ia);
//				CircleShape shape = new CircleShape();
//				shape.m_radius = 1.0f;

				FixtureDef fd = new FixtureDef();
				fd.shape = shape;
				fd.density = 1.0f;

//				float restitution[] = {0.0f, 0.1f, 0.3f, 0.5f, 0.75f, 0.9f, 1.0f};

				BodyDef bd = new BodyDef();
				bd.type = BodyType.DYNAMIC;
			    bd.position.set((float)ia.getLocation().getX(), (float)ia.getLocation().getY());

				Body body = _world.createBody(bd);
				registeredMap.put(ia, body);
			}
		}
		
	}
	
	public GamaPoint getGravity(){
		return gravity;
	}
	public void setGravity(final GamaPoint _gravity){
		gravity = _gravity;
		_world.setGravity(new Vec2((float)gravity.x,(float)gravity.y));
	}
	
	public Body getBody(final IAgent agent){
		return registeredMap.get(agent);
	}
	
	@action("computeForces")
	@args({"timeStep","velocityIterations", "port", "dbName", "usrName", "password"})	
	public  Object  connectDB(final IScope scope) throws GamaRuntimeException
	{
		
		Double timeStep = (scope.hasArg("timeStep")?(Double)scope.getArg("timeStep", IType.FLOAT):1.0);
		Integer velocityIterations = (scope.hasArg("velocityIterations")?(Integer)scope.getArg("velocityIterations", IType.INT):1);
		Integer positionIterations = (scope.hasArg("positionIterations")?(Integer)scope.getArg("positionIterations", IType.INT):1);
		_world.step(timeStep.floatValue(), velocityIterations, positionIterations);
		return null;
		
	}
	
	

}
