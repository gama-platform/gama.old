/*********************************************************************************************
 *
 * 'JOGLRenderer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.renderer;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.swt.GLCanvas;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.display.AbstractDisplayGraphics;
import msi.gama.outputs.layers.charts.ChartOutput;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.helpers.CameraHelper;
import ummisco.gama.opengl.renderer.helpers.KeystoneHelper;
import ummisco.gama.opengl.renderer.helpers.LightHelper;
import ummisco.gama.opengl.renderer.helpers.PickingHelper;
import ummisco.gama.opengl.renderer.helpers.ROIHelper;
import ummisco.gama.opengl.renderer.helpers.RotationHelper;
import ummisco.gama.opengl.renderer.helpers.SceneHelper;
import ummisco.gama.opengl.scene.ModelScene;
import ummisco.gama.opengl.scene.ResourceObject;
import ummisco.gama.opengl.view.SWTOpenGLDisplaySurface;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * This class plays the role of Renderer and IGraphics. Class JOGLRenderer.
 *
 * @author drogoul
 * @since 27 avr. 2015
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class JOGLRenderer extends AbstractDisplayGraphics implements IOpenGLRenderer {

	// Helpers
	private final KeystoneHelper keystoneHelper = createKeystoneHelper();
	private final PickingHelper pickingHelper = new PickingHelper(this);
	private final LightHelper lightHelper = new LightHelper(this);
	private final CameraHelper cameraHelper = new CameraHelper(this);
	private final ROIHelper roiHelper = new ROIHelper(this);
	private final RotationHelper rotationHelper = new RotationHelper(this);
	private final SceneHelper sceneHelper = createSceneHelper();

	// OpenGL back-end
	protected OpenGL openGL;

	// State
	protected volatile boolean inited, visible;

	// Canvas
	protected GLCanvas canvas;

	@Override
	public void setDisplaySurface(final IDisplaySurface d) {
		super.setDisplaySurface(d);
		d.getScope().setGraphics(this);
		openGL = new OpenGL(this);
	}

	protected SceneHelper createSceneHelper() {
		return new SceneHelper(this);
	}

	protected KeystoneHelper createKeystoneHelper() {
		return new KeystoneHelper(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#setCanvas(com.jogamp.opengl.swt.GLCanvas)
	 */
	@Override
	public void setCanvas(final GLCanvas canvas) {
		this.canvas = canvas;
		canvas.addGLEventListener(this);
		cameraHelper.hook();
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
		WorkbenchHelper.asyncRun(() -> canvas.setVisible(visible));
		openGL.setGL2(drawable.getGL().getGL2());
		cameraHelper.initialize();
		openGL.initializeGLStates(data.getBackgroundColor());
		lightHelper.initialize();
		// We mark the renderer as inited
		inited = true;
	}

	@Override
	public void fillBackground(final Color bgColor, final double opacity) {
		openGL.setCurrentObjectAlpha(opacity);
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

	@Override
	public SWTOpenGLDisplaySurface getSurface() {
		return (SWTOpenGLDisplaySurface) surface;
	}

	@Override
	public final GLCanvas getCanvas() {
		return canvas;
	}

	// This method is normally called either when the graphics is created or
	// when the output is changed
	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#initScene()
	 */
	@Override
	public void initScene() {
		final ModelScene scene = sceneHelper.getSceneToRender();
		if (scene != null) {
			scene.reload();
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
		return sceneHelper.beginUpdatingScene();

	}

	@Override
	public boolean isNotReadyToUpdate() {
		if (data.isSynchronized()) { return false; }
		return sceneHelper.isNotReadyToUpdate();
	}

	@Override
	public void dispose() {
		super.dispose();
		dispose(canvas);
	}

	@Override
	public void beginDrawingLayer(final ILayer layer) {
		super.beginDrawingLayer(layer);
		sceneHelper.beginDrawingLayer(layer, currentLayerAlpha);
	}

	/**
	 * Method endDrawingLayers()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#endDrawingLayers()
	 */
	@Override
	public void endDrawingLayers() {
		sceneHelper.endUpdatingScene();
		getSurface().invalidateVisibleRegions();
	}

	@Override
	public void display(final GLAutoDrawable canvas) {
		if (!sceneHelper.isReady()) { return; }

		if (keystoneHelper.isActive()) {
			keystoneHelper.beginRenderToTexture();
		}

		cameraHelper.update();

		openGL.beginScene();

		if (lightHelper.isActive()) {
			lightHelper.draw();
		}

		sceneHelper.draw();

		openGL.disableTextures();
		openGL.setLighting(false);

		if (data.isShowfps()) {
			openGL.drawFPS((int) canvas.getAnimator().getLastFPS());
		}

		if (roiHelper.isActive()) {
			openGL.getGeometryDrawer().drawROIHelper(roiHelper.getROIEnvelope());
		}
		if (rotationHelper.isActive()) {
			rotationHelper.draw();
		}

		if (keystoneHelper.isActive()) {
			keystoneHelper.finishRenderToTexture();
		}
		openGL.endScene();

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
		final GL2 gl = drawable.getContext().getGL().getGL2();
		keystoneHelper.reshape(width, height);
		openGL.reshape(gl, width, height);
		// cameraHelper.animate();
		surface.updateDisplay(true);
	}

	@Override
	public void dispose(final GLAutoDrawable drawable) {
		sceneHelper.garbageCollect(openGL);
		sceneHelper.dispose();
		openGL.dispose();
		keystoneHelper.dispose();
		cameraHelper.dispose();
		drawable.removeGLEventListener(this);
	}

	/**
	 * 
	 * IGraphics DRAWING METHODS
	 * 
	 */

	@Override
	public boolean cannotDraw() {
		return sceneHelper.getSceneToUpdate() != null && sceneHelper.getSceneToUpdate().cannotAdd();
	}

	@Override
	public Rectangle2D drawFile(final GamaFile file, final FileDrawingAttributes attributes) {
		if (sceneHelper.getSceneToUpdate() == null) { return null; }
		tryToHighlight(attributes);
		if (file instanceof GamaGeometryFile) {
			final ResourceObject object =
					sceneHelper.getSceneToUpdate().addGeometryFile((GamaGeometryFile) file, attributes);
			if (object != null) {
				openGL.cacheGeometry(object);
			}
		} else if (file instanceof GamaImageFile) {
			if (attributes.useCache()) {
				openGL.cacheTexture(file.getFile(getSurface().getScope()));
			}
			sceneHelper.getSceneToUpdate().addImageFile((GamaImageFile) file, attributes);
		}

		return rect;
	}

	@Override
	public Rectangle2D drawField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		if (sceneHelper.getSceneToUpdate() == null) { return null; }
		final List<?> textures = attributes.getTextures();
		if (textures != null && !textures.isEmpty()) {
			for (final Object img : textures) {
				if (img instanceof GamaImageFile) {
					openGL.cacheTexture(((GamaImageFile) img).getFile(getSurface().getScope()));
				}
			}
		}
		sceneHelper.getSceneToUpdate().addField(fieldValues, attributes);
		/*
		 * This line has been removed to fix the issue 1174 if ( gridColor != null ) { drawGridLine(img, gridColor); }
		 */
		return rect;
	}

	/**
	 * Method drawShape. Add a given JTS Geometry in the list of all the existing geometry that will be displayed by
	 * openGl.
	 */
	@Override
	public Rectangle2D drawShape(final Geometry shape, final ShapeDrawingAttributes attributes) {
		if (shape == null) { return null; }
		if (sceneHelper.getSceneToUpdate() == null) { return null; }
		tryToHighlight(attributes);
		sceneHelper.getSceneToUpdate().addGeometry(shape, attributes);
		return rect;
	}

	@Override
	public Rectangle2D drawImage(final BufferedImage img, final FileDrawingAttributes attributes) {
		if (sceneHelper.getSceneToUpdate() == null) { return null; }
		sceneHelper.getSceneToUpdate().addImage(img, attributes);
		tryToHighlight(attributes);
		if (attributes.getBorder() != null) {
			drawGridLine(new GamaPoint(img.getWidth(), img.getHeight()), attributes.getBorder());
		}
		return rect;
	}

	@Override
	public Rectangle2D drawChart(final ChartOutput chart) {
		if (sceneHelper.getSceneToUpdate() == null) { return null; }
		int x = getLayerWidth();
		int y = getLayerHeight();
		x = (int) (Math.min(x, y) * 0.80);
		y = x;
		// TODO See if it not possible to generate directly a texture renderer instead
		final BufferedImage im = chart.getImage(x, y, getSurface().getData().isAntialias());
		sceneHelper.getSceneToUpdate().addImage(im, new FileDrawingAttributes(null, true));
		return rect;
	}

	protected void tryToHighlight(final FileDrawingAttributes attributes) {
		if (highlight) {
			attributes.setHighlighted(data.getHighlightColor());
		}
	}

	public void drawGridLine(final GamaPoint dimensions, final Color lineColor) {
		if (sceneHelper.getSceneToUpdate() == null) { return; }
		double stepX, stepY;
		final double cellWidth = getEnvHeight() / dimensions.x;
		final double cellHeight = getEnvWidth() / dimensions.y;
		final GamaColor color = GamaColor.getInt(lineColor.getRGB());
		final ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(null, color, color, IShape.Type.GRIDLINE);
		for (double i = 0; i < dimensions.x; i++) {
			for (double j = 0; j < dimensions.y; j++) {
				stepX = i + 0.5;
				stepY = j + 0.5;
				final Geometry g = GamaGeometryType
						.buildRectangle(cellWidth, cellHeight, new GamaPoint(stepX * cellWidth, stepY * cellHeight))
						.getInnerGeometry();
				sceneHelper.getSceneToUpdate().addGeometry(g, attributes);
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
		openGL.cacheFont(attributes.font);
		attributes.getLocation().setY(-attributes.getLocation().getY());
		sceneHelper.getSceneToUpdate().addString(string, attributes);
		return null;
	}

	/**
	 * 
	 * DIMENSIONS, RATIOS AND LOCATIONS METHODS
	 * 
	 */

	@Override
	public final ILocation getCameraPos() {
		return cameraHelper.getPosition();
	}

	@Override
	public final ILocation getCameraTarget() {
		return cameraHelper.getTarget();
	}

	@Override
	public final ILocation getCameraOrientation() {
		return cameraHelper.getOrientation();
	}

	@Override
	public double getxRatioBetweenPixelsAndModelUnits() {
		return openGL.getRatios().x;
	}

	@Override
	public double getyRatioBetweenPixelsAndModelUnits() {
		return openGL.getRatios().y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getWidth()
	 */
	@Override
	public final double getWidth() {
		return canvas.getSurfaceWidth() * surface.getZoomLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getHeight()
	 */
	@Override
	public final double getHeight() {
		return canvas.getSurfaceHeight() * surface.getZoomLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getMaxEnvDim()
	 */
	@Override
	public double getMaxEnvDim() {
		final double env_width = data.getEnvWidth();
		final double env_height = data.getEnvHeight();
		return env_width > env_height ? env_width : env_height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getEnvWidth()
	 */
	@Override
	public double getEnvWidth() {
		return data.getEnvWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getEnvHeight()
	 */
	@Override
	public double getEnvHeight() {
		return data.getEnvHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getRealWorldPointFromWindowPoint(java.awt.Point)
	 */
	@Override
	public GamaPoint getRealWorldPointFromWindowPoint(final Point mouse) {
		return openGL.getWorldPositionFrom(new GamaPoint(mouse.x, mouse.y), cameraHelper.getPosition());
	}

	@Override
	public final int getDisplayWidth() {
		return (int) Math.round(getWidth());
	}

	@Override
	public final int getDisplayHeight() {
		return (int) Math.round(getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getCameraHelper()
	 */

	@Override
	public CameraHelper getCameraHelper() {
		return cameraHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getKeystoneHelper()
	 */
	@Override
	public KeystoneHelper getKeystoneHelper() {
		return keystoneHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getPickingHelper()
	 */
	@Override
	public PickingHelper getPickingHelper() {
		return pickingHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getOpenGLHelper()
	 */
	@Override
	public OpenGL getOpenGLHelper() {
		return openGL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getLightHelper()
	 */
	@Override
	public LightHelper getLightHelper() {
		return lightHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getROIHelper()
	 */
	@Override
	public ROIHelper getROIHelper() {
		return roiHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getRotationHelper()
	 */
	@Override
	public RotationHelper getRotationHelper() {
		return rotationHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.IOpenGLRenderer#getSceneHelper()
	 */
	@Override
	public SceneHelper getSceneHelper() {
		return sceneHelper;
	}

}
