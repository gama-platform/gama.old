/*********************************************************************************************
 *
 *
 * 'AbstractArchitecture.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.architecture.reflex;

import msi.gama.runtime.IScope;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.expressions.IExpression;
import msi.gaml.skills.Skill;
import msi.gaml.species.ISpecies;

public abstract class AbstractArchitecture extends Skill implements IArchitecture {

	public AbstractArchitecture() {
		super();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getName();
	}

	@Override
	public String getKeyword() {
		return getName();
	}

	@Override
	public String getTrace(final IScope scope) {
		return "";
	}

	@Override
	public IExpression getFacet(final String... key) {
		return null;
	}

	@Override
	public boolean hasFacet(final String key) {
		return false;
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {
	}

	@Override
	public void dispose() {
	}

}