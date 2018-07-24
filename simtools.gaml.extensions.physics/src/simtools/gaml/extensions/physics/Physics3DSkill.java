/*********************************************************************************************
 *
 *
 * 'Physics3DSkill.java', in plugin 'simtools.gaml.extensions.physics', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package simtools.gaml.extensions.physics;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@vars({ @variable(name = "space", type = IType.AGENT), @variable(name = "density", type = IType.FLOAT, init = "1.0"),
		@variable(name = "mass", type = IType.FLOAT, init = "1.0"),
		@variable(name = "velocity", type = IType.LIST, init = "[0.0, 0.0, 0.0]"),
		@variable(name = "collisionBound", type = IType.MAP),
		@variable(name = "motor", type = IType.POINT, init = "{0.0, 0.0}") })
@skill(name = "physics", concept = { IConcept.SKILL, IConcept.THREED }, doc = {
		@doc("A skill allowing an agent to act like a physical 3D world") })
public class Physics3DSkill extends Skill {

	@setter("physical_3D_world")
	public void setWorldAgent(final IAgent _agent, final IAgent _world) {
		if (_world instanceof Physical3DWorldAgent)
			((Physical3DWorldAgent) _world).registerAgent(_agent);
	}
}
