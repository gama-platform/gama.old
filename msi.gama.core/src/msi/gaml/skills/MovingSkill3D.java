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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.ITopology;
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
import msi.gama.util.path.IPath;
import msi.gaml.operators.Maths;
import msi.gaml.types.IType;
import msi.gama.metamodel.shape.IShape;

/**
 * MovingSkill3D : This class is intended to define the minimal set of behaviours required from an
 * agent that is able to move. Each member that has a meaning in GAML is annotated with the
 * respective tags (vars, getter, setter, init, action & args)
 * 
 * @author Arnaud Grignard
 */

@doc("The moving skill 3D is intended to define the minimal set of behaviours required for agents that are able to move on different topologies")
@vars({
	@var(name = IKeyword.SPEED,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc("the speed of the agent (in meter/second)")),
	@var(name = IKeyword.HEADING,
		type = IType.INT,
		init = "rnd(359)",
		doc = @doc("the absolute heading of the agent in degrees (in the range 0-359)")),
	@var(name = IKeyword.PITCH,
		type = IType.INT,
		init = "rnd(359)",
		doc = @doc("the absolute pitch of the agent in degrees (in the range 0-359)")),
	@var(name = IKeyword.ROLL,
		type = IType.INT,
		init = "rnd(359)",
		doc = @doc("the absolute roll of the agent in degrees (in the range 0-359)")),
	@var(name = IKeyword.DESTINATION,
		type = IType.POINT,
		depends_on = { IKeyword.SPEED, IKeyword.HEADING, IKeyword.LOCATION },
		doc = @doc("continuously updated destination of the agent with respect to its speed and heading (read-only)")) })
@skill(name = IKeyword.MOVING_3D_SKILL)
public class MovingSkill3D extends MovingSkill {

	@Override
	@getter(IKeyword.DESTINATION)
	public ILocation getDestination(final IAgent agent) {
		final ILocation actualLocation = agent.getLocation();
		final double dist = getSpeed(agent);
		final ITopology topology = getTopology(agent);
		return topology.getDestination3D(actualLocation, getHeading(agent), getPitch(agent), dist, false);
	}

	@getter(IKeyword.PITCH)
	public Integer getPitch(final IAgent agent) {
		Integer p = (Integer) agent.getAttribute(IKeyword.PITCH);
		if ( p == null ) {
			p = agent.getScope().getRandom().between(0, 359);
			setPitch(agent, p);
		}
		return Maths.checkHeading(p);
	}

	@setter(IKeyword.PITCH)
	public void setPitch(final IAgent agent, final Integer newPitch) {
		agent.setAttribute(IKeyword.PITCH, newPitch);
	}

	@getter(IKeyword.ROLL)
	public Integer getRoll(final IAgent agent) {
		Integer r = (Integer) agent.getAttribute(IKeyword.ROLL);
		if ( r == null ) {
			r = RandomUtils.getDefault().between(0, 359);
			setRoll(agent, r);
		}
		return Maths.checkHeading(r);
	}

	@setter(IKeyword.ROLL)
	public void setRoll(final IAgent agent, final Integer newRoll) {
		agent.setAttribute(IKeyword.ROLL, newRoll);
	}

	protected int computePitchFromAmplitude(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final int ampl = scope.hasArg("amplitude") ? scope.getIntArg("amplitude") : 359;
		setPitch(agent, getPitch(agent) + scope.getRandom().between(-ampl / 2, ampl / 2));
		return getPitch(agent);
	}

	protected int computePitch(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Integer pitch = scope.hasArg(IKeyword.PITCH) ? scope.getIntArg(IKeyword.PITCH) : null;
		if ( pitch != null ) {
			setPitch(agent, pitch);
		}
		return getPitch(agent);
	}

	// FIXME: BOUNDS NOT WORKING YET
	@Override
	@action(name = "move",
		args = {
			@arg(name = IKeyword.SPEED,
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = IKeyword.HEADING,
				type = IType.INT,
				optional = true,
				doc = @doc("int, optional, the direction to take for this move (replaces the current value of heading)")),
			@arg(name = IKeyword.PITCH,
				type = IType.INT,
				optional = true,
				doc = @doc("int, optional, the direction to take for this move (replaces the current value of pitch)")),
			@arg(name = IKeyword.HEADING,
				type = IType.INT,
				optional = true,
				doc = @doc("int, optional, the direction to take for this move (replaces the current value of roll)")),
			@arg(name = IKeyword.BOUNDS,
				type = { IType.GEOMETRY, IType.AGENT },
				optional = true,
				doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")) },
		doc = @doc(examples = { @example("do move speed: speed - 10 heading: heading + rnd (30) bounds: agentA;") },
			value = "moves the agent forward, the distance being computed with respect to its speed and heading. The value of the corresponding variables are used unless arguments are passed."))
	public
		IPath primMoveForward(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ILocation location = agent.getLocation();
		final double dist = computeDistance(scope, agent);
		final int heading = computeHeading(scope, agent);
		final int pitch = computePitch(scope, agent);
		ILocation loc = scope.getTopology().getDestination3D(location, heading, pitch, dist, true);		
		GamaShape g = (GamaShape)scope.getSimulationScope().getGeometry();
		g.getDepth();
		if ( loc == null ||  loc.getZ() > g.getDepth() || loc.getZ() < 0 ) {
			setHeading(agent, heading - 180);
			setPitch(agent, -pitch);
		} else {
			/*
			 * final Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			 * if ( bounds != null ) {
			 * final IShape geom = GamaGeometryType.staticCast(scope, bounds, null);
			 * if ( geom != null && geom.getInnerGeometry() != null ) {
			 * loc = computeLocationForward(scope, dist, loc, geom.getInnerGeometry());
			 * }
			 * }
			 */
			setLocation(agent, loc);
			setHeading(agent, heading);
			setPitch(agent, pitch);
		}
		return null;
	}

	@Override
	@action(name = "wander",
		args = {
			@arg(name = IKeyword.SPEED,
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "amplitude",
				type = IType.INT,
				optional = true,
				doc = @doc("a restriction placed on the random heading choice. The new heading is chosen in the range (heading - amplitude/2, heading+amplitude/2)")),
			@arg(name = IKeyword.BOUNDS,
				type = { IType.AGENT, IType.GEOMETRY },
				optional = true,
				doc = @doc("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")) },
		doc = @doc(examples = { @example("do wander speed: speed - 10 amplitude: 120 bounds: agentA;") },
			value = "Moves the agent towards a random location at the maximum distance (with respect to its speed). The heading of the agent is chosen randomly if no amplitude is specified. This action changes the value of heading."))
	public
		void primMoveRandomly(final IScope scope) throws GamaRuntimeException {

		final IAgent agent = getCurrentAgent(scope);
		final ILocation location = agent.getLocation();
		final int heading = computeHeadingFromAmplitude(scope, agent);
		final int pitch = computePitchFromAmplitude(scope, agent);
		final double dist = computeDistance(scope, agent);

		ILocation loc = scope.getTopology().getDestination3D(location, heading, pitch, dist, true);

		// TODO Remove the 100
		if ( loc == null || loc.getZ() > 100 || loc.getZ() < 0 ) {
			setHeading(agent, heading - 180);
			setPitch(agent, -pitch);
		} else {
			/*
			 * final Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			 * if ( bounds != null ) {
			 * final IShape geom = GamaGeometryType.staticCast(scope, bounds, null);
			 * if ( geom != null && geom.getInnerGeometry() != null ) {
			 * loc = computeLocationForward(scope, dist, loc, geom.getInnerGeometry());
			 * }
			 * }
			 */
			setLocation(agent, loc);
			setHeading(agent, heading);
			setPitch(agent, pitch);
		}
	}
}
