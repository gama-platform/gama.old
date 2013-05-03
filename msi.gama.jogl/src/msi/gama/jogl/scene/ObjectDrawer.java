package msi.gama.jogl.scene;

import static javax.media.opengl.GL.*;
import javax.media.opengl.GL;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import com.sun.opengl.util.GLUT;
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

	final void draw(T object) {
		gl.glTranslated(object.offset.x, -object.offset.y, object.offset.z);
		gl.glScaled(object.scale.x, object.scale.y, 1);
		_draw(object);
		gl.glScaled(1 / object.scale.x, 1 / object.scale.y, 1);
		gl.glTranslated(-object.offset.x, object.offset.y, -object.offset.z);
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
			gl.glTranslated(collection.collection.getBounds().centre().x,
				+collection.collection.getBounds().centre().y, 0.0d);
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

		public StringDrawer(JOGLAWTGLRenderer r) {
			super(r);
		}

		@Override
		protected void _draw(StringObject s) {
			gl.glDisable(GL_BLEND);
			gl.glColor4d(s.color.getRed(), s.color.getGreen(), s.color.getBlue(), 1.0d);
			gl.glRasterPos3d(s.x, s.y, s.z + s.z_layer);
			gl.glScaled(8.0d, 8.0d, 8.0d);
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, s.string);
			gl.glScaled(0.125d, 0.125d, 0.125d);
			gl.glEnable(GL_BLEND);

		}
	}

}
