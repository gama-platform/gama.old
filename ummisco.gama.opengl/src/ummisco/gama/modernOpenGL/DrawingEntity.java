package ummisco.gama.modernOpenGL;

import com.jogamp.opengl.util.texture.Texture;

import ummisco.gama.modernOpenGL.shader.ShaderProgram;

public class DrawingEntity {
	
	static public enum Type { TEXTURED, FACE, LINE, POINT, STRING }; // border == triangulate
	
	public Type type;
	
	private float[] pickingIds;
	private float[] vertices;
	private float[] colors;
	private float[] indices;
	private float[] normals;
	private float[] uvMapping;
	private Texture texture;
	private Material material;
	
	private ShaderProgram shader;
	
	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public float[] getUvMapping() {
		return uvMapping;
	}

	public void setUvMapping(float[] uvMapping) {
		this.uvMapping = uvMapping;
	}

	public DrawingEntity() {
		
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
	
	public float[] getPickingIds() {
		return pickingIds;
	}

	public void setPickingIds(float[] pickingIds) {
		this.pickingIds = pickingIds;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public int getTextureID() {
		return texture.getTextureObject();
	}
	
	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}
	
	public ShaderProgram getShader() {
		return shader;
	}
	
}
