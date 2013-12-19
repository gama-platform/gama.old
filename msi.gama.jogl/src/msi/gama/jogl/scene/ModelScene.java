package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_TRIANGLES;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.media.opengl.GL;
import msi.gama.jogl.scene.ObjectDrawer.DEMDrawer;
import msi.gama.jogl.scene.ObjectDrawer.GeometryDrawer;
import msi.gama.jogl.scene.ObjectDrawer.ImageDrawer;
import msi.gama.jogl.scene.ObjectDrawer.StringDrawer;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.util.IList;
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
	private final SceneObjects<StringObject> strings;
	private final Map<BufferedImage, MyTexture> textures = new LinkedHashMap();
	final GamaPoint offset = new GamaPoint(0, 0, 0);
	final GamaPoint scale = new GamaPoint(1, 1, 1);
	boolean staticObjectsAreLocked;
	// final Double envWidth, envHeight;
	private boolean envGeometryInitialized = false;

	public ModelScene(final JOGLAWTGLRenderer renderer) {
		geometries = new SceneObjects(new GeometryDrawer(renderer), true, false);
		strings = new SceneObjects(new StringDrawer(renderer), !StringDrawer.USE_VERTEX_ARRAYS, false);
		images = new SceneObjects(new ImageDrawer(renderer), true, false);
		dems = new SceneObjects(new DEMDrawer(renderer), true, false);
		staticObjects = new SceneObjects.Static(new GeometryDrawer(renderer), true, false);
		// envWidth = renderer.env_width;
		// envHeight = renderer.env_height;
	}

	/**
	 * Called every new iteration when updateDisplay() is called on the surface
	 */
	public void wipe(final JOGLAWTGLRenderer renderer) {
		envGeometryInitialized = false;
		//The display is cleared every iteration if not in a trace display mode or when reloading a simulation
		if(!renderer.getTraceDisplay() || (renderer.displaySurface.outputReloaded == true)){ 
			geometries.clear(renderer);
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
			renderer.displaySurface.outputReloaded = false;
		}
		else{
			geometries.openGLListIndex =null;
			images.openGLListIndex =null;
			dems.openGLListIndex =null;
			strings.openGLListIndex =null;	
		}
	}

	public void draw(final JOGLAWTGLRenderer renderer, final boolean picking, final boolean drawEnv) {

		if ( drawEnv ) {
			this.drawAxes(renderer.gl, renderer.getMaxEnvDim() / 10);
		}

		this.drawEnvironmentBounds(renderer, drawEnv);
		geometries.draw(picking, renderer);
		staticObjects.draw(picking, renderer);
		images.draw(picking, renderer);
		dems.draw(picking, renderer);
		strings.draw(picking, renderer);
	}

	public void addString(final String string, final double x, final double y, final double z, final Integer size,
		final Double sizeInModelUnits, final GamaPoint offset, final GamaPoint scale, final Color color,
		final String font, final Integer style, final Integer angle, final Double alpha, final Boolean bitmap) {
		strings.add(new StringObject(string, font, style, offset, scale, color, angle, x, y, z, 0, sizeInModelUnits,
			size, alpha, bitmap));
	}

	public void addImage(final BufferedImage img, final IAgent agent, final double z_layer, final int currentLayerId,
		final Double x, final Double y, final Double z, final Double width, final Double height, final Integer angle,
		final GamaPoint offset, final GamaPoint scale, final boolean isDynamic, final Double alpha,
		final MyTexture texture, final String name) {
		images.add(new ImageObject(img, agent, z_layer, currentLayerId, x, y, Double.isNaN(z) ? 0 : z, alpha, width,
			height, angle == null ? 0 : angle, offset, scale, isDynamic, texture, name));
		if ( texture != null ) {
			textures.put(img, texture);
		}
	}

	public void addDEM(final double[] dem, final BufferedImage demTexture, final BufferedImage demImg,
		final IAgent agent, final boolean isTextured, final boolean isTriangulated, final boolean isShowText,
		final boolean fromImage, final Envelope env, final Double z_factor, final Double alpha, final GamaPoint offset,
		final GamaPoint scale, final double cellSize, final MyTexture texture, final String name, final int currentLayerId) {
		dems.add(new DEMObject(dem, demTexture, demImg, agent, env, isTextured, isTriangulated, isShowText, fromImage,
			z_factor, null, offset, scale, alpha, cellSize, texture, name, currentLayerId));
		if ( texture != null ) {
			textures.put(demTexture, texture);
		}
	}

	public void addGeometry(final Geometry geometry, final IAgent agent, final double z_layer,
		final int currentLayerId, final Color color, final boolean fill, final Color border, final boolean isTextured, final IList<String> textureFileNames,
		final Integer angle, final double height, final GamaPoint offSet, final GamaPoint scale,
		final boolean roundCorner, final String type, final boolean currentLayerIsStatic, final double alpha,
		final String popName) {

		final GeometryObject curJTSGeometry =
			new GeometryObject(geometry, agent, z_layer, currentLayerId, color, alpha, fill, border, isTextured,textureFileNames,
				angle == null ? 0 : angle, height, offSet, scale, roundCorner, type, popName);
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

		// X Axis
		addString("x", 1.2f * size, 0.0d, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d, false);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4d(1.0d, 0, 0, 1.0d);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(size, 0, 0);
		gl.glEnd();
		gl.glBegin(GL_TRIANGLES);
		gl.glVertex3d(0.8d * size, 0.1d * size, 0.0d);
		gl.glVertex3d(0.8d * size, -0.1d * size, 0.0d);
		gl.glVertex3d(1.0d * size, 0.0d, 0.0d);
		gl.glEnd();
		// Y Axis
		addString("y", 0.0d, -1.2f * size, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d, false);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4d(0, 1.0d, 0, 1.0d);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, -size, 0);
		gl.glEnd();
		gl.glBegin(GL_TRIANGLES);
		gl.glVertex3d(-0.1d * size, -0.8d * size, 0.0d);
		gl.glVertex3d(0.1d * size, -0.8d * size, 0.0d);
		gl.glVertex3d(0.0d, -1.0f * size, 0.0d);
		gl.glEnd();
		// Z Axis
		gl.glRasterPos3d(0.0d, 0.0d, 1.2f * size);
		addString("z", 0.0d, 0.0d, 1.2f * size, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0, 1d, false);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4d(0, 0, 1.0d, 1.0d);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, size);
		gl.glEnd();
		gl.glBegin(GL_TRIANGLES);
		gl.glVertex3d(0.0d, 0.1d * size, 0.8d * size);
		gl.glVertex3d(0.0d, -0.1d * size, 0.8d * size);
		gl.glVertex3d(0.0d, 0.0d, 1.0f * size);
		gl.glEnd();

	}

	public void drawEnvironmentBounds(final JOGLAWTGLRenderer renderer, final boolean drawEnv) {
		// Draw environment rectangle
		final Geometry g;
		if ( drawEnv ) {
			double envWidth = renderer.displaySurface.getEnvWidth();
			double envHeight = renderer.displaySurface.getEnvHeight();
			g =
				GamaGeometryType.buildRectangle(envWidth, envHeight, new GamaPoint(envWidth / 2, envHeight / 2))
					.getInnerGeometry();
		} else {// FIXME: don't know why but if this geometry is not added then the color behave randomly
			g = GamaGeometryType.buildRectangle(0, 0, new GamaPoint(0, 0)).getInnerGeometry();
		}
		final Color c = new Color(225, 225, 225);
		if ( !envGeometryInitialized ) {
			addGeometry(g, GAMA.getSimulation().getAgent(), 0, 0, c, false, c, false, null, 0, 0, offset, scale, false,
				"env", false, 1d, "environment");
			envGeometryInitialized = true;
		}
	}

	public void drawZValue(final double pos, final float value) {
		addString("z:" + String.valueOf(value), pos, pos, 0.0d, 12, 12d, offset, scale, Color.black, "Helvetica", 0, 0,
			1d, false);
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
