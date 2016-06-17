package ummisco.gama.modernOpenGL.shader;

import javax.vecmath.Matrix4f;

import com.jogamp.opengl.GL2;

import ummisco.gama.modernOpenGL.Light;
import ummisco.gama.modernOpenGL.Maths;
import ummisco.gama.opengl.camera.ICamera;

public class ShaderProgram extends AbstractShader {
	
	private static String VERTEX_FILE = "F:/Gama/GamaSource/ummisco.gama.opengl/src/ummisco/gama/modernOpenGL/shader/vertexShader";		
	private static String FRAGMENT_FILE = "F:/Gama/GamaSource/ummisco.gama.opengl/src/ummisco/gama/modernOpenGL/shader/fragmentShader";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
//	private int location_lightPosition;
//	private int location_lightColor;
	
	public static final int POSITION_ATTRIBUTE_IDX = 0;
	public static final int COLOR_ATTRIBUTE_IDX = 1;
	
	public ShaderProgram(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(POSITION_ATTRIBUTE_IDX, "attribute_Position");
		super.bindAttribute(COLOR_ATTRIBUTE_IDX, "attribute_Color");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = getUniformLocation("transformationMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		//location_lightPosition = getUniformLocation("lightPosition");
		//location_lightColor = getUniformLocation("lightColor");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadLight(Light light) {
//		super.loadVector(location_lightPosition,light.getPosition());
//		super.loadVector(location_lightColor,light.getColor());
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(ICamera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
}
