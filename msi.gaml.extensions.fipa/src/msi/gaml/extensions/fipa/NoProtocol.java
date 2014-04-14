/*********************************************************************************************
 * 
 *
 * 'NoProtocol.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import msi.gaml.extensions.fipa.FIPAConstants.Protocols;

/**
 * NoProtocol represents a free-style conversation (following no interaction protocol).
 */
public class NoProtocol extends FIPAProtocol {

	/** The roots. */
	public static Object[] roots = {};

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	@Override
	public int getIndex() {
		return Protocols.NO_PROTOCOL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.FIPAProtocol#hasProtocol()
	 */
	@Override
	public boolean hasProtocol() {
		return false;
	}

	@Override
	public String getName() {
		return FIPAConstants.Protocols.NO_PROTOCOL_STR;
	}
}
