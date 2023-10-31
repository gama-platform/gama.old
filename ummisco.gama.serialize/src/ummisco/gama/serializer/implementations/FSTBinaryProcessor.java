/*******************************************************************************************************
 *
 * FSTBinaryProcessor.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import org.nustaq.serialization.FSTConfiguration;

/**
 * The Class FSTImplementation. Allows to provide common initializations to FST Configurations and do the dirty work.
 * Not thread / simulation safe.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 2 août 2023
 */
public class FSTBinaryProcessor extends FSTAbstractProcessor {

	/**
	 * Instantiates a new gama FST serialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope.
	 * @date 5 août 2023
	 */
	public FSTBinaryProcessor() {
		super(FSTConfiguration.createDefaultConfiguration());
	}

	@Override
	public String getFormat() { return BINARY_FORMAT; }

	@Override
	public byte getFormatIdentifier() { return 0; }

}
