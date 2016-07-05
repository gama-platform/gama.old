package ummisco.gama.modernOpenGL.shader;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

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
	private int location_useNormals;	// 0 for no, 1 for yes
	private int location_texture;
	private int location_ambientLight;
	
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
		location_useNormals = getUniformLocation("useNormals");
		location_texture = getUniformLocation("textureSampler");
		location_ambientLight = getUniformLocation("ambientLight");
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
	
	public void loadAmbientLight(Vector3f light) {
		super.loadVector(location_ambientLight,light);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(ICamera camera) {
		Matrix4f viewMatrix = TransformationMatrix.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadTexture(int textureId) {
		super.loadInt(location_texture,textureId);
	}
	
	public void enableTexture() {
		super.loadFloat(location_useTexture,1f);
	}
	
	public void disableTexture() {
		super.loadFloat(location_useTexture,0f);
	}
	
	public void disableNormal() {
		super.loadFloat(location_useNormals, 0f);
	}
	
	public void enableNormal() {
		super.loadFloat(location_useNormals, 1f);
	}
}
