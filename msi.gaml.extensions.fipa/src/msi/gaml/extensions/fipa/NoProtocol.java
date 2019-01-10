/*******************************************************************************************************
 *
 * msi.gaml.extensions.fipa.NoProtocol.java, in plugin msi.gaml.extensions.fipa,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.fipa;

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
	// @Override
	// public int getIndex() {
	// return Protocols.NO_PROTOCOL;
	// }

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
