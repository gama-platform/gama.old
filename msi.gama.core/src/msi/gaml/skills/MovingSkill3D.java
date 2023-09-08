/*******************************************************************************************************
 *
 * MovingSkill3D.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.skills;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gama.util.path.IPath;
import msi.gaml.operators.Maths;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;

/**
 * MovingSkill3D : This class is intended to define the minimal set of behaviours required from an agent that is able to
 * move. Each member that has a meaning in GAML is annotated with the respective tags (vars, getter, setter, init,
 * action & args)
 *
 * @author Arnaud Grignard
 */

@doc ("The moving skill 3D is intended to define the minimal set of behaviours required for agents that are able to move on different topologies")
@vars ({ @variable (
		name = IKeyword.SPEED,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc ("the speed of the agent (in meter/second)")),
		@variable (
				name = IKeyword.HEADING,
				type = IType.FLOAT,
				init = "rnd(360.0)",
				doc = @doc ("the absolute heading of the agent in degrees (in the range 0-359)")),
		@variable (
				name = IKeyword.PITCH,
				type = IType.FLOAT,
				init = "rnd(360.0)",
				doc = @doc ("the absolute pitch of the agent in degrees (in the range 0-359)")),
		@variable (
				name = IKeyword.ROLL,
				type = IType.FLOAT,
				init = "rnd(360.0)",
				doc = @doc ("the absolute roll of the agent in degrees (in the range 0-359)")),
		@variable (
				name = IKeyword.DESTINATION,
				type = IType.POINT,
				depends_on = { IKeyword.SPEED, IKeyword.HEADING, IKeyword.LOCATION },
				doc = @doc ("continuously updated destination of the agent with respect to its speed and heading (read-only)")) })
@skill (
		name = IKeyword.MOVING_3D_SKILL,
		concept = { IConcept.THREED, IConcept.SKILL })
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class MovingSkill3D extends MovingSkill {

	@Override
	@getter (IKeyword.DESTINATION)
	public GamaPoint getDestination(final IAgent agent) {
		final GamaPoint actualLocation = agent.getLocation();
		final double dist = this.computeDistance(agent.getScope(), agent);
		final ITopology topology = getTopology(agent);
		return topology.getDestination3D(actualLocation, getHeading(agent), getPitch(agent), dist, false);
	}

	/**
	 * Gets the pitch.
	 *
	 * @param agent the agent
	 * @return the pitch
	 */
	@getter (IKeyword.PITCH)
	public Double getPitch(final IAgent agent) {
		Double p = (Double) agent.getAttribute(IKeyword.PITCH);
		if (p == null) {
			p = agent.getScope().getRandom().next() * 360;
			setPitch(agent, p);
		}
		return Maths.checkHeading(p);
	}

	/**
	 * Sets the pitch.
	 *
	 * @param agent the agent
	 * @param newPitch the new pitch
	 */
	@setter (IKeyword.PITCH)
	public void setPitch(final IAgent agent, final double newPitch) {
		agent.setAttribute(IKeyword.PITCH, newPitch);
	}

	/**
	 * Gets the roll.
	 *
	 * @param agent the agent
	 * @return the roll
	 */
	@getter (IKeyword.ROLL)
	public Double getRoll(final IAgent agent) {
		Double r = (Double) agent.getAttribute(IKeyword.ROLL);
		if (r == null) {
			r = agent.getScope().getRandom().next() * 360;
			setRoll(agent, r);
		}
		return Maths.checkHeading(r);
	}

	/**
	 * Sets the roll.
	 *
	 * @param agent the agent
	 * @param newRoll the new roll
	 */
	@setter (IKeyword.ROLL)
	public void setRoll(final IAgent agent, final Double newRoll) {
		agent.setAttribute(IKeyword.ROLL, newRoll);
	}

	/**
	 * Compute pitch from amplitude.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @return the double
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	protected double computePitchFromAmplitude(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final int ampl = scope.hasArg("amplitude") ? scope.getIntArg("amplitude") : 359;
		setPitch(agent, getPitch(agent) + scope.getRandom().between(-ampl / 2, ampl / 2));
		return getPitch(agent);
	}

	/**
	 * Compute pitch.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @return the double
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	protected double computePitch(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Integer pitch = scope.hasArg(IKeyword.PITCH) ? scope.getIntArg(IKeyword.PITCH) : null;
		if (pitch != null) { setPitch(agent, pitch); }
		return getPitch(agent);
	}

	// FIXME: BOUNDS NOT WORKING YET
	@Override
	@action (
			name = "move",
			args = { @arg (
					name = IKeyword.SPEED,
					type = IType.FLOAT,
					optional = true,
					doc = @doc ("the speed to use for this move (replaces the current value of speed)")),
					@arg (
							name = IKeyword.HEADING,
							type = IType.INT,
							optional = true,
							doc = @doc ("int, optional, the direction to take for this move (replaces the current value of heading)")),
					@arg (
							name = IKeyword.PITCH,
							type = IType.INT,
							optional = true,
							doc = @doc ("int, optional, the direction to take for this move (replaces the current value of pitch)")),
					@arg (
							name = IKeyword.ROLL,
							type = IType.INT,
							optional = true,
							doc = @doc ("int, optional, the direction to take for this move (replaces the current value of roll)")),
					@arg (
							name = IKeyword.BOUNDS,
							type = IType.GEOMETRY,
							optional = true,
							doc = @doc ("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")) },
			doc = @doc (
					examples = { @example ("do move speed: speed - 10 heading: heading + rnd (30) bounds: agentA;") },
					value = "moves the agent forward, the distance being computed with respect to its speed and heading. The value of the corresponding variables are used unless arguments are passed."))
	public IPath primMoveForward(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final GamaPoint location = agent.getLocation();
		final double dist = computeDistance(scope, agent);
		final double heading = computeHeading(scope, agent);
		final double pitch = computePitch(scope, agent);
		final GamaPoint loc = scope.getTopology().getDestination3D(location, heading, pitch, dist, true);
		if (loc == null) {
			setHeading(agent, heading - 180);
			setPitch(agent, -pitch);
		} else {
			/*
			 * final Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE); if ( bounds != null ) { final IShape
			 * geom = GamaGeometryType.staticCast(scope, bounds, null); if ( geom != null && geom.getInnerGeometry() !=
			 * null ) { loc = computeLocationForward(scope, dist, loc, geom.getInnerGeometry()); } }
			 */
			setLocation(agent, loc);
			setHeading(agent, heading);
			setPitch(agent, pitch);
		}
		return null;
	}

	@Override
	public boolean primMoveRandomly(final IScope scope) throws GamaRuntimeException {

		final IAgent agent = getCurrentAgent(scope);
		final GamaPoint location = agent.getLocation();
		final double heading = computeHeadingFromAmplitude(scope, agent);
		final double pitch = computePitchFromAmplitude(scope, agent);
		final double dist = computeDistance(scope, agent);
		GamaPoint loc = scope.getTopology().getDestination3D(location, heading, pitch, dist, true);
		if (loc == null) {
			setHeading(agent, heading - 180);
			setPitch(agent, -pitch);
		} else {
			final Object on = scope.getArg(IKeyword.ON, IType.GRAPH);
			Double newHeading = null;
			if (on instanceof GamaSpatialGraph) {
				final GamaSpatialGraph graph = (GamaSpatialGraph) on;
				IMap<IShape, Double> probaDeplacement = null;
				if (scope.hasArg("proba_edges")) {
					probaDeplacement = (IMap<IShape, Double>) scope.getVarValue("proba_edges");
				}
				moveToNextLocAlongPathSimplified(scope, agent, graph, dist, probaDeplacement);
				return true;
			}
			final Object bounds = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			if (bounds != null) {
				IShape geom = GamaGeometryType.staticCast(scope, bounds, null, false);

				if (geom.getGeometries().size() > 1) {
					for (final IShape g : geom.getGeometries()) {
						if (g.euclidianDistanceTo(location) < 0.01) {
							geom = g;
							break;
						}
					}
				}
				if (geom.getInnerGeometry() != null) {
					final GamaPoint loc2 = computeLocationForward(scope, dist, loc, geom);
					if (!loc2.equals(loc)) {
						newHeading = heading - 180;
						loc = loc2;
					}
				}
			}
			setLocation(agent, loc);

			// only used for particuler case of bounded wandering
			if (newHeading != null) {
				setHeading(agent, newHeading);

			}

			// WARNING Pourquoi refaire un setHeading ici ??? C'est déjà fait dans setLocation(). Et en plus celui-ci
			// est incorrect.
			// WARNING Idem pour pitch. Cf. le commentaire dans primGoto ci-dessous. Il suffirait de redéfinir
			// setLocation(agent...) avec le calcul du "vrai" heading et du "vrai" pitch résultant de la
			// destination

			setHeading(agent, heading);
			setPitch(agent, pitch);
		}
		return true;
	}

	@Override
	public IPath primGoto(final IScope scope) throws GamaRuntimeException {

		// WARNING Je ne vois pas l'intérêt de redéfinir cette méthode. Il suffirait de redéfinir setLocation(IAgent...)
		// dans laquelle seraient mis les calculs effectués ci-dessous sur le heading et
		// le pitch, et cette méthode serait ainsi appelée par super.primGoto()

		final Object target = scope.getArg("target", IType.NONE);
		if (target == null) return null;
		final IAgent agent = getCurrentAgent(scope);
		final GamaPoint oldLocation = agent.getLocation();
		super.primGoto(scope);
		final GamaPoint newLocation = agent.getLocation();
		final GamaPoint diff = newLocation.minus(oldLocation);
		final int signumX = Maths.signum(diff.x);
		final int signumY = Maths.signum(diff.y);
		// Heading
		if (signumX == 0) {
			setHeading(agent, signumY == 0 ? 0 : signumY > 0 ? 90 : 270);
		} else {
			setHeading(agent, Math.atan(diff.y / diff.x) * Maths.toDeg + (signumX > 0 ? 0 : 180));
		}

		// Pitch
		if (signumX == 0 && signumY == 0) {
			final int signumZ = Maths.signum(diff.z);
			setPitch(agent, signumZ == 0 ? 0 : signumZ > 0 ? 90 : 270);
		} else {
			setPitch(agent, Math.atan(diff.z / Math.sqrt(diff.x * diff.x + diff.y * diff.y)) * Maths.toDeg);
		}

		return null;
	}
}
