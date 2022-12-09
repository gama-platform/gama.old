/*******************************************************************************************************
 *
 * AbstractShader.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.shaders;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Scanner;

import com.jogamp.opengl.GL2;

import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class AbstractShader.
 */
public abstract class AbstractShader {

	/** The gl. */
	protected GL2 gl;

	/** The is overlay. */
	protected boolean isOverlay = false;

	/** The program ID. */
	private int programID;
	
	/** The vertex shader ID. */
	private final int vertexShaderID;
	
	/** The fragment shader ID. */
	private final int fragmentShaderID;

	/** The location layer alpha. */
	private int location_layerAlpha;

	/** The Constant POSITION_ATTRIBUTE_IDX. */
	public static final int POSITION_ATTRIBUTE_IDX = 0;

	/** The Constant UVMAPPING_ATTRIBUTE_IDX. */
	public static final int UVMAPPING_ATTRIBUTE_IDX = 3;

	/** The matrix buffer. */
	private static FloatBuffer matrixBuffer = FloatBuffer.allocate(16);

	/**
	 * Instantiates a new abstract shader.
	 *
	 * @param gl the gl
	 * @param vertexFile the vertex file
	 * @param fragmentFile the fragment file
	 */
	protected AbstractShader(final GL2 gl, final String vertexFile, final String fragmentFile) {
		this.gl = gl;
		InputStream vertexInputStream, fragmentInputStream;

		try {
			vertexInputStream = getClass().getResourceAsStream(vertexFile);
			if (vertexInputStream == null)
				throw new RuntimeException("Cannot locate vertex shader program " + vertexFile);
			fragmentInputStream = getClass().getResourceAsStream(fragmentFile);
			if (fragmentInputStream == null)
				throw new RuntimeException("Cannot locate vertex shader program " + vertexFile);
		} catch (final Exception e) {
			DEBUG.ERR(e.getMessage());
			vertexShaderID = -1;
			fragmentShaderID = -1;
			return;
		}

		vertexShaderID = loadShader(vertexInputStream, GL2.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentInputStream, GL2.GL_FRAGMENT_SHADER);

		// Each shaderProgram must have
		// one vertex shader and one fragment shader.
		programID = this.gl.glCreateProgram();
		this.gl.glAttachShader(programID, vertexShaderID);
		this.gl.glAttachShader(programID, fragmentShaderID);

		// Associate attribute ids with the attribute names inside
		// the vertex shader.
		bindAttributes();

		this.gl.glLinkProgram(programID);
		this.gl.glValidateProgram(programID);

		getAllUniformLocations();
	}

	/**
	 * Load shader.
	 *
	 * @param is the is
	 * @param type the type
	 * @return the int
	 */
	private int loadShader(final InputStream is, final int type) {
		String shaderString = null;

		try (Scanner s = new java.util.Scanner(is);) {
			s.useDelimiter("\\A");
			shaderString = s.hasNext() ? s.next() : "";

			final int shaderID = gl.glCreateShader(type);

			// Compile the vertexShader String into a program.
			final String[] vlines = new String[] { shaderString };
			final int[] vlengths = new int[] { vlines[0].length() };
			gl.glShaderSource(shaderID, vlines.length, vlines, vlengths, 0);
			gl.glCompileShader(shaderID);
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

			// Check compile status.
			final int[] compiled = new int[1];
			gl.glGetShaderiv(shaderID, GL2.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {
				final int[] logLength = new int[1];
				gl.glGetShaderiv(shaderID, GL2.GL_INFO_LOG_LENGTH, logLength, 0);

				final byte[] log = new byte[logLength[0]];
				gl.glGetShaderInfoLog(shaderID, logLength[0], (int[]) null, 0, log, 0);

				DEBUG.ERR("Error compiling the vertex shader: " + new String(log));
			}

			return shaderID;
		}
	}

	/**
	 * Start.
	 */
	public void start() {
		gl.glUseProgram(programID);
	}

	/**
	 * Stop.
	 */
	public void stop() {
		gl.glUseProgram(0);
	}

	/**
	 * Gets the program ID.
	 *
	 * @return the program ID
	 */
	public int getProgramID() {
		return programID;
	}

	/**
	 * Sets the program ID.
	 *
	 * @param programID the new program ID
	 */
	public void setProgramID(final int programID) {
		this.programID = programID;
	}

	/**
	 * Gets the uniform location.
	 *
	 * @param uniformName the uniform name
	 * @return the uniform location
	 */
	public int getUniformLocation(final String uniformName) {
		return gl.glGetUniformLocation(programID, uniformName);
	}

	/**
	 * Bind attributes.
	 */
	protected abstract void bindAttributes();

	/**
	 * Bind attribute.
	 *
	 * @param attribute the attribute
	 * @param variableName the variable name
	 */
	protected void bindAttribute(final int attribute, final String variableName) {
		gl.glBindAttribLocation(programID, attribute, variableName);
	}

	/**
	 * Gets the all uniform locations.
	 *
	 * @return the all uniform locations
	 */
	protected void getAllUniformLocations() {
		location_layerAlpha = getUniformLocation("layerAlpha");
	}

	// static public FloatBuffer getFloatBuffer(final Matrix4f matrix) {
	// final FloatBuffer result = FloatBuffer.allocate(16);
	// result.put(0, matrix.m00);
	// result.put(1, matrix.m01);
	// result.put(2, matrix.m02);
	// result.put(3, matrix.m03);
	// result.put(4, matrix.m10);
	// result.put(5, matrix.m11);
	// result.put(6, matrix.m12);
	// result.put(7, matrix.m13);
	// result.put(8, matrix.m20);
	// result.put(9, matrix.m21);
	// result.put(10, matrix.m22);
	// result.put(11, matrix.m23);
	// result.put(12, matrix.m30);
	// result.put(13, matrix.m31);
	// result.put(14, matrix.m32);
	// result.put(15, matrix.m33);
	// return result;
	// }
	//
	// protected void loadMatrix(final int location, final Matrix4f matrix) {
	// matrixBuffer = getFloatBuffer(matrix);
	// matrixBuffer.flip();
	// gl.glUniformMatrix4fv(location, 1, false, matrixBuffer.array(), 0);
	// }

	/**
	 * Load float.
	 *
	 * @param location the location
	 * @param value the value
	 */
	public void loadFloat(final int location, final float value) {
		gl.glUniform1f(location, value);
	}

	/**
	 * Load int.
	 *
	 * @param location the location
	 * @param value the value
	 */
	protected void loadInt(final int location, final int value) {
		gl.glUniform1i(location, value);
	}

	/**
	 * Sets the layer alpha.
	 *
	 * @param layerAlpha the new layer alpha
	 */
	public void setLayerAlpha(final float layerAlpha) {
		loadFloat(location_layerAlpha, layerAlpha);
	}

	/**
	 * Gets the translation.
	 *
	 * @return the translation
	 */
	public GamaPoint getTranslation() {
		return new GamaPoint(0, 0, 0);
	}

	/**
	 * Checks if is overlay.
	 *
	 * @return true, if is overlay
	 */
	public boolean isOverlay() {
		return isOverlay;
	}

	/**
	 * Use normal.
	 *
	 * @return true, if successful
	 */
	abstract public boolean useNormal();

	/**
	 * Use texture.
	 *
	 * @return true, if successful
	 */
	abstract public boolean useTexture();

	/**
	 * Gets the texture ID.
	 *
	 * @return the texture ID
	 */
	abstract public int getTextureID();
}
