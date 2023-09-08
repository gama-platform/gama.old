/*******************************************************************************************************
 *
 * AbstractArchitecture.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.reflex;

import msi.gama.runtime.IScope;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.expressions.IExpression;
import msi.gaml.skills.Skill;
import msi.gaml.species.ISpecies;

/**
 * The Class AbstractArchitecture.
 */
public abstract class AbstractArchitecture extends Skill implements IArchitecture {

	/**
	 * Instantiates a new abstract architecture.
	 */
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