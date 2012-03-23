package msi.gama.jogl.gis_3D;

//A vertex has xyz (location) and uv (for texture)
public class Vertex {
	public float x, y, z; // 3D x,y,z location
	public float u, v; // 2D texture coordinates

	public String toString() {
		return "(" + x + "," + y + "," + z + ")" + "(" + u + "," + v + ")";
	}
}