/*******************************************************************************************************
 *
 * Activator.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import msi.gama.kernel.experiment.SimulationRecorderFactory;
import msi.gama.util.file.json.Jsoner;
import ummisco.gama.serializer.implementations.FSTBinaryProcessor;
import ummisco.gama.serializer.implementations.FSTJsonProcessor;
import ummisco.gama.serializer.implementations.KryoBinaryProcessor;
import ummisco.gama.serializer.implementations.SerialisationProcessorFactory;
import ummisco.gama.serializer.implementations.SerialisedSimulationRecorder;
import ummisco.gama.serializer.implementations.XStreamImplementation;
import ummisco.gama.serializer.inject.ConverterJSON;

/**
 * The Class Activator.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class Activator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		SimulationRecorderFactory.setRecorderClass(SerialisedSimulationRecorder.class);
		SerialisationProcessorFactory.register(new XStreamImplementation());
		SerialisationProcessorFactory.register(new FSTJsonProcessor());
		SerialisationProcessorFactory.register(new FSTBinaryProcessor());
		SerialisationProcessorFactory.register(new KryoBinaryProcessor());
		Jsoner.streamConverter = new ConverterJSON();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {

	}
}
