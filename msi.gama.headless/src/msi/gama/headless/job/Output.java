/*******************************************************************************************************
 *
 * Output.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.job;


import msi.gama.common.interfaces.IKeyword;
import msi.gaml.descriptions.IDescription;

/**
 * The Class Output.
 */
public class Output {
	
	/** The Constant DEFAULT_WIDTH. */
	public static final int DEFAULT_WIDTH = 500;
	
	/** The Constant DEFAULT_HEIGHT. */
	public static final int DEFAULT_HEIGHT = 500;
	
	/** The Constant DEFAULT_FRAME_RATE. */
	public static final int DEFAULT_FRAME_RATE = 1;

	/** The output id. */
	private static int OUTPUT_ID = 0;
	
	/** The name. */
	public String name;
	
	/** The width. */
	public int width;
	
	/** The height. */
	public int height;
	
	/** The frame rate. */
	public int frameRate;
	
	/** The id. */
	public String id;
	
	/** The path. */
	public String path;

	/**
	 * Instantiates a new output.
	 *
	 * @param o the o
	 */
	public Output(final Output o) {
		this.id = new Integer(Output.generateID()).toString();
		this.width = o.width;
		this.height = o.height;
		this.frameRate = o.frameRate;
		this.name = o.name;
	}

	/**
	 * Clone.
	 *
	 * @param o the o
	 * @return the output
	 */
	public Output clone(final Output o) {
		return new Output(o);
	}

	/**
	 * Generate ID.
	 *
	 * @return the int
	 */
	public static int generateID() {
		return OUTPUT_ID++;
	}

	/**
	 * Load and build output.
	 *
	 * @param exp the exp
	 * @return the output
	 */
	public static Output loadAndBuildOutput(final IDescription exp) {
		final String name = exp.getLitteral(IKeyword.NAME);
		final Output res = new Output(name, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FRAME_RATE, new Integer(OUTPUT_ID).toString(), null);
		return res;
	}

	/**
	 * Instantiates a new output.
	 *
	 * @param name the name
	 * @param width the width
	 * @param height the height
	 * @param frameRate the frame rate
	 * @param id the id
	 * @param path the path
	 */
	public Output(final String name, final int width, final int height, final int frameRate, final String id, final String path) {
		super();
		this.name = name;
		this.width = width;
		this.height = height;
		this.frameRate = frameRate;
		this.id = id;
		this.path = path;
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
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 *
	 * @param w the new width
	 */
	public void setWidth(final int w) {
		this.width = w;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height.
	 *
	 * @param h the new height
	 */
	public void setHeight(final int h) {
		this.height = h;
	}

	/**
	 * Gets the frame rate.
	 *
	 * @return the frame rate
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * Sets the frame rate.
	 *
	 * @param frameRate the new frame rate
	 */
	public void setFrameRate(final int frameRate) {
		this.frameRate = frameRate;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets the output path.
	 *
	 * @return the output path
	 */
	public String getOutputPath() {
		return this.path;
	}

}
