/*********************************************************************************************
 * 
 * 
 * 'RoadNodeSkill.java', in plugin 'simtools.gaml.extensions.traffic', is part of the source code of the GAMA modeling
 * and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package simtools.gaml.extensions.traffic;

import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@vars({
	@variable(
		name = RoadNodeSkill.ROADS_IN,
		type = IType.LIST,
		of = IType.AGENT,
		doc = @doc("the list of input roads")
	),
	@variable(
		name = RoadNodeSkill.PRIORITY_ROADS,
		type = IType.LIST,
		of = IType.AGENT,
		doc = @doc("the list of priority roads")
	),
	@variable(
		name = RoadNodeSkill.ROADS_OUT,
		type = IType.LIST,
		of = IType.AGENT,
		doc = @doc("the list of output roads")
	),
	@variable(
		name = RoadNodeSkill.STOP,
		type = IType.LIST,
		of = IType.LIST,
		doc = @doc("define for each type of stop, the list of concerned roads")
	),
	@variable(
		name = RoadNodeSkill.BLOCK,
		type = IType.MAP,
		doc = @doc("define the list of agents blocking the node, and for each agent, the list of concerned roads")
	)
})
@skill(
	name = RoadNodeSkill.ROAD_NODE,
	concept = { IConcept.TRANSPORT, IConcept.SKILL },
	doc = @doc ("A skill for agents representing intersections on roads")
)
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class RoadNodeSkill extends Skill {
	public static final String SKILL_ROAD_NODE = "skill_road_node";

	public static final String ROADS_IN = "roads_in";
	public static final String PRIORITY_ROADS = "priority_roads";
	public static final String ROADS_OUT = "roads_out";
	public static final String STOP = "stop";
	public static final String BLOCK = "block";

	@getter(ROADS_IN)
	public static List getRoadsIn(final IAgent agent) {
		return (List) agent.getAttribute(ROADS_IN);
	}

	@getter(ROADS_OUT)
	public static List getRoadsOut(final IAgent agent) {
		return (List) agent.getAttribute(ROADS_OUT);
	}

	@setter(ROADS_IN)
	public static void setRoadsIn(final IAgent agent, final List rds) {
		agent.setAttribute(ROADS_IN, rds);
	}

	@setter(ROADS_OUT)
	public static void setRoadsOut(final IAgent agent, final List rds) {
		agent.setAttribute(ROADS_OUT, rds);
	}

	@getter(STOP)
	public static List<List> getStop(final IAgent agent) {
		return (List<List>) agent.getAttribute(STOP);
	}

	@setter(STOP)
	public static void setStop(final IAgent agent, final List<List> stop) {
		agent.setAttribute(STOP, stop);
	}

	@getter(BLOCK)
	public static Map<IAgent, List> getBlock(final IAgent agent) {
		return (Map<IAgent, List>) agent.getAttribute(BLOCK);
	}

	@setter(BLOCK)
	public static void setBlock(final IAgent agent, final Map<IAgent, List> block) {
		agent.setAttribute(BLOCK, block);
	}

	@getter(PRIORITY_ROADS)
	public static List getPriorityRoads(final IAgent agent) {
		return (List) agent.getAttribute(PRIORITY_ROADS);
	}

	@setter(PRIORITY_ROADS)
	public static void setPriorityRoads(final IAgent agent, final List rds) {
		agent.setAttribute(PRIORITY_ROADS, rds);
	}
}
