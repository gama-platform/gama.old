package simtools.gaml.extensions.traffic;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.skills.MovingSkill;
import msi.gaml.types.IType;

@vars({
	@var(name = "living_space", type = IType.FLOAT_STR, init = "1.0", doc = @doc("the min distance between the agent and an obstacle (in meter)")),
	@var(name = "lanes_attribute", type = IType.STRING_STR, doc = @doc("the name of the attribut of the road agent that determine the number of road lanes")),
	@var(name = "tolerance", type = IType.FLOAT_STR, init = "0.1", doc = @doc("the tolerance distance used for the computation (in meter)")),
	@var(name = "obstacle_species", type = IType.LIST_STR, init = "[]", doc = @doc("the list of species that are considered as obstacles")),
	@var(name = IKeyword.SPEED, type = IType.FLOAT_STR, init = "1.0", doc = @doc("the speed of the agent (in meter/second)")) })
@skill(name = "driving")
public class DrivingSkill extends MovingSkill {

	
}
