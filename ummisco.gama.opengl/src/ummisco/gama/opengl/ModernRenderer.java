/*********************************************************************************************
 *
 * 'ModernRenderer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.nio.IntBuffer;

import javax.vecmath.Matrix4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import ummisco.gama.modernOpenGL.ModernDrawer;
import ummisco.gama.opengl.utils.LightHelper;
import ummisco.gama.opengl.vaoGenerator.DrawingEntityGenerator;
import ummisco.gama.opengl.vaoGenerator.TransformationMatrix;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * This class plays the role of Renderer and IGraphics. Class ModernRenderer.
 *
 * @author mazarsju
 * @since 23 avr. 2016
 *
 */
public class ModernRenderer extends Abstract3DRenderer {

	private Matrix4f projectionMatrix;
	private ModernDrawer drawer;
	public boolean renderToTexture = true;
	public boolean colorPicking = false;
	private final IKeystoneState keystone = new KeystoneState();

	public class KeystoneState implements IKeystoneState {
		private boolean drawKeystoneHelper = false;

		protected float[][] coords;

		@Override
		public int cornerSelected(final GamaPoint mouse) {
			for (int cornerId = 0; cornerId < coords.length; cornerId++) {
				final float xCorner = coords[cornerId][0];
				final float yCorner = coords[cornerId][1];
				if (Math.abs(mouse.x - xCorner) < 0.03 && Math.abs(mouse.y - yCorner) < 0.03) { return cornerId; }
			}
			// check if the click has been in the center of the screen (in the
			// intersection between the diagonals)
			final float[] centerPosition = centerScreen(coords);
			if (Math.abs(mouse.x - centerPosition[0]) < 0.03 && Math.abs(mouse.y - centerPosition[1]) < 0.03)
				return 10;
			return -1;
		}

		@Override
		public GamaPoint[] getCoords() {
			return new GamaPoint[] { new GamaPoint(coords[0][0], coords[0][1]),
					new GamaPoint(coords[1][0], coords[1][1]), new GamaPoint(coords[2][0], coords[2][1]),
					new GamaPoint(coords[3][0], coords[3][1]) };
		}

		@Override
		public void setUpCoords() {
			coords = new float[4][2];
			int i = 0;
			for (final GamaPoint p : data.getKeystone()) {
				setKeystoneCoordinates(i++, p);
			}
		}

		@Override
		public void setCornerSelected(final int cornerId) {
			cornerSelected = cornerId;
		}

		@Override
		public void setKeystoneCoordinates(final int cornerId, final GamaPoint p) {
			coords[cornerId] = new float[] { (float) p.x, (float) p.y };
		}

		protected int cornerSelected = -1;

		@Override
		public boolean drawKeystoneHelper() {
			return drawKeystoneHelper;
		}

		@Override
		public int getCornerSelected() {
			return cornerSelected;
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

		private float[] centerScreen(final float[][] cornerCoords) {
			final float p0_x = cornerCoords[0][0];
			final float p0_y = cornerCoords[0][1];
			final float p1_x = cornerCoords[2][0];
			final float p1_y = cornerCoords[2][1];
			final float p2_x = cornerCoords[1][0];
			final float p2_y = cornerCoords[1][1];
			final float p3_x = cornerCoords[3][0];
			final float p3_y = cornerCoords[3][1];
			float s1_x, s1_y, s2_x, s2_y;
			s1_x = p1_x - p0_x;
			s1_y = p1_y - p0_y;
			s2_x = p3_x - p2_x;
			s2_y = p3_y - p2_y;

			float t;
			t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

			final float[] result = new float[2];
			result[0] = p0_x + t * s1_x;
			result[1] = p0_y + t * s1_y;
			return result;
		}

		@Override
		public boolean isKeystoneInAction() {
			return true; // always true in this implementation
		}

		@Override
		public void setCornerHovered(final int c) {}

		@Override
		public int cornerHovered(final GamaPoint mouse) {
			return -1;
		}

		@Override
		public void resetCorner(final int corner) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public IKeystoneState getKeystone() {
		return keystone;
	}

	protected final IntBuffer selectBuffer = Buffers.newDirectIntBuffer(1024);

	@Override
	public boolean useShader() {
		return true;
	}

	@Override
	public boolean preloadTextures() {
		return false;
	}

	@Override
	public void init(final GLAutoDrawable drawable) {

		WorkbenchHelper.run(() -> getCanvas().setVisible(visible));
		// the drawingEntityGenerator is used only when there is a webgl display
		// and/or a modernRenderer.
		drawingEntityGenerator = new DrawingEntityGenerator(this);
		lightHelper = new LightHelper(this);
		gl = drawable.getGL().getGL2();
		openGL.setGL2(gl);
		// openGL.setGL2(gl);
		final Color background = data.getBackgroundColor();
		gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f, background.getBlue() / 255.0f,
				1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		isNonPowerOf2TexturesAvailable = gl.isNPOTTextureAvailable();

		initializeCanvasListeners();
		updateCameraPosition();
		updatePerspective();

		setUpKeystoneCoordinates();
		drawer = new ModernDrawer(this, gl);
		lightHelper.initializeLighting(openGL);
		// We mark the renderer as inited
		inited = true;
	}

	@Override
	public void display(final GLAutoDrawable drawable) {

		currentScene = sceneBuffer.getSceneToRender();
		if (currentScene == null) { return; }
		gl = drawable.getGL().getGL2();

		drawer.prepareFrameBufferObject();

		final Color background = data.getBackgroundColor();
		gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f, background.getBlue() / 255.0f,
				1.0f);
		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do

		updateCameraPosition();
		updatePerspective();

		rotateModel();
		drawScene();
		gl.glDisable(GL.GL_DEPTH_TEST); // disables depth testing
		drawer.renderToTexture();

		if (!visible) {
			// We make the canvas visible only after a first display has occured
			visible = true;
			WorkbenchHelper.run(() -> getCanvas().setVisible(true));

		}

	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width,
			final int height) {
		// Get the OpenGL graphics context
		if (width <= 0 || height <= 0) { return; }
		updatePerspective();
	}

	@Override
	protected final void updatePerspective() {
		final int height = getDrawable().getSurfaceHeight();
		final int width = getDrawable().getSurfaceWidth();
		final double maxDim = getMaxEnvDim();
		final double fov = data.getCameralens();

		projectionMatrix = TransformationMatrix.createProjectionMatrix(data.isOrtho(), height, width, maxDim, fov);

		camera.animate();
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void beginPicking() {
		// TODO
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and return its index
	 * 
	 * @return name of selected object
	 */
	@Override
	public void endPicking() {
		// TODO
	}

	@Override
	public void dispose(final GLAutoDrawable drawable) {
		// TODO
		sceneBuffer.garbageCollect(openGL);
		sceneBuffer.dispose();
		openGL.dispose();
		drawer.cleanUp();

		this.canvas = null;
		this.camera = null;
		this.currentLayer = null;
		// this.setCurrentPickedObject(null);
		this.currentScene = null;
		drawable.removeGLEventListener(this);
	}

	// Use when the rotation button is on.
	public void rotateModel() {
		if (data.isRotationOn()) {
			data.incrementZRotation();
		}
	}

	public ModernDrawer getDrawer() {
		return drawer;
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public Rectangle2D drawFile(final GamaFile file, final FileDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) { return null; }
		if (attributes.getSize() == null) {
			attributes.setSize(Scaling3D.of(worldDimensions));
		}
		if (file instanceof GamaImageFile)
			sceneBuffer.getSceneToUpdate().addImageFile((GamaImageFile) file, attributes);
		else if (file instanceof GamaGeometryFile) {
			sceneBuffer.getSceneToUpdate().addGeometryFile((GamaGeometryFile) file, attributes);
		}
		return rect;
	}

	@Override
	public Rectangle2D drawField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		// TODO
		return null;
	}

	@Override
	public GamaPoint getRealWorldPointFromWindowPoint(final Point windowPoint) {
		// TODO
		return null;
	}

	/**
	 * Method beginOverlay()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#beginOverlay(msi.gama.outputs.layers.OverlayLayer)
	 */
	@Override
	public void beginOverlay(final OverlayLayer layer) {
		// TODO
	}

	/**
	 * Method endOverlay()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#endOverlay()
	 */
	@Override
	public void endOverlay() {
		// TODO
	}

	@Override
	public boolean mouseInROI(final Point mousePosition) {
		// TODO
		return false;
	}

	@Override
	public boolean cannotDraw() {
		return sceneBuffer.getSceneToUpdate() != null && sceneBuffer.getSceneToUpdate().cannotAdd();
	}

}
