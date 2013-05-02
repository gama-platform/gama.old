package msi.gama.jogl.utils;

// A vertex has xyz (location) and uv (for texture)
public class Vertex {

	public double x, y, z; // 3D x,y,z location
	public double u, v; // 2D texture coordinates

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")" + "(" + u + "," + v + ")";
	}
}