/*******************************************************************************************************
 *
 * ModelScene.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import org.locationtech.jts.geom.Geometry;

import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import msi.gama.common.interfaces.ILayer;
import msi.gama.common.preferences.GamaPreferences;
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
import ummisco.gama.opengl.scene.layers.LayerObjectWithTrace;
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

	static {
		DEBUG.OFF();
	}

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
	private volatile double zIncrement;

	/** The current layer trace. */
	private volatile int currentLayerTraceNumber;

	/** The max Z. */
	final double maxZ;

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
		// maxZ = 1/ renderer.getMaxEnvDim() ;
		maxZ = renderer.getMaxEnvDim() / 2000d;
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
		layers.forEach((name, layer) -> { if (layer != null && !layer.isStatic()) { layer.clear(gl); } });
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
		// if (index++ == 0) {
		// DEBUG.OUT("Drawing the first scene");
		// } else {
		// DEBUG.OUT("Drawing scene " + index);
		// }
		gl.push(GLMatrixFunc.GL_MODELVIEW);
		gl.setZIncrement(renderer.getData().isOrtho() ? 0D : zIncrement);
		// AD called here so that it is inside the keystone drawing. See #3285
		gl.rotateModel();
		for (LayerObject layer : layers.values()) {
			// AD Added
			if (layer != null && layer.isVisible()) {
				// AD added to prevent overlays to rotate
				if (layer.isOverlay()) { gl.pushIdentity(GLMatrixFunc.GL_MODELVIEW); }
				try {
					if (renderer.getPickingHelper().isPicking() && !layer.isPickable()) { continue; }
					layer.draw(gl);
				} catch (final RuntimeException r) {
					DEBUG.ERR("Runtime error " + r.getMessage() + " in OpenGL loop");
					r.printStackTrace();
				} finally {
					if (layer.isOverlay()) { gl.pop(GLMatrixFunc.GL_MODELVIEW); }
				}
			}
		}

		gl.setZIncrement(0);
		rendered = true;
		renderer.getSurface().getOutput().setRendered(true);
		gl.pop(GLMatrixFunc.GL_MODELVIEW);
	}

	/**
	 * Compute visual Z increment.
	 *
	 * @return the double
	 */
	private double computeVisualZIncrement() {
		if (objectNumber < 1 || !GamaPreferences.Displays.OPENGL_Z_FIGHTING.getValue()) return 0d;
		// The maximum visual z allowance between the object at the bottom and the one at the top

		// The increment is simply
		return maxZ / objectNumber;
	}

	/**
	 * Increment.
	 *
	 * @return true, if successful
	 */
	private boolean increment() {
		if (currentLayer == null) return false;
		objectNumber += currentLayerTraceNumber;
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
		// DEBUG.OUT("ModelScene #" + myIndex + ": Layers keys before disposing = " + layers.keySet());
		layers.clear();
		currentLayer = null;
	}

	/**
	 * Begin drawing layers.
	 */
	public void beginDrawingLayers() {
		currentLayerTraceNumber = 0;
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
		dispose();
		initWorld();
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
			// DEBUG.OUT("ModelScene #" + myIndex + ": Layer " + layer.getName() + " is not present in the scene");
			currentLayer = layer.isOverlay() ? new OverlayLayerObject(renderer, layer)
					: layer.getData().getTrace() > 0 ? new LayerObjectWithTrace(renderer, layer)
					: new LayerObject(renderer, layer);
			// DEBUG.OUT("ModelScene #" + myIndex + ": Creating layer " + " : static " + currentLayer.isStatic()
			// + "; trace " + currentLayer.hasTrace());
			layers.put(key, currentLayer);
		}
		// DEBUG.OUT("ModelScene #" + myIndex + ": Layers keys after creation = " + layers.keySet());
		currentLayer.setAlpha(alpha);
		currentLayerTraceNumber = currentLayer.numberOfActualTraces();
	}

	/**
	 * @return
	 */
	public ModelScene copyStatic() {
		// DEBUG.OUT("Creating static scene");
		final ModelScene newScene = new ModelScene(renderer, false);
		// DEBUG.OUT("ModelScene #" + myIndex + ": layers keys before copying to static = " + layers.keySet());
		layers.forEach((name, layer) -> {

			// DEBUG.OUT("ModelScene #" + myIndex + ": Examining layer " + name + " : static " + layer.isStatic()
			// + "; trace " + layer.hasTrace());
			if (layer.isStatic() || layer.hasTrace()) {
				// DEBUG.OUT("===>> Adding " + name + " as static ");
				newScene.layers.put(name, layer);
			}
		});

		return newScene;
	}

}
