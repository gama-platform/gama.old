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

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import ummisco.gama.opengl.Abstract3DRenderer;
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
	private volatile boolean rendered = false;
	private volatile int objectNumber;
	private double zIncrement;

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
	public void wipe(final OpenGL gl) {

		for (final Map.Entry<String, LayerObject> entry : layers.entrySet()) {
			final LayerObject obj = entry.getValue();
			if (obj != null && (!obj.isStatic() || obj.isInvalid())) {
				obj.clear(gl);
			}
		}
		// Wipe the textures.
		gl.deleteVolatileTextures();
	}

	public void draw(final OpenGL gl) {

		if (renderer.useShader()) {
			// if the rotation helper layer exists, put it at the end of the map
			// (otherwise, transparency issues)
			final LayerObject rotLayer = layers.get(ROTATION_HELPER_KEY);
			if (rotLayer != null) {
				layers.remove(ROTATION_HELPER_KEY);
				layers.put(ROTATION_HELPER_KEY, rotLayer);
			}
		}

		gl.setZIncrement(zIncrement);

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
		gl.setZIncrement(0);
		rendered = true;
	}

	private double computeVisualZIncrement() {
		if (objectNumber == 0)
			return 0d;
		// The maximum visual z allowance between the object at the bottom and the one at the top
		final double maxZ = renderer.getMaxEnvDim() / 2000d;
		// The increment is simply
		return maxZ / objectNumber;
	}

	public boolean cannotAdd() {
		if (currentLayer == null)
			return true;
		return currentLayer.isStatic() && currentLayer.isLocked();
	}

	private <T extends AbstractObject> T configure(final T object) {
		objectNumber++;
		return object;
	}

	public StringObject addString(final String string, final DrawingAttributes attributes) {
		if (cannotAdd()) { return null; }
		return configure(currentLayer.addString(string, attributes));
	}

	public GeometryObject addImageFile(final GamaImageFile file, final FileDrawingAttributes attributes) {
		if (cannotAdd()) { return null; }
		return configure(currentLayer.addImage(file, attributes));
	}

	public ResourceObject addGeometryFile(final GamaGeometryFile file, final FileDrawingAttributes attributes) {
		if (cannotAdd()) { return null; }
		return configure(currentLayer.addFile(file, attributes));
	}

	public GeometryObject addImage(final BufferedImage img, final DrawingAttributes attributes) {
		if (cannotAdd()) { return null; }
		return configure(currentLayer.addImage(img, attributes));
	}

	public GeometryObject addGeometry(final Geometry geometry, final ShapeDrawingAttributes attributes) {
		if (cannotAdd()) { return null; }
		return configure(currentLayer.addGeometry(geometry, attributes));
	}

	public FieldObject addField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		if (cannotAdd()) { return null; }
		return configure(currentLayer.addField(fieldValues, attributes));
	}

	public void dispose() {
		layers.clear();
		currentLayer = null;
	}

	public void beginDrawingLayers() {}

	public void endDrawingLayers() {
		zIncrement = computeVisualZIncrement();
		// if (!SceneReceiver.getInstance().canReceive())
		// return;
		// SceneReceiver.getInstance().receive(this.toSimpleScene());
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
