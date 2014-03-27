package msi.gama.jogl.scene;

import static javax.media.opengl.GL.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.media.opengl.GLException;
import msi.gama.common.util.ImageUtils;
import msi.gama.jogl.scene.StaticLayerObject.WordLayerObject;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.IList;
import com.google.common.collect.Iterables;
import com.vividsolutions.jts.geom.Geometry;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

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

	public static final String ENV_KEY = "__env__0";
	private final Map<String, LayerObject> layers = new LinkedHashMap();
	private LayerObject currentLayer;
	private final JOGLAWTGLRenderer renderer;
	private final Map<BufferedImage, MyTexture> textures = new IdentityHashMap();
	private volatile boolean staticObjectsAreLocked;

	public ModelScene(final JOGLAWTGLRenderer renderer) {
		this.renderer = renderer;
		initWorld();
	}

	private void initWorld() {
		currentLayer = new WordLayerObject(renderer);
		layers.put(ENV_KEY, currentLayer);
	}

	/**
	 * Called every new iteration when updateDisplay() is called on the surface
	 */
	public void wipe(final int requestedTraceSize) {
		// The display is cleared every iteration if not in a trace display mode or when reloading a simulation
		int traceSize = Math.max(requestedTraceSize, 0);

		for ( Map.Entry<String, LayerObject> entry : layers.entrySet() ) {
			LayerObject obj = entry.getValue();
			if ( obj != null ) {
				obj.clear(requestedTraceSize);
			}
		}
		// reloaded = false;
		if ( traceSize == 0 ) {
			// What to do with textures ? Ideally we should keep the n-th last (n = traceSize).
			for ( final Iterator<BufferedImage> it = textures.keySet().iterator(); it.hasNext(); ) {
				final BufferedImage im = it.next();
				// FIXME: if an image is not declared as dynamic, it will be kept in memory (even if it is not used)
				if ( textures.get(im).isDynamic() ) {
					// FIXME The textures are never disposed. Is it ok ?
					// renderer.getContext().makeCurrent();
					// textures.get(im).texture.dispose();
					it.remove();
				}
			}
		}
	}

	public void draw(final boolean picking) {
		for ( Map.Entry<String, LayerObject> entry : layers.entrySet() ) {
			LayerObject layer = entry.getValue();
			if ( layer != null ) {
				layer.draw(renderer, picking);
			}
		}
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
		currentLayer.addImage(img, agent, location, dimensions, angle, isDynamic, name);
	}

	public void addDEMFromPNG(final BufferedImage demTexture, final BufferedImage demDefinition, final Envelope3D bounds) {
		if ( currentLayer.isStatic() && staticObjectsAreLocked ) { return; }
		currentLayer.addDEM(null, demTexture, demDefinition, null, false, false, false, false, true, false, bounds, 1, null);
	}

	public void addDEM(final double[] dem, final BufferedImage demTexture, final IAgent agent,
		final boolean isTextured, final boolean isTriangulated, final boolean isGrayScaled, final boolean isShowText, final Envelope3D env,
		final double cellSize, final String name) {
		if ( currentLayer.isStatic() && staticObjectsAreLocked ) { return; }
		currentLayer.addDEM(dem, demTexture, null, agent, isTextured, isTriangulated, isGrayScaled, isShowText, false, true, env,
			cellSize, name);
	}

	public void addGeometry(final Geometry geometry, final IAgent agent, final Color color, final boolean fill,
		final Color border, final boolean isTextured, final IList<String> textureFileNames, final Integer angle,
		final double height, final boolean roundCorner, final IShape.Type type, final double ratio) {
		if ( currentLayer.isStatic() && staticObjectsAreLocked ) { return; }
		currentLayer.addGeometry(geometry, agent, color, fill, border, isTextured, textureFileNames, angle, height,
			roundCorner, type, ratio);
	}

	public Iterable<GeometryObject> getGeometries() {
		return Iterables.concat(layers.values());
	}

	//
	// public Map<BufferedImage, MyTexture> getTextures() {
	// return textures;
	// }

	public void dispose() {
		textures.clear();
		for ( Map.Entry<String, LayerObject> entry : layers.entrySet() ) {
			LayerObject obj = entry.getValue();
			if ( obj != null ) {
				obj.dispose();
			}
		}
		layers.clear();
	}

	public void beginDrawingLayers() {

	}

	public void endDrawingLayers() {
		staticObjectsAreLocked = true;
	}

	public void reload() {
		staticObjectsAreLocked = false;
		dispose();
		initWorld();
	}

	public void hideLayer(final String name) {
		layers.put(name, null);
	}

	public void beginDrawingLayer(final String name, final Integer id, final GamaPoint offset, final GamaPoint scale,
		final Double alpha, final boolean isStatic, final Integer trace, final Boolean fading) {
		String key = name + String.valueOf(id);
		currentLayer = layers.get(key);
		if ( currentLayer == null ) {
			currentLayer = isStatic ? new StaticLayerObject(renderer, id) : new LayerObject(renderer, id);
			layers.put(key, currentLayer);
		}
		currentLayer.setOffset(offset);
		currentLayer.setScale(scale);
		currentLayer.setAlpha(alpha);
		currentLayer.setTrace(trace);
		currentLayer.setFading(fading);
	}

	public MyTexture createTexture(final String fileName, final boolean isDynamic) {
		try {
			BufferedImage image = ImageUtils.getInstance().getImageFromFile(fileName);
			return createTexture(image, isDynamic);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public MyTexture createTexture(final BufferedImage image, final boolean isDynamic) {
		if ( image == null ) { return null; }
		if ( textures.containsKey(image) ) { return textures.get(image); }
		 renderer.getContext().makeCurrent();
		Texture texture;
		try {
			texture = AWTTextureIO.newTexture(renderer.profile,image, false /* true for mipmapping */);
		} catch (final GLException e) {
			return null;
		}
		texture.setTexParameteri(renderer.gl,GL_TEXTURE_MIN_FILTER, renderer.minAntiAliasing);
		texture.setTexParameteri(renderer.gl,GL_TEXTURE_MAG_FILTER, renderer.magAntiAliasing);
		final MyTexture curTexture = new MyTexture(texture, isDynamic);
		textures.put(image, curTexture);
		return curTexture;
	}

}
