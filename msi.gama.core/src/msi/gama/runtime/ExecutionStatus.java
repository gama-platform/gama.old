/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.runtime;

/**
 * The Enum PrimCommandStatus.
 */
public enum ExecutionStatus {

	/**
	 * The command/action/primitive has stopped, but failed during its execution. The
	 * semantics of "failure" is dependent on the designer.
	 */
	failure,

	/**
	 * Indicates that the command/action/primitive is still running. Default status for most of the
	 * primitives
	 */
	running,

	/**
	 * The command/action/primitive has stopped, but been successfull in its execution. The
	 * semantics of "success" is dependent on the designer.
	 */
	success,

	/**
	 * The command/action/primitive has terminated, but its execution wil not "count" if the agent
	 * has only one action to do at a time. Declaring "skipped" allows it to let another action
	 * execute. To use with caution since it can lead to endless loops during an agent execution
	 */
	skipped,

	/** Signifies that the action has terminated normally, but that its success status is undefined */
	terminated,

	@Deprecated
	condition_failed,

	/**
	 * The command/action/primitive has signified that the outer block of commands (an action or a
	 * behavior) in which it is situated should be discontinued
	 */
	interrupt,

	/**
	 * The last command/action/primitive has signified that the inner loop or switch it is in should
	 * be halted and that the control flow should continue to the next command
	 */
	_break;

	public static ExecutionStatus valueOf(final int i) {
		return values()[i];
	}

	public static int intValueOf(final ExecutionStatus s) {
		switch (s) {
			case failure:
				return 0;
			case running:
				return 1;
			case success:
				return 2;
			case skipped:
				return 3;
			case terminated:
				return 4;
			case condition_failed:
				return 5;
			case interrupt:
				return 6;
			case _break:
				return 7;
			default:
				return 2;
		}
	}
}