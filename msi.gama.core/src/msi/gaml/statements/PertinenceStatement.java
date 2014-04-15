/*********************************************************************************************
 * 
 *
 * 'PertinenceStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gaml.descriptions.IDescription;

public class PertinenceStatement extends AbstractStatementSequence {

	// protected List<? extends ISymbol> commands;
	protected double pertinenceValue;

	public PertinenceStatement(final IDescription desc) {
		super(desc);
	}

	// @Override
	// public void setChildren(final List<? extends ISymbol> commands) {
	// this.commands = commands;
	// }

}
