/*******************************************************************************************************
 *
 * SkillDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;
import java.util.Collections;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.skills.Skill;

/**
 * The Class SkillDescription.
 */
public class SkillDescription extends TypeDescription {

	/** The instance. */
	Skill instance;

	/** The is control. */
	// final boolean isControl;

	/** The java base. */
	final Class<? extends ISkill> javaBase;

	/**
	 * Instantiates a new skill description.
	 *
	 * @param name
	 *            the name
	 * @param support
	 *            the support
	 * @param children
	 *            the children
	 * @param plugin
	 *            the plugin
	 */
	public SkillDescription(final String name, final Class<? extends ISkill> support,
			final Iterable<IDescription> children, final String plugin) {
		super(IKeyword.SKILL, support, null, null, children, null, null, plugin);
		this.name = name;
		this.javaBase = support;
		setIf(Flag.IsControl, IArchitecture.class.isAssignableFrom(support));

	}

	@Override
	public Class getJavaBase() { return javaBase; }

	@Override
	public String getName() { return name; }

	@Override
	public IDescription addChild(final IDescription child) {
		child.setEnclosingDescription(this);
		if (child instanceof ActionDescription) {
			addAction((ActionDescription) child);
		} else {
			addOwnAttribute((VariableDescription) child);
		}
		return child;
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor<IDescription> visitor) {
		return visitOwnChildren(visitor);
	}

	@Override
	public Collection<String> getActionNames() { return actions == null ? Collections.EMPTY_LIST : actions.keySet(); }

	@Override
	public Collection<String> getAttributeNames() {
		return attributes == null ? Collections.EMPTY_LIST : attributes.keySet();
	}

	/**
	 * Creates the instance.
	 *
	 * @return the skill
	 */
	public Skill createInstance() {
		Skill instance = null;
		try {
			instance = (Skill) getJavaBase().newInstance();
			instance.setDescription(this);
		} catch (InstantiationException | IllegalAccessException e) {}
		return instance;
	}

	/**
	 * Gets the single instance of SkillDescription.
	 *
	 * @return single instance of SkillDescription
	 */
	public Skill getInstance() {
		if (instance == null) { instance = createInstance(); }
		return instance;
	}

	/**
	 * Checks if is control.
	 *
	 * @return true, if is control
	 */
	public boolean isControl() { return isSet(Flag.IsControl); }

	@Override
	public String getTitle() { return "skill " + getName(); }

	@Override
	public Doc getDocumentation() {
		final doc d = getDocAnnotation();
		final StringBuilder sb = new StringBuilder(200);
		if (d != null) {
			String s = d.value();
			if (s != null && !s.isEmpty()) {
				sb.append(s);
				sb.append("<br/>");
			}
			String deprecated = d.deprecated();
			if (deprecated != null && !deprecated.isEmpty()) {
				sb.append("<b>Deprecated</b>: ");
				sb.append("<i>");
				sb.append(deprecated);
				sb.append("</i><br/>");
			}
		}
		sb.append(getAttributeDocumentation());
		sb.append("<br/>");
		sb.append(getActionDocumentation());
		sb.append("<br/>");
		return new RegularDoc(sb);

	}

	/**
	 * Gets the doc annotation.
	 *
	 * @return the doc annotation
	 */
	public doc getDocAnnotation() {
		skill s = javaBase.getAnnotation(skill.class);
		doc[] docs = s.doc();
		doc d = null;
		if (docs.length == 0) {
			if (javaBase.isAnnotationPresent(doc.class)) { d = javaBase.getAnnotation(doc.class); }
		} else {
			d = docs[0];
		}
		return d;
	}

	/**
	 * Gets the deprecated.
	 *
	 * @return the deprecated
	 */
	public String getDeprecated() {
		doc d = getDocAnnotation();
		if (d == null) return null;
		String s = d.deprecated();
		if (s == null || s.isEmpty()) return null;
		return s;
	}

}