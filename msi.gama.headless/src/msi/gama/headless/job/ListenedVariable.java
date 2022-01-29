/*******************************************************************************************************
 *
 * ListenedVariable.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.job;

import msi.gama.headless.common.DataType;
import msi.gama.headless.job.ExperimentJob.OutputType;

/**
 * The Class ListenedVariable.
 */
public class ListenedVariable {

	/**
	 * The Class NA.
	 */
	public class NA {
		
		/**
		 * Instantiates a new na.
		 */
		NA() {}

		@Override
		public String toString() {
			return "NA";
		}
	}

	/** The name. */
	String name;
	
	/** The width. */
	public int width;
	
	/** The height. */
	public int height;
	
	/** The frame rate. */
	int frameRate;
	
	/** The type. */
	OutputType type;
	
	/** The data type. */
	DataType dataType;
	
	/** The value. */
	Object value;
	
	/** The step. */
	long step;
	
	/** The path. */
	String path;
	// private boolean isNa;

	/**
	 * Sets the na value.
	 *
	 * @return the object
	 */
	private Object setNaValue() {
		this.value = new NA();
		// this.isNa = true;
		return this.value;
	}

	/**
	 * Instantiates a new listened variable.
	 *
	 * @param name the name
	 * @param width the width
	 * @param height the height
	 * @param frameRate the frame rate
	 * @param type the type
	 * @param outputPath the output path
	 */
	public ListenedVariable(final String name, final int width, final int height, final int frameRate,
			final OutputType type, final String outputPath) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.frameRate = frameRate;
		this.type = type;
		this.path = outputPath;
		this.setNaValue();
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
	 * Sets the value.
	 *
	 * @param obj the obj
	 * @param st the st
	 * @param typ the typ
	 */
	public void setValue(final Object obj, final long st, final DataType typ) {
		// this.isNa = false;
		value = obj == null ? setNaValue() : obj;
		this.step = st;
		this.dataType = typ;
	}

	/**
	 * Sets the value.
	 *
	 * @param obj the obj
	 * @param st the st
	 */
	public void setValue(final Object obj, final long st) {
		setValue(obj, st, this.dataType);
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
	 * Gets the type.
	 *
	 * @return the type
	 */
	public OutputType getType() {
		return type;
	}

	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
}