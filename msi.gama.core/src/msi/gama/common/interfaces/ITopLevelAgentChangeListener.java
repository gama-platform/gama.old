/*******************************************************************************************************
 *
 * ITopLevelAgentChangeListener.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.ITopLevelAgent;

/**
 * The Interface ITopLevelAgentInterface.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 14 août 2023
 */
public interface ITopLevelAgentChangeListener {

	/**
	 * Sets the listening agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the new listening agent
	 * @date 14 août 2023
	 */
	void topLevelAgentChanged(ITopLevelAgent agent);

}
