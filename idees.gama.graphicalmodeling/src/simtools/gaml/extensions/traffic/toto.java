package simtools.gaml.extensions.traffic;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.skills.MovingSkill;
import msi.gaml.types.IType;

@vars({
	@var(name = "toot_var", type = IType.FLOAT_STR, init = "1.0", doc = @doc("the min distance between the agent and an obstacle (in meter)"))})
@skill(name = "toto")
public class toto extends MovingSkill {

	
}
