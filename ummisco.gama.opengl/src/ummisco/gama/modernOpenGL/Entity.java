package ummisco.gama.modernOpenGL;

public class Entity {
	
	private TexturedModel model;
	
	private float[] vertices;
	private float[] colors;
	private float[] indices;
	private float[] normals;
	
	public Entity(TexturedModel model) {
		this.model = model;
	}
	
	public Entity() {
		
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public float[] getVertices() {
		return vertices;
	}

	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

	public float[] getColors() {
		return colors;
	}

	public void setColors(float[] colors) {
		this.colors = colors;
	}
	
	public float[] getIndices() {
		return indices;
	}

	public void setIndices(float[] indices) {
		this.indices = indices;
	}

	public float[] getNormals() {
		return normals;
	}

	public void setNormals(float[] normals) {
		this.normals = normals;
	}
	
	
	
}
