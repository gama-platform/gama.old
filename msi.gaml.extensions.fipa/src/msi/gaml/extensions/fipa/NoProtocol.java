/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
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
}
