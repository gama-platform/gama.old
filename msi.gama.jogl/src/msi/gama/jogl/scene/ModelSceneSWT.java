package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_TRIANGLES;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.media.opengl.GL;
import msi.gama.jogl.scene.ObjectDrawerSWT.CollectionDrawerSWT;
import msi.gama.jogl.scene.ObjectDrawerSWT.GeometryDrawerSWT;
import msi.gama.jogl.scene.ObjectDrawerSWT.ImageDrawerSWT;
import msi.gama.jogl.scene.ObjectDrawerSWT.StringDrawerSWT;
import msi.gama.jogl.utils.JOGLSWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.types.GamaGeometryType;
import org.geotools.data.simple.SimpleFeatureCollection;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * The class ModelScene. A repository for all the objects that constitute the scene of a model : strings, images,
 * shapes...
 * 
 * @author drogoul
 * @since 3 mai 2013
 * 
 */
public class ModelSceneSWT {

	private final SceneObjectsSWT<GeometryObjectSWT> geometries;
	private final SceneObjectsSWT<GeometryObjectSWT> staticObjects;
	private final SceneObjectsSWT<ImageObjectSWT> images;
	private final SceneObjectsSWT<CollectionObjectSWT> collections;
	private final SceneObjectsSWT<StringObjectSWT> strings;
	private final Map<BufferedImage, MyTexture> textures = new LinkedHashMap();
	final GamaPoint offset = new GamaPoint(0, 0, 0);
	final GamaPoint scale = new GamaPoint(1, 1, 1);
	final Double envWidth, envHeight;

	public ModelSceneSWT(JOGLSWTGLRenderer renderer) {
		geometries = new SceneObjectsSWT(new GeometryDrawerSWT(renderer), true);
		collections = new SceneObjectsSWT(new CollectionDrawerSWT(renderer), true);
		strings = new SceneObjectsSWT(new StringDrawerSWT(renderer), !StringDrawerSWT.USE_VERTEX_ARRAYS);
		images = new SceneObjectsSWT(new ImageDrawerSWT(renderer), true);
		staticObjects = new SceneObjectsSWT.Static(new GeometryDrawerSWT(renderer), true);
		envWidth = renderer.env_width;
		envHeight = renderer.env_height;
	}

	/**
	 * Called every new iteration when updateDisplay() is called on the surface
	 */
	public void wipe(JOGLSWTGLRenderer renderer) {
		geometries.clear(renderer);
		collections.clear(renderer);
		images.clear(renderer);
		strings.clear(renderer);
		for ( Iterator<BufferedImage> it = textures.keySet().iterator(); it.hasNext(); ) {
			BufferedImage im = it.next();
			// FIXME: if an image is not declared as dynamic, it will be kept in memory (even if it is not used)
			if ( textures.get(im).isDynamic ) {
				// FIXME The textures are never disposed. Is it ok ?
				// renderer.getContext().makeCurrent();
				// textures.get(im).texture.dispose();
				it.remove();
			}
		}

	}

	public void draw(JOGLSWTGLRenderer renderer, boolean picking, boolean drawAxes, boolean drawBounds) {
		System.out.println("draw-----------");
		if ( drawAxes ) {
			System.out.println(" if draw axes"+renderer.getMaxEnvDim() / 20);
			this.drawAxes(renderer.gl, renderer.getMaxEnvDim() / 20);
			drawZValue(-renderer.getMaxEnvDim() / 20, (float) renderer.camera.getPosition().getZ());
		}
		if ( drawBounds ) {
			this.drawEnvironmentBounds(renderer);
		}
		geometries.draw(picking);
		staticObjects.draw(picking);
		images.draw(picking);
		strings.draw(picking);
	}

	public void addCollections(final SimpleFeatureCollection collection, final Color color) {
		collections.add(new CollectionObjectSWT(collection, color));
	}

	public void addString(final String string, final double x, final double y, final double z, Integer size,
		Double sizeInModelUnits, GamaPoint offset, GamaPoint scale, Color color, String font, Integer style,
		Integer angle, Double alpha) {
		strings.add(new StringObjectSWT(string, font, style, offset, scale, color, angle, x, y, z, 0, sizeInModelUnits,
			size, alpha));
	}

	public void addImage(final BufferedImage img, final IAgent agent, final Double x, final Double y, final Double z,
		final Double width, final Double height, final Integer angle, final GamaPoint offset, final GamaPoint scale,
		final boolean isDynamic, Double alpha, MyTexture texture) {
		images.add(new ImageObjectSWT(img, agent, x, y, Double.isNaN(z) ? 0 : z, alpha, width, height, angle == null ? 0
			: angle, offset, scale, isDynamic, texture));
		if ( texture != null ) {
			textures.put(img, texture);
		}
	}

	public void addGeometry(final Geometry geometry, final IAgent agent, final double z_layer,
		final int currentLayerId, final Color color, final boolean fill, final Color border, final boolean isTextured,
		final Integer angle, final double height, final GamaPoint offSet, GamaPoint scale, final boolean roundCorner,
		final String type, boolean currentLayerIsStatic, double alpha) {
		GeometryObjectSWT curJTSGeometry =
			new GeometryObjectSWT(geometry, agent, z_layer, currentLayerId, color, alpha, fill, border, isTextured,
				angle == null ? 0 : angle, height, offSet, scale, roundCorner, type);
		if ( currentLayerIsStatic ) {
			staticObjects.add(curJTSGeometry);
		} else {
			geometries.add(curJTSGeometry);
		}
	}

	public void drawAxes(GL gl, final double size) {
		// FIXME Should be put in the static list (get the list from the id of staticObjects)
		System.out.println("draw axes");
		gl.glColor4d(0.0d, 0.0d, 0.0d, 1.0d);
		addString("1:" + String.valueOf(size), size, size, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0,
			0, 1d);
		// X Axis
		addString("x", 1.2f * size, 0.0d, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4d(1.0d, 0, 0, 1.0d);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(size, 0, 0);
		gl.glEnd();
		gl.glBegin(GL_TRIANGLES);
		gl.glVertex3d(1.0d * size, 0.1d * size, 0.0d);
		gl.glVertex3d(1.0d * size, -0.1d * size, 0.0d);
		gl.glVertex3d(1.2d * size, 0.0d, 0.0d);
		gl.glEnd();
		// Y Axis
		addString("y", 0.0d, 1.2f * size, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4d(0, 1.0d, 0, 1.0d);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, size, 0);
		gl.glEnd();
		gl.glBegin(GL_TRIANGLES);
		gl.glVertex3d(-0.05d * size, 1.0d * size, 0.0d);
		gl.glVertex3d(0.05d * size, 1.0d * size, 0.0d);
		gl.glVertex3d(0.0d, 1.1f * size, 0.0d);
		gl.glEnd();
		// Z Axis
		gl.glRasterPos3d(0.0d, 0.0d, 1.2f * size);
		addString("z", 0.0d, 0.0d, 1.2f * size, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4d(0, 0, 1.0d, 1.0d);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, size);
		gl.glEnd();
		gl.glBegin(GL_TRIANGLES);
		gl.glVertex3d(0.0d, 0.05d * size, 1.0d * size);
		gl.glVertex3d(0.0d, -0.05d * size, 1.0d * size);
		gl.glVertex3d(0.0d, 0.0d, 1.1f * size);
		gl.glEnd();

	}

	public void drawEnvironmentBounds(JOGLSWTGLRenderer renderer) {
		// Draw Width and height value
		addString(String.valueOf(envWidth), envWidth / 2, envHeight * 0.01d, 0.0d, 12, 12d, offset, scale, Color.black,
			"Helvetica", 0, 0, 1d);
		addString(String.valueOf(envHeight), envWidth * 1.01f, -(envHeight / 2), 0.0d, 12, 12d, offset, scale,
			Color.black, "Helvetica", 0, 0, 1d);

		// Draw environment rectangle
		Geometry g =
			GamaGeometryType.buildRectangle(envWidth, envHeight, new GamaPoint(envWidth / 2, envHeight / 2))
				.getInnerGeometry();
		Color c = new Color(225, 225, 225);
		addGeometry(g, null, 0, 0, c, false, c, false, 0, 0, offset, scale, true, "", false, 1d);
	}

	public void drawZValue(final double pos, final float value) {
		addString("z:" + String.valueOf(value), pos, pos, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0,
			1d);
	}

	public SceneObjectsSWT<GeometryObjectSWT> getGeometries() {
		return geometries;
	}

	public Map<BufferedImage, MyTexture> getTextures() {
		return textures;
	}

	public void dispose() {
		textures.clear();
		geometries.dispose();
		strings.dispose();
		images.dispose();
		staticObjects.dispose();
	}

}
