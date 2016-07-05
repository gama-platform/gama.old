package ummisco.gama.modernOpenGL;

import ummisco.gama.opengl.utils.Utils;

public class DrawingEntity {
	
	static public enum Type { TEXTURED, FACE, LINE, POINT }; // border == triangulate
	
	public Type type;
	
	private float[] vertices;
	private float[] colors;
	private float[] indices;
	private float[] normals;
	private float[] uvMapping;
	private int textID;
	private Material material;
	
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
	
	public int getTextureID() {
		return textID;
	}
	
	public void setTextureID(int id) {
		this.textID = id;
	}
	
	public DrawingEntity concatenateWith(DrawingEntity entity) {
		DrawingEntity result = new DrawingEntity();
		// we store the number of vertices (we will need to add this value to the idx of the second entity)
		int nbVertices = vertices.length/3;
		result.setColors(Utils.concatFloatArrays(colors, entity.getColors()));
		result.setVertices(Utils.concatFloatArrays(vertices, entity.getVertices()));
		result.setNormals(Utils.concatFloatArrays(normals, entity.getNormals()));
		result.setUvMapping(Utils.concatFloatArrays(uvMapping, entity.getUvMapping()));
		result.setMaterial(entity.getMaterial());
		result.type = type;
		result.setTextureID(textID);
		float[] secondIdxArray = new float[entity.getIndices().length];
		for (int i = 0 ; i < secondIdxArray.length ; i++) {
			secondIdxArray[i] = entity.getIndices()[i] + nbVertices;
		}
		result.setIndices(Utils.concatFloatArrays(indices,secondIdxArray));
		return result;
	}
	
}
