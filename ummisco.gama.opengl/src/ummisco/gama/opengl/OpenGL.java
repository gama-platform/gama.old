package ummisco.gama.opengl;

import static com.jogamp.opengl.glu.GLU.gluTessBeginContour;
import static com.jogamp.opengl.glu.GLU.gluTessBeginPolygon;
import static com.jogamp.opengl.glu.GLU.gluTessEndContour;
import static com.jogamp.opengl.glu.GLU.gluTessEndPolygon;
import static msi.gama.common.geometry.GeometryUtils.applyToInnerGeometries;
import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;
import static msi.gama.common.geometry.GeometryUtils.getYNegatedCoordinates;
import static msi.gama.common.geometry.GeometryUtils.iterateOverTriangles;
import static msi.gama.metamodel.shape.IShape.Type.CIRCLE;
import static msi.gama.metamodel.shape.IShape.Type.CONE;
import static msi.gama.metamodel.shape.IShape.Type.CUBE;
import static msi.gama.metamodel.shape.IShape.Type.CYLINDER;
import static msi.gama.metamodel.shape.IShape.Type.POINT;
import static msi.gama.metamodel.shape.IShape.Type.PYRAMID;
import static msi.gama.metamodel.shape.IShape.Type.SPHERE;
import static msi.gama.metamodel.shape.IShape.Type.SQUARE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.BufferOverflowException;
import java.nio.DoubleBuffer;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.jogamp.opengl.glu.GLUtessellatorCallbackAdapter;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.vividsolutions.jts.geom.Polygon;

import jogamp.opengl.glu.tessellator.GLUtessellatorImpl;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.ICoordinates.VertexVisitor;
import msi.gama.common.geometry.Rotation3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.geometry.UnboundedCoordinateSequence;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.operators.Maths;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.renderer.JOGLRenderer;
import ummisco.gama.opengl.renderer.caches.GeometryCache;
import ummisco.gama.opengl.renderer.caches.GeometryCache.BuiltInGeometry;
import ummisco.gama.opengl.renderer.caches.TextRenderersCache;
import ummisco.gama.opengl.renderer.helpers.PickingHelper;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.FieldDrawer;
import ummisco.gama.opengl.scene.GeometryDrawer;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.opengl.scene.ResourceObject;
import ummisco.gama.opengl.scene.StringDrawer;
import utils.DEBUG;

/**
 * A class that represents an intermediate state between the rendering and the opengl state. It captures all the
 * commands sent to opengl to either record them and ouput VBOs or send them immediately (in immediate mode). Only the
 * immediate mode is implemented now. This class also manages the different caches (textures, geometries, envelopes,
 * text renderers)
 * 
 * @author drogoul
 *
 */
public class OpenGL {

	public static final int NO_TEXTURE = Integer.MAX_VALUE;

	// Special drawers
	private final GeometryDrawer geometryDrawer;
	private final StringDrawer stringDrawer;
	private final FieldDrawer fieldDrawer;

	// Matrices of the display
	final int[] viewport = new int[4];
	final double mvmatrix[] = new double[16];
	final double projmatrix[] = new double[16];

	// The real openGL context
	private GL2 gl;
	private final GLUT glut;
	private final GLU glu;
	private int viewWidth, viewHeight;
	private final PickingHelper pickingState;

	// Textures
	private final LoadingCache<BufferedImage, TextureRenderer> volatileTextures;
	private final Cache<String, TextureRenderer> staticTextures =
			CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS).build();
	final List<String> texturesToProcess = new CopyOnWriteArrayList<>();
	private final Envelope3D textureEnvelope = new Envelope3D();
	private final Rotation3D currentTextureRotation = Rotation3D.identity();
	private boolean textured;
	private int primaryTexture = NO_TEXTURE;
	private int alternateTexture = NO_TEXTURE;
	final UnboundedCoordinateSequence workingVertices = new UnboundedCoordinateSequence();

	// Colors
	private Color currentColor;
	private double currentObjectAlpha = 1d;
	private boolean isAntiAlias;
	private boolean antiAliasChanged;
	private boolean lighted;

	// Text
	private boolean inRasterTextMode;
	protected final TextRenderersCache textRendererCache = new TextRenderersCache();
	protected final float textSizeMultiplier = 2f;

	// Geometries
	protected final GeometryCache geometryCache;
	protected boolean isWireframe;
	final GLUtessellatorImpl tobj = (GLUtessellatorImpl) GLU.gluNewTess();
	final VertexVisitor glTesselatorDrawer;

	// World
	final double worldX, worldY;
	final GamaPoint ratios = new GamaPoint();
	final Runnable cameraLook;
	final LayeredDisplayData data;

	// Working objects

	final GamaPoint workingPoint = new GamaPoint();
	final GamaPoint currentNormal = new GamaPoint();
	final GamaPoint currentScale = new GamaPoint(1, 1, 1);
	final GamaPoint textureCoords = new GamaPoint();
	private double currentZIncrement, currentZTranslation, savedZTranslation;
	private volatile boolean ZTranslationSuspended;
	private final boolean useJTSTriangulation = !GamaPreferences.Displays.OPENGL_TRIANGULATOR.getValue();

	// Will be changed when loading the renderer
	private Boolean isNonPowerOf2TexturesAvailable;

	public OpenGL(final JOGLRenderer renderer) {
		glut = new GLUT();
		glu = new GLU();
		worldX = renderer.getEnvWidth();
		worldY = renderer.getEnvHeight();
		pickingState = renderer.getPickingHelper();
		geometryCache = new GeometryCache(renderer);
		volatileTextures = CacheBuilder.newBuilder().build(new CacheLoader<BufferedImage, TextureRenderer>() {

			@Override
			public TextureRenderer load(final BufferedImage key) throws Exception {
				return buildTextureRenderer(OpenGL.this, key);
			}
		});

		glTesselatorDrawer = (final double[] ordinates) -> {
			tobj.gluTessVertex(ordinates, 0, ordinates);
		};
		final GLUtessellatorCallback adapter = new GLUtessellatorCallbackAdapter() {
			@Override
			public void begin(final int type) {
				beginDrawing(type);
			}

			@Override
			public void end() {
				endDrawing();
			}

			@Override
			public void vertex(final Object vertexData) {
				final double[] v = (double[]) vertexData;
				drawVertex(0, v[0], v[1], v[2]);
			}

		};
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, adapter);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, adapter);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, adapter);
		GLU.gluTessProperty(tobj, GLU.GLU_TESS_TOLERANCE, 0.1);
		data = renderer.data;
		cameraLook = () -> renderer.getCameraHelper().animate();
		geometryDrawer = new GeometryDrawer(this);
		fieldDrawer = new FieldDrawer(this);
		stringDrawer = new StringDrawer(this);
	}

	public ObjectDrawer<? extends AbstractObject> getDrawerFor(final AbstractObject.DrawerType type) {
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

	public void dispose() {
		// textRendererCache.dispose();
		if (geometryCache != null) {
			geometryCache.dispose(gl);
		}
		volatileTextures.invalidateAll();
		staticTextures.asMap().forEach((s, t) -> {
			t.dispose();
		});
		staticTextures.invalidateAll();
		staticTextures.cleanUp();
		gl = null;

	}

	public GL2 getGL() {
		return gl;
	}

	public void setGL2(final GL2 gl2) {
		this.gl = gl2;
		if (isNonPowerOf2TexturesAvailable == null) {
			isNonPowerOf2TexturesAvailable =
					!GamaPreferences.Displays.DISPLAY_POWER_OF_TWO.getValue() && gl.isNPOTTextureAvailable();
			GamaPreferences.Displays.DISPLAY_POWER_OF_TWO
					.onChange(newValue -> AWTTextureIO.setTexRectEnabled(newValue));
			AWTTextureIO.setTexRectEnabled(GamaPreferences.Displays.DISPLAY_POWER_OF_TWO.getValue());
			DEBUG.OUT("Non power-of-two textures available: " + isNonPowerOf2TexturesAvailable);
		}

	}

	public GLUT getGlut() {
		return glut;
	}

	/**
	 * Reshapes the GL world to comply with a new view size and computes the resulting ratios between pixels and world
	 * coordinates
	 * 
	 * @param gl
	 *            the (possibly new) GL2 context
	 * @param width
	 *            the width of the view (in pixels)
	 * @param height
	 *            the height of the view (in pixels)
	 * @return
	 */
	public void reshape(final GL2 gl, final int width, final int height) {
		setGL2(gl);
		gl.glViewport(0, 0, width, height);
		viewWidth = width;
		viewHeight = height;
		resetMatrix(GL2.GL_MODELVIEW);
		resetMatrix(GL2.GL_PROJECTION);
		updatePerspective();
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);

		final double[] pixelSize = new double[4];
		glu.gluProject(getWorldWidth(), 0, 0, mvmatrix, 0, projmatrix, 0, viewport, 0, pixelSize, 0);
		final double initialEnvWidth = pixelSize[0];
		final double initialEnvHeight = pixelSize[1];
		final double envWidthInPixels = 2 * pixelSize[0] - width;
		final double envHeightInPixels = 2 * pixelSize[1] - height;
		final double windowWidthInModelUnits = getWorldWidth() * width / envWidthInPixels;
		final double windowHeightInModelUnits = getWorldHeight() * height / envHeightInPixels;
		final double xRatio = width / windowWidthInModelUnits / data.getZoomLevel();
		final double yRatio = height / windowHeightInModelUnits / data.getZoomLevel();
		if (DEBUG.IS_ON()) {
			debugSizes(width, height, initialEnvWidth, initialEnvHeight, envWidthInPixels, envHeightInPixels,
					data.getZoomLevel(), xRatio, yRatio);
		}
		ratios.setLocation(xRatio, yRatio);
	}

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

	}

	public void updatePerspective() {
		final double height = getViewHeight();
		final double aspect = getViewWidth() / (height == 0d ? 1d : height);
		final double maxDim = getMaxWorldDim();
		if (!data.isOrtho()) {
			try {
				final double zNear = maxDim / 100d;
				double fW, fH;
				final double fovY = data.getCameralens();
				if (aspect > 1.0) {
					fH = Math.tan(fovY / 360 * Math.PI) * zNear;
					fW = fH * aspect;
				} else {
					fW = Math.tan(fovY / 360 * Math.PI) * zNear;
					fH = fW / aspect;
				}

				gl.glFrustum(-fW, fW, -fH, fH, zNear, maxDim * 100d);
			} catch (final BufferOverflowException e) {
				DEBUG.ERR("Buffer overflow exception");
			}
		} else {
			if (aspect >= 1.0) {
				gl.glOrtho(-maxDim * aspect, maxDim * aspect, -maxDim, maxDim, maxDim * 10, -maxDim * 10);
			} else {
				gl.glOrtho(-maxDim, maxDim, -maxDim / aspect, maxDim / aspect, maxDim, -maxDim);
			}
			gl.glTranslated(0d, 0d, maxDim * 0.05);
		}
		cameraLook.run();
	}

	public double[] getPixelWidthAndHeightOfWorld() {
		final double[] coord = new double[4];
		glu.gluProject(getWorldWidth(), 0, 0, mvmatrix, 0, projmatrix, 0, viewport, 0, coord, 0);
		return coord;
	}

	public GamaPoint getWorldPositionFrom(final GamaPoint mouse, final GamaPoint camera) {
		if (gl == null) { return GamaPoint.NULL_POINT; }
		final double[] wcoord = new double[4];
		final double x = (int) mouse.x, y = viewport[3] - (int) mouse.y;
		glu.gluUnProject(x, y, 0.1, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		final GamaPoint v1 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);
		glu.gluUnProject(x, y, 0.9, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		final GamaPoint v2 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);
		final GamaPoint v3 = v2.minus(v1).normalized();
		final double distance = camera.z / GamaPoint.dotProduct(new GamaPoint(0.0, 0.0, -1.0), v3);
		final GamaPoint worldCoordinates = camera.plus(v3.times(distance));
		return new GamaPoint(worldCoordinates.x, worldCoordinates.y);
	}

	public int getViewWidth() {
		return viewWidth;
	}

	public int getViewHeight() {
		return viewHeight;
	}

	public void setZIncrement(final double z) {
		currentZTranslation = 0;
		currentZIncrement = z;
	}

	/**
	 * Computes the translation in Z to enable z-fighting, using the current z increment, computed by ModelScene. The
	 * translations are cumulative
	 */
	public void translateByZIncrement() {
		if (!ZTranslationSuspended) {
			currentZTranslation += currentZIncrement;
		}
	}

	public void suspendZTranslation() {
		ZTranslationSuspended = true;
		savedZTranslation = currentZTranslation;
		currentZTranslation = 0;
	}

	public void resumeZTranslation() {
		ZTranslationSuspended = false;
		currentZTranslation = savedZTranslation;
	}

	/**
	 * Returns the previous state
	 * 
	 * @param lighted
	 * @return
	 */
	public boolean setLighting(final boolean lighted) {
		if (this.lighted == lighted) { return lighted; }
		if (lighted) {
			gl.glEnable(GL2.GL_LIGHTING);
		} else {
			gl.glDisable(GL2.GL_LIGHTING);
		}
		this.lighted = lighted;
		return !lighted;
	}

	public boolean getLighting() {
		return lighted;
	}

	public void matrixMode(final int mode) {
		gl.glMatrixMode(mode);
	}

	public void pushMatrix() {
		gl.glPushMatrix();
	}

	public void popMatrix() {
		gl.glPopMatrix();
	}

	private void resetMatrix(final int mode) {
		matrixMode(mode);
		gl.glLoadIdentity();
	}

	public void pushIdentity(final int mode) {
		matrixMode(mode);
		pushMatrix();
		gl.glLoadIdentity();
	}

	public void pop(final int mode) {
		matrixMode(mode);
		popMatrix();
	}

	public void push(final int mode) {
		matrixMode(mode);
		pushMatrix();
	}

	public void beginDrawing(final int style) {
		gl.glBegin(style);
	}

	public void endDrawing() {
		gl.glEnd();
	}

	public void translateBy(final double x, final double y, final double z) {
		gl.glTranslated(x, y, z);
	}

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

	public void translateBy(final GamaPoint p) {
		translateBy(p.x, p.y, p.z);
	}

	public void rotateBy(final double angle, final double x, final double y, final double z) {
		gl.glRotated(angle, x, y, z);
	}

	public void rotateBy(final Rotation3D rotation) {
		final GamaPoint axis = rotation.getAxis();
		final double angle = rotation.getAngle() * Maths.toDeg;
		rotateBy(angle, axis.x, axis.y, axis.z);
	}

	public void scaleBy(final double x, final double y, final double z) {
		currentScale.setLocation(x, y, z);
		gl.glScaled(x, y, z);
	}

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
	public void drawSimpleShape(final ICoordinates yNegatedVertices, final int number, final boolean solid,
			final boolean clockwise, final boolean computeNormal, final Color border) {
		if (solid) {
			if (computeNormal) {
				setNormal(yNegatedVertices, clockwise);
			}
			final int style = number == 4 ? GL2.GL_QUADS : number == -1 ? GL2.GL_POLYGON : GL2.GL_TRIANGLES;
			drawVertices(style, yNegatedVertices, number, clockwise);
		}
		drawClosedLine(yNegatedVertices, border, -1);
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
		if (useJTSTriangulation) {
			iterateOverTriangles(p,
					(tri) -> drawSimpleShape(getYNegatedCoordinates(tri), 3, true, clockwise, false, null));
		} else {
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
		}
	}

	public void drawClosedLine(final ICoordinates yNegatedVertices, final int number) {
		drawVertices(GL.GL_LINE_LOOP, yNegatedVertices, number, true);
	}

	public void drawClosedLine(final ICoordinates yNegatedVertices, final Color color, final int number) {
		if (color == null) { return; }
		final Color previous = swapCurrentColor(color);
		drawClosedLine(yNegatedVertices, number);
		setCurrentColor(previous);
	}

	public void drawLine(final ICoordinates yNegatedVertices, final int number) {
		final boolean previous = this.setLighting(false);
		drawVertices(GL.GL_LINE_STRIP, yNegatedVertices, number, true);
		this.setLighting(previous);
	}

	/**
	 * Outputs a single vertex to OpenGL, applying the z-translation to it and computing the maximum z outputted so far
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private void outputVertex(final double x, final double y, final double z) {
		gl.glVertex3d(x, y, z + currentZTranslation);
	}

	private void outputTexCoord(final double u, final double v) {
		gl.glTexCoord2d(u, v);
	}

	public void outputNormal(final double x, final double y, final double z) {
		currentNormal.setLocation(x, y, z);
		gl.glNormal3d(x, y, z);
	}

	public void drawVertex(final GamaPoint coords, final GamaPoint normal, final GamaPoint tex) {
		if (normal != null) {
			outputNormal(normal.x, normal.y, normal.z);
		}
		if (tex != null) {
			gl.glTexCoord3d(tex.x, tex.y, tex.z);
		}
		outputVertex(coords.x, coords.y, coords.z);
	}

	public void drawVertex(final int i, final double x, final double y, final double z) {
		if (isTextured()) {
			textureCoords.setLocation(x, y, z);
			currentTextureRotation.applyTo(textureCoords);
			final double u = 1 - (textureCoords.x - textureEnvelope.getMinX()) / textureEnvelope.getWidth();
			final double v = (textureCoords.y - textureEnvelope.getMinY()) / textureEnvelope.getHeight();
			outputTexCoord(u, v);
		}
		outputVertex(x, y, z);
	}

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
	 * Draw the vertices using the style provided (e.g. GL_QUADS, GL_LINE), the color provided (which will be reverted
	 * as soon as the draw has finished), a given number of vertices in this sequence, in the clockwise or CCW direction
	 * 
	 * @param style
	 * @param color
	 * @param yNegatedVertices
	 * @param number
	 * @param clockwise
	 */
	public void drawVertices(final int style, final Color color, final ICoordinates yNegatedVertices, final int number,
			final boolean clockwise) {
		final Color previous = swapCurrentColor(color);
		drawVertices(style, yNegatedVertices, number, clockwise);
		setCurrentColor(previous);
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

	public GamaPoint setNormal(final ICoordinates yNegatedVertices, final boolean clockwise) {
		yNegatedVertices.getNormal(clockwise, 1, currentNormal);
		outputNormal(currentNormal.x, currentNormal.y, currentNormal.z);
		if (isTextured()) {
			computeTextureCoordinates(yNegatedVertices, clockwise);
		}
		return currentNormal;
	}

	private void computeTextureCoordinates(final ICoordinates yNegatedVertices, final boolean clockwise) {
		workingVertices.setTo(yNegatedVertices);
		currentTextureRotation.rotateToHorizontal(currentNormal, workingVertices.directionBetweenLastPointAndOrigin(),
				clockwise);
		workingVertices.applyRotation(currentTextureRotation);
		workingVertices.getEnvelopeInto(textureEnvelope);
	}

	public void setCurrentColor(final Color c, final double alpha) {
		if (c == null) { return; }
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
		setCurrentColor(value, value, value, currentObjectAlpha);
	}

	public Color getCurrentColor() {
		return currentColor;
	}

	// LINE WIDTH

	public void setLineWidth(final double width) {
		gl.glLineWidth((float) width);
	}

	// ALPHA

	public final void setCurrentObjectAlpha(final double alpha) {
		currentObjectAlpha = alpha;
	}

	public double getCurrentObjectAlpha() {
		return currentObjectAlpha;
	}

	// ANTIALIAS

	public void setAntiAlias(final boolean aa) {
		antiAliasChanged = aa != isAntiAlias;
		isAntiAlias = aa;
	}

	public boolean isAntiAlias() {
		return isAntiAlias;
	}

	// TEXTURES

	/**
	 * Sets the id of the textures to enable. If the first is equal to NO_TEXTURE, all textures are disabled. If the
	 * second is equal to NO_TEXTURE, then the first one is also bound to the second unit.
	 * 
	 * @param t
	 *            the id of the texture to enable. Integer.MAX_VALUE means disabling textures
	 */
	public void setCurrentTextures(final int t0, final int t1) {
		primaryTexture = t0;
		alternateTexture = t1;
		textured = t0 != NO_TEXTURE;
		enablePrimaryTexture();
	}

	public void bindTexture(final int texture) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		// Apply antialas to the texture based on the current preferences
		if (antiAliasChanged) {
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
					isAntiAlias ? GL.GL_LINEAR_MIPMAP_LINEAR : GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, isAntiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
			antiAliasChanged = false;
		}
	}

	public void enablePrimaryTexture() {
		if (primaryTexture == NO_TEXTURE) { return; }
		bindTexture(primaryTexture);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	public void enableAlternateTexture() {
		if (alternateTexture == NO_TEXTURE) { return; }
		bindTexture(alternateTexture);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	public void disableTextures() {
		gl.glDisable(GL.GL_TEXTURE_2D);
		textured = false;
	}

	public void deleteVolatileTextures() {
		final Collection<TextureRenderer> textures = volatileTextures.asMap().values();
		for (final TextureRenderer t : textures) {
			t.dispose();
		}
		volatileTextures.invalidateAll();
	}

	public void deleteTexture(final Texture texture) {
		texture.destroy(gl);
	}

	public void cacheTexture(final File file) {
		if (file == null) { return; }
		initializeStaticTexture(file);
	}

	private void initializeStaticTexture(final File file) {
		if (!texturesToProcess.contains(file.getAbsolutePath())) {
			texturesToProcess.add(file.getAbsolutePath());
		}
	}

	public void processUnloadedTextures() {
		for (final String path : texturesToProcess) {
			getTextureRenderer(new File(path), false, true);
		}
	}

	public TextureRenderer getTextureRenderer(final GamaImageFile file, final boolean useCache) {
		return getTextureRenderer(file.getFile(null), file.isAnimated(), useCache);
	}

	public TextureRenderer getTextureRenderer(final BufferedImage img) {
		return volatileTextures.getUnchecked(img);
	}

	public TextureRenderer getTextureRenderer(final File file, final boolean isAnimated, final boolean useCache) {
		if (file == null) { return null; }
		TextureRenderer texture = null;
		if (isAnimated || !useCache) {
			final BufferedImage image = ImageUtils.getInstance().getImageFromFile(file, useCache, true);
			texture = getTextureRenderer(image);

		} else {
			try {
				texture = staticTextures.get(file.getAbsolutePath(), () -> buildTextureRenderer(gl, file));
			} catch (final ExecutionException e) {
				e.printStackTrace();
			}
		}
		return texture;
	}

	private TextureRenderer buildTextureRenderer(final GL gl, final File file) {
		try {
			final BufferedImage im = ImageUtils.getInstance().getImageFromFile(file, true, true);
			return buildTextureRenderer(gl, im);
		} catch (final GLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public TextureRenderer buildTextureRenderer(final OpenGL gl, final BufferedImage image) {
		return buildTextureRenderer(gl.getGL(), image);
	}

	public TextureRenderer buildTextureRenderer(final GL gl, final BufferedImage im) {
		try {
			final int width, height;
			if (isNonPowerOf2TexturesAvailable) {
				width = im.getWidth();
				height = im.getHeight();
			} else {
				width = getClosestPow(im.getWidth());
				height = getClosestPow(im.getHeight());
			}
			final TextureRenderer tr = new TextureRenderer(width, height, ImageUtils.checkTransparency(im), true);
			tr.setSmoothing(true);
			final Graphics2D g = tr.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.drawImage(im, 0, 0, width, height, null);
			return tr;
		} catch (final GLException e) {
			e.printStackTrace();
			return null;
		}
	}

	static boolean IsPowerOfTwo(final int x) {
		return (x & x - 1) == 0;
	}

	static int getClosestPow(final int value) {
		int power = 1;
		while (power < value) {
			power *= 2;
		}
		return power;
	}

	// GEOMETRIES

	public void cacheGeometry(final ResourceObject object) {
		if (geometryCache != null) {
			geometryCache.saveGeometryToProcess(object);
		}
	}

	public void processUnloadedGeometries() {
		if (geometryCache != null) {
			geometryCache.processUnloadedGeometries(this);
		}
		textRendererCache.processUnloadedFonts();
	}

	public Envelope3D getEnvelopeFor(final GamaGeometryFile file) {
		if (geometryCache != null) { return geometryCache.getEnvelope(file); }
		return null;
	}

	// TEXT

	public void cacheFont(final Font f) {
		textRendererCache.saveFontToProcess(f, f.getSize() * textSizeMultiplier);
	}

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
		if (!inRasterTextMode) {
			beginRasterTextMode();
		}
		final boolean previous = setLighting(false);
		gl.glRasterPos3d(x, y, z);
		glut.glutBitmapString(font, s);
		setLighting(previous);
		exitRasterTextMode();
	}

	/**
	 * Draws a sequence of strings at the given coords (where s.length = 3 * coords.length). Enters and exits raster
	 * mode before and after drawing the sequence
	 * 
	 * @param seq
	 *            the sequence of strings
	 * @param font
	 *            the font to draw with
	 * @param coords
	 *            the sequence of {x, y, z} coordinates
	 */
	public void rasterText(final String[] seq, final int font, final double[] coords) {
		if (!inRasterTextMode) {
			beginRasterTextMode();
		}
		final boolean previous = setLighting(false);
		for (int i = 0; i < seq.length; i++) {
			gl.glRasterPos3d(coords[i * 3], coords[i * 3 + 1], coords[i * 3 + 2] + currentZTranslation);
			glut.glutBitmapString(font, seq[i]);
		}
		setLighting(previous);
		exitRasterTextMode();
	}

	private void exitRasterTextMode() {
		gl.glEnable(GL.GL_BLEND);
		popMatrix();
		inRasterTextMode = false;
	}

	private void beginRasterTextMode() {
		pushMatrix();
		gl.glDisable(GL.GL_BLEND);
		inRasterTextMode = true;
	}

	public void drawFPS(final int fps) {
		setCurrentColor(Color.black);
		final String s = fps == 0 ? "(computing FPS...)" : fps + " FPS";
		rasterText(s, GLUT.BITMAP_HELVETICA_12, -5, 5, 0);
	}

	/**
	 * Draws a string in perspective in the current color, with the given font, at the given position
	 * 
	 * @param string
	 *            the string to draw
	 * @param font
	 *            the font to use
	 * @param x,y,z
	 *            the coordinates
	 * @param scale
	 *            the scale to apply
	 */
	public void perspectiveText(final String string, final Font font, final double x, final double y, final double z,
			final GamaPoint anchor) {
		final int fontSize = (int) (font.getSize() * textSizeMultiplier);
		final TextRenderer r = textRendererCache.get(font, fontSize);
		if (r == null) { return; }

		if (getCurrentColor() != null) {
			r.setColor(getCurrentColor());
		}
		r.begin3DRendering();
		final Rectangle2D bounds = r.getBounds(string);

		final float ratio = (float) ratios.y;
		final float scale = 1f / ratio / textSizeMultiplier;

		final double curX = x - bounds.getWidth() * scale * anchor.x;
		final double curY = y + bounds.getY() * scale * anchor.y;

		r.draw3D(string, (float) curX, (float) curY, (float) (z + currentZTranslation), scale);
		r.flush();
		r.end3DRendering();
	}

	public double getMaxWorldDim() {
		return worldX > worldY ? worldX : worldY;
	}

	public double getWorldWidth() {
		return worldX;
	}

	public double getWorldHeight() {
		return worldY;
	}

	public void setWireframe(final boolean wireframe) {
		isWireframe = wireframe;
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, !isWireframe ? GL2GL3.GL_FILL : GL2GL3.GL_LINE);
	}

	public boolean isWireframe() {
		return isWireframe;
	}

	// PICKING

	public void runWithNames(final Runnable r) {
		gl.glInitNames();
		gl.glPushName(0);
		r.run();
		gl.glPopName();
	}

	public void registerForSelection(final int index) {
		gl.glLoadName(index);
	}

	public void markIfSelected(final DrawingAttributes attributes) {
		pickingState.tryPick(attributes);
	}

	// LISTS

	public int compileAsList(final Runnable r) {
		final int index = gl.glGenLists(1);
		gl.glNewList(index, GL2.GL_COMPILE);
		r.run();
		gl.glEndList();
		return index;
	}

	public void drawList(final int i) {
		gl.glCallList(i);
	}

	public void deleteList(final Integer index) {
		gl.glDeleteLists(index, 1);
	}

	public void drawCachedGeometry(final GamaGeometryFile file, final Color border) {
		if (geometryCache == null) { return; }
		if (file == null) { return; }
		final Integer index = geometryCache.get(this, file);
		if (index != null) {
			drawList(index);
		}
		if (border != null && !isWireframe() && index != null) {
			final Color old = swapCurrentColor(border);
			getGL().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			drawList(index);
			setCurrentColor(old);
			getGL().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		}
	}

	public void drawCachedGeometry(final IShape.Type id, final boolean solid, final Color border) {
		if (geometryCache == null) { return; }
		if (id == null) { return; }
		final BuiltInGeometry object = geometryCache.get(this, id);
		if (object != null) {
			if (solid && !isWireframe()) {
				object.draw(this);
			}

			if (!solid || isWireframe() || border != null) {
				final Color old = swapCurrentColor(border != null ? border : getCurrentColor());
				getGL().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
				object.draw(this);
				setCurrentColor(old);
				getGL().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			}
		}
	}

	public void initializeShapeCache() {
		final int slices = GamaPreferences.Displays.DISPLAY_SLICE_NUMBER.getValue();
		final int stacks = slices;
		textured = true;
		geometryCache.put(SPHERE, BuiltInGeometry.assemble().faces(compileAsList(() -> {
			translateBy(0d, 0d, 1d);
			drawSphere(1.0, slices, stacks);
			translateBy(0, 0, -1d);
		})));
		geometryCache.put(CYLINDER, BuiltInGeometry.assemble().bottom(compileAsList(() -> {
			drawDisk(0d, 1d, slices, slices / 3);
		})).top(compileAsList(() -> {
			translateBy(0d, 0d, 1d);
			drawDisk(0d, 1d, slices, slices / 3);
			translateBy(0d, 0d, -1d);
		})).faces(compileAsList(() -> {
			drawCylinder(1.0d, 1.0d, 1.0d, slices, stacks);
		})));
		geometryCache.put(CONE, BuiltInGeometry.assemble().bottom(compileAsList(() -> {
			drawDisk(0d, 1d, slices, slices / 3);
		})).faces(compileAsList(() -> {
			drawCylinder(1.0, 0.0, 1.0, slices, stacks);
		})));
		final ICoordinates baseVertices = ICoordinates.ofLength(5);
		final ICoordinates faceVertices = ICoordinates.ofLength(5);
		baseVertices.setTo(-0.5, 0.5, 0, 0.5, 0.5, 0, 0.5, -0.5, 0, -0.5, -0.5, 0, -0.5, 0.5, 0);

		geometryCache.put(CUBE, BuiltInGeometry.assemble().bottom(compileAsList(() -> {
			drawSimpleShape(baseVertices, 4, true, false, true, null);
		})).top(compileAsList(() -> {
			baseVertices.translateBy(0, 0, 1);
			drawSimpleShape(baseVertices, 4, true, true, true, null);
			baseVertices.translateBy(0, 0, -1);
		})).faces(compileAsList(() -> {
			baseVertices.visit((pj, pk) -> {
				faceVertices.setTo(pk.x, pk.y, pk.z, pk.x, pk.y, pk.z + 1, pj.x, pj.y, pj.z + 1, pj.x, pj.y, pj.z, pk.x,
						pk.y, pk.z);
				drawSimpleShape(faceVertices, 4, true, true, true, null);
			});
		})));
		geometryCache.put(POINT, BuiltInGeometry.assemble().faces(compileAsList(() -> {
			drawSphere(1.0, 5, 5);
		})));

		geometryCache.put(IShape.Type.ROUNDED, BuiltInGeometry.assemble().bottom(compileAsList(() -> {
			drawRoundedRectangle();
		})));
		geometryCache.put(SQUARE, BuiltInGeometry.assemble().bottom(compileAsList(() -> {
			drawSimpleShape(baseVertices, 4, true, true, true, null);
		})));
		geometryCache.put(CIRCLE, BuiltInGeometry.assemble().bottom(compileAsList(() -> {
			drawDisk(0.0, 1.0, slices, 1);
		})));
		final ICoordinates triangleVertices = ICoordinates.ofLength(4);
		final ICoordinates vertices = ICoordinates.ofLength(5);
		vertices.setTo(-0.5, -0.5, 0, -0.5, 0.5, 0, 0.5, 0.5, 0, 0.5, -0.5, 0, -0.5, -0.5, 0);
		geometryCache.put(PYRAMID, BuiltInGeometry.assemble().bottom(compileAsList(() -> {
			drawSimpleShape(vertices, 4, true, false, true, null);
		})).faces(compileAsList(() -> {
			final GamaPoint top = new GamaPoint(0, 0, 1);
			vertices.visit((pj, pk) -> {
				triangleVertices.setTo(pj.x, pj.y, pj.z, top.x, top.y, top.z, pk.x, pk.y, pk.z, pj.x, pj.y, pj.z);
				drawSimpleShape(triangleVertices, 3, true, true, true, null);

			});
		})));
		textured = false;

	}

	public boolean isTextured() {
		return textured && !isWireframe;
	}

	// COMPLEX SHAPES

	private static final double PI_2 = 2f * Math.PI;

	static double roundRect[] = { .92, 0, .933892, .001215, .947362, .004825, .96, .010718, .971423, .018716, .981284,
			.028577, .989282, .04, .995175, .052638, .998785, .066108, 1, .08, 1, .92, .998785, .933892, .995175,
			.947362, .989282, .96, .981284, .971423, .971423, .981284, .96, .989282, .947362, .995175, .933892, .998785,
			.92, 1, .08, 1, .066108, .998785, .052638, .995175, .04, .989282, .028577, .981284, .018716, .971423,
			.010718, .96, .004825, .947362, .001215, .933892, 0, .92, 0, .08, .001215, .066108, .004825, .052638,
			.010718, .04, .018716, .028577, .028577, .018716, .04, .010718, .052638, .004825, .066108, .001215, .08,
			0 };

	static DoubleBuffer db = (DoubleBuffer) Buffers.newDirectDoubleBuffer(roundRect.length).put(roundRect).rewind();

	public void drawRoundedRectangle() {
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glVertexPointer(2, GL2.GL_DOUBLE, 0, db);
		gl.glDrawArrays(GL2.GL_TRIANGLE_FAN, 0, 40);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
	}

	public void drawDisk(final double inner, final double outer, final int slices, final int loops) {
		double da, dr;
		/* Normal vectors */
		outputNormal(0.0, 0.0, +1.0);
		da = PI_2 / slices;
		dr = (outer - inner) / loops;

		final double dtc = 2.0f * outer;
		double sa, ca;
		double r1 = inner;
		int l;
		gl.glFrontFace(GL.GL_CCW);
		for (l = 0; l < loops; l++) {
			final double r2 = r1 + dr;
			int s;
			beginDrawing(GL2.GL_QUAD_STRIP);
			for (s = 0; s <= slices; s++) {
				double a;
				if (s == slices) {
					a = 0.0f;
				} else {
					a = s * da;
				}
				sa = Math.sin(a);
				ca = Math.cos(a);
				outputTexCoord(0.5f + sa * r2 / dtc, 0.5f + ca * r2 / dtc);
				outputVertex(r2 * sa, r2 * ca, 0);
				outputTexCoord(0.5f + sa * r1 / dtc, 0.5f + ca * r1 / dtc);
				outputVertex(r1 * sa, r1 * ca, 0);
			}
			endDrawing();
			r1 = r2;
		}
		gl.glFrontFace(GL.GL_CW);
	}

	public void drawSphere(final double radius, final int slices, final int stacks) {
		double rho, drho, theta, dtheta;
		double x, y, z;
		double s, t, ds, dt;
		int i, j, imin, imax;
		drho = Math.PI / stacks;
		dtheta = PI_2 / slices;
		ds = 1.0f / slices;
		dt = 1.0f / stacks;
		t = 1.0f; // because loop now runs from 0
		imin = 0;
		imax = stacks;
		gl.glFrontFace(GL.GL_CCW);
		// draw intermediate stacks as quad strips
		for (i = imin; i < imax; i++) {
			rho = i * drho;
			beginDrawing(GL2.GL_QUAD_STRIP);
			s = 0.0f;
			for (j = 0; j <= slices; j++) {
				theta = j == slices ? 0.0f : j * dtheta;
				x = -Math.sin(theta) * Math.sin(rho);
				y = Math.cos(theta) * Math.sin(rho);
				z = Math.cos(rho);
				outputNormal(x, y, z);
				outputTexCoord(s, t);
				outputVertex(x * radius, y * radius, z * radius);
				x = -Math.sin(theta) * Math.sin(rho + drho);
				y = Math.cos(theta) * Math.sin(rho + drho);
				z = Math.cos(rho + drho);
				outputNormal(x, y, z);
				outputTexCoord(s, t - dt);
				s += ds;
				outputVertex(x * radius, y * radius, z * radius);
			}
			endDrawing();
			t -= dt;
		}
		gl.glFrontFace(GL.GL_CW);
	}

	public void drawCylinder(final double base, final double top, final double height, final int slices,
			final int stacks) {

		double da, r, dr, dz;
		double x, y, z, nz;
		int i, j;
		da = PI_2 / slices;
		dr = (top - base) / stacks;
		dz = height / stacks;
		nz = (base - top) / height;

		final double ds = 1.0f / slices;
		final double dt = 1.0f / stacks;
		float t = 0.0f;
		z = 0.0f;
		r = base;
		gl.glFrontFace(GL.GL_CCW);
		for (j = 0; j < stacks; j++) {
			float s = 0.0f;
			beginDrawing(GL2.GL_QUAD_STRIP);
			for (i = 0; i <= slices; i++) {
				if (i == slices) {
					x = Math.sin(0.0f);
					y = Math.cos(0.0f);
				} else {
					x = Math.sin(i * da);
					y = Math.cos(i * da);
				}
				outputNormal(x, y, nz);
				outputTexCoord(s, t);
				outputVertex(x * r, y * r, z);
				outputNormal(x, y, nz);
				outputTexCoord(s, t + dt);
				outputVertex(x * (r + dr), y * (r + dr), z + dz);

				s += ds;
			} // for slices
			endDrawing();
			r += dr;
			t += dt;
			z += dz;
		} // for stacks
		gl.glFrontFace(GL.GL_CW);
	}

	public void beginObject(final AbstractObject object) {
		setWireframe(isWireframe);
		setLineWidth(object.getLineWidth());
		setCurrentTextures(object.getPrimaryTexture(this), object.getAlternateTexture(this));
		setCurrentColor(object.getColor());
		if (object.isFilled() && !object.isSynthetic()) {
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
		}

	}

	public void endObject(final AbstractObject object) {
		disableTextures();
		translateByZIncrement();
		if (object.isFilled() && !object.isSynthetic()) {
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		}

	}

	public void beginScene() {
		setAntiAlias(isAntiAlias);
		setWireframe(data.isWireframe());
		processUnloadedTextures();
		processUnloadedGeometries();
		final Color backgroundColor = data.getBackgroundColor();
		gl.glClearColor(backgroundColor.getRed() / 255.0f, backgroundColor.getGreen() / 255.0f,
				backgroundColor.getBlue() / 255.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		gl.glClearDepth(1.0f);
		setLighting(data.isLightOn());
		resetMatrix(GL2.GL_PROJECTION);
		updatePerspective();
		resetMatrix(GL2.GL_MODELVIEW);
		rotateModel();
	}

	private boolean isContinuousRotationActive() {
		return data.isContinuousRotationOn() && !data.cameraInteractionDisabled();
	}

	public void rotateModel() {
		if (isContinuousRotationActive()) {
			data.incrementZRotation();
		}
		if (data.getCurrentRotationAboutZ() != 0d) {
			final double env_width = getWorldWidth();
			final double env_height = getWorldHeight();
			translateBy(env_width / 2, -env_height / 2, 0d);
			rotateBy(data.getCurrentRotationAboutZ(), 0, 0, 1);
			translateBy(-env_width / 2, +env_height / 2, 0d);
		}
	}

	public void endScene() {
		gl.glFlush();
	}

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
		// gl.glDisable(GL.GL_LINE_SMOOTH);
		// Enabling forced normalization of normal vectors (important)
		gl.glEnable(GL2.GL_NORMALIZE);
		// Enabling multi-sampling (necessary ?)
		// if (USE_MULTI_SAMPLE) {
		gl.glEnable(GL2.GL_MULTISAMPLE);
		gl.glHint(GL2.GL_MULTISAMPLE_FILTER_HINT_NV, GL2.GL_NICEST);
		// }
		initializeShapeCache();
	}

	public GamaPoint getRatios() {
		return ratios;
	}

}
