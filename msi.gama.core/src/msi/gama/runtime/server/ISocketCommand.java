/*******************************************************************************************************
 *
 * ISocketCommand.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
public interface ISocketCommand {

	/** The socket. */
	String SOCKET = "socket";

	/** The file. */
	String FILE = "file";

	/** The Constant ESCAPED. */
	String ESCAPED = "escaped";

	/** The Constant EXPR. */
	String EXPR = "expr";

	/** The Constant PARAMETERS. */
	String PARAMETERS = "parameters";

	/** The Constant UNTIL. */
	String UNTIL = "until";

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
	GamaServerMessage execute(final WebSocket socket, final IMap<String, Object> map);

}
