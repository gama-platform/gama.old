package msi.gama.jogl.utils.collada;

public class ColladaGeometry {

	public String name;// The ID attribute of the element
	public float[] map; // Contains data in the elements
	public String primitiveTYPE; // Identifies the primitive type, such as lines or triangles
	public int index_count; // The number of indices used to draw elements
	public int indices; // The index data from the element

}
