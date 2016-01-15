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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Geometry;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.scene.StaticLayerObject.WordLayerObject;

/**
 *
 * The class ModelScene. A repository for all the objects that constitute the scene of a model : strings, images,
 * shapes...
 * 04/03/14: Now organized by layers to address the issue of z depth
 *
 * @author drogoul
 * @since 3 mai 2013
 *
 */
public class ModelScene {

	private static int number = 0;
	private final int id;
	public static final String ENV_KEY = "__env__0";
	protected final Map<String, LayerObject> layers = new LinkedHashMap();
	protected LayerObject currentLayer;
	protected final JOGLRenderer renderer;
	private volatile boolean staticObjectsAreLocked;
	private final Map<BufferedImage, Texture> textures = new HashMap(10);
	private volatile boolean rendered = false;

	public ModelScene(final JOGLRenderer renderer, final boolean withWorld) {
		this.renderer = renderer;
		this.id = number++;
		if ( withWorld ) {
			initWorld();
		}
	}

	public int getId() {
		return id;
	}

	protected void initWorld() {
		currentLayer = new WordLayerObject(renderer);
		layers.put(ENV_KEY, currentLayer);
	}

	/**
	 * @param context
	 * Called every new iteration when updateDisplay() is called on the surface
	 */
	public void wipe(final GL gl) {
		// The display is cleared every iteration if not in a trace display mode or when reloading a simulation
		// int traceSize = Math.max(requestedTraceSize, 0);

		for ( Map.Entry<String, LayerObject> entry : layers.entrySet() ) {
			LayerObject obj = entry.getValue();
			if ( obj != null && (!obj.isStatic() || obj.isInvalid()) ) {
				obj.clear(gl);
			}
		}
		// Wipe the textures. However, might be necessary to know what to do for the trace...
		int size = textures.size();
		if ( size != 0 ) {
			int[] textureIdsToDestroy = new int[size];
			int index = 0;
			for ( Map.Entry<BufferedImage, Texture> entry : textures.entrySet() ) {
				Texture t = entry.getValue();
				textureIdsToDestroy[index++] = t == null ? 0 : t.getTextureObject();
			}
			gl.glDeleteTextures(textureIdsToDestroy.length, textureIdsToDestroy, 0);
			textures.clear();
		}

	}

	public Texture getTexture(final GL gl, final BufferedImage image) {
		if ( image == null ) { return null; }
		Texture texture = textures.get(image);
		if ( texture == null ) {
			if ( TextureCache.contains(image) ) {
				texture = TextureCache.get(gl, image);
			} else {
				texture = TextureCache.buildTexture(gl, image);
				textures.put(image, texture);
			}
		}
		if ( texture != null ) {
			boolean antiAlias = renderer.data.isAntialias();
			// Apply antialas to the texture based on the current preferences
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, antiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, antiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
		}
		return texture;
	}

	public void draw(final GL2 gl, final boolean picking) {
		// System.out.println("Beginning rendering Model front scene #" + id);
		LayerObject[] array = layers.values().toArray(new LayerObject[0]);
		for ( LayerObject layer : array ) {
			if ( layer != null && !layer.isInvalid() ) {
				layer.draw(gl, renderer, picking);
			}
		}
		rendered = true;
	}

	public void addString(final String string, final GamaPoint location, final Integer size,
		final Double sizeInModelUnits, final Color color, final String font, final Integer style, final Double angle,
		final Boolean bitmap) {
		if ( currentLayer.isStatic() && staticObjectsAreLocked ) { return; }
		currentLayer.addString(string, location, size, sizeInModelUnits, color, font, style, angle, bitmap);
	}

	public void addImage(final BufferedImage img, final IAgent agent, final GamaPoint location,
		final GamaPoint dimensions, final Double angle, final boolean isDynamic, final String name) {
		if ( currentLayer.isStatic() && staticObjectsAreLocked ) { return; }
		if ( !isDynamic ) {
			TextureCache.initializeStaticTexture(img);
		}
		currentLayer.addImage(img, agent, location, dimensions, angle, isDynamic, name);
	}

	public void addDEMFromPNG(final BufferedImage demTexture, final BufferedImage demDefinition,
		final Envelope3D bounds) {
		if ( currentLayer.isStatic() && staticObjectsAreLocked ) { return; }
		if ( demTexture != null ) {
			// TextureCache.getInstance().initializeDynamicTexture(this, demTexture);
		}
		currentLayer.addDEM(null, demTexture, demDefinition, null, false, false, false, false, true, false, bounds, 1,
			null, null);
	}

	public void addDEM(final double[] dem, final BufferedImage demTexture, final IAgent agent, final boolean isTextured,
		final boolean isTriangulated, final boolean isGrayScaled, final boolean isShowText, final Envelope3D env,
		final double cellSize, final String name, final Color lineColor) {
		if ( currentLayer.isStatic() && staticObjectsAreLocked ) { return; }
		if ( demTexture != null ) {
			// TextureCache.getInstance().initializeDynamicTexture(this, demTexture);
		}
		currentLayer.addDEM(dem, demTexture, null, agent, isTextured, isTriangulated, isGrayScaled, isShowText, false,
			true, env, cellSize, name, lineColor);
	}

	public void addGeometry(final Geometry geometry, final IAgent agent, final Color color, final boolean fill,
		final Color border, final boolean isTextured, final java.util.List<BufferedImage> textures, final Integer angle,
		final double height, final boolean roundCorner, final IShape.Type type, final java.util.List<Double> ratio,
		final java.util.List<GamaColor> colors, final GamaPair<Double, GamaPoint> rotate3D) {
		if ( currentLayer.isStatic() && staticObjectsAreLocked ) { return; }
		if ( textures != null && !textures.isEmpty() ) {
			for ( BufferedImage img : textures ) {
				if ( img != null ) {
					TextureCache.initializeStaticTexture(img);
				}
			}
		}
		currentLayer.addGeometry(geometry, agent, color, fill, border, isTextured, textures, angle, height, roundCorner,
			type, ratio, colors, rotate3D);
	}

	public void dispose() {
		layers.clear();
		currentLayer = null;
	}

	public void beginDrawingLayers() {
		// completed = false;
		// System.out.println("Beginning update of Model back scene #" + id);
	}

	public void endDrawingLayers() {
		staticObjectsAreLocked = true;
		// System.out.println("End of update of Model back scene #" + id);
	}

	public boolean rendered() {
		return rendered;
	}

	public void reload() {
		// System.out.println("ModelScene " + id + " reloaded");
		staticObjectsAreLocked = false;
		dispose();
		initWorld();
	}

	public void hideLayer(final String name) {
		layers.put(name, null);
	}

	public void beginDrawingLayer(final ILayer layer, final GamaPoint offset, final GamaPoint scale,
		final Double alpha) {
		int id = layer.getOrder();
		String key = layer.getName() + id;
		currentLayer = layers.get(key);
		if ( currentLayer == null ) {
			currentLayer = new LayerObject(renderer, layer);
			// System.out.println("Adding layer " + key + " to scene " + this.id);
			layers.put(key, currentLayer);
		}
		currentLayer.setOffset(offset);
		currentLayer.setScale(scale);
		currentLayer.setAlpha(alpha);
	}

	/**
	 * @return
	 */
	public ModelScene copyStatic() {
		ModelScene newScene = new ModelScene(renderer, false);
		for ( Map.Entry<String, LayerObject> entry : layers.entrySet() ) {
			if ( (entry.getValue().isStatic() || entry.getValue().hasTrace()) && !entry.getValue().isInvalid() ) {
				newScene.layers.put(entry.getKey(), entry.getValue());
			}
		}
		return newScene;
	}

	/**
	 *
	 */
	public void invalidateLayers() {
		for ( Map.Entry<String, LayerObject> entry : layers.entrySet() ) {
			entry.getValue().invalidate();
		}
	}

}
