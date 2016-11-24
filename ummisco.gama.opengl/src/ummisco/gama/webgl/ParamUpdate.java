/*********************************************************************************************
 *
 * 'ParamUpdate.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.webgl;

public class ParamUpdate {
	public String id;
	public Object data;
	
	public ParamUpdate(String id, Object data) {
		this.id = id;
		this.data = data;
	}
}
