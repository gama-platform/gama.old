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

import java.util.*;

import msi.gama.common.interfaces.ISkill;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;

public class Skill extends AbstractProto implements ISkill {

	public static class Factory {

		public static Skill create(final String name, final Class<? extends ISkill> support, final String plugin) {
			Skill skill = null;
			try {
				skill = (Skill) support.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if ( skill != null ) {
				skill.setSupport(support);
				skill.setName(name);
				skill.setDefiningPlugin(plugin);
			}
			return skill;
		}
	}

	/**
	 * @param name
	 * @param support
	 * @param plugin
	 */
	protected Skill() {
		super(null, null, null);
	}

	@Override
	public void setName(final String newName) {
		name = newName;
	}

	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(200);
		sb.append(super.getDocumentation()).append("<br/>");
		sb.append("<b>Attributes:</b> ").append(getVarNames()).append("<br>");
		sb.append("<b>Actions: </b>").append(getActionNames()).append("<br>");
		sb.append("<br/>");
		return sb.toString();

	}

	/**
	 * @return
	 */
	private Collection<String> getActionNames() {
		Collection<IDescription> descs = AbstractGamlAdditions.getActionsForSkill(this.getName());
		List<String> names = new ArrayList();
		for ( IDescription desc : descs ) {
			names.add(desc.getName());
		}
		Collections.sort(names);
		return names;
	}

	/**
	 * TODO SHould be handled more efficiently (ie give skills their own knowledge of var adn actions (though a SkillDescription)
	 * @return
	 */
	private Collection<String> getVarNames() {
		Collection<IDescription> descs = AbstractGamlAdditions.getVariablesForSkill(this.getName());
		List<String> names = new ArrayList();
		for ( IDescription desc : descs ) {
			names.add(desc.getName());
		}
		Collections.sort(names);
		return names;
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
	 * @see msi.gama.common.interfaces.ISkill#duplicate()
	 */
	@Override
	public ISkill duplicate() {
		return this;
	}
	//
	// @Override
	// public void setDuplicator(final ISkillConstructor duplicator) {
	// // Nothing to do here
	// }

	@Override
	public void setDefiningPlugin(final String plugin) {
		this.plugin = plugin;
	}

	/**
	 * Method getTitle()
	 * @see msi.gama.common.interfaces.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Skill " + getName() + " (contributed by " + getDefiningPlugin() + ")";
	}

	/**
	 * Method getKind()
	 * @see msi.gaml.descriptions.AbstractProto#getKind()
	 */
	@Override
	public int getKind() {
		return 7;
	}

}
