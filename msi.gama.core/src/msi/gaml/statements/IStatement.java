/*********************************************************************************************
 * 
 * 
 * 'IStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gaml.compilation.ISymbol;

/**
 * Written by drogoul Feb. 2009
 * 
 * @todo Description
 * 
 */
public interface IStatement extends ISymbol, IExecutable {

	public interface WithArgs extends IStatement {

		public abstract void setFormalArgs(Arguments args);

		public abstract void setRuntimeArgs(Arguments args);

	}

	public interface Breakable extends IStatement {
		// Unused tagging interface (for the moment)
	}

}
