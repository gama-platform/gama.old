package ummisco.gama.modernOpenGL.shader;

import javax.vecmath.Matrix4f;

import com.jogamp.opengl.GL2;

import ummisco.gama.modernOpenGL.Light;
import ummisco.gama.opengl.camera.ICamera;
import ummisco.gama.opengl.vaoGenerator.TransformationMatrix;

public class ShaderProgram extends AbstractShader {
	
	private static String VERTEX_FILE = "vertexShader";		
	private static String FRAGMENT_FILE = "fragmentShader";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColor;
	private int location_shineDamper;	// for specular light
	private int location_reflectivity;	// for specular light
	private int location_useTexture;	// 0 for no, 1 for yes
	private int location_texture;
	
	public static final int POSITION_ATTRIBUTE_IDX = 0;
	public static final int COLOR_ATTRIBUTE_IDX = 1;
	public static final int NORMAL_ATTRIBUTE_IDX = 2;
	public static final int UVMAPPING_ATTRIBUTE_IDX = 3;
	
	public ShaderProgram(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(POSITION_ATTRIBUTE_IDX, "attribute_Position");
		super.bindAttribute(COLOR_ATTRIBUTE_IDX, "attribute_Color");
		super.bindAttribute(UVMAPPING_ATTRIBUTE_IDX, "attribute_TextureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = getUniformLocation("transformationMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_lightPosition = getUniformLocation("lightPosition");
		location_lightColor = getUniformLocation("lightColor");
		location_shineDamper = getUniformLocation("shineDamper");
		location_reflectivity = getUniformLocation("reflectivity");
		location_useTexture = getUniformLocation("useTexture");
		location_texture = getUniformLocation("textureSampler");
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadLight(Light light) {
		super.loadVector(location_lightPosition,light.getPosition());
		super.loadVector(location_lightColor,light.getColor());
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(ICamera camera) {
		Matrix4f viewMatrix = TransformationMatrix.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadTexture(int textureId) {
		super.loadFloat(location_useTexture,1f);
		super.loadInt(location_texture,textureId);
	}
}
