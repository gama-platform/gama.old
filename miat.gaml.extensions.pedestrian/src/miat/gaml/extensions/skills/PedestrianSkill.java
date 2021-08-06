package miat.gaml.extensions.skills;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GraphTopology;
import msi.gama.metamodel.topology.graph.ISpatialGraph;
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
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.path.IPath;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Points;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Creation;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@skill (
		name = "pedestrian",
		concept = { IConcept.TRANSPORT, IConcept.SKILL },
		doc = @doc ("A skill that provides agent with the ability to walk on continuous space while"
				+ " finding their way on a virtual network"))
@vars ({ @variable (
		name = "shoulder_length",
		type = IType.FLOAT,
		init = "0.45",
		doc = @doc ("The width of the pedestrian (in meters) - classic values: [0.39, 0.515]")),
		@variable (
				name = "minimal_distance",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("Minimal distance between pedestrians")),
		@variable (
				name = "pedestrian_consideration_distance",
				type = IType.FLOAT,
				init = "2.0",
				doc = @doc ("Distance of consideration of other pedestrians (to compute the nearby obstacles, used as distance, the max between this value and (step * speed) - classic value: 3.5m")),
		@variable (
				name = "obstacle_consideration_distance",
				type = IType.FLOAT,
				init = "2.0",
				doc = @doc ("Distance of consideration of obstacles (to compute the nearby obstacles, used as distance, the max between this value and (step * speed) - classic value: 3.5m")),
		@variable (
				name = "avoid_other",
				type = IType.BOOL,
				init = "true",
				doc = @doc ("has the pedestrian to avoid other pedestrians?")),
		@variable (
				name = "obstacle_species",
				type = IType.LIST,
				init = "[]",
				doc = @doc ("the list of species that are considered as obstacles")),
		@variable (
				name = "pedestrian_species",
				type = IType.LIST,
				init = "[]",
				doc = @doc ("the list of species that are considered as pedestrians")),
		@variable (
				name = "proba_detour",
				type = IType.FLOAT,
				init = "0.1",
				doc = @doc ("probability to accept to do a detour")),
		@variable (
				name = "A_pedestrians_SFM",
				type = IType.FLOAT,
				init = "4.5",
				doc = @doc ("Value of A in the SFM model for pedestrians - the force of repulsive interactions (classic values : mean = 4.5, std = 0.3)")),
		@variable (
				name = "A_obstacles_SFM",
				type = IType.FLOAT,
				init = "4.5",
				doc = @doc ("Value of A in the SFM model for obstacles - the force of repulsive interactions (classic values : mean = 4.5, std = 0.3)")),
		@variable (
				name = "B_pedestrians_SFM",
				type = IType.FLOAT,
				init = "2.0",
				doc = @doc ("Value of B in the SFM model for pedestrians - the range (in meters) of repulsive interactions")),
		@variable (
				name = "B_obstacles_SFM",
				type = IType.FLOAT,
				init = "2.0",
				doc = @doc ("Value of B in the SFM model for obstacles - the range (in meters) of repulsive interactions")),
		@variable (
				name = "k_SFM",
				type = IType.FLOAT,
				init = "200",
				doc = @doc ("Value of k in the SFM model: force counteracting body compression")),
		@variable (
				name = "kappa_SFM",
				type = IType.FLOAT,
				init = "400",
				doc = @doc ("Value of kappa in the SFM model: friction counteracting body compression")),
		@variable (
				name = "relaxion_SFM",
				type = IType.FLOAT,
				init = "0.54",
				doc = @doc ("Value of relaxion in the SFM model - the amount of delay time for an agent to adapt.(classic values : mean = 0.54, std = 0.05)")),
		@variable (
				name = "gama_SFM",
				type = IType.FLOAT,
				init = "0.35",
				doc = @doc ("Value of gama in the SFM model  the amount of normal social force added in tangential direction. between 0.0 and 1.0 (classic values : mean = 0.35, std = 0.01)")),
		@variable (
				name = "lambda_SFM",
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc ("Value of lambda in the SFM model - the (an-)isotropy (between 0.0 and 1.0)")),
		@variable (
				name = "n_SFM",
				type = IType.FLOAT,
				init = "2.0",
				doc = @doc ("Value of n in the SFM model (classic values : mean = 2.0, std = 0.1)")),
		@variable (
				name = "n_prime_SFM",
				type = IType.FLOAT,
				init = "3.0",
				doc = @doc ("Value of n\' in the SFM model (classic values : mean = 3.0, std = 0.7)")),
		@variable (
				name = "pedestrian_model",
				type = IType.STRING,
				init = "'simple'",
				doc = @doc ("Model use for the movement of agents (Social Force Model). Can be either \"simple\" "
						+ "or \"advanced\" (default) for different versions of SFM Helbing model")),

		@variable (
				name = "velocity",
				type = IType.POINT,
				init = "{0,0,0}",
				doc = @doc ("The velocity of the pedestrian (in meters)")),
		@variable (
				name = "forces",
				type = IType.MAP,
				init = "[]",
				doc = @doc ("the map of forces")),
		@variable (
				name = "final_waypoint",
				type = IType.GEOMETRY,
				init = "nil",
				doc = @doc ("the final waypoint of the agent")),
		@variable (
				name = "current_waypoint",
				type = IType.GEOMETRY,
				init = "nil",
				doc = @doc ("the current waypoint of the agent")),
		@variable (
				name = "current_index",
				type = IType.INT,
				init = "0",
				doc = @doc ("the current index of the agent waypoint (according to the waypoint list)")),
		@variable (
				name = "waypoints",
				type = IType.LIST,
				of = IType.GEOMETRY,
				init = "[]",
				doc = @doc ("the current list of points/shape that the agent has to reach (path)")),
		@variable (
				name = "roads_waypoints",
				type = IType.MAP,
				init = "[]",
				doc = @doc ("for each waypoint, the associated road")),
		@variable (
				name = "use_geometry_waypoint",
				type = IType.BOOL,
				init = "false",
				doc = @doc ("use geometries as waypoint instead of points")),
		@variable (
				name = "tolerance_waypoint",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("distance to a waypoint (in meters) to consider that an agent is arrived at the waypoint"))

})
public class PedestrianSkill extends MovingSkill {

	// ---------- CONSTANTS -------------- //

	// General mode of walking
	public final static String PEDESTRIAN_MODEL = "pedestrian_model";

	public final static String SHOULDER_LENGTH = "shoulder_length";
	public final static String MINIMAL_DISTANCE = "minimal_distance";

	public final static String CURRENT_TARGET = "current_waypoint";
	public final static String OBSTACLE_CONSIDERATION_DISTANCE = "obstacle_consideration_distance";
	public final static String PEDESTRIAN_CONSIDERATION_DISTANCE = "pedestrian_consideration_distance";
	public final static String PROBA_DETOUR = "proba_detour";
	public final static String AVOID_OTHER = "avoid_other";
	public final static String OBSTACLE_SPECIES = "obstacle_species";
	public final static String PEDESTRIAN_SPECIES = "pedestrian_species";
	public final static String VELOCITY = "velocity";
	public final static String FORCES = "forces";

	public final static String A_PEDESTRIAN_SFM = "A_pedestrians_SFM";
	public final static String A_OBSTACLES_SFM = "A_obstacles_SFM";
	public final static String B_PEDESTRIAN_SFM = "B_pedestrians_SFM";
	public final static String B_OBSTACLES_SFM = "B_obstacles_SFM";

	public final static String K_SFM = "k_SFM";
	public final static String KAPPA_SFM = "kappa_SFM";
	public final static String RELAXION_SFM = "relaxion_SFM";
	public final static String GAMA_SFM = "gama_SFM";
	public final static String lAMBDA_SFM = "lambda_SFM";

	public final static String N_SFM = "n_SFM";
	public final static String N_PRIME_SFM = "n_prime_SFM";

	public final static String CURRENT_TARGET_GEOM = "current_waypoint_geom";
	public final static String CURRENT_INDEX = "current_index";
	public final static String FINAL_TARGET = "final_waypoint";
	public final static String CURRENT_PATH = "current_path";
	public final static String PEDESTRIAN_GRAPH = "pedestrian_graph";
	public final static String TOLERANCE_TARGET = "tolerance_waypoint";

	public final static String USE_GEOMETRY_TARGET = "use_geometry_waypoint";

	// ACTION
	public final static String COMPUTE_VIRTUAL_PATH = "compute_virtual_path";
	public final static String WALK = "walk";
	public final static String WALK_TO = "walk_to";
	// ---------- VARIABLES GETTER AND SETTER ------------- //

	public final static String TARGETS = "waypoints";
	public final static String ROADS_TARGET = "roads_waypoints";

	@getter (SHOULDER_LENGTH)
	public double getShoulderLength(final IAgent agent) {
		return (Double) agent.getAttribute(SHOULDER_LENGTH);
	}

	@getter (FORCES)
	public IMap<IShape, GamaPoint> getForces(final IAgent agent) {
		return (IMap<IShape, GamaPoint>) agent.getAttribute(FORCES);
	}

	@setter (SHOULDER_LENGTH)
	public void setShoulderLength(final IAgent agent, final double s) {
		agent.setAttribute(SHOULDER_LENGTH, s);
	}

	@getter (MINIMAL_DISTANCE)
	public double getMinDist(final IAgent agent) {
		return (Double) agent.getAttribute(MINIMAL_DISTANCE);
	}

	@setter (MINIMAL_DISTANCE)
	public void setMinDist(final IAgent agent, final double s) {
		agent.setAttribute(MINIMAL_DISTANCE, s);
	}

	@getter (K_SFM)
	public double getKSFM(final IAgent agent) {
		return (Double) agent.getAttribute(K_SFM);
	}

	@setter (K_SFM)
	public void setKSFM(final IAgent agent, final double s) {
		agent.setAttribute(K_SFM, s);
	}

	@getter (KAPPA_SFM)
	public double getKappaSFM(final IAgent agent) {
		return (Double) agent.getAttribute(KAPPA_SFM);
	}

	@setter (KAPPA_SFM)
	public void setKappaSFM(final IAgent agent, final double s) {
		agent.setAttribute(KAPPA_SFM, s);
	}

	@setter (N_PRIME_SFM)
	public void setN_PRIME_SFM(final IAgent agent, final Double val) {
		agent.setAttribute(N_PRIME_SFM, val);
	}

	@getter (N_PRIME_SFM)
	public Double getN_PRIME_SFM(final IAgent agent) {
		return (Double) agent.getAttribute(N_PRIME_SFM);
	}

	@setter (N_SFM)
	public void setN_SFM(final IAgent agent, final Double val) {
		agent.setAttribute(N_SFM, val);
	}

	@getter (N_SFM)
	public Double getN_SFM(final IAgent agent) {
		return (Double) agent.getAttribute(N_SFM);
	}

	@getter (OBSTACLE_SPECIES)
	public GamaList<ISpecies> getObstacleSpecies(final IAgent agent) {
		return (GamaList<ISpecies>) agent.getAttribute(OBSTACLE_SPECIES);
	}

	@setter (OBSTACLE_SPECIES)
	public void setObstacleSpecies(final IAgent agent, final GamaList<ISpecies> os) {
		agent.setAttribute(OBSTACLE_SPECIES, os);
	}

	@getter (PEDESTRIAN_SPECIES)
	public GamaList<ISpecies> getPedestrianSpecies(final IAgent agent) {
		return (GamaList<ISpecies>) agent.getAttribute(PEDESTRIAN_SPECIES);
	}

	@setter (PEDESTRIAN_SPECIES)
	public void setPedestrianSpecies(final IAgent agent, final GamaList<ISpecies> os) {
		agent.setAttribute(PEDESTRIAN_SPECIES, os);
	}

	@getter (CURRENT_TARGET)
	public IShape getCurrentTarget(final IAgent agent) {
		return (IShape) agent.getAttribute(CURRENT_TARGET);
	}

	@setter (CURRENT_TARGET)
	public void setCurrentTarget(final IAgent agent, final IShape point) {
		agent.setAttribute(CURRENT_TARGET, point);
	}

	@getter (OBSTACLE_CONSIDERATION_DISTANCE)
	public Double getObstacleConsiderationDistance(final IAgent agent) {
		return (Double) agent.getAttribute(OBSTACLE_CONSIDERATION_DISTANCE);
	}

	@setter (OBSTACLE_CONSIDERATION_DISTANCE)
	public void setObstacleConsiderationDistance(final IAgent agent, final Double val) {
		agent.setAttribute(OBSTACLE_CONSIDERATION_DISTANCE, val);
	}

	@getter (PEDESTRIAN_CONSIDERATION_DISTANCE)
	public Double getPedestrianConsiderationDistance(final IAgent agent) {
		return (Double) agent.getAttribute(PEDESTRIAN_CONSIDERATION_DISTANCE);
	}

	@setter (PEDESTRIAN_CONSIDERATION_DISTANCE)
	public void setPedestrianConsiderationDistance(final IAgent agent, final Double val) {
		agent.setAttribute(PEDESTRIAN_CONSIDERATION_DISTANCE, val);
	}

	@getter (PROBA_DETOUR)
	public Double getProbaDetour(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_DETOUR);
	}

	@setter (lAMBDA_SFM)
	public void setlAMBDA_SFM(final IAgent agent, final Double val) {
		agent.setAttribute(lAMBDA_SFM, val);
	}

	@getter (lAMBDA_SFM)
	public Double getlAMBDA_SFM(final IAgent agent) {
		return (Double) agent.getAttribute(lAMBDA_SFM);
	}

	@setter (GAMA_SFM)
	public void setGAMA_SFM(final IAgent agent, final Double val) {
		agent.setAttribute(GAMA_SFM, val);
	}

	@getter (GAMA_SFM)
	public Double getGAMA_SFM(final IAgent agent) {
		return (Double) agent.getAttribute(GAMA_SFM);
	}

	@setter (PROBA_DETOUR)
	public void setProbaDetour(final IAgent agent, final Double val) {
		agent.setAttribute(PROBA_DETOUR, val);
	}

	@getter (RELAXION_SFM)
	public Double getRELAXION_SFM(final IAgent agent) {
		return (Double) agent.getAttribute(RELAXION_SFM);
	}

	@setter (RELAXION_SFM)
	public void setRELAXION_SFM(final IAgent agent, final Double val) {
		agent.setAttribute(RELAXION_SFM, val);
	}

	@getter (A_PEDESTRIAN_SFM)
	public Double getAPedestrian_SFM(final IAgent agent) {
		return (Double) agent.getAttribute(A_PEDESTRIAN_SFM);
	}

	@setter (A_PEDESTRIAN_SFM)
	public void setAPedestrian_SFM(final IAgent agent, final Double val) {
		agent.setAttribute(A_PEDESTRIAN_SFM, val);
	}

	@getter (A_OBSTACLES_SFM)
	public Double getAObstSFM(final IAgent agent) {
		return (Double) agent.getAttribute(A_OBSTACLES_SFM);
	}

	@setter (A_OBSTACLES_SFM)
	public void setAObstSFM(final IAgent agent, final Double val) {
		agent.setAttribute(A_OBSTACLES_SFM, val);
	}

	@getter (B_PEDESTRIAN_SFM)
	public Double getB_SFM(final IAgent agent) {
		return (Double) agent.getAttribute(B_PEDESTRIAN_SFM);
	}

	@setter (B_PEDESTRIAN_SFM)
	public void setB_SFM(final IAgent agent, final Double val) {
		agent.setAttribute(B_PEDESTRIAN_SFM, val);
	}

	@getter (B_OBSTACLES_SFM)
	public Double getBObstSFM(final IAgent agent) {
		return (Double) agent.getAttribute(B_OBSTACLES_SFM);
	}

	@setter (B_OBSTACLES_SFM)
	public void setBObstSFM(final IAgent agent, final Double val) {
		agent.setAttribute(B_OBSTACLES_SFM, val);
	}

	@getter (AVOID_OTHER)
	public Boolean getAvoidOther(final IAgent agent) {
		return (Boolean) agent.getAttribute(AVOID_OTHER);
	}

	@setter (AVOID_OTHER)
	public void setAvoidOther(final IAgent agent, final Boolean val) {
		agent.setAttribute(AVOID_OTHER, val);
	}

	@setter (VELOCITY)
	public void setVelocity(final IAgent agent, final GamaPoint val) {
		agent.setAttribute(VELOCITY, val);
	}

	@getter (VELOCITY)
	public GamaPoint getVelocity(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(VELOCITY);
	}

	@getter (TOLERANCE_TARGET)
	public double getToleranceTarget(final IAgent agent) {
		return (Double) agent.getAttribute(TOLERANCE_TARGET);
	}

	@setter (TOLERANCE_TARGET)
	public void setToleranceTarget(final IAgent agent, final double s) {
		agent.setAttribute(TOLERANCE_TARGET, s);
	}

	@getter (TARGETS)
	public IList<IShape> getTargets(final IAgent agent) {
		return (IList<IShape>) agent.getAttribute(TARGETS);
	}

	@getter (ROADS_TARGET)
	public IMap getRoadsTargets(final IAgent agent) {
		return (IMap) agent.getAttribute(ROADS_TARGET);
	}

	@setter (TARGETS)
	public void setTargets(final IAgent agent, final IList<IShape> points) {
		agent.setAttribute(TARGETS, points);
	}

	@getter (FINAL_TARGET)
	public IShape getFinalTarget(final IAgent agent) {
		return (IShape) agent.getAttribute(FINAL_TARGET);
	}

	@setter (FINAL_TARGET)
	public void setFinalTarget(final IAgent agent, final IShape point) {
		agent.setAttribute(FINAL_TARGET, point);
	}

	@getter (CURRENT_INDEX)
	public Integer getCurrentIndex(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_INDEX);
	}

	@setter (CURRENT_INDEX)
	public void setCurrentIndex(final IAgent agent, final Integer index) {
		agent.setAttribute(CURRENT_INDEX, index);
	}

	@getter (USE_GEOMETRY_TARGET)
	public Boolean getUseGeometryTarget(final IAgent agent) {
		return (Boolean) agent.getAttribute(USE_GEOMETRY_TARGET);
	}

	@setter (USE_GEOMETRY_TARGET)
	public void setUseGeometryTarget(final IAgent agent, final Boolean val) {
		agent.setAttribute(USE_GEOMETRY_TARGET, val);
	}

	@getter (PEDESTRIAN_MODEL)
	public String getPedestrianModel(final IAgent agent) {
		return (String) agent.getAttribute(PEDESTRIAN_MODEL);
	}

	@setter (PEDESTRIAN_MODEL)
	public void setPedestrianModel(final IAgent agent, final String val) {
		if ("advanced".equals(val) || "simple".equals(val)) {
			agent.setAttribute(PEDESTRIAN_MODEL, val);
		} else {
			GamaRuntimeException.error(
					"" + val + " is not a possible value for pedestrian model; possible values: ['simple', 'advanced']",
					agent.getScope());
		}
	}

	// ----------------------------------- //

	@action (
			name = WALK_TO,
			args = { @arg (
					name = "target",
					type = IType.GEOMETRY,
					optional = false,
					doc = @doc ("Move toward the target using the SFM model")),
					@arg (
							name = IKeyword.BOUNDS,
							type = IType.GEOMETRY,
							optional = true,
							doc = @doc ("the geometry (the localized entity geometry) that restrains this move (the agent moves inside this geometry")),

			},

			doc = @doc (
					value = "action to walk toward a target",
					examples = { @example ("do walk_to {10,10};") }))
	public void primWalkTo(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		if (agent == null || agent.dead()) return;
		IShape goal = computeTarget(scope, agent);
		if (goal == null) return;
		IShape bounds = null;
		if (scope.hasArg(IKeyword.BOUNDS)) {
			final Object obj = scope.getArg(IKeyword.BOUNDS, IType.NONE);
			bounds = GamaGeometryType.staticCast(scope, obj, null, false);
		}
		IList<ISpecies> speciesList = getObstacleSpecies(agent);
		IContainer obstacles = null;
		if (speciesList.size() == 1) {
			obstacles = speciesList.get(0);
		} else {
			obstacles = GamaListFactory.create(Types.AGENT);
			for (ISpecies species : speciesList) {

				((IList<IAgent>) obstacles).addAll(Cast.asList(scope, species));
			}
		}

		speciesList = getPedestrianSpecies(agent);
		IContainer pedestrians = null;
		if (speciesList.size() == 1) {
			pedestrians = speciesList.get(0);
		} else {
			pedestrians = GamaListFactory.create(Types.AGENT);
			for (ISpecies species : speciesList) {

				((IList<IAgent>) pedestrians).addAll(Cast.asList(scope, species));
			}
		}

		GamaPoint currentTarget = goal.getLocation();
		double maxDist = computeDistance(scope, agent);
		double realSpeed = walkWithForceModel(scope, agent, currentTarget, getAvoidOther(agent), bounds, pedestrians,
				obstacles, maxDist);

		setRealSpeed(agent, realSpeed);

	}

	/**
	 * General walking dynamic with force based avoidance
	 *
	 * @param scope
	 * @param agent
	 * @param currentTarget
	 * @param avoidOther
	 * @param bounds
	 * @param pedestrianList
	 * @param obstaclesList
	 * @param maxDist
	 * @return
	 */
	public double walkWithForceModel(final IScope scope, final IAgent agent, final IShape currentTarget,
			final boolean avoidOther, final IShape bounds, final IContainer<Integer, ?> pedestrianList,
			final IContainer<Integer, ?> obstaclesList, final double maxDist) {
		GamaPoint location = getLocation(agent).copy(scope);
		GamaPoint target = currentTarget.isPoint() ? currentTarget.getLocation()
				: Punctal._closest_point_to(location, currentTarget);
		target.setZ(target.z);
		double dist = location.distance(target);
		// System.out.println("location: " + location +" target: " + target + " dist: " + dist);

		if (dist == 0.0) return 0.0;

		if (!currentTarget.isPoint() && bounds != null && getCurrentEdge(agent) != null) {
			IList<IShape> pts = GamaListFactory.create();
			pts.add(agent.getLocation());
			pts.add(target);
			IShape line = Creation.line(scope, pts);
			if (!bounds.covers(line)) {
				target = Punctal._closest_point_to(target, getCurrentEdge(agent));
				pts.clear();
				pts.add(agent.getLocation());
				pts.add(target);
				line = Creation.line(scope, pts);
				if (!bounds.covers(line)) { target = Punctal._closest_point_to(location, getCurrentEdge(agent)); }

			}
		}

		GamaPoint velocity = null;
		if (avoidOther) {
			double distPercep = Math.max(maxDist, getPedestrianConsiderationDistance(agent));
			double distPercepObst = Math.max(maxDist, getObstacleConsiderationDistance(agent));
			if ("simple".equals(getPedestrianModel(agent))) {
				velocity = avoidSFMSimple(scope, agent, location, target, distPercep, distPercepObst, pedestrianList,
						obstaclesList);

			} else {
				velocity = avoidSFM(scope, agent, location, target, distPercep, distPercepObst, pedestrianList,
						obstaclesList);

			}
			velocity = velocity.multiplyBy(dist);
		} else {
			velocity = target.copy(scope).minus(location);
		}

		GamaPoint tar = velocity.copy(scope).add(location);
		double distToTarget = location.euclidianDistanceTo(tar);
		if (distToTarget > 0.0) {
			double coeff = Math.min(maxDist / distToTarget, 1.0);
			if (coeff == 1.0) {
				location = tar;
			} else {
				velocity = velocity.multiplyBy(coeff);
				location = location.add(velocity);
			}
		}

		if (bounds != null && !Spatial.Properties.overlaps(scope, location, bounds)) {
			location = Spatial.Punctal.closest_points_with(location, bounds).get(1);
		}
		double realSpeed = 0.0;
		double proba_detour = getProbaDetour(agent);
		if ((!Random.opFlip(scope, 1.0 - proba_detour) || (location.euclidianDistanceTo(target) <= agent.getLocation().euclidianDistanceTo(target)))) {
			realSpeed = agent.euclidianDistanceTo(location) / scope.getSimulation().getTimeStep(scope);
			setVelocity(agent, location.copy(scope).minus(getLocation(agent)));
			setLocation(agent, location);
		} else {
			setVelocity(agent, new GamaPoint(0, 0, 0));
		}

		return realSpeed;
	}

	/**
	 * Classical implementation of the Social Force Model (Helbing and Molnar, 1998)
	 *
	 * @param scope
	 * @param agent
	 * @param location
	 * @param currentTarget
	 * @param distPercep
	 * @param obstaclesList
	 * @return
	 */
	// public GamaPoint avoidSFMSimple(IScope scope, IAgent agent, GamaPoint location, GamaPoint currentTarget, double
	// distPercep, IContainer obstaclesList) {
	public GamaPoint avoidSFMSimple(final IScope scope, final IAgent agent, final GamaPoint location,
			final GamaPoint currentTarget, final double distPercepPedestrian, final double distPercepObstacle,
			final IContainer pedestriansList, final IContainer obstaclesList) {
		IMap<IShape, GamaPoint> forcesMap = GamaMapFactory.create();
		
		GamaPoint current_velocity = getVelocity(agent).copy(scope);
		GamaPoint fsoc = new GamaPoint(0, 0, 0);
		double dist = location.euclidianDistanceTo(currentTarget);
		double step = scope.getSimulation().getClock().getStepInSeconds();
		double speed = getSpeed(agent);
		IList<IAgent> obstacles = GamaListFactory.create(Types.AGENT);
		IList<IAgent> pedestrians = GamaListFactory.create(Types.AGENT);

		pedestrians.addAll(Spatial.Queries.at_distance(scope, pedestriansList, distPercepPedestrian));
		obstacles.addAll(Spatial.Queries.at_distance(scope, obstaclesList, distPercepObstacle));
		
		pedestrians.remove(agent);
		pedestrians.removeIf(IAgent::dead);
		double lambda = getlAMBDA_SFM(agent);
		double gama_ = getGAMA_SFM(agent);
		double A = getAPedestrian_SFM(agent);
		double n = getN_SFM(agent);
		double n_prime = getN_PRIME_SFM(agent);
		for (IAgent ag : pedestrians) {
			GamaPoint force = new GamaPoint(0, 0, 0);
			double distance = agent.euclidianDistanceTo(ag);
			GamaPoint itoj = Points.subtract(ag.getLocation(), agent.getLocation());
			itoj = itoj.divideBy(Maths.sqrt(scope, itoj.x * itoj.x + itoj.y * itoj.y + itoj.z * itoj.z));

			GamaPoint D = current_velocity.copy(scope).subtract(getVelocity(ag)).multiplyBy(lambda).add(itoj);
			double D_norm = Maths.sqrt(scope, D.x * D.x + D.y * D.y + D.z * D.z);
			double B = gama_ * D_norm;
			GamaPoint t_ = D.divideBy(D_norm);
			GamaPoint n_;
			if (t_.x == 0) {
				n_ = new GamaPoint(t_.y > 0 ? -1 : 1, 0, 0);
			} else if (t_.y == 0) {
				n_ = new GamaPoint(0, t_.x > 0 ? 1 : -1, 0);
			} else {
				double nx = -t_.y / t_.x;
				double norm = Math.sqrt(nx * nx + 1);
				n_ = t_.x > 0 ? new GamaPoint(-nx / norm, -1 / norm, 0) : new GamaPoint(nx / norm, 1 / norm, 0);
			}
			double t_xDotitoj = t_.x * itoj.x + t_.y * itoj.y + t_.z * itoj.z;
			t_xDotitoj = Math.max(Math.min(t_xDotitoj, 1.0), -1.0);
			double teta = Math.abs(Maths.acos(t_xDotitoj) * Math.PI / 180);
			if (teta <= Math.PI) {
				GamaPoint f_1 = t_.multiplyBy(Math.exp(-Math.pow(n_prime * B * teta, 2)));
				GamaPoint f_2 = n_.multiplyBy(Math.exp(-Math.pow(n * B * teta, 2)));
				force = f_1.add(f_2).multiplyBy(-A * Math.exp(-distance / B));
				fsoc = fsoc.add(force);
			}

			forcesMap.put(ag, force);
		}
		
		
		GamaPoint desiredVelo = currentTarget.copy(scope).minus(location)
				.divideBy(dist / Math.min(getSpeed(agent), dist / scope.getSimulation().getClock().getStepInSeconds()));
		GamaPoint fdest = desiredVelo.minus(current_velocity).dividedBy(getRELAXION_SFM(agent));


		forcesMap.put(agent, fdest);
		agent.setAttribute(FORCES, forcesMap);
		GamaPoint forces = fdest.add(fsoc);
		return current_velocity.add(forces).normalize();
	}

	public GamaPoint avoidSFM(final IScope scope, final IAgent agent, final GamaPoint location,
			final GamaPoint currentTarget, final double distPercepPedestrian, final double distPercepObstacle,
			final IContainer pedestriansList, final IContainer obstaclesList) {
		GamaPoint current_velocity = getVelocity(agent).copy(scope);
		double BWall = getObstacleConsiderationDistance(agent);
		double Bpedestrian = getPedestrianConsiderationDistance(agent);
		Double distMin = getMinDist(agent);
		double shoulderL = getShoulderLength(agent) / 2.0 + distMin;
		IMap<IShape, GamaPoint> forcesMap = GamaMapFactory.create();
		double dist = location.euclidianDistanceTo(currentTarget);
		if (dist == 0 || getSpeed(agent) <= 0.0) return new GamaPoint(0, 0, 0);
		IList<IAgent> obstacles = GamaListFactory.create(Types.AGENT);
		IList<IAgent> pedestrians = GamaListFactory.create(Types.AGENT);

		obstacles.addAll(Spatial.Queries.at_distance(scope, obstaclesList, distPercepObstacle));
		pedestrians.addAll(Spatial.Queries.at_distance(scope, pedestriansList, distPercepPedestrian));

		obstacles.remove(agent);
		obstacles.removeIf(IAgent::dead);
		double lambda = getlAMBDA_SFM(agent);
		double gama_ = getGAMA_SFM(agent);
		double APedes = getAPedestrian_SFM(agent);
		double AWall = getAObstSFM(agent);

		double k = getKSFM(agent);
		double kappa = getKappaSFM(agent);
		GamaPoint ei = current_velocity.copy(scope).normalize();

		GamaPoint desiredVelo = currentTarget.copy(scope).minus(location)
				.divideBy(dist / Math.min(getSpeed(agent), dist / scope.getSimulation().getClock().getStepInSeconds()));
		GamaPoint fdest = desiredVelo.minus(current_velocity).dividedBy(getRELAXION_SFM(agent));

		if (ei.equals(new GamaPoint())) { ei = fdest; }
		GamaPoint forcesPedestrian = new GamaPoint();
		for (IAgent ag : pedestrians) {
			double distance = agent.getLocation().euclidianDistanceTo(ag.getLocation());
			GamaPoint force = new GamaPoint();
			if (distance > 0) {
				double fact = APedes * Math.exp((shoulderL + getShoulderLength(ag) / 2.0 - distance) / Bpedestrian);

				GamaPoint nij = Points.subtract(agent.getLocation(), ag.getLocation());
				nij = nij.dividedBy(distance);
				double phi = Punctal.angleInDegreesBetween(scope, new GamaPoint(), ei, nij.copy(scope).multiplyBy(-1));
				GamaPoint fnorm = nij.multiplyBy(fact * (lambda + (1 - lambda) * (1 + Math.cos(phi)) / 2.0));

				GamaPoint tij = new GamaPoint(-1 * nij.y, nij.x);
				GamaPoint ej = getVelocity(ag).copy(scope).normalize();
				double phiij = GamaPoint.dotProduct(ei, ej);
				GamaPoint ftang = phiij <= 0 ? tij.multiplyBy(gama_ * fnorm.norm()) : new GamaPoint(0, 0, 0);
				GamaPoint fsoc = fnorm.add(ftang);
				force = fsoc.copy(scope);

				double omega = shoulderL + getShoulderLength(ag) / 2.0 - distance;
				if (omega > 0) {
					GamaPoint fphys = new GamaPoint();
					fphys = fphys.add(nij.copy(scope).multiplyBy(omega * k));
					double deltaSpeed = GamaPoint.dotProduct(getVelocity(ag).copy(scope).minus(current_velocity), tij);
					fphys = fphys.add(tij.copy(scope).multiplyBy(omega * kappa * deltaSpeed));
					force = force.add(fphys);
				}
			}
			forcesPedestrian = forcesPedestrian.add(force);
			forcesMap.put(ag, force);

		}

		GamaPoint forcesWall = new GamaPoint();
		for (IAgent ag : obstacles) {
			double distance = agent.euclidianDistanceTo(ag);
			GamaPoint closest_point = null;

			if (distance == 0) {
				closest_point = Punctal._closest_point_to(agent.getLocation(), ag.getGeometry().getExteriorRing(scope));
			} else {
				closest_point = Punctal._closest_point_to(agent.getLocation(), ag);
			}
			GamaPoint force = new GamaPoint();
			if (distance > 0) {
				double fact = AWall * Math.exp((shoulderL - distance) / BWall);
				double omega = shoulderL - distance;
				if (omega > 0) { fact += k * omega; }
				GamaPoint nij = Points.subtract(agent.getLocation(), closest_point.getLocation());
				nij = nij.normalize();

				GamaPoint fwall = nij.multiplyBy(fact);

				if (omega > 0) {
					GamaPoint tij = new GamaPoint(-1 * nij.y, nij.x);
					double product = GamaPoint.dotProduct(current_velocity, tij);

					fwall = fwall.minus(tij.multiplyBy(omega * kappa * product));
				}
				forcesWall.add(fwall);

			}

			forcesMap.put(ag, force);

		}

		forcesMap.put(agent, fdest);
		agent.setAttribute(FORCES, forcesMap);
		GamaPoint forces = fdest.add(forcesPedestrian);
		return current_velocity.add(forces).normalize();
	}

	@action (
			name = COMPUTE_VIRTUAL_PATH,
			args = { @arg (
					name = PEDESTRIAN_GRAPH,
					type = IType.GRAPH,
					optional = false,
					doc = @doc ("the graph on wich compute the path")),
					@arg (
							name = "target",
							type = IType.GEOMETRY,
							optional = false,
							doc = @doc ("the target to reach, can be any agent")) },

			doc = @doc (
					value = "action to compute a path to a location according to a given graph",
					returns = "the computed path, return nil if no path can be taken",
					examples = { @example ("do compute_virtual_path graph: pedestrian_network target: any_point;") }))
	public IPath primComputeVirtualPath(final IScope scope) throws GamaRuntimeException {
		IPath thePath = null;

		final ISpatialGraph graph = (ISpatialGraph) scope.getArg(PEDESTRIAN_GRAPH, IType.GRAPH);
		final IAgent agent = getCurrentAgent(scope);
		final boolean useGeometryTarget = getUseGeometryTarget(agent);
		IShape target = (IShape) scope.getArg("target", IType.GEOMETRY);
		IShape source = agent.getLocation();

		thePath = ((GraphTopology) graph.getTopology(scope)).pathBetween(scope, source, target);
		// If there is no path between source and target ...
		if (thePath == null) return thePath;
		IMap<IShape, IShape> roadTarget = GamaMapFactory.create();
		IList<IShape> targets = GamaListFactory.create();
		IList<IShape> segments = thePath.getEdgeGeometry();
		GamaPoint pp = source.getCentroid();

		for (int i = 0; i < segments.size(); i++) {
			IShape cSeg = segments.get(i);
			IShape cRoad = thePath.getRealObject(cSeg);
			IMap<IAgent, IShape> map = PedestrianRoadSkill.getConnectedSegmentsIntersection((IAgent) cRoad);

			IShape geom = null, cRoadNext = null, geomNext = null;
			if (useGeometryTarget) {
				geom = PedestrianRoadSkill.getFreeSpace(cRoad.getAgent());
				if (i < segments.size() - 1) {
					cRoadNext = thePath.getRealObject(segments.get(i + 1));
					geomNext = PedestrianRoadSkill.getFreeSpace(cRoadNext.getAgent());
				} else {
					geomNext = null;
				}
			}

			IShape nSeg = i == segments.size() - 1 ? null : segments.get(i + 1);
			// AD Note: getPoints() can be a very costly operation. It'd be better to call it once.
			for (int j = 1; j < cSeg.getPoints().size(); j++) {
				GamaPoint pt = cSeg.getPoints().get(j);
				IShape cTarget = null;
				if (PedestrianRoadSkill.getRoadStatus(scope, cRoad) == PedestrianRoadSkill.SIMPLE_STATUS) {
					cTarget = pt;
				} else {
					GamaPoint nextPt = null;
					if (j == cSeg.getPoints().size() - 1) {
						nextPt = nSeg == null ? null : nSeg.getPoints().get(1);
					} else {
						nextPt = cSeg.getPoints().get(j + 1);
					}
					cTarget = pt;
					if (cTarget == null) { cTarget = pt; }
				}
				if (useGeometryTarget) {
					cTarget = null;
					if (geomNext != null) {
						if (map != null && map.contains(scope, cRoadNext)) {

							cTarget = map.get(cRoadNext);
						} else {

							cTarget = Spatial.Operators.inter(scope, geom, geomNext);
						}
					}
					if (cTarget == null) { cTarget = pt; }
				}
				targets.add(cTarget);

				roadTarget.put(cTarget, cRoad);
				pp = cTarget.getLocation();
			}
		}
		IShape targ = targets.get(0);
		IAgent road = (IAgent) roadTarget.get(targ);
		if (road != null) { PedestrianRoadSkill.register(scope, road, agent); }

		if (!targets.get(0).getLocation().equals(agent.getLocation())) {
			if (road == null) {
				agent.setLocation(targets.get(0).getLocation());
			} else {
				IShape fS = PedestrianRoadSkill.getFreeSpace(road);
				if (!fS.intersects(agent.getLocation())) {
					agent.setLocation(Punctal._closest_point_to(agent.getLocation(), fS));
				}
			}
		}

		agent.setAttribute(ROADS_TARGET, roadTarget);
		setCurrentIndex(agent, 0);
		setTargets(agent, targets);

		setFinalTarget(agent, target);
		setCurrentTarget(agent, targ);

		agent.setAttribute(CURRENT_PATH, thePath);
		return thePath;
	}

	@action (
			name = "release_path",
			args = { @arg (
					name = "current_road",
					type = IType.AGENT,
					optional = true,
					doc = @doc ("current road on which the agent is located (can be nil)")), },
			doc = @doc (
					value = "clean all the interne state of the agent"))
	public void primArrivedAtDestination(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		IAgent road = (IAgent) scope.getArg("current_road", IType.AGENT);
		setCurrentIndex(agent, 0);
		setCurrentTarget(agent, null);
		setTargets(agent, GamaListFactory.create());
		setFinalTarget(agent, null);
		setCurrentPath(agent, null);
		setCurrentEdge(agent, (IShape) null);
		setRealSpeed(agent, 0.0);
		if (road != null) { PedestrianRoadSkill.unregister(scope, road, agent); }
	}

	@action (
			name = WALK,
			doc = @doc (
					value = "action to walk toward the final target using the current_path (requires to use the "
							+ COMPUTE_VIRTUAL_PATH + " action before)",
					examples = { @example ("do walk;") }))
	public void primWalk(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		if (agent == null || agent.dead()) return;
		final IShape finalTarget = getFinalTarget(agent);
		if (finalTarget == null) return;

		final IList<IShape> targets = getTargets(agent);
		if (targets == null || targets.isEmpty()) return;

		GamaPoint location = getLocation(agent).copy(scope);
		double maxDist = computeDistance(scope, agent);

		boolean movement = true;
		int maxIndex = targets.size() - 1;
		while (movement) {

			movement = false;
			int index = getCurrentIndex(agent);
			IShape currentTarget = getCurrentTarget(agent);
			IAgent road = (IAgent) getRoadsTargets(agent).get(currentTarget);

			IShape bounds = null;
			boolean avoidOther = getAvoidOther(agent);

			if (road != null) {

				avoidOther = PedestrianRoadSkill.getRoadStatus(scope, road) == PedestrianRoadSkill.SIMPLE_STATUS ? false
						: avoidOther;
				bounds = PedestrianRoadSkill.getFreeSpace(scope, road);

			}

			IContainer<Integer, ?> pedestrians =
					road == null ? GamaListFactory.create() : PedestrianRoadSkill.getCloseAgents(road);
			IList<ISpecies> speciesList = getObstacleSpecies(agent);
			IContainer obstacles = null;
			if (speciesList.size() == 1) {
				obstacles = speciesList.get(0);
			} else {
				obstacles = GamaListFactory.create(Types.AGENT);
				for (ISpecies species : speciesList) {

					((IList<IAgent>) obstacles).addAll(Cast.asList(scope, species));
				}
			}

			GamaPoint prevLoc = location.copy(scope);
			walkWithForceModel(scope, agent, currentTarget, avoidOther, bounds, pedestrians, obstacles, maxDist);
			location = agent.getLocation();

			if (arrivedAtTarget(scope, location, currentTarget, getToleranceTarget(agent), index, maxIndex, targets)) {
				if (road != null) { PedestrianRoadSkill.unregister(scope, road, agent); }

				if (index < maxIndex) {
					index++;

					setCurrentIndex(agent, index);
					setCurrentTarget(agent, targets.get(index));
					road = (IAgent) getRoadsTargets(agent).get(getCurrentTarget(agent));

					if (road != null) { PedestrianRoadSkill.register(scope, road, agent); }

					maxDist -= location.distance(prevLoc);
					if (maxDist > 0) { movement = true; }
				} else {
					final ISpecies context = agent.getSpecies();
					final IStatement.WithArgs actionTNR = context.getAction("release_path");
					final Arguments argsTNR = new Arguments();
					argsTNR.put("current_road", ConstantExpressionDescription.create(road));
					actionTNR.setRuntimeArgs(scope, argsTNR);

					actionTNR.executeOn(scope);

				}
			}
		}

	}

	boolean arrivedAtTarget(final IScope scope, final GamaPoint location, final IShape currentTarget, final double size,
			final int index, final int maxIndex, final IList<IShape> targets) {
		double dist = location.euclidianDistanceTo(currentTarget);
		if (dist <= size) return true;
		return false;
	}

}
