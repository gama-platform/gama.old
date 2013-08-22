package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_TRIANGLES;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.media.opengl.GL;
import msi.gama.jogl.scene.ObjectDrawer.CollectionDrawer;
import msi.gama.jogl.scene.ObjectDrawer.DEMDrawer;
import msi.gama.jogl.scene.ObjectDrawer.GeometryDrawer;
import msi.gama.jogl.scene.ObjectDrawer.ImageDrawer;
import msi.gama.jogl.scene.ObjectDrawer.StringDrawer;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.types.GamaGeometryType;
import org.geotools.data.simple.SimpleFeatureCollection;
import com.vividsolutions.jts.geom.*;

/**
 * 
 * The class ModelScene. A repository for all the objects that constitute the scene of a model : strings, images,
 * shapes...
 * 
 * @author drogoul
 * @since 3 mai 2013
 * 
 */
public class ModelScene {

	private final SceneObjects<GeometryObject> geometries;
	private final SceneObjects<GeometryObject> staticObjects;
	private final SceneObjects<ImageObject> images;
	private final SceneObjects<DEMObject> dems;
	private final SceneObjects<CollectionObject> collections;
	private final SceneObjects<StringObject> strings;
	private final Map<BufferedImage, MyTexture> textures = new LinkedHashMap();
	final GamaPoint offset = new GamaPoint(0, 0, 0);
	final GamaPoint scale = new GamaPoint(1, 1, 1);
	boolean staticObjectsAreLocked;
	final Double envWidth, envHeight;

	public ModelScene(final JOGLAWTGLRenderer renderer) {
		geometries = new SceneObjects(new GeometryDrawer(renderer), true, false);
		collections = new SceneObjects(new CollectionDrawer(renderer), true, false);
		strings = new SceneObjects(new StringDrawer(renderer), !StringDrawer.USE_VERTEX_ARRAYS, false);
		images = new SceneObjects(new ImageDrawer(renderer), true, false);
		dems = new SceneObjects(new DEMDrawer(renderer), true, false);
		staticObjects = new SceneObjects.Static(new GeometryDrawer(renderer), true, false);
		envWidth = renderer.env_width;
		envHeight = renderer.env_height;
	}

	/**
	 * Called every new iteration when updateDisplay() is called on the surface
	 */
	public void wipe(final JOGLAWTGLRenderer renderer) {
		geometries.clear(renderer);
		collections.clear(renderer);
		images.clear(renderer);
		dems.clear(renderer);
		strings.clear(renderer);
		for ( final Iterator<BufferedImage> it = textures.keySet().iterator(); it.hasNext(); ) {
			final BufferedImage im = it.next();
			// FIXME: if an image is not declared as dynamic, it will be kept in memory (even if it is not used)
			if ( textures.get(im).isDynamic ) {
				// FIXME The textures are never disposed. Is it ok ?
				// renderer.getContext().makeCurrent();
				// textures.get(im).texture.dispose();
				it.remove();
			}
		}

	}

	public void draw(final JOGLAWTGLRenderer renderer, final boolean picking, final boolean drawAxes,
		final boolean drawBounds) {
		// renderer.getContext().makeCurrent();
		if ( drawAxes ) {
			this.drawAxes(renderer.gl, renderer.getMaxEnvDim() / 20);
		}
		if ( drawBounds ) {
			this.drawEnvironmentBounds(renderer);
		}
		// FIXME: Need to understand why I need to set this color (otherwise dem model(or model without any geometries)
		// are blue)
		else {
			renderer.gl.glColor3d(1.0, 1.0, 1.0);
		}
		geometries.draw(picking, renderer);
		staticObjects.draw(picking, renderer);
		images.draw(picking, renderer);
		dems.draw(picking, renderer);
		strings.draw(picking, renderer);
	}

	public void addCollections(final SimpleFeatureCollection collection, final Color color) {
		collections.add(new CollectionObject(collection, color));
	}

	public void addString(final String string, final double x, final double y, final double z, final Integer size,
		final Double sizeInModelUnits, final GamaPoint offset, final GamaPoint scale, final Color color,
		final String font, final Integer style, final Integer angle, final Double alpha, final Integer type) {
		strings.add(new StringObject(string, font, style, offset, scale, color, angle, x, y, z, 0, sizeInModelUnits,
			size, alpha, type));
	}

	public void addImage(final BufferedImage img, final IAgent agent, final double z_layer, final int currentLayerId, final Double x, final Double y, final Double z,
		final Double width, final Double height, final Integer angle, final GamaPoint offset, final GamaPoint scale,
		final boolean isDynamic, final Double alpha, final MyTexture texture, final String name) {
		images.add(new ImageObject(img, agent,z_layer, currentLayerId, x, y, Double.isNaN(z) ? 0 : z, alpha, width, height, angle == null ? 0
			: angle, offset, scale, isDynamic, texture, name));
		if ( texture != null ) {
			textures.put(img, texture);
		}
	}
	
	public void addDEM(final double[] dem, final BufferedImage texture, final BufferedImage demImg,
		final boolean isTextured, final boolean isTriangulated, final boolean isShowText, final boolean fromImage,
		final Envelope env, final Double z_factor, final Double alpha, final GamaPoint offset, final GamaPoint scale,
		final int cellSize) {
		dems.add(new DEMObject(dem, texture, demImg, env, isTextured, isTriangulated, isShowText, fromImage, z_factor,
			null, offset, scale, alpha, cellSize));
	}

	public void addGeometry(final Geometry geometry, final IAgent agent, final double z_layer,
		final int currentLayerId, final Color color, final boolean fill, final Color border, final boolean isTextured,
		final Integer angle, final double height, final GamaPoint offSet, final GamaPoint scale,
		final boolean roundCorner, final String type, final boolean currentLayerIsStatic, final double alpha) {
		final GeometryObject curJTSGeometry =
			new GeometryObject(geometry, agent, z_layer, currentLayerId, color, alpha, fill, border, isTextured,
				angle == null ? 0 : angle, height, offSet, scale, roundCorner, type);
		if ( currentLayerIsStatic ) {
			if ( !staticObjectsAreLocked ) {
				staticObjects.add(curJTSGeometry);
			}
		} else {
			geometries.add(curJTSGeometry);
		}
	}
	


	public void drawAxes(final GL gl, final double size) {
		// FIXME Should be put in the static list (get the list from the id of staticObjects)

		gl.glColor4d(0.0d, 0.0d, 0.0d, 1.0d);
		addString("1:" + String.valueOf(size), size, size, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0,
			0, 1d, 1);
		// X Axis
		addString("x", 1.2f * size, 0.0d, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d, 1);
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
		addString("y", 0.0d, 1.2f * size, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d, 1);
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
		addString("z", 0.0d, 0.0d, 1.2f * size, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d, 1);
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

	public void drawEnvironmentBounds(final JOGLAWTGLRenderer renderer) {
		// Draw Width and height value
		addString(String.valueOf(envWidth), envWidth / 2, envHeight * 0.01d, 0.0d, 12, 12d, offset, scale, Color.black,
			"Helvetica", 0, 0, 1d, 1);
		addString(String.valueOf(envHeight), envWidth * 1.01f, -(envHeight / 2), 0.0d, 12, 12d, offset, scale,
			Color.black, "Helvetica", 0, 0, 1d, 1);

		// Draw environment rectangle
		final Geometry g =
			GamaGeometryType.buildRectangle(envWidth, envHeight, new GamaPoint(envWidth / 2, envHeight / 2))
				.getInnerGeometry();
		final Color c = new Color(225, 225, 225);
		addGeometry(g, null, 0, 0, c, false, c, false, 0, 0, offset, scale, true, "env", false, 1d);
	}

	public void drawZValue(final double pos, final float value) {
		addString("z:" + String.valueOf(value), pos, pos, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0,
			1d, 1);
	}

	public SceneObjects<GeometryObject> getGeometries() {
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
		dems.dispose();
		staticObjects.dispose();
	}

	/**
	 * 
	 */
	public void lockStaticObjects() {
		staticObjectsAreLocked = true;
	}

}
