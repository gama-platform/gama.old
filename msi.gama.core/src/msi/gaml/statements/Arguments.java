/*******************************************************************************************************
 *
 * msi.gaml.statements.Arguments.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

/**
 * @author drogoul
 */
public class Arguments extends Facets {

	/*
	 * The caller represents the agent in the context of which the arguments need to be evaluated.
	 */
	ThreadLocal<IAgent> caller = new ThreadLocal<>();

	public Arguments() {}

	public Arguments(final IAgent caller) {
		setCaller(caller);
	}

	public Arguments(final Arguments args) {
		super(args);
		if (args != null) {
			setCaller(args.caller.get());
		}
	}

	@Override
	public Arguments cleanCopy() {
		final Arguments result = new Arguments(this);
		result.transformValues(cleanCopy);
		return result;
	}

	public void setCaller(final IAgent caller) {
		this.caller.set(caller);
	}

	public IAgent getCaller() {
		return caller.get();
	}

	@Override
	public void dispose() {
		super.dispose();
		caller.set(null);
	}

	/**
	 * Returns arguments where all the temp variables belonging to the scope passed in parameter are replaced by their
	 * values
	 */
	public Arguments resolveAgainst(final IScope scope) {
		forEachFacet((n, f) -> {
			f.setExpression(f.getExpression().resolveAgainst(scope));
			return true;
		});
		return this;
	}

}
