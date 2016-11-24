/*********************************************************************************************
 *
 * 'IEnvelopeComputer.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.runtime.IScope;

public interface IEnvelopeComputer {

	Envelope computeEnvelopeFrom(final IScope scope, final Object obj);

}
