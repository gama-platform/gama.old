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
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.nio.BufferOverflowException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.shape.Envelope3D;
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
import ummisco.gama.opengl.scene.HelpersDrawer;
import ummisco.gama.opengl.scene.ModelScene;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.opengl.scene.ResourceDrawer;
import ummisco.gama.opengl.scene.StringDrawer;
import ummisco.gama.opengl.utils.FPSDrawer;
import ummisco.gama.opengl.utils.GLUtilLight;
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

	private final FPSDrawer fpsDrawer = new FPSDrawer();
	private Color currentColor;
	private Texture currentTexture;
	private final GeometryDrawer geometryDrawer;
	private final StringDrawer stringDrawer;
	private final FieldDrawer fieldDrawer;
	private final ResourceDrawer resourceDrawer;
	private final HelpersDrawer helpersDrawer;

	public JOGLRenderer(final SWTOpenGLDisplaySurface d) {
		super(d);
		geometryDrawer = new GeometryDrawer(this);
		fieldDrawer = new FieldDrawer(this);
		stringDrawer = new StringDrawer(this);
		resourceDrawer = new ResourceDrawer(this);
		helpersDrawer = new HelpersDrawer(this);
	}

	public GL2 getGL() {
		return gl;
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
		WorkbenchHelper.run(() -> getCanvas().setVisible(visible));

		commonInit(drawable);

		// Putting the swap interval to 0 (instead of 1) seems to cure some of
		// the problems of resizing of views.
		gl.setSwapInterval(0);

		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		// the depth buffer & enable the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		if (GamaPreferences.ONLY_VISIBLE_FACES.getValue()) {
			gl.glEnable(GL.GL_CULL_FACE);
			gl.glCullFace(GL.GL_BACK);
		}
		gl.glFrontFace(GL.GL_CW);
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do
		GLUtilLight.InitializeLighting(gl, data, false);

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
		gl.glDisable(GL.GL_LINE_SMOOTH);

		// We mark the renderer as inited
		inited = true;

	}

	@Override
	public Integer getGeometryListFor(final GL2 gl, final GamaGeometryFile file) {
		return geometryCache.get(gl, this, file);
	}

	@Override
	public TextRenderer getTextRendererFor(final Font font) {
		final TextRenderer r = textRendererCache.get(font);
		if (getCurrentColor() != null && r != null)
			r.setColor(getCurrentColor());
		return r;
	}

	@Override
	public void display(final GLAutoDrawable drawable) {

		currentScene = sceneBuffer.getSceneToRender();
		if (currentScene == null) { return; }
		// We preload any geometry, textures, etc. that are used in layers
		gl = drawable.getGL().getGL2();
		textureCache.processUnloadedTextures(gl);
		geometryCache.processUnloadedGeometries(gl, this);

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projmatrix, 0);
		gl.glClearDepth(1.0f);
		final Color background = data.getBackgroundColor();
		gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f, background.getBlue() / 255.0f,
				1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();

		updateCameraPosition();
		updatePerspective();

		if (data.isLightOn()) {
			gl.glEnable(GL2.GL_LIGHTING);
			GLUtilLight.SetAmbiantLight(gl, data.getAmbientLightColor());
			GLUtilLight.UpdateDiffuseLightValue(gl, this);
		} else {
			gl.glDisable(GL2.GL_LIGHTING);
		}

		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, !data.isTriangulation() ? GL2GL3.GL_FILL : GL2GL3.GL_LINE);

		if (data.isRotationOn())
			rotateModel(gl);

		drawScene(gl);

		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_TEXTURE_2D);

		if (data.isShowfps())
			fpsDrawer.draw(gl, this);

		if (ROIEnvelope != null) {
			helpersDrawer.drawROIHelper(gl, ROIEnvelope);
		}
		if (drawRotationHelper) {
			helpersDrawer.drawRotationHelper(gl, rotationHelperPosition,
					camera.getPosition().distance3D(rotationHelperPosition));
		}
		if (!visible) {
			// We make the canvas visible only after a first display has occured
			visible = true;
			WorkbenchHelper.asyncRun(() -> getCanvas().setVisible(true));

		}

	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width,
			final int height) {
		// Get the OpenGL graphics context
		if (width <= 0 || height <= 0) { return; }
		gl = drawable.getContext().getGL().getGL2();
		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);
		// Enable the model view - any new transformations will affect the
		// model-view matrix
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
		// perspective view
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		// Only if zoomFit... camera.resetCamera(data.getEnvWidth(),
		// data.getEnvHeight(), data.isOutput3D());
		updatePerspective();
	}

	@Override
	protected final void updatePerspective() {
		final int height = getDrawable().getSurfaceHeight();
		final double aspect = (double) getDrawable().getSurfaceWidth() / (double) (height == 0 ? 1 : height);

		final double maxDim = getMaxEnvDim();

		if (!data.isOrtho()) {
			try {
				final double zNear = maxDim / 1000;
				double fW, fH;
				final double fovY = this.data.getCameralens();
				if (aspect > 1.0) {
					fH = Math.tan(fovY / 360 * Math.PI) * zNear;
					fW = fH * aspect;
				} else {
					fW = Math.tan(fovY / 360 * Math.PI) * zNear;
					fH = fW / aspect;
				}
				gl.glFrustum(-fW, fW, -fH, fH, zNear, maxDim * 10);

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
	public void beginPicking(final GL2 gl) {

		final GLU glu = getGlu();

		// 1. Selecting buffer
		selectBuffer.clear(); // prepare buffer for new objects
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);// add buffer
																	// to openGL

		// Pass below is very similar to refresh method in GLrenderer
		// 2. Take the viewport attributes,
		final int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		// final int width = viewport[2]; // get width and
		// final int height = viewport[3]; // height from viewport

		// 3. Prepare openGL for rendering in select mode
		gl.glRenderMode(GL2.GL_SELECT);

		/*
		 * The application must redefine the viewing volume so that it renders only a small area around the place where
		 * the mouse was clicked. In order to do that it is necessary to set the matrix mode to GL_PROJECTION.
		 * Afterwards, the application should push the current matrix to save the normal rendering mode settings. Next
		 * initialise the matrix
		 */

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		/*
		 * Define the viewing volume so that rendering is done only in a small area around the cursor. gluPickMatrix
		 * method restrict the area where openGL will drawing objects
		 *
		 * OpenGL has a different origin for its window coordinates than the operation system. The second parameter
		 * provides for the conversion between the two systems, i.e. it transforms the origin from the upper left
		 * corner, into the bottom left corner
		 */
		glu.gluPickMatrix(camera.getMousePosition().x, viewport[3] - camera.getMousePosition().y, 4, 4, viewport, 0);

		// FIXME Why do we have to call updatePerspective() here ?
		updatePerspective();
		// Comment GL_MODELVIEW to debug3D picking (redraw the model when
		// clicking)
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// 4. After this pass you must draw Objects

	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and return its index
	 * 
	 * @return name of selected object
	 */
	@Override
	public void endPicking(final GL2 gl) {

		// this.setPickedPressed(false);// no further iterations
		int selectedIndex = PickingState.NONE;

		// 5. When you back to Render mode gl.glRenderMode() methods return
		// number of hits
		final int howManyObjects = gl.glRenderMode(GL2.GL_RENDER);

		// 6. Restore to normal settings
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

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
		sceneBuffer.garbageCollect((GL2) drawable.getGL());
		sceneBuffer.dispose();
		textureCache.dispose(drawable.getGL());
		geometryCache.dispose(drawable.getGL().getGL2());
		textRendererCache.dispose(drawable.getGL());
		this.canvas = null;
		this.camera = null;
		this.currentLayer = null;
		this.currentScene = null;
		drawable.removeGLEventListener(this);
	}

	// Use when the rotation button is on.
	public void rotateModel(final GL2 gl) {
		currentZRotation++;
		if (currentZRotation != 0) {
			final double env_width = worldDimensions.x;
			final double env_height = worldDimensions.y;
			gl.glTranslated(env_width / 2, -env_height / 2, 0);
			gl.glRotated(currentZRotation, 0, 0, 1);
			gl.glTranslated(-env_width / 2, +env_height / 2, 0);
		}
	}

	@Override
	public Rectangle2D drawFile(final GamaFile file, final FileDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) { return null; }

		if (file instanceof GamaGeometryFile) {
			final String path = file.getPath(getSurface().getScope());
			if (!ENVELOPES_CACHE.containsKey(path))
				ENVELOPES_CACHE.put(path, file.computeEnvelope(surface.getScope()));
			geometryCache.saveGeometryToProcess((GamaGeometryFile) file);
		} else if (file instanceof GamaImageFile) {
			textureCache.initializeStaticTexture(getSurface().getScope(), (GamaImageFile) file);
		}
		tryToHighlight(attributes);
		sceneBuffer.getSceneToUpdate().addFile(file, attributes);
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
		if (glu == null) { return null; }
		int realy = 0;// GL y coord pos
		final double[] wcoord = new double[4];// wx, wy, wz;// returned xyz
												// coords

		final int x = (int) windowPoint.getX(), y = (int) windowPoint.getY();

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
			case RESOURCE:
				return resourceDrawer;
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

	// HELPERS FOR COLOR, ALPHA AND TEXTURES

	public void setCurrentColor(final Color c, final double alpha) {
		if (c == null)
			return;
		setCurrentColor(c.getRed() / 255d, c.getGreen() / 255d, c.getBlue() / 255d, c.getAlpha() / 255d * alpha);
	}

	public void setCurrentColor(final Color c) {
		setCurrentColor(c, currentObjectAlpha);
	}

	public void setCurrentColor(final double red, final double green, final double blue, final double alpha) {
		currentColor = new Color((float) red, (float) green, (float) blue, (float) alpha);
		gl.glColor4d(red, green, blue, alpha);
	}

	public void setCurrentColor(final double value) {
		setCurrentColor(value, value, value, 1);
	}

	public Color getCurrentColor() {
		if (currentColor == null)
			return Color.white;
		return currentColor;
	}

	public void setCurrentTexture(final Texture t) {
		if (t == null) {
			gl.glDisable(GL.GL_TEXTURE_2D);
		} else {
			// We enable it anyway, in case the texture target has been disabled
			t.enable(gl);
			if (currentTexture == t)
				return;
		}
		currentTexture = t;
		if (t != null) {
			final boolean antiAlias = data.isAntialias();
			// Apply antialas to the texture based on the current preferences
			t.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, antiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
			t.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, antiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
		}
	}

	public Texture getCurrentTexture() {
		return currentTexture;
	}

}
