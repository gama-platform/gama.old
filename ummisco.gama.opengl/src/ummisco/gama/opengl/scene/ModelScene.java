/*********************************************************************************************
 *
 * 'ModelScene.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Geometry;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.TOrderedHashMap;
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
 * The class ModelScene. A repository for all the objects that constitute the scene of a model : strings, images,
 * shapes... 04/03/14: Now organized by layers to address the issue of z depth
 *
 * @author drogoul
 * @since 3 mai 2013
 *
 */
public class ModelScene {

	public static final String AXES_KEY = "__axes__0";
	public static final String FRAME_KEY = "__frame__0";
	public static final String ROTATION_HELPER_KEY = "__rotation__0";
	public static final String KEYSTONE_HELPER_KEY = "__keystone__0";
	public static final String LIGHTS_KEY = "__lights__0";
	public static final String FPS_KEY = "z__fps__0";
	protected final TOrderedHashMap<String, LayerObject> layers = new TOrderedHashMap<String, LayerObject>();
	protected LayerObject currentLayer;
	protected final Abstract3DRenderer renderer;
	private final Map<BufferedImage, Texture> localVolatileTextures = new THashMap<BufferedImage, Texture>(2);
	private volatile boolean rendered = false;
	private volatile double visualZIncrement;
	private volatile int objectNumber;

	public static abstract class ObjectVisitor {
		public abstract void process(AbstractObject object);
	}

	public ModelScene(final Abstract3DRenderer renderer, final boolean withWorld) {
		this.renderer = renderer;
		if (withWorld) {
			initWorld();
		}
	}

	protected void initWorld() {
		if (renderer.data.isDrawEnv()) {
			layers.put(FRAME_KEY, new FrameLayerObject(renderer));
			layers.put(AXES_KEY, new AxesLayerObject(renderer));
			// layers.put(KEYSTONE_HELPER_KEY, new KeystoneHelperLayerObject(renderer));
		}
		if (renderer.useShader()) {
			layers.put(ROTATION_HELPER_KEY, new RotationHelperLayerObject(renderer));
			layers.put(KEYSTONE_HELPER_KEY, new KeystoneHelperLayerObject(renderer));
			layers.put(LIGHTS_KEY, new LightsLayerObject(renderer));
			if (renderer.data.isShowfps())
				layers.put(FPS_KEY, new FPSLayerObject(renderer));
		}
	}

	/**
	 * @param context
	 *            Called every new iteration when updateDisplay() is called on the surface
	 */
	public void wipe(final GL2 gl) {

		for (final Map.Entry<String, LayerObject> entry : layers.entrySet()) {
			final LayerObject obj = entry.getValue();
			if (obj != null && (!obj.isStatic() || obj.isInvalid())) {
				obj.clear(gl);
			}
		}
		// Wipe the textures.
		final int size = localVolatileTextures.size();
		if (size != 0) {
			final int[] textureIdsToDestroy = new int[size];
			int index = 0;
			for (final Map.Entry<BufferedImage, Texture> entry : localVolatileTextures.entrySet()) {
				final Texture t = entry.getValue();
				textureIdsToDestroy[index++] = t == null ? 0 : t.getTextureObject();
			}
			gl.glDeleteTextures(textureIdsToDestroy.length, textureIdsToDestroy, 0);
			localVolatileTextures.clear();
		}
	}

	public Texture getTexture(final GL gl, final BufferedImage image) {
		if (image == null) { return null; }
		Texture texture = localVolatileTextures.get(image);
		if (texture == null) {
			texture = TextureCache.buildTexture(gl, image);
			localVolatileTextures.put(image, texture);
		}
		return texture;
	}

	// Must have been stored before
	public Texture getTexture(final GL gl, final GamaImageFile file) {
		if (file == null) { return null; }
		final Texture texture = renderer.getSharedTextureCache().get(renderer.getSurface().getScope(), gl, file);
		return texture;
	}

	public void draw(final GL2 gl) {
		// if the rotation helper layer exists, put it at the end of the map
		// (otherwise, transparency issues)

		final LayerObject rotLayer = layers.get(ROTATION_HELPER_KEY);
		if (rotLayer != null) {
			layers.remove(ROTATION_HELPER_KEY);
			layers.put(ROTATION_HELPER_KEY, rotLayer);
		}

		for (final LayerObject layer : layers.values()) {
			if (layer != null && !layer.isInvalid()) {
				try {
					layer.draw(gl);
					layer.lock();
				} catch (final RuntimeException r) {
					System.err.println("Runtime error " + r.getMessage() + " in OpenGL loop");
					r.printStackTrace();
				}
			}
		}
		rendered = true;
	}

	private void computeVisualZIncrement() {
		if (objectNumber == 0)
			return;
		// The maximum visual z allowance between the object at the bottom and the one at the top
		final double maxZ = renderer.getMaxEnvDim() / 2000d;
		// The increment is simply
		visualZIncrement = maxZ / objectNumber;
	}

	public double getVisualZIncrement() {
		return visualZIncrement;
	}

	public boolean cannotAdd() {
		if (currentLayer == null)
			return true;
		return currentLayer.isStatic() && currentLayer.isLocked();
	}

	private void configure(final AbstractObject object) {
		object.setZFightingOffset(objectNumber++);
	}

	public void addString(final String string, final DrawingAttributes attributes) {
		if (cannotAdd()) { return; }
		configure(currentLayer.addString(string, attributes));
	}

	@SuppressWarnings ("rawtypes")
	public void addFile(final GamaFile file, final FileDrawingAttributes attributes) {
		if (cannotAdd()) { return; }
		if (file instanceof GamaImageFile) {
			configure(currentLayer.addImage(file, attributes));
		} else if (file instanceof GamaGeometryFile) {
			configure(currentLayer.addFile((GamaGeometryFile) file, attributes));
		}
	}

	public void addImage(final BufferedImage img, final DrawingAttributes attributes) {
		if (cannotAdd()) { return; }
		configure(currentLayer.addImage(img, attributes));
	}

	public void addGeometry(final Geometry geometry, final ShapeDrawingAttributes attributes) {
		if (cannotAdd()) { return; }
		configure(currentLayer.addGeometry(geometry, attributes));
	}

	public void addField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		if (cannotAdd()) { return; }
		configure(currentLayer.addField(fieldValues, attributes));
	}

	public void dispose() {
		layers.clear();
		currentLayer = null;
	}

	public void beginDrawingLayers() {
		visualZIncrement = 0;
	}

	public void endDrawingLayers() {
		computeVisualZIncrement();
		if (!SceneReceiver.getInstance().canReceive())
			return;
		SceneReceiver.getInstance().receive(this.toSimpleScene());
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
		// offset.z = offset.z + id * 0.01f;
		currentLayer.setOffset(offset);
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
			worldLayer.setOffset(null);
			worldLayer.setScale(null);
		}

	}

	private SimpleScene toSimpleScene() {
		final List<SimpleLayer> simpleLayers = new ArrayList<>();
		for (final LayerObject layer : this.layers.values()) {
			simpleLayers.add(layer.toSimpleLayer());
		}
		final int[] rgbBackgroundColor = new int[3];
		rgbBackgroundColor[0] = renderer.data.getBackgroundColor().getRed();
		rgbBackgroundColor[1] = renderer.data.getBackgroundColor().getGreen();
		rgbBackgroundColor[2] = renderer.data.getBackgroundColor().getBlue();
		return new SimpleScene(simpleLayers, this.renderer.data.getDiffuseLights(), rgbBackgroundColor,
				this.renderer.data.getEnvWidth(), this.renderer.data.getEnvHeight(), this.renderer.hashCode());
	}

}
