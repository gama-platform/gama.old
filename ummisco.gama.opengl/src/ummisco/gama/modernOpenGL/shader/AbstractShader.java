package ummisco.gama.modernOpenGL.shader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;

import ummisco.gama.opengl.vaoGenerator.GeomMathUtils;

public abstract class AbstractShader {
	
	protected GL2 gl;
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = FloatBuffer.allocate(16);
	
	public AbstractShader(GL2 gl, String vertexFile, String fragmentFile) {
		this.gl = gl;
		
		String absolutePathToShaderFolder = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() 
				+ "src"
				+ this.getClass().getResource("../shader").getPath();
		vertexFile = absolutePathToShaderFolder + vertexFile;
		fragmentFile = absolutePathToShaderFolder + fragmentFile;

		vertexShaderID = loadShader(vertexFile,GL2.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile,GL2.GL_FRAGMENT_SHADER);
		
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
	
	public int loadShader(String file, int type) {
		
		String shaderString = null;
		
		try {
			shaderString = readFile(file,StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	protected abstract void getAllUniformLocations();
	
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
	
	protected void loadBoolean (int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		gl.glUniform1f(location, toLoad);
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
}
