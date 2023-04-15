/*******************************************************************************************************
 *
 * IKeywords.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.command;

/**
 * The Interface IKeywords.
 */
public interface IKeywords {
	
	/** The Constant RUNSIMULARTION. */
	public final static String RUNSIMULARTION = "run";
	
	/** The Constant STARTSIMULATION. */
	public final static String STARTSIMULATION = "start_simulation";
	
	/** The Constant LOADSUBMODEL. */
	public final static String LOADSUBMODEL = "load_sub_model";
	
	/** The Constant STEPSUBMODEL. */
	public final static String STEPSUBMODEL = "step_sub_model";
	
	/** The Constant EVALUATESUBMODEL. */
	public final static String EVALUATESUBMODEL = "evaluate_sub_model";
	
	/** The Constant WITHPARAMS. */
	public final static String WITHPARAMS = "with_param";
	
	/** The Constant WITHOUTPUTS. */
	public final static String WITHOUTPUTS = "with_output";
	
	/** The Constant WITHSEED. */
	public final static String WITHSEED = "seed";
	
	/** The Constant OUT. */
	public final static String OUT = "out";
	
	/** The Constant CORE. */
	public final static String CORE = "core";
	
	/** The Constant END. */
	public final static String END = "end_cycle";
	
	/** The Constant EXPERIMENT. */
	public final static String EXPERIMENT = "name";
	
	/** The Constant MODEL. */
	public final static String MODEL= "of";
}
