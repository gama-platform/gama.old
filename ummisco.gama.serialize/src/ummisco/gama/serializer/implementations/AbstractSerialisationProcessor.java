/*******************************************************************************************************
 *
 * AbstractSerialisationProcessor.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
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
 * The Class AbstractSerialisationImplementation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 7 août 2023
 */
public abstract class AbstractSerialisationProcessor<SerialisedForm extends ISerialisedAgent>
		implements ISerialisationProcessor<SerialisedForm>, ISerialisationConstants {

	/**
	 * Restore simulation from.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @param some
	 *            the some
	 * @date 8 août 2023
	 */
	@Override
	public void restoreAgentFromBytes(final IAgent sim, final byte[] input) {
		restoreFromSerialisedForm(sim, read(sim.getScope(), input));
	}

	/**
	 * Save simulation to bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @return the byte[]
	 * @date 8 août 2023
	 */
	@Override
	public byte[] saveAgentToBytes(final IScope scope, final IAgent sim) {
		return write(scope, encodeToSerialisedForm(sim));
	}

	@Override
	public byte[] saveObjectToBytes(final IScope scope, final Object obj) {
		return write(scope, obj);
	}

	/**
	 * Convert to proxy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @return the proxy
	 * @date 7 août 2023
	 */
	protected abstract SerialisedForm encodeToSerialisedForm(IAgent object);

	/**
	 * Convert from proxy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @param proxy
	 *            the proxy
	 * @date 7 août 2023
	 */
	protected abstract void restoreFromSerialisedForm(IAgent object, SerialisedForm proxy);

	/**
	 * Write.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            TODO
	 * @param obj
	 *            the obj
	 * @return the byte[]
	 * @date 29 sept. 2023
	 */
	public abstract byte[] write(IScope scope, Object obj);

	/**
	 * Read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param input
	 *            the input
	 * @return the proxy
	 * @date 7 août 2023
	 */
	@Override
	public abstract SerialisedForm read(IScope scope, byte[] input);

}