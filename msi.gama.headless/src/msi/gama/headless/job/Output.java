/*********************************************************************************************
 * 
 *
 * 'Output.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.job;

import msi.gama.common.interfaces.IKeyword;
import msi.gaml.descriptions.IDescription;

public class Output {
	public static final int DEFAULT_FRAME_RATE = 1;

	private static int OUTPUT_ID = 0;
	public String name;
	public int frameRate;
	public String id;
	public String path;

	public Output(final Output o) {
		this.id = new Integer(Output.generateID()).toString();
		this.frameRate = o.frameRate;
		this.name = o.name;
	}

	public Output clone(final Output o) {
		return new Output(o);
	}

	public static int generateID() {
		return OUTPUT_ID++;
	}

	public static Output loadAndBuildOutput(final IDescription exp) {
		final String name = exp.getLitteral(IKeyword.NAME);
		final Output res = new Output(name, DEFAULT_FRAME_RATE, new Integer(OUTPUT_ID).toString(), null);
		return res;
	}

	public Output(final String name, final int frameRate, final String id, final String path) {
		super();
		this.name = name;
		this.frameRate = frameRate;
		this.id = id;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(final int frameRate) {
		this.frameRate = frameRate;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getOutputPath() {
		return this.path;
	}

}
