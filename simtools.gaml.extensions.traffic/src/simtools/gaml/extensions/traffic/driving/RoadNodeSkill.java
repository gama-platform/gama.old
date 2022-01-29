/*******************************************************************************************************
 *
 * RoadNodeSkill.java, in simtools.gaml.extensions.traffic, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.driving;

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

/**
 * The Class RoadNodeSkill.
 */
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
	name = RoadNodeSkill.SKILL_ROAD_NODE,
	concept = { IConcept.TRANSPORT, IConcept.SKILL },
	doc = @doc ("A skill for agents representing intersections on roads")
)
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class RoadNodeSkill extends Skill {
	
	/** The Constant SKILL_ROAD_NODE. */
	public static final String SKILL_ROAD_NODE = "skill_road_node";

	/** The Constant ROADS_IN. */
	public static final String ROADS_IN = "roads_in";
	
	/** The Constant PRIORITY_ROADS. */
	public static final String PRIORITY_ROADS = "priority_roads";
	
	/** The Constant ROADS_OUT. */
	public static final String ROADS_OUT = "roads_out";
	
	/** The Constant STOP. */
	public static final String STOP = "stop";
	
	/** The Constant BLOCK. */
	public static final String BLOCK = "block";

	/**
	 * Gets the roads in.
	 *
	 * @param agent the agent
	 * @return the roads in
	 */
	@getter(ROADS_IN)
	public static List<IAgent> getRoadsIn(final IAgent agent) {
		return (List<IAgent>) agent.getAttribute(ROADS_IN);
	}

	/**
	 * Gets the roads out.
	 *
	 * @param agent the agent
	 * @return the roads out
	 */
	@getter(ROADS_OUT)
	public static List<IAgent> getRoadsOut(final IAgent agent) {
		return (List<IAgent>) agent.getAttribute(ROADS_OUT);
	}

	/**
	 * Sets the roads in.
	 *
	 * @param agent the agent
	 * @param rds the rds
	 */
	@setter(ROADS_IN)
	public static void setRoadsIn(final IAgent agent, final List<IAgent> rds) {
		agent.setAttribute(ROADS_IN, rds);
	}

	/**
	 * Sets the roads out.
	 *
	 * @param agent the agent
	 * @param rds the rds
	 */
	@setter(ROADS_OUT)
	public static void setRoadsOut(final IAgent agent, final List<IAgent> rds) {
		agent.setAttribute(ROADS_OUT, rds);
	}

	/**
	 * Gets the stop.
	 *
	 * @param agent the agent
	 * @return the stop
	 */
	@getter(STOP)
	public static List<List> getStop(final IAgent agent) {
		return (List<List>) agent.getAttribute(STOP);
	}

	/**
	 * Sets the stop.
	 *
	 * @param agent the agent
	 * @param stop the stop
	 */
	@setter(STOP)
	public static void setStop(final IAgent agent, final List<List> stop) {
		agent.setAttribute(STOP, stop);
	}

	/**
	 * Gets the block.
	 *
	 * @param agent the agent
	 * @return the block
	 */
	@getter(BLOCK)
	public static Map<IAgent, List> getBlock(final IAgent agent) {
		return (Map<IAgent, List>) agent.getAttribute(BLOCK);
	}

	/**
	 * Sets the block.
	 *
	 * @param agent the agent
	 * @param block the block
	 */
	@setter(BLOCK)
	public static void setBlock(final IAgent agent, final Map<IAgent, List> block) {
		agent.setAttribute(BLOCK, block);
	}

	/**
	 * Gets the priority roads.
	 *
	 * @param agent the agent
	 * @return the priority roads
	 */
	@getter(PRIORITY_ROADS)
	public static List getPriorityRoads(final IAgent agent) {
		return (List) agent.getAttribute(PRIORITY_ROADS);
	}

	/**
	 * Sets the priority roads.
	 *
	 * @param agent the agent
	 * @param rds the rds
	 */
	@setter(PRIORITY_ROADS)
	public static void setPriorityRoads(final IAgent agent, final List rds) {
		agent.setAttribute(PRIORITY_ROADS, rds);
	}
}
