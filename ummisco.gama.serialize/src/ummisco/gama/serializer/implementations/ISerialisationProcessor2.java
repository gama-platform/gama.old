/*******************************************************************************************************
 *
 * ISerialisationProcessor.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

/**
 * The Interface ISerialisationProcessor.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @param <SerialisedForm>
 *            the generic type
 * @date 8 août 2023
 */
public interface ISerialisationProcessor2 {

	/**
	 * Save object to bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            TODO
	 * @param obj
	 *            the obj
	 * @return the byte[]
	 * @date 29 sept. 2023
	 */
	byte[] saveObjectToBytes(IScope scope, final Object obj);

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
	 * Restore object from bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param input
	 *            the input
	 * @return the object
	 * @date 29 sept. 2023
	 */
	Object createObjectFromBytes(final IScope scope, final byte[] input);

}