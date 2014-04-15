/*********************************************************************************************
 * 
 * 
 * 'MovingSkill3D.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.skills;

import gnu.trove.map.hash.THashMap;
import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gama.util.path.*;
import msi.gaml.operators.*;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

/**
 * MovingSkill3D : This class is intended to define the minimal set of behaviours required from an
 * agent that is able to move. Each member that has a meaning in GAML is annotated with the
 * respective tags (vars, getter, setter, init, action & args)
 * 
 * @author Arnaud Grignard
 */

@doc("The moving skill 3D is intended to define the minimal set of behaviours required for agents that are able to move on different topologies")
@vars({
	@var(name = IKeyword.SPEED, type = IType.FLOAT, init = "1.0", doc = @doc("the speed of the agent (in meter/second)")),
	@var(name = IKeyword.HEADING, type = IType.INT, init = "rnd(359)", doc = @doc("the absolute heading of the agent in degrees (in the range 0-359)")),
	@var(name = IKeyword.PITCH, type = IType.INT, init = "rnd(359)", doc = @doc("the absolute pitch of the agent in degrees (in the range 0-359)")),
	@var(name = IKeyword.ROLL, type = IType.INT, init = "rnd(359)", doc = @doc("the absolute roll of the agent in degrees (in the range 0-359)")),
	@var(name = IKeyword.DESTINATION, type = IType.POINT, depends_on = { IKeyword.SPEED, IKeyword.HEADING,
		IKeyword.LOCATION }, doc = @doc("continuously updated destination of the agent with respect to its speed and heading (read-only)")) })
@skill(name = IKeyword.MOVING_3D_SKILL)
public class MovingSkill3D extends Skill {

	
	@getter(IKeyword.DESTINATION)
	public ILocation getDestination(final IAgent agent) {
		final ILocation actualLocation = agent.getLocation();
		final double dist = getSpeed(agent);
		final ITopology topology = getTopology(agent);
		return topology.getDestination3D(actualLocation, agent.getHeading(), agent.getPitch(),dist, false);
	}
	
	@setter(IKeyword.DESTINATION)
	public void setDestination(final IAgent agent, final ILocation p) {
		// READ_ONLY
	}
	
	@getter(IKeyword.SPEED)
	public double getSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(IKeyword.SPEED);
	}

	@setter(IKeyword.SPEED)
	public void setSpeed(final IAgent agent, final double s) {
		agent.setAttribute(IKeyword.SPEED, s);
		// scope.setAgentVarValue(agent, IKeyword.SPEED, s);
	}
	
	@getter(IKeyword.HEADING)
	public int getHeading(final IAgent agent) {
		return agent.getHeading();
	}

	@setter(IKeyword.HEADING)
	public void setHeading(final IAgent agent, final int heading) {
		agent.setHeading(heading);
	}


	
	@getter(IKeyword.PITCH)
	public int getPitch(final IAgent agent) {
		return agent.getPitch();
	}

	@setter(IKeyword.PITCH)
	public void setPitch(final IAgent agent, final int pitch) {
			agent.setPitch(pitch);
	}
	
	@getter(IKeyword.ROLL)
	public int getRoll(final IAgent agent) {
		return agent.getRoll();
	}

	@setter(IKeyword.ROLL)
	public void setRoll(final IAgent agent, final int roll) {
		agent.setRoll(roll);
	}
	
	/**
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException Prim: move randomly. Has to be redefined for every class that
	 *             implements this interface.
	 * 
	 * @param args the args speed (meter/sec) : the speed with which the agent wants to move
	 *            distance (meter) : the distance the agent want to cover in one step amplitude (in
	 *            degrees) : 360 or 0 means completely random move, while other values, combined
	 *            with the heading of the agent, define the angle in which the agent will choose a
	 *            new place. A bounds (geometry, agent, list of agents, list of geometries, species)
	 *            can be specified
	 * @return the path followed
	 */

	protected int computeHeadingFromAmplitude(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final int ampl = scope.hasArg("amplitude") ? scope.getIntArg("amplitude") : 359;
		agent.setHeading(agent.getHeading() + scope.getRandom().between(-ampl / 2, ampl / 2));
		return agent.getHeading();
	}

	protected int computeHeading(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Integer heading = scope.hasArg(IKeyword.HEADING) ? scope.getIntArg(IKeyword.HEADING) : null;
		if ( heading != null ) {
			agent.setHeading(heading);
		}
		return agent.getHeading();
	}

	protected double computeDistance(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		// We do not change the speed of the agent anymore. Only the current primitive is affected
		final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		// 20/1/2012 Change : The speed of the agent is multiplied by the timestep in order to
		// obtain the maximal distance it can cover in one step.
		return s * scope.getClock().getStep();
	}

	protected int computePitchFromAmplitude(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final int ampl = scope.hasArg("amplitude") ? scope.getIntArg("amplitude") : 359;
		agent.setPitch(agent.getPitch() + scope.getRandom().between(-ampl / 2, ampl / 2));
		return agent.getPitch();
	}

	protected int computePitch(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Integer pitch = scope.hasArg(IKeyword.PITCH) ? scope.getIntArg(IKeyword.PITCH) : null;
		if ( pitch != null ) {
			agent.setPitch(pitch);
		}
		return agent.getPitch();
	}


	//FIXME: BOUNDS NOT WORKING YET
	@action(name = "move", args = {
			@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = IKeyword.HEADING, type = IType.INT, optional = true, doc = @doc("int, optional, the direction to take for this move (replaces the current value of heading)")),
			@arg(name = IKeyword.PITCH, type = IType.INT, optional = true, doc = @doc("int, optional, the direction to take for this move (replaces the current value of pitch)")),
			@arg(name = IKeyword.HEADING, type = IType.INT, optional = true, doc = @doc("int, optional, the direction to take for this move (replaces the current value of roll)")),
			@arg(name = IKeyword.BOUNDS, type = { IType.GEOMETRY, IType.AGENT }, optional = true, doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")) }, 
			doc = @doc(examples = { @example("do move speed: speed - 10 heading: heading + rnd (30) bounds: agentA;") }, 
			value = "moves the agent forward, the distance being computed with respect to its speed and heading. The value of the corresponding variables are used unless arguments are passed."))
	public IPath primMoveForward3D(final IScope scope) throws GamaRuntimeException {
			final IAgent agent = getCurrentAgent(scope);
			final ILocation location = agent.getLocation();
			final double dist = computeDistance(scope, agent);
			final int heading = computeHeading(scope, agent);
			final int pitch = computePitch(scope,agent);
			ILocation loc = scope.getTopology().getDestination3D(location, heading, pitch ,dist, true);
				
			if ( loc == null || loc.getZ() > 100 || loc.getZ() < 0) {
				agent.setHeading(heading - 180);
				agent.setPitch(-pitch);
			} else {
				/*final Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
				if ( bounds != null ) {
					final IShape geom = GamaGeometryType.staticCast(scope, bounds, null);
					if ( geom != null && geom.getInnerGeometry() != null ) {
						loc = computeLocationForward(scope, dist, loc, geom.getInnerGeometry());
					}
				}*/
				agent.setLocation(loc);
				agent.setHeading(heading);
				agent.setPitch(pitch);
			}
			return null;
		}

	@action(name = "wander", args = {
		@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "amplitude", type = IType.INT, optional = true, doc = @doc("a restriction placed on the random heading choice. The new heading is chosen in the range (heading - amplitude/2, heading+amplitude/2)")),
		@arg(name = IKeyword.BOUNDS, type = { IType.AGENT, IType.GEOMETRY }, optional = true, doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")) }, doc = @doc(examples = { @example("do wander speed: speed - 10 amplitude: 120 bounds: agentA;") }, value = "Moves the agent towards a random location at the maximum distance (with respect to its speed). The heading of the agent is chosen randomly if no amplitude is specified. This action changes the value of heading."))
	public void primMove3DRandomly(final IScope scope) throws GamaRuntimeException {

		final IAgent agent = getCurrentAgent(scope);
		final ILocation location = agent.getLocation();
		final int heading = computeHeadingFromAmplitude(scope, agent);
		final int pitch = computePitchFromAmplitude(scope,agent);
		final double dist = computeDistance(scope, agent);

		ILocation loc = scope.getTopology().getDestination3D(location, heading, pitch, dist, true);

		if ( loc == null || loc.getZ() > 100 || loc.getZ() < 0) {
			agent.setHeading(heading - 180);
			agent.setPitch(-pitch);
		} else {
			/*final Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			if ( bounds != null ) {
				final IShape geom = GamaGeometryType.staticCast(scope, bounds, null);
				if ( geom != null && geom.getInnerGeometry() != null ) {
					loc = computeLocationForward(scope, dist, loc, geom.getInnerGeometry());
				}
			}*/
			agent.setLocation(loc);
			agent.setHeading(heading);
			agent.setPitch(pitch);
		}
	}
}
