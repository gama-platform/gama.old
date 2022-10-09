package msi.gama.headless.core;

import org.java_websocket.WebSocket;

import msi.gama.common.interfaces.IConsoleDisplayer;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.runtime.NullGuiHandler;
import msi.gama.runtime.IScope;
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


	private static void sendMessage(IExperimentAgent exp, Object m, GamaServerMessageType type) {
		
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
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			DEBUG.OUT(ex.toString());
		}
	}
	
	@Override
	public void openMessageDialog(final IScope scope, final String message) {
		DEBUG.OUT(message);
		sendMessage(scope.getExperiment(), message, GamaServerMessageType.SimulationDialog);
	}

	@Override
	public void openErrorDialog(final IScope scope, final String error) {
		DEBUG.OUT(error);
		sendMessage(scope.getExperiment(), error, GamaServerMessageType.SimulationError);
	}

	@Override
	public void runtimeError(final IScope scope, final GamaRuntimeException g) {
		sendMessage(scope.getExperiment(), g, GamaServerMessageType.SimulationError);
		DEBUG.OUT(g);
	}
	
	
	@Override
	public IConsoleDisplayer getConsole() {
		if (console == null) {
			
			console = new IConsoleDisplayer() {
				
				@Override
				public void showConsoleView(ITopLevelAgent agent) {
					//nothing to do
				}
				
				@Override
				public void informConsole(String s, ITopLevelAgent root) {
					informConsole(s, root, null);
				}
				
				@Override
				public void informConsole(String s, ITopLevelAgent root, GamaColor color) {
					try {
						sendMessage(root.getExperiment(),
									Jsoner.deserialize(
										"{" 
											+ "\"message\": \"" + s + "\","
											+ "\"color\":" + Jsoner.serialize(color) + ","
											+ "\"exp_id\":" + root.getExperiment().getScope().getData("exp_id")
										+ "}"), 
									GamaServerMessageType.SimulationOutput);
					} catch (DeserializationException e) {
						//If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(root.getExperiment(),
									"{" 
										+ "\"message\": \"" + s + "\","
										+ "\"color\":" + Jsoner.serialize(color) + ","
										+ "\"exp_id\":" + root.getExperiment().getScope().getData("exp_id")
									+ "}", 
									GamaServerMessageType.SimulationOutput);
					}					
				}
				
				@Override
				public void eraseConsole(boolean setToNull) {
					//nothing to do
				}
				
				@Override
				public void debugConsole(int cycle, String s, ITopLevelAgent root) {
					debugConsole(cycle, s, root, null);
				}
				
				@Override
				public void debugConsole(int cycle, String s, ITopLevelAgent root, GamaColor color) {
					try {
						sendMessage(root.getExperiment(), 
									Jsoner.deserialize("{" 
										+ "\"cycle\":" + cycle + ","
										+ "\"message\": \"" + Jsoner.escape(s) + "\","
										+ "\"color\":" + Jsoner.serialize(color) + ","
										+ "\"exp_id\":" + root.getExperiment().getScope().getData("exp_id")
									+ "}"), 
									GamaServerMessageType.SimulationDebug);
					} catch (DeserializationException e) {
						//If for some reason we cannot deserialize, we send it as text
						e.printStackTrace();
						sendMessage(root.getExperiment(), 
								"{" 
									+ "\"cycle\":" + cycle + ","
									+ "\"message\": \"" + Jsoner.escape(s) + "\","
									+ "\"color\":" + Jsoner.serialize(color) + ","
									+ "\"exp_id\":" + root.getExperiment().getScope().getData("exp_id")
								+ "}", 
								GamaServerMessageType.SimulationDebug);
					}
				}
			};
		}
		return console;
	
	}
	
}
