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

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISkillConstructor;

public abstract class Skill implements ISkill {

	String name;
	String plugin;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String newName) {
		name = newName;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getName();
	}

	protected IAgent getCurrentAgent(final IScope scope) {
		return scope.getAgentScope();
	}

	protected ITopology getTopology(final IAgent agent) {
		return agent.getTopology();
	}

	/**
	 * By default, skills are singletons. This behavior is redefined in AbstractArchitecture
	 * @see msi.gaml.skills.ISkill#duplicate()
	 */
	@Override
	public ISkill duplicate() {
		return this;
	}

	@Override
	public void setDuplicator(final ISkillConstructor duplicator) {
		// Nothing to do here
	}

	public void setDefiningPlugin(final String plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

}
