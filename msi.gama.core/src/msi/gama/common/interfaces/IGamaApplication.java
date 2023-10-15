/*******************************************************************************************************
 *
 * IGamaApplication.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.util.Map;

import org.eclipse.equinox.app.IApplication;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.server.ISocketCommand;

/**
 * The Interface IGamaApplication.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public interface IGamaApplication extends IApplication {

	/**
	 * Gets the server commands.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the server commands
	 * @date 15 oct. 2023
	 */
	Map<String, ISocketCommand> getServerCommands();

	/**
	 * Register. Forces the appl
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 15 oct. 2023
	 */
	default void register() {
		GAMA.setApplication(this);
		if (isHeadless()) { GAMA.setHeadLessMode(isServer()); }
	}

	/**
	 * Checks if is headless.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is headless
	 * @date 15 oct. 2023
	 */
	boolean isHeadless();

	/**
	 * Checks if is server.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is server
	 * @date 15 oct. 2023
	 */
	boolean isServer();

}
