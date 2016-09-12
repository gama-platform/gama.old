/*********************************************************************************************
 *
 *
 * 'Skill.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.skills;

import msi.gama.common.interfaces.ISkill;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.SkillDescription;

public class Skill implements ISkill {

	protected SkillDescription description;

	protected Skill() {
	}

	@Override
	public void setName(final String newName) {
	}

	public void setDescription(final SkillDescription desc) {
		description = desc;
	}

	@Override
	public String getDocumentation() {
		return description.getDocumentation();
	}

	@Override
	public SkillDescription getDescription() {
		return description;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getName();
	}

	protected IAgent getCurrentAgent(final IScope scope) {
		return scope.getAgent();
	}

	protected ITopology getTopology(final IAgent agent) {
		return agent.getTopology();
	}

	@Override
	public String getTitle() {
		return "Skill " + getName() + " (contributed by " + getDefiningPlugin() + ")";
	}

	@Override
	public String getDefiningPlugin() {
		return description.getDefiningPlugin();
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		description.collectMetaInformation(meta);
	}

	@Override
	public String getName() {
		return description.getName();
	}

}
