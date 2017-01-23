/*********************************************************************************************
 *
 * 'DrawingEntity.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL;

import javax.vecmath.Vector3f;

import ummisco.gama.modernOpenGL.shader.AbstractShader;

public class DrawingEntity {

	static public enum Type {
		TEXTURED, FACE, LINE, POINT, STRING, BILLBOARDING
	}; // border == triangulate

	public Type type;

	private float[] pickingIds;
	private float[] vertices;
	private float[] colors;
	private float[] indices;
	private float[] normals;
	private float[] uvMapping;
	private int textureID;
	private String texturePath;
	private int[][][] bufferedImageTextureValue; // only for grid or special textures with no path attached
	private Material material;

	// only for string :
	private float fontWidth = -1; // init value set to -1. If the value is -1, the entity is not a string
	private float fontEdge = -1; // init value set to -1. If the value is -1, the entity is not a string
	private boolean isBillboarding = false;
	private boolean isOverlay = false;
	private Vector3f translation = new Vector3f();

	private AbstractShader shader;

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(final Material material) {
		this.material = material;
	}

	public float[] getUvMapping() {
		return uvMapping;
	}

	public void setUvMapping(final float[] uvMapping) {
		this.uvMapping = uvMapping;
	}

	public DrawingEntity() {

	}

	public float[] getVertices() {
		return vertices;
	}

	public void setVertices(final float[] vertices) {
		this.vertices = vertices;
	}

	public float[] getColors() {
		return colors;
	}

	public void setColors(final float[] colors) {
		this.colors = colors;
	}

	public float[] getIndices() {
		return indices;
	}

	public void setIndices(final float[] indices) {
		this.indices = indices;
	}

	public float[] getNormals() {
		return normals;
	}

	public void setNormals(final float[] normals) {
		this.normals = normals;
	}

	public void setTextureID(final int textureID) {
		this.textureID = textureID;
	}

	public int getTextureID() {
		return textureID;
	}

	// public void setTexturePath(String texturePath) {
	// this.texturePath = texturePath;
	// }
	//
	// public String getTexturePath() {
	// return texturePath;
	// }

	public void setShader(final AbstractShader shader) {
		this.shader = shader;
	}

	public AbstractShader getShader() {
		return shader;
	}

	public void setFontWidth(final float value) {
		fontWidth = value;
	}

	public float getFontWidth() {
		return fontWidth;
	}

	public void setFontEdge(final float value) {
		fontEdge = value;
	}

	public float getFontEdge() {
		return fontEdge;
	}

	public void enableBillboarding() {
		isBillboarding = true;
	}

	public void disableBillboarding() {
		isBillboarding = false;
	}

	public boolean isBillboarding() {
		return isBillboarding;
	}

	public boolean isOverlay() {
		return isOverlay;
	}

	public void enableOverlay(final boolean value) {
		isOverlay = value;
	}

	public void setTranslation(final Vector3f translation) {
		this.translation = translation;
	}

	public Vector3f getTranslation() {
		return this.translation;
	}

	public float[] getPickingIds() {
		return pickingIds;
	}

	public void setPickingIds(final float[] pickingIds) {
		this.pickingIds = pickingIds;
	}

	public int[][][] getBufferedImageTextureValue() {
		return bufferedImageTextureValue;
	}

	public void setBufferedImageTextureValue(final int[][][] bufferedImageTextureValue) {
		this.bufferedImageTextureValue = bufferedImageTextureValue;
	}

}
