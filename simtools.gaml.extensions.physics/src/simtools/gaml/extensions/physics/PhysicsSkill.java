package simtools.gaml.extensions.physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;


import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.MovingSkill;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;


@vars({@var(name = "physical_world", type = IType.AGENT_STR),
	@var(name = "density", type = IType.FLOAT_STR, init="1.0"),
	@var(name = "velocity", type = IType.POINT_STR, init="{0.0, 0.0}"),
	@var(name = "motor", type = IType.POINT_STR, init="{0.0, 0.0}")
	})
@skill("physical")
public class PhysicsSkill extends Skill{
	@setter("physical_world")
	public void setWorldAgent(final IAgent _agent, final IAgent _world){
		if(_world == null)
			return;
			
		PhysicalWorldAgent pwa = (PhysicalWorldAgent)_world;
		pwa.registerAgent(_agent);
	}

/**
	@action("update_physic")
	@args({ "physics_world" })
	public Object primGoToPhysics(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		PhysicalWorldAgent world = (PhysicalWorldAgent)scope.getArg("physics_world", IType.AGENT);
		System.out.println("World agent : " + world);
		Body body= world.getBody(agent);
		System.out.println("body agent : " + body);
		
		System.out.println("body Position : " + body.getPosition().x + " : "  + body.getPosition().y);
		GamaPoint position = new GamaPoint(new Double(body.getPosition().x), new Double(body.getPosition().y));
	      agent.setLocation(position);
	      System.out.println("Position : " + agent.getLocation().getX() + " : "  + agent.getLocation().getY());
	      
		return null;
	}
**/	
	/**
	@action("gotoTraffic")
	@args({ "target", IKeyword.SPEED, "on", "return_path", LIVING_SPACE, TOLERANCE, LANES_ATTRIBUTE })
	public IPath primGotoTraffic(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		ILocation source = agent.getLocation().copy();
		final double maxDist = computeDistance(scope, agent);
		final double tolerance = computeTolerance(scope, agent);
		final double livingSpace = computeLivingSpace(scope, agent);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope,agent);
		String laneAttributes = computeLanesNumber(scope, agent) ;
		if (laneAttributes == null || "".equals(laneAttributes))
			laneAttributes = "lanes_number";
		
		final ILocation goal = computeTarget(scope, agent);
		if ( goal == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if ( path == null || !path.getTopology().equals(topo) ||
			!path.getEndVertex().equals(goal) || !path.getStartVertex().equals(source) ) {
			path = topo.pathBetween(source, goal);
		}

		if ( path == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
		if ( returnPath != null && returnPath ) {
			IPath pathFollowed = moveToNextLocAlongPathTraffic(agent, path, maxDist,livingSpace, tolerance, laneAttributes, obsSpecies);
			if ( pathFollowed == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			scope.setStatus(ExecutionStatus.success);
			return pathFollowed;
		}
		moveToNextLocAlongPathSimplifiedTraffic(agent, path, maxDist,livingSpace, tolerance, laneAttributes, obsSpecies);
		scope.setStatus(ExecutionStatus.success);
		return null;
	}
**/
}
