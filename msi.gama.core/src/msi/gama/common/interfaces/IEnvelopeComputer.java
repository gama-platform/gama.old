package msi.gama.common.interfaces;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.runtime.IScope;

public interface IEnvelopeComputer {

	Envelope computeEnvelopeFrom(final IScope scope, final Object obj);

}
