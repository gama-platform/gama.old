/*********************************************************************************************
 *
 * 'Abstract3DRenderer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
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
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.display.AbstractDisplayGraphics;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.camera.CameraArcBall;
import ummisco.gama.opengl.camera.FreeFlyCamera;
import ummisco.gama.opengl.camera.ICamera;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.ModelScene;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.opengl.scene.SceneBuffer;
import ummisco.gama.opengl.vaoGenerator.DrawingEntityGenerator;
import ummisco.gama.opengl.vaoGenerator.ShapeCache;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * This class plays the role of Renderer and IGraphics. Class Abstract3DRenderer.
 *
 * @author drogoul
 * @since 27 avr. 2015
 *
 */
public abstract class Abstract3DRenderer extends AbstractDisplayGraphics implements GLEventListener {

	public class PickingState {

		final static int NONE = -2;
		final static int WORLD = -1;

		volatile boolean isPicking;
		volatile boolean isMenuOn;
		volatile int pickedIndex = NONE;

		public void setPicking(final boolean isPicking) {
			this.isPicking = isPicking;
			if (!isPicking) {
				setPickedIndex(NONE);
				setMenuOn(false);
			}
		}

		public void setMenuOn(final boolean isMenuOn) {
			this.isMenuOn = isMenuOn;
		}

		public void setPickedIndex(final int pickedIndex) {
			this.pickedIndex = pickedIndex;
			if (pickedIndex == WORLD && !isMenuOn) {
				// Selection occured, but no object have been selected
				setMenuOn(true);
				getSurface().selectAgent(null);
			}
		}

		public boolean isPicked(final int objectIndex) {
			return pickedIndex == objectIndex;
		}

		public boolean isBeginningPicking() {
			return isPicking && pickedIndex == NONE;
		}

		public boolean isMenuOn() {
			return isMenuOn;
		}

		public boolean isPicking() {
			return isPicking;
		}

	}

	public final static boolean DRAW_NORM = false;
	protected DrawingEntityGenerator drawingEntityGenerator;
	protected final PickingState pickingState = new PickingState();
	public SceneBuffer sceneBuffer;
	protected ModelScene currentScene;
	protected GLCanvas canvas;
	public ICamera camera;
	protected volatile boolean inited;
	protected volatile boolean visible;
	protected double currentZRotation = 0;
	protected double currentObjectAlpha = 1d;
	int[] viewport = new int[4];
	double mvmatrix[] = new double[16];
	double projmatrix[] = new double[16];
	public boolean colorPicking = false;
	protected GLUT glut;
	protected GLU glu;
	protected GL2 gl;
	protected final GamaPoint worldDimensions;
	protected Envelope3D ROIEnvelope = null;
	// relative to rotation helper
	protected boolean drawRotationHelper = false;
	protected GamaPoint rotationHelperPosition = null;
	// relative to keystone
	protected boolean drawKeystoneHelper = false;

	public boolean drawKeystoneHelper() {
		return drawKeystoneHelper;
	}

	protected float[][] keystoneCoordinates;

	public float[][] getKeystoneCoordinates() {
		return keystoneCoordinates;
	}

	public void setKeystoneCoordinates(final int cornerId, final float[] coordinates) {
		keystoneCoordinates[cornerId] = coordinates;
	}

	protected int cornerSelected = -1;

	public int getCornerSelected() {
		return cornerSelected;
	}

	public void startDrawKeystoneHelper() {
		drawKeystoneHelper = true;
		cornerSelected = -1;
	}

	public void stopDrawKeystoneHelper() {
		drawKeystoneHelper = false;
	}

	// CACHES FOR TEXTURES, FONTS AND GEOMETRIES

	protected final GeometryCache geometryCache = new GeometryCache();
	protected final TextRenderersCache textRendererCache = new TextRenderersCache();
	protected final TextureCache textureCache = GamaPreferences.OpenGL.DISPLAY_SHARED_CONTEXT.getValue()
			? TextureCache.getSharedInstance() : new TextureCache();

	public static Boolean isNonPowerOf2TexturesAvailable = false;
	protected static Map<String, Envelope> ENVELOPES_CACHE = new ConcurrentHashMap<>();
	protected final IntBuffer selectBuffer = Buffers.newDirectIntBuffer(1024);

	public Abstract3DRenderer(final SWTOpenGLDisplaySurface d) {
		super(d);
		worldDimensions = new GamaPoint(data.getEnvWidth(), data.getEnvHeight());
		camera = new CameraArcBall(this);
		sceneBuffer = new SceneBuffer(this);
		ShapeCache.freedShapeCache();
	}

	@SuppressWarnings ("unused")
	public GLAutoDrawable createDrawable(final Composite parent) {
		final boolean useSharedContext = GamaPreferences.OpenGL.DISPLAY_SHARED_CONTEXT.getValue();
		final GLProfile profile =
				useSharedContext ? TextureCache.getSharedContext().getGLProfile() : GLProfile.getDefault();
		final GLCapabilities cap = new GLCapabilities(profile);
		cap.setStencilBits(8);
		cap.setDoubleBuffered(true);
		cap.setHardwareAccelerated(true);
		cap.setSampleBuffers(true);
		cap.setAlphaBits(4);
		cap.setNumSamples(4);
		canvas = new GLCanvas(parent, SWT.NONE, cap, null);
		if (useSharedContext) {
			canvas.setSharedAutoDrawable(TextureCache.getSharedContext());
		}
		canvas.setAutoSwapBufferMode(true);
		new SWTGLAnimator(canvas);
		canvas.addGLEventListener(this);
		final FillLayout gl = new FillLayout();
		canvas.setLayout(gl);
		return canvas;
	}

	protected void commonInit(final GLAutoDrawable drawable) {
		WorkbenchHelper.run(() -> getCanvas().setVisible(visible));
		// the drawingEntityGenerator is used only when there is a webgl display
		// and/or a modernRenderer.
		drawingEntityGenerator = new DrawingEntityGenerator(this);

		glu = new GLU();
		glut = new GLUT();
		currentZRotation = data.getZRotation();
		gl = drawable.getGL().getGL2();
		final Color background = data.getBackgroundColor();
		gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f, background.getBlue() / 255.0f,
				1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		isNonPowerOf2TexturesAvailable = gl.isNPOTTextureAvailable();

		initializeCanvasListeners();
		updateCameraPosition();
		updatePerspective();

		setUpKeystoneCoordinates();
	}

	public final ModelScene getCurrentScene() {
		return currentScene;
	}

	public abstract Integer getGeometryListFor(final GL2 gl, final GamaGeometryFile file);

	public abstract TextRenderer getTextRendererFor(final Font font);

	public final GLCanvas getCanvas() {
		return canvas;
	}

	public final GLAutoDrawable getDrawable() {
		return canvas;
	}

	protected void initializeCanvasListeners() {

		WorkbenchHelper.asyncRun(() -> {
			if (getCanvas() == null || getCanvas().isDisposed()) { return; }
			getCanvas().addKeyListener(camera);
			getCanvas().addMouseListener(camera);
			getCanvas().addMouseMoveListener(camera);
			getCanvas().addMouseWheelListener(camera);
			getCanvas().addMouseTrackListener(camera);

		});

	}

	public final double getMaxEnvDim() {
		// built dynamically to prepare for the changes in size of the
		// environment
		final double env_width = worldDimensions.x;
		final double env_height = worldDimensions.y;
		return env_width > env_height ? env_width : env_height;
	}

	public final double getEnvWidth() {
		return worldDimensions.x;
	}

	public final double getEnvHeight() {
		return worldDimensions.y;
	}

	public DrawingEntityGenerator getDrawingEntityGenerator() {
		return drawingEntityGenerator;
	}

	public final void switchCamera() {
		final ICamera oldCamera = camera;
		WorkbenchHelper.asyncRun(() -> {
			getCanvas().removeKeyListener(oldCamera);
			getCanvas().removeMouseListener(oldCamera);
			getCanvas().removeMouseMoveListener(oldCamera);
			getCanvas().removeMouseWheelListener(oldCamera);
			getCanvas().removeMouseTrackListener(oldCamera);
		});

		if (!data.isArcBallCamera()) {
			camera = new FreeFlyCamera(this);
		} else {
			camera = new CameraArcBall(this);
		}

		initializeCanvasListeners();

	}

	public final double getWidth() {
		return getDrawable().getSurfaceWidth() * surface.getZoomLevel();
	}

	public final double getHeight() {
		return getDrawable().getSurfaceHeight() * surface.getZoomLevel();
	}

	public final void updateCameraPosition() {
		camera.update();
	}

	protected abstract void updatePerspective();

	@Override
	public void fillBackground(final Color bgColor, final double opacity) {
		setCurrentObjectAlpha(opacity);
	}

	/**
	 * Method getDisplayWidthInPixels()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#getDisplayWidthInPixels()
	 */
	@Override
	public final int getDisplayWidth() {
		return (int) Math.round(getWidth());
	}

	/**
	 * Method getDisplayHeightInPixels()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#getDisplayHeightInPixels()
	 */
	@Override
	public final int getDisplayHeight() {
		return (int) Math.round(getHeight());
	}

	public final GLU getGlu() {
		return glu;
	}

	public final GLUT getGlut() {
		return glut;
	}

	public final GamaPoint getIntWorldPointFromWindowPoint(final Point windowPoint) {
		final GamaPoint p = getRealWorldPointFromWindowPoint(windowPoint);
		return new GamaPoint((int) p.x, (int) p.y);
	}

	public abstract GamaPoint getRealWorldPointFromWindowPoint(final Point windowPoint);

	/**
	 * Method getZoomLevel()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#getZoomLevel()
	 */
	@Override
	public final Double getZoomLevel() {
		return data.getZoomLevel();
	}

	/**
	 * Useful for drawing fonts
	 * 
	 * @return
	 */
	public final double getGlobalYRatioBetweenPixelsAndModelUnits() {
		return getHeight() / data.getEnvHeight();
	}

	/**
	 * Method is2D()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#is2D()
	 */
	@Override
	public final boolean is2D() {
		return false;
	}

	/**
	 * @param path
	 * @return
	 */
	public final static Envelope getEnvelopeFor(final String path) {
		return ENVELOPES_CACHE.get(path);
	}

	/**
	 * @return
	 */
	public static float getLineWidth() {
		return GamaPreferences.OpenGL.CORE_LINE_WIDTH.getValue().floatValue();
	}

	@Override
	public final SWTOpenGLDisplaySurface getSurface() {
		return (SWTOpenGLDisplaySurface) surface;
	}

	@Override
	public final ILocation getCameraPos() {
		return camera.getPosition();
	}

	@Override
	public final ILocation getCameraTarget() {
		return camera.getTarget();
	}

	@Override
	public final ILocation getCameraOrientation() {
		return camera.getOrientation();
	}

	public boolean useShader() {
		return false;
	}

	public boolean preloadTextures() {
		return true;
	}

	public TextureCache getSharedTextureCache() {
		return textureCache;
	}

	public abstract boolean mouseInROI(final Point mousePosition);

	// END HELPERS

	@SuppressWarnings ("rawtypes")
	public ObjectDrawer getDrawerFor(final AbstractObject.DrawerType type) {
		return null;
	}

	public double getZRotation() {
		return currentZRotation;
	}

	public boolean isDrawRotationHelper() {
		return drawRotationHelper;
	}

	public GamaPoint getRotationHelperPosition() {
		return rotationHelperPosition;
	}

	public void setUpKeystoneCoordinates() {
		keystoneCoordinates = new float[4][2];
		float[] coords1 = new float[] { 0, 1 }; // bottom-left
		float[] coords2 = new float[] { 0, 0 }; // top-left
		float[] coords3 = new float[] { 1, 0 }; // top-right
		float[] coords4 = new float[] { 1, 1 }; // bottom-right
		if (data.getKeystone() != null) {
			coords1 =
					new float[] { (float) data.getKeystone().get(2).getX(), (float) data.getKeystone().get(2).getY() };
			coords2 =
					new float[] { (float) data.getKeystone().get(0).getX(), (float) data.getKeystone().get(0).getY() };
			coords3 =
					new float[] { (float) data.getKeystone().get(1).getX(), (float) data.getKeystone().get(1).getY() };
			coords4 =
					new float[] { (float) data.getKeystone().get(3).getX(), (float) data.getKeystone().get(3).getY() };
		}
		setKeystoneCoordinates(0, coords1);
		setKeystoneCoordinates(1, coords2);
		setKeystoneCoordinates(2, coords3);
		setKeystoneCoordinates(3, coords4);
	}

	public void cornerSelected(final int cornerId) {
		cornerSelected = cornerId;
	}

	public final void setCurrentObjectAlpha(final double alpha) {
		currentObjectAlpha = alpha;
	}

	public double getCurrentObjectAlpha() {
		return currentObjectAlpha;
	}

	public GamaPoint getWorldsDimensions() {
		return worldDimensions;
	}

	/**
	 * Method drawGeometry. Add a given JTS Geometry in the list of all the existing geometry that will be displayed by
	 * openGl.
	 */
	@Override
	public Rectangle2D drawShape(final IShape shape, final ShapeDrawingAttributes attributes) {
		if (shape == null) { return null; }
		if (sceneBuffer.getSceneToUpdate() == null) { return null; }
		tryToHighlight(attributes);
		preloadTextures(attributes);
		sceneBuffer.getSceneToUpdate().addGeometry(shape.getInnerGeometry(), attributes);
		return rect;
	}

	/**
	 * Method drawImage.
	 *
	 * @param img
	 *            Image
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawImage(final BufferedImage img, final FileDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) { return null; }
		sceneBuffer.getSceneToUpdate().addImage(img, attributes);
		tryToHighlight(attributes);
		if (attributes.getBorder() != null) {
			drawGridLine(new GamaPoint(img.getWidth(), img.getHeight()), attributes.getBorder());
		}
		return rect;
	}

	protected void tryToHighlight(final FileDrawingAttributes attributes) {
		if (highlight) {
			attributes.setHighlighted(data.getHighlightColor());
		}
	}

	public void drawGridLine(final GamaPoint dimensions, final Color lineColor) {
		if (sceneBuffer.getSceneToUpdate() == null) { return; }
		double stepX, stepY;
		final double cellWidth = worldDimensions.x / dimensions.x;
		final double cellHeight = worldDimensions.y / dimensions.y;
		final GamaColor color = GamaColor.getInt(lineColor.getRGB());
		final ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(null, color, color, IShape.Type.GRIDLINE);
		for (double i = 0; i < dimensions.x; i++) {
			for (double j = 0; j < dimensions.y; j++) {
				stepX = i + 0.5;
				stepY = j + 0.5;
				final Geometry g = GamaGeometryType
						.buildRectangle(cellWidth, cellHeight, new GamaPoint(stepX * cellWidth, stepY * cellHeight))
						.getInnerGeometry();
				sceneBuffer.getSceneToUpdate().addGeometry(g, attributes);
			}
		}
	}

	@Override
	public Rectangle2D drawString(final String string, final TextDrawingAttributes attributes) {
		// Multiline: Issue #780
		if (string.contains("\n")) {
			for (final String s : string.split("\n")) {
				attributes.getLocation().setY(attributes.getLocation().getY()
						+ attributes.font.getSize() * this.getyRatioBetweenPixelsAndModelUnits());
				drawString(s, attributes);
			}
			return null;
		}
		attributes.getLocation().setY(-attributes.getLocation().getY());
		sceneBuffer.getSceneToUpdate().addString(string, attributes);
		return null;
	}

	protected void preloadTextures(final DrawingAttributes attributes) {
		if (!preloadTextures())
			return;
		final List<?> textures = attributes.getTextures();
		if (textures != null && !textures.isEmpty()) {
			for (final Object img : textures) {
				if (img instanceof GamaImageFile) {
					textureCache.initializeStaticTexture(getSurface().getScope(), (GamaImageFile) img);
				}
			}
		}
	}

	public void startDrawRotationHelper(final GamaPoint pos) {
		rotationHelperPosition = pos;
		drawRotationHelper = true;
		final double distance = Math.sqrt(Math.pow(camera.getPosition().x - rotationHelperPosition.x, 2)
				+ Math.pow(camera.getPosition().y - rotationHelperPosition.y, 2)
				+ Math.pow(camera.getPosition().z - rotationHelperPosition.z, 2));
		final double size = distance / 10; // the size of the displayed axis
		if (currentScene != null)
			currentScene.startDrawRotationHelper(pos, size);
	}

	public void stopDrawRotationHelper() {
		rotationHelperPosition = null;
		drawRotationHelper = false;
		if (currentScene != null)
			currentScene.stopDrawRotationHelper();
	}

	public void defineROI(final Point start, final Point end) {
		final GamaPoint startInWorld = getRealWorldPointFromWindowPoint(start);
		final GamaPoint endInWorld = getRealWorldPointFromWindowPoint(end);
		ROIEnvelope = new Envelope3D(new Envelope(startInWorld.x, endInWorld.x, startInWorld.y, endInWorld.y));
	}

	public void cancelROI() {
		if (camera.isROISticky())
			return;
		ROIEnvelope = null;
	}

	public PickingState getPickingState() {
		return pickingState;
	}

	public Envelope3D getROIEnvelope() {
		return ROIEnvelope;
	}

	public void drawScene(final GL2 gl) {
		currentScene = sceneBuffer.getSceneToRender();
		if (currentScene == null) { return; }
		// Do some garbage collecting in model scenes
		sceneBuffer.garbageCollect(gl);
		// if picking, we draw a first pass to pick the color
		if (pickingState.isBeginningPicking()) {
			beginPicking(gl);
			currentScene.draw(gl);
			endPicking(gl);
		}
		// we draw the scene on screen
		currentScene.draw(gl);
	}

	// This method is normally called either when the graphics is created or
	// when the output is changed
	// @Override
	public void initScene() {
		if (sceneBuffer != null) {
			final ModelScene scene = sceneBuffer.getSceneToRender();
			if (scene != null) {
				scene.reload();
			}
		}
	}

	@Override
	public boolean beginDrawingLayers() {
		while (!inited) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				return false;
			}
		}
		return sceneBuffer.beginUpdatingScene();

	}

	@Override
	public void dispose() {
		super.dispose();
		dispose(getDrawable());
	}

	@Override
	public void beginDrawingLayer(final ILayer layer) {
		super.beginDrawingLayer(layer);
		GamaPoint currentOffset, currentScale;
		if (!(layer instanceof OverlayLayer)) {
			final double currentZLayer = getMaxEnvDim() * layer.getPosition().getZ();

			// get the value of the z scale if positive otherwise set it to 1.
			double z_scale;
			if (layer.getExtent().getZ() > 0) {
				z_scale = layer.getExtent().getZ();
			} else {
				z_scale = 1;
			}

			currentOffset = new GamaPoint(getXOffsetInPixels() / (getWidth() / worldDimensions.x),
					getYOffsetInPixels() / (getHeight() / worldDimensions.y), currentZLayer);
			currentScale = new GamaPoint(getLayerWidth() / getWidth(), getLayerHeight() / getHeight(), z_scale);
		} else {
			currentOffset = new GamaPoint(getXOffsetInPixels() / (getWidth() / worldDimensions.x),
					getYOffsetInPixels() / (getHeight() / worldDimensions.y), 1);

			currentScale = new GamaPoint(getLayerWidth() / getWidth(), getLayerHeight() / getHeight(), 1);

		}
		final ModelScene scene = sceneBuffer.getSceneToUpdate();
		if (scene != null) {
			scene.beginDrawingLayer(layer, currentOffset, currentScale, currentLayerAlpha);
		}
	}

	/**
	 * Method endDrawingLayers()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#endDrawingLayers()
	 */
	@Override
	public void endDrawingLayers() {
		sceneBuffer.endUpdatingScene();
		getSurface().invalidateVisibleRegions();
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	public abstract void beginPicking(final GL2 gl);

	public abstract void endPicking(final GL2 gl);

}
