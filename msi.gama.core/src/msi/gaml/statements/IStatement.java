/*******************************************************************************************************
 *
 * IStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISymbol;

/**
 * Written by drogoul Feb. 2009
 *
 *
 *
 */
public interface IStatement extends ISymbol, IExecutable {

	/**
	 * The Interface WithArgs.
	 */
	public interface WithArgs extends IStatement {

		/**
		 * Sets the formal args.
		 *
		 * @param args
		 *            the new formal args
		 */
		void setFormalArgs(Arguments args);

		/**
		 * Sets the runtime args.
		 *
		 * @param scope
		 *            the scope
		 * @param args
		 *            the args
		 */
		@Override
		default void setRuntimeArgs(final IScope scope, final Arguments args) {}

	}

	/**
	 * The Interface Breakable.
	 */
	public interface Breakable extends IStatement {
		// Unused tagging interface (for the moment)
	}

}
