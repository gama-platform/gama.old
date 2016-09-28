package ummisco.gama.modernOpenGL.shader;

import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;

import ummisco.gama.opengl.vaoGenerator.GeomMathUtils;

public abstract class AbstractShader {
	
	protected GL2 gl;
	
	protected boolean isOverlay=false;
	private float ratioForOverlay;
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_layerAlpha;
	
	public static final int POSITION_ATTRIBUTE_IDX = 0;
	public static final int COLOR_ATTRIBUTE_IDX = 1;
	public static final int NORMAL_ATTRIBUTE_IDX = 2;
	public static final int UVMAPPING_ATTRIBUTE_IDX = 3;
	
	private static FloatBuffer matrixBuffer = FloatBuffer.allocate(16);
	
	protected AbstractShader(GL2 gl, String vertexFile, String fragmentFile) {
		this.gl = gl;
		
		InputStream vertexInputStream = this.getClass().getClassLoader().getResourceAsStream("/shader/"+vertexFile);
		InputStream fragmentInputStream = this.getClass().getClassLoader().getResourceAsStream("/shader/"+fragmentFile);

		vertexShaderID = loadShader(vertexInputStream,GL2.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentInputStream,GL2.GL_FRAGMENT_SHADER);
		
		//Each shaderProgram must have
		//one vertex shader and one fragment shader.
		programID = this.gl.glCreateProgram();
		this.gl.glAttachShader(programID, vertexShaderID);
		this.gl.glAttachShader(programID, fragmentShaderID);

		//Associate attribute ids with the attribute names inside
		//the vertex shader.
		bindAttributes();

		this.gl.glLinkProgram(programID);
		this.gl.glValidateProgram(programID);

		getAllUniformLocations();
	}
	
	private int loadShader(InputStream is, int type) {
		String shaderString = null;
		
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		shaderString = s.hasNext() ? s.next() : "";
		
		int shaderID = gl.glCreateShader(type);
		
		//Compile the vertexShader String into a program.
		String[] vlines = new String[] { shaderString };
		int[] vlengths = new int[] { vlines[0].length() };
		gl.glShaderSource(shaderID, vlines.length, vlines, vlengths, 0);
		gl.glCompileShader(shaderID);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		//Check compile status.
		int[] compiled = new int[1];
		gl.glGetShaderiv(shaderID, GL2.GL_COMPILE_STATUS, compiled,0);
		if(compiled[0]==0){
			int[] logLength = new int[1];
			gl.glGetShaderiv(shaderID, GL2.GL_INFO_LOG_LENGTH, logLength, 0);

			byte[] log = new byte[logLength[0]];
			gl.glGetShaderInfoLog(shaderID, logLength[0], (int[])null, 0, log, 0);

			System.err.println("Error compiling the vertex shader: " + new String(log));
			System.exit(1);
		}
		
		return shaderID;
	}
	
	public void start() {
		gl.glUseProgram(programID);
	}
	
	public void stop() {
		gl.glUseProgram(0);
	}
	
	public void cleanUp() {
		stop();
		gl.glDetachShader(programID, vertexShaderID);
		gl.glDetachShader(programID, fragmentShaderID);
		gl.glDeleteShader(vertexShaderID);
		gl.glDeleteShader(fragmentShaderID);
		gl.glDeleteProgram(programID);
	}
	
	public int getProgramID() {
		return programID;
	}

	public void setProgramID(int programID) {
		this.programID = programID;
	}
	
	protected int getUniformLocation(String uniformName) {
		return gl.glGetUniformLocation(programID, uniformName);
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName) {
		gl.glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected void getAllUniformLocations() {
		location_transformationMatrix = getUniformLocation("transformationMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_layerAlpha = getUniformLocation("layerAlpha");
	}
	
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrixBuffer = GeomMathUtils.getFloatBuffer(matrix);
		matrixBuffer.flip();
		gl.glUniformMatrix4fv(location, 1, false, matrixBuffer.array() , 0);
	}
	
	protected void loadFloat(int location, float value) {
		gl.glUniform1f(location, value);
	}
	
	protected void loadInt(int location, int value) {
		gl.glUniform1i(location, value);
	}
	
	protected void loadVector(int location, Vector3f vector) {
		gl.glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		if (isOverlay) {
			matrix.setIdentity();
		}
		loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		if (isOverlay) {
			matrix.setIdentity();
			matrix.m30 = -1f;
			matrix.m31 = 1f;
			//matrix.setScale(1f);
			matrix.m11 = -matrix.m11;
		}
		loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f viewMatrix) {
		if (isOverlay) {
			viewMatrix.setIdentity();
		}
		loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void setLayerAlpha(float layerAlpha) {
		loadFloat(location_layerAlpha, layerAlpha);
	}
	
	public Vector3f getTranslation() {
		return new Vector3f(0,0,0);
	}
	
	public void setRatioForOverlay(float value) {
		ratioForOverlay = value;
	}
	
	public void enableOverlay(boolean value) {
		isOverlay = value;
	}
	
	public boolean isOverlay() {
		return isOverlay;
	}
	
	abstract public boolean useNormal();
	abstract public boolean useTexture();
	abstract public int getTextureID();
}
