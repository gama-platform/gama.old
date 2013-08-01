package gaml.additions;
import msi.gama.outputs.*;
import msi.gama.kernel.batch.*;
import msi.gaml.architecture.weighted_tasks.*;
import msi.gama.outputs.layers.*;
import msi.gaml.architecture.user.*;
import msi.gaml.architecture.reflex.*;
import msi.gaml.architecture.finite_state_machine.*;
import msi.gaml.species.*;
import msi.gama.metamodel.shape.*;
import msi.gaml.expressions.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.population.*;
import msi.gama.kernel.simulation.*;
import java.util.*;
import  msi.gama.metamodel.shape.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.*;
import java.lang.*;
import msi.gama.metamodel.agent.*;
import msi.gaml.types.*;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import msi.gaml.descriptions.*;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.*;
import msi.gama.util.graph.*;
import msi.gama.util.path.*;
import msi.gama.runtime.exceptions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.skills.*;
import msi.gaml.variables.*;
import msi.gama.kernel.experiment.*;
import msi.gaml.operators.*;
import msi.gaml.operators.Random;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Points;
import msi.gaml.operators.Spatial.Properties;
import msi.gaml.operators.System;
import static msi.gaml.operators.Cast.*;
import static msi.gaml.operators.Spatial.*;
import static msi.gama.common.interfaces.IKeyword.*;

public class GamlAdditions extends AbstractGamlAdditions {
		protected static GamlElementDocumentation DOC(int i) { return GamlDocumentation.contents.get(i);}

public void initialize() {
_var(idees.gama.agents.OsmBuildingAgent.class,desc(4,S(TYPE,"4",NAME,"building",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmBuildingAgent.class,desc(4,S(TYPE,"4",NAME,"barrier",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmBuildingAgent.class,desc(4,S(TYPE,"4",NAME,"shop",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmBuildingAgent.class,desc(2,S(TYPE,"2",NAME,"height",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmBuildingAgent.class,desc(1,S(TYPE,"1",NAME,"building:levels",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmBuildingAgent.class,desc(3,S(TYPE,"3",NAME,"wall",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmBuildingAgent.class,desc(3,S(TYPE,"3",NAME,"bridge",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmRoadAgent.class,desc(4,S(TYPE,"4",NAME,"highway",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmRoadAgent.class,desc(1,S(TYPE,"1",NAME,"lanes",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmRoadAgent.class,desc(3,S(TYPE,"3",NAME,"motorroad",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmRoadAgent.class,desc(3,S(TYPE,"3",NAME,"oneway",CONST,FALSE)),null,null,null);
_var(idees.gama.agents.OsmRoadAgent.class,desc(2,S(TYPE,"2",NAME,"maxspeed",CONST,FALSE)),null,null,null);
_action("loadOSMFile",idees.gama.skills.OsmSkills.class,new GamaHelper(T(List.class), idees.gama.skills.OsmSkills.class){ @Override public List run(IScope s, IAgent a,ISkill t, Object... v){ return ((idees.gama.skills.OsmSkills) t).loadOSMFile(s); } },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList(desc(ARG,NAME,"file"),desc(ARG,NAME,"road_species"),desc(ARG,NAME,"building_species"),desc(ARG,NAME,"split_lines"))), NAME, "load_osm",TYPE, T(List.class).toString(), VIRTUAL,FALSE));
_skill("osm",idees.gama.skills.OsmSkills.class, new ISkillConstructor(){ @Override public ISkill newInstance(){return new idees.gama.skills.OsmSkills();}});
_species("osm_building",idees.gama.agents.OsmBuildingAgent.class, new IAgentConstructor(){ @Override public IAgent createOneAgent(IPopulation p) {return new idees.gama.agents.OsmBuildingAgent(p);}});
_species("osm_road",idees.gama.agents.OsmRoadAgent.class, new IAgentConstructor(){ @Override public IAgent createOneAgent(IPopulation p) {return new idees.gama.agents.OsmRoadAgent(p);}});
	}
}