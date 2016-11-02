/*********************************************************************************************
 *
 * 'Vertex.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.utils;

// A vertex has xyz (location) and uv (for texture)
public class Vertex {

	public double x, y, z; // 3D x,y,z location
	public double u, v; // 2D texture coordinates

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")" + "(" + u + "," + v + ")";
	}
}