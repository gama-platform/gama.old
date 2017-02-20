package ummisco.gama.opengl;

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
import ummisco.gama.modernOpenGL.FrameBufferObject;
import ummisco.gama.modernOpenGL.shader.AbstractShader;
import ummisco.gama.modernOpenGL.shader.postprocessing.AbstractPostprocessingShader;
import ummisco.gama.modernOpenGL.shader.postprocessing.KeystoneShaderProgram;
import ummisco.gama.opengl.scene.OpenGL;

public class KeystoneDrawer implements IKeystoneState {

	private FrameBufferObject fboScene;
	private final JOGLRenderer renderer;
	private GL2 gl;
	private OpenGL openGL;
	protected boolean drawKeystoneHelper = false;
	protected int cornerSelected = -1, cornerHovered = -1;
	private int uvMappingBufferIndex;
	private int verticesBufferIndex;
	private int indexBufferIndex;
	private KeystoneShaderProgram shader;

	public KeystoneDrawer(final JOGLRenderer r) {
		this.renderer = r;
	}

	public void setGLHelper(final OpenGL openGL) {
		this.openGL = openGL;
		this.gl = openGL.getGL();
	}

	@Override
	public int getCornerSelected() {
		return cornerSelected;
	}

	@Override
	public GamaPoint[] getCoords() {
		return renderer.data.getKeystone().toCoordinateArray();
	}

	@Override
	public boolean drawKeystoneHelper() {
		return drawKeystoneHelper;
	}

	@Override
	public void startDrawHelper() {
		drawKeystoneHelper = true;
		cornerSelected = -1;
	}

	@Override
	public void stopDrawHelper() {
		drawKeystoneHelper = false;
	}

	public void dispose() {
		if (fboScene != null) {
			fboScene.cleanUp();
		}
		gl.glDeleteBuffers(3, new int[] { indexBufferIndex, verticesBufferIndex, uvMappingBufferIndex }, 0);
		// glu.gluDeleteQuadric(q);
	}

	public void beginRenderToTexture() {
		gl.glClearColor(0, 0, 0, 1.0f);
		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		if (fboScene == null) {
			fboScene = new FrameBufferObject(gl, renderer.getDisplayWidth(), renderer.getDisplayHeight());
		}
		// redirect the rendering to the fbo_scene (will be rendered later, as a texture)
		fboScene.bindFrameBuffer();

	}

	private void drawSquare(final double[] loc, final double side, final Color fill) {
		drawRectangle(loc, side, side, fill);
	}

	private void drawRectangle(final double[] loc, final double width, final double height, final Color fill) {
		openGL.pushMatrix();
		openGL.translateBy(loc);
		openGL.setCurrentColor(fill);
		openGL.scaleBy(Scaling3D.of(width, height, 1));
		openGL.drawCachedGeometry(IShape.Type.SQUARE, null);
		openGL.popMatrix();
	}

	private static final Color[] FILL_COLORS =
			new Color[] { NamedGamaColor.getNamed("gamared"), NamedGamaColor.getNamed("gamablue"), Color.black };

	private void drawKeystoneMarks() {
		openGL.pushIdentity(GL2.GL_PROJECTION);
		gl.glOrtho(0, 1, 0, 1, 1, -1);
		openGL.disableLighting();
		LayeredDisplayData.KEYSTONE_IDENTITY.visit((id, x, y, z) -> {
			final double wPxSize = 1d / renderer.getWidth();
			final double hPxSize = 1d / renderer.getHeight();
			final String content = "{" + floor4Digit(getCoords()[id].x) + "," + floor4Digit(getCoords()[id].y) + "}";

			final double width = wPxSize * (openGL.getGlut().glutBitmapLength(GLUT.BITMAP_HELVETICA_18, content) + 20);
			final double height = hPxSize * (18 + 20);

			openGL.pushIdentity(GL2.GL_MODELVIEW);
			final int fill = id == cornerSelected ? 0 : id == cornerHovered ? 1 : 2;
			drawRectangle(new double[] { x, y, z }, width * 2, height * 2, FILL_COLORS[fill]);
			gl.glColor3d(1, 1, 1);
			openGL.getGL().glLoadIdentity();
			final double xPos = x == 0 ? 10 * wPxSize : 1 - width + 10 * wPxSize;
			final double yPos = y == 0 ? 12 * hPxSize : 1 - height + 12 * hPxSize;

			openGL.getGL().glRasterPos2d(xPos, yPos);
			openGL.getGlut().glutBitmapString(GLUT.BITMAP_HELVETICA_18, content);
			openGL.pop(GL2.GL_MODELVIEW);
		}, 4, true);

		openGL.pop(GL2.GL_MODELVIEW);
		openGL.enableLighting();
		openGL.pop(GL2.GL_PROJECTION);

	}

	private double floor4Digit(double number) {
		number *= 1000;
		number = Math.round(number);
		number /= 1000;
		return number;
	}

	public void finishRenderToTexture() {
		if (drawKeystoneHelper) {
			drawKeystoneMarks();
		}
		// gl.glDisable(GL2.GL_DEPTH_TEST); // disables depth testing
		final AbstractPostprocessingShader shader = getShader();
		// unbind the last fbo
		fboScene.unbindCurrentFrameBuffer();
		// prepare shader
		shader.start();
		prepareShader(shader);
		// build the surface
		createScreenSurface();
		// draw
		gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_INT, 0);
		shader.stop();
	}

	public KeystoneShaderProgram getShader() {
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
		// Keystoning computation (cf
		// http://www.bitlush.com/posts/arbitrary-quadrilaterals-in-opengl-es-2-0)
		// transform the coordinates [0,1] --> [-1,+1]
		final ICoordinates coords = renderer.data.getKeystone();
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

		// INDEX BUFFER
		final int[] intIdxBuffer = new int[] { 0, 1, 2, 0, 2, 3 };
		final IntBuffer ibIdxBuff = Buffers.newDirectIntBuffer(intIdxBuffer);
		// Select the VBO, GPU memory data, to use for colors
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBufferIndex);
		final int numBytes = intIdxBuffer.length * 4;
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, numBytes, ibIdxBuff, GL2.GL_STATIC_DRAW);
		ibIdxBuff.rewind();
	}

	private void storeAttributes(final int shaderAttributeType, final int bufferIndex, final int size,
			final float[] data) {
		bindBuffer(shaderAttributeType, bufferIndex, size);
		// compute the total size of the buffer :
		final int numBytes = data.length * 4;
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numBytes, null, GL2.GL_STATIC_DRAW);
		final FloatBuffer fbData = Buffers.newDirectFloatBuffer(data/* totalData,positionInBuffer */);
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, data.length * 4, fbData);
		gl.glEnableVertexAttribArray(shaderAttributeType);
	}

	private void bindBuffer(final int shaderAttributeType, final int bufferIndex, final int size) {
		// Select the VBO, GPU memory data, to use for data
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferIndex);
		// Associate Vertex attribute with the last bound VBO
		gl.glVertexAttribPointer(shaderAttributeType, size, GL2.GL_FLOAT, false, 0, 0 /* offset */);
	}

	@Override
	public void setUpCoords() {}

	@Override
	public void setCornerSelected(final int cornerId) {
		cornerSelected = cornerId;
	}

	@Override
	public void resetCorner(final int cornerId) {
		setKeystoneCoordinates(cornerId, LayeredDisplayData.KEYSTONE_IDENTITY.at(cornerId));
		cornerSelected = -1;
		cornerHovered = -1;
	}

	@Override
	public int cornerSelected(final GamaPoint mouse) {
		final ICoordinates coords = renderer.data.getKeystone();
		for (int cornerId = 0; cornerId < coords.size(); cornerId++) {
			if (mouse.distance(coords.at(cornerId)) < 0.04)
				return cornerId;
		}
		return -1;
	}

	@Override
	public int cornerHovered(final GamaPoint mouse) {
		final ICoordinates coords = renderer.data.getKeystone();
		for (int cornerId = 0; cornerId < coords.size(); cornerId++) {
			if (mouse.distance(coords.at(cornerId)) < 0.03)
				return cornerId;
		}
		return -1;
	}

	@Override
	public void setCornerHovered(final int cornerId) {
		cornerHovered = cornerId;
	}

	@Override
	public void setKeystoneCoordinates(final int cornerId, final GamaPoint p) {
		renderer.data.getKeystone().replaceWith(cornerId, p.x, p.y, p.z);
		renderer.data.setKeystone(renderer.data.getKeystone());
	}

	@Override
	public boolean isKeystoneInAction() {
		if (drawKeystoneHelper)
			return true;
		if (!renderer.data.isKeystoneDefined())
			return false;
		return true;
	}

	public void reshape(final int width, final int height) {
		if (fboScene != null) {
			fboScene.setDisplayDimensions(width, height);
		}

	}

}
