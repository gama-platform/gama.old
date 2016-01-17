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

public class Output {
	public String name;
	public int frameRate;
	public String	id;

	
	public Output(String name, int frameRate, String id) {
		super();
		this.name = name;
		this.frameRate = frameRate;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFrameRate() {
		return frameRate;
	}
	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
