/*******************************************************************************************************
 *
 * ISocketCommand.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import org.java_websocket.WebSocket;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.IMap;

/**
 * The Interface ISocketCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
@FunctionalInterface
public interface ISocketCommand {

	/**
	 * The Class CommandException.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 5 déc. 2023
	 */
	public class CommandException extends Exception {

		/** The response. */
		final CommandResponse response;

		/**
		 * Instantiates a new command exception.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param response
		 *            the response
		 * @date 5 déc. 2023
		 */
		public CommandException(final CommandResponse response) {
			super(response.exp_id);
			this.response = response;
		}

		/**
		 * Gets the response.
		 *
		 * @return the response
		 */
		public CommandResponse getResponse() { return response; }

	}

	/** The socket. */
	String SOCKET = "socket";

	/** The file. */
	String FILE = "file";

	/** The Constant ESCAPED. */
	String ESCAPED = "escaped";

	/** The Constant EXPR. */
	String EXPR = "expr";

	/** The Constant SYNTAX. */
	String SYNTAX = "syntax";

	/** The Constant PARAMETERS. */
	String PARAMETERS = "parameters";

	/** The Constant UNTIL. */
	String UNTIL = IKeyword.UNTIL;

	/** The Constant SERVER. */
	String SERVER = "server";

	/** The Constant SYNC. */
	String SYNC = "sync";

	/** The Constant SOCKET_ID. */
	String SOCKET_ID = "socket_id";

	/** The Constant EXP_ID. */
	String EXP_ID = "exp_id";

	/** The Constant NB_STEP. */
	String NB_STEP = "nb_step";

	/** The play. */
	String PLAY = "play";

	/** The pause. */
	String PAUSE = "pause";

	/** The step. */
	String STEP = IKeyword.STEP;

	/** The back. */
	String BACK = "back"; // synonym to stepBack

	/** The stepback. */
	String STEPBACK = "stepBack";

	/** The load. */
	String LOAD = "load";

	/** The stop. */
	String STOP = "stop";

	/** The reload. */
	String RELOAD = "reload";

	/** The expression. */
	String EXPRESSION = "expression";

	/** The evaluate. */
	String EVALUATE = "evaluate"; // synonym to expression

	/** The exit. */
	String EXIT = "exit";

	/** The download. */
	String DOWNLOAD = "download";

	/** The upload. */
	String UPLOAD = "upload";

	/** The ask. This action allows to ask an agent to execute an action */
	String ASK = IKeyword.ASK;


	/** The args. for the arguments of an action to execute*/
	String ARGS = "args";
	
	/** The validate. This action allows to validate a GAML expression passed as a string */
	String VALIDATE = "validate";

	/**
	 * Execute.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the gama server message
	 * @date 15 oct. 2023
	 */
	GamaServerMessage execute(final GamaWebSocketServer server, final WebSocket socket, final IMap<String, Object> map);

}
