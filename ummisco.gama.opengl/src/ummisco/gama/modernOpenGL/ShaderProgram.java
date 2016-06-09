package ummisco.gama.modernOpenGL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Matrix4f;

import com.jogamp.opengl.GL2;

public class ShaderProgram {
	
	private GL2 gl;
	
	private final static String vertexShaderString =
	// For GLSL 1 and 1.1 code i highly recommend to not include a
	// GLSL ES language #version line, GLSL ES section 3.4
	// Many GPU drivers refuse to compile the shader if #version is different from
	// the drivers internal GLSL version.
	//
	// This demo use GLSL version 1.1 (the implicit version)			 
		"#if __VERSION__ >= 130\n" + // GLSL 130+ uses in and out
		"  #define attribute in\n" + // instead of attribute and varying
		"  #define varying out\n" +  // used by OpenGL 3 core and later.
		"#endif\n" +
		
		"#ifdef GL_ES \n" +
		"precision mediump float; \n" + // Precision Qualifiers
		"precision mediump int; \n" +   // GLSL ES section 4.5.2
		"#endif \n" +
		
		"uniform mat4    transformationMatrix; \n" + //
		"uniform mat4    projectionMatrix; \n" + // 
		"attribute vec4  attribute_Position; \n" + // the vertex shader
		"attribute vec4  attribute_Color; \n" +    // uniform and attributes
		
		"varying vec4    varying_Color; \n" + // Outgoing varying data
		                                      // sent to the fragment shader
		"void main(void) \n" +
		"{ \n" +
		"  varying_Color = attribute_Color; \n" +
		"  gl_Position = projectionMatrix * transformationMatrix * attribute_Position; \n" +
		"} ";
		
	private final static String fragmentShaderString =
		"#if __VERSION__ >= 130\n" +
		"  #define varying in\n" +
		"  out vec4 mgl_FragColor;\n" +
		"  #define texture2D texture\n" +
		"  #define gl_FragColor mgl_FragColor\n" +
		"#endif\n" +
		
		"#ifdef GL_ES \n" +
		"precision mediump float; \n" +
		"precision mediump int; \n" +
		"#endif \n" +
		
		"varying   vec4    varying_Color; \n" + //incoming varying data to the
		                                        //fragment shader
		                                        //sent from the vertex shader
		"void main (void) \n" +
		"{ \n" +
		"  gl_FragColor = varying_Color; \n" +
		"} ";
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	
	private static FloatBuffer matrixBuffer = FloatBuffer.allocate(16);
	
	public ShaderProgram(GL2 gl) {
		this.gl = gl;
		loadShader();
	}
	
	public void loadShader() {
//		String vertexShaderString =
//	// For GLSL 1 and 1.1 code i highly recommend to not include a
//	// GLSL ES language #version line, GLSL ES section 3.4
//	// Many GPU drivers refuse to compile the shader if #version is different from
//	// the drivers internal GLSL version.
//	//
//	// This demo use GLSL version 1.1 (the implicit version)			 
//			"#if __VERSION__ >= 130\n" + // GLSL 130+ uses in and out
//			"  #define attribute in\n" + // instead of attribute and varying
//			"  #define varying out\n" +  // used by OpenGL 3 core and later.
//			"#endif\n" +
//			
//			"#ifdef GL_ES \n" +
//			"precision mediump float; \n" + // Precision Qualifiers
//			"precision mediump int; \n" +   // GLSL ES section 4.5.2
//			"#endif \n" +
//			
//			"uniform mat4    uniform_Projection; \n" + // Incoming data used by
//			"attribute vec4  attribute_Position; \n" + // the vertex shader
//			"attribute vec4  attribute_Color; \n" +    // uniform and attributes
//			
//			"varying vec4    varying_Color; \n" + // Outgoing varying data
//			                                      // sent to the fragment shader
//			"void main(void) \n" +
//			"{ \n" +
//			"  varying_Color = attribute_Color; \n" +
//			"  gl_Position = uniform_Projection * attribute_Position; \n" +
//			"} ";
//			
//			String fragmentShaderString =
//			"#if __VERSION__ >= 130\n" +
//			"  #define varying in\n" +
//			"  out vec4 mgl_FragColor;\n" +
//			"  #define texture2D texture\n" +
//			"  #define gl_FragColor mgl_FragColor\n" +
//			"#endif\n" +
//			
//			"#ifdef GL_ES \n" +
//			"precision mediump float; \n" +
//			"precision mediump int; \n" +
//			"#endif \n" +
//			
//			"varying   vec4    varying_Color; \n" + //incoming varying data to the
//			                                        //fragment shader
//			                                        //sent from the vertex shader
//			"void main (void) \n" +
//			"{ \n" +
//			"  gl_FragColor = varying_Color; \n" +
//			"} ";
			
			// Create GPU shader handles
			// OpenGL ES returns a index id to be stored for future reference.
			int vertShader = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
			int fragShader = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
			
			//Compile the vertexShader String into a program.
			String[] vlines = new String[] { vertexShaderString };
			int[] vlengths = new int[] { vlines[0].length() };
			gl.glShaderSource(vertShader, vlines.length, vlines, vlengths, 0);
			gl.glCompileShader(vertShader);
			
			//Check compile status.
			int[] compiled = new int[1];
			gl.glGetShaderiv(vertShader, GL2.GL_COMPILE_STATUS, compiled,0);
			if(compiled[0]!=0){System.out.println("Horray! vertex shader compiled");}
			else {
				int[] logLength = new int[1];
				gl.glGetShaderiv(vertShader, GL2.GL_INFO_LOG_LENGTH, logLength, 0);
	
				byte[] log = new byte[logLength[0]];
				gl.glGetShaderInfoLog(vertShader, logLength[0], (int[])null, 0, log, 0);
	
				System.err.println("Error compiling the vertex shader: " + new String(log));
				System.exit(1);
			}
			
			//Compile the fragmentShader String into a program.
			String[] flines = new String[] { fragmentShaderString };
			int[] flengths = new int[] { flines[0].length() };
			gl.glShaderSource(fragShader, flines.length, flines, flengths, 0);
			gl.glCompileShader(fragShader);
	
			//Check compile status.
			gl.glGetShaderiv(fragShader, GL2.GL_COMPILE_STATUS, compiled,0);
			if(compiled[0]!=0){System.out.println("Horray! fragment shader compiled");}
			else {
				int[] logLength = new int[1];
				gl.glGetShaderiv(fragShader, GL2.GL_INFO_LOG_LENGTH, logLength, 0);
	
				byte[] log = new byte[logLength[0]];
				gl.glGetShaderInfoLog(fragShader, logLength[0], (int[])null, 0, log, 0);
	
				System.err.println("Error compiling the fragment shader: " + new String(log));
				System.exit(1);
			}
			
	
			//Each shaderProgram must have
			//one vertex shader and one fragment shader.
			programID = gl.glCreateProgram();
			gl.glAttachShader(programID, vertShader);
			gl.glAttachShader(programID, fragShader);
	
			//Associate attribute ids with the attribute names inside
			//the vertex shader.
			gl.glBindAttribLocation(programID, 0, "attribute_Position");
			gl.glBindAttribLocation(programID, 1, "attribute_Color");
	
			gl.glLinkProgram(programID);
			gl.glValidateProgram(programID);

			getAllUniformLocations();
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
	
	protected void bindAttribute(int attribute, String variableName) {
		gl.glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected void getAllUniformLocations() {
		location_transformationMatrix = getUniformLocation("transformationMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		loadMatrix(location_projectionMatrix, matrix);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrixBuffer = Maths.getFloatBuffer(matrix);
		matrixBuffer.flip();
		gl.glUniformMatrix4fv(location, 1, false, matrixBuffer.array() , 0);
	}
}
