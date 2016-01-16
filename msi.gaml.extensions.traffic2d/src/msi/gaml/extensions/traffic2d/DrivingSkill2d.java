/*********************************************************************************************
 *
 *
 * 'DrivingSkill2d.java', in plugin 'msi.gaml.extensions.traffic2d', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.extensions.traffic2d;

import java.io.*;
import java.util.Collection;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
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
import msi.gama.util.path.*;
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
		setHeading(agent, initialHeading);
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
		return (GamaList<ISpecies>) (scope.hasArg(OBSTACLE_SPECIES) ? scope.getListArg(OBSTACLE_SPECIES)
			: getObstacleSpecies(agent));
	}

	protected GamaList<ISpecies> computeBackgroundSpecies(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		return (GamaList<ISpecies>) (scope.hasArg(BACKGROUND_SPECIES) ? scope.getListArg(BACKGROUND_SPECIES)
			: getBackgroundSpecies(agent));
	}

	protected int computeConsideringRange(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		return scope.hasArg(CONSIDERING_RANGE) ? scope.getIntArg(CONSIDERING_RANGE) : getConsideringRange(agent);
	}

	protected boolean computeIsCalculatedPerimeter(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		return scope.hasArg("isCalculatedPerimeter") ? scope.getBoolArg("isCalculatedPerimeter")
			: getIsCalculatedPerimeter(agent);
	}

	/**
	 * Coded by lvminh, updated 2012 oct 30
	 */
	@action(name = "vehicle_goto",
		args = {
			@arg(name = "target",
				type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				optional = false,
				doc = @doc("the location or entity towards which to move.")),
			@arg(name = IKeyword.SPEED,
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "background",
				type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY },
				optional = false,
				doc = @doc("list, agent, graph, geometry on which the agent moves (the agent moves inside this geometry)")),
			@arg(name = "on",
				type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY },
				optional = true,
				doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")) },
		doc = @doc(value = "moves the agent towards the target passed in the arguments.",
			returns = "the path followed by the agent.",
			examples = { @example("do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	@args(names = { "target_type" })
	// @args(names = { "target", IKeyword.SPEED, "on", "target_type" })
	public
		Integer primVehicleGoto(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ILocation source = agent.getLocation().copy(scope);
		double maxDist = computeDistance(scope, agent);
		IShape goal = computeTarget(scope, agent);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
		// System.out.println("obstacle species: " + obsSpecies.toString());
		final GamaList<ISpecies> backgroundSpecies = computeBackgroundSpecies(scope, agent);
		// System.out.println("background species: " + backgroundSpecies.toString());
		final boolean hasBackground = backgroundSpecies != null && backgroundSpecies.length(scope) > 0;
		final int consideringRange = computeConsideringRange(scope, agent);
		// System.out.println("considering range : " + consideringRange);
		if ( goal == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return -1;
		}

		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return -1;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if ( path == null || !path.getTopology(scope).equals(topo) || !path.getEndVertex().equals(goal) ||
			!path.getStartVertex().equals(source) ) {
			path = topo.pathBetween(scope, source, goal);
		}
		if ( path == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			scope.getGui().debug("M: DrivingSKill - can not get the path");
			return -1;
		}

		final IList<IShape> edges = path.getEdgeGeometry();
		final IShape lineEnd = edges.lastValue(scope);
		final GamaPoint falseTarget = (GamaPoint) Punctal._closest_point_to((IShape) path.getEndVertex(), lineEnd);

		final Boolean targetType = (Boolean) scope.getArg("target_type", IType.NONE);
		if ( targetType != null && !targetType ) {
			goal = falseTarget;
		}

		final boolean isReachedTargetCheck = isReachedTarget(scope, agent, goal.getLocation());
		if ( isReachedTargetCheck ) { return 2; }
		final boolean isReachedFalseTarget = isReachedTarget(scope, agent, falseTarget);
		/**
		 * if (isReachedFalseTarget){
		 * System.out.println("M: DrivingSKill - Reached the false target");
		 * }
		 * /
		 **/
		final GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		// System.out.println("Max distance: " + maxDist);
		/* obstacle agents */
		final Collection<IAgent> neighbours =
			agent.getTopology().getNeighboursOf(scope, currentLocation, maxDist + consideringRange, Different.with());
		final IList<IAgent> obstacleAgents = GamaListFactory.create(Types.AGENT);
		for ( IAgent ia : neighbours ) {
			if ( obsSpecies.contains(ia.getSpecies()) ) {
				obstacleAgents.add(ia);
			}
		}

		// System.out.println("obstacle agent: " + obstacleAgents.toString());
		/**/

		/* creating candidate points */

		final IList<CandidateEntry> candidateEntries = GamaListFactory.create(Types.NO_TYPE);
		GamaPoint pointToAdd = null;
		final int currentHeadingAngle = getHeading(agent);
		int candidateHeading, dAngle;
		// System.out.println("M: maxDist before: " + maxDist);
		final GamaShape agentShape = (GamaShape) agent.getGeometry();
		IShape consideringBackgroundAgentForCurrentPosition = null;
		if ( hasBackground ) {
			for ( final ISpecies currentSpecy : backgroundSpecies ) {
				final IList<IAgent> overlappingBackgroundWithCurrentPosition =
					msi.gaml.operators.Spatial.Queries.overlapping(scope, currentSpecy, agentShape);
				for ( final IAgent ia : overlappingBackgroundWithCurrentPosition.iterable(scope) ) {
					if ( consideringBackgroundAgentForCurrentPosition == null ) {
						consideringBackgroundAgentForCurrentPosition = ia.getGeometry();
					} else {
						consideringBackgroundAgentForCurrentPosition =
							msi.gaml.operators.Spatial.Operators.union(scope,
								consideringBackgroundAgentForCurrentPosition, ia.getGeometry());
					}
				}
			}
			maxDist =
				distanceToNearestInFront(scope, agent, obstacleAgents, 45,
					consideringBackgroundAgentForCurrentPosition, consideringRange, maxDist);
		} else {
			maxDist = distanceToNearestInFront(scope, agent, obstacleAgents, 45, null, consideringRange, maxDist);
		}
		// System.out.println(agent.getName() + " M: maxDist after: " + maxDist);
		for ( int i = -4; i <= 4; i++ ) {
			dAngle = i * 13;
			candidateHeading = currentHeadingAngle + dAngle;
			candidateHeading = Maths.checkHeading(candidateHeading);
			pointToAdd =
				new GamaPoint(currentLocation.x + maxDist * Maths.cos(candidateHeading), currentLocation.y + maxDist *
					Maths.sin(candidateHeading));
			// System.out.println("heading: " + currentHeadingAngle + " | " + " candidate angle: " +
			// candidateAngle);
			// System.out.println("current location: " + currentLocation.toString() + " | " +
			// " candidate location: " + pointToAdd.toString());
			final GamaShape candidateShape =
				(GamaShape) msi.gaml.operators.Spatial.Transformations.rotated_by(scope, agentShape, dAngle);
			candidateShape.setLocation(pointToAdd);
			// check for non-overlapping
			if ( isNonOverlapping(scope, candidateShape, obstacleAgents) ) {
				if ( hasBackground ) {
					// get background agents
					IShape consideringBackgroundAgent = consideringBackgroundAgentForCurrentPosition;
					for ( final ISpecies currentSpecy : backgroundSpecies ) {
						final IList<IAgent> overlappingBackgroundWithCandidatePosition =
							msi.gaml.operators.Spatial.Queries.overlapping(scope, currentSpecy, candidateShape);
						for ( final IAgent ia : overlappingBackgroundWithCandidatePosition.iterable(scope) ) {
							if ( consideringBackgroundAgent == null ) {
								consideringBackgroundAgent = ia.getGeometry();
							} else {
								consideringBackgroundAgent =
									msi.gaml.operators.Spatial.Operators.union(scope, consideringBackgroundAgent,
										ia.getGeometry());
							}
						}
					}
					if ( consideringBackgroundAgent != null ) {
						// System.out.println(((GamaShape)consideringBackgroundAgent).getPoints().toString());
						if ( msi.gaml.operators.DeprecatedOperators.covered_by(candidateShape,
							consideringBackgroundAgent) ) {
							final CandidateEntry candidateEntry =
								new CandidateEntry(pointToAdd, candidateHeading, dAngle);
							candidateEntries.add(candidateEntry);
						}
					}
				} else {
					final CandidateEntry candidateEntry = new CandidateEntry(pointToAdd, candidateHeading, dAngle);
					candidateEntries.add(candidateEntry);
				}
			}
		}
		/**/

		/* select the nearest */
		if ( candidateEntries.size() > 0 ) {
			CandidateEntry chosenCandidate = null;
			double minDistance = Double.MAX_VALUE;
			final double currentDistance = getCurrentDistance(agent);
			for ( final CandidateEntry currenEntry : candidateEntries ) {
				final GamaPoint currentPoint = currenEntry.candidatePoint;
				final double candidateDistance = topo.distanceBetween(scope, currentPoint.getLocation(), falseTarget);
				if ( candidateDistance < minDistance && candidateDistance != currentDistance ) {
					chosenCandidate = currenEntry;
					minDistance = candidateDistance;
				}
			}

			if ( chosenCandidate == null ) {
				chosenCandidate = candidateEntries.anyValue(scope);
			}
			// System.out.println("distance: "+ minDistance + ", location: " +
			// chosenCandidate.toString());
			setCurrentDistance(agent, minDistance);
			final int newHeading = chosenCandidate.candidateHeading;
			final int newRotateAngle = chosenCandidate.dAngle;
			agent.setGeometry(msi.gaml.operators.Spatial.Transformations.rotated_by(scope, agent, newRotateAngle));
			setLocation(agent, chosenCandidate.candidatePoint);
			setHeading(agent, newHeading);
		} else {
			/* move back */
			candidateHeading = currentHeadingAngle + 180;
			candidateHeading = Maths.checkHeading(candidateHeading);
			final GamaPoint chosenCandidate =
				new GamaPoint(currentLocation.x + maxDist * Maths.cos(candidateHeading), currentLocation.y + maxDist *
					Maths.sin(candidateHeading));
			final GamaShape candidateShape =
				(GamaShape) msi.gaml.operators.Spatial.Transformations.at_location(scope, agentShape, chosenCandidate);
			if ( isNonOverlapping(scope, candidateShape, obstacleAgents) ) {
				setLocation(agent, chosenCandidate);
			}
			/**/
		}
		// scope.setStatus(ExecutionStatus.success);
		if ( isReachedFalseTarget ) { return 1; }
		return 0;
	}

	private boolean isNonOverlapping(final IScope scope, final GamaShape candidateShape,
		final IList<IAgent> obstacleAgents) {
		for ( final IAgent ia : obstacleAgents ) {
			if ( msi.gaml.operators.Spatial.Properties.overlaps(scope, candidateShape, ia.getGeometry()) ) { return false; }
		}
		return true;
	}

	/**
	 * Coded by lvminh, updated 2012 oct 30
	 */
	@action(name = "pedestrian_goto",
		args = {
			@arg(name = "target",
				type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				optional = false,
				doc = @doc("the location or entity towards which to move.")),
			@arg(name = IKeyword.SPEED,
				type = IType.FLOAT,
				optional = true,
				doc = @doc("the speed to use for this move (replaces the current value of speed)")),
			@arg(name = "background",
				type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY },
				optional = false,
				doc = @doc("list, agent, graph, geometry on which the agent moves (the agent moves inside this geometry)")),
			@arg(name = "on",
				type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY },
				optional = true,
				doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")) },
		doc = @doc(value = "moves the agent towards the target passed in the arguments.",
			returns = "the path followed by the agent.",
			examples = { @example("do goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	// @args(names = { "target", IKeyword.SPEED, "on", "target_type" })
	@args(names = { "target_type" })
	public
		Integer primPedestrianGoto(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ILocation source = agent.getLocation().copy(scope);
		double maxDist = computeDistance(scope, agent);
		IShape goal = computeTarget(scope, agent);
		final GamaList<ISpecies> obsSpecies = computeObstacleSpecies(scope, agent);
		// System.out.println("obstacle species: " + obsSpecies.toString());
		final GamaList<ISpecies> backgroundSpecies = computeBackgroundSpecies(scope, agent);
		// System.out.println("background species: " + backgroundSpecies.toString());
		final boolean hasBackground = backgroundSpecies != null && backgroundSpecies.length(scope) > 0;
		final int consideringRange = computeConsideringRange(scope, agent);
		// System.out.println("considering range : " + consideringRange);
		if ( goal == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return -1;
		}

		final ITopology topo = computeTopology(scope, agent);
		if ( topo == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return -1;
		}
		IPath path = (GamaPath) agent.getAttribute("current_path");
		if ( path == null || !path.getTopology(scope).equals(topo) || !path.getEndVertex().equals(goal) ||
			!path.getStartVertex().equals(source) ) {
			path = topo.pathBetween(scope, source, goal);
		}
		if ( path == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			scope.getGui().debug("M: DrivingSKill - can not get the path");
			return -1;
		}

		final IList<IShape> edges = path.getEdgeGeometry();
		final IShape lineEnd = edges.lastValue(scope);
		final GamaPoint falseTarget = (GamaPoint) Punctal._closest_point_to((IShape) path.getEndVertex(), lineEnd);

		final Boolean targetType = (Boolean) scope.getArg("target_type", IType.NONE);
		if ( targetType != null && !targetType ) {
			goal = falseTarget;
		}

		final boolean isReachedTargetCheck = isReachedTarget(scope, agent, goal.getLocation());
		if ( isReachedTargetCheck ) { return 2; }
		final boolean isReachedFalseTarget = isReachedTarget(scope, agent, falseTarget);
		/**
		 * if (isReachedFalseTarget){
		 * System.out.println("M: DrivingSKill - Reached the false target");
		 * }
		 * /
		 **/
		final GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		// System.out.println("Max distance: " + maxDist);
		/* obstacle agents */
		final Collection<IAgent> neighbours =
			agent.getTopology().getNeighboursOf(scope, currentLocation, maxDist + consideringRange, Different.with());
		final IList<IAgent> obstacleAgents = GamaListFactory.create(Types.AGENT);
		for ( IAgent ia : neighbours ) {
			if ( obsSpecies.contains(ia.getSpecies()) ) {
				obstacleAgents.add(ia);
			}
		}

		// System.out.println("obstacle agent: " + obstacleAgents.toString());
		/**/

		/* creating candidate points */
		try {
			final IList<CandidateEntry> candidateEntries = GamaListFactory.create(Types.NO_TYPE);
			GamaPoint pointToAdd = null;
			final int currentHeadingAngle = getHeading(agent);
			int candidateHeading, dAngle;
			// System.out.println("M: maxDist before: " + maxDist);
			final GamaShape agentShape = (GamaShape) agent.getGeometry();
			IShape consideringBackgroundAgentForCurrentPosition = null;
			if ( hasBackground ) {
				for ( final ISpecies currentSpecy : backgroundSpecies ) {
					final IList<IAgent> overlappingBackgroundWithCurrentPosition =
						msi.gaml.operators.Spatial.Queries.overlapping(scope, currentSpecy, agentShape);
					for ( final IAgent ia : overlappingBackgroundWithCurrentPosition.iterable(scope) ) {
						if ( consideringBackgroundAgentForCurrentPosition == null ) {
							consideringBackgroundAgentForCurrentPosition = ia.getGeometry();
						} else {
							consideringBackgroundAgentForCurrentPosition =
								msi.gaml.operators.Spatial.Operators.union(scope,
									consideringBackgroundAgentForCurrentPosition, ia.getGeometry());
						}
					}
				}
				maxDist =
					distanceToNearestInFront(scope, agent, obstacleAgents, 45,
						consideringBackgroundAgentForCurrentPosition, consideringRange, maxDist);
			} else {
				maxDist = distanceToNearestInFront(scope, agent, obstacleAgents, 45, null, consideringRange, maxDist);
			}
			// System.out.println(agent.getName() + " M: maxDist after: " + maxDist);
			for ( int i = 0; i <= 5; i++ ) {
				dAngle = i * 50;
				candidateHeading = currentHeadingAngle + dAngle;
				candidateHeading = Maths.checkHeading(candidateHeading);
				pointToAdd =
					new GamaPoint(currentLocation.x + maxDist * Maths.cos(candidateHeading), currentLocation.y +
						maxDist * Maths.sin(candidateHeading));
				// System.out.println("heading: " + currentHeadingAngle + " | " +
				// " candidate angle: " + candidateAngle);
				// System.out.println("current location: " + currentLocation.toString() + " | " +
				// " candidate location: " + pointToAdd.toString());
				final GamaShape candidateShape =
					(GamaShape) msi.gaml.operators.Spatial.Transformations.rotated_by(scope, agentShape, dAngle);
				candidateShape.setLocation(pointToAdd);
				// check for non-overlapping
				if ( isNonOverlapping(scope, candidateShape, obstacleAgents) ) {
					if ( hasBackground ) {
						// get background agents
						IShape consideringBackgroundAgent = consideringBackgroundAgentForCurrentPosition;
						for ( final ISpecies currentSpecy : backgroundSpecies ) {
							final IList<IAgent> overlappingBackgroundWithCandidatePosition =
								msi.gaml.operators.Spatial.Queries.overlapping(scope, currentSpecy, candidateShape);
							for ( final IAgent ia : overlappingBackgroundWithCandidatePosition.iterable(scope) ) {
								if ( consideringBackgroundAgent == null ) {
									consideringBackgroundAgent = ia.getGeometry();
								} else {
									consideringBackgroundAgent =
										msi.gaml.operators.Spatial.Operators.union(scope, consideringBackgroundAgent,
											ia.getGeometry());
								}
							}
						}
						if ( consideringBackgroundAgent != null ) {
							// System.out.println(((GamaShape)consideringBackgroundAgent).getPoints().toString());
							if ( msi.gaml.operators.DeprecatedOperators.covered_by(candidateShape,
								consideringBackgroundAgent) ) {
								final CandidateEntry candidateEntry =
									new CandidateEntry(pointToAdd, candidateHeading, dAngle);
								candidateEntries.add(candidateEntry);
							}
						}
					} else {
						final CandidateEntry candidateEntry = new CandidateEntry(pointToAdd, candidateHeading, dAngle);
						candidateEntries.add(candidateEntry);
					}
				}
			}
			/**/

			/* select the nearest */
			if ( candidateEntries.size() > 0 ) {
				CandidateEntry chosenCandidate = null;
				double minDistance = Double.MAX_VALUE;
				final double currentDistance = getCurrentDistance(agent);
				for ( final CandidateEntry currenEntry : candidateEntries ) {
					final GamaPoint currentPoint = currenEntry.candidatePoint;
					final double candidateDistance =
						topo.distanceBetween(scope, currentPoint.getLocation(), falseTarget);
					if ( candidateDistance < minDistance && candidateDistance != currentDistance ) {
						chosenCandidate = currenEntry;
						minDistance = candidateDistance;
					}
				}

				if ( chosenCandidate == null ) {
					chosenCandidate = candidateEntries.anyValue(scope);
				}
				// System.out.println("distance: "+ minDistance + ", location: " +
				// chosenCandidate.toString());
				setCurrentDistance(agent, minDistance);
				final int newHeading = chosenCandidate.candidateHeading;
				setLocation(agent, chosenCandidate.candidatePoint);
				setHeading(agent, newHeading);
			}
			// scope.setStatus(ExecutionStatus.success);
			if ( isReachedFalseTarget ) { return 1; }
			return 0;
		} catch (final Exception e) {
			return -1;
		}
	}

	private double getCalculatedPerimeter(final IAgent agent) throws GamaRuntimeException {
		final boolean isCalculatedPerimeter = getIsCalculatedPerimeter(agent);
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
		final double maxDist = computeDistance(scope, agent);
		if ( currentPerimeter < maxDist ) {
			currentPerimeter = maxDist;
		}
		final double distanceToTarget = agent.euclidianDistanceTo(goal);
		return distanceToTarget <= currentPerimeter;
	}

	private double distanceToNearestInFront(final IScope scope, final IAgent agent, final IList<IAgent> obstacleAgents,
		final int dAngle, final IShape consideringBackgroundAgentForCurrentPosition, final int consideringRange,
		double minDistance) {
		final int currentHeading = getHeading(agent);
		final double currentPointX = agent.getLocation().getX();
		final double currentPointY = agent.getLocation().getY();
		final double agentRadius = getCalculatedPerimeter(agent) / 4;
		// System.out.println(agent.getName() + ": agentRadius :" + agentRadius);
		final GamaPoint headPoint =
			new GamaPoint(currentPointX + agentRadius * Maths.cos(currentHeading), currentPointY + agentRadius *
				Maths.sin(currentHeading));
		boolean isFoundObstacleInFront = false;
		try {
			if ( obstacleAgents != null && obstacleAgents.length(scope) > 0 ) {
				for ( final IAgent ia : obstacleAgents ) {
					final double obstaclePointX = ia.getLocation().getX();
					final double obstaclePointY = ia.getLocation().getY();
					final double dy = obstaclePointY - headPoint.getY();
					final double dx = obstaclePointX - headPoint.getX();
					final double obstacleAngle = Math.atan2(dy, dx) * Maths.toDeg;
					final int obstacleHeading = Maths.checkHeading((int) obstacleAngle);
					if ( Maths.abs(obstacleHeading - currentHeading) <= dAngle ) {
						isFoundObstacleInFront = true;
						final double obstacleDistance = Math.sqrt(dx * dx + dy * dy);
						// System.out.println(ia.getName() + ":" + obstacleDistance);
						if ( obstacleDistance > 0 && obstacleDistance < minDistance ) {
							minDistance = obstacleDistance;
						}
					}
				}
			}
		} catch (final Exception e) {
			scope.getGui().debug("obstacle " + e.getStackTrace());
		}

		try {
			if ( !isFoundObstacleInFront ) {
				if ( consideringBackgroundAgentForCurrentPosition != null ) {
					final double xx = consideringRange * Maths.cos(currentHeading);
					final double yy = consideringRange * Maths.sin(currentHeading);
					final GamaPoint aheadPoint = new GamaPoint(headPoint.x + xx, headPoint.y + yy);
					final IShape gl = GamaGeometryType.buildLine(aheadPoint, headPoint);
					if ( gl != null ) {
						final IShape interShape =
							msi.gaml.operators.Spatial.Operators.inter(scope, gl,
								consideringBackgroundAgentForCurrentPosition);
						if ( interShape != null ) {
							final Coordinate coords[] = interShape.getInnerGeometry().getCoordinates();
							// System.out.println(coords.length);
							for ( int i = 0; i < coords.length; i++ ) {
								final double dx = headPoint.getX() - coords[i].x;
								final double dy = headPoint.getY() - coords[i].y;
								final double borderDistance = Math.sqrt(dx * dx + dy * dy);
								if ( borderDistance > 0.1 && borderDistance < minDistance ) {
									minDistance = borderDistance;
								}
							}
						}
					}
				}
			}
		} catch (final Exception e) {
			scope.getGui().debug(agent.getName() + " background ");
		}
		if ( minDistance < 0.5 ) {
			minDistance = 0.5;
		}
		return minDistance;
	}

	@action(name = "read_replay_file", args = { @arg(name = "file_name",
		type = IType.STRING,
		optional = false,
		doc = @doc("File name.")) })
	@args(names = { "file_name" })
	public IList primReadReplayFile(final IScope scope) throws GamaRuntimeException {
		String fileName = (String) scope.getArg("file_name", IType.NONE);

		if ( fileName == null ) { return null; }
		fileName = FileUtils.constructAbsoluteFilePath(scope, Cast.asString(scope, fileName), true);
		scope.getGui().debug("1: " + fileName);
		try {
			final File file = new File(fileName);
			if ( !file.exists() || !file.isFile() ) { return null; }
			final BufferedReader in = new BufferedReader(new FileReader(file));
			final IList<Object> result = GamaListFactory.create();
			IList<GamaPoint> positionList = null;
			String readingLine = in.readLine();
			// System.out.println("2: " + readingLine);
			while (readingLine != null) {
				final String[] tokens = readingLine.split(":");
				if ( tokens.length >= 3 ) {
					positionList = GamaListFactory.create(Types.POINT);
					// System.out.println("3:" + tokens[0] + "|" + tokens[1]);
					final String[] positions = tokens[2].split(";");
					// System.out.println("4:" + tokens[1] + "|" + positions.length);
					for ( final String positionItem : positions ) {
						final String[] coordinates = positionItem.split(",");
						if ( coordinates.length >= 2 ) {
							final double x = Double.parseDouble(coordinates[0]);
							final double y = Double.parseDouble(coordinates[1]);
							final GamaPoint addingPoint = new GamaPoint(x, y);
							positionList.add(addingPoint);
						}
					}
					result.add(positionList);
				}
				readingLine = in.readLine();
			}
			in.close();
			return result;
		} catch (final Exception ex) {
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
		fileName = FileUtils.constructAbsoluteFilePath(scope, Cast.asString(scope, fileName), true);
		scope.getGui().debug("1: " + fileName);
		final IList agent_information = (IList) scope.getArg("agent_information", IType.NONE);
		if ( agent_information == null || agent_information.size() < 3 ) { return null; }
		scope.getGui().debug("2: " + agent_information.size());
		final String currentFileName =
			FileUtils.constructAbsoluteFilePath(scope, Cast.asString(scope, "."), true) + "/log/" + fileName;
		scope.getGui().debug("3: " + currentFileName);
		try {
			// File file = new File(fileName);
			// if (!file.exists() || !file.isFile()) return null;
			final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(currentFileName));
			out.writeObject(agent_information);
			out.close();
			return null;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@action(name = "read_replay", args = { @arg(name = "file_name",
		type = IType.STRING,
		optional = false,
		doc = @doc("File name.")) })
	@args(names = { "file_name" })
	public IList primReadReplay(final IScope scope) throws GamaRuntimeException {
		String fileName = (String) scope.getArg("file_name", IType.NONE);

		if ( fileName == null ) { return null; }
		fileName = FileUtils.constructAbsoluteFilePath(scope, Cast.asString(scope, fileName), true);
		// System.out.println("1: " + fileName);
		try {
			final File file = new File(fileName);
			if ( !file.exists() || !file.isFile() ) { return null; }
			final ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			final GamaList result = (GamaList) in.readObject();
			in.close();
			return result;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}

class CandidateEntry {

	public GamaPoint candidatePoint;
	public int candidateHeading;
	public int dAngle;

	public CandidateEntry(final GamaPoint inputPoint, final int inputHeading, final int inputDAngle) {
		this.candidatePoint = inputPoint;
		this.candidateHeading = inputHeading;
		this.dAngle = inputDAngle;
	}
}