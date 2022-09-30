package msi.gama.headless.listener;

public enum GamaServerMessageType {
	
	/**
	 * Used after the websocket handshake if everything went well
	 */
	ConnectionSuccessful,
	
	/**
	 * Used to describe the content printed using the write statement in a running simulation
	 */
	SimulationOutput, 
	
	
	/**
	 * Warnings of a simulation that would be found in the console in normal gama mode
	 */
	SimulationWarning,

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
