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
_var(ummisco.miro.extension.transportation.skill.TransportationSkill.class,desc(15,S(TYPE,"15",NAME,"individualGraph",CONST,FALSE)),null,null,null);
_var(ummisco.miro.extension.transportation.skill.TransportationSkill.class,desc(4,S(TYPE,"4",NAME,"filePath",CONST,FALSE,"of","0")),new GamaHelper(ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public String run(IScope scope, IAgent a, ISkill t, Object... v) {return t == null? null:((ummisco.miro.extension.transportation.skill.TransportationSkill)t).getSourceFilePath(a);}},null,new GamaHelper(ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public Object run(IScope scope, IAgent a, ISkill t, Object... arg) {if (t != null) ((ummisco.miro.extension.transportation.skill.TransportationSkill) t).setSourceFilePath(a, (String) arg[0]); return null; }});
_var(ummisco.miro.extension.transportation.skill.TransportationSkill.class,desc(4,S(TYPE,"4",NAME,"stationID",CONST,FALSE)),null,null,null);
_var(ummisco.miro.extension.transportation.skill.TransportationSkill.class,desc(3,S(TYPE,"3",NAME,"isTemporalGraph",CONST,FALSE,"init",TRUE)),new GamaHelper(ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public Boolean run(IScope scope, IAgent a, ISkill t, Object... v) {return t == null? false:((ummisco.miro.extension.transportation.skill.TransportationSkill)t).isTemporalGraph(a);}},null,new GamaHelper(ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public Object run(IScope scope, IAgent a, ISkill t, Object... arg) {if (t != null) ((ummisco.miro.extension.transportation.skill.TransportationSkill) t).setTemporalGraph(a, (Boolean) arg[0]); return null; }});
_var(ummisco.miro.extension.transportation.skill.TransportationSkill.class,desc(3,S(TYPE,"3",NAME,"isTemporalGraph",CONST,FALSE,"of","0","init",TRUE)),new GamaHelper(ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public Boolean run(IScope scope, IAgent a, ISkill t, Object... v) {return t == null? false:((ummisco.miro.extension.transportation.skill.TransportationSkill)t).isTemporalGraph(a);}},null,new GamaHelper(ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public Object run(IScope scope, IAgent a, ISkill t, Object... arg) {if (t != null) ((ummisco.miro.extension.transportation.skill.TransportationSkill) t).setTemporalGraph(a, (Boolean) arg[0]); return null; }});
_var(ummisco.miro.extension.transportation.skill.TransportationSkill.class,desc(4,S(TYPE,"4",NAME,"filePath",CONST,FALSE)),new GamaHelper(ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public String run(IScope scope, IAgent a, ISkill t, Object... v) {return t == null? null:((ummisco.miro.extension.transportation.skill.TransportationSkill)t).getSourceFilePath(a);}},null,new GamaHelper(ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public Object run(IScope scope, IAgent a, ISkill t, Object... arg) {if (t != null) ((ummisco.miro.extension.transportation.skill.TransportationSkill) t).setSourceFilePath(a, (String) arg[0]); return null; }});
_var(ummisco.miro.extension.transportation.skill.TransportationSkill.class,desc(4,S(TYPE,"4",NAME,"stationID",CONST,FALSE,"of","0")),null,null,null);
_var(ummisco.miro.extension.transportation.skill.TransportationSkill.class,desc(15,S(TYPE,"15",NAME,"individualGraph",CONST,FALSE,"of","0")),null,null,null);
_action("loadFile",ummisco.miro.extension.transportation.skill.TransportationSkill.class,new GamaHelper(T(void.class), ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public Object run(IScope s, IAgent a,ISkill t, Object... v){  ((ummisco.miro.extension.transportation.skill.TransportationSkill) t).loadFile(s); return null;} },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList(desc(ARG,NAME,"source"),desc(ARG,NAME,"datatype"))), NAME, "loadFile",TYPE, T(void.class).toString(), VIRTUAL,FALSE));
_action("computTravel",ummisco.miro.extension.transportation.skill.TransportationSkill.class,new GamaHelper(T(GM), ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public GamaMap run(IScope s, IAgent a,ISkill t, Object... v){ return ((ummisco.miro.extension.transportation.skill.TransportationSkill) t).computTravel(s); } },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList(desc(ARG,NAME,"from"),desc(ARG,NAME,"to"),desc(ARG,NAME,"on"),desc(ARG,NAME,"departureDate"))), NAME, "travel_arrival",TYPE, T(GM).toString(), VIRTUAL,FALSE));
_action("loadVehicleGraph",ummisco.miro.extension.transportation.skill.TransportationSkill.class,new GamaHelper(T(void.class), ummisco.miro.extension.transportation.skill.TransportationSkill.class){ @Override public Object run(IScope s, IAgent a,ISkill t, Object... v){  ((ummisco.miro.extension.transportation.skill.TransportationSkill) t).loadVehicleGraph(s); return null;} },desc(PRIMITIVE, null, new ChildrenProvider(Arrays.asList(desc(ARG,NAME,"source"))), NAME, "loadVehicleGraph",TYPE, T(void.class).toString(), VIRTUAL,FALSE));
_skill("busTransportation",ummisco.miro.extension.transportation.skill.TransportationSkill.class, new ISkillConstructor(){ @Override public ISkill newInstance(){return new ummisco.miro.extension.transportation.skill.TransportationSkill();}});
	}
}