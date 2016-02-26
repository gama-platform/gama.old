package ummisco.miro.extension.moving;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.path.*;
import msi.gaml.skills.MovingSkill;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.Coordinate;

@skill(name = "trafficMoving", concept = { IConcept.TRANSPORT, IConcept.SKILL })
public class TrafficMovingSkill extends MovingSkill {

	@action(name = "goto_traffic",
		args = {
			@arg(name = "target",
				type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				optional = false,
				doc = @doc("the location or entity towards which to move.")),
			@arg(name = IKeyword.SPEED,
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "on",
				type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY },
				optional = true,
				doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")),
			@arg(name = "duration", type = IType.FLOAT, optional = true, doc = @doc("duration of the moving")),
			@arg(name = "max_speed", type = IType.FLOAT, optional = true, doc = @doc("speedMoving")),
			@arg(name = "return_path",
				type = IType.BOOL,
				optional = true,
				doc = @doc("if true, return the path followed (by default: false)")) },
		doc = @doc(value = "moves the agent towards the target passed in the arguments while considering the other agents in the network (only for graph topology)",
			returns = "optional: the path followed by the agent.",
			examples = { @example("do gotoTraffic target: one_of (list (species (self))) speed: speed * 2 on: road_network living_space: 2.0;") }))
	public
		void primGotoTraffic(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		ILocation source = agent.getLocation().copy(scope);

		final IShape goal = computeTarget(scope, agent);
		if ( goal == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return;
		}
		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		float duration = ((Double) scope.getArg("duration", IType.FLOAT)).floatValue();
		float maxSpeed = ((Double) scope.getArg("max_speed", IType.FLOAT)).floatValue();

		System.out.println("current path  " + path);
		if ( path == null || !path.getTopology(scope).equals(topo) || !path.getEndVertex().equals(goal) ||
			!path.getStartVertex().equals(source) ) {
			path = topo.pathBetween(scope, source, goal);
		} else {

			if ( topo instanceof GraphTopology ) {
				if ( ((GraphTopology) topo).getPlaces() != path.getGraph() ||
					((GraphTopology) topo).getPlaces().getVersion() != path.getGraphVersion() ) {
					path = topo.pathBetween(scope, source, goal);
				}
			}
		}

		if ( path == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return;
		}
		Boolean returnPath = (Boolean) scope.getArg("return_path", IType.NONE);
		if ( returnPath != null && returnPath ) {
			IPath pathFollowed = moveAlongWay(scope, agent, path, duration, maxSpeed);
			// moveToNextLocAlongPath(scope, agent, path, maxDist, weigths);
			if ( pathFollowed == null ) {
				// scope.setStatus(ExecutionStatus.failure);
				return;
			}
			// scope.setStatus(ExecutionStatus.success);
			return;
		}
		moveAlongWay(scope, agent, path, duration, maxSpeed);
		// scope.setStatus(ExecutionStatus.success);

		return;
	}

	@action(name = "theorical_duration",
		args = {
			@arg(name = "from",
				type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				optional = false,
				doc = @doc("the location or entity towards which to move.")),
			@arg(name = "to",
				type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				optional = false,
				doc = @doc("the location or entity towards which to move.")),
			@arg(name = "on",
				type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY },
				optional = true,
				doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")),
			@arg(name = "max_speed", type = IType.FLOAT, optional = true, doc = @doc("speedMoving")) },
		doc = @doc(value = "moves the agent towards the target passed in the arguments while considering the other agents in the network (only for graph topology)",
			returns = "optional: the path followed by the agent.",
			examples = { @example("do gotoTraffic target: one_of (list (species (self))) speed: speed * 2 on: road_network living_space: 2.0;") }))
	public
		Float computeTheoricalTransportationTime(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		ILocation source = computeFrom(scope, agent);
		final ILocation goal = computeTo(scope, agent);

		if ( goal == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			GamaMap<String, Object> result = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
			result.put("duration", new Integer(-1));
			return new Float(-1);
		}
		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			GamaMap<String, Object> result = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
			result.put("duration", new Integer(-1));
			return new Float(-1);
		}
		IPath path = null;
		float maxSpeed = ((Double) scope.getArg("max_speed", IType.FLOAT)).floatValue();

		path = topo.pathBetween(scope, source, goal);

		if ( path == null ) {
			System.out.println("error -> path null");
			GamaMap<String, Object> result = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
			result.put("duration", new Integer(-2));

			return new Float(-2);
		}
		IList<IAgent> edges = path.getEdgeList();

		if ( edges.size() > 0 ) {
			System.out.print("coucocu " + edges.get(0).getName());
		}

		double duration = 0;
		for ( IAgent agt : edges.iterable(scope) ) {
			double len = ((Double) agt.getAttribute("length")).doubleValue();
			double streetMaxSpeed = ((Double) agt.getAttribute("maxRoadSpeed")).doubleValue();
			duration += len / (streetMaxSpeed > maxSpeed ? maxSpeed : streetMaxSpeed);
		}

		return new Float(duration);

	}

	protected ILocation computeFrom(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Object target = scope.getArg("from", IType.NONE);
		ILocation result = null;
		if ( target != null && target instanceof ILocated ) {
			result = ((ILocated) target).getLocation();
		}
		if ( result == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		return result;
	}

	protected ILocation computeTo(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final Object target = scope.getArg("to", IType.NONE);
		ILocation result = null;
		if ( target != null && target instanceof ILocated ) {
			result = ((ILocated) target).getLocation();
		}
		if ( result == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		return result;
	}

	private IPath moveAlongWay(final IScope scope, final IAgent agent, final IPath path, final double totalDuration,
		final double maxSpeed) {

		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		IList indexVals = initMoveAlongPath(agent, path, currentLocation);
		double duration = totalDuration;
		int index = (Integer) indexVals.get(0);
		int indexSegment = (Integer) indexVals.get(1);
		int endIndexSegment = (Integer) indexVals.get(2);
		currentLocation = (GamaPoint) indexVals.get(3);
		GamaPoint falseTarget = (GamaPoint) indexVals.get(4);
		IList<IShape> edges = path.getEdgeGeometry();
		int nb = edges.size();
		GamaSpatialGraph graph = (GamaSpatialGraph) path.getGraph();
		IList<IShape> segments = GamaListFactory.create(Types.GEOMETRY);
		GamaPoint startLocation = (GamaPoint) agent.getLocation().copy(scope);
		THashMap agents = new THashMap();
		IShape oldline = null;
		IShape oldlineAg = null;
		if ( index < nb ) {
			oldline = edges.get(index);
			oldlineAg = path.getRealObject(oldline);
		}

		for ( int i = index; i < nb; i++ ) {
			IShape line = edges.get(i);
			IShape lineAg = path.getRealObject(line);
			double lineSpeed = computeLineSpeed(lineAg, "speed");

			if ( !oldlineAg.equals(lineAg) ) {
				increaseVehicle(lineAg);
				decreaseVehicle(oldline);
			}
			// current edge
			Coordinate coords[] = line.getInnerGeometry().getCoordinates();
			// weight is 1 by default, otherwise is the distributed edge's weight by length unity

			double movingSpeed = maxSpeed < lineSpeed ? maxSpeed : lineSpeed;

			for ( int j = indexSegment; j < coords.length; j++ ) {
				// pt is the next target
				GamaPoint pt = null;
				if ( i == nb - 1 && j == endIndexSegment ) {
					// The agents has arrived to the target, and he is located in the
					// nearest location to the real target on the graph
					pt = falseTarget;
				} else {
					// otherwise is the extremity of the segment
					pt = new GamaPoint(coords[j]);
				}
				// distance from current location to next target
				double dist = pt.euclidianDistanceTo(currentLocation);
				// For the while, for a high weight, the vehicle moves slowly

				double totalDistance = movingSpeed * duration;
				// that's the real distance to move
				// Agent moves
				if ( duration <= 0 ) {
					break;
				}
				if ( totalDistance < dist ) {
					GamaPoint pto = currentLocation.copy(scope);
					double ratio = totalDistance / dist;
					double newX = currentLocation.x + ratio * (pt.x - currentLocation.x);
					double newY = currentLocation.y + ratio * (pt.y - currentLocation.y);
					currentLocation.setLocation(newX, newY);

					IShape gl = GamaGeometryType.buildLine(pto, currentLocation);
					IAgent a = line.getAgent();
					if ( a != null ) {
						agents.put(gl, a);
					}
					segments.add(gl);

					totalDistance = 0;
					duration = 0;
					break;
				} else if ( totalDistance > dist ) {
					IShape gl = GamaGeometryType.buildLine(currentLocation, pt);
					IAgent a = line.getAgent();
					if ( a != null ) {
						agents.put(gl, a);
					}
					segments.add(gl);

					currentLocation = pt;
					duration = duration - dist / movingSpeed;
					if ( i == nb - 1 && j == endIndexSegment ) {
						break;
					}
					indexSegment++;
				} else {
					IShape gl = GamaGeometryType.buildLine(currentLocation, pt);
					IAgent a = line.getAgent();
					if ( a != null ) {
						agents.put(gl, a);
					}
					segments.add(gl);

					currentLocation = pt;
					duration = 0;
					if ( indexSegment < coords.length - 1 ) {
						indexSegment++;
					} else {
						index++;
					}
					break;
				}
			}
			if ( duration <= 0 ) {
				break;
			}
			indexSegment = 1;
			index++;
			// The current edge is over, agent moves to the next one
		}
		if ( currentLocation.equals(falseTarget) ) {
			currentLocation = (GamaPoint) path.getEndVertex();
			// currentLocation.
		}
		path.setIndexSegementOf(agent, indexSegment);
		path.setIndexOf(agent, index);

		setLocation(agent, currentLocation);
		path.setSource(currentLocation.copy(scope));
		setLocation(agent, currentLocation);
		if ( segments.isEmpty() ) { return null; }
		IPath followedPath =
			PathFactory.newInstance(agent.getTopology(), startLocation, currentLocation, segments, false);
		// new GamaPath(agent.getTopology(), startLocation, currentLocation, segments, false);
		followedPath.setRealObjects(agents);

		return followedPath;

	}

	private double computeLineSpeed(final IShape lineAg, final String laneAttributes) {
		return lineAg == null || !(lineAg instanceof IAgent) ? Double.MAX_VALUE : ((Double) ((IAgent) lineAg)
			.getAttribute(laneAttributes)).doubleValue();

	}

	public void decreaseVehicle(final IShape lineAg) {
		if ( !(lineAg instanceof IAgent) ) { return; }
		int nbVehicle = ((Integer) ((IAgent) lineAg).getAttribute("nbVehicle")).intValue();
		((IAgent) lineAg).setAttribute("nbVehicle", new Integer(nbVehicle - 1));
	}

	public void increaseVehicle(final IShape lineAg) {
		if ( !(lineAg instanceof IAgent) ) { return; }

		int nbVehicle = ((Integer) ((IAgent) lineAg).getAttribute("nbVehicle")).intValue();
		((IAgent) lineAg).setAttribute("nbVehicle", new Integer(nbVehicle + 1));
	}

}
