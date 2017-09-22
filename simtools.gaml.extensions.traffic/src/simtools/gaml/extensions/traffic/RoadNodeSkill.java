/*********************************************************************************************
 * 
 * 
 * 'RoadNodeSkill.java', in plugin 'simtools.gaml.extensions.traffic', is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
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
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@vars ({ @var (
		name = "roads_in",
		type = IType.LIST,
		of = IType.AGENT,
		doc = @doc ("the list of input roads")),
		@var (
				name = "priority_roads",
				type = IType.LIST,
				of = IType.AGENT,
				doc = @doc ("the list of priority roads")),
		@var (
				name = "roads_out",
				type = IType.LIST,
				of = IType.AGENT,
				doc = @doc ("the list of output roads")),
		@var (
				name = "stop",
				type = IType.LIST,
				of = IType.LIST,
				doc = @doc ("define for each type of stop, the list of concerned roads")),
		@var (
				name = "block",
				type = IType.MAP,
				doc = @doc ("define the list of agents blocking the node, and for each agent, the list of concerned roads")) })
@skill (
		name = "skill_road_node",
		concept = { IConcept.TRANSPORT, IConcept.SKILL },
		doc = @doc ("A skill for agents representing intersections on roads"))
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class RoadNodeSkill extends Skill {

	public final static String ROADS_IN = "roads_in";
	public final static String PRIORITY_ROADS = "priority_roads";
	public final static String ROADS_OUT = "roads_out";
	public final static String STOP = "stop";
	public final static String BLOCK = "block";

	@getter (ROADS_IN)
	public List getRoadsIn(final IAgent agent) {
		return (List) agent.getAttribute(ROADS_IN);
	}

	@getter (ROADS_OUT)
	public List getRoadsOut(final IAgent agent) {
		return (List) agent.getAttribute(ROADS_OUT);
	}

	@setter (ROADS_IN)
	public void setSourceNode(final IAgent agent, final List rds) {
		agent.setAttribute(ROADS_IN, rds);
	}

	@setter (ROADS_OUT)
	public void setTargetNode(final IAgent agent, final List rds) {
		agent.setAttribute(ROADS_OUT, rds);
	}

	@getter (STOP)
	public List<List> getStop(final IAgent agent) {
		return (List<List>) agent.getAttribute(STOP);
	}

	@setter (STOP)
	public void setStop(final IAgent agent, final List<List> stop) {
		agent.setAttribute(STOP, stop);
	}

	@getter (BLOCK)
	public Map<IAgent, List> getBlock(final IAgent agent) {
		return (Map<IAgent, List>) agent.getAttribute(BLOCK);
	}

	@setter (BLOCK)
	public void setBlock(final IAgent agent, final Map<IAgent, List> block) {
		agent.setAttribute(BLOCK, block);
	}

	@getter (PRIORITY_ROADS)
	public List getPriorityRoads(final IAgent agent) {
		return (List) agent.getAttribute(PRIORITY_ROADS);
	}

	@setter (PRIORITY_ROADS)
	public void setPriorityRoads(final IAgent agent, final List rds) {
		agent.setAttribute(PRIORITY_ROADS, rds);
	}

}
