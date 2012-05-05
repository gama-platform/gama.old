package msi.gaml.extensions.physics;

import java.util.HashMap;

import org.jbox2d.collision.broadphase.Pair;
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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
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
import msi.gama.util.GamaPair;
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
@vars({ @var(name = "gravity", type = IType.POINT_STR, init = "{0.0, 0.0}"),
		@var(name = "registeredAgents", type = IType.LIST_STR, init = "[]") })
public class PhysicalWorldAgent extends GamlAgent {

	public final static String REGISTERED_AGENTS = "registeredAgents";
	private GamaPoint gravity = new GamaPoint(0, 10);
	private IList<IAgent> registeredAgents = null;
	private HashMap<IAgent, Body> registeredMap = new HashMap<IAgent, Body>();
	private World _world = new World(new Vec2((float) gravity.x,
			(float) gravity.y), true);

	public PhysicalWorldAgent(final ISimulation sim, final IPopulation s)
			throws GamaRuntimeException {
		super(sim, s);
	}

	@getter(var = "registeredAgents")
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

	public void registerAgent(IAgent _agent) {
		if (registeredAgents == null) {
			registeredAgents = new GamaList<IAgent>();
		}
		_registerAgent(_agent);
	}

	private PolygonShape[] GamaPolyToPolyPhysic1(IAgent geom) {

		System.out.println("Ici");
		IList<IShape> triangles = GeometryUtils.triangulation(geom
				.getInnerGeometry());
		PolygonShape trianglesShapes[] = new PolygonShape[triangles.size()];
		int i = 0;
		for (IShape tr : triangles) {
	//		if(i==0 )
				System.out.println("***********");
			trianglesShapes[i] = GamaTriangleToPolyPhysic(tr,(float) geom.getLocation().getX(), (float) geom
					.getLocation().getY());
	//		if(i==0)
			{
				for(Vec2 v : trianglesShapes[i].getVertices()){
					System.out.println(v.x + ";" +v.y);
				}

			}
			i++;
		}
		return trianglesShapes;

	}

	public PolygonShape GamaTriangleToPolyPhysic(IShape geom,float locx,float locy) {
		PolygonShape poly = new PolygonShape();
		Coordinate[] coords = geom.getInnerGeometry().getCoordinates();
//		Vec2[] vecs = new Vec2[coords.length];
		Vec2[] vecs = new Vec2[3];
		for (int i = 0; i < /**coords.length**/3; i++) {
			Coordinate coord = coords[i];
			Vec2 v = new Vec2((float) coord.x-locx, (float) coord.y-locy);
			vecs[i] = v;
		}
		System.out.println("Vertex number : " + vecs.length);
		poly.set(vecs, vecs.length);

		return poly;
	}
/*
	public PolygonShape GamaPolyToPolyPhysic(IShape geom) {

		PolygonShape poly = new PolygonShape();
		IList<IShape> triangles = GeometryUtils.triangulation(geom
				.getInnerGeometry());
		for (IShape tr : triangles) {
			Coordinate[] coords = tr.getInnerGeometry().getCoordinates();
			Vec2[] vecs = new Vec2[coords.length];
			for (int i = 0; i < coords.length; i++) {
				Coordinate coord = coords[i];
				Vec2 v = new Vec2((float) coord.x, (float) coord.y);
				vecs[i] = v;
			}
			System.out.println("Vertex number : " + vecs.length);
			poly.set(vecs, vecs.length);
		}
		return poly;

	}
*/
	private void cleanRegisteredAgents() {
		if (registeredAgents != null) {
			while (registeredAgents.size() > 0) {
				IAgent ia = registeredAgents.remove(0);
				Body bd = registeredMap.remove(ia);
				_world.destroyBody(bd);
			}
		}
	}

	private void setRegisteredAgentsToWorld() {
		if (registeredAgents != null) {
			for (IAgent ia : registeredAgents) {
				_registerAgent(ia);
			}
		}

	}
	
	

	private void _registerAgent(IAgent ia) {
		System.out.println("Registering : " + ia);
		// PolygonShape shape = GamaPolyToPolyPhysic(ia);
		// CircleShape shape = new CircleShape();
		// shape.m_radius = 1.0f;

		/**
		 * FixtureDef fd = new FixtureDef(); fd.shape = shape; fd.density =
		 * 1.0f;
		 **/
		// float restitution[] = {0.0f, 0.1f, 0.3f, 0.5f, 0.75f, 0.9f, 1.0f};

		float density = ((Double) ia.getAttribute("density")).floatValue();
		GamaPoint velocity = ((GamaPoint) ia.getAttribute("velocity"));


		BodyDef bd = new BodyDef();
		bd.position.set((float) ia.getLocation().getX(), (float) ia
				.getLocation().getY());
		bd.type = BodyType.DYNAMIC;

		Body body = _world.createBody(bd);

		PolygonShape[] triangles = GamaPolyToPolyPhysic1(ia);
		for (PolygonShape shape : triangles)
		body.createFixture(shape, density);

		body.setLinearVelocity(new Vec2((float) velocity.getX(),
				(float) velocity.getY()));
		registeredMap.put(ia, body);

	}

	public GamaPoint getGravity() {
		return gravity;
	}

	@setter("gravity")
	public void setGravity(final GamaPoint _gravity) {
		gravity = _gravity;
		_world.setGravity(new Vec2((float) gravity.x, (float) gravity.y));
	}

	public Body getBody(final IAgent agent) {
		return registeredMap.get(agent);
	}

	@action("computeForces")
	@args({ "timeStep", "velocityIterations", "port", "dbName", "usrName",
			"password" })
	public Object primComputeForces(final IScope scope)
			throws GamaRuntimeException {

		Double timeStep = (scope.hasArg("timeStep") ? (Double) scope.getArg(
				"timeStep", IType.FLOAT) : 1.0);
		Integer velocityIterations = (scope.hasArg("velocityIterations") ? (Integer) scope
				.getArg("velocityIterations", IType.INT) : 1);
		Integer positionIterations = (scope.hasArg("positionIterations") ? (Integer) scope
				.getArg("positionIterations", IType.INT) : 1);

		for (IAgent ia : registeredMap.keySet()) {
			Body body = registeredMap.get(ia);

			GamaPoint velocity = ((GamaPoint) ia.getAttribute("velocity"));
			Vec2 vel=new Vec2();
			vel.set((float)velocity.getX(),(float)velocity.getY());
			body.setLinearVelocity(vel);
			GamaPoint motor = ((GamaPoint) ia.getAttribute("motor"));
			Vec2 force=new Vec2();
			Vec2  pos=body.getPosition();
			force.set((float)motor.getX(),(float)motor.getY());
			body.applyForce(force, pos);
		}

		
		// TODO update positions of every agent to take into account external
		// movements
		// for machant
		_world.step(timeStep.floatValue(), velocityIterations,
				positionIterations);
		// Updates the location of the objects

		for (IAgent ia : registeredMap.keySet()) {
			Body body = registeredMap.get(ia);
			GamaPoint position = new GamaPoint(
					new Double(body.getPosition().x), new Double(
							body.getPosition().y));
			System.out.println("Position : " + position.getX() + " : "
					+ position.getY());
			ia.setLocation(position);
			
			GamaPoint velpoint = new GamaPoint(
					new Double(body.getLinearVelocity().x), new Double(
							body.getLinearVelocity().y));
			ia.setAttribute("velocity",velpoint);
		
		}
		return null;

	}

}
