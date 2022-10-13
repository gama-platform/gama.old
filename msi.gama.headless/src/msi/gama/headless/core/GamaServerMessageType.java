package msi.gama.headless.core;

public enum GamaServerMessageType {
	
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
	 * Errors of a simulation that would be found in the console in normal gama mode, either at compilation or during runtime
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
