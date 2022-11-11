/*******************************************************************************************************
 *
 * GamaServerGUIHandler.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.core;

import org.java_websocket.WebSocket;

import msi.gama.common.interfaces.IConsoleDisplayer;
import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.NullGuiHandler;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.json.DeserializationException;
import msi.gama.util.file.json.Jsoner;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Implements the behaviours to trigger when GUI events happen in a simulation run in GamaServer
 *
 */
public class GamaServerGUIHandler extends NullGuiHandler {

	/** The status. */
	IStatusDisplayer status;

	/**
	 * Send message.
	 *
	 * @param exp
	 *            the exp
	 * @param m
	 *            the m
	 * @param type
	 *            the type
	 */
	private static void sendMessage(final IExperimentAgent exp, final Object m, final GamaServerMessageType type) {

		try {

			if (exp == null) {
				DEBUG.OUT("No experiment, unable to send message: " + m);
				return;
			}

			var scope = exp.getScope();
			if (scope == null) {
				DEBUG.OUT("No scope, unable to send message: " + m);
				return;
			}
			var socket = (WebSocket) scope.getData("socket");
			socket.send(Jsoner.serialize(new GamaServerMessage(type, m, (String) scope.getData("exp_id"))));

		} catch (Exception ex) {
			ex.printStackTrace();
			DEBUG.OUT(ex.toString());
		}
	}

	/**
	 * Can send dialog messages.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	private boolean canSendDialogMessages(final IScope scope) {
		return scope != null && scope.getData("dialog") != null ? (boolean) scope.getData("dialog") : true;
	}

	@Override
	public void openMessageDialog(final IScope scope, final String message) {
		DEBUG.OUT(message);
		if (!canSendDialogMessages(scope)) return;
		sendMessage(scope.getExperiment(), message, GamaServerMessageType.SimulationDialog);
	}

	@Override
	public void openErrorDialog(final IScope scope, final String error) {

		DEBUG.OUT(error);
		if (!canSendDialogMessages(scope)) return;
		sendMessage(scope.getExperiment(), error, GamaServerMessageType.SimulationErrorDialog);
	}

	@Override
	public void runtimeError(final IScope scope, final GamaRuntimeException g) {
		sendMessage(scope.getExperiment(), g, GamaServerMessageType.SimulationError);
		DEBUG.OUT(g);
	}

	@Override
	public IStatusDisplayer getStatus() {

		if (status == null) {
			status = new IStatusDisplayer() {

				private boolean canSendMessage(final IExperimentAgent exp) {
					var scope = exp.getScope();
					return scope != null && scope.getData("status") != null ? (boolean) scope.getData("status") : true;
				}

				@Override
				public void informStatus(final IScope scope, final String string) {

					if (!canSendMessage(scope.getExperiment())) return;

					try {
						sendMessage(scope.getExperiment(),
								Jsoner.deserialize("{" + "\"message\": \"" + string + "\"" + "}"),
								GamaServerMessageType.SimulationStatusInform);
					} catch (DeserializationException e) {
						// If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(scope.getExperiment(), "{" + "\"message\": \"" + string + "\"" + "}",
								GamaServerMessageType.SimulationStatusInform);
					}

				}

				@Override
				public void errorStatus(final IScope scope, final String message) {

					if (!canSendMessage(scope.getExperiment())) return;

					try {
						sendMessage(scope.getExperiment(),
								Jsoner.deserialize("{" + "\"message\": \"" + message + "\"" + "}"),
								GamaServerMessageType.SimulationStatusError);
					} catch (DeserializationException e) {
						// If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(scope.getExperiment(), "{" + "\"message\": \"" + message + "\"" + "}",
								GamaServerMessageType.SimulationStatusError);
					}

				}

				@Override
				public void setStatus(final IScope scope, final String msg, final GamaColor color) {

					if (!canSendMessage(scope.getExperiment())) return;

					try {
						sendMessage(
								scope.getExperiment(), Jsoner.deserialize("{" + "\"message\": \"" + msg + "\","
										+ "\"color\": " + Jsoner.serialize(color) + "" + "}"),
								GamaServerMessageType.SimulationStatus);
					} catch (DeserializationException e) {
						// If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(scope.getExperiment(),
								"{" + "\"message\": \"" + msg + "\"," + "\"color\": " + Jsoner.serialize(color) + "}",
								GamaServerMessageType.SimulationStatus);
					}
				}

				@Override
				public void informStatus(final IScope scope, final String message, final String icon) {

					if (!canSendMessage(scope.getExperiment())) return;

					try {
						sendMessage(scope.getExperiment(),
								Jsoner.deserialize(
										"{" + "\"message\": \"" + message + "\"," + "\"icon\": \"" + icon + "\"" + "}"),
								GamaServerMessageType.SimulationStatusInform);
					} catch (DeserializationException e) {
						// If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(scope.getExperiment(),
								"{" + "\"message\": \"" + message + "\"," + "\"icon\": \"" + icon + "\"" + "}",
								GamaServerMessageType.SimulationStatusInform);
					}

				}

				@Override
				public void setStatus(final IScope scope, final String msg, final String icon) {

					if (!canSendMessage(scope.getExperiment())) return;

					try {
						sendMessage(scope.getExperiment(),
								Jsoner.deserialize(
										"{" + "\"message\": \"" + msg + "\"," + "\"icon\":\"" + icon + "\"" + "}"),
								GamaServerMessageType.SimulationStatus);
					} catch (DeserializationException e) {
						// If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(scope.getExperiment(),
								"{" + "\"message\": \"" + msg + "\"," + "\"icon\": \"" + icon + "\"" + "}",
								GamaServerMessageType.SimulationStatus);
					}
				}

				@Override
				public void neutralStatus(final IScope scope, final String string) {

					if (!canSendMessage(scope.getExperiment())) return;

					try {
						sendMessage(scope.getExperiment(),
								Jsoner.deserialize("{" + "\"message\": \"" + string + "\"" + "}"),
								GamaServerMessageType.SimulationStatusNeutral);
					} catch (DeserializationException e) {
						// If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(scope.getExperiment(), "{" + "\"message\": \"" + string + "\"" + "}",
								GamaServerMessageType.SimulationStatusNeutral);
					}

				}

			};
		}
		return status;
	}

	@Override
	public IConsoleDisplayer getConsole() {
		if (console == null) {

			console = new IConsoleDisplayer() {

				private boolean canSendMessage(final IExperimentAgent exp) {
					var scope = exp.getScope();
					return scope != null && scope.getData("console") != null ? (boolean) scope.getData("console")
							: true;
				}

				@Override
				public void informConsole(final String s, final ITopLevelAgent root, final GamaColor color) {

					if (!canSendMessage(root.getExperiment())) return;

					try {
						sendMessage(
								root.getExperiment(), Jsoner.deserialize("{" + "\"message\": \"" + s + "\","
										+ "\"color\":" + Jsoner.serialize(color) + "}"),
								GamaServerMessageType.SimulationOutput);
					} catch (DeserializationException e) {
						// If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(root.getExperiment(),
								"{" + "\"message\": \"" + s + "\"," + "\"color\":" + Jsoner.serialize(color) + "}",
								GamaServerMessageType.SimulationOutput);
					}
				}

				@Override
				public void debugConsole(final int cycle, final String s, final ITopLevelAgent root,
						final GamaColor color) {
					if (!canSendMessage(root.getExperiment())) return;
					try {
						sendMessage(root.getExperiment(),
								Jsoner.deserialize("{" + "\"cycle\":" + cycle + "," + "\"message\": \""
										+ Jsoner.escape(s) + "\"," + "\"color\":" + Jsoner.serialize(color) + "}"),
								GamaServerMessageType.SimulationDebug);
					} catch (DeserializationException e) {
						// If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(root.getExperiment(),
								"{" + "\"cycle\":" + cycle + "," + "\"message\": \"" + Jsoner.escape(s) + "\","
										+ "\"color\":" + Jsoner.serialize(color) + "}",
								GamaServerMessageType.SimulationDebug);
					}
				}
			};
		}
		return console;

	}

}
