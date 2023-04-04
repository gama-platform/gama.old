/*******************************************************************************************************
 *
 * OpenGL.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl;

import static com.jogamp.opengl.glu.GLU.gluTessBeginContour;
import static com.jogamp.opengl.glu.GLU.gluTessBeginPolygon;
import static com.jogamp.opengl.glu.GLU.gluTessEndContour;
import static com.jogamp.opengl.glu.GLU.gluTessEndPolygon;
import static msi.gama.common.geometry.GeometryUtils.applyToInnerGeometries;
import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

import jogamp.opengl.glu.tessellator.GLUtessellatorImpl;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.ICoordinates.VertexVisitor;
import msi.gama.common.geometry.Rotation3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.geometry.UnboundedCoordinateSequence;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.operators.Maths;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.DrawingAttributes.DrawerType;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.renderer.caches.GeometryCache;
import ummisco.gama.opengl.renderer.caches.GeometryCache.BuiltInGeometry;
import ummisco.gama.opengl.renderer.caches.ITextureCache;
import ummisco.gama.opengl.renderer.caches.TextureCache2;
import ummisco.gama.opengl.renderer.helpers.AbstractRendererHelper;
import ummisco.gama.opengl.renderer.helpers.KeystoneHelper;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.opengl.scene.geometry.GeometryDrawer;
import ummisco.gama.opengl.scene.mesh.MeshDrawer;
import ummisco.gama.opengl.scene.resources.ResourceDrawer;
import ummisco.gama.opengl.scene.text.TextDrawer;
import ummisco.gama.ui.utils.DPIHelper;

/**
 * A class that represents an intermediate state between the rendering and the opengl state. It captures all the
 * commands sent to opengl to either record them and ouput VBOs or send them immediately (in immediate mode). Only the
 * immediate mode is implemented now. This class also manages the different caches (textures, geometries, envelopes,
 * text renderers)
 *
 * @author drogoul
 *
 */
public class OpenGL extends AbstractRendererHelper implements ITesselator {

	static {
		DEBUG.OFF();
		GamaPreferences.Displays.DRAW_ROTATE_HELPER.onChange(v -> SHOULD_DRAW_ROTATION_SPHERE = v);
	}

	/** The should draw rotation sphere. */
	private static boolean SHOULD_DRAW_ROTATION_SPHERE = GamaPreferences.Displays.DRAW_ROTATE_HELPER.getValue();

	/** The Constant NO_TEXTURE. */
	public static final int NO_TEXTURE = Integer.MAX_VALUE;

	/** The Constant NO_ANISOTROPY. */
	public static final float NO_ANISOTROPY = -1f;

	/** The drawers. */
	final Map<DrawerType, ObjectDrawer<?>> drawers = new HashMap<>();

	/** The viewport. */
	// Matrices of the display
	public final int[] viewport = new int[4];

	/** The mvmatrix. */
	public final double mvmatrix[] = new double[16];

	/** The projmatrix. */
	public final double projmatrix[] = new double[16];

	/** The gl. */
	// The real openGL context
	private GL2 gl;

	/** The glut. */
	private final GLUT glut;

	/** The glu. */
	private final GLU glu;

	/** The view height. */
	private int viewWidth, viewHeight;

	/** The picking state. */
	// private final PickingHelper pickingState;

	/** The texture cache. */
	// Textures
	private final ITextureCache textureCache = new TextureCache2(this);

	/** The texture envelope. */
	private final Envelope3D textureEnvelope = Envelope3D.create();

	/** The current texture rotation. */
	private final Rotation3D currentTextureRotation = Rotation3D.identity();

	/** The null texture rotation. */
	private final Rotation3D nullTextureRotation = new Rotation3D(0, 0, 0, 1, false);

	/** The textured. */
	private boolean textured;

	/** The primary texture. */
	private int primaryTexture = NO_TEXTURE;

	/** The alternate texture. */
	private int alternateTexture = NO_TEXTURE;

	/** The anisotropic level. */
	private float anisotropicLevel = NO_ANISOTROPY;

	/** The current color. */
	// Colors
	private Color currentColor;

	/** The current object alpha. */
	private double currentObjectAlpha = 1d;

	/** The lighted. */
	private boolean objectIsLighted;

	/** The display is lighted. */
	private boolean displayIsLighted;

	/** The in raster text mode. */
	// Text
	private boolean inRasterTextMode;
	// protected final FontCache fontCache = new FontCache();

	/** The geometry cache. */
	// Geometries
	protected final GeometryCache geometryCache;

	/** The display is wireframe. */
	protected volatile boolean displayIsWireframe;

	/** The object is wireframe. */
	protected volatile boolean objectIsWireframe;

	/** The tobj. */
	final GLUtessellatorImpl tobj = (GLUtessellatorImpl) GLU.gluNewTess();

	/** The gl tesselator drawer. */
	final VertexVisitor glTesselatorDrawer;

	/** The ratios. */
	// World
	final GamaPoint ratios = new GamaPoint();

	/** The rotation mode. */
	private boolean rotationMode;

	/** The current normal. */
	// Working objects
	final GamaPoint currentNormal = new GamaPoint();

	/** The texture coords. */
	// final GamaPoint currentScale = new GamaPoint(1, 1, 1);
	final GamaPoint textureCoords = new GamaPoint();

	/** The working vertices. */
	final UnboundedCoordinateSequence workingVertices = new UnboundedCoordinateSequence();

	/** The saved Z translation. */
	private double currentZIncrement, currentZTranslation, savedZTranslation;

	/** The Z translation suspended. */
	private volatile boolean ZTranslationSuspended;

	/** The end scene. */
	// private final boolean useJTSTriangulation = !GamaPreferences.Displays.OPENGL_TRIANGULATOR.getValue();
	private final Pass endScene = this::endScene;

	/**
	 * Instantiates a new open GL.
	 *
	 * @param renderer
	 *            the renderer
	 */
	public OpenGL(final IOpenGLRenderer renderer) {
		super(renderer);
		// DEBUG.OUT("Creation of OpenGL");
		glut = new GLUT();
		glu = new GLU();
		// pickingState = renderer.getPickingHelper();
		geometryCache = new GeometryCache(renderer);
		glTesselatorDrawer = (final double[] ordinates) -> { tobj.gluTessVertex(ordinates, 0, ordinates); };
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, this);
		GLU.gluTessProperty(tobj, GLU.GLU_TESS_TOLERANCE, 0.1);
		drawers.put(DrawerType.STRING,
				/* FLAGS.USE_LEGACY_DRAWERS ? new LegacyTextDrawer(this) : */ new TextDrawer(this));
		drawers.put(DrawerType.GEOMETRY, new GeometryDrawer(this));
		drawers.put(DrawerType.MESH,
				/* FLAGS.USE_LEGACY_DRAWERS ? new LegacyMeshDrawer(this) : */ new MeshDrawer(this));
		drawers.put(DrawerType.RESOURCE, new ResourceDrawer(this));
	}

	/**
	 * Gets the drawer for.
	 *
	 * @param type
	 *            the type
	 * @return the drawer for
	 */
	public ObjectDrawer<? extends AbstractObject<?, ?>> getDrawerFor(final DrawerType type) {
		return drawers.get(type);
	}

	/**
	 * Gets the geometry drawer.
	 *
	 * @return the geometry drawer
	 */
	public GeometryDrawer getGeometryDrawer() { return (GeometryDrawer) drawers.get(DrawerType.GEOMETRY); }

	/**
	 * Dispose.
	 */
	public void dispose() {
		for (ObjectDrawer<?> o : drawers.values()) { o.dispose(); }
		geometryCache.dispose();
		textureCache.dispose();
		gl = null;

	}

	@Override
	public GL2 getGL() { return gl; }

	/**
	 * Sets the gl2.
	 *
	 * @param gl2
	 *            the new gl2
	 */
	public void setGL2(final GL2 gl2) {
		this.gl = gl2;
		textureCache.initialize();
		if (anisotropicLevel == NO_ANISOTROPY && gl2.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
			final FloatBuffer aniso = Buffers.newDirectFloatBuffer(1);
			gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso);
			anisotropicLevel = aniso.get();
			// DEBUG.OUT("Anisotropic level: " + anisotropicLevel);
		}

	}

	/**
	 * Gets the glut.
	 *
	 * @return the glut
	 */
	public GLUT getGlut() { return glut; }

	/**
	 * Reshapes the GL world to comply with a new view size and computes the resulting ratios between pixels and world
	 * coordinates
	 *
	 * @param newGL
	 *            the (possibly new) GL2 context
	 * @param width
	 *            the width of the view (in pixels)
	 * @param height
	 *            the height of the view (in pixels)
	 * @return
	 */
	public void reshape(final GL2 newGL, final int width, final int height) {
		setGL2(newGL);
		// newGL.glViewport(0, 0, width, height);
		viewWidth = width;
		viewHeight = height;
		resetMatrix(GLMatrixFunc.GL_MODELVIEW);
		resetMatrix(GLMatrixFunc.GL_PROJECTION);
		updatePerspective(newGL);

		final double[] pixelSize = new double[4];
		glu.gluProject(getWorldWidth(), 0, 0, mvmatrix, 0, projmatrix, 0, viewport, 0, pixelSize, 0);
		final double initialEnvWidth = pixelSize[0];
		final double initialEnvHeight = pixelSize[1];
		final double envWidthInPixels = 2 * pixelSize[0] - width;
		final double envHeightInPixels = 2 * pixelSize[1] - height;
		final double windowWidthInModelUnits = getWorldWidth() * width / envWidthInPixels;
		final double windowHeightInModelUnits = getWorldHeight() * height / envHeightInPixels;
		final double xRatio = width / windowWidthInModelUnits / getData().getZoomLevel();
		final double yRatio = height / windowHeightInModelUnits / getData().getZoomLevel();
		if (DEBUG.IS_ON()) {
			debugSizes(width, height, initialEnvWidth, initialEnvHeight, envWidthInPixels, envHeightInPixels,
					getData().getZoomLevel(), xRatio, yRatio);
		}
		ratios.setLocation(xRatio, yRatio, 0d);
	}

	/**
	 * Debug sizes.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param initialEnvWidth
	 *            the initial env width
	 * @param initialEnvHeight
	 *            the initial env height
	 * @param envWidth
	 *            the env width
	 * @param envHeight
	 *            the env height
	 * @param zoomLevel
	 *            the zoom level
	 * @param xRatio
	 *            the x ratio
	 * @param yRatio
	 *            the y ratio
	 */
	@SuppressWarnings ("restriction")
	private void debugSizes(final int width, final int height, final double initialEnvWidth,
			final double initialEnvHeight, final double envWidth, final double envHeight, final double zoomLevel,
			final double xRatio, final double yRatio) {

		DEBUG.SECTION("RESHAPING TO " + width + "x" + height);
		DEBUG.OUT("Camera zoom level ", 35, zoomLevel);
		DEBUG.OUT("Size of env in units ", 35, getWorldWidth() + " | " + getWorldHeight());
		DEBUG.OUT("Ratio width/height in units ", 35, getWorldWidth() / getWorldHeight());
		DEBUG.OUT("Initial Size of env in pixels ", 35, initialEnvWidth + " | " + initialEnvHeight);
		DEBUG.OUT("Size of env in pixels ", 35, envWidth + " | " + envHeight);
		DEBUG.OUT("Ratio width/height in pixels ", 35, envWidth / envHeight);
		DEBUG.OUT("Window pixels/env pixels ", 35, width / envWidth + " | " + height / envHeight);
		DEBUG.OUT("Current XRatio pixels/env in units ", 35, xRatio + " | " + yRatio);
		DEBUG.OUT("Device Zoom =  " + DPIHelper.getDeviceZoom(renderer.getCanvas().getMonitor()));
		DEBUG.OUT("AutoScale down = ", false);
		DEBUG.OUT(" " + DPIHelper.autoScaleDown(getCanvas().getMonitor(), width) + " "
				+ DPIHelper.autoScaleDown(getCanvas().getMonitor(), height));
		// DEBUG.OUT("Client area of window:" + getRenderer().getCanvas().getClientArea());
	}

	/**
	 * Update perspective.
	 *
	 * @param gl
	 *            the gl
	 */
	public void updatePerspective(final GL2 gl) {
		final double height = getViewHeight();
		final double aspect = getViewWidth() / (height == 0d ? 1d : height);
		final double maxDim = getMaxEnvDim();
		double zNear = getZNear();
		if (zNear < 0.0) {
			zNear = maxDim / 100d;
			data.setZNear(zNear);
		}
		double zFar = getZFar();
		if (zFar < 0.0) {
			zFar = maxDim * 100d;
			data.setZFar(zFar);
		}

		if (!getData().isOrtho()) {
			try {
				double fW, fH;
				final double fovY = getData().getCameraLens();
				if (aspect > 1.0) {
					fH = Math.tan(fovY / 360 * Math.PI) * zNear;
					fW = fH * aspect;
				} else {
					fW = Math.tan(fovY / 360 * Math.PI) * zNear;
					fH = fW / aspect;
				}

				gl.glFrustum(-fW, fW, -fH, fH, zNear, zFar);
			} catch (final BufferOverflowException e) {
				DEBUG.ERR("Buffer overflow exception");
			}
		} else if (aspect >= 1.0) {
			gl.glOrtho(-maxDim * aspect, maxDim * aspect, -maxDim, maxDim, maxDim * 10, -maxDim * 10);
		} else {
			gl.glOrtho(-maxDim, maxDim, -maxDim / aspect, maxDim / aspect, maxDim * 10, -maxDim * 10);
		}

		// else {
		// gl.glOrtho(-maxDim, maxDim, -maxDim, maxDim, maxDim * 10, -maxDim * 10);
		// }
		//
		// translateBy(0d, 0d, maxDim * 0.2);
		getRenderer().getCameraHelper().animate();
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projmatrix, 0);
	}

	/**
	 * Gets the pixel width and height of world.
	 *
	 * @return the pixel width and height of world
	 */
	public double[] getPixelWidthAndHeightOfWorld() {
		final double[] coord = new double[4];
		glu.gluProject(getWorldWidth(), 0, 0, mvmatrix, 0, projmatrix, 0, viewport, 0, coord, 0);
		return coord;
	}

	/**
	 * Gets the view width.
	 *
	 * @return the view width
	 */
	public int getViewWidth() { return viewWidth; }

	/**
	 * Gets the view height.
	 *
	 * @return the view height
	 */
	public int getViewHeight() { return viewHeight; }

	/**
	 * Sets the z increment.
	 *
	 * @param z
	 *            the new z increment
	 */
	public void setZIncrement(final double z) {
		currentZTranslation = 0;
		currentZIncrement = z;
	}

	/**
	 * Computes the translation in Z to enable z-fighting, using the current z increment, computed by ModelScene. The
	 * translations are cumulative
	 */
	public void translateByZIncrement() {
		if (!ZTranslationSuspended) { currentZTranslation += currentZIncrement; }
	}

	/**
	 * Suspend Z translation.
	 */
	public void suspendZTranslation() {
		ZTranslationSuspended = true;
		savedZTranslation = currentZTranslation;
		currentZTranslation = 0;
	}

	/**
	 * Resume Z translation.
	 */
	public void resumeZTranslation() {
		ZTranslationSuspended = false;
		currentZTranslation = savedZTranslation;
	}

	/**
	 * Gets the current Z translation.
	 *
	 * @return the current Z translation
	 */
	public double getCurrentZTranslation() { return currentZTranslation; }

	/**
	 * Gets the current Z increment.
	 *
	 * @return the current Z increment
	 */
	public double getCurrentZIncrement() { return currentZIncrement; }

	/**
	 * Returns the previous state
	 *
	 * @param lighted
	 * @return
	 */
	public boolean setObjectLighting(final boolean lighted) {
		boolean previous = objectIsLighted;
		if (lighted != previous) {
			objectIsLighted = lighted;
			updateLightMode();
		}
		return previous;
	}

	/**
	 * Sets the display lighting.
	 *
	 * @param lighted
	 *            the lighted
	 * @return true, if successful
	 */
	public boolean setDisplayLighting(final boolean lighted) {
		boolean previous = displayIsLighted;
		if (lighted != previous) {
			displayIsLighted = lighted;
			updateLightMode();
		}
		return previous;
	}

	/**
	 * Update light mode.
	 */
	private void updateLightMode() {
		if (getLighting()) {
			gl.glEnable(GLLightingFunc.GL_LIGHTING);
		} else {
			gl.glDisable(GLLightingFunc.GL_LIGHTING);
		}
	}

	/**
	 * Gets the lighting.
	 *
	 * @return the lighting
	 */
	public boolean getLighting() { return objectIsLighted && displayIsLighted; }

	/**
	 * Matrix mode.
	 *
	 * @param mode
	 *            the mode
	 */
	public void matrixMode(final int mode) {
		gl.glMatrixMode(mode);
	}

	/**
	 * Push matrix.
	 */
	public void pushMatrix() {
		gl.glPushMatrix();
	}

	/**
	 * Pop matrix.
	 */
	public void popMatrix() {
		gl.glPopMatrix();
	}

	/**
	 * Reset matrix.
	 *
	 * @param mode
	 *            the mode
	 */
	private void resetMatrix(final int mode) {
		matrixMode(mode);
		gl.glLoadIdentity();
	}

	/**
	 * Push identity.
	 *
	 * @param mode
	 *            the mode
	 */
	public void pushIdentity(final int mode) {
		matrixMode(mode);
		pushMatrix();
		gl.glLoadIdentity();
	}

	/**
	 * Pop.
	 *
	 * @param mode
	 *            the mode
	 */
	public void pop(final int mode) {
		matrixMode(mode);
		popMatrix();
	}

	/**
	 * Push.
	 *
	 * @param mode
	 *            the mode
	 */
	public void push(final int mode) {
		matrixMode(mode);
		pushMatrix();
	}

	/**
	 * Enable.
	 *
	 * @param state
	 *            the state
	 */
	public void enable(final int state) {
		if (!gl.glIsEnabled(state)) { gl.glEnableClientState(state); }
	}

	/**
	 * Disable.
	 *
	 * @param state
	 *            the state
	 */
	public void disable(final int state) {
		if (gl.glIsEnabled(state)) { gl.glDisableClientState(state); }
	}

	@Override
	public void beginDrawing(final int style) {
		gl.glBegin(style);
	}

	@Override
	public void endDrawing() {
		gl.glEnd();
	}

	/**
	 * Translate by.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void translateBy(final double x, final double y, final double z) {
		gl.glTranslated(x, y, z);
	}

	/**
	 * Translate by.
	 *
	 * @param ordinates
	 *            the ordinates
	 */
	public void translateBy(final double... ordinates) {
		switch (ordinates.length) {
			case 0:
				return;
			case 1:
				translateBy(ordinates[0], 0, 0);
				break;
			case 2:
				translateBy(ordinates[0], ordinates[1], 0);
				break;
			default:
				translateBy(ordinates[0], ordinates[1], ordinates[2]);
		}
	}

	/**
	 * Translate by.
	 *
	 * @param p
	 *            the p
	 */
	public void translateBy(final GamaPoint p) {
		translateBy(p.x, p.y, p.z);
	}

	/**
	 * Rotate by.
	 *
	 * @param angle
	 *            the angle
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void rotateBy(final double angle, final double x, final double y, final double z) {
		gl.glRotated(angle, x, y, z);
	}

	/**
	 * Rotate by.
	 *
	 * @param rotation
	 *            the rotation
	 */
	public void rotateBy(final Rotation3D rotation) {
		final GamaPoint axis = rotation.getAxis();
		final double angle = rotation.getAngle() * Maths.toDeg;
		rotateBy(angle, axis.x, axis.y, axis.z);
	}

	/**
	 * Scale by.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void scaleBy(final double x, final double y, final double z) {
		// currentScale.setLocation(x, y, z);
		gl.glScaled(x, y, z);
	}

	/**
	 * Scale by.
	 *
	 * @param scaling
	 *            the scaling
	 */
	public void scaleBy(final Scaling3D scaling) {
		scaleBy(scaling.getX(), scaling.getY(), scaling.getZ());
	}

	// DRAWING

	/**
	 * Draws an arbitrary shape using a set of vertices as input, computing the normal if necessary and drawing the
	 * contour if a border is present
	 *
	 * @param yNegatedVertices
	 *            the set of vertices to draw
	 * @param number
	 *            the number of vertices to draw. Either 3 (a triangle), 4 (a quad) or -1 (a polygon)
	 * @param solid
	 *            whether to draw the shape as a solid shape
	 * @param clockwise
	 *            whether to draw the shape in the clockwise direction (the vertices are always oriented clockwise)
	 * @param computeNormal
	 *            whether to compute the normal for this shape
	 * @param border
	 *            if not null, will be used to draw the contour
	 */
	public void drawSimpleShape(final ICoordinates yNegatedVertices, final int number, final boolean clockwise,
			final boolean computeNormal, final Color border) {
		if (!isWireframe()) {
			if (computeNormal) { setNormal(yNegatedVertices, clockwise); }
			final int style = number == 4 ? GL2ES3.GL_QUADS : number == -1 ? GL2.GL_POLYGON : GL.GL_TRIANGLES;
			drawVertices(style, yNegatedVertices, number, clockwise);
		}
		if (border != null || isWireframe()) {
			final Color colorToUse = border != null ? border : getCurrentColor();
			drawClosedLine(yNegatedVertices, colorToUse, -1);
		}
	}

	/**
	 * Use whatever triangulator is available (JTS or GLU) to draw a polygon
	 *
	 * @param p
	 * @param yNegatedVertices
	 * @param clockwise
	 * @param drawer
	 */
	public void drawPolygon(final Polygon p, final ICoordinates yNegatedVertices, final boolean clockwise) {
		gluTessBeginPolygon(tobj, null);
		gluTessBeginContour(tobj);
		yNegatedVertices.visitClockwise(glTesselatorDrawer);
		gluTessEndContour(tobj);
		applyToInnerGeometries(p, geom -> {
			gluTessBeginContour(tobj);
			getContourCoordinates(geom).visitYNegatedCounterClockwise(glTesselatorDrawer);
			gluTessEndContour(tobj);
		});
		gluTessEndPolygon(tobj);
		// }
	}

	/**
	 * Draw closed line.
	 *
	 * @param yNegatedVertices
	 *            the y negated vertices
	 * @param number
	 *            the number
	 */
	public void drawClosedLine(final ICoordinates yNegatedVertices, final int number) {
		drawVertices(GL.GL_LINE_LOOP, yNegatedVertices, number, true);
	}

	/**
	 * Draw closed line.
	 *
	 * @param yNegatedVertices
	 *            the y negated vertices
	 * @param color
	 *            the color
	 * @param number
	 *            the number
	 */
	public void drawClosedLine(final ICoordinates yNegatedVertices, final Color color, final int number) {
		if (color == null) return;
		final Color previous = swapCurrentColor(color);
		drawClosedLine(yNegatedVertices, number);
		setCurrentColor(previous);
	}

	/**
	 * Draw line.
	 *
	 * @param yNegatedVertices
	 *            the y negated vertices
	 * @param number
	 *            the number
	 */
	public void drawLine(final ICoordinates yNegatedVertices, final int number) {
		// final boolean previous = this.setLighting(false);
		drawVertices(GL.GL_LINE_STRIP, yNegatedVertices, number, true);
		// this.setLighting(previous);
	}

	/**
	 * Outputs a single vertex to OpenGL, applying the z-translation to it and computing the maximum z outputted so far
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public void outputVertex(final double x, final double y, final double z) {
		gl.glVertex3d(x, y, z + currentZTranslation);
	}

	/**
	 * Output tex coord.
	 *
	 * @param u
	 *            the u
	 * @param v
	 *            the v
	 */
	public void outputTexCoord(final double u, final double v) {
		gl.glTexCoord2d(u, v);
	}

	/**
	 * Output normal.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void outputNormal(final double x, final double y, final double z) {
		currentNormal.setLocation(x, y, z);
		gl.glNormal3d(x, y, z);
	}

	/**
	 * Draw vertex.
	 *
	 * @param coords
	 *            the coords
	 * @param normal
	 *            the normal
	 * @param tex
	 *            the tex
	 */
	public void drawVertex(final GamaPoint coords, final GamaPoint normal, final GamaPoint tex) {
		if (normal != null) { outputNormal(normal.x, normal.y, normal.z); }
		if (tex != null) { gl.glTexCoord3d(tex.x, tex.y, tex.z); }
		outputVertex(coords.x, coords.y, coords.z);
	}

	@Override
	public void drawVertex(final int i, final double x, final double y, final double z) {
		if (isTextured()) {
			textureCoords.setLocation(x, y, z);
			if (GamaPreferences.Displays.OPENGL_TEXTURE_ORIENTATION.getValue()) {
				currentTextureRotation.applyTo(textureCoords);
			} else {
				nullTextureRotation.applyTo(textureCoords);
			}
			final double u = 1 - (textureCoords.x - textureEnvelope.getMinX()) / textureEnvelope.getWidth();
			final double v = (textureCoords.y - textureEnvelope.getMinY()) / textureEnvelope.getHeight();
			outputTexCoord(u, v);
		}
		outputVertex(x, y, z);
	}

	/**
	 * Draw vertices.
	 *
	 * @param style
	 *            the style
	 * @param yNegatedVertices
	 *            the y negated vertices
	 * @param number
	 *            the number
	 * @param clockwise
	 *            the clockwise
	 */
	public void drawVertices(final int style, final ICoordinates yNegatedVertices, final int number,
			final boolean clockwise) {
		beginDrawing(style);
		yNegatedVertices.visit(this::drawVertex, number, clockwise);
		endDrawing();
	}

	/**
	 * Draw the vertices using the style provided and uses the double[] parameter to determine the texture coordinates
	 * associated with each vertex
	 *
	 * @param glQuads
	 * @param yNegatedVertices
	 * @param i
	 * @param b
	 * @param texCoords
	 */
	public void drawVertices(final int style, final ICoordinates yNegatedVertices, final int number,
			final boolean clockwise, final double[] texCoords) {
		beginDrawing(style);
		yNegatedVertices.visit((index, x, y, z) -> {
			outputTexCoord(texCoords[index * 2], texCoords[index * 2 + 1]);
			outputVertex(x, y, z);
		}, number, clockwise);
		endDrawing();
	}

	/**
	 * Replaces the current color by the parameter, sets the alpha of the parameter to be the one of the current color,
	 * and returns the ex-current color
	 *
	 * @param color
	 *            a Color
	 * @return the previous current color
	 */
	public Color swapCurrentColor(final Color color) {
		final Color old = currentColor;
		setCurrentColor(color, old == null ? 1 : old.getAlpha() / 255d);
		return old;
	}

	/**
	 * Sets the normal.
	 *
	 * @param yNegatedVertices
	 *            the y negated vertices
	 * @param clockwise
	 *            the clockwise
	 * @return the gama point
	 */
	public GamaPoint setNormal(final ICoordinates yNegatedVertices, final boolean clockwise) {
		yNegatedVertices.getNormal(clockwise, 1, currentNormal);
		outputNormal(currentNormal.x, currentNormal.y, currentNormal.z);
		if (isTextured()) { computeTextureCoordinates(yNegatedVertices, clockwise); }
		return currentNormal;
	}

	/**
	 * Compute texture coordinates.
	 *
	 * @param yNegatedVertices
	 *            the y negated vertices
	 * @param clockwise
	 *            the clockwise
	 */
	private void computeTextureCoordinates(final ICoordinates yNegatedVertices, final boolean clockwise) {
		workingVertices.setTo(yNegatedVertices);
		if (GamaPreferences.Displays.OPENGL_TEXTURE_ORIENTATION.getValue()) {
			currentTextureRotation.rotateToHorizontal(currentNormal,
					workingVertices.directionBetweenLastPointAndOrigin(), clockwise);
			workingVertices.applyRotation(currentTextureRotation);
		} else {
			workingVertices.applyRotation(nullTextureRotation);
		}

		workingVertices.getEnvelopeInto(textureEnvelope);
	}

	/**
	 * Sets the current color.
	 *
	 * @param c
	 *            the c
	 * @param alpha
	 *            the alpha
	 */
	public void setCurrentColor(final Color c, final double alpha) {
		if (c == null) return;
		setCurrentColor(c.getRed() / 255d, c.getGreen() / 255d, c.getBlue() / 255d, c.getAlpha() / 255d * alpha);
	}

	/**
	 * Sets the current color.
	 *
	 * @param c
	 *            the new current color
	 */
	public void setCurrentColor(final Color c) {
		setCurrentColor(c, currentObjectAlpha);
	}

	/**
	 * Sets the current color.
	 *
	 * @param red
	 *            the red
	 * @param green
	 *            the green
	 * @param blue
	 *            the blue
	 * @param alpha
	 *            the alpha
	 */
	public void setCurrentColor(final double red, final double green, final double blue, final double alpha) {
		currentColor = new Color((float) Math.max(red, 0), (float) Math.max(green, 0), (float) Math.max(blue, 0),
				(float) alpha);
		gl.glColor4d(red, green, blue, alpha);
	}

	/**
	 * Gets the current color.
	 *
	 * @return the current color
	 */
	public Color getCurrentColor() { return currentColor; }

	// LINE WIDTH

	/**
	 * Sets the line width.
	 *
	 * @param width
	 *            the new line width
	 */
	public void setLineWidth(final double width) {
		gl.glLineWidth((float) width);
	}

	// ALPHA
	/**
	 * Between 0d (transparent) to 1d (opaque)
	 *
	 * @param alpha
	 */
	public final void setCurrentObjectAlpha(final double alpha) { currentObjectAlpha = alpha; }

	/**
	 * Gets the current object alpha.
	 *
	 * @return the current object alpha
	 */
	public double getCurrentObjectAlpha() { return currentObjectAlpha; }

	// TEXTURES

	/**
	 * Sets the id of the textures to enable. If the first is equal to NO_TEXTURE, all textures are disabled. If the
	 * second is equal to NO_TEXTURE, then the first one is also bound to the second unit.
	 *
	 * @param t
	 *            the id of the texture to enable. NO_TEXTURE means disabling textures
	 */
	public void setCurrentTextures(final int t0, final int t1) {
		primaryTexture = t0;
		alternateTexture = t1;
		textured = t0 != NO_TEXTURE;
		enablePrimaryTexture();
	}

	/**
	 * Bind texture.
	 *
	 * @param texture
	 *            the texture
	 */
	public void bindTexture(final int texture) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		// Apply antialas to the texture based on the current preferences
		final boolean isAntiAlias = getData().isAntialias();
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, isAntiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, isAntiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
		if (isAntiAlias && anisotropicLevel > NO_ANISOTROPY) {
			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropicLevel);
		}
	}

	/**
	 * Enable primary texture.
	 */
	public void enablePrimaryTexture() {
		if (primaryTexture == NO_TEXTURE) return;
		bindTexture(primaryTexture);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	/**
	 * Enable alternate texture.
	 */
	public void enableAlternateTexture() {
		if (alternateTexture == NO_TEXTURE) return;
		bindTexture(alternateTexture);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	/**
	 * Disable textures.
	 */
	public void disableTextures() {
		gl.glDisable(GL.GL_TEXTURE_2D);
		textured = false;
	}

	/**
	 * Delete volatile textures.
	 */
	public void deleteVolatileTextures() {
		textureCache.deleteVolatileTextures();
	}

	/**
	 * Cache texture.
	 *
	 * @param file
	 *            the file
	 */
	public void cacheTexture(final IImageProvider file) {
		if (file == null) return;
		textureCache.processs(file);
	}

	/**
	 * Gets the texture id.
	 *
	 * @param file
	 *            the file
	 * @param useCache
	 *            the use cache
	 * @return the texture id
	 */
	public int getTextureId(final IImageProvider file, final boolean useCache) {
		final Texture r = textureCache.getTexture(file, file.isAnimated(), useCache);
		if (r == null) return NO_TEXTURE;
		return r.getTextureObject();
	}

	/**
	 * Gets the texture id.
	 *
	 * @param img
	 *            the img
	 * @return the texture id
	 */
	public int getTextureId(final BufferedImage img) {
		final Texture r = textureCache.getTexture(img);
		if (r == null) return NO_TEXTURE;
		return r.getTextureObject();
	}

	/**
	 * Gets the texture.
	 *
	 * @param file
	 *            the file
	 * @param isAnimated
	 *            the is animated
	 * @param useCache
	 *            the use cache
	 * @return the texture
	 */
	public Texture getTexture(final IImageProvider file, final boolean isAnimated, final boolean useCache) {
		return textureCache.getTexture(file, isAnimated, useCache);
	}

	// GEOMETRIES

	/**
	 * Cache geometry.
	 *
	 * @param object
	 *            the object
	 */
	public void cacheGeometry(final GamaGeometryFile object) {
		geometryCache.process(object);
	}

	/**
	 * Gets the envelope for.
	 *
	 * @param obj
	 *            the obj
	 * @return the envelope for
	 */
	public Envelope3D getEnvelopeFor(final Object obj) {
		if (obj instanceof GamaGeometryFile) return geometryCache.getEnvelope((GamaGeometryFile) obj);
		if (obj instanceof Geometry) return Envelope3D.of((Geometry) obj);
		return null;
	}

	// TEXT

	/**
	 * Draws one string in raster at the given coords and with the given font. Enters and exits raster mode before and
	 * after drawing the string
	 *
	 * @param seq
	 *            the string to draw
	 * @param font
	 *            the font to draw with
	 * @param x,y,z
	 *            the {x, y, z} coordinates
	 */
	public void rasterText(final String s, final int font, final double x, final double y, final double z) {
		beginRasterTextMode();
		final boolean previous = setObjectLighting(false);
		gl.glRasterPos3d(x, y, z);
		glut.glutBitmapString(font, s);
		setObjectLighting(previous);
		exitRasterTextMode();
	}

	/**
	 * Exit raster text mode.
	 */
	public void exitRasterTextMode() {
		gl.glEnable(GL.GL_BLEND);
		popMatrix();
		inRasterTextMode = false;
	}

	/**
	 * Begin raster text mode.
	 */
	public void beginRasterTextMode() {
		if (inRasterTextMode) return;
		pushMatrix();
		gl.glDisable(GL.GL_BLEND);
		inRasterTextMode = true;
	}

	/**
	 * Gets the world width.
	 *
	 * @return the world width
	 */
	public double getWorldWidth() { return getData().getEnvWidth(); }

	/**
	 * Gets the world height.
	 *
	 * @return the world height
	 */
	public double getWorldHeight() { return getData().getEnvHeight(); }

	/**
	 * Sets the display wireframe.
	 *
	 * @param wireframe
	 *            the new display wireframe
	 */
	public boolean setDisplayWireframe(final boolean wireframe) {
		boolean old = displayIsWireframe;
		if (old != wireframe) {
			displayIsWireframe = wireframe;
			updatePolygonMode();
		}
		return old;
	}

	/**
	 * Sets the object wireframe. Returns the previous value.
	 *
	 * @param wireframe
	 *            the new object wireframe
	 */
	public boolean setObjectWireframe(final boolean wireframe) {
		boolean old = objectIsWireframe;
		if (old != wireframe) {
			objectIsWireframe = wireframe;
			updatePolygonMode();
		}
		return old;
	}

	/**
	 * Sets the polygon mode.
	 *
	 * @param wireframe
	 *            the new polygon mode
	 */
	public void updatePolygonMode() {
		// AD might be a bit costly as setObjectWireFrame() is called for every object...
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, isWireframe() ? GL2GL3.GL_LINE : GL2GL3.GL_FILL);
	}

	/**
	 * Checks if is wireframe.
	 *
	 * @return true, if is wireframe
	 */
	public boolean isWireframe() { return displayIsWireframe || objectIsWireframe; }

	// PICKING

	/**
	 * Run with names.
	 *
	 * @param r
	 *            the r
	 */
	public void runWithNames(final Runnable r) {
		gl.glInitNames();
		gl.glPushName(0);
		r.run();
		gl.glPopName();
	}

	/**
	 * Register for selection.
	 *
	 * @param index
	 *            the index
	 */
	public void registerForSelection(final int index) {
		gl.glLoadName(index);
	}

	// LISTS

	/**
	 * Compile as list.
	 *
	 * @param r
	 *            the r
	 * @return the int
	 */
	public int compileAsList(final Runnable r) {
		final int index = gl.glGenLists(1);
		gl.glNewList(index, GL2.GL_COMPILE);
		r.run();
		gl.glEndList();
		return index;
	}

	/**
	 * Draw list.
	 *
	 * @param i
	 *            the i
	 */
	public void drawList(final int i) {
		gl.glCallList(i);
	}

	/**
	 * Delete list.
	 *
	 * @param index
	 *            the index
	 */
	public void deleteList(final Integer index) {
		gl.glDeleteLists(index, 1);
	}

	/**
	 * Draw cached geometry.
	 *
	 * @param file
	 *            the file
	 * @param border
	 *            the border
	 */
	public void drawCachedGeometry(final GamaGeometryFile file, final Color border) {
		if (file == null) return;
		final Integer index = geometryCache.get(file);
		if (index != null) {
			drawList(index);
			if (border != null || isWireframe()) {
				final Color colorToUse = border != null ? border : getCurrentColor();
				final Color old = swapCurrentColor(colorToUse);
				boolean previous = setObjectWireframe(true);
				try {
					drawList(index);
				} finally {
					setCurrentColor(old);
					setObjectWireframe(previous);
				}
			}
		}
	}

	/**
	 * Draw cached geometry.
	 *
	 * @param id
	 *            the id
	 * @param border
	 *            the border
	 */
	public void drawCachedGeometry(final IShape.Type id, /* final boolean solid, */ final Color border) {
		if (geometryCache == null || id == null) return;
		final BuiltInGeometry object = geometryCache.get(id);
		if (object == null) return;
		if (!isWireframe()) { object.draw(this); }
		if (border != null || isWireframe()) {
			final Color colorToUse = border != null ? border : getCurrentColor();
			final Color old = swapCurrentColor(colorToUse);
			boolean previous = setObjectWireframe(true);
			try {
				object.draw(this);
			} finally {
				setCurrentColor(old);
				setObjectWireframe(previous);
			}
		}
	}

	/**
	 * Initialize shape cache.
	 */
	public void initializeShapeCache() {
		textured = true;
		geometryCache.initialize(this);
		textured = false;
	}

	/**
	 * Checks if is textured.
	 *
	 * @return true, if is textured
	 */
	public boolean isTextured() { return textured && !isWireframe(); }

	/** The previous object lighting. */
	boolean previousObjectWireframe, previousObjectLighting;

	/**
	 * Begin object.
	 *
	 * @param object
	 *            the object
	 */
	public void beginObject(final AbstractObject object, final boolean isPicking) {
		// DEBUG.OUT("Object " + object + " begin and is " + (object.getAttributes().isEmpty() ? "empty" : "filled"));
		DrawingAttributes att = object.getAttributes();
		if (isPicking) { registerForSelection(att.getIndex()); }
		boolean empty = att.isEmpty();
		previousObjectWireframe = setObjectWireframe(empty);
		previousObjectLighting = setObjectLighting(att.isLighting());
		setLineWidth(att.getLineWidth());
		setCurrentTextures(object.getPrimaryTexture(this), object.getAlternateTexture(this));
		if (isTextured()) { gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA); }
		setCurrentColor(att.getColor());
		if (!empty && !att.isSynthetic()) {
			gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_DECAL);
		}

	}

	/**
	 * End object.
	 *
	 * @param object
	 *            the object
	 */
	public void endObject(final AbstractObject object, final boolean isPicking) {
		disableTextures();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		translateByZIncrement();
		if (object.isFilled() && !object.getAttributes().isSynthetic()) {
			gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
		}
		setObjectLighting(previousObjectLighting);
		setObjectWireframe(previousObjectWireframe);
		if (isPicking) { renderer.getPickingHelper().tryPick(object.getAttributes()); }
	}

	/** The previous display lighting. */
	boolean previousDisplayWireframe, previousDisplayLighting;

	/**
	 * Begin scene.
	 *
	 * @return the pass
	 */
	public Pass beginScene() {
		previousDisplayWireframe = setDisplayWireframe(getData().isWireframe());
		previousDisplayLighting = setDisplayLighting(getData().isLightOn());
		processUnloadedCacheObjects();
		final Color backgroundColor = getData().getBackgroundColor();
		gl.glClearColor(backgroundColor.getRed() / 255.0f, backgroundColor.getGreen() / 255.0f,
				backgroundColor.getBlue() / 255.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		gl.glClearDepth(1.0f);
		resetMatrix(GLMatrixFunc.GL_PROJECTION);
		updatePerspective(gl);
		resetMatrix(GLMatrixFunc.GL_MODELVIEW);
		// AD removed from here and put in ModelScene.draw() so that it is inside the keystone drawing. See #3285
		// rotateModel();
		return endScene;
	}

	/**
	 * End scene.
	 */
	public void endScene() {
		boolean drawFPS = getData().isShowfps();
		boolean drawRotation = rotationMode && SHOULD_DRAW_ROTATION_SPHERE;
		boolean drawROI = renderer.getCameraHelper().getROIEnvelope() != null;
		if (drawFPS || drawRotation || drawROI) { disableTextures(); }
		drawFPS(drawFPS);
		drawROI(drawROI);
		drawRotation(drawRotation);
		setDisplayLighting(previousDisplayLighting);
		setDisplayWireframe(previousDisplayWireframe);
		gl.glFinish();
	}

	/**
	 * Process unloaded cache objects.
	 */
	public void processUnloadedCacheObjects() {
		textureCache.processUnloaded();
		geometryCache.processUnloaded();
	}

	/**
	 * Checks if is continuous rotation active.
	 *
	 * @return true, if is continuous rotation active
	 */
	// private boolean isContinuousRotationActive() {
	// return getData().isContinuousRotationOn() && !getData().isLocked();
	// }

	/**
	 * Rotate model.
	 */
	public void rotateModel() {
		// if (!getData().isLocked()) { getData().incrementZRotation(); }
		if (getData().hasRotation()) {
			GamaPoint c = getData().getRotationCenter();
			translateBy(c.x, c.y, c.z);
			GamaPoint p = getData().getRotationAxis();
			if (p == null) {
				rotateBy(getData().getRotationAngle(), 0, 0, 1);
			} else {
				rotateBy(getData().getRotationAngle(), p.x, p.y, p.z);
			}
			translateBy(-c.x, -c.y, -c.z);
		}
	}

	/**
	 * Initialize GL states.
	 *
	 * @param bg
	 *            the bg
	 */
	public void initializeGLStates(final Color bg) {
		gl.glClearColor(bg.getRed() / 255.0f, bg.getGreen() / 255.0f, bg.getBlue() / 255.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

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
		if (GamaPreferences.Displays.ONLY_VISIBLE_FACES.getValue()) {
			gl.glEnable(GL.GL_CULL_FACE);
			gl.glCullFace(GL.GL_BACK);
		}
		// Turn on clockwise direction of vertices as an indication of "front" (important)
		gl.glFrontFace(GL.GL_CW);

		// Hints
		int hint = getData().isAntialias() ? GL.GL_NICEST : GL.GL_FASTEST;
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, hint);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, hint);
		gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, hint);
		// gl.glHint(GL2GL3.GL_POLYGON_SMOOTH_HINT, hint);
		gl.glHint(GL2.GL_MULTISAMPLE_FILTER_HINT_NV, hint);
		// Enable texture 2D
		gl.glEnable(GL.GL_TEXTURE_2D);
		// Blending & alpha control
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
		gl.glEnable(GL2ES1.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL.GL_GREATER, 0.01f);
		// Disabling line smoothing to only rely on FSAA
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL2ES1.GL_POINT_SMOOTH);
		// gl.glEnable(GL2GL3.GL_POLYGON_SMOOTH);
		// Enabling forced normalization of normal vectors (important)
		gl.glEnable(GLLightingFunc.GL_NORMALIZE);
		// Enabling multi-sampling (necessary ?)
		// if (USE_MULTI_SAMPLE) {
		gl.glEnable(GL.GL_MULTISAMPLE);
		// Setting the default polygon mode
		updatePolygonMode();
		initializeShapeCache();

	}

	/**
	 * Gets the ratios.
	 *
	 * @return the ratios
	 */
	public GamaPoint getRatios() { return ratios; }

	/**
	 *
	 * DECORATIONS: ROI, Rotation, FPS
	 *
	 */

	public void setRotationMode(final boolean b) { rotationMode = b; }

	/**
	 * Checks if is in rotation mode.
	 *
	 * @return true, if is in rotation mode
	 */
	public boolean isInRotationMode() { return rotationMode; }

	/**
	 * Draw FPS.
	 *
	 * @param doIt
	 *            the do it
	 */
	public void drawFPS(final boolean doIt) {
		if (doIt) {
			setCurrentColor(Color.black);
			final int nb = (int) getCanvas().getAnimator().getLastFPS();
			final String s = nb == 0 ? "(computing FPS...)" : nb + " FPS";
			rasterText(s, GLUT.BITMAP_HELVETICA_12, -5, 5, 0);
		}
	}

	/**
	 * Draw ROI.
	 *
	 * @param doIt
	 *            the do it
	 */
	public void drawROI(final boolean doIt) {
		if (doIt) { getGeometryDrawer().drawROIHelper(renderer.getCameraHelper().getROIEnvelope()); }
	}

	/**
	 * Size of rotation elements.
	 *
	 * @return the double
	 */
	public double sizeOfRotationElements() {
		return Math.min(getMaxEnvDim() / 4d, getData().getCameraPos().minus(getData().getCameraTarget()).norm() / 6d);
	}

	/**
	 * Draw rotation.
	 *
	 * @param doIt
	 *            the do it
	 */
	public void drawRotation(final boolean doIt) {
		if (doIt) {
			final GamaPoint target = getData().getCameraTarget();
			final double distance = getData().getCameraPos().minus(target).norm();
			getGeometryDrawer().drawRotationHelper(target, distance, Math.min(getMaxEnvDim() / 4d, distance / 8d));
		}
	}

	@Override
	public void initialize() {}

	/**
	 * Checks if is rendering keystone.
	 *
	 * @return true, if is rendering keystone
	 */
	public boolean isRenderingKeystone() {
		KeystoneHelper k = getRenderer().getKeystoneHelper();
		return k.isActive() || getRenderer().getData().isKeystoneDefined();
	}

}
