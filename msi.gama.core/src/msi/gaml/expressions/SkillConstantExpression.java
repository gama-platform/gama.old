/*********************************************************************************************
 *
 *
 * 'SpeciesConstantExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.ISkill;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.GamaSkillRegistry;
import msi.gaml.types.IType;

public class SkillConstantExpression extends ConstantExpression {

	public SkillConstantExpression(final String val, final IType<ISkill> t) {
		super(GamaSkillRegistry.INSTANCE.getSkillInstanceFor(val), t);
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return ((ISkill) value).getDocumentation();
	}

	@Override
	public String getTitle() {
		return ((ISkill) value).getTitle();
	}

	@Override
	public String literalValue() {
		return ((ISkill) value).getName();
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		final ISkill skill = (ISkill) value;
		meta.put(GamlProperties.PLUGINS, skill.getDefiningPlugin());
		meta.put(skill instanceof IArchitecture ? GamlProperties.ARCHITECTURES : GamlProperties.SKILLS,
				skill.getName());
	}

}
