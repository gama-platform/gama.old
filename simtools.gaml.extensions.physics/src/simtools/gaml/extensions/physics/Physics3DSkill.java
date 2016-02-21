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
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@vars({ @var(name = "space", type = IType.AGENT), @var(name = "density", type = IType.FLOAT, init = "1.0"),
	@var(name = "mass", type = IType.FLOAT, init = "1.0"),
	@var(name = "velocity", type = IType.LIST, init = "[0.0, 0.0, 0.0]"),
	@var(name = "collisionBound", type = IType.MAP), @var(name = "motor", type = IType.POINT, init = "{0.0, 0.0}") })
@skill(name = "physics")
public class Physics3DSkill extends Skill {

	@setter("physical_3D_world")
	public void setWorldAgent(final IAgent _agent, final IAgent _world) {
		if ( _world == null ) { return; }
		Physical3DWorldAgent pwa = (Physical3DWorldAgent) _world;
		pwa.registerAgent(_agent);
	}
}
