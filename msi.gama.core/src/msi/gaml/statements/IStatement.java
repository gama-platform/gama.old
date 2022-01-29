/*******************************************************************************************************
 *
 * IStatement.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
 * @todo Description
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
		 * @param args the new formal args
		 */
		public abstract void setFormalArgs(Arguments args);

		public abstract void setRuntimeArgs(IScope scope, Arguments args);

	}

	/**
	 * The Interface Breakable.
	 */
	public interface Breakable extends IStatement {
		// Unused tagging interface (for the moment)
	}

}
