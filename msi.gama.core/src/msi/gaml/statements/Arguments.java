/*********************************************************************************************
 *
 * 'Arguments.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.metamodel.agent.IAgent;

/**
 * @author drogoul
 */
public class Arguments extends Facets {

	/*
	 * The caller represents the agent in the context of which the arguments need to be evaluated.
	 */
	IAgent caller;

	public Arguments() {}

	public Arguments(final IAgent caller) {
		this.caller = caller;
	}

	public Arguments(final Arguments args) {
		super(args);
		if (args != null)
			this.caller = args.caller;
	}

	@Override
	public Arguments cleanCopy() {
		final Arguments result = new Arguments(this);
		result.transformValues(cleanCopy);
		result.compact();
		return result;
	}

	public void setCaller(final IAgent caller) {
		this.caller = caller;
	}

	public IAgent getCaller() {
		return caller;
	}

	@Override
	public void dispose() {
		clear();
		caller = null;
	}

}
