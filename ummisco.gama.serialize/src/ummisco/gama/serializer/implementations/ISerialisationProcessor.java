/*******************************************************************************************************
 *
 * ISerialisationProcessor.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.ISerialisedAgent;
import msi.gama.runtime.IScope;

/**
 * The Interface ISerialisationProcessor.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @param <SerialisedForm>
 *            the generic type
 * @date 8 août 2023
 */
public interface ISerialisationProcessor<SerialisedForm extends ISerialisedAgent> {

	/**
	 * Save simulation to bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @return the byte[]
	 * @date 8 août 2023
	 */
	byte[] saveAgentToBytes(final IAgent sim);

	/**
	 * Restore simulation from bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @param input
	 *            the input
	 * @date 8 août 2023
	 */
	void restoreAgentFromBytes(final IAgent sim, final byte[] input);

	/**
	 * Gets the format identifier.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the format identifier
	 * @date 8 août 2023
	 */
	byte getFormatIdentifier();

	/**
	 * Gets the format.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the format
	 * @date 8 août 2023
	 */
	String getFormat();

	/**
	 * Write.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param objectToSerialise
	 *            the object to serialise
	 * @return the byte[]
	 * @date 7 août 2023
	 */
	byte[] write(IScope scope, SerialisedForm objectToSerialise);

	/**
	 * Read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param input
	 *            the input
	 * @return the proxy
	 * @date 7 août 2023
	 */
	SerialisedForm read(IScope scope, byte[] input);

	/**
	 * Pretty print.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 8 août 2023
	 */
	default void prettyPrint() {}

}