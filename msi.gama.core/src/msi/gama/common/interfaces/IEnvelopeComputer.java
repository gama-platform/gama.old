/*********************************************************************************************
 *
 * 'IEnvelopeComputer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;

public interface IEnvelopeComputer {

	Envelope3D computeEnvelopeFrom(final IScope scope, final Object obj);

}
