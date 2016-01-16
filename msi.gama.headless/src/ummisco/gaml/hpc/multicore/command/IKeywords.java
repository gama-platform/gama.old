/*********************************************************************************************
 * 
 *
 * 'IKeywords.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.hpc.multicore.command;

public interface IKeywords {
	public final static String STARTSIMULATION = "run";
	public final static String WITHPARAMS = "with_param";
	public final static String WITHOUTPUTS = "with_output";
	public final static String WITHSEED = "seed";
	public final static String OUT = "out";
	public final static String CORE = "core";
	public final static String END = "end_cycle";
	public final static String EXPERIMENT = "name";
	public final static String MODEL= "of";
}
