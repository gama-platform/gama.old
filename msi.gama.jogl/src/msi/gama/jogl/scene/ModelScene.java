package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_COMPILE;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import msi.gama.jogl.scene.ObjectDrawer.CollectionDrawer;
import msi.gama.jogl.scene.ObjectDrawer.GeometryDrawer;
import msi.gama.jogl.scene.ObjectDrawer.ImageDrawer;
import msi.gama.jogl.scene.ObjectDrawer.StringDrawer;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
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
public class ModelScene {

	public class SceneObjects<T extends AbstractObject> implements Iterable<T> {

		final ObjectDrawer<T> drawer;
		final List<T> objects = new ArrayList();
		Integer openGLListIndex;

		SceneObjects(ObjectDrawer<T> drawer) {
			this.drawer = drawer;
		}

		@Override
		public Iterator<T> iterator() {
			return objects.iterator();
		}

		protected void clearObjects() {
			objects.clear();
		}

		public void clear(JOGLAWTGLRenderer renderer) {
			clearObjects();
			if ( openGLListIndex != null ) {
				renderer.gl.glDeleteLists(openGLListIndex, 1);
				openGLListIndex = null;
			}
		}

		public Integer getIndexInOpenGLList() {
			return openGLListIndex;
		}

		public void setIndexInOpenGLList(Integer index) {
			this.openGLListIndex = index;
		}

		public List<T> getObjects() {
			return objects;
		}

		public void add(T object) {
			objects.add(object);
		}

		public void draw(boolean picking) {
			if ( picking ) {
				drawer.gl.glPushMatrix();
				drawer.gl.glInitNames();
				drawer.gl.glPushName(0);
				for ( T object : objects ) {
					object.draw(drawer, picking);
				}
				drawer.gl.glPopName();
				drawer.gl.glPopMatrix();
			} else {
				if ( openGLListIndex == null ) {
					openGLListIndex = drawer.gl.glGenLists(1);
					drawer.gl.glNewList(openGLListIndex, GL_COMPILE);
					for ( T object : objects ) {
						object.draw(drawer, picking);
					}
					drawer.gl.glEndList();
				}
				drawer.gl.glCallList(openGLListIndex);
			}
		}

	}

	public class StaticObjects<T extends AbstractObject> extends SceneObjects<T> {

		StaticObjects(ObjectDrawer<T> drawer) {
			super(drawer);
		}

		@Override
		public void add(T object) {
			if ( openGLListIndex != null ) { return; }
			super.add(object);
		}

		@Override
		public void clear(JOGLAWTGLRenderer renderer) {}
	}

	private final SceneObjects<GeometryObject> geometries;
	private final SceneObjects<GeometryObject> staticObjects;
	private final SceneObjects<ImageObject> images;
	private final SceneObjects<CollectionObject> collections;
	private final SceneObjects<StringObject> strings;
	private final Map<BufferedImage, MyTexture> textures = new LinkedHashMap();

	public ModelScene(JOGLAWTGLRenderer renderer) {
		geometries = new SceneObjects(new GeometryDrawer(renderer));
		collections = new SceneObjects(new CollectionDrawer(renderer));
		strings = new SceneObjects(new StringDrawer(renderer));
		images = new SceneObjects(new ImageDrawer(renderer));
		staticObjects = new StaticObjects(new GeometryDrawer(renderer));
	}

	/**
	 * Called every new iteration when updateDisplay() is called on the surface
	 */
	public void wipe(JOGLAWTGLRenderer renderer) {
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

	public void draw(boolean picking) {
		geometries.draw(picking);
		staticObjects.draw(picking);
		images.draw(picking);
		strings.draw(picking);
	}

	public void addCollections(final SimpleFeatureCollection collection, final Color color) {
		collections.add(new CollectionObject(collection, color));
	}

	public void addString(final String string, final double x, final double y, final double z, double height,
		GamaPoint offset, GamaPoint scale, Color color, String fontName, Integer styleName, Integer angle, Double alpha) {
		// FIXME Add Font information like GLUT.BITMAP_TIMES_ROMAN_24;
		strings.add(new StringObject(string, fontName, styleName, offset, scale, color, angle, x, y, z, 0, height,
			alpha));
	}

	public void addImage(final BufferedImage img, final IAgent agent, final Double x, final Double y, final Double z,
		final Double width, final Double height, final Integer angle, final GamaPoint offset, final GamaPoint scale,
		final boolean isDynamic, Double alpha, MyTexture texture) {
		images.add(new ImageObject(img, agent, x, y, Double.isNaN(z) ? 0 : z, alpha, width, height, angle == null ? 0
			: angle, offset, scale, isDynamic, texture));
		if ( texture != null ) {
			textures.put(img, texture);
		}
	}

	public void addGeometry(final Geometry geometry, final IAgent agent, final double z_layer,
		final int currentLayerId, final Color color, final boolean fill, final Color border, final boolean isTextured,
		final Integer angle, final double height, final GamaPoint offSet, GamaPoint scale, final boolean roundCorner,
		final String type, boolean currentLayerIsStatic, double alpha) {
		GeometryObject curJTSGeometry =
			new GeometryObject(geometry, agent, z_layer, currentLayerId, color, alpha, fill, border, isTextured,
				angle == null ? 0 : angle, height, offSet, scale, roundCorner, type);
		if ( currentLayerIsStatic ) {
			staticObjects.add(curJTSGeometry);
		} else {
			geometries.add(curJTSGeometry);
		}
	}

	public SceneObjects<GeometryObject> getGeometries() {
		return geometries;
	}

	public Map<BufferedImage, MyTexture> getTextures() {
		return textures;
	}

}
