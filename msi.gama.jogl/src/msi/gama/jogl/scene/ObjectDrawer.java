package msi.gama.jogl.scene;

import static javax.media.opengl.GL.*;
import java.awt.Font;
import java.util.*;
import javax.media.opengl.GL;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.*;
import com.vividsolutions.jts.geom.*;

public abstract class ObjectDrawer<T extends AbstractObject> {

	final JOGLAWTGLRenderer renderer;
	final GLUT glut = new GLUT();
	final GL gl;

	public ObjectDrawer(JOGLAWTGLRenderer r) {
		renderer = r;
		gl = r.gl;
	}

	// Better to subclass _draw than this one
	void draw(T object) {
		gl.glPushMatrix();
		gl.glTranslated(object.offset.x, -object.offset.y, object.offset.z);
		gl.glScaled(object.scale.x, object.scale.y, 1);
		_draw(object);
		gl.glPopMatrix();
	}

	protected abstract void _draw(T object);

	/**
	 * 
	 * The class ImageDrawer.
	 * 
	 * @author drogoul
	 * @since 4 mai 2013
	 * 
	 */
	public static class ImageDrawer extends ObjectDrawer<ImageObject> {

		public float textureTop, textureBottom, textureLeft, textureRight;

		public ImageDrawer(JOGLAWTGLRenderer r) {
			super(r);
		}

		@Override
		protected void _draw(ImageObject img) {
			MyTexture curTexture = renderer.getScene().getTextures().get(img.image);
			if ( curTexture == null ) { return; }
			// Enable the texture
			gl.glEnable(GL_TEXTURE_2D);
			Texture t = curTexture.texture;
			t.enable();
			t.bind();
			// Reset opengl color. Set the transparency of the image to
			// 1 (opaque).
			gl.glColor4d(1.0d, 1.0d, 1.0d, img.alpha);
			TextureCoords textureCoords;
			textureCoords = t.getImageTexCoords();
			textureTop = textureCoords.top();
			textureBottom = textureCoords.bottom();
			textureLeft = textureCoords.left();
			textureRight = textureCoords.right();
			if ( img.angle != 0 ) {
				gl.glTranslated(img.x + img.width / 2, -(img.y + img.height / 2), 0.0d);
				// FIXME:Check counterwise or not, and do we rotate
				// around the center or around a point.
				gl.glRotated(-img.angle, 0.0d, 0.0d, 1.0d);
				gl.glTranslated(-(img.x + img.width / 2), +(img.y + img.height / 2), 0.0d);

				gl.glBegin(GL_QUADS);
				// bottom-left of the texture and quad
				gl.glTexCoord2f(textureLeft, textureBottom);
				gl.glVertex3d(img.x, -(img.y + img.height), img.z);
				// bottom-right of the texture and quad
				gl.glTexCoord2f(textureRight, textureBottom);
				gl.glVertex3d(img.x + img.width, -(img.y + img.height), img.z);
				// top-right of the texture and quad
				gl.glTexCoord2f(textureRight, textureTop);
				gl.glVertex3d(img.x + img.width, -img.y, img.z);
				// top-left of the texture and quad
				gl.glTexCoord2f(textureLeft, textureTop);
				gl.glVertex3d(img.x, -img.y, img.z);
				gl.glEnd();
				gl.glTranslated(img.x + img.width / 2, -(img.y + img.height / 2), 0.0d);
				gl.glRotated(img.angle, 0.0d, 0.0d, 1.0d);
				gl.glTranslated(-(img.x + img.width / 2), +(img.y + img.height / 2), 0.0d);
			} else {
				gl.glBegin(GL_QUADS);
				// bottom-left of the texture and quad
				gl.glTexCoord2f(textureLeft, textureBottom);
				gl.glVertex3d(img.x, -(img.y + img.height), img.z);
				// bottom-right of the texture and quad
				gl.glTexCoord2f(textureRight, textureBottom);
				gl.glVertex3d(img.x + img.width, -(img.y + img.height), img.z);
				// top-right of the texture and quad
				gl.glTexCoord2f(textureRight, textureTop);
				gl.glVertex3d(img.x + img.width, -img.y, img.z);
				// top-left of the texture and quad
				gl.glTexCoord2f(textureLeft, textureTop);
				gl.glVertex3d(img.x, -img.y, img.z);
				gl.glEnd();
			}
			gl.glDisable(GL_TEXTURE_2D);
		}
	}

	/**
	 * 
	 * The class GeometryDrawer.
	 * 
	 * @author drogoul
	 * @since 4 mai 2013
	 * 
	 */
	public static class GeometryDrawer extends ObjectDrawer<GeometryObject> {

		JTSDrawer jtsDrawer;

		public GeometryDrawer(JOGLAWTGLRenderer r) {
			super(r);
			jtsDrawer = new JTSDrawer(r);
		}

		@Override
		protected void _draw(GeometryObject geometry) {
			// Rotate angle (in XY plan)
			if ( geometry.angle != 0 ) {
				gl.glTranslated(geometry.geometry.getCentroid().getX(), this.jtsDrawer.yFlag *
					geometry.geometry.getCentroid().getY(), 0.0d);
				gl.glRotated(-geometry.angle, 0.0d, 0.0d, 1.0d);
				gl.glTranslated(-geometry.geometry.getCentroid().getX(), -this.jtsDrawer.yFlag *
					geometry.geometry.getCentroid().getY(), 0.0d);
			}
			for ( int i = 0; i < geometry.geometry.getNumGeometries(); i++ ) {
				if ( geometry.geometry.getGeometryType() == "MultiPolygon" ) {
					jtsDrawer.DrawMultiPolygon((MultiPolygon) geometry.geometry, geometry.z_layer, geometry.color,
						geometry.alpha, geometry.fill, geometry.border, geometry.angle, geometry.height,
						geometry.rounded);
				} else if ( geometry.geometry.getGeometryType() == "Polygon" ) {
					// The JTS geometry of a sphere is a circle (a polygon)
					if ( geometry.type.equals("sphere") ) {
						jtsDrawer.DrawSphere(geometry.agent.getLocation(), geometry.z_layer, geometry.height,
							geometry.color, geometry.alpha);
					} else {
						if ( geometry.height > 0 ) {
							jtsDrawer.DrawPolyhedre((Polygon) geometry.geometry, geometry.z_layer, geometry.color,
								geometry.alpha, geometry.fill, geometry.height, geometry.angle, true, geometry.border,
								geometry.rounded);
						} else {
							jtsDrawer.DrawPolygon((Polygon) geometry.geometry, geometry.z_layer, geometry.color,
								geometry.alpha, geometry.fill, geometry.border, geometry.isTextured, geometry.angle,
								true, geometry.rounded);
						}
					}
				} else if ( geometry.geometry.getGeometryType() == "MultiLineString" ) {

					jtsDrawer.DrawMultiLineString((MultiLineString) geometry.geometry, geometry.z_layer,
						geometry.color, geometry.alpha, geometry.height);
				} else if ( geometry.geometry.getGeometryType() == "LineString" ) {

					if ( geometry.height > 0 ) {
						jtsDrawer.DrawPlan((LineString) geometry.geometry, geometry.z_layer, geometry.color,
							geometry.alpha, geometry.height, 0, true);
					} else {
						jtsDrawer.DrawLineString((LineString) geometry.geometry, geometry.z_layer, 1.2f,
							geometry.color, geometry.alpha);
					}
				} else if ( geometry.geometry.getGeometryType() == "Point" ) {
					if ( geometry.height > 0 ) {
						jtsDrawer.DrawSphere(geometry.agent.getLocation(), geometry.z_layer, geometry.height,
							geometry.color, geometry.alpha);
					} else {
						jtsDrawer.DrawPoint((Point) geometry.geometry, geometry.z_layer, 10,
							renderer.getMaxEnvDim() / 1000, geometry.color, geometry.alpha);
					}
				}
			}
			// Rotate angle (in XY plan)
			if ( geometry.angle != 0 ) {
				gl.glTranslated(geometry.geometry.getCentroid().getX(), this.jtsDrawer.yFlag *
					geometry.geometry.getCentroid().getY(), 0.0d);
				gl.glRotated(geometry.angle, 0.0d, 0.0d, 1.0d);
				gl.glTranslated(-geometry.geometry.getCentroid().getX(), -this.jtsDrawer.yFlag *
					geometry.geometry.getCentroid().getY(), 0.0d);

			}
		}
	}

	/**
	 * 
	 * The class CollectionDrawer.
	 * 
	 * @author drogoul
	 * @since 4 mai 2013
	 * 
	 */
	public static class CollectionDrawer extends ObjectDrawer<CollectionObject> {

		JTSDrawer jtsDrawer;

		public CollectionDrawer(JOGLAWTGLRenderer r) {
			super(r);
			jtsDrawer = new JTSDrawer(r);
		}

		@Override
		public void _draw(CollectionObject collection) {
			// Draw Shape file so need to inverse the y composante.
			jtsDrawer.yFlag = 1;
			gl.glPushMatrix();
			gl.glTranslated(-collection.collection.getBounds().centre().x,
				-collection.collection.getBounds().centre().y, 0.0d);
			// Iterate throught all the collection
			SimpleFeatureIterator iterator = collection.collection.features();
			// Color color= Color.red;
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry sourceGeometry = (Geometry) feature.getDefaultGeometry();
				if ( sourceGeometry.getGeometryType() == "MultiPolygon" ) {
					jtsDrawer.DrawMultiPolygon((MultiPolygon) sourceGeometry, 0.0d, collection.color, 1.0d, true, null,
						0, 0.0d, false);
				} else if ( sourceGeometry.getGeometryType() == "Polygon" ) {
					jtsDrawer.DrawPolygon((Polygon) sourceGeometry, 0.0d, collection.color, 1.0d, true, null, false, 0,
						true, false);
				} else if ( sourceGeometry.getGeometryType() == "MultiLineString" ) {
					jtsDrawer.DrawMultiLineString((MultiLineString) sourceGeometry, 0.0d, collection.color, 1.0d, 0.0d);
				} else if ( sourceGeometry.getGeometryType() == "LineString" ) {
					jtsDrawer.DrawLineString((LineString) sourceGeometry, 0.0d, 1.0d, collection.color, 1.0d);
				} else if ( sourceGeometry.getGeometryType() == "Point" ) {
					jtsDrawer.DrawPoint((Point) sourceGeometry, 0.0d, 10, 10, collection.color, 1.0d);
				}
			}
			gl.glPopMatrix();
			jtsDrawer.yFlag = -1;
		}
	}

	/**
	 * 
	 * The class StringDrawer.
	 * 
	 * @author drogoul
	 * @since 4 mai 2013
	 * 
	 */

	public static class StringDrawer extends ObjectDrawer<StringObject> {

		// Setting it to true requires that the ModelScene handles strings outside a list (see ModelScene)
		static final boolean USE_VERTEX_ARRAYS = false;
		Map<String, Map<Integer, Map<Integer, TextRenderer>>> cache = new LinkedHashMap();

		// static String[] fonts = { "Helvetica", "Geneva", "Times", "Courier", "Arial", "Gill Sans", "Sans Serif" };
		// static int[] sizes = { 8, 10, 12, 14, 16, 20, 24, 36, 48, 60, 72 };
		// static int[] styles = { Font.PLAIN, Font.BOLD, Font.ITALIC };
		// static {
		// for ( String f : fonts ) {
		// for ( int s : sizes ) {
		// for ( int t : styles ) {
		// get(f, s, t);
		// }
		//
		// }
		// }
		// }

		TextRenderer get(String font, int size, int style) {
			Map<Integer, Map<Integer, TextRenderer>> map1 = cache.get(font);
			if ( map1 == null ) {
				map1 = new HashMap();
				cache.put(font, map1);
			}
			Map<Integer, TextRenderer> map2 = map1.get(size);
			if ( map2 == null ) {
				map2 = new HashMap();
				map1.put(size, map2);
			}
			TextRenderer r = map2.get(style);
			if ( r == null ) {
				r = new TextRenderer(new Font(font, style, size), true, true, null, true);
				r.setSmoothing(true);
				r.setUseVertexArrays(USE_VERTEX_ARRAYS);
				map2.put(style, r);
			}
			return r;
		}

		public StringDrawer(JOGLAWTGLRenderer r) {
			super(r);
		}

		protected void _drawOld(StringObject s) {
			gl.glDisable(GL_BLEND);
			gl.glColor4d(s.color.getRed(), s.color.getGreen(), s.color.getBlue(), 1.0d);
			gl.glRasterPos3d(s.x, s.y, s.z + s.z_layer);
			gl.glScaled(8.0d, 8.0d, 8.0d);
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, s.string);
			gl.glScaled(0.125d, 0.125d, 0.125d);
			gl.glEnable(GL_BLEND);

		}

		@Override
		public void draw(StringObject object) {
			// gl.glTranslated(object.offset.x, -object.offset.y, object.offset.z);
			// gl.glScaled(object.scale.x, object.scale.y, 1);
			_draw(object);
			// gl.glScaled(1 / object.scale.x, 1 / object.scale.y, 1);
			// gl.glTranslated(-object.offset.x, object.offset.y, -object.offset.z);

		}

		@Override
		protected void _draw(StringObject s) {

			// FIXME height ?
			// renderer.getContext().makeCurrent();
			// GuiUtils.debug("ObjectDrawer.StringDrawer._draw env size " + renderer.getWidth() + " ;" +
			// renderer.getHeight());

			TextRenderer r = get(s.font, s.size, s.style);
			r.setColor(s.color);
			r.begin3DRendering();
			float x = (float) ((float) s.x * s.scale.x + s.offset.x);
			float y = (float) ((float) s.y * s.scale.y - s.offset.y);
			// GuiUtils.debug("ObjectDrawer.StringDrawer._draw '" + s.string + "' at " + x + " ; " + y + " [original: "
			// +
			// s.x + " ; " + s.y + "]" + "[offset: " + s.offset.x + " ; " + s.offset.y + "]");
			// gl.glTranslated(s.offset.x, -s.offset.y, s.offset.z);
			gl.glPushMatrix();
			// gl.glScaled(s.scale.x, s.scale.y, 1);
			r.draw3D(s.string, x, y, (float) (s.z + s.z_layer),
				(float) (/* s.scale.y * */renderer.env_height / renderer.getHeight()));
			// gl.glScaled(1 / s.scale.x, 1 / s.scale.y, 1);
			gl.glPopMatrix();
			// gl.glTranslated(-s.offset.x, s.offset.y, -s.offset.z);
			r.end3DRendering();
			// r.begin3DRendering();
			// r.draw3D(s.string, (float) s.x, (float) s.y, (float) (s.z + s.z_layer), s.alpha.floatValue());
			// r.flush();
			// r.end3DRendering();
			// renderer.getContext().release();
			// gl.glDisable(GL_BLEND);
			// gl.glColor4d(s.color.getRed(), s.color.getGreen(), s.color.getBlue(), 1.0d);
			// gl.glRasterPos3d(s.x, s.y, s.z + s.z_layer);
			// gl.glScaled(8.0d, 8.0d, 8.0d);
			// glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, s.string);
			// gl.glScaled(0.125d, 0.125d, 0.125d);
			// gl.glEnable(GL_BLEND);

		}

		@Override
		public void dispose() {
			cache.clear();
		}
	}

	public void dispose() {}

}
