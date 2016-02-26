package ummisco.miro.extension.moving;

import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;


@vars({ @var(name = "speed", type = IType.FLOAT, init = "1.0"),
	@var(name = "nbVehicle", type = IType.INT, init = "0")
})
@skill(name = "roadTrafficManagement", concept = { IConcept.TRANSPORT, IConcept.SKILL })
public class RoadTrafficManagement extends Skill {	

}
