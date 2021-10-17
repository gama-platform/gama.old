/*******************************************************************************************************
 *
 * ModelScene.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import org.locationtech.jts.geom.Geometry;

import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import msi.gama.common.interfaces.ILayer;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.MeshDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.layers.AxesLayerObject;
import ummisco.gama.opengl.scene.layers.FrameLayerObject;
import ummisco.gama.opengl.scene.layers.LayerObject;
import ummisco.gama.opengl.scene.layers.OverlayLayerObject;

/**
 *
 * The class ModelScene. A repository for all the objects that constitute the scene of a model : strings, images,
 * shapes... 04/03/14: Now organized by layers to address the issue of z depth
 *
 * @author drogoul
 * @since 3 mai 2013
 *
 */
public class ModelScene {

	/** The Constant AXES_KEY. */
	public static final String AXES_KEY = "__axes__0";

	/** The Constant FRAME_KEY. */
	public static final String FRAME_KEY = "__frame__0";

	/** The layers. */
	protected final IMap<String, LayerObject> layers = GamaMapFactory.create();

	/** The current layer. */
	protected LayerObject currentLayer;

	/** The renderer. */
	protected final IOpenGLRenderer renderer;

	/** The rendered. */
	private volatile boolean rendered = false;

	/** The object number. */
	private volatile int objectNumber;

	/** The z increment. */
	private double zIncrement;

	/** The current layer trace. */
	private int currentLayerTrace;

	/**
	 * Instantiates a new model scene.
	 *
	 * @param renderer
	 *            the renderer
	 * @param withWorld
	 *            the with world
	 */
	public ModelScene(final IOpenGLRenderer renderer, final boolean withWorld) {
		this.renderer = renderer;
		if (withWorld) { initWorld(); }
	}

	/**
	 * Inits the world.
	 */
	protected void initWorld() {
		if (renderer.getData().isDrawEnv()) {
			layers.put(FRAME_KEY, new FrameLayerObject(renderer));
			layers.put(AXES_KEY, new AxesLayerObject(renderer));
		}
	}

	/**
	 * @param context
	 *            Called every new iteration when updateDisplay() is called on the surface
	 */
	public void wipe(final OpenGL gl) {
		layers.forEach((name, obj) -> { if (obj != null && (!obj.isStatic() || obj.isInvalid())) { obj.clear(gl); } });

		// Wipe the textures.
		gl.deleteVolatileTextures();
	}

	/**
	 * Draw.
	 *
	 * @param gl
	 *            the gl
	 */
	public void draw(final OpenGL gl) {

		gl.push(GLMatrixFunc.GL_MODELVIEW);
		gl.setZIncrement(zIncrement);

		layers.forEach((name, layer) -> {
			if (layer != null && !layer.isInvalid()) {
				try {
					layer.lock();
					layer.draw(gl);
				} catch (final RuntimeException r) {
					DEBUG.ERR("Runtime error " + r.getMessage() + " in OpenGL loop");
					r.printStackTrace();
				}
			}
		});
		gl.setZIncrement(0);
		rendered = true;
		gl.getRenderer().getSurface().synchronizer.signalRenderingIsFinished();
		gl.pop(GLMatrixFunc.GL_MODELVIEW);
	}

	/**
	 * Compute visual Z increment.
	 *
	 * @return the double
	 */
	private double computeVisualZIncrement() {
		if (objectNumber <= 1) return 0d;
		// The maximum visual z allowance between the object at the bottom and the one at the top
		final double maxZ = renderer.getMaxEnvDim() / 2000d;
		// The increment is simply
		return maxZ / objectNumber;
	}

	/**
	 * Cannot add.
	 *
	 * @return true, if successful
	 */
	public boolean cannotAdd() {
		if (currentLayer == null) return true;
		return currentLayer.isStatic() && currentLayer.isLocked();
	}

	/**
	 * Increment.
	 *
	 * @return true, if successful
	 */
	private boolean increment() {
		if (cannotAdd()) return false;
		objectNumber += currentLayerTrace;
		return true;
	}

	/**
	 * Adds the string.
	 *
	 * @param string
	 *            the string
	 * @param attributes
	 *            the attributes
	 */
	public void addString(final String string, final TextDrawingAttributes attributes) {
		if (increment()) { currentLayer.addString(string, attributes); }
	}

	/**
	 * Adds the geometry file.
	 *
	 * @param file
	 *            the file
	 * @param attributes
	 *            the attributes
	 */
	public void addGeometryFile(final GamaGeometryFile file, final DrawingAttributes attributes) {
		if (increment()) { currentLayer.addFile(file, attributes); }
	}

	/**
	 * Adds the image.
	 *
	 * @param img
	 *            the img
	 * @param attributes
	 *            the attributes
	 */
	public void addImage(final Object img, final DrawingAttributes attributes) {
		if (increment()) { currentLayer.addImage(img, attributes); }
	}

	/**
	 * Adds the geometry.
	 *
	 * @param geometry
	 *            the geometry
	 * @param attributes
	 *            the attributes
	 */
	public void addGeometry(final Geometry geometry, final DrawingAttributes attributes) {
		if (increment()) { currentLayer.addGeometry(geometry, attributes); }
	}

	/**
	 * Adds the field.
	 *
	 * @param fieldValues
	 *            the field values
	 * @param attributes
	 *            the attributes
	 */
	public void addField(final IField fieldValues, final MeshDrawingAttributes attributes) {
		if (increment()) { currentLayer.addField(fieldValues, attributes); }
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		layers.clear();
		currentLayer = null;
	}

	/**
	 * Begin drawing layers.
	 */
	public void beginDrawingLayers() {
		currentLayerTrace = 0;
	}

	/**
	 * End drawing layers.
	 */
	public void endDrawingLayers() {
		zIncrement = computeVisualZIncrement();
	}

	/**
	 * Rendered.
	 *
	 * @return true, if successful
	 */
	public boolean rendered() {
		return rendered;
	}

	/**
	 * Reload.
	 */
	public void reload() {
		unlock();
		dispose();
		initWorld();
	}

	/**
	 * Unlock.
	 */
	public void unlock() {
		for (final LayerObject l : layers.values()) { l.unlock(); }
	}

	/**
	 * Begin drawing layer.
	 *
	 * @param layer
	 *            the layer
	 * @param alpha
	 *            the alpha
	 */
	public void beginDrawingLayer(final ILayer layer, final Double alpha) {
		final String key = layer.getName() + layer.getDefinition().getOrder();
		currentLayer = layers.get(key);
		if (currentLayer == null) {
			currentLayer = createRegularLayer(renderer, layer);
			layers.put(key, currentLayer);
		}
		currentLayer.setAlpha(alpha);
		currentLayerTrace = currentLayer.numberOfTraces();
	}

	/**
	 * Creates the regular layer.
	 *
	 * @param renderer
	 *            the renderer
	 * @param layer
	 *            the layer
	 * @return the layer object
	 */
	protected LayerObject createRegularLayer(final IOpenGLRenderer renderer, final ILayer layer) {
		boolean overlay = layer != null && layer.isOverlay();
		return overlay ? new OverlayLayerObject(renderer, layer) : new LayerObject(renderer, layer);
	}

	/**
	 * @return
	 */
	public ModelScene copyStatic() {
		final ModelScene newScene = new ModelScene(renderer, false);
		layers.forEach((name, layer) -> {
			if ((layer.isStatic() || layer.hasTrace()) && !layer.isInvalid()) { newScene.layers.put(name, layer); }
		});

		return newScene;
	}

	/**
	 *
	 */
	public void invalidateLayers() {
		layers.forEach((name, layer) -> { layer.invalidate(); });
	}

	/**
	 * Layer offset changed.
	 */
	public void layerOffsetChanged() {
		layers.forEach((name, layer) -> { if (layer.canSplit()) { layer.computeOffset(); } });

	}

	/**
	 * Recompute layout dimensions.
	 *
	 * @param gl
	 *            the gl
	 */
	public void recomputeLayoutDimensions(final OpenGL gl) {
		layers.forEach((name, layer) -> { if (layer.isOverlay() || layer.isStatic()) { layer.forceRedraw(gl); } });

	}

}
