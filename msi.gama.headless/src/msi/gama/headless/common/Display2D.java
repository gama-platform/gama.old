/*********************************************************************************************
 * 
 *
 * 'Display2D.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.common;

import java.io.Serializable;

public class Display2D implements Serializable {

	private String path;

	// private String key;

	public static Display2D valueOf(final String path) {
		return new Display2D(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return path;
	}

	public Display2D(final String path) {
		this.path = path;
	}

}
