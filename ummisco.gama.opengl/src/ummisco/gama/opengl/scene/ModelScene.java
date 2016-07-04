/*********************************************************************************************
 *
 *
 * 'ModelScene.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.TextureCache;
import ummisco.gama.webgl.SceneReceiver;
import ummisco.gama.webgl.SimpleLayer;
import ummisco.gama.webgl.SimpleScene;

/**
 *
 * The class ModelScene. A repository for all the objects that constitute the
 * scene of a model : strings, images, shapes... 04/03/14: Now organized by
 * layers to address the issue of z depth
 *
 * @author drogoul
 * @since 3 mai 2013
 *
 */
public class ModelScene {

	private static int number = 0;
	private final int id;
	public static final String AXES_KEY = "__axes__0";
	public static final String FRAME_KEY = "__frame__0";
	protected final Map<String, LayerObject> layers = new LinkedHashMap<String, LayerObject>();
	protected LayerObject currentLayer;
	protected final Abstract3DRenderer renderer;
	private final Map<BufferedImage, Texture> localVolatileTextures = new HashMap<BufferedImage, Texture>(10);
	private volatile boolean rendered = false;

	public ModelScene(final Abstract3DRenderer renderer, final boolean withWorld) {
		this.renderer = renderer;
		this.id = number++;
		if (withWorld) {
			initWorld();
		}
	}

	public int getId() {
		return id;
	}

	protected void initWorld() {
		if (renderer.data.isDrawEnv()) {
			LayerObject object = new AxesLayerObject(renderer);
			layers.put(AXES_KEY, object);
			object = new FrameLayerObject(renderer);
			layers.put(FRAME_KEY, object);

		}
	}

	/**
	 * @param context
	 *            Called every new iteration when updateDisplay() is called on
	 *            the surface
	 */
	public void wipe(final GL gl) {
		// The display is cleared every iteration if not in a trace display mode
		// or when reloading a simulation
		// int traceSize = FastMath.max(requestedTraceSize, 0);

		for (final Map.Entry<String, LayerObject> entry : layers.entrySet()) {
			final LayerObject obj = entry.getValue();
			if (obj != null && (!obj.isStatic() || obj.isInvalid())) {
				obj.clear(gl);
			}
		}
		// Wipe the textures. However, might be necessary to know what to do for
		// the trace...
		final int size = localVolatileTextures.size();
		if (size != 0) {
			final int[] textureIdsToDestroy = new int[size];
			int index = 0;
			for (final Map.Entry<BufferedImage, Texture> entry : localVolatileTextures.entrySet()) {
				final Texture t = entry.getValue();
				textureIdsToDestroy[index++] = t == null ? 0 : t.getTextureObject();
				// entry.getKey().flush();
			}
			gl.glDeleteTextures(textureIdsToDestroy.length, textureIdsToDestroy, 0);
			localVolatileTextures.clear();
		}
	}

	public Texture getTexture(final GL gl, final BufferedImage image) {
		if (image == null) {
			return null;
		}
		Texture texture = localVolatileTextures.get(image);
		if (texture == null) {
			texture = TextureCache.buildTexture(gl, image);
			localVolatileTextures.put(image, texture);
		}
		return antiAliasTexture(gl, texture);
	}

	// Must have been stored before
	public Texture getTexture(final GL gl, final GamaImageFile file) {
		if (file == null) {
			return null;
		}
		final Texture texture = renderer.getSharedTextureCache().get(gl, file);
		return antiAliasTexture(gl, texture);
	}

	private Texture antiAliasTexture(final GL gl, final Texture texture) {
		if (texture != null) {
			final boolean antiAlias = renderer.data.isAntialias();
			// Apply antialas to the texture based on the current preferences
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, antiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, antiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
		}
		return texture;
	}

	public void draw(final GL2 gl) {
		// System.out.println("Beginning rendering Model front scene #" + id);
		final LayerObject[] array = layers.values().toArray(new LayerObject[0]);
		for (final LayerObject layer : array) {
			if (layer != null && !layer.isInvalid()) {
				try {
					layer.draw(gl, renderer);
					layer.lock();
				} catch (final RuntimeException r) {
					System.err.println("Runtime error " + r.getMessage() + " in OpenGL loop");
					r.printStackTrace();
				}
			}
		}
		rendered = true;
	}

	public boolean cannotAdd() {
		if (currentLayer == null)
			return true;
		return currentLayer.isStatic() && currentLayer.isLocked();
	}

	public void addString(final String string, final DrawingAttributes attributes) {
		if (cannotAdd()) {
			return;
		}
		currentLayer.addString(string, attributes);
	}

	public void addFile(final GamaFile file, final FileDrawingAttributes attributes) {
		if (cannotAdd()) {
			return;
		}
		if (file instanceof GamaImageFile) {
			renderer.getSharedTextureCache().initializeStaticTexture((GamaImageFile) file);
			currentLayer.addImage((GamaImageFile) file, attributes);
		} else if (file instanceof GamaGeometryFile) {
			currentLayer.addFile((GamaGeometryFile) file, attributes);
		}
	}

	public void addImage(final BufferedImage img, final DrawingAttributes attributes) {
		if (cannotAdd()) {
			return;
		}
		currentLayer.addImage(img, attributes);
	}

	public void addGeometry(final Geometry geometry, final ShapeDrawingAttributes attributes) {
		if (cannotAdd()) {
			return;
		}
		if (attributes.textures != null && !attributes.textures.isEmpty()) {
			for (final Object img : attributes.textures) {
				if (img instanceof GamaImageFile) {
					renderer.getSharedTextureCache().initializeStaticTexture((GamaImageFile) img);
				}
			}
		}
		currentLayer.addGeometry(geometry, attributes);
	}

	public void addField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		if (cannotAdd()) {
			return;
		}
		if (attributes.textures != null && !attributes.textures.isEmpty()) {
			for (final Object img : attributes.textures) {
				if (img instanceof GamaImageFile) {
					renderer.getSharedTextureCache().initializeStaticTexture((GamaImageFile) img);
				}
			}
		}
		currentLayer.addField(fieldValues, attributes);
	}

	public void dispose() {
		layers.clear();
		currentLayer = null;
	}

	public void beginDrawingLayers() {
	}

	public void endDrawingLayers() {
		if (!SceneReceiver.getInstance().canReceive())
			return;
		SceneReceiver.getInstance().receive(this.toSimpleScene());
	}

	private SimpleScene toSimpleScene() {
		final List<SimpleLayer> simpleLayers = new ArrayList();
		for (final LayerObject layer : this.layers.values()) {
			simpleLayers.add(layer.toSimpleLayer());
		}
		return new SimpleScene(simpleLayers);
	}

	public boolean rendered() {
		return rendered;
	}

	public void reload() {
		for (final LayerObject l : layers.values()) {
			l.unlock();
		}
		dispose();
		initWorld();
	}

	public void hideLayer(final String name) {
		layers.put(name, null);
	}

	public void beginDrawingLayer(final ILayer layer, final GamaPoint offset, final GamaPoint scale,
			final Double alpha) {
		final int id = layer.getOrder();
		final String key = layer.getName() + id;
		currentLayer = layers.get(key);
		if (currentLayer == null) {
			currentLayer = new LayerObject(renderer, layer);
			layers.put(key, currentLayer);
		}
		currentLayer.setOffset(offset.plus(new GamaPoint(0, 0, id * 0.1f)));
		currentLayer.setScale(scale);
		currentLayer.setAlpha(alpha);
	}

	public void beginOverlay() {
		currentLayer.setOverlay(true);
	}

	/**
	 * @return
	 */
	public ModelScene copyStatic() {
		final ModelScene newScene = new ModelScene(renderer, false);
		for (final Map.Entry<String, LayerObject> entry : layers.entrySet()) {
			final LayerObject layer = entry.getValue();
			if ((layer.isStatic() || layer.hasTrace()) && !layer.isInvalid()) {
				newScene.layers.put(entry.getKey(), layer);
			}
		}
		return newScene;
	}

	/**
	 *
	 */
	public void invalidateLayers() {
		for (final Map.Entry<String, LayerObject> entry : layers.entrySet()) {
			entry.getValue().invalidate();
		}
	}

	/**
	 * @param gl
	 */
	public void preload(final GL2 gl) {
		for (final Map.Entry<String, LayerObject> entry : layers.entrySet()) {
			entry.getValue().preload(gl);
		}
	}

	public void startDrawRotationHelper(final GamaPoint pivotPoint, final double size) {
		final AxesLayerObject worldLayer = (AxesLayerObject) layers.get(AXES_KEY);
		if (worldLayer != null) {
			worldLayer.setOffset(pivotPoint.yNegated());
			final double ratio = size / renderer.getMaxEnvDim();
			worldLayer.setScale(new GamaPoint(ratio, ratio, ratio));

		}
	}

	public void stopDrawRotationHelper() {
		final AxesLayerObject worldLayer = (AxesLayerObject) layers.get(AXES_KEY);
		if (worldLayer != null) {
			worldLayer.setOffset(GamaPoint.NULL_POINT);
			worldLayer.setScale(new GamaPoint(.15, .15, .15));
		}

	}

}
