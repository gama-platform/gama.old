package ummisco.gama.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL2;
import com.mysql.jdbc.StringUtils;

import msi.gama.metamodel.shape.GamaPoint;

public class ArbitraryQuadrilateralsProjection {
	int[] status = new int[1];
	int program;
	FloatBuffer attributeBuffer;
	ShortBuffer indicesBuffer;
	short[] indicesData;
	float[] attributesData;
	private final int[] textureIds = new int[1];
	int textureId;
	int attributePosition;
	int attributeRegion;

	public void draw(final GL2 gl, final GamaPoint... v) {

		gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);

		drawNonAffine((float) v[0].x, (float) v[0].y, (float) v[1].x, (float) v[1].y, (float) v[2].x, (float) v[2].y,
				(float) v[3].x, (float) v[3].y);

		attributeBuffer.position(0);
		attributeBuffer.put(attributesData);

		attributeBuffer.position(0);
		gl.glVertexAttribPointer(attributePosition, 2, GL2.GL_FLOAT, false, 5 * 4, attributeBuffer);
		gl.glEnableVertexAttribArray(attributePosition);

		attributeBuffer.position(2);
		gl.glVertexAttribPointer(attributeRegion, 3, GL2.GL_FLOAT, false, 5 * 4, attributeBuffer);
		gl.glEnableVertexAttribArray(attributeRegion);

		indicesBuffer.position(0);
		gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_SHORT, indicesBuffer);
		// gl.glUseProgram(0);
	}

	public void create(final GL2 gl, final int textureId, final float viewWidth, final float viewHeight) {
		// final String vertexShaderSource = "#if __VERSION__ >= 130\n" + " #define attribute in\n"
		// + " #define varying out\n" + "#endif\n" + "attribute vec2 a_Position;" + "attribute vec3 a_Region;"
		// + "varying vec3 v_Region;" + "uniform mat3 u_World;" + "void main()" + "{" + " v_Region = a_Region;"
		// + " vec3 xyz = u_World * vec3(a_Position, 1);" + " gl_Position = vec4(xyz.xy, 0, 1);" + "}";

		final String vertexShaderSource = "#if __VERSION__ >= 130\n" + "	#define attribute in\n"
				+ "	#define varying out\n" + "#endif\n" + "attribute vec2 a_Position;" + "attribute vec3 a_Region;"
				+ "varying vec3 v_Region;" + "uniform mat3 u_World;" + "void main()" + "{" + "   v_Region = a_Region;"
				+ "   vec3 xyz =  vec3(a_Position, 0);" + "   gl_Position = vec4(a_Position.xy, -1, 1);" + "}";
		final String fragmentShaderSource = "#if __VERSION__ >= 130\n" + "	#define attribute in\n"
				+ "	#define varying out\n" + "#endif\n" + "varying vec3 v_Region;" + "uniform sampler2D u_TextureId;"
				+ "void main()" + "{" + "   gl_FragColor = texture2D(u_TextureId, v_Region.xy / v_Region.z);" + "}";

		attributeBuffer = ByteBuffer.allocateDirect(5 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		attributesData = new float[5 * 4];

		indicesBuffer = ByteBuffer.allocateDirect(6 * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		indicesData = new short[] { 0, 1, 2, 2, 3, 0 };

		indicesBuffer.position(0);
		indicesBuffer.put(indicesData);

		program = loadProgram(gl, vertexShaderSource, fragmentShaderSource);

		gl.glUseProgram(program);

		final float width = viewWidth;
		final float height = viewHeight;

		final float world[] = new float[] { 2f / width, 0, 0, 0, 2f / height, 0, -1f, -1f, 1 };

		final int uniformWorld = gl.glGetUniformLocation(program, "u_World");
		final int uniformTextureId = gl.glGetUniformLocation(program, "u_TextureId");

		gl.glUniformMatrix3fv(uniformWorld, 1, false, world, 0);
		gl.glUniform1i(uniformTextureId, textureId);

		attributePosition = gl.glGetAttribLocation(program, "a_Position");
		attributeRegion = gl.glGetAttribLocation(program, "a_Region");
	}

	public void drawNonAffine(final float bottomLeftX, final float bottomLeftY, final float bottomRightX,
			final float bottomRightY, final float topRightX, final float topRightY, final float topLeftX,
			final float topLeftY) {
		final float ax = topRightX - bottomLeftX;
		final float ay = topRightY - bottomLeftY;
		final float bx = topLeftX - bottomRightX;
		final float by = topLeftY - bottomRightY;

		final float cross = ax * by - ay * bx;

		boolean rendered = false;

		if (cross != 0) {
			final float cy = bottomLeftY - bottomRightY;
			final float cx = bottomLeftX - bottomRightX;

			final float s = (ax * cy - ay * cx) / cross;

			if (s > 0 && s < 1) {
				final float t = (bx * cy - by * cx) / cross;

				if (t > 0 && t < 1) {
					// uv coordinates for texture
					final float u0 = 0; // texture bottom left u
					final float v0 = 0; // texture bottom left v
					final float u2 = 1; // texture top right u
					final float v2 = 1; // texture top right v

					int bufferIndex = 0;

					final float q0 = 1 / (1 - t);
					final float q1 = 1 / (1 - s);
					final float q2 = 1 / t;
					final float q3 = 1 / s;

					attributesData[bufferIndex++] = bottomLeftX;
					attributesData[bufferIndex++] = bottomLeftY;
					attributesData[bufferIndex++] = u0 * q0;
					attributesData[bufferIndex++] = v2 * q0;
					attributesData[bufferIndex++] = q0;

					attributesData[bufferIndex++] = bottomRightX;
					attributesData[bufferIndex++] = bottomRightY;
					attributesData[bufferIndex++] = u2 * q1;
					attributesData[bufferIndex++] = v2 * q1;
					attributesData[bufferIndex++] = q1;

					attributesData[bufferIndex++] = topRightX;
					attributesData[bufferIndex++] = topRightY;
					attributesData[bufferIndex++] = u2 * q2;
					attributesData[bufferIndex++] = v0 * q2;
					attributesData[bufferIndex++] = q2;

					attributesData[bufferIndex++] = topLeftX;
					attributesData[bufferIndex++] = topLeftY;
					attributesData[bufferIndex++] = u0 * q3;
					attributesData[bufferIndex++] = v0 * q3;
					attributesData[bufferIndex++] = q3;

					rendered = true;
				}
			}
		}

		if (!rendered) { throw new RuntimeException("Shape must be concave and vertices must be clockwise."); }
	}

	private int loadProgram(final GL2 gl, final String vertexShaderSource, final String fragmentShaderSource) {
		final int id = gl.glCreateProgram();

		final int vertexShaderId = loadShader(gl, GL2.GL_VERTEX_SHADER, vertexShaderSource);
		final int fragmentShaderId = loadShader(gl, GL2.GL_FRAGMENT_SHADER, fragmentShaderSource);

		gl.glAttachShader(id, vertexShaderId);
		gl.glAttachShader(id, fragmentShaderId);
		gl.glLinkProgram(id);
		gl.glDeleteShader(vertexShaderId);
		gl.glDeleteShader(fragmentShaderId);
		gl.glGetProgramiv(id, GL2.GL_LINK_STATUS, status, 0);

		if (status[0] == 0) {
			final byte[] chars = new byte[1000];
			gl.glGetProgramInfoLog(id, 1000, new int[1], 0, chars, 0);

			gl.glDeleteProgram(id);

			throw new RuntimeException("Shader error:" + StringUtils.toAsciiString(chars));
		}

		return id;
	}

	private int loadShader(final GL2 gl, final int type, final String source) {
		final int id = gl.glCreateShader(type);
		gl.glShaderSource(id, 1, new String[] { source }, new int[] { source.length() }, 0);
		gl.glCompileShader(id);
		gl.glGetShaderiv(id, GL2.GL_COMPILE_STATUS, status, 0);

		return id;
	}

}