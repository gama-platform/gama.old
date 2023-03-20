/*******************************************************************************************************
 *
 * KeystoneHelper.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.helpers;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
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
import ummisco.gama.ui.utils.DPIHelper;

/**
 * The Class KeystoneHelper.
 */
public class KeystoneHelper extends AbstractRendererHelper {

	/** The finishing helper. */
	private final Pass finishingHelper = this::finishRenderToTexture;

	/** The fbo scene. */
	private FrameBufferObject fboScene;

	/** The draw keystone helper. */
	protected boolean drawKeystoneHelper = false;

	/** The corner hovered. */
	protected int cornerSelected = -1, cornerHovered = -1;

	/** The uv mapping buffer index. */
	private int uvMappingBufferIndex;

	/** The vertices buffer index. */
	private int verticesBufferIndex;

	/** The index buffer index. */
	private int indexBufferIndex;

	/** The shader. */
	private KeystoneShaderProgram shader;

	/** The world corners. */
	private boolean worldCorners = false;

	/** The Constant FILL_COLORS. */
	private static final Color[] FILL_COLORS = { NamedGamaColor.getNamed("gamared").withAlpha(0.3),
			NamedGamaColor.getNamed("gamablue").withAlpha(0.3), NamedGamaColor.getNamed("black").withAlpha(0.3) };

	/** The ib idx buff. */
	final IntBuffer ibIdxBuff = Buffers.newDirectIntBuffer(new int[] { 0, 1, 2, 0, 2, 3 });

	/**
	 * Instantiates a new keystone helper.
	 *
	 * @param r
	 *            the r
	 */
	public KeystoneHelper(final IOpenGLRenderer r) {
		super(r);
	}

	/**
	 * Gets the view width.
	 *
	 * @return the view width
	 */
	int getViewWidth() {
		return getRenderer().getViewWidth();
		// return PlatformHelper.scaleDownIfMac(getRenderer().getViewWidth());
	}

	/**
	 * Gets the view height.
	 *
	 * @return the view height
	 */
	int getViewHeight() {
		return getRenderer().getViewHeight();
		// return PlatformHelper.scaleDownIfMac(getRenderer().getViewHeight());
	}

	@Override
	public void initialize() {

	}

	/**
	 * Gets the corner selected.
	 *
	 * @return the corner selected
	 */
	public int getCornerSelected() { return cornerSelected; }

	/**
	 * Gets the coords.
	 *
	 * @return the coords
	 */
	public GamaPoint[] getCoords() { return getData().getKeystone().toCoordinateArray(); }

	/**
	 * Gets the keystone coordinates.
	 *
	 * @param corner
	 *            the corner
	 * @return the keystone coordinates
	 */
	public GamaPoint getKeystoneCoordinates(final int corner) {
		return getCoords()[corner];
	}

	/**
	 * Start draw helper.
	 */
	public void startDrawHelper() {
		drawKeystoneHelper = true;
		cornerSelected = -1;
	}

	/**
	 * Stop draw helper.
	 */
	public void stopDrawHelper() {
		drawKeystoneHelper = false;
	}

	/**
	 * Switch corners.
	 */
	public void switchCorners() {
		getData();
		worldCorners =
				!LayeredDisplayData.KEYSTONE_IDENTITY.getEnvelope().covers(getData().getKeystone().getEnvelope());
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		final GL2 gl = getGL();
		if (fboScene != null) { fboScene.cleanUp(); }
		if (gl != null) {
			gl.glDeleteBuffers(3, new int[] { indexBufferIndex, verticesBufferIndex, uvMappingBufferIndex }, 0);
		}
	}

	/**
	 * Begin render to texture.
	 */
	@SuppressWarnings ("restriction")
	public void beginRenderToTexture() {
		final GL2 gl = getGL();
		gl.glClearColor(0, 0, 0, 1.0f);
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if (fboScene == null) { fboScene = new FrameBufferObject(gl, getViewWidth(), getViewHeight()); }
		// redirect the rendering to the fbo_scene (will be rendered later, as a texture)
		fboScene.bindFrameBuffer();

	}

	/**
	 * Draw rectangle.
	 *
	 * @param openGL
	 *            the open GL
	 * @param centerX
	 *            the center X
	 * @param centerY
	 *            the center Y
	 * @param centerZ
	 *            the center Z
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param fill
	 *            the fill
	 */
	private void drawRectangle(final OpenGL openGL, final double centerX, final double centerY, final double centerZ,
			final double width, final double height, final Color fill) {
		openGL.pushMatrix();
		openGL.translateBy(centerX, centerY, centerY);
		openGL.setCurrentColor(fill);
		openGL.scaleBy(Scaling3D.of(width, height, 1));
		openGL.drawCachedGeometry(IShape.Type.SQUARE, null);
		openGL.popMatrix();
	}

	/**
	 * Draw keystone marks.
	 */
	private void drawKeystoneMarks() {
		final OpenGL openGL = getOpenGL();
		final GL2 gl = getGL();

		//
		final int displayWidthInPixels = getViewWidth();
		final int displayHeightInPixels = getViewHeight();
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

		openGL.pushIdentity(GLMatrixFunc.GL_PROJECTION);
		gl.glOrtho(0, 1, 0, 1, 1, -1);
		boolean previous = openGL.setObjectLighting(false);
		openGL.push(GLMatrixFunc.GL_MODELVIEW);
		vertices.visit((id, x, y, z) -> {
			// Basic computations on text and color
			final String text = floor4Digit(getCoords()[id].x) + "," + floor4Digit(getCoords()[id].y);
			final int lengthOfTextInPixels = openGL.getGlut().glutBitmapLength(GLUT.BITMAP_HELVETICA_18, text);
			final double labelWidthIn01 = pixelWidthIn01 * (lengthOfTextInPixels + 20);
			final int fill = id == cornerSelected ? 0 : id == cornerHovered ? 1 : 2;
			// Drawing the background of labels
			final double xLabelIn01 = x + (id == 0 || id == 1 ? labelWidthIn01 / 2 : -labelWidthIn01 / 2);
			final double yLabelIn01 = y + (id == 0 || id == 3 ? labelHeightIn01 / 2 : -labelHeightIn01 / 2);
			drawRectangle(openGL, xLabelIn01, yLabelIn01, z, labelWidthIn01, labelHeightIn01, FILL_COLORS[fill]);

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
		openGL.pop(GLMatrixFunc.GL_MODELVIEW);
		openGL.setObjectLighting(previous);
		openGL.pop(GLMatrixFunc.GL_PROJECTION);

	}

	/**
	 * Floor 4 digit.
	 *
	 * @param n
	 *            the n
	 * @return the double
	 */
	private double floor4Digit(final double n) {
		double number = n * 1000;
		number = Math.round(number);
		number /= 1000;
		return number;
	}

	/**
	 * Finish render to texture.
	 */
	public void finishRenderToTexture() {
		if (drawKeystoneHelper) { drawKeystoneMarks(); }
		// gl.glDisable(GL2.GL_DEPTH_TEST); // disables depth testing
		final AbstractPostprocessingShader theShader = getShader();
		// unbind the last fbo
		if (fboScene != null) {
			// We verify if it is not null
			fboScene.unbindCurrentFrameBuffer();
			// prepare shader
			theShader.start();
			prepareShader(theShader);
			// build the surface
			createScreenSurface();
			// draw
			final GL2 gl = getGL();
			gl.glDrawElements(GL.GL_TRIANGLES, 6, GL.GL_UNSIGNED_INT, 0);
			theShader.stop();
		}

	}

	/**
	 * Gets the shader.
	 *
	 * @return the shader
	 */
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

	/**
	 * Prepare shader.
	 *
	 * @param shaderProgram
	 *            the shader program
	 */
	private void prepareShader(final AbstractPostprocessingShader shaderProgram) {
		shaderProgram.loadTexture(0);
		shaderProgram.storeTextureID(fboScene.getFBOTexture());
	}

	/**
	 * Creates the screen surface.
	 */
	public void createScreenSurface() {
		final GL2 gl = getGL();
		// Keystoning computation (cf
		// http://www.bitlush.com/posts/arbitrary-quadrilaterals-in-opengl-es-2-0)
		// transform the coordinates [0,1] --> [-1,+1]
		final ICoordinates coords = getData().getKeystone();
		final float[] p0 = { (float) coords.at(0).x * 2f - 1f, (float) (coords.at(0).y * 2f - 1f) }; // bottom-left
		final float[] p1 = { (float) coords.at(1).x * 2f - 1f, (float) coords.at(1).y * 2f - 1f }; // top-left
		final float[] p2 = { (float) coords.at(2).x * 2f - 1f, (float) coords.at(2).y * 2f - 1f }; // top-right
		final float[] p3 = { (float) coords.at(3).x * 2f - 1f, (float) coords.at(3).y * 2f - 1f }; // bottom-right

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
			final float[] listVertices = { p0[0], p0[1], 1f, p1[0], p1[1], 0f, p2[0], p2[1], 0f, p3[0], p3[1], 1f };
			final float[] listUvMapping =
					{ 0f, 1f * q0, 0f, q0, 0f, 0f, 0f, q1, 1f * q2, 0f, 0f, q2, 1f * q3, 1f * q3, 0f, q3 };
			// VERTICES POSITIONS BUFFER
			storeAttributes(AbstractShader.POSITION_ATTRIBUTE_IDX, verticesBufferIndex, 3, listVertices);
			// UV MAPPING (If a texture is defined)
			storeAttributes(AbstractShader.UVMAPPING_ATTRIBUTE_IDX, uvMappingBufferIndex, 4, listUvMapping);

		}

		// gl.glActiveTexture(GL.GL_TEXTURE0);
		// gl.glBindTexture(GL.GL_TEXTURE_2D, fboScene.getFBOTexture());
		getOpenGL().bindTexture(fboScene.getFBOTexture());
		// Select the VBO, GPU memory data, to use for colors
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBufferIndex);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, 24, ibIdxBuff, GL.GL_STATIC_DRAW);
		ibIdxBuff.rewind();
	}

	/**
	 * Store attributes.
	 *
	 * @param shaderAttributeType
	 *            the shader attribute type
	 * @param bufferIndex
	 *            the buffer index
	 * @param size
	 *            the size
	 * @param data
	 *            the data
	 */
	private void storeAttributes(final int shaderAttributeType, final int bufferIndex, final int size,
			final float[] data) {
		final GL2 gl = getGL();
		// Select the VBO, GPU memory data, to use for data
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIndex);
		// Associate Vertex attribute with the last bound VBO
		gl.glVertexAttribPointer(shaderAttributeType, size, GL.GL_FLOAT, false, 0, 0 /* offset */);
		// compute the total size of the buffer :
		final int numBytes = data.length * 4;
		gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, null, GL.GL_STATIC_DRAW);

		final FloatBuffer fbData = Buffers.newDirectFloatBuffer(data);
		gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, numBytes, fbData);
		gl.glEnableVertexAttribArray(shaderAttributeType);
	}

	/**
	 * Sets the corner selected.
	 *
	 * @param cornerId
	 *            the new corner selected
	 */
	public void setCornerSelected(final int cornerId) { cornerSelected = cornerId; }

	/**
	 * Reset corner.
	 *
	 * @param cornerId
	 *            the corner id
	 */
	public void resetCorner(final int cornerId) {
		setKeystoneCoordinates(cornerId, LayeredDisplayData.KEYSTONE_IDENTITY.at(cornerId));
		cornerSelected = -1;
		cornerHovered = -1;
	}

	/**
	 * Corner selected.
	 *
	 * @param mouse
	 *            the mouse
	 * @return the int
	 */
	public int cornerSelected(final GamaPoint mouse) {
		if (mouse.x < getViewWidth() / 2) {
			if (mouse.y < getViewHeight() / 2) return 1;
			return 0;
		}
		if (mouse.y < getViewHeight() / 2) return 2;
		return 3;
	}

	/**
	 * Corner hovered.
	 *
	 * @param mouse
	 *            the mouse
	 * @return the int
	 */
	public int cornerHovered(final GamaPoint mouse) {
		if (mouse.x < getViewWidth() / 2) {
			if (mouse.y < getViewHeight() / 2) return 1;
			return 0;
		}
		if (mouse.y < getViewHeight() / 2) return 2;
		return 3;
	}

	/**
	 * Sets the corner hovered.
	 *
	 * @param cornerId
	 *            the new corner hovered
	 */
	public void setCornerHovered(final int cornerId) { cornerHovered = cornerId; }

	/**
	 * Sets the keystone coordinates.
	 *
	 * @param cornerId
	 *            the corner id
	 * @param p
	 *            the p
	 */
	public void setKeystoneCoordinates(final int cornerId, final GamaPoint p) {
		getData().getKeystone().replaceWith(cornerId, p.x, p.y, p.z);
		switchCorners();
		getData().setKeystone(getData().getKeystone());
	}

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() { return drawKeystoneHelper; }

	/**
	 * Render.
	 *
	 * @return the pass
	 */
	public Pass render() {
		if (drawKeystoneHelper || getData().isKeystoneDefined()) {
			beginRenderToTexture();
			return finishingHelper;
		}
		return null;
	}

	/**
	 * Reshape.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public void reshape(final int width, final int height) {
		if (fboScene != null) {
			fboScene.setDisplayDimensions(DPIHelper.autoScaleUp(renderer.getCanvas().getMonitor(), width),
					DPIHelper.autoScaleUp(renderer.getCanvas().getMonitor(), height));
		}

	}

}
