/*******************************************************************************************************
 *
 * CoreConstantsSupplier.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.constants;

import java.util.Map;

import msi.gama.common.interfaces.IEventLayerDelegate;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.layers.EventLayerStatement;
import msi.gama.outputs.layers.properties.ICameraDefinition;
import msi.gama.outputs.layers.properties.ILightDefinition;
import msi.gama.util.GamaColor;

/**
 * The Class CoreConstantsSupplier.
 */
public class CoreConstantsSupplier implements IConstantsSupplier {

	@Override
	public void supplyConstantsTo(final IConstantAcceptor acceptor) {

		browse(ICameraDefinition.class, acceptor);
		browse(ILightDefinition.class, acceptor);
		browse(GamlCoreUnits.class, acceptor);
		browse(GamlCoreConstants.class, acceptor);

		acceptor.accept(IKeyword.DEFAULT, IKeyword.DEFAULT, "Default value for cameras and lights", null, false);

		// We build constants based on the colors declared in GamaColor / ColorCSS
		for (final Map.Entry<String, GamaColor> entry : GamaColor.colors.entrySet()) {
			final GamaColor c = entry.getValue();
			final String doc = "Standard CSS color corresponding to rgb (" + c.red() + ", " + c.green() + ", "
					+ c.blue() + "," + c.getAlpha() + ")";
			acceptor.accept(entry.getKey(), c, doc, null, false);
		}

		// We browse all the constants declared in the event layer delegates and add them (like mouse or keyboard
		// events)
		for (final IEventLayerDelegate delegate : EventLayerStatement.delegates) {
			browse(delegate.getClass(), acceptor);
		}

	}

}
