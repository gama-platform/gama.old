/*********************************************************************************************
 *
 * 'JOGLRenderer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
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
import java.io.File;
import java.nio.BufferOverflowException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.FieldDrawer;
import ummisco.gama.opengl.scene.GeometryDrawer;
import ummisco.gama.opengl.scene.ModelScene;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.opengl.scene.ResourceObject;
import ummisco.gama.opengl.scene.StringDrawer;
import ummisco.gama.opengl.utils.LightHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * This class plays the role of Renderer and IGraphics. Class JOGLRenderer.
 *
 * @author drogoul
 * @since 27 avr. 2015
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class JOGLRenderer extends Abstract3DRenderer {
	final int[] viewport = new int[4];
	final double mvmatrix[] = new double[16];
	final double projmatrix[] = new double[16];
	private GeometryDrawer geometryDrawer;
	private StringDrawer stringDrawer;
	private FieldDrawer fieldDrawer;

	private KeystoneDrawer keystone;
	private boolean renderToTexture;

	@Override
	public void setDisplaySurface(final IDisplaySurface d) {
		super.setDisplaySurface(d);
		keystone = new KeystoneDrawer(this);
		geometryDrawer = new GeometryDrawer(this);
		fieldDrawer = new FieldDrawer(this);
		stringDrawer = new StringDrawer(this);
	}

	@Override
	public IKeystoneState getKeystone() {
		return keystone;
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
		WorkbenchHelper.run(() -> getCanvas().setVisible(visible));
		lightHelper = new LightHelper(this);
		gl = drawable.getGL().getGL2();
		openGL.setGL2(gl);
		keystone.setGLHelper(openGL);
		final Color bg = data.getBackgroundColor();
		gl.glClearColor(bg.getRed() / 255.0f, bg.getGreen() / 255.0f, bg.getBlue() / 255.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		isNonPowerOf2TexturesAvailable = gl.isNPOTTextureAvailable();

		initializeCanvasListeners();
		updateCameraPosition();
		updatePerspective();

		// Putting the swap interval to 0 (instead of 1) seems to cure some of
		// the problems of resizing of views.
		gl.setSwapInterval(0);

		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		// Enabling the depth buffer & the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do
		// Whether face culling is enabled or not
		if (GamaPreferences.OpenGL.ONLY_VISIBLE_FACES.getValue()) {
			gl.glEnable(GL.GL_CULL_FACE);
			gl.glCullFace(GL.GL_BACK);
		}
		// Turn on clockwise direction of vertices as an indication of "front" (important)f
		gl.glFrontFace(GL.GL_CW);

		lightHelper.initializeLighting(openGL);

		// Perspective correction
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		// Enable texture 2D
		gl.glEnable(GL.GL_TEXTURE_2D);
		// Blending & alpha control
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		gl.glEnable(GL2.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL2.GL_GREATER, 0.01f);
		// Disabling line smoothing to only rely on FSAA
		gl.glDisable(GL.GL_LINE_SMOOTH);
		// Enabling forced normalization of normal vectors (important)
		gl.glEnable(GL2.GL_NORMALIZE);
		// Enabling multi-sampling (necessary ?)
		// if (USE_MULTI_SAMPLE) {
		gl.glEnable(GL2.GL_MULTISAMPLE);
		gl.glHint(GL2.GL_MULTISAMPLE_FILTER_HINT_NV, GL2.GL_NICEST);
		// }
		openGL.initializeShapeCache();
		setUpKeystoneCoordinates();
		// We mark the renderer as inited
		inited = true;

	}

	@Override
	public void display(final GLAutoDrawable drawable) {
		// Re-set the GL context in case it has changed
		gl = drawable.getGL().getGL2();
		openGL.setGL2(gl);
		//
		openGL.setAntiAlias(data.isAntialias());
		openGL.setWireframe(data.isWireframe());
		//
		currentScene = sceneBuffer.getSceneToRender();
		if (currentScene == null) { return; }
		// We preload any geometry, textures, etc. that are used in layers
		openGL.processUnloadedTextures();
		openGL.processUnloadedGeometries();
		//

		if (keystone.isKeystoneInAction())
			keystone.beginRenderToTexture();
		openGL.beginScene(data.getBackgroundColor());
		openGL.resetMatrix(GL2.GL_PROJECTION);

		updateCameraPosition();
		updatePerspective();

		openGL.resetMatrix(GL2.GL_MODELVIEW);

		if (data.isLightOn()) {
			openGL.pushMatrix();
			openGL.enableLighting();
			lightHelper.setAmbiantLight(openGL, data.getAmbientLightColor());
			lightHelper.updateDiffuseLightValue(openGL);
			openGL.popMatrix();
		} else {
			openGL.disableLighting();
		}

		rotateModel();

		drawScene();

		openGL.disableTextures();
		openGL.disableLighting();
		if (data.isShowfps()) {
			final int fps = (int) canvas.getAnimator().getLastFPS();
			openGL.setCurrentColor(Color.black);
			final String s = fps == 0 ? "(computing FPS...)" : fps + " FPS";
			openGL.rasterText(s, GLUT.BITMAP_HELVETICA_12, -5, 5, 0);
		}

		if (ROIEnvelope != null) {
			geometryDrawer.drawROIHelper(ROIEnvelope);
		}
		if (drawRotationHelper) {
			geometryDrawer.drawRotationHelper(rotationHelperPosition,
					camera.getPosition().distance3D(rotationHelperPosition));
		}
		//
		if (keystone.isKeystoneInAction()) {
			keystone.finishRenderToTexture();
		}

		gl.glFlush();
		if (!visible) {
			// We make the canvas visible only after a first display has occured
			visible = true;
			WorkbenchHelper.asyncRun(() -> getCanvas().setVisible(true));

		}

	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width,
			final int height) {
		if (width <= 0 || height <= 0) { return; }
		gl = drawable.getContext().getGL().getGL2();
		gl.glViewport(0, 0, width, height);
		openGL.setViewWidth(width);
		openGL.setViewHeight(height);
		openGL.resetMatrix(GL2.GL_MODELVIEW);
		openGL.resetMatrix(GL2.GL_PROJECTION);
		updatePerspective();
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
		keystone.reshape(width, height);
	}

	@Override
	protected final void updatePerspective() {
		final int height = getDrawable().getSurfaceHeight();
		final double aspect = (double) getDrawable().getSurfaceWidth() / (double) (height == 0 ? 1 : height);
		final double maxDim = getMaxEnvDim();
		if (!data.isOrtho()) {
			try {
				final double zNear = maxDim / 100;
				double fW, fH;
				final double fovY = this.data.getCameralens();
				if (aspect > 1.0) {
					fH = Math.tan(fovY / 360 * Math.PI) * zNear;
					fW = fH * aspect;
				} else {
					fW = Math.tan(fovY / 360 * Math.PI) * zNear;
					fH = fW / aspect;
				}
				gl.glFrustum(-fW, fW, -fH, fH, zNear, maxDim * 100);
			} catch (final BufferOverflowException e) {
				System.out.println("Buffer overflow exception");
			}
		} else {
			if (aspect >= 1.0) {
				gl.glOrtho(-maxDim * aspect, maxDim * aspect, -maxDim, maxDim, maxDim * 10, -maxDim * 10);
			} else {
				gl.glOrtho(-maxDim, maxDim, -maxDim / aspect, maxDim / aspect, maxDim, -maxDim);
			}
			gl.glTranslated(0d, 0d, maxDim * 0.05);
		}
		camera.animate();
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * First pass prepare select buffer for select mode by clearing it, prepare openGL to select mode and tell it where
	 * should draw object by using gluPickMatrix() method
	 * 
	 * @return if returned value is true that mean the picking is enabled
	 */
	@Override
	public void beginPicking() {
		final GLU glu = GLU.createGLU();
		// 1. Selecting buffer
		selectBuffer.clear(); // prepare buffer for new objects
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);
		// Pass below is very similar to refresh method in GLrenderer
		// 2. Take the viewport attributes,
		final int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		// 3. Prepare openGL for rendering in select mode
		gl.glRenderMode(GL2.GL_SELECT);
		/*
		 * The application must redefine the viewing volume so that it renders only a small area around the place where
		 * the mouse was clicked. In order to do that it is necessary to set the matrix mode to GL_PROJECTION.
		 * Afterwards, the application should push the current matrix to save the normal rendering mode settings. Next
		 * initialise the matrix
		 */
		openGL.matrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		/*
		 * Define the viewing volume so that rendering is done only in a small area around the cursor. gluPickMatrix
		 * method restrict the area where openGL will drawing objects
		 *
		 */
		glu.gluPickMatrix(camera.getMousePosition().x, viewport[3] - camera.getMousePosition().y, 4, 4, viewport, 0);
		// FIXME Why do we have to call updatePerspective() here ?
		updatePerspective();
		openGL.matrixMode(GL2.GL_MODELVIEW);
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and return its index
	 * 
	 * @return name of selected object
	 */
	@Override
	public void endPicking() {
		int selectedIndex = PickingState.NONE;
		// 5. When you back to Render mode gl.glRenderMode() methods return
		// number of hits
		final int howManyObjects = gl.glRenderMode(GL2.GL_RENDER);
		// 6. Restore to normal settings
		openGL.matrixMode(GL2.GL_PROJECTION);
		openGL.popMatrix();
		openGL.matrixMode(GL2.GL_MODELVIEW);
		// 7. Seach the select buffer to find the nearest object
		// code below derive which objects is nearest from monitor
		//
		if (howManyObjects > 0) {
			// simple searching algorithm
			selectedIndex = selectBuffer.get(3);
			int mindistance = Math.abs(selectBuffer.get(1));
			for (int i = 0; i < howManyObjects; i++) {
				if (mindistance < Math.abs(selectBuffer.get(1 + i * 4))) {
					mindistance = Math.abs(selectBuffer.get(1 + i * 4));
					selectedIndex = selectBuffer.get(3 + i * 4);
				}
			}
			// end of searching
		} else {
			selectedIndex = PickingState.WORLD;// return -1 as there was no hits
		}
		pickingState.setPickedIndex(selectedIndex);
	}

	@Override
	public void dispose(final GLAutoDrawable drawable) {
		sceneBuffer.garbageCollect(openGL);
		sceneBuffer.dispose();
		openGL.dispose();
		this.canvas = null;
		this.camera = null;
		this.currentLayer = null;
		this.currentScene = null;
		keystone.dispose();
		drawable.removeGLEventListener(this);
	}

	// Use when the rotation button is on.
	public void rotateModel() {
		if (data.isRotationOn() && !data.cameraInteractionDisabled()) {
			data.incrementZRotation();
		}
		if (getCurrentZRotation() != 0d) {
			final double env_width = worldDimensions.x;
			final double env_height = worldDimensions.y;
			openGL.translateBy(env_width / 2, -env_height / 2, 0d);
			openGL.rotateBy(getCurrentZRotation(), 0, 0, 1);
			openGL.translateBy(-env_width / 2, +env_height / 2, 0d);
		}
	}

	@Override
	public Rectangle2D drawFile(final GamaFile file, final FileDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) { return null; }
		tryToHighlight(attributes);
		final File f = file.getFile(getSurface().getScope());
		if (file instanceof GamaGeometryFile) {
			final GamaGeometryFile ggf = (GamaGeometryFile) file;
			final String path = f.getAbsolutePath();
			final ResourceObject object =
					sceneBuffer.getSceneToUpdate().addGeometryFile((GamaGeometryFile) file, attributes);
			if (object != null)
				openGL.cacheGeometry(object);
		} else if (file instanceof GamaImageFile) {
			if (attributes.useCache())
				openGL.cacheTexture(file.getFile(getSurface().getScope()));
			sceneBuffer.getSceneToUpdate().addImageFile((GamaImageFile) file, attributes);
		}

		return rect;
	}

	@Override
	public Rectangle2D drawField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) { return null; }
		preloadTextures(attributes);
		sceneBuffer.getSceneToUpdate().addField(fieldValues, attributes);
		/*
		 * This line has been removed to fix the issue 1174 if ( gridColor != null ) { drawGridLine(img, gridColor); }
		 */
		return rect;
	}

	@Override
	public GamaPoint getRealWorldPointFromWindowPoint(final Point windowPoint) {
		int realy = 0;// GL y coord pos
		final double[] wcoord = new double[4];
		final int x = (int) windowPoint.getX(), y = (int) windowPoint.getY();
		final GLU glu = GLU.createGLU(gl);
		realy = viewport[3] - y;
		glu.gluUnProject(x, realy, 0.1, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		final GamaPoint v1 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);
		glu.gluUnProject(x, realy, 0.9, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		final GamaPoint v2 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);
		final GamaPoint v3 = v2.minus(v1).normalized();
		final float distance =
				(float) (camera.getPosition().getZ() / GamaPoint.dotProduct(new GamaPoint(0.0, 0.0, -1.0), v3));
		final GamaPoint worldCoordinates = camera.getPosition().plus(v3.times(distance));

		return new GamaPoint(worldCoordinates.x, worldCoordinates.y);
	}

	public double[] getPixelWidthAndHeightOfWorld() {
		final GLU glu = GLU.createGLU(gl);
		final double[] coord = new double[4];
		glu.gluProject(getEnvWidth(), 0, 0, mvmatrix, 0, projmatrix, 0, viewport, 0, coord, 0);
		return coord;
	}

	/**
	 * Method beginOverlay()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#beginOverlay(msi.gama.outputs.layers.OverlayLayer)
	 */
	@Override
	public void beginOverlay(final OverlayLayer layer) {
		final ModelScene scene = sceneBuffer.getSceneToUpdate();
		if (scene != null) {
			scene.beginOverlay();
		}

	}

	/**
	 * Method endOverlay()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#endOverlay()
	 */
	@Override
	public void endOverlay() {

	}

	@Override
	public boolean mouseInROI(final Point mousePosition) {
		final Envelope3D env = getROIEnvelope();
		if (env == null)
			return false;
		final GamaPoint p = getRealWorldPointFromWindowPoint(mousePosition);
		return env.contains(p);
	}

	@Override
	public boolean cannotDraw() {
		return canvas == null || sceneBuffer.getSceneToUpdate() != null && sceneBuffer.getSceneToUpdate().cannotAdd();
	}

	@Override
	public ObjectDrawer getDrawerFor(final AbstractObject.DrawerType type) {
		switch (type) {
			case STRING:
				return stringDrawer;
			case GEOMETRY:
				return geometryDrawer;
			case FIELD:
				return fieldDrawer;
		}
		return null;
	}

	public GeometryDrawer getGeometryDrawer() {
		return geometryDrawer;
	}

}
