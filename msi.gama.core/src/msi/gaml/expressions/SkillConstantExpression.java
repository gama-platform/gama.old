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

import java.util.Set;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.skills.ISkill;
import msi.gaml.types.IType;

public class SkillConstantExpression extends ConstantExpression {

	public SkillConstantExpression(final String val, final IType<ISkill> t) {
		super(AbstractGamlAdditions.getSkillInstanceFor(val), t);
	}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final Set<String> plugins) {
		plugins.add(((ISkill) value).getDefiningPlugin());
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

}
