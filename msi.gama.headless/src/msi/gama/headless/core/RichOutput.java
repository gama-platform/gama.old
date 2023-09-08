/*******************************************************************************************************
 *
 * RichOutput.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.core;

import msi.gama.headless.common.DataType;

/**
 * The Class RichOutput.
 */
public class RichOutput {

	/** The name. */
	private final String name;
	
	/** The value. */
	private final Object value;
	
	/** The step. */
	private final long step;
	
	/** The type. */
	private final DataType type;
	
	/**
	 * Instantiates a new rich output.
	 *
	 * @param n the n
	 * @param sp the sp
	 * @param val the val
	 * @param mtype the mtype
	 */
	RichOutput(final String n, final long sp, final Object val,final DataType mtype )
	{
		this.name=n;
		this.value=val;
		this.step=sp;
		this.type = mtype;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Gets the step.
	 *
	 * @return the step
	 */
	public long getStep() {
		return step;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public DataType getType() {
		return this.type;
	}
}