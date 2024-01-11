/*******************************************************************************************************
 *
 * GamlActivator.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.expression.GamlExpressionCompiler;
import msi.gama.lang.gaml.resource.GamlResourceInfoProvider;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.lang.gaml.validation.GamlTextValidator;
import msi.gaml.compilation.GAML;
import msi.gaml.expressions.GamlExpressionFactory;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamlActivator.
 */
public class GamlActivator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		// Spawns a new thread in order to escape the "activator/osgi" thread as soon as possible (see #3636) -- seems
		// not necessary anymore
		DEBUG.TIMER_WITH_EXCEPTIONS("GAML", "Initializing parser", "done in", () -> {
			GamlExpressionFactory.registerParserProvider(GamlExpressionCompiler::new);
			GAML.registerInfoProvider(GamlResourceInfoProvider.INSTANCE);
			GAML.registerGamlEcoreUtils(EGaml.getInstance());
			GAML.registerGamlModelBuilder(GamlModelBuilder.getDefaultInstance());
			GAML.registerGamlTextValidator(new GamlTextValidator());
		});

	}

	@Override
	public void stop(final BundleContext context) throws Exception {

	}

}
