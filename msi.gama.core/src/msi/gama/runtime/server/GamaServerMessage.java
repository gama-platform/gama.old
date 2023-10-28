/*******************************************************************************************************
 *
 * GamaServerMessage.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import msi.gama.util.file.json.Jsoner;
import msi.gaml.interfaces.IJsonable;

/**
 * The Class GamaServerMessage.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class GamaServerMessage implements IJsonable {

	/**
	 * The Enum Type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 15 oct. 2023
	 */
	public enum Type {

		/**
		 * Used after the websocket handshake if everything went well
		 */
		ConnectionSuccessful,

		/**
		 * Represents a message in the status bar during an experiment
		 */
		SimulationStatus,

		/**
		 * Represents an informStatus message during an experiment
		 */
		SimulationStatusInform,
		/**
		 * Represents an error message in the status bar during an experiment
		 */
		SimulationStatusError,

		/**
		 * Represents a neutral message in the status bar during an experiment
		 */
		SimulationStatusNeutral,

		/**
		 * Used to describe the content printed using the write statement in a running simulation
		 */
		SimulationOutput,

		/**
		 * Used to describe the content printed using the debug statement in a running simulation
		 */
		SimulationDebug,

		/**
		 * Used to describe the content printed in dialogs in a running simulation
		 */
		SimulationDialog,

		/**
		 * Used to describe the content printed in error dialogs in a running simulation
		 */
		SimulationErrorDialog,

		/**
		 * Errors of a simulation that would be found in the console in normal gama mode, either at compilation or
		 * during runtime
		 */
		SimulationError,

		/**
		 * Used when running a Gama-server command throws an error
		 */
		RuntimeError,

		/**
		 * Used when an unexpected error happen in Gama-server
		 */
		GamaServerError,

		/**
		 * Used when a request is missing a parameter or has inconsistent values
		 */
		MalformedRequest,

		/**
		 * Returned once a Gama-server command has been executed without encountering any problem
		 */
		CommandExecutedSuccessfully,

		/**
		 * When a simulation reached the endCond condition
		 */
		SimulationEnded,

		/**
		 * Used when a command is syntactically and semantically correct, but cannot be run for some reason
		 */
		UnableToExecuteRequest

	}

	/** The type. */
	public final Type type;

	/** The content. */
	public final Object content;

	/**
	 * The exp_id in case the message is linked to a running experiment
	 */
	public final String exp_id;

	/**
	 * Instantiates a new gama server message.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param t
	 *            the t
	 * @param content
	 *            the content
	 * @param exp_id
	 *            the exp_id in case the message is linked to a running experiment
	 * @date 15 oct. 2023
	 */
	public GamaServerMessage(final Type t, final Object content, final String exp_id) {
		this.type = t;
		this.content = content;
		this.exp_id = exp_id;
	}

	/**
	 * Instantiates a new gama server message.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param t
	 *            the t
	 * @param content
	 *            the content
	 * @date 15 oct. 2023
	 */
	public GamaServerMessage(final Type t, final Object content) {
		this(t, content, null);
	}

	/**
	 * Serialize to json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @date 28 oct. 2023
	 */
	@Override
	public String serializeToJson() {
		return "{ " + "\"type\": \"" + type + "\"," + "\"content\": " + Jsoner.serialize(content)
				+ (exp_id != null ? ",\"exp_id\":\"" + exp_id + "\"" : "") + "}";
	}

}
