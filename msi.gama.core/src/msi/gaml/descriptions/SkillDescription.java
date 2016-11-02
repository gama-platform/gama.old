/*********************************************************************************************
 *
 * 'SkillDescription.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;
import java.util.Collections;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.skills.Skill;

public class SkillDescription extends TypeDescription {

	Skill instance;
	final boolean isControl;
	final Class<? extends ISkill> javaBase;

	public SkillDescription(final String name, final Class<? extends ISkill> support,
			final Iterable<IDescription> children, final String plugin) {
		super(IKeyword.SKILL, support, null, null, children, null, null, plugin);
		this.name = name;
		this.javaBase = support;
		this.isControl = IArchitecture.class.isAssignableFrom(support);

	}

	@Override
	public Class getJavaBase() {
		return javaBase;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IDescription addChild(final IDescription child) {

		if (child instanceof ActionDescription) {
			addAction((ActionDescription) child);
		} else {
			addOwnAttribute((VariableDescription) child);
		}
		return child;
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		return visitOwnChildren(visitor);
	}

	@Override
	public Collection<String> getActionNames() {
		return actions == null ? Collections.EMPTY_LIST : actions.keySet();
	}

	@Override
	public Collection<String> getAttributeNames() {
		return attributes == null ? Collections.EMPTY_LIST : attributes.keySet();
	}

	public Skill createInstance() {
		Skill instance = null;
		try {
			instance = (Skill) getJavaBase().newInstance();
			instance.setDescription(this);
		} catch (InstantiationException | IllegalAccessException e) {
		}
		return instance;
	}

	public Skill getInstance() {
		if (instance == null)
			instance = createInstance();
		return instance;
	}

	public boolean isControl() {
		return isControl;
	}

	@Override
	public String getDocumentation() {
		final doc d = getDocAnnotation();
		final StringBuilder sb = new StringBuilder(200);
		if (d != null) {
			String s = d.value();
			if (s != null && !s.isEmpty()) {
				sb.append(s);
				sb.append("<br/>");
			}
			s = d.deprecated();
			if (s != null && !s.isEmpty()) {
				sb.append("<b>Deprecated</b>: ");
				sb.append("<i>");
				sb.append(s);
				sb.append("</i><br/>");
			}
		}
		sb.append("<b>Attributes:</b> ").append(getAttributeNames()).append("<br>");
		sb.append("<b>Actions: </b>").append(getActionNames()).append("<br>");
		sb.append("<br/>");
		return sb.toString();

	}

	public doc getDocAnnotation() {
		doc d = null;
		if (javaBase.isAnnotationPresent(doc.class)) {
			d = javaBase.getAnnotation(doc.class);
		}
		return d;
	}

}