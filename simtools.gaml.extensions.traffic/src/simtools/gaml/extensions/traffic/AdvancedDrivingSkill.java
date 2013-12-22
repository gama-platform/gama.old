package simtools.gaml.extensions.traffic;

import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.In;
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
import msi.gama.util.*;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.path.*;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial.Operators;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.skills.MovingSkill;
import msi.gaml.types.*;

import com.vividsolutions.jts.geom.*;

@vars({
	@var(name = IKeyword.SPEED, type = IType.FLOAT, init = "1.0", doc = @doc("the speed of the agent (in meter/second)")),
	@var(name = "security_distance_coeff", type = IType.FLOAT, init = "1.0", doc = @doc("the coefficient for the computation of the the min distance between two drivers (according to the vehicle speed - security_distance = 1°m + security_distance_coeff *real_speed )")),
	@var(name = "real_speed", type = IType.FLOAT, init = "0.0", doc = @doc("real speed of the agent (in meter/second)")),
	@var(name = "current_lane", type = IType.INT, init = "0", doc = @doc("the current lane on which the agent is")),
	@var(name = "vehicle_length", type = IType.FLOAT, init = "0.0", doc = @doc("the length of the vehicle (in meters)")),
	@var(name = "current_road", type = IType.AGENT, doc = @doc("current road on which the agent is")),
	@var(name = "proba_lane_change_up", type = IType.FLOAT,init = "1.0",doc = @doc("probability to change lane to a upper lane (left lane if right side driving) if necessary")),
	@var(name = "proba_lane_change_down", type = IType.FLOAT,init = "1.0",doc = @doc("probability to change lane to a lower lane (right lane if right side driving) if necessary")),
	@var(name = "proba_respect_priorities", type = IType.FLOAT,init = "1.0",doc = @doc("probability to respect priority (right or left) laws")),
	@var(name = "proba_respect_stops", type = IType.LIST, of = IType.FLOAT,init = "[]",doc = @doc("probability to respect stop laws - one value for each type of stop")),
	@var(name = "proba_block_node", type = IType.FLOAT, init = "0.0",doc = @doc("probability to block a node (do not let other driver cross the crossroad)")),
	@var(name = "right_side_driving", type = IType.BOOL, init = "true",doc = @doc("are drivers driving on the right size of the road?"))})
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
	public final static String RIGHT_SIDE_DRIVING = "right_side_driving";
	
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

	
	@action(name = "advanced_follow_driving", args = {
			@arg(name = "path", type = IType.PATH, optional = false, doc = @doc("a path to be followed.")),
			@arg(name = "target", type = IType.POINT, optional = true, doc = @doc("the target to reach")),
			@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "time", type = IType.FLOAT, optional = true, doc = @doc("time to travel"))}, 
			doc = @doc(value = "moves the agent towards along the path passed in the arguments while considering the other agents in the network (only for graph topology)", returns = "the remaining time", examples = { "do osm_follow path: the_path on: road_network;" }))
		public Double primAdvancedFollow(final IScope scope) throws GamaRuntimeException {
			final IAgent agent = getCurrentAgent(scope);
			final double security_distance = getSecurityDistanceCoeff(agent) * getRealSpeed(agent) + 1;
			final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
			final Double t = scope.hasArg("time") ? scope.getFloatArg("time") : 1.0;
			
			final double maxDist = computeDistance(scope, agent,s,t);
			final int currentLane = getCurrentLane(agent);
			final Double probaChangeLaneUp = getProbaLaneChangeUp(agent);
			final Double probaChangeLaneDown = getProbaLaneChangeDown(agent);
			final Boolean rightSide = getRightSideDriving(agent);
			final IAgent currentRoad = (IAgent) getCurrentRoad(agent);
			final ITopology topo = computeTopology(scope, agent);
			if ( topo == null ) {
				return 0.0;
			}
			final GamaPoint target = scope.hasArg("target") ? (GamaPoint) scope.getArg("target", IType.NONE) : null;
			final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
			if ( path != null && !path.getEdgeList().isEmpty() ) {
				double tps = t * moveToNextLocAlongPathOSM(scope, agent, path, target, maxDist, security_distance, currentLane, currentRoad,probaChangeLaneUp,probaChangeLaneDown,rightSide);	
				if (tps < 1.0)
					agent.setAttribute(REAL_SPEED, this.getRealSpeed(agent) / ((1 - tps)));
				
				return tps;
			}
			return 0.0;
		}
		

		@action(name = "is_ready_next_road", args = {
			@arg(name = "road", type = IType.AGENT, optional = false, doc = @doc("the road to test")),
			@arg(name = "lane", type = IType.INT, optional = false, doc = @doc("the lane to test"))}, 
			doc = @doc(value = "action to test if the driver can take the given road at the given lane", returns = "true (the driver can take the road) or false (the driver cannot take the road)", examples = { "do is_ready_next_road road: a_road lane: 0;" }))
		public Boolean primIsReadyNextRoad(final IScope scope) throws GamaRuntimeException {
			IAgent road = (IAgent) scope.getArg("road", IType.AGENT);
			Integer lane = (Integer) scope.getArg("lane", IType.INT);
			IAgent driver = getCurrentAgent(scope);
			double vL = getVehiculeLength(driver);
			double secDistCoeff = getSecurityDistanceCoeff(driver);
			double probaBlock = getProbaBlockNode(driver);
			boolean testBlockNode = Random.opFlip(scope, probaBlock);
			IAgent node = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
			Map<IAgent,List<IAgent>> block = ((Map<IAgent,List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK));
			List<IAgent> ba = new GamaList<IAgent>(block.keySet());
			for (IAgent dr : ba) {
				if (!dr.getLocation().equals(node.getLocation()))
					block.remove(dr);
			}
			return isReadyNextRoad(scope, road,driver,secDistCoeff, vL,block) && (testBlockNode || nextRoadTestLane(driver,road, lane, secDistCoeff, vL));
		}
		
		public Boolean isReadyNextRoad(final IScope scope,IAgent road,IAgent driver,double secDistCoeff, double vL,Map<IAgent,List<IAgent>> block) throws GamaRuntimeException {
			IAgent theNode  = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
			List<List> stops =  (List<List>) theNode.getAttribute(RoadNodeSkill.STOP);
			List<Double> respectsStops = getRespectStops(driver);
			IAgent currentRoad = (IAgent) driver.getAttribute(CURRENT_ROAD);
			for (int i = 0; i < stops.size(); i++) {
				Boolean stop = stops.get(i).contains(currentRoad);
				if (stop && ((respectsStops.size() <= i) || Random.opFlip(scope, respectsStops.get(i)))) {
					return false;
				}
			}
			for (List<IAgent> rd : block.values()) {
				if (rd.contains(currentRoad))
					return false;
			}
			Boolean rightSide = getRightSideDriving(driver);
			
		
			double angleRef = Punctal.angleInDegreesBetween((GamaPoint)theNode.getLocation(),(GamaPoint)currentRoad.getLocation(), (GamaPoint)road.getLocation());
			List<IAgent> roadsIn = (List) theNode.getAttribute(RoadNodeSkill.ROADS_IN);
			if (!Random.opFlip(scope, getRespectPriorities(driver))) return true;
			for (IAgent rd: roadsIn) {
				if (rd != currentRoad) {
					double angle = Punctal.angleInDegreesBetween((GamaPoint)theNode.getLocation(),(GamaPoint)currentRoad.getLocation(), (GamaPoint)rd.getLocation());
					if ((rightSide &&angle > angleRef) || (!rightSide &&angle < angleRef)) {
						int nbL = (Integer) rd.getAttribute(RoadSkill.LANES);
						List<List<IAgent>> agentsOn = (List) rd.getAttribute(RoadSkill.AGENTS_ON);
						for (int i = 0 ; i < nbL ; i++) {
							for (IAgent pp : agentsOn.get(i)) {
								double vL2 = getVehiculeLength(pp);
								if (Maths.round(getRealSpeed(pp),2) > 0.0 && (pp.euclidianDistanceTo(driver) < ((1 + secDistCoeff * getRealSpeed(pp)) + vL2/2 + vL/2))) {
									return false;
								}
							}
						}
					}
				}
			}
			return true;
		}
		
		public boolean nextRoadTestLane(IAgent driver,IAgent road,int lane, double secDistCoeff, double vL){
			double realSpeed = getRealSpeed(driver);
			double secDist = 1 + realSpeed* secDistCoeff;
			
			List<IAgent> drivers =(List) ((List) road.getAttribute(RoadSkill.AGENTS_ON)).get(lane);
			for (IAgent dr: drivers){
				if (dr == driver) continue;
				if (dr.euclidianDistanceTo(driver) < (vL/2 + secDist + getVehiculeLength(dr)/2)) {
					return false;
				}
			}
			return true;
		}
		
		@action(name = "lane_choice", args = {
				@arg(name = "road", type = IType.AGENT, optional = false, doc = @doc("the road on which to choose the lane"))}, 
				doc = @doc(value = "action to choose a lane", returns = "the chosen lane, return -1 if no lane can be taken", examples = { "do lane_choice road: a_road;" }))
			public Integer primLanChoice(final IScope scope) throws GamaRuntimeException {
				IAgent road = (IAgent) scope.getArg("road", IType.AGENT);
				Integer lanes = (Integer) road.getAttribute(RoadSkill.LANES);
				IAgent driver = getCurrentAgent(scope);
				Integer currentLane = (Integer) getCurrentLane(driver);
				IAgent currentRoad = (IAgent) getCurrentRoad(driver);
				IAgent node = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
				double vL = getVehiculeLength(driver);
				double secDistCoeff = getSecurityDistanceCoeff(driver);
				double probaBlock = getProbaBlockNode(driver);
				boolean testBlockNode = Random.opFlip(scope, probaBlock);
				Map<IAgent,List<IAgent>> block = ((Map<IAgent,List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK));
				List<IAgent> ba = new GamaList<IAgent>(block.keySet());
				List<IAgent> roadsIn = (List<IAgent>)node.getAttribute(RoadNodeSkill.ROADS_IN);
				
				for (IAgent dr : ba) {
					if (!dr.getLocation().equals(node.getLocation()))
						block.remove(dr);
				}
				
				boolean ready = isReadyNextRoad(scope, road,driver,secDistCoeff, vL, block);
				if (!ready) return -1;
				if (lanes == 0)  {
					int lane = testBlockNode ||nextRoadTestLane(driver,road, 0, secDistCoeff, vL) ? 0 : -1;
					if (lane != -1 )
						addBlockingDriver(0, testBlockNode, driver, currentRoad, road, node, roadsIn,block); 
					return lane;
				}
					
				int cvTmp = Math.min(currentLane, lanes - 1);
				int cv = testBlockNode ||nextRoadTestLane(driver,road, cvTmp, secDistCoeff, vL) ? cvTmp : -1;
				if (cv != -1) {
					addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn,block); 
					return cv;
				}
				double probaLaneChangeDown = getProbaLaneChangeDown(driver);
				double probaLaneChangeUp = getProbaLaneChangeUp(driver);
				boolean changeDown = Random.opFlip(scope, probaLaneChangeDown);
				boolean changeUp = Random.opFlip(scope, probaLaneChangeUp);
				if (changeDown || changeUp) {
					for (int i = 0; i < lanes; i++) {
						int l1 = cvTmp - i;
						if (l1 >= 0 && changeDown) {
							cv = testBlockNode ||nextRoadTestLane(driver,road, l1, secDistCoeff, vL) ? l1 : -1;
							if (cv != -1) {
								addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn,block); 
								return cv; 
							}
						}
						int l2 = cvTmp + i;
						if (l2 < lanes && changeUp) {
							cv = testBlockNode ||nextRoadTestLane(driver,road, l2, secDistCoeff, vL) ? l2 : -1;
							if (cv != -1) {
								addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn,block); 
								return cv; 
							}
						}
					}
				}
				return cv;
		}
		
		public void addBlockingDriver(int cv, boolean testBlockNode, IAgent driver, IAgent currentRoad, IAgent road, IAgent node, List<IAgent> roadsIn,Map<IAgent,List<IAgent>> block) {
			if (testBlockNode && roadsIn.size() > 1) {
				List<IAgent> rb = roadBlocked(currentRoad,road,node,roadsIn);
				if (! rb.isEmpty())
					block.put(driver,rb );
			}
		}
	
		public List<IAgent> roadBlocked(IAgent roadIn, IAgent roadOut, IAgent node, List<IAgent> roadsIn) {
			List<IAgent> roadsBlocked = new GamaList<IAgent>();
			for (IAgent rd : roadsIn) {
				if (rd != roadIn && !rd.getLocation().equals(roadOut.getLocation()))
					roadsBlocked.add(rd);
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

		protected double computeDistance(final IScope scope, final IAgent agent, final double s,final double t) throws GamaRuntimeException {
			
			return s * t * scope.getClock().getStep();
		}

		private double avoidCollision(final IScope scope, final IAgent agent, final double distance,
				final double security_distance, final GamaPoint currentLocation, final GamaPoint target,
				final int lane, final IAgent currentRoad, boolean changeLane) {
				IList agents = (IList) ((GamaList) currentRoad.getAttribute(RoadSkill.AGENTS_ON)).get(lane);
				double vL = getVehiculeLength(agent);
				if (agents.size() < 2) {
					if (changeLane && distance < vL)
						return 0;
					return distance;
				}
				double distanceMax = distance + security_distance +  vL;
				
				List<IAgent> agsFiltered = new GamaList(agent.getTopology().getNeighboursOf(scope,agent.getLocation(), distanceMax, In.list(scope, agents)));
				
				if (agsFiltered.isEmpty())
					return distance;
				
				double distanceToGoal = agent.euclidianDistanceTo(target);//getDistanceToGoal(agent);
				//double distanceMax = distance + security_distance +  0.5 * getVehiculeLength(agent);
				IAgent nextAgent = null;
				double minDiff = Double.MAX_VALUE;
				for (IAgent ag : agsFiltered) {
					double dist = ag.euclidianDistanceTo(target);
					double diff = (distanceToGoal - dist) ;
					if (changeLane && diff < vL) return 0;
					if (diff <  minDiff && ((changeLane && diff >= 0) || (! changeLane && diff > 0)) )  {
						minDiff = diff;
						nextAgent = ag;
					}
				}
				
				if (nextAgent == null)
					return distance;
				
				double realDist = Math.min(distance, minDiff - security_distance - 0.5 * vL - 0.5 * getVehiculeLength(nextAgent) );
				if (changeLane && realDist < vL) 
					return 0;
				return Math.max(0.0,realDist );
			}
		
		private void changeLane(final IScope scope, final IAgent agent, int previousLane, int newLane,final IAgent currentRoad,List agentOn ){
			agent.setAttribute(CURRENT_LANE, newLane);
			((List)(agentOn.get(previousLane))).remove(agent);
			((List)(agentOn.get(newLane))).add(agent);
		}
		private double avoidCollision(final IScope scope, final IAgent agent, final double distance,
				final double security_distance, final GamaPoint currentLocation, final GamaPoint target,
				final int lane, final IAgent currentRoad,final Double probaChangeLaneUp,final Double probaChangeLaneDown, final Boolean rightSide) {
				double distMax = 0;
				int newLane = lane;
				List agentOn = (List) currentRoad.getAttribute(RoadSkill.AGENTS_ON);
				if (lane > 0 && GAMA.getRandom().next() < probaChangeLaneDown) {
					double val = avoidCollision(scope,agent,distance,security_distance, currentLocation,target,lane - 1, currentRoad, true);
					//System.out.println(agent + " lane - 1 : " + val);
					if (val == distance) {
						newLane = lane - 1;
						changeLane(scope, agent, lane, newLane,currentRoad,agentOn );
						return distance;
					}
					if (val > distMax && val > 0) {
						newLane = lane - 1;
						distMax = val;
					}
				}
				double val = avoidCollision(scope,agent,distance,security_distance, currentLocation,target,lane, currentRoad, false);
			//	System.out.println(agent + " lane : " + val);
				if (val == distance) {
					//changeLane(scope, agent, lane, newLane,currentRoad,agentOn );
					return distance;
				}
				if (val >= distMax) {
					distMax = val;
					newLane = lane;
				}
				if (lane <  ((Integer) currentRoad.getAttribute(RoadSkill.LANES) - 1) && GAMA.getRandom().next() < probaChangeLaneUp) {
					val = avoidCollision(scope,agent,distance,security_distance, currentLocation,target,lane+1, currentRoad, true);
					//System.out.println(agent + " lane + 1 : " + val);
					if (val > distMax && val > 0) {
						distMax = val; 
						newLane = lane + 1;
					}
				}

				if (lane != newLane) changeLane(scope, agent, lane, newLane,currentRoad,agentOn );
				//System.out.println(agent + " newLane : " + newLane + " distMax: " + distMax);
				return distMax;
			}
		

		private GamaPoint computeRealTarget(final IAgent agent, 
			final double security_distance, final GamaPoint currentLocation, final GamaPoint target,
			final int lane, final IAgent currentRoad) {
			
		//	System.out.println("currentRoad : " + currentRoad);
		//	System.out.println("currentRoad.getAttribute(agents_on)) : " + currentRoad.getAttribute("agents_on"));
			
			List<IAgent> agents = (List<IAgent>) ((GamaList) currentRoad.getAttribute(RoadSkill.AGENTS_ON)).get(lane);
			if (agents.size() < 2)
				return target;
			//System.out.println("agents : " + agents);
			double distanceToGoal = agent.euclidianDistanceTo(target);//getDistanceToGoal(agent);
			IAgent nextAgent = null;
			double minDiff = Double.MAX_VALUE;
			for (IAgent ag : agents) {
				if (ag == agent) continue;
				double dist = ag.euclidianDistanceTo(target);//getDistanceToGoal(ag);
				double diff = (distanceToGoal - dist) ;
				if (dist < distanceToGoal && diff <  minDiff ) {
					minDiff = diff;
					nextAgent = ag;
				}
			}
			if (nextAgent == null)
				return target;
			//System.out.println("currentRoad.getAttribute(agents_on)).get(index - 1) : " + ((GamaList) ((GamaList) currentRoad.getAttribute("agents_on")).get(lane)).get(index - 1));
			double buff_dist = getVehiculeLength(agent) + getVehiculeLength(nextAgent) + security_distance;
			IShape shape = Transformations.enlarged_by(nextAgent.getLocation(), buff_dist);
			IShape shapeInter = Operators.inter(currentRoad, shape);
			//System.out.println("ICI\nnextAgent.getLocation() : " + nextAgent.getLocation());
			//System.out.println("currentRoad : " + currentRoad.getGeometry());
			
			
			//return target; 
			if (shapeInter != null) {
				return (GamaPoint) Punctal._closest_point_to(currentLocation, shapeInter);
			}
			return target;
		}

		private double moveToNextLocAlongPathOSM(final IScope scope, final IAgent agent, final IPath path, final GamaPoint target, final double _distance, final double security_distance, int currentLane, final  IAgent currentRoad, final Double probaChangeLaneUp, final Double probaChangeLaneDown, final Boolean rightSide ) {
			
			GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
			GamaPoint falseTarget = target == null ? new GamaPoint(currentRoad.getInnerGeometry().getCoordinates()[currentRoad.getInnerGeometry().getCoordinates().length]) : target;
			/*for (Object ag : path.getEdgeList()) {
				System.out.println(ag + " -> " + ((IAgent) ag).getGeometry());
			}*/
		
			//final GamaPoint falseTarget = pt_target; //computeRealTarget(agent, security_distance, currentLocation, pt_target, currentLane, currentRoad);
			final GamaList indexVals =  initMoveAlongPath(agent, path, currentLocation, falseTarget, currentRoad);
			if (indexVals == null) return 0.0;
			int indexSegment = (Integer) indexVals.get(0);
			final int endIndexSegment = (Integer) indexVals.get(1);
			//System.out.println("currentRoad : " + currentRoad.getGeometry());
			//System.out.println("currentLocation : " + currentLocation + " falseTarget : " + falseTarget);
			//System.out.println("indexSegment : " + indexSegment + " endIndexSegment : " + endIndexSegment);
			
			if (indexSegment > endIndexSegment) {
				return 0.0;
			}
			double distance = _distance;
			final GamaGraph graph = (GamaGraph) path.getGraph();
			double realDistance = 0;
			final IShape line = currentRoad.getGeometry();
			final Coordinate coords[] = line.getInnerGeometry().getCoordinates(); 
			
			
			for ( int j = indexSegment; j <= endIndexSegment; j++ ) {
				GamaPoint pt = null;
				if ( j == endIndexSegment ) {
					pt = falseTarget;
				} else {
					pt = new GamaPoint(coords[j]);
				}
			//	System.out.println("j : " + j + " endIndexSegment : " + endIndexSegment + " pt : " + pt);
				
				double dist = pt.euclidianDistanceTo(currentLocation);
				
				distance =
						avoidCollision(scope, agent, distance, security_distance, currentLocation, falseTarget, currentLane, currentRoad,probaChangeLaneUp,probaChangeLaneDown ,rightSide);
				currentLane = (Integer) agent.getAttribute(CURRENT_LANE);
				if ( distance < dist ) {
					final double ratio = distance / dist;
					final double newX = currentLocation.getX() + ratio * (pt.getX() - currentLocation.getX());
					final double newY = currentLocation.getY() + ratio * (pt.getY() - currentLocation.getY());
					GamaPoint npt = new GamaPoint(newX,newY);
					realDistance += currentLocation.euclidianDistanceTo(npt);
					currentLocation.setLocation(npt);
					distance = 0;
					break;
				} else {
					currentLocation = pt;
					distance = distance - dist;
					realDistance += dist; 
					if (j == endIndexSegment ) {
						break;
					}
					indexSegment++;
				}
			}
			//path.setIndexSegementOf(agent, indexSegment);
			agent.setLocation(currentLocation);
			path.setSource(currentLocation.copy(scope));
			agent.setAttribute(REAL_SPEED, realDistance / scope.getClock().getStep());
			setDistanceToGoal(agent, currentLocation.euclidianDistanceTo(falseTarget));
			//System.out.println("_distance : " + _distance);
			//System.out.println("distance : " + distance);
			return _distance == 0.0 ? 1.0 : (distance / _distance) ;
		}
		
		protected GamaList initMoveAlongPath(final IAgent agent, final IPath path, final GamaPoint currentLocation,final GamaPoint falseTarget, final IAgent currentRoad) {
			final GamaList initVals = new GamaList();
			Integer indexSegment = 0;
			Integer endIndexSegment = 0;
			final IList<IShape> edges = path.getEdgeGeometry();
			if (edges.isEmpty()) return null;
			//final int nb = edges.size();
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
					final LineString segment = GeometryUtils.factory.createLineString(temp);
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
