/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.skills;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;

@vars({ @var(name = "points", type = IType.LIST_STR, of = IType.POINT_STR),
	@var(name = "distances", type = IType.LIST_STR, of = IType.FLOAT_STR),
	@var(name = "exploring", type = IType.BOOL_STR, init = "false"),
	@var(name = "patrolling", type = IType.BOOL_STR, init = "false"),
	@var(name = "currentPointIndex", type = IType.INT_STR, init = "-1"),
	@var(name = "beginIndex", type = IType.INT_STR, init = "0") })
@skill("exploring")
public class ExploringSkill extends MovingSkill {

	@action("explore")
	@args({ "speed" })
	public Object primExplore(final IScope scope) {
		return null;
	}

	@action("getPoint")
	@args({ "index" })
	public GamaPoint primGetPoint(final IScope scope) throws GamaRuntimeException {
		final int i = Cast.asInt(scope.getArg("index"));
		final GamaPoint target =
			(GamaPoint) ((GamaList) scope.getAgentVarValue(getCurrentAgent(scope), "points"))
				.get(i);
		return target;

	}

	@action("sort_point")
	@args({})
	public Object primSort(final IScope scope) throws GamaRuntimeException {
		List<GamaPoint> points = (List) scope.getAgentVarValue(getCurrentAgent(scope), "points");
		List<Double> distances = (List) scope.getAgentVarValue(getCurrentAgent(scope), "distances");
		for ( int i = 0; i < points.size() - 1; i++ ) {
			for ( int j = i + 1; j < points.size(); j++ ) {
				if ( distances.get(i) > distances.get(j) ) {
					final GamaPoint interPoint = points.get(i);
					points.set(i, points.get(j));
					points.set(j, interPoint);

					final Double interDistance = distances.get(i);
					distances.set(i, distances.get(j));
					distances.set(j, interDistance);
				}
			}
		}

		return points;
	}

	@action("patrol")
	@args({ "speed" })
	public GamaPoint primPatrol(final IScope scope) throws GamaRuntimeException {
		IAgent agent = getCurrentAgent(scope);
		List<GamaPoint> points = (List) scope.getAgentVarValue(getCurrentAgent(scope), "points");
		int currentPointIndex =
			(Integer) scope.getAgentVarValue(getCurrentAgent(scope), "currentPointIndex");
		int beginIndex = (Integer) scope.getAgentVarValue(getCurrentAgent(scope), "beginIndex");

		if ( points == null || points.isEmpty() ) {
			agent.setAttribute("patrolling", false);
			scope.setStatus(ExecutionStatus.failure);
			return agent.getLocation();

		}

		if ( currentPointIndex == -1 ) {
			agent.setAttribute("patrolling", false);
			scope.setStatus(ExecutionStatus.failure);
			return agent.getLocation();

		}

		if ( currentPointIndex >= points.size() ) {
			beginIndex++;
			currentPointIndex = beginIndex;
		}

		agent.setAttribute("patrolling", true);

		final Double s = Cast.asFloat(scope.getArg("speed"));
		if ( s > 0 ) {
			agent.setAttribute("speed", s);
		}
		final double maxDist =
			(Double) scope.getAgentVarValue(agent, "speed") *
				scope.getSimulationScope().getScheduler().getStep();

		final GamaPoint target = points.get(currentPointIndex);
		final GamaPoint source = agent.getLocation();
		GamaPoint loc = null;
		if ( target == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return source;
		}
		ExecutionStatus status;
		if ( getTopology(agent).distanceBetween(source, target) <= maxDist ) {
			loc = target;
			status = ExecutionStatus.success;
		} else {
			final int wantedDirection = getTopology(agent).directionInDegreesTo(source, target);
			loc = getTopology(agent).getDestination(source, wantedDirection, maxDist, true);
			status = ExecutionStatus.skipped;
		}

		if ( loc != null ) {
			agent.setLocation(loc);
		}

		if ( status == ExecutionStatus.skipped ) {
			currentPointIndex += 100;

			if ( currentPointIndex < points.size() - 1 ) {
				status = ExecutionStatus.running;
			}
		}
		agent.setAttribute("currentPointIndex", currentPointIndex);
		agent.setAttribute("beginIndex", beginIndex);
		scope.setStatus(status);
		return agent.getLocation();
	}

}
