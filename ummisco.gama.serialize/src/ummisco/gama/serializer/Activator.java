/*******************************************************************************************************
 *
 * Activator.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import msi.gama.kernel.experiment.SimulationRecorderFactory;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.ByteArrayZipper;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.file.json.Jsoner;
import ummisco.gama.serializer.implementations.FSTBinaryProcessor;
import ummisco.gama.serializer.implementations.FSTJsonProcessor;
import ummisco.gama.serializer.implementations.SerialisationProcessorFactory;
import ummisco.gama.serializer.implementations.SerialisedSimulationRecorder;
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
		// SerialisationProcessorFactory.register(new XStreamImplementation());
		SerialisationProcessorFactory.register(new FSTJsonProcessor());
		SerialisationProcessorFactory.register(new FSTBinaryProcessor());
		Jsoner.streamConverter = new ConverterJSON();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {

	}

	/**
	 * Main.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param args
	 *            the args
	 * @return the int
	 * @date 28 oct. 2023
	 */
	public static void main(final String[] args) {
		Jsoner.streamConverter = new ConverterJSON();
		SerialisationProcessorFactory.register(new FSTJsonProcessor());
		System.out.println(Jsoner.serialize(GamaColor.get(Color.black)));
		System.out.println(Jsoner.serialize(new GamaPoint()));
		System.out.println(Jsoner.serialize(Color.red));
		System.out.println(Jsoner.serialize(new GamaFont("Helvetica", 0, 12)));
		System.out.println(Jsoner.serialize(new Date(2023, 10, 27)));
		System.out.println(Jsoner.serialize(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)));
		System.out
				.println(
						new String(
								ByteArrayZipper.unzip(new String(
										ByteArrayZipper
												.zip("Hackney Diamonds is a record by the Rolling Stones".getBytes()),
										StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1)),
								StandardCharsets.ISO_8859_1));
	}
}
