package simtools.gaml.extensions.traffic;

import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.ISpatialGraph;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.IPath;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.operators.Spatial.Queries;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

@vars({
	@var(name = IKeyword.SPEED, type = IType.FLOAT, init = "1.0", doc = @doc("the speed of the agent (in meter/second)")),
	@var(name = "current_path", type = IType.PATH, init = "nil", doc = @doc("the current path that tha agent follow")),
	@var(name = "final_target", type = IType.POINT, init = "nil", doc = @doc("the final target of the agent")),
	@var(name = "current_target", type = IType.POINT, init = "nil", doc = @doc("the current target of the agent")),
	@var(name = "current_index", type = IType.INT, init = "0", doc = @doc("the current index of the agent target (according to the targets list)")),
	@var(name = "targets", type = IType.LIST, of = IType.POINT, init = "[]", doc = @doc("the current list of points that the agent has to reach (path)")),
	@var(name = "security_distance_coeff", type = IType.FLOAT, init = "1.0", doc = @doc("the coefficient for the computation of the the min distance between two drivers (according to the vehicle speed - security_distance = 1°m + security_distance_coeff *real_speed )")),
	@var(name = "real_speed", type = IType.FLOAT, init = "0.0", doc = @doc("real speed of the agent (in meter/second)")),
	@var(name = "current_lane", type = IType.INT, init = "0", doc = @doc("the current lane on which the agent is")),
	@var(name = "vehicle_length", type = IType.FLOAT, init = "0.0", doc = @doc("the length of the vehicle (in meters)")),
	@var(name = "speed_coeff", type = IType.FLOAT, init = "1.0", doc = @doc("speed coefficient for the speed that the driver want to reach (according to the max speed of the road)")),
	@var(name = "max_acceleration", type = IType.FLOAT, init = "0.5", doc = @doc("maximum acceleration of the car for a cycle")),
	@var(name = "current_road", type = IType.AGENT, doc = @doc("current road on which the agent is")),
	@var(name = "on_linked_road", type = IType.BOOL, init = "false", doc = @doc("is the agent on the linked road?")),
	@var(name = "proba_lane_change_up", type = IType.FLOAT, init = "1.0", doc = @doc("probability to change lane to a upper lane (left lane if right side driving) if necessary")),
	@var(name = "proba_lane_change_down", type = IType.FLOAT, init = "1.0", doc = @doc("probability to change lane to a lower lane (right lane if right side driving) if necessary")),
	@var(name = "proba_respect_priorities", type = IType.FLOAT, init = "1.0", doc = @doc("probability to respect priority (right or left) laws")),
	@var(name = "proba_respect_stops", type = IType.LIST, of = IType.FLOAT, init = "[]", doc = @doc("probability to respect stop laws - one value for each type of stop")),
	@var(name = "proba_block_node", type = IType.FLOAT, init = "0.0", doc = @doc("probability to block a node (do not let other driver cross the crossroad)")),
	@var(name = "proba_use_linked_road", type = IType.FLOAT, init = "0.0", doc = @doc("probability to change lane to a linked road lane if necessary")),
	@var(name = "right_side_driving", type = IType.BOOL, init = "true", doc = @doc("are drivers driving on the right size of the road?")),
	@var(name = "max_speed", type = IType.FLOAT, init = "50.0", doc = @doc("maximal speed of the vehicle")),
	@var(name = "distance_to_goal", type = IType.FLOAT, init = "0.0", doc = @doc("euclidean distance to the next point of the current segment")),
	})
@skill(name = "advanced_driving")
public class AdvancedDrivingSkill extends MovingSkill {

	public final static String SECURITY_DISTANCE_COEFF = "security_distance_coeff";
	public final static String REAL_SPEED = "real_speed";
	public final static String CURRENT_ROAD = "current_road";
	public final static String CURRENT_LANE = "current_lane";
	public final static String DISTANCE_TO_GOAL = "distance_to_goal";
	public final static String VEHICLE_LENGTH = "vehicle_length";
	public final static String PROBA_LANE_CHANGE_UP = "proba_lane_change_up";
	public final static String PROBA_LANE_CHANGE_DOWN = "proba_lane_change_down";
	public final static String PROBA_RESPECT_PRIORITIES = "proba_respect_priorities";
	public final static String PROBA_RESPECT_STOPS = "proba_respect_stops";
	public final static String PROBA_BLOCK_NODE = "proba_block_node";
	public final static String PROBA_USE_LINKED_ROAD = "proba_use_linked_road";
	public final static String RIGHT_SIDE_DRIVING = "right_side_driving";
	public final static String ON_LINKED_ROAD = "on_linked_road";
	public final static String TARGETS = "targets";
	public final static String CURRENT_TARGET = "current_target";
	public final static String CURRENT_INDEX = "current_index";
	public final static String FINAL_TARGET = "final_target";
	public final static String CURRENT_PATH = "current_path";
	public final static String ACCELERATION_MAX = "max_acceleration";
	public final static String SPEED_COEFF = "speed_coeff";
	public final static String MAX_SPEED = "max_speed";

	@getter(ACCELERATION_MAX)
	public double getAccelerationMax(final IAgent agent) {
		return (Double) agent.getAttribute(ACCELERATION_MAX);
	}

	@setter(ACCELERATION_MAX)
	public void setAccelerationMax(final IAgent agent, final Double val) {
		agent.setAttribute(ACCELERATION_MAX, val);
	}
	@getter(SPEED_COEFF)
	public double getSpeedCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SPEED_COEFF);
	}

	@setter(SPEED_COEFF)
	public void setSpeedCoeff(final IAgent agent, final Double val) {
		agent.setAttribute(SPEED_COEFF, val);
	}
	
	@getter(MAX_SPEED)
	public double getMaxSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(MAX_SPEED);
	}

	@setter(MAX_SPEED)
	public void setMaxSpeed(final IAgent agent, final Double val) {
		agent.setAttribute(MAX_SPEED, val);
	}
	
	@getter(CURRENT_TARGET)
	public GamaPoint getCurrentTarget(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(CURRENT_TARGET);
	}

	@setter(CURRENT_TARGET)
	public void setCurrentTarget(final IAgent agent, final GamaPoint point) {
		agent.setAttribute(CURRENT_TARGET, point);
	}
	
	@getter(FINAL_TARGET)
	public GamaPoint getFinalTarget(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(FINAL_TARGET);
	}

	@setter(FINAL_TARGET)
	public void setFinalTarget(final IAgent agent, final GamaPoint point) {
		agent.setAttribute(FINAL_TARGET, point);
	}
	
	@getter(CURRENT_INDEX)
	public Integer getCurrentIndex(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_INDEX);
	}

	@setter(CURRENT_INDEX)
	public void setCurrentIndex(final IAgent agent, final Integer index) {
		agent.setAttribute(CURRENT_INDEX, index);
	}
	
	@getter(CURRENT_PATH)
	public IPath getCurrentPath(final IAgent agent) {
		return (IPath) agent.getAttribute(CURRENT_PATH);
	}

	@setter(CURRENT_PATH)
	public void setCurrentPath(final IAgent agent, final IPath path) {
		agent.setAttribute(CURRENT_PATH, path);
	}
	@getter(TARGETS)
	public List<GamaPoint> getTargets(final IAgent agent) {
		return (List<GamaPoint>) agent.getAttribute(TARGETS);
	}

	@setter(TARGETS)
	public void setTargets(final IAgent agent, final List<GamaPoint> points) {
		agent.setAttribute(TARGETS, points);
	}
	
	@getter(PROBA_USE_LINKED_ROAD)
	public double getProbaUseLinkedRoad(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_USE_LINKED_ROAD);
	}

	@setter(PROBA_USE_LINKED_ROAD)
	public void setProbaUseLinkedRoad(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_USE_LINKED_ROAD, proba);
	}
	
	@getter(PROBA_LANE_CHANGE_DOWN)
	public double getProbaLaneChangeDown(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_LANE_CHANGE_DOWN);
	}

	@setter(PROBA_LANE_CHANGE_DOWN)
	public void setProbaLaneChangeDown(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_LANE_CHANGE_DOWN, proba);
	}

	@getter(PROBA_LANE_CHANGE_UP)
	public double getProbaLaneChangeUp(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_LANE_CHANGE_UP);
	}

	@setter(PROBA_LANE_CHANGE_UP)
	public void setProbaLaneChangeUp(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_LANE_CHANGE_UP, proba);
	}

	@getter(PROBA_RESPECT_PRIORITIES)
	public double getRespectPriorities(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_RESPECT_PRIORITIES);
	}

	@setter(PROBA_RESPECT_PRIORITIES)
	public void setRespectPriorities(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_RESPECT_PRIORITIES, proba);
	}

	@getter(PROBA_BLOCK_NODE)
	public double getProbaBlockNode(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_BLOCK_NODE);
	}

	@setter(PROBA_BLOCK_NODE)
	public void setProbaBlockNode(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_BLOCK_NODE, proba);
	}

	@getter(PROBA_RESPECT_STOPS)
	public List<Double> getRespectStops(final IAgent agent) {
		return (List<Double>) agent.getAttribute(PROBA_RESPECT_STOPS);
	}

	@setter(PROBA_RESPECT_STOPS)
	public void setRespectStops(final IAgent agent, final List<Boolean> probas) {
		agent.setAttribute(PROBA_RESPECT_STOPS, probas);
	}
	
	@getter(ON_LINKED_ROAD)
	public boolean getOnLinkedRoad(final IAgent agent) {
		return (Boolean) agent.getAttribute(ON_LINKED_ROAD);
	}

	@setter(ON_LINKED_ROAD)
	public void setOnLinkedRoad(final IAgent agent, final Boolean onLinkedRoad) {
		agent.setAttribute(ON_LINKED_ROAD, onLinkedRoad);
	}

	@getter(RIGHT_SIDE_DRIVING)
	public boolean getRightSideDriving(final IAgent agent) {
		return (Boolean) agent.getAttribute(RIGHT_SIDE_DRIVING);
	}

	@setter(RIGHT_SIDE_DRIVING)
	public void setRightSideDriving(final IAgent agent, final Boolean isRight) {
		agent.setAttribute(RIGHT_SIDE_DRIVING, isRight);
	}

	@getter(SECURITY_DISTANCE_COEFF)
	public double getSecurityDistanceCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SECURITY_DISTANCE_COEFF);
	}

	@setter(SECURITY_DISTANCE_COEFF)
	public void setSecurityDistanceCoeff(final IAgent agent, final double ls) {
		agent.setAttribute(SECURITY_DISTANCE_COEFF, ls);
	}

	@getter(CURRENT_ROAD)
	public IAgent getCurrentRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(CURRENT_ROAD);
	}

	@getter(REAL_SPEED)
	public double getRealSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(REAL_SPEED);
	}

	@getter(VEHICLE_LENGTH)
	public double getVehiculeLength(final IAgent agent) {
		return (Double) agent.getAttribute(VEHICLE_LENGTH);
	}

	@getter(CURRENT_LANE)
	public int getCurrentLane(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_LANE);
	}

	@getter(DISTANCE_TO_GOAL)
	public double getDistanceToGoal(final IAgent agent) {
		return (Double) agent.getAttribute(DISTANCE_TO_GOAL);
	}

	@setter(DISTANCE_TO_GOAL)
	public void setDistanceToGoal(final IAgent agent, final double dg) {
		agent.setAttribute(DISTANCE_TO_GOAL, dg);
	}

	public Double primAdvancedFollow(final IScope scope,IAgent agent, double s, double t,IPath path, GamaPoint target) throws GamaRuntimeException {
		final double security_distance = getSecurityDistanceCoeff(agent) * getRealSpeed(agent) + 1;
		final int currentLane = getCurrentLane(agent);
		final Double probaChangeLaneUp = getProbaLaneChangeUp(agent);
		final Double probaChangeLaneDown = getProbaLaneChangeDown(agent);
		final Double probaProbaUseLinkedRoad = getProbaUseLinkedRoad(agent);
		final Boolean rightSide = getRightSideDriving(agent);
		final IAgent currentRoad = getCurrentRoad(agent);
		final IAgent linkedRoad = (IAgent) currentRoad.getAttribute(RoadSkill.LINKED_ROAD);
		final boolean onLinkedRoad = getOnLinkedRoad(agent);
		final double maxDist = computeDistance(scope, agent, s, t);
		
		if ( maxDist == 0 ) { return 0.0; }
		if ( path != null && !path.getEdgeList().isEmpty() ) {
			double tps = 0;
			if (onLinkedRoad) {
				tps =
						t *
							moveToNextLocAlongPathOSM(scope, agent, path, target, maxDist, security_distance, currentLane,
									currentRoad,linkedRoad, probaChangeLaneUp, probaChangeLaneDown,probaProbaUseLinkedRoad, rightSide);
			} else {
				tps =
						t *
							moveToNextLocAlongPathOSM(scope, agent, path, target, maxDist, security_distance, currentLane,
								currentRoad, linkedRoad, probaChangeLaneUp, probaChangeLaneDown,probaProbaUseLinkedRoad, rightSide);
			}
			
			if ( tps < 1.0 ) {
				agent.setAttribute(REAL_SPEED, this.getRealSpeed(agent) / (1 - tps));
			}

			return tps;
		}
		return 0.0;
	}
	@action(name = "advanced_follow_driving", args = {
		@arg(name = "path", type = IType.PATH, optional = false, doc = @doc("a path to be followed.")),
		@arg(name = "target", type = IType.POINT, optional = true, doc = @doc("the target to reach")),
		@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "time", type = IType.FLOAT, optional = true, doc = @doc("time to travel")) }, doc = @doc(value = "moves the agent towards along the path passed in the arguments while considering the other agents in the network (only for graph topology)", returns = "the remaining time", examples = { "do osm_follow path: the_path on: road_network;" }))
	public Double primAdvancedFollow(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		final Double t = scope.hasArg("time") ? scope.getFloatArg("time") : 1.0;
		final GamaPoint target = scope.hasArg("target") ? (GamaPoint) scope.getArg("target", IType.NONE) : null;
		final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		return primAdvancedFollow(scope,agent, s, t,path, target);
	}

	@action(name = "is_ready_next_road", args = {
		@arg(name = "new_road", type = IType.AGENT, optional = false, doc = @doc("the road to test")),
		@arg(name = "lane", type = IType.INT, optional = false, doc = @doc("the lane to test")) }, doc = @doc(value = "action to test if the driver can take the given road at the given lane", returns = "true (the driver can take the road) or false (the driver cannot take the road)", examples = { "do is_ready_next_road new_road: a_road lane: 0;" }))
	public Boolean primIsReadyNextRoad(final IScope scope) throws GamaRuntimeException {
		IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		Integer lane = (Integer) scope.getArg("lane", IType.INT);
		IAgent driver = getCurrentAgent(scope);
		double vL = getVehiculeLength(driver);
		double secDistCoeff = getSecurityDistanceCoeff(driver);
		double probaBlock = getProbaBlockNode(driver);
		boolean testBlockNode = Random.opFlip(scope, probaBlock);
		IAgent node = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
		Map<IAgent, List<IAgent>> block = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
		List<IAgent> ba = new GamaList<IAgent>(block.keySet());
		for ( IAgent dr : ba ) {
			if ( !dr.getLocation().equals(node.getLocation()) ) {
				block.remove(dr);
			}
		}
		return isReadyNextRoad(scope, road, driver, secDistCoeff, vL, block) &&
			(testBlockNode || nextRoadTestLane(driver, road, lane, secDistCoeff, vL));
	}

	public Boolean isReadyNextRoad(final IScope scope, final IAgent road, final IAgent driver,
		final double secDistCoeff, final double vL, final Map<IAgent, List<IAgent>> block) throws GamaRuntimeException {
		IAgent theNode = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
		List<List> stops = (List<List>) theNode.getAttribute(RoadNodeSkill.STOP);
		List<Double> respectsStops = getRespectStops(driver);
		IAgent currentRoad = (IAgent) driver.getAttribute(CURRENT_ROAD);
		for ( int i = 0; i < stops.size(); i++ ) {
			Boolean stop = stops.get(i).contains(currentRoad);
			if ( stop && (respectsStops.size() <= i || Random.opFlip(scope, respectsStops.get(i))) ) { return false; }
		}
		for ( List<IAgent> rd : block.values() ) {
			if ( rd.contains(currentRoad) ) { return false; }
		}
		Boolean rightSide = getRightSideDriving(driver);

		double angleRef =
			Punctal.angleInDegreesBetween((GamaPoint) theNode.getLocation(), (GamaPoint) currentRoad.getLocation(),
				(GamaPoint) road.getLocation());
		List<IAgent> roadsIn = (List) theNode.getAttribute(RoadNodeSkill.ROADS_IN);
		if ( !Random.opFlip(scope, getRespectPriorities(driver)) ) { return true; }
		for ( IAgent rd : roadsIn ) {
			if ( rd != currentRoad ) {
				double angle =
					Punctal.angleInDegreesBetween((GamaPoint) theNode.getLocation(),
						(GamaPoint) currentRoad.getLocation(), (GamaPoint) rd.getLocation());
				if ( rightSide && angle > angleRef || !rightSide && angle < angleRef ) {
					int nbL = (Integer) rd.getAttribute(RoadSkill.LANES);
					List<List<IAgent>> agentsOn = (List) rd.getAttribute(RoadSkill.AGENTS_ON);
					for ( int i = 0; i < nbL; i++ ) {
						for ( IAgent pp : agentsOn.get(i) ) {
							double vL2 = getVehiculeLength(pp);
							if ( Maths.round(getRealSpeed(pp), 2) > 0.0 &&
								pp.euclidianDistanceTo(driver) < 1 + secDistCoeff * getRealSpeed(pp) + vL2 / 2 + vL / 2 ) { return false; }
						}
					}
				}
			}
		}
		return true;
	}

	public boolean nextRoadTestLane(final IAgent driver, final IAgent road, final int lane, final double secDistCoeff,
		final double vL) {
		double realSpeed = getRealSpeed(driver);
		double secDist = 1 + realSpeed * secDistCoeff;

		List<IAgent> drivers = (List) ((List) road.getAttribute(RoadSkill.AGENTS_ON)).get(lane);
		for ( IAgent dr : drivers ) {
			if ( dr == driver ) {
				continue;
			}
			if ( dr.euclidianDistanceTo(driver) < vL / 2 + secDist + getVehiculeLength(dr) / 2 ) { return false; }
		}
		return true;
	}


	
	@action(name = "compute_path", args = { @arg(name = "graph", type = IType.GRAPH, optional = false, doc = @doc("the graph on wich compute the path")),@arg(name = "target", type = IType.AGENT, optional = false, doc = @doc("the target node to reach")) }, doc = @doc(value = "action to compute a path to a target location according to a given graph", returns = "the computed path, return nil if no path can be taken", examples = { "do compute_path graph: road_network target: the_node;" }))
	public IPath primComputePath(final IScope scope) throws GamaRuntimeException {
		ISpatialGraph graph = (ISpatialGraph) scope.getArg("graph", IType.GRAPH);
		IAgent target = (IAgent) scope.getArg("target", IType.AGENT);
		IAgent agent = getCurrentAgent(scope);
		IAgent source = Queries.closest_to(scope, target.getSpecies(), agent);
		//System.out.println("source : " + source + " target : " + target);
		IPath path = graph.getTopology().pathBetween(scope, source, target);
		if (path != null && ! path.getEdgeGeometry().isEmpty()) {
			List<GamaPoint> targets = getTargets(agent);
			targets.clear();
			for (Object edge: path.getEdgeGeometry()) {
				IShape egGeom = (IShape) edge;
				Coordinate[] coords = egGeom.getInnerGeometry().getCoordinates();
				GamaPoint pt = new GamaPoint(coords[coords.length-1]);
				targets.add(pt);
			}
			setTargets(agent, targets);
			IAgent nwRoad = (IAgent) path.getEdgeList().get(0); 
			setCurrentIndex(agent, 0);
			setCurrentTarget(agent,targets.get(0));
			setFinalTarget(agent, (GamaPoint) target.getLocation());
			setCurrentPath(agent, path);
			RoadSkill.register(nwRoad, agent, 0);
			return path;
			
		} else {
			setTargets(agent, new GamaList<GamaPoint>());
			setCurrentTarget(agent,null);
			setFinalTarget(agent, null);
			setCurrentPath(agent, null);
		}
		return null;		
	}
	
	
	private Double speedChoice(IAgent agent, IAgent road)  {
		return Math.min(getMaxSpeed(agent), Math.min(getRealSpeed(agent) + getAccelerationMax(agent),getSpeedCoeff(agent) * (Double)road.getAttribute(RoadSkill.MAXSPEED)));
	}
	
	/*static double t1 = 0;
	static double t2 = 0;
	static double t3 = 0;
	static double t4 = 0;
	static double t31 = 0;
	static double t32 = 0;
	static double t33 = 0;
	static double t34 = 0;
	static double t35 = 0;
	static double t36 = 0;
	static double t37 = 0;

	static double t341 = 0;
	static double t342 = 0;
	static double t343 = 0;
	static double t344 = 0;
	static double t345 = 0;
	static double t346 = 0;
	static double t347 = 0;*/
	@action(name = "drive", doc = @doc(value = "action to drive toward the final target", examples = { "do drive;" }))
	public void primDrive(final IScope scope) throws GamaRuntimeException {
		//System.out.println("t1: " + t1 + " t2: " + t2 + " t3: " + t3 + " t341: " + t341 + " t342: " + t342 + " t343: " + t343 + " t344: " + t344 + " t345: " + t345 + " t346: " + t346 + " t347: " + t347 + " t4:" + t4);
		//long t = System.currentTimeMillis();
		IAgent agent = getCurrentAgent(scope);
		GamaPoint finalTarget = getFinalTarget(agent);
		final IPath path = getCurrentPath(agent);
		final ISpecies context = agent.getSpecies();
		final IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		Arguments argsEF = new Arguments();
		final IStatement.WithArgs actionLC = context.getAction("lane_choice");
		Arguments argsLC = new Arguments();
		final IStatement.WithArgs actionSC = context.getAction("speed_choice");
		Arguments argsSC = new Arguments();
		
		double remainingTime = 1.0;
	//	t1 += System.currentTimeMillis() - t;
		while (remainingTime > 0.0) {
		//	t = System.currentTimeMillis();
			IAgent road = getCurrentRoad(agent);
			final GamaPoint target = getCurrentTarget(agent);
			argsSC.put("new_road", ConstantExpressionDescription.create(road));
			actionSC.setRuntimeArgs(argsSC);
			double speed = (Double) actionSC.executeOn(scope);
			
			//double speed = speedChoice(agent,road);
			setSpeed(agent, speed);
		//	t2 += System.currentTimeMillis() - t;
		//	t = System.currentTimeMillis();
			remainingTime = primAdvancedFollow(scope,agent, speed, remainingTime,path, target); 
		//	t3 += System.currentTimeMillis() - t;
			if (agent.getLocation().equals(finalTarget)) {
				setFinalTarget(agent, null);
				return;
			}
		//	t = System.currentTimeMillis();
			
			if (remainingTime > 0.0 && agent.getLocation().equals(getCurrentTarget(agent))) {
				Integer currentIndex = getCurrentIndex(agent);
				if (currentIndex >= (path.getEdgeList().size() - 1)) {
					setCurrentPath(agent, null);
					setFinalTarget(agent, null);
				//	t4 += System.currentTimeMillis() - t;
					return;
				}
				IAgent newRoad = (IAgent) path.getEdgeList().get(currentIndex + 1); 
				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);
				argsLC.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionLC.setRuntimeArgs(argsLC);
				int lane = (Integer) actionLC.executeOn(scope);
				//int lane = laneChoice(scope,newRoad); 
				if (lane >= 0) {
					currentIndex = currentIndex + 1;
					setCurrentIndex(agent, currentIndex);
					RoadSkill.register(newRoad, agent, lane);
					setCurrentTarget(agent, getTargets(agent).get(currentIndex)); 
				} else {
				//	t4 += System.currentTimeMillis() - t;
					return;
				}  
			}
			//t4 += System.currentTimeMillis() - t;
			
		}
		
	}
	
	@action(name = "external_factor_impact", args = { @arg(name = "new_road", type = IType.AGENT, optional = false, doc = @doc("the road on which to the driver wants to go")),@arg(name = "remaining_time", type = IType.FLOAT, optional = false, doc = @doc("the remaining time"))  },doc = @doc(value = "action that allows to define how the remaining time is impacted by external factor", returns = "the remaining time", examples = { "do external_factor_impact new_road: a_road remaining_time: 0.5;" }))
	public Double primExternalFactorOnRemainingTime(final IScope scope) throws GamaRuntimeException {
		return scope.getFloatArg("remaining_time");
	}
	
	@action(name = "speed_choice", args = {@arg(name = "new_road", type = IType.AGENT, optional = false, doc = @doc("the road on which to choose the speed"))  }, doc = @doc(value = "action to choose a speed", returns = "the chosen speed", examples = { "do speed_choice new_road: the_road;" }))
	public Double primSpeedChoice(final IScope scope) throws GamaRuntimeException {
		IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		IAgent agent = getCurrentAgent(scope);
		double speed = speedChoice(agent,road);
		setSpeed(agent, speed);
		return speed;
	}
	
	public Integer laneChoice(final IScope scope,IAgent road) throws GamaRuntimeException {
		Integer lanes = (Integer) road.getAttribute(RoadSkill.LANES);
		IAgent driver = getCurrentAgent(scope);
		Integer currentLane = getCurrentLane(driver);
		IAgent currentRoad = getCurrentRoad(driver);
		IAgent linkedRoad = (IAgent) road.getAttribute(RoadSkill.LINKED_ROAD);
		boolean onLinkedRoad = getOnLinkedRoad(driver);
		IAgent node = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
		double vL = getVehiculeLength(driver);
		double secDistCoeff = getSecurityDistanceCoeff(driver);
		double probaBlock = getProbaBlockNode(driver);
		boolean testBlockNode = Random.opFlip(scope, probaBlock);
		Map<IAgent, List<IAgent>> block = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
		List<IAgent> ba = new GamaList<IAgent>(block.keySet());
		List<IAgent> roadsIn = (List<IAgent>) node.getAttribute(RoadNodeSkill.ROADS_IN);

		double probaUseLinkedRoad = getProbaUseLinkedRoad(driver);
		boolean testUseLinkedRoad = Random.opFlip(scope, probaUseLinkedRoad);
		if (onLinkedRoad) {
			currentLane = lanes - 1;
		}
		for ( IAgent dr : ba ) {
			if ( !dr.getLocation().equals(node.getLocation()) ) {
				block.remove(dr);
			}
		}

		boolean ready = isReadyNextRoad(scope, road, driver, secDistCoeff, vL, block);
		if ( !ready ) { return -1; }
		if ( lanes == 0 && !onLinkedRoad) {
			int lane = testBlockNode || nextRoadTestLane(driver, road, 0, secDistCoeff, vL) ? 0 : -1;
			if ( lane != -1 ) {
				addBlockingDriver(0, testBlockNode, driver, currentRoad, road, node, roadsIn, block);
				return lane;
			} else if (linkedRoad != null && testUseLinkedRoad){
				Integer lanesLinked = (Integer) linkedRoad.getAttribute(RoadSkill.LANES);
				int newLane = nextRoadTestLane(driver, linkedRoad, lanesLinked - 1, secDistCoeff, vL) ? lanesLinked - 1 : -1;
				if (newLane > -1 ) 
					return newLane + lanes;
			}
			return lane;
		}

		int cvTmp = Math.min(currentLane, lanes - 1);
		int cv = testBlockNode || nextRoadTestLane(driver, road, cvTmp, secDistCoeff, vL) ? cvTmp : -1;
		if ( cv != -1 ) {
			addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn, block);
			return cv;
		}
		double probaLaneChangeDown = getProbaLaneChangeDown(driver);
		double probaLaneChangeUp = getProbaLaneChangeUp(driver);
		boolean changeDown = Random.opFlip(scope, probaLaneChangeDown);
		boolean changeUp = Random.opFlip(scope, probaLaneChangeUp);
		if ( changeDown || changeUp ) {
			for ( int i = 0; i < lanes; i++ ) {
				int l1 = cvTmp - i;
				if ( l1 >= 0 && changeDown ) {
					cv = testBlockNode || nextRoadTestLane(driver, road, l1, secDistCoeff, vL) ? l1 : -1;
					if ( cv != -1 ) {
						addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn, block);
						return cv;
					}
				}
				int l2 = cvTmp + i;
				if ( l2 < lanes && changeUp ) {
					cv = testBlockNode || nextRoadTestLane(driver, road, l2, secDistCoeff, vL) ? l2 : -1;
					if ( cv != -1 ) {
						addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn, block);
						return cv;
					}
				}
			}
		}
		if (cv == -1 && linkedRoad != null && testUseLinkedRoad) {
			Integer lanesLinked = (Integer) linkedRoad.getAttribute(RoadSkill.LANES);
			for ( int i = 1; i <= lanesLinked; i++ ) {
				int newLane = nextRoadTestLane(driver, linkedRoad, lanesLinked - i, secDistCoeff, vL) ? lanesLinked - i : -1;
				if (newLane > -1 ) 
					return newLane + lanes;
			}
		}
		return cv;
	}
	
	@action(name = "lane_choice", args = { @arg(name = "new_road", type = IType.AGENT, optional = false, doc = @doc("the road on which to choose the lane")) }, doc = @doc(value = "action to choose a lane", returns = "the chosen lane, return -1 if no lane can be taken", examples = { "do lane_choice new_road: a_road;" }))
	public Integer primLaneChoice(final IScope scope) throws GamaRuntimeException {
		IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		return laneChoice(scope,road);
	}

	public void addBlockingDriver(final int cv, final boolean testBlockNode, final IAgent driver,
		final IAgent currentRoad, final IAgent road, final IAgent node, final List<IAgent> roadsIn,
		final Map<IAgent, List<IAgent>> block) {
		if ( testBlockNode && roadsIn.size() > 1 ) {
			List<IAgent> rb = roadBlocked(currentRoad, road, node, roadsIn);
			if ( !rb.isEmpty() ) {
				block.put(driver, rb);
			}
		}
	}

	public List<IAgent> roadBlocked(final IAgent roadIn, final IAgent roadOut, final IAgent node,
		final List<IAgent> roadsIn) {
		List<IAgent> roadsBlocked = new GamaList<IAgent>();
		for ( IAgent rd : roadsIn ) {
			if ( rd != roadIn && !rd.getLocation().equals(roadOut.getLocation()) ) {
				roadsBlocked.add(rd);
			}
		}
		return roadsBlocked;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Return the next location toward a target on a line
	 * 
	 * @param coords coordinates of the line
	 * @param source current location
	 * @param target location to reach
	 * @param distance max displacement distance
	 * @return the next location
	 */

	protected double computeDistance(final IScope scope, final IAgent agent, final double s, final double t)
		throws GamaRuntimeException {

		return s * t * scope.getClock().getStep();
	}
	
	public double distance2D(GamaPoint p1, GamaPoint p2) {
		return p1.distance(p2);
	}

	private double avoidCollision(final IScope scope, final IAgent agent, final double distance,
		final double security_distance, final GamaPoint currentLocation, final GamaPoint target, final int lane,
		final IAgent currentRoad, final boolean changeLane) {
		//long t = System.currentTimeMillis();
		
		IList<IAgent> agents = (IList) ((GamaList) currentRoad.getAttribute(RoadSkill.AGENTS_ON)).get(lane);
		double vL = getVehiculeLength(agent);
		if ( agents.size() < 2 ) {
			if ( changeLane && distance < vL ) { return 0; }
			return distance;
		}
		double distanceMax = distance + security_distance + vL;
		//t341+= System.currentTimeMillis() - t;
		//t = System.currentTimeMillis();
		
		/*List<IAgent> agsFiltered =
			new GamaList(agent.getTopology().getNeighboursOf(scope, agent.getLocation(), distanceMax,
				In.list(scope, agents)));
		if ( agsFiltered.isEmpty() ) { return distance; }
*/
		double distanceToGoal = getDistanceToGoal(agent); //distance2D((GamaPoint) agent.getLocation(), target);//agent.euclidianDistanceTo(target);// getDistanceToGoal(agent);
		// double distanceMax = distance + security_distance + 0.5 * getVehiculeLength(agent);
		IAgent nextAgent = null;
		double minDiff = Double.MAX_VALUE;
		//t343+= System.currentTimeMillis() - t;
		//t = System.currentTimeMillis();
		
		for ( IAgent ag : agents ) {
			double dist = getDistanceToGoal(ag);//distance2D((GamaPoint) ag.getLocation(), target);
			double diff = distanceToGoal - dist;
			if ( changeLane && diff < vL ) { return 0; }
			if ( diff < minDiff && (changeLane && diff >= 0 || !changeLane && diff > 0) ) {
				minDiff = diff;
				nextAgent = ag;
			}
		}
		//t344+= System.currentTimeMillis() - t;
	//	t = System.currentTimeMillis();
		

		if ( nextAgent == null ) { return distance; }

		double realDist =
			Math.min(distance, minDiff - security_distance - 0.5 * vL - 0.5 * getVehiculeLength(nextAgent));
		//t345+= System.currentTimeMillis() - t;
		
		if ( changeLane && realDist < vL ) { return 0; }
		return Math.max(0.0, realDist);
	}

	private void changeLane(final IScope scope, final IAgent agent, final int previousLane, final int newLane, final List currentAgentOn,final List newAgentOn) {
		agent.setAttribute(CURRENT_LANE, newLane);
		((List) currentAgentOn.get(previousLane)).remove(agent);
		((List) newAgentOn.get(newLane)).add(agent);
	}
	
	private double avoidCollisionLinkedRoad(final IScope scope, final IAgent agent, final double distance,
			final double security_distance, final GamaPoint currentLocation, final GamaPoint target, final int lane,
			final IAgent currentRoad, final IAgent linkedRoad, final Double probaChangeLaneUp, final Double probaChangeLaneDown,final Double probaUseLinkedRoad,
			final Boolean rightSide) {
			double distMax = 0;
			int newLane = lane;
			int nbLinkedLanes = (Integer) linkedRoad.getAttribute(RoadSkill.LANES);
			int nbLanes = (Integer) currentRoad.getAttribute(RoadSkill.LANES);
			List agentOnCurrentRoad = (List) currentRoad.getAttribute(RoadSkill.AGENTS_ON);
			List agentOnLinkedRoad = (List) linkedRoad.getAttribute(RoadSkill.AGENTS_ON);
			List newAgentOn = agentOnLinkedRoad;
			boolean onLinkedRoad = true;
			
			if ( GAMA.getRandom().next() < probaChangeLaneDown ) {
				if (lane < nbLinkedLanes - 1) {
					double val =
						avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane + 1,
								linkedRoad, true);
					if ( val == distance ) {
						newLane = lane + 1;
						changeLane(scope, agent, lane, newLane, agentOnLinkedRoad,agentOnLinkedRoad);
						return distance;
					}
					if ( val > distMax && val > 0 ) {
						newLane = lane + 1;
						distMax = val;
					}
				} else {
					double val =
							avoidCollision(scope, agent, distance, security_distance, currentLocation, target, nbLanes - 1,
									currentRoad, true);
					if ( val == distance ) {
							newLane = nbLanes - 1;
							setOnLinkedRoad(agent, false);
							changeLane(scope, agent, lane, newLane, agentOnLinkedRoad,agentOnCurrentRoad);
							return distance;
						}
						if ( val > distMax && val > 0 ) {
							newLane = nbLanes - 1;
							newAgentOn = agentOnCurrentRoad;
							distMax = val;
							onLinkedRoad = false;
						}
				}
			}
			double val =
				avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane, linkedRoad, false);
			if ( val == distance ) {
				return distance;
			}
			if ( val >= distMax ) {
				distMax = val;
				newLane = lane;
				newAgentOn = agentOnLinkedRoad;
				onLinkedRoad = true;
			}
			if ( lane > 0 &&
				GAMA.getRandom().next() < probaChangeLaneUp ) {
				val =
					avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane - 1,
						linkedRoad, true);
				if ( val > distMax && val > 0 ) {
					distMax = val;
					newLane = lane - 1;
					onLinkedRoad = true;
					newAgentOn = agentOnLinkedRoad;
				}
			}

			if ( lane != newLane ) {
				if (! onLinkedRoad) {
					setOnLinkedRoad(agent, false);
				}
				changeLane(scope, agent, lane, newLane, agentOnLinkedRoad,newAgentOn);
				
			}
			
			return distMax;
		}
	private double avoidCollision(final IScope scope, final IAgent agent, final double distance,
		final double security_distance, final GamaPoint currentLocation, final GamaPoint target, final int lane,
		final IAgent currentRoad, final IAgent linkedRoad, final Double probaChangeLaneUp, final Double probaChangeLaneDown,final Double probaUseLinkedRoad,
		final Boolean rightSide) {
		
		double distMax = 0;
		int newLane = lane;
		double vl = getVehiculeLength(agent)/2.0;
		List agentOn = (List) currentRoad.getAttribute(RoadSkill.AGENTS_ON);
		List newAgentOn = agentOn;
		boolean changeLane = false;
		if ( lane > 0 && GAMA.getRandom().next() < probaChangeLaneDown ) {
			double val =
				avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane - 1,
					currentRoad, true);
				if ( val == distance ) {
				newLane = lane - 1;
				changeLane(scope, agent, lane, newLane, agentOn,agentOn);
				
				return distance;
			}
			if ( val > distMax && val > vl ) {
				newLane = lane - 1;
				changeLane = true;
				distMax = val;
			}
		}
		
		double val =
			avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane, currentRoad, false);
		if ( val == distance ) {
			return distance;
		}
		if ( val >= distMax ) {
			distMax = val;
			newLane = lane;
			changeLane = false;
		}
		if ( lane < (Integer) currentRoad.getAttribute(RoadSkill.LANES) - 1 &&
			GAMA.getRandom().next() < probaChangeLaneUp ) {
			val =
				avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane + 1,
					currentRoad, true);
			if ( val > distMax && val > vl ) {
				distMax = val;
				newLane = lane + 1;
				changeLane = true;
			}
		}
		if ( linkedRoad != null && GAMA.getRandom().next() < probaUseLinkedRoad ) {
			int nbLinkedLanes = (Integer) linkedRoad.getAttribute(RoadSkill.LANES);
			val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target, nbLinkedLanes - 1,
						linkedRoad, true);
				if ( val > distMax && val > vl ) {
					distMax = val;
					newLane = nbLinkedLanes - 1;
					newAgentOn = (List) linkedRoad.getAttribute(RoadSkill.AGENTS_ON);
					setOnLinkedRoad(agent, true);
					changeLane = true;
				}
			}
		if ( changeLane ) {
			changeLane(scope, agent, lane, newLane, agentOn,newAgentOn);
		}
		return distMax;
	}

	private double moveToNextLocAlongPathOSM(final IScope scope, final IAgent agent, final IPath path,
		final GamaPoint target, final double _distance, final double security_distance, int currentLane,
		final IAgent currentRoad, final IAgent linkedRoad, final Double probaChangeLaneUp, final Double probaChangeLaneDown, final Double probaUseLinkedRoad,
		final Boolean rightSide) {
		long t = System.currentTimeMillis();
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		GamaPoint falseTarget =
			target == null ? new GamaPoint(currentRoad.getInnerGeometry().getCoordinates()[currentRoad
				.getInnerGeometry().getCoordinates().length]) : target;
		
		final GamaList indexVals = initMoveAlongPath(agent, path, currentLocation, falseTarget, currentRoad);
		//t31 += System.currentTimeMillis() - t;
		t = System.currentTimeMillis();
		
		if ( indexVals == null ) { return 0.0; }
		int indexSegment = (Integer) indexVals.get(0);
		final int endIndexSegment = (Integer) indexVals.get(1);
	
		if ( indexSegment > endIndexSegment ) { return 0.0; }
		double distance = _distance;
		final GamaGraph graph = (GamaGraph) path.getGraph();
		double realDistance = 0;
		final IShape line = currentRoad.getGeometry();
		final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
	//	t32 += System.currentTimeMillis() - t;
		long t2 = System.currentTimeMillis();
		for ( int j = indexSegment; j <= endIndexSegment; j++ ) {
			t = System.currentTimeMillis();
			GamaPoint pt = null;
			if ( j == endIndexSegment ) {
				pt = falseTarget;
			} else {
				pt = new GamaPoint(coords[j]);
			}
			double dist = pt.euclidianDistanceTo(currentLocation);
			setDistanceToGoal(agent, dist);
			boolean onLinkedRoad = getOnLinkedRoad(agent);
		//	t33 += System.currentTimeMillis() - t;
			t = System.currentTimeMillis();
			if (onLinkedRoad)
				distance = avoidCollisionLinkedRoad(scope, agent, distance, security_distance, currentLocation, falseTarget, currentLane,
					currentRoad, linkedRoad, probaChangeLaneUp, probaChangeLaneDown, probaUseLinkedRoad, rightSide);
			else 
				distance = avoidCollision(scope, agent, distance, security_distance, currentLocation, falseTarget, currentLane,
						currentRoad, linkedRoad, probaChangeLaneUp, probaChangeLaneDown, probaUseLinkedRoad, rightSide);
		//	t34 += System.currentTimeMillis() - t;
			t = System.currentTimeMillis();
			currentLane = (Integer) agent.getAttribute(CURRENT_LANE);
			if ( distance < dist ) {
				final double ratio = distance / dist;
				final double newX = currentLocation.getX() + ratio * (pt.getX() - currentLocation.getX());
				final double newY = currentLocation.getY() + ratio * (pt.getY() - currentLocation.getY());
				GamaPoint npt = new GamaPoint(newX, newY);
				realDistance += currentLocation.euclidianDistanceTo(npt);
				currentLocation.setLocation(npt);
				distance = 0;
			//	t35 += System.currentTimeMillis() - t;
				break;
			} else {
				currentLocation = pt;
				distance = distance - dist;
				realDistance += dist;
			//	t35 += System.currentTimeMillis() - t;
				if ( j == endIndexSegment ) {
					break;
				}
				indexSegment++;
			}
		}
		//t36 += System.currentTimeMillis() - t;
		t = System.currentTimeMillis();
			agent.setLocation(currentLocation);
		path.setSource(currentLocation.copy(scope));
		agent.setAttribute(REAL_SPEED, realDistance / scope.getClock().getStep());
		//t37 += System.currentTimeMillis() - t;
		return _distance == 0.0 ? 1.0 : distance / _distance;
	}

	protected GamaList initMoveAlongPath(final IAgent agent, final IPath path, final GamaPoint currentLocation,
		final GamaPoint falseTarget, final IAgent currentRoad) {
		final GamaList initVals = new GamaList();
		Integer indexSegment = 0;
		Integer endIndexSegment = 0;
		final IList<IShape> edges = path.getEdgeGeometry();
		if ( edges.isEmpty() ) { return null; }
		if ( currentRoad.getInnerGeometry().getNumPoints() == 2 ) {
			indexSegment = 0;
			endIndexSegment = 0;

		} else {
			double distanceS = Double.MAX_VALUE;
			double distanceT = Double.MAX_VALUE;
			IShape line = currentRoad.getGeometry();
			final Point pointS = (Point) currentLocation.getInnerGeometry();
			final Point pointT = (Point) falseTarget.getInnerGeometry();
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
			final int nbSp = coords.length;
			final Coordinate[] temp = new Coordinate[2];
			for ( int i = 0; i < nbSp - 1; i++ ) {
				temp[0] = coords[i];
				temp[1] = coords[i + 1];
				final LineString segment = GeometryUtils.FACTORY.createLineString(temp);
				final double distS = segment.distance(pointS);
				if ( distS < distanceS ) {
					distanceS = distS;
					indexSegment = i + 1;
				}
				final double distT = segment.distance(pointT);
				if ( distT < distanceT ) {
					distanceT = distT;
					endIndexSegment = i + 1;
				}
			}
		}
		initVals.add(indexSegment);
		initVals.add(endIndexSegment);
		return initVals;
	}
}
