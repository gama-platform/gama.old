package msi.gaml.extensions.traffic2d;

import java.io.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.path.IPath;
import msi.gama.util.path.GamaPath;
import msi.gaml.operators.*;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.Coordinate;

@vars({ @var(name = "considering_range", type = IType.INT, init = "30"),
	@var(name = "isCalculatedPerimeter", type = IType.BOOL, init = "false"),
	@var(name = "isPassedFalseTarget", type = IType.BOOL, init = "false"),
	@var(name = "currentPerimeter", type = IType.FLOAT, init = "0.0"),
	@var(name = "currentDistance", type = IType.FLOAT, init = "0.0"),
	@var(name = "obstacle_species", type = IType.LIST, init = "[]"),
	@var(name = "background_species", type = IType.LIST, init = "[]"),
	@var(name = IKeyword.SPEED, type = IType.FLOAT, init = "1.0") })
@skill(name = "driving2d")
public class DrivingSkill2d extends MovingSkill {

	public final static String OBSTACLE_SPECIES = "obstacle_species";
	public final static String INITIAL_HEADING = "initial_heading";
	public final static String BACKGROUND_SPECIES = "background_species";
	public final static String CONSIDERING_RANGE = "considering_range";

	public boolean getIsCalculatedPerimeter(final IAgent agent) {
		return (Boolean) agent.getAttribute("isCalculatedPerimeter");
	}

	public void setIsCalculatedPerimeter(final IAgent agent, final boolean isCalculated) {
		agent.setAttribute("isCalculatedPerimeter", isCalculated);
	}

	public boolean getIsPassedFalseTarget(final IAgent agent) {
		return (Boolean) agent.getAttribute("isPassedFalseTarget");
	}

	public void setIsPassedFalseTarget(final IAgent agent, final boolean isPassed) {
		agent.setAttribute("isPassedFalseTarget", isPassed);
	}

	public double getCurrentPerimeter(final IAgent agent) {
		return (Double) agent.getAttribute("currentPerimeter");
	}

	public void setCurrentPerimeter(final IAgent agent, final double inputPerimeter) {
		agent.setAttribute("currentPerimeter", inputPerimeter);
	}

	public double getCurrentDistance(final IAgent agent) {
		return (Double) agent.getAttribute("currentDistance");
	}

	public void setCurrentDistance(final IAgent agent, final double inputDistance) {
		agent.setAttribute("currentDistance", inputDistance);
	}

	@getter(INITIAL_HEADING)
	public int getIntitialHeading(final IAgent agent) {
		return (Integer) agent.getAttribute(INITIAL_HEADING);
	}

	@setter(INITIAL_HEADING)
	public void setIntitialHeading(final IAgent agent, final int initialHeading) {
		agent.setAttribute(INITIAL_HEADING, initialHeading);
		agent.setHeading(initialHeading);
	}

	@getter(CONSIDERING_RANGE)
	public int getConsideringRange(final IAgent agent) {
		return (Integer) agent.getAttribute(CONSIDERING_RANGE);
	}

	@setter(CONSIDERING_RANGE)
	public void setConsideringRange(final IAgent agent, final int consideringRange) {
		agent.setAttribute(CONSIDERING_RANGE, consideringRange);
	}

	@getter(OBSTACLE_SPECIES)
	public GamaList<ISpecies> getObstacleSpecies(final IAgent agent) {
		return (GamaList<ISpecies>) agent.getAttribute(OBSTACLE_SPECIES);
	}

	@setter(OBSTACLE_SPECIES)
	public void setObstacleSpecies(final IAgent agent, final GamaList<ISpecies> os) {
		agent.setAttribute(OBSTACLE_SPECIES, os);
	}

	@getter(BACKGROUND_SPECIES)
	public GamaList<ISpecies> getBackgroundSpecies(final IAgent agent) {
		return (GamaList<ISpecies>) agent.getAttribute(BACKGROUND_SPECIES);
	}

	@setter(BACKGROUND_SPECIES)
	public void setBackgroundSpecies(final IAgent agent, final GamaList<ISpecies> bs) {
		agent.setAttribute(BACKGROUND_SPECIES, bs);
	}

	protected GamaList<ISpecies> computeObstacleSpecies(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return (GamaList<ISpecies>) (scope.hasArg(OBSTACLE_SPECIES) ? scope
			.getListArg(OBSTACLE_SPECIES) : getObstacleSpecies(agent));
	}

	protected GamaList<ISpecies> computeBackgroundSpecies(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return (GamaList<ISpecies>) (scope.hasArg(BACKGROUND_SPECIES) ? scope
			.getListArg(BACKGROUND_SPECIES) : getBackgroundSpecies(agent));
	}

	protected int computeConsideringRange(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return scope.hasArg(CONSIDERING_RANGE) ? scope.getIntArg(CONSIDERING_RANGE)
			: getConsideringRange(agent);
	}

	protected boolean computeIsCalculatedPerimeter(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return scope.hasArg("isCalculatedPerimeter") ? scope.getBoolArg("isCalculatedPerimeter")
			: getIsCalculatedPerimeter(agent);
	}

	/**
	 * Coded by lvminh, updated 2012 oct 30
	 */
	@action(name = "vehicle_goto", args = {
		@arg(name = "target", type = { IType.POINT, IType.GEOMETRY, IType.AGENT }, optional = false, doc = @doc("the location or entity towards which to move.")),
		@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "background", type = { IType.LIST, IType.AGENT, IType.GRAPH,
			IType.GEOMETRY }, optional = false, doc = @doc("list, agent, graph, geometry on which the agent moves (the agent moves inside this geometry)")),
		@arg(name = "on", type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY }, optional = true, doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")) }, doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { "do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}" }))
	@args(names = { "target", IKeyword.SPEED, "on", "target_type" })
	public Integer primVehicleGoto(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		ILocation source = agent.getLocation().copy(scope);
		double maxDist = computeDistance(scope, agent);
		ILocation goal = computeTarget(scope, agent);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
		// System.out.println("obstacle species: " + obsSpecies.toString());
		final GamaList<ISpecies> backgroundSpecies = computeBackgroundSpecies(scope, agent);
		// System.out.println("background species: " + backgroundSpecies.toString());
		final boolean hasBackground =
			backgroundSpecies != null && backgroundSpecies.length(scope) > 0;
		int consideringRange = computeConsideringRange(scope, agent);
		// System.out.println("considering range : " + consideringRange);
		if ( goal == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return -1;
		}

		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return -1;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if ( path == null || !path.getTopology().equals(topo) ||
			!path.getEndVertex().equals(goal) || !path.getStartVertex().equals(source) ) {
			path = topo.pathBetween(scope, source, goal);
		}
		if ( path == null ) {
			scope.setStatus(ExecutionStatus.failure);
			GuiUtils.debug("M: DrivingSKill - can not get the path");
			return -1;
		}

		IList<IShape> edges = path.getEdgeList();
		IShape lineEnd = edges.last(scope);
		GamaPoint falseTarget = (GamaPoint) Punctal._closest_point_to((IShape)path.getEndVertex(), lineEnd);

		Boolean targetType = (Boolean) scope.getArg("target_type", IType.NONE);
		if ( targetType != null && !targetType ) {
			goal = falseTarget;
		}

		boolean isReachedTargetCheck = isReachedTarget(scope, agent, goal);
		if ( isReachedTargetCheck ) { return 2; }
		boolean isReachedFalseTarget = isReachedTarget(scope, agent, falseTarget);
		/**
		 * if (isReachedFalseTarget){
		 * System.out.println("M: DrivingSKill - Reached the false target");
		 * }
		 * /
		 **/
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		// System.out.println("Max distance: " + maxDist);
		/* obstacle agents */
		IList<IAgent> neighbours =
			agent.getTopology().getNeighboursOf(currentLocation, maxDist + consideringRange,
				Different.with());
		GamaList<IAgent> obstacleAgents = new GamaList<IAgent>();
		for ( IAgent ia : neighbours ) {
			if ( obsSpecies.contains(ia.getSpecies()) ) {
				obstacleAgents.add(ia);
			}
		}

		// System.out.println("obstacle agent: " + obstacleAgents.toString());
		/**/

		/* creating candidate points */

		GamaList<CandidateEntry> candidateEntries = new GamaList<CandidateEntry>();
		GamaPoint pointToAdd = null;
		int currentHeadingAngle = agent.getHeading();
		int candidateHeading, dAngle;
		// System.out.println("M: maxDist before: " + maxDist);
		GamaShape agentShape = (GamaShape) agent.getGeometry();
		IShape consideringBackgroundAgentForCurrentPosition = null;
		if ( hasBackground ) {
			for ( ISpecies currentSpecy : backgroundSpecies ) {
				IList<IAgent> overlappingBackgroundWithCurrentPosition =
					msi.gaml.operators.Spatial.Queries.overlapping(scope, currentSpecy, agentShape);
				for ( IAgent ia : overlappingBackgroundWithCurrentPosition ) {
					if ( consideringBackgroundAgentForCurrentPosition == null ) {
						consideringBackgroundAgentForCurrentPosition = ia.getGeometry();
					} else {
						consideringBackgroundAgentForCurrentPosition =
							msi.gaml.operators.Spatial.Operators.union(
								consideringBackgroundAgentForCurrentPosition, ia.getGeometry());
					}
				}
			}
			maxDist =
				distanceToNearestInFront(scope, agent, obstacleAgents, 45,
					consideringBackgroundAgentForCurrentPosition, consideringRange, maxDist);
		} else {
			maxDist =
				distanceToNearestInFront(scope, agent, obstacleAgents, 45, null, consideringRange,
					maxDist);
		}
		// System.out.println(agent.getName() + " M: maxDist after: " + maxDist);
		for ( int i = -4; i <= 4; i++ ) {
			dAngle = i * 13;
			candidateHeading = currentHeadingAngle + dAngle;
			candidateHeading = Maths.checkHeading(candidateHeading);
			pointToAdd =
				new GamaPoint(currentLocation.x + maxDist * Maths.cos(candidateHeading),
					currentLocation.y + maxDist * Maths.sin(candidateHeading));
			// System.out.println("heading: " + currentHeadingAngle + " | " + " candidate angle: " +
			// candidateAngle);
			// System.out.println("current location: " + currentLocation.toString() + " | " +
			// " candidate location: " + pointToAdd.toString());
			GamaShape candidateShape =
				(GamaShape) msi.gaml.operators.Spatial.Transformations.rotated_by(scope,
					agentShape, dAngle);
			candidateShape.setLocation(pointToAdd);
			// check for non-overlapping
			if ( isNonOverlapping(scope, candidateShape, obstacleAgents) ) {
				if ( hasBackground ) {
					// get background agents
					IShape consideringBackgroundAgent =
						consideringBackgroundAgentForCurrentPosition;
					for ( ISpecies currentSpecy : backgroundSpecies ) {
						IList<IAgent> overlappingBackgroundWithCandidatePosition =
							msi.gaml.operators.Spatial.Queries.overlapping(scope, currentSpecy,
								candidateShape);
						for ( IAgent ia : overlappingBackgroundWithCandidatePosition ) {
							if ( consideringBackgroundAgent == null ) {
								consideringBackgroundAgent = ia.getGeometry();
							} else {
								consideringBackgroundAgent =
									msi.gaml.operators.Spatial.Operators.union(
										consideringBackgroundAgent, ia.getGeometry());
							}
						}
					}
					if ( consideringBackgroundAgent != null ) {
						// System.out.println(((GamaShape)consideringBackgroundAgent).getPoints().toString());
						if ( msi.gaml.operators.Spatial.Properties.covered_by(candidateShape,
							consideringBackgroundAgent) ) {
							CandidateEntry candidateEntry =
								new CandidateEntry(pointToAdd, candidateHeading, dAngle);
							candidateEntries.add(candidateEntry);
						}
					}
				} else {
					CandidateEntry candidateEntry =
						new CandidateEntry(pointToAdd, candidateHeading, dAngle);
					candidateEntries.add(candidateEntry);
				}
			}
		}
		/**/

		/* select the nearest */
		if ( candidateEntries.size() > 0 ) {
			CandidateEntry chosenCandidate = null;
			double minDistance = Double.MAX_VALUE;
			double currentDistance = getCurrentDistance(agent);
			for ( CandidateEntry currenEntry : candidateEntries ) {
				GamaPoint currentPoint = currenEntry.candidatePoint;
				double candidateDistance =
					topo.distanceBetween(scope, currentPoint.getLocation(), falseTarget);
				if ( candidateDistance < minDistance && candidateDistance != currentDistance ) {
					chosenCandidate = currenEntry;
					minDistance = candidateDistance;
				}
			}

			if ( chosenCandidate == null ) {
				chosenCandidate = candidateEntries.any(scope);
			}
			// System.out.println("distance: "+ minDistance + ", location: " +
			// chosenCandidate.toString());
			setCurrentDistance(agent, minDistance);
			int newHeading = chosenCandidate.candidateHeading;
			int newRotateAngle = chosenCandidate.dAngle;
			agent.setGeometry(msi.gaml.operators.Spatial.Transformations.rotated_by(scope, agent,
				newRotateAngle));
			agent.setLocation(chosenCandidate.candidatePoint);
			agent.setHeading(newHeading);
		} else {
			/* move back */
			candidateHeading = currentHeadingAngle + 180;
			candidateHeading = Maths.checkHeading(candidateHeading);
			GamaPoint chosenCandidate =
				new GamaPoint(currentLocation.x + maxDist * Maths.cos(candidateHeading),
					currentLocation.y + maxDist * Maths.sin(candidateHeading));
			GamaShape candidateShape =
				(GamaShape) msi.gaml.operators.Spatial.Transformations.at_location(scope,
					agentShape, chosenCandidate);
			if ( isNonOverlapping(scope, candidateShape, obstacleAgents) ) {
				agent.setLocation(chosenCandidate);
			}
			/**/
		}
		scope.setStatus(ExecutionStatus.success);
		if ( isReachedFalseTarget ) { return 1; }
		return 0;
	}

	private boolean isNonOverlapping(final IScope scope, GamaShape candidateShape,
		GamaList<IAgent> obstacleAgents) {
		for ( IAgent ia : obstacleAgents ) {
			if ( msi.gaml.operators.Spatial.Properties.overlaps(scope, candidateShape,
				ia.getGeometry()) ) { return false; }
		}
		return true;
	}

	/**
	 * Coded by lvminh, updated 2012 oct 30
	 */
	@action(name = "pedestrian_goto", args = {
		@arg(name = "target", type = { IType.POINT, IType.GEOMETRY, IType.AGENT }, optional = false, doc = @doc("the location or entity towards which to move.")),
		@arg(name = IKeyword.SPEED, type = IType.FLOAT, optional = true, doc = @doc("the speed to use for this move (replaces the current value of speed)")),
		@arg(name = "background", type = { IType.LIST, IType.AGENT, IType.GRAPH,
			IType.GEOMETRY }, optional = false, doc = @doc("list, agent, graph, geometry on which the agent moves (the agent moves inside this geometry)")),
		@arg(name = "on", type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY }, optional = true, doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")) }, doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { "do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}" }))
	@args(names = { "target", IKeyword.SPEED, "on", "target_type" })
	public Integer primPedestrianGoto(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		ILocation source = agent.getLocation().copy(scope);
		double maxDist = computeDistance(scope, agent);
		ILocation goal = computeTarget(scope, agent);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
		// System.out.println("obstacle species: " + obsSpecies.toString());
		final GamaList<ISpecies> backgroundSpecies = computeBackgroundSpecies(scope, agent);
		// System.out.println("background species: " + backgroundSpecies.toString());
		final boolean hasBackground =
			backgroundSpecies != null && backgroundSpecies.length(scope) > 0;
		int consideringRange = computeConsideringRange(scope, agent);
		// System.out.println("considering range : " + consideringRange);
		if ( goal == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return -1;
		}

		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return -1;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if ( path == null || !path.getTopology().equals(topo) ||
			!path.getEndVertex().equals(goal) || !path.getStartVertex().equals(source) ) {
			path = topo.pathBetween(scope, source, goal);
		}
		if ( path == null ) {
			scope.setStatus(ExecutionStatus.failure);
			GuiUtils.debug("M: DrivingSKill - can not get the path");
			return -1;
		}

		IList<IShape> edges = path.getEdgeList();
		IShape lineEnd = edges.last(scope);
		GamaPoint falseTarget = (GamaPoint) Punctal._closest_point_to((IShape)path.getEndVertex(), lineEnd);

		Boolean targetType = (Boolean) scope.getArg("target_type", IType.NONE);
		if ( targetType != null && !targetType ) {
			goal = falseTarget;
		}

		boolean isReachedTargetCheck = isReachedTarget(scope, agent, goal);
		if ( isReachedTargetCheck ) { return 2; }
		boolean isReachedFalseTarget = isReachedTarget(scope, agent, falseTarget);
		/**
		 * if (isReachedFalseTarget){
		 * System.out.println("M: DrivingSKill - Reached the false target");
		 * }
		 * /
		 **/
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		// System.out.println("Max distance: " + maxDist);
		/* obstacle agents */
		IList<IAgent> neighbours =
			agent.getTopology().getNeighboursOf(currentLocation, maxDist + consideringRange,
				Different.with());
		GamaList<IAgent> obstacleAgents = new GamaList<IAgent>();
		for ( IAgent ia : neighbours ) {
			if ( obsSpecies.contains(ia.getSpecies()) ) {
				obstacleAgents.add(ia);
			}
		}

		// System.out.println("obstacle agent: " + obstacleAgents.toString());
		/**/

		/* creating candidate points */
		try {
			GamaList<CandidateEntry> candidateEntries = new GamaList<CandidateEntry>();
			GamaPoint pointToAdd = null;
			int currentHeadingAngle = agent.getHeading();
			int candidateHeading, dAngle;
			// System.out.println("M: maxDist before: " + maxDist);
			GamaShape agentShape = (GamaShape) agent.getGeometry();
			IShape consideringBackgroundAgentForCurrentPosition = null;
			if ( hasBackground ) {
				for ( ISpecies currentSpecy : backgroundSpecies ) {
					IList<IAgent> overlappingBackgroundWithCurrentPosition =
						msi.gaml.operators.Spatial.Queries.overlapping(scope, currentSpecy,
							agentShape);
					for ( IAgent ia : overlappingBackgroundWithCurrentPosition ) {
						if ( consideringBackgroundAgentForCurrentPosition == null ) {
							consideringBackgroundAgentForCurrentPosition = ia.getGeometry();
						} else {
							consideringBackgroundAgentForCurrentPosition =
								msi.gaml.operators.Spatial.Operators.union(
									consideringBackgroundAgentForCurrentPosition, ia.getGeometry());
						}
					}
				}
				maxDist =
					distanceToNearestInFront(scope, agent, obstacleAgents, 45,
						consideringBackgroundAgentForCurrentPosition, consideringRange, maxDist);
			} else {
				maxDist =
					distanceToNearestInFront(scope, agent, obstacleAgents, 45, null,
						consideringRange, maxDist);
			}
			// System.out.println(agent.getName() + " M: maxDist after: " + maxDist);
			for ( int i = 0; i <= 5; i++ ) {
				dAngle = i * 50;
				candidateHeading = currentHeadingAngle + dAngle;
				candidateHeading = Maths.checkHeading(candidateHeading);
				pointToAdd =
					new GamaPoint(currentLocation.x + maxDist * Maths.cos(candidateHeading),
						currentLocation.y + maxDist * Maths.sin(candidateHeading));
				// System.out.println("heading: " + currentHeadingAngle + " | " +
				// " candidate angle: " + candidateAngle);
				// System.out.println("current location: " + currentLocation.toString() + " | " +
				// " candidate location: " + pointToAdd.toString());
				GamaShape candidateShape =
					(GamaShape) msi.gaml.operators.Spatial.Transformations.rotated_by(scope,
						agentShape, dAngle);
				candidateShape.setLocation(pointToAdd);
				// check for non-overlapping
				if ( isNonOverlapping(scope, candidateShape, obstacleAgents) ) {
					if ( hasBackground ) {
						// get background agents
						IShape consideringBackgroundAgent =
							consideringBackgroundAgentForCurrentPosition;
						for ( ISpecies currentSpecy : backgroundSpecies ) {
							IList<IAgent> overlappingBackgroundWithCandidatePosition =
								msi.gaml.operators.Spatial.Queries.overlapping(scope, currentSpecy,
									candidateShape);
							for ( IAgent ia : overlappingBackgroundWithCandidatePosition ) {
								if ( consideringBackgroundAgent == null ) {
									consideringBackgroundAgent = ia.getGeometry();
								} else {
									consideringBackgroundAgent =
										msi.gaml.operators.Spatial.Operators.union(
											consideringBackgroundAgent, ia.getGeometry());
								}
							}
						}
						if ( consideringBackgroundAgent != null ) {
							// System.out.println(((GamaShape)consideringBackgroundAgent).getPoints().toString());
							if ( msi.gaml.operators.Spatial.Properties.covered_by(candidateShape,
								consideringBackgroundAgent) ) {
								CandidateEntry candidateEntry =
									new CandidateEntry(pointToAdd, candidateHeading, dAngle);
								candidateEntries.add(candidateEntry);
							}
						}
					} else {
						CandidateEntry candidateEntry =
							new CandidateEntry(pointToAdd, candidateHeading, dAngle);
						candidateEntries.add(candidateEntry);
					}
				}
			}
			/**/

			/* select the nearest */
			if ( candidateEntries.size() > 0 ) {
				CandidateEntry chosenCandidate = null;
				double minDistance = Double.MAX_VALUE;
				double currentDistance = getCurrentDistance(agent);
				for ( CandidateEntry currenEntry : candidateEntries ) {
					GamaPoint currentPoint = currenEntry.candidatePoint;
					double candidateDistance =
						topo.distanceBetween(scope, currentPoint.getLocation(), falseTarget);
					if ( candidateDistance < minDistance && candidateDistance != currentDistance ) {
						chosenCandidate = currenEntry;
						minDistance = candidateDistance;
					}
				}

				if ( chosenCandidate == null ) {
					chosenCandidate = candidateEntries.any(scope);
				}
				// System.out.println("distance: "+ minDistance + ", location: " +
				// chosenCandidate.toString());
				setCurrentDistance(agent, minDistance);
				int newHeading = chosenCandidate.candidateHeading;
				agent.setLocation(chosenCandidate.candidatePoint);
				agent.setHeading(newHeading);
			}
			scope.setStatus(ExecutionStatus.success);
			if ( isReachedFalseTarget ) { return 1; }
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}

	private double getCalculatedPerimeter(final IAgent agent) throws GamaRuntimeException {
		boolean isCalculatedPerimeter = getIsCalculatedPerimeter(agent);
		double currentPerimeter = 0;
		if ( isCalculatedPerimeter ) {
			currentPerimeter = getCurrentPerimeter(agent);
		} else {
			currentPerimeter = agent.getPerimeter();
			setCurrentPerimeter(agent, currentPerimeter);
			setIsCalculatedPerimeter(agent, true);
		}
		return currentPerimeter;
	}

	private boolean isReachedTarget(final IScope scope, final IAgent agent, final ILocation goal) {
		double currentPerimeter = getCalculatedPerimeter(agent);
		double maxDist = computeDistance(scope, agent);
		if ( currentPerimeter < maxDist ) {
			currentPerimeter = maxDist;
		}
		double distanceToTarget = agent.euclidianDistanceTo(goal);
		return distanceToTarget <= currentPerimeter;
	}

	private double distanceToNearestInFront(IScope scope, final IAgent agent,
		GamaList<IAgent> obstacleAgents, int dAngle,
		IShape consideringBackgroundAgentForCurrentPosition, int consideringRange,
		double minDistance) {
		int currentHeading = agent.getHeading();
		double currentPointX = agent.getLocation().getX();
		double currentPointY = agent.getLocation().getY();
		double agentRadius = getCalculatedPerimeter(agent) / 4;
		// System.out.println(agent.getName() + ": agentRadius :" + agentRadius);
		GamaPoint headPoint =
			new GamaPoint(currentPointX + agentRadius * Maths.cos(currentHeading), currentPointY +
				agentRadius * Maths.sin(currentHeading));
		boolean isFoundObstacleInFront = false;
		try {
			if ( obstacleAgents != null && obstacleAgents.length(scope) > 0 ) {
				for ( IAgent ia : obstacleAgents ) {
					double obstaclePointX = ia.getLocation().getX();
					double obstaclePointY = ia.getLocation().getY();
					double dy = obstaclePointY - headPoint.getY();
					double dx = obstaclePointX - headPoint.getX();
					double obstacleAngle = Math.atan2(dy, dx) * Maths.toDeg;
					int obstacleHeading = Maths.checkHeading((int) obstacleAngle);
					if ( Maths.abs(obstacleHeading - currentHeading) <= dAngle ) {
						isFoundObstacleInFront = true;
						double obstacleDistance = Math.sqrt(dx * dx + dy * dy);
						// System.out.println(ia.getName() + ":" + obstacleDistance);
						if ( obstacleDistance > 0 && obstacleDistance < minDistance ) {
							minDistance = obstacleDistance;
						}
					}
				}
			}
		} catch (Exception e) {
			GuiUtils.debug("obstacle " + e.getStackTrace());
		}

		try {
			if ( !isFoundObstacleInFront ) {
				if ( consideringBackgroundAgentForCurrentPosition != null ) {
					double xx = consideringRange * Maths.cos(currentHeading);
					double yy = consideringRange * Maths.sin(currentHeading);
					GamaPoint aheadPoint = new GamaPoint(headPoint.x + xx, headPoint.y + yy);
					IShape gl = GamaGeometryType.buildLine(aheadPoint, headPoint);
					if ( gl != null ) {
						IShape interShape =
							msi.gaml.operators.Spatial.Operators.inter(gl,
								consideringBackgroundAgentForCurrentPosition);
						if ( interShape != null ) {
							Coordinate coords[] = interShape.getInnerGeometry().getCoordinates();
							// System.out.println(coords.length);
							for ( int i = 0; i < coords.length; i++ ) {
								double dx = headPoint.getX() - coords[i].x;
								double dy = headPoint.getY() - coords[i].y;
								double borderDistance = Math.sqrt(dx * dx + dy * dy);
								if ( borderDistance > 0.1 && borderDistance < minDistance ) {
									minDistance = borderDistance;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			GuiUtils.debug(agent.getName() + " background ");
		}
		if ( minDistance < 0.5 ) {
			minDistance = 0.5;
		}
		return minDistance;
	}

	@action(name = "read_replay_file", args = { @arg(name = "file_name", type = IType.STRING, optional = false, doc = @doc("File name.")) })
	@args(names = { "file_name" })
	public IList primReadReplayFile(final IScope scope) throws GamaRuntimeException {
		String fileName = (String) scope.getArg("file_name", IType.NONE);

		if ( fileName == null ) { return null; }
		fileName =
			scope.getSimulationScope().getModel()
				.getRelativeFilePath(Cast.asString(scope, fileName), true);
		GuiUtils.debug("1: " + fileName);
		try {
			File file = new File(fileName);
			if ( !file.exists() || !file.isFile() ) { return null; }
			final BufferedReader in = new BufferedReader(new FileReader(file));
			GamaList<Object> result = new GamaList<Object>();
			GamaList<GamaPoint> positionList = null;
			String readingLine = in.readLine();
			// System.out.println("2: " + readingLine);
			while (readingLine != null) {
				String[] tokens = readingLine.split(":");
				if ( tokens.length >= 3 ) {
					positionList = new GamaList<GamaPoint>();
					// System.out.println("3:" + tokens[0] + "|" + tokens[1]);
					String[] positions = tokens[2].split(";");
					// System.out.println("4:" + tokens[1] + "|" + positions.length);
					for ( String positionItem : positions ) {
						String[] coordinates = positionItem.split(",");
						if ( coordinates.length >= 2 ) {
							double x = Double.parseDouble(coordinates[0]);
							double y = Double.parseDouble(coordinates[1]);
							GamaPoint addingPoint = new GamaPoint(x, y);
							positionList.add(addingPoint);
						}
					}
					result.add(positionList);
				}
				readingLine = in.readLine();
			}
			in.close();
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@action(name = "save_replay", args = {
		@arg(name = "file_name", type = IType.STRING, optional = false, doc = @doc("File name.")),
		@arg(name = "agent_information", type = { IType.LIST }, optional = false, doc = @doc("list of position")) })
	@args(names = { "file_name", "agent_information" })
	public Object primSaveReplayStep(final IScope scope) throws GamaRuntimeException {
		String fileName = (String) scope.getArg("file_name", IType.NONE);
		if ( fileName == null ) { return null; }
		fileName =
			scope.getSimulationScope().getModel()
				.getRelativeFilePath(Cast.asString(scope, fileName), true);
		GuiUtils.debug("1: " + fileName);
		IList agent_information = (IList) scope.getArg("agent_information", IType.NONE);
		if ( agent_information == null || agent_information.size() < 3 ) { return null; }
		GuiUtils.debug("2: " + agent_information.size());
		String currentFileName =
			scope.getSimulationScope().getModel()
				.getRelativeFilePath(Cast.asString(scope, "."), true) +
				"/log/" + fileName;
		GuiUtils.debug("3: " + currentFileName);
		try {
			// File file = new File(fileName);
			// if (!file.exists() || !file.isFile()) return null;
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(currentFileName));
			out.writeObject(agent_information);
			out.close();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@action(name = "read_replay", args = { @arg(name = "file_name", type = IType.STRING, optional = false, doc = @doc("File name.")) })
	@args(names = { "file_name" })
	public IList primReadReplay(final IScope scope) throws GamaRuntimeException {
		String fileName = (String) scope.getArg("file_name", IType.NONE);

		if ( fileName == null ) { return null; }
		fileName =
			scope.getSimulationScope().getModel()
				.getRelativeFilePath(Cast.asString(scope, fileName), true);
		// System.out.println("1: " + fileName);
		try {
			File file = new File(fileName);
			if ( !file.exists() || !file.isFile() ) { return null; }
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			GamaList result = (GamaList) in.readObject();
			in.close();
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}

class CandidateEntry {

	public GamaPoint candidatePoint;
	public int candidateHeading;
	public int dAngle;

	public CandidateEntry(GamaPoint inputPoint, int inputHeading, int inputDAngle) {
		this.candidatePoint = inputPoint;
		this.candidateHeading = inputHeading;
		this.dAngle = inputDAngle;
	}
}