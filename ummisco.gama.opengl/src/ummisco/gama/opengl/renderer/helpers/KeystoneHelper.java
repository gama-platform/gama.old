package ummisco.gama.opengl.renderer.helpers;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.util.GamaColor.NamedGamaColor;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.renderer.shaders.AbstractPostprocessingShader;
import ummisco.gama.opengl.renderer.shaders.AbstractShader;
import ummisco.gama.opengl.renderer.shaders.FrameBufferObject;
import ummisco.gama.opengl.renderer.shaders.KeystoneShaderProgram;

public class KeystoneHelper extends AbstractRendererHelper {

	private FrameBufferObject fboScene;
	protected boolean drawKeystoneHelper = false;
	protected int cornerSelected = -1, cornerHovered = -1;
	private int uvMappingBufferIndex;
	private int verticesBufferIndex;
	private int indexBufferIndex;
	private KeystoneShaderProgram shader;
	private boolean worldCorners = false;
	private static final Color[] FILL_COLORS = new Color[] { NamedGamaColor.getNamed("gamared").withAlpha(0.3),
			NamedGamaColor.getNamed("gamablue").withAlpha(0.3), NamedGamaColor.getNamed("black").withAlpha(0.3) };

	final IntBuffer ibIdxBuff = Buffers.newDirectIntBuffer(new int[] { 0, 1, 2, 0, 2, 3 });

	public KeystoneHelper(final IOpenGLRenderer r) {
		super(r);
	}

	@Override
	public void initialize() {

	}

	public int getCornerSelected() {
		return cornerSelected;
	}

	public GamaPoint[] getCoords() {
		return getData().getKeystone().toCoordinateArray();
	}

	public GamaPoint getKeystoneCoordinates(final int corner) {
		return getCoords()[corner];
	}

	public boolean drawKeystoneHelper() {
		return drawKeystoneHelper;
	}

	public void startDrawHelper() {
		drawKeystoneHelper = true;
		cornerSelected = -1;
	}

	public void stopDrawHelper() {
		drawKeystoneHelper = false;
	}

	public void switchCorners() {
		worldCorners = !getData().KEYSTONE_IDENTITY.getEnvelope().covers(getData().getKeystone().getEnvelope());
	}

	public void dispose() {
		final GL2 gl = getGL();
		if (fboScene != null) {
			fboScene.cleanUp();
		}
		if (gl != null) {
			gl.glDeleteBuffers(3, new int[] { indexBufferIndex, verticesBufferIndex, uvMappingBufferIndex }, 0);
		}
	}

	public void beginRenderToTexture() {
		final GL2 gl = getGL();
		gl.glClearColor(0, 0, 0, 1.0f);
		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		if (fboScene == null) {
			fboScene = new FrameBufferObject(gl, getRenderer().getDisplayWidth(), getRenderer().getDisplayHeight());
		}
		// redirect the rendering to the fbo_scene (will be rendered later, as a texture)
		fboScene.bindFrameBuffer();

	}

	private void drawRectangle(final OpenGL openGL, final double[] center, final double width, final double height,
			final Color fill) {
		openGL.pushMatrix();
		openGL.translateBy(center);
		openGL.setCurrentColor(fill);
		openGL.scaleBy(Scaling3D.of(width, height, 1));
		openGL.drawCachedGeometry(IShape.Type.SQUARE, true, null);
		openGL.popMatrix();
	}

	private void drawKeystoneMarks() {
		final OpenGL openGL = getOpenGL();
		final GL2 gl = getGL();

		//
		final int displayWidthInPixels = getRenderer().getViewWidth();
		final int displayHeightInPixels = getRenderer().getViewHeight();
		final double pixelWidthIn01 = 1d / displayWidthInPixels;
		final double pixelHeightIn01 = 1d / displayHeightInPixels;
		final double[] worldCoords = getOpenGL().getPixelWidthAndHeightOfWorld();
		final double worldWidthInPixels = worldCoords[0];
		final double worldHeightInPixels = worldCoords[1];
		final double widthRatio = worldWidthInPixels / displayWidthInPixels;
		final double heightRatio = worldHeightInPixels / displayHeightInPixels;
		final double xOffsetIn01 = 1 - widthRatio;
		final double yOffsetIn01 = 1 - heightRatio;
		final double labelHeightIn01 = pixelHeightIn01 * (18 + 20);
		ICoordinates vertices;
		if (!worldCorners) {
			vertices = LayeredDisplayData.KEYSTONE_IDENTITY;
			// 0, 0, 0 | 0, 1, 0 | 1, 1, 0 | 1, 0, 0
		} else {
			vertices = ICoordinates.ofLength(4);
			vertices.at(0).setLocation(xOffsetIn01, yOffsetIn01, 0);
			vertices.at(1).setLocation(xOffsetIn01, 1 - yOffsetIn01, 0);
			vertices.at(2).setLocation(1 - xOffsetIn01, 1 - yOffsetIn01, 0);
			vertices.at(3).setLocation(1 - xOffsetIn01, yOffsetIn01, 0);
		}

		openGL.pushIdentity(GL2.GL_PROJECTION);
		gl.glOrtho(0, 1, 0, 1, 1, -1);
		openGL.setLighting(false);
		openGL.push(GL2.GL_MODELVIEW);
		vertices.visit((id, x, y, z) -> {
			// Basic computations on text and color
			final String text = floor4Digit(getCoords()[id].x) + "," + floor4Digit(getCoords()[id].y);
			final int lengthOfTextInPixels = openGL.getGlut().glutBitmapLength(GLUT.BITMAP_HELVETICA_18, text);
			final double labelWidthIn01 = pixelWidthIn01 * (lengthOfTextInPixels + 20);
			final int fill = id == cornerSelected ? 0 : id == cornerHovered ? 1 : 2;
			// Drawing the background of labels
			final double xLabelIn01 = x + (id == 0 || id == 1 ? labelWidthIn01 / 2 : -labelWidthIn01 / 2);
			final double yLabelIn01 = y + (id == 0 || id == 3 ? labelHeightIn01 / 2 : -labelHeightIn01 / 2);
			drawRectangle(openGL, new double[] { xLabelIn01, yLabelIn01, z }, labelWidthIn01, labelHeightIn01,
					FILL_COLORS[fill]);

			// Setting back the color to white
			gl.glColor3d(1, 1, 1);
			// Drawing the text itself
			final double xPosIn01 = id == 0 || id == 1 ? 10 * pixelWidthIn01 + (worldCorners ? xOffsetIn01 : 0)
					: 1 - labelWidthIn01 + 10 * pixelWidthIn01 - (worldCorners ? xOffsetIn01 : 0);
			final double yPosIn01 = id == 0 || id == 3 ? 12 * pixelHeightIn01 + (worldCorners ? yOffsetIn01 : 0)
					: 1 - labelHeightIn01 + 12 * pixelHeightIn01 - (worldCorners ? yOffsetIn01 : 0);
			openGL.getGL().glRasterPos2d(xPosIn01, yPosIn01);
			openGL.getGlut().glutBitmapString(GLUT.BITMAP_HELVETICA_18, text);
		}, 4, true);
		openGL.pop(GL2.GL_MODELVIEW);
		openGL.setLighting(true);
		openGL.pop(GL2.GL_PROJECTION);

	}

	private double floor4Digit(final double n) {
		double number = n * 1000;
		number = Math.round(number);
		number /= 1000;
		return number;
	}

	public void finishRenderToTexture() {
		if (drawKeystoneHelper) {
			drawKeystoneMarks();
		}
		// gl.glDisable(GL2.GL_DEPTH_TEST); // disables depth testing
		final AbstractPostprocessingShader theShader = getShader();
		// unbind the last fbo
		fboScene.unbindCurrentFrameBuffer();
		// prepare shader
		theShader.start();
		prepareShader(theShader);
		// build the surface
		createScreenSurface();
		// draw
		final GL2 gl = getGL();
		gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_INT, 0);
		theShader.stop();
	}

	public KeystoneShaderProgram getShader() {
		final GL2 gl = getGL();
		if (shader == null) {
			shader = new KeystoneShaderProgram(gl, "keystoneVertexShader2", "keystoneFragmentShader2");
			final int[] handles = new int[3];
			gl.glGenBuffers(3, handles, 0);
			uvMappingBufferIndex = handles[0];
			verticesBufferIndex = handles[1];
			indexBufferIndex = handles[2];
		}
		return shader;
	}

	private void prepareShader(final AbstractPostprocessingShader shaderProgram) {
		shaderProgram.loadTexture(0);
		shaderProgram.storeTextureID(fboScene.getFBOTexture());
	}

	public void createScreenSurface() {
		final GL2 gl = getGL();
		// Keystoning computation (cf
		// http://www.bitlush.com/posts/arbitrary-quadrilaterals-in-opengl-es-2-0)
		// transform the coordinates [0,1] --> [-1,+1]
		final ICoordinates coords = getData().getKeystone();
		final float[] p0 = new float[] { (float) coords.at(0).x * 2f - 1f, (float) (coords.at(0).y * 2f - 1f) }; // bottom-left
		final float[] p1 = new float[] { (float) coords.at(1).x * 2f - 1f, (float) coords.at(1).y * 2f - 1f }; // top-left
		final float[] p2 = new float[] { (float) coords.at(2).x * 2f - 1f, (float) coords.at(2).y * 2f - 1f }; // top-right
		final float[] p3 = new float[] { (float) coords.at(3).x * 2f - 1f, (float) coords.at(3).y * 2f - 1f }; // bottom-right

		final float ax = (p2[0] - p0[0]) / 2f;
		final float ay = (p2[1] - p0[1]) / 2f;
		final float bx = (p3[0] - p1[0]) / 2f;
		final float by = (p3[1] - p1[1]) / 2f;

		final float cross = ax * by - ay * bx;

		if (cross != 0) {
			final float cy = (p0[1] - p1[1]) / 2f;
			final float cx = (p0[0] - p1[0]) / 2f;

			final float s = (ax * cy - ay * cx) / cross;

			final float t = (bx * cy - by * cx) / cross;

			final float q0 = 1 / (1 - t);
			final float q1 = 1 / (1 - s);
			final float q2 = 1 / t;
			final float q3 = 1 / s;

			// I can now pass (u * q, v * q, q) to OpenGL
			final float[] listVertices =
					new float[] { p0[0], p0[1], 1f, p1[0], p1[1], 0f, p2[0], p2[1], 0f, p3[0], p3[1], 1f };
			final float[] listUvMapping =
					new float[] { 0f, 1f * q0, 0f, q0, 0f, 0f, 0f, q1, 1f * q2, 0f, 0f, q2, 1f * q3, 1f * q3, 0f, q3 };
			// VERTICES POSITIONS BUFFER
			storeAttributes(AbstractShader.POSITION_ATTRIBUTE_IDX, verticesBufferIndex, 3, listVertices);
			// UV MAPPING (If a texture is defined)
			storeAttributes(AbstractShader.UVMAPPING_ATTRIBUTE_IDX, uvMappingBufferIndex, 4, listUvMapping);

		}

		// gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, fboScene.getFBOTexture());
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				getData().isAntialias() ? GL.GL_LINEAR : GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				getData().isAntialias() ? GL.GL_LINEAR : GL.GL_NEAREST);

		// Select the VBO, GPU memory data, to use for colors
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBufferIndex);
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, 24, ibIdxBuff, GL2.GL_STATIC_DRAW);
		ibIdxBuff.rewind();
	}

	private void storeAttributes(final int shaderAttributeType, final int bufferIndex, final int size,
			final float[] data) {
		final GL2 gl = getGL();
		// Select the VBO, GPU memory data, to use for data
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferIndex);
		// Associate Vertex attribute with the last bound VBO
		gl.glVertexAttribPointer(shaderAttributeType, size, GL2.GL_FLOAT, false, 0, 0 /* offset */);
		// compute the total size of the buffer :
		final int numBytes = data.length * 4;
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numBytes, null, GL2.GL_STATIC_DRAW);

		final FloatBuffer fbData = Buffers.newDirectFloatBuffer(data);
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, numBytes, fbData);
		gl.glEnableVertexAttribArray(shaderAttributeType);
	}

	public void setCornerSelected(final int cornerId) {
		cornerSelected = cornerId;
	}

	public void resetCorner(final int cornerId) {
		setKeystoneCoordinates(cornerId, LayeredDisplayData.KEYSTONE_IDENTITY.at(cornerId));
		cornerSelected = -1;
		cornerHovered = -1;
	}

	public int cornerSelected(final GamaPoint mouse) {
		if (mouse.x < getRenderer().getViewWidth() / 2) {
			if (mouse.y < getRenderer().getViewHeight() / 2) {
				return 1;
			} else {
				return 0;
			}
		} else {
			if (mouse.y < getRenderer().getViewHeight() / 2) {
				return 2;
			} else {
				return 3;
			}
		}
	}

	public int cornerHovered(final GamaPoint mouse) {
		if (mouse.x < getRenderer().getViewWidth() / 2) {
			if (mouse.y < getRenderer().getViewHeight() / 2) {
				return 1;
			} else {
				return 0;
			}
		} else {
			if (mouse.y < getRenderer().getViewHeight() / 2) {
				return 2;
			} else {
				return 3;
			}
		}
	}

	public void setCornerHovered(final int cornerId) {
		cornerHovered = cornerId;
	}

	public void setKeystoneCoordinates(final int cornerId, final GamaPoint p) {
		getData().getKeystone().replaceWith(cornerId, p.x, p.y, p.z);
		switchCorners();
		getData().setKeystone(getData().getKeystone());
	}

	public boolean isActive() {
		if (drawKeystoneHelper) { return true; }
		if (!getData().isKeystoneDefined()) { return false; }
		return true;
	}

	public void reshape(final int width, final int height) {
		if (fboScene != null) {
			fboScene.setDisplayDimensions(width, height);
		}

	}

}
