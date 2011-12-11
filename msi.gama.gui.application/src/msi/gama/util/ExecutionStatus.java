/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

/**
 * The Enum PrimCommandStatus.
 */
public enum ExecutionStatus {

	/** The action has stopped, but failed during its execution. */
	/* 0 */failure,

	/** Indicates that it is still running. */
	/* 1 */running,

	/** Action has stopped, but been successfull in its execution. */
	/* 2 */success,

	/**
	 * Action has been executed. However, the execution wil not "count" if the agent has only one
	 * action to do at a time. Declaring "skipped" allows it to let another action execute. To use
	 * with caution since it can lead to endless loops during an agent execution
	 */
	/* 3 */skipped,

	/** Signifies that the action has terminated normally;. */
	/* 4 */end,

	/* 5 */condition_failed,

	/* 6 */interrupt;

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
			case end:
				return 4;
			case condition_failed:
				return 5;
			case interrupt:
				return 6;
			default:
				return 2;
		}
	}
}