package msi.gama.jogl.scene;

import static javax.media.opengl.GL.*;
import java.awt.Font;
import java.awt.image.*;
import java.io.File;
import java.util.*;
import javax.media.opengl.GL;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.*;
import com.vividsolutions.jts.geom.*;

public abstract class ObjectDrawer<T extends AbstractObject> {

	final JOGLAWTGLRenderer renderer;
	final GLUT glut = new GLUT();

	public ObjectDrawer(final JOGLAWTGLRenderer r) {
		renderer = r;
	}

	// Better to subclass _draw than this one
	void draw(final T object) {
		renderer.gl.glPushMatrix();
		renderer.gl.glTranslated(object.offset.x, -object.offset.y, object.offset.z);
		renderer.gl.glScaled(object.scale.getX(), object.scale.getY(), object.scale.getZ());
		if ( renderer.getZFighting() ) {
			SetPolygonOffset(object);
		}
		_draw(object);
		renderer.gl.glPopMatrix();
	}

	void SetPolygonOffset(final T object) {
		if ( !object.fill ) {
			renderer.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			renderer.gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			renderer.gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
			renderer.gl.glPolygonOffset(0.0f, -(object.getZ_fighting_id().floatValue() + 0.1f));
		} else {
			renderer.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			renderer.gl.glDisable(GL.GL_POLYGON_OFFSET_LINE);
			renderer.gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			renderer.gl.glPolygonOffset(1, -object.getZ_fighting_id().floatValue());
		}
	}

	protected abstract void _draw(T object);

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

		public GeometryDrawer(final JOGLAWTGLRenderer r) {
			super(r);
			jtsDrawer = new JTSDrawer(r);
		}

		@Override
		protected void _draw(final GeometryObject geometry) {
			// Rotate angle (in XY plan)
			if ( geometry.angle != 0 ) {
				renderer.gl.glTranslated(geometry.geometry.getCentroid().getX(), this.jtsDrawer.yFlag *
					geometry.geometry.getCentroid().getY(), 0.0d);
				renderer.gl.glRotated(-geometry.angle, 0.0d, 0.0d, 1.0d);
				renderer.gl.glTranslated(-geometry.geometry.getCentroid().getX(), -this.jtsDrawer.yFlag *
					geometry.geometry.getCentroid().getY(), 0.0d);
			}
			for ( int i = 0; i < geometry.geometry.getNumGeometries(); i++ ) {
				if ( renderer.getStencil() ) {
					renderer.gl.glEnable(GL_DEPTH_TEST);
					renderer.gl.glStencilFunc(GL_GREATER, 1, 1);
				}
				if ( geometry.geometry.getGeometryType() == "MultiPolygon" ) {
					jtsDrawer.DrawMultiPolygon((MultiPolygon) geometry.geometry, geometry.getColor(), geometry.alpha,
						geometry.fill, geometry.border, geometry.angle, geometry.height, geometry.rounded,
						geometry.getZ_fighting_id());
				} else if ( geometry.geometry.getGeometryType() == "Polygon" ) {
					// The JTS geometry of a sphere is a circle (a polygon)
					/*
					 * GuiUtils.debug(" exterior ring x: " +
					 * ((Polygon)geometry.geometry).getExteriorRing().getCoordinateN(0).x);
					 * GuiUtils.debug(" exterior ring y: " +
					 * ((Polygon)geometry.geometry).getExteriorRing().getCoordinateN(0).y);
					 * GuiUtils.debug(" exterior ring z: " +
					 * ((Polygon)geometry.geometry).getExteriorRing().getCoordinateN(0).z);
					 * GuiUtils.debug(" agent: " + geometry.agent.getLocation());
					 * GuiUtils.debug(" centroid: " + geometry.geometry.getCentroid().getCoordinate().z);
					 */
					if ( geometry.type != null && geometry.type.equals("sphere") ) {
						jtsDrawer.DrawSphere((Polygon) geometry.geometry, geometry.height, geometry.getColor(),
							geometry.alpha);
					} else {
						if ( geometry.height > 0 ) {
							jtsDrawer.DrawPolyhedre((Polygon) geometry.geometry, geometry.getColor(), geometry.alpha,
								geometry.fill, geometry.height, geometry.angle, true, geometry.border,
								geometry.rounded, geometry.getZ_fighting_id());
						} else {
							if ( renderer.getStencil() ) {
								renderer.gl.glStencilFunc(GL_ALWAYS, 0, 1);
								renderer.gl.glDisable(GL_DEPTH_TEST);
								renderer.gl.glStencilOp(GL_KEEP, GL_ZERO, GL_REPLACE);
							}
							jtsDrawer.DrawPolygon((Polygon) geometry.geometry, geometry.getColor(), geometry.alpha,
								geometry.fill, geometry.border, geometry.isTextured, geometry.angle, true,
								geometry.rounded, geometry.getZ_fighting_id());
						}
					}
				} else if ( geometry.geometry.getGeometryType() == "MultiLineString" ) {

					jtsDrawer.DrawMultiLineString((MultiLineString) geometry.geometry, 0, geometry.getColor(),
						geometry.alpha, geometry.height);
				} else if ( geometry.geometry.getGeometryType() == "LineString" ) {

					if ( geometry.height > 0 ) {
						jtsDrawer.DrawPlan((LineString) geometry.geometry, 0, geometry.getColor(), geometry.alpha,
							geometry.height, 0, true);
					} else {
						jtsDrawer.DrawLineString((LineString) geometry.geometry, 0, 1.2f, geometry.getColor(),
							geometry.alpha);
					}
				} else if ( geometry.geometry.getGeometryType() == "Point" ) {
					// FIXME: Should never go here even with a height value as the geometry of a sphere is a polygon...
					if ( geometry.height > 0 ) {
						jtsDrawer.DrawSphere((Polygon) geometry.geometry.getEnvelope().buffer(1), geometry.height,
							geometry.getColor(), geometry.alpha);
					} else {
						jtsDrawer.DrawPoint((Point) geometry.geometry, 0, 10, renderer.getMaxEnvDim() / 1000,
							geometry.getColor(), geometry.alpha);
					}
				}
			}
			// Rotate angle (in XY plan)
			if ( geometry.angle != 0 ) {
				renderer.gl.glTranslated(geometry.geometry.getCentroid().getX(), this.jtsDrawer.yFlag *
					geometry.geometry.getCentroid().getY(), 0.0d);
				renderer.gl.glRotated(geometry.angle, 0.0d, 0.0d, 1.0d);
				renderer.gl.glTranslated(-geometry.geometry.getCentroid().getX(), -this.jtsDrawer.yFlag *
					geometry.geometry.getCentroid().getY(), 0.0d);

			}
		}
	}

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

		public ImageDrawer(final JOGLAWTGLRenderer r) {
			super(r);
		}

		@Override
		protected void _draw(final ImageObject img) {

			MyTexture curTexture = renderer.getScene().getTextures().get(img.image);
			if ( curTexture == null ) { return; }
			// Enable the texture
			renderer.gl.glEnable(GL_TEXTURE_2D);
			Texture t = curTexture.texture;
			t.enable();
			t.bind();
			renderer.gl.glColor4d(1.0d, 1.0d, 1.0d, img.alpha);
			TextureCoords textureCoords;
			textureCoords = t.getImageTexCoords();
			textureTop = textureCoords.top();
			textureBottom = textureCoords.bottom();
			textureLeft = textureCoords.left();
			textureRight = textureCoords.right();
			if ( img.angle != 0 ) {
				renderer.gl.glTranslated(img.x + img.width / 2, -(img.y + img.height / 2), 0.0d);
				// FIXME:Check counterwise or not, and do we rotate
				// around the center or around a point.
				renderer.gl.glRotated(-img.angle, 0.0d, 0.0d, 1.0d);
				renderer.gl.glTranslated(-(img.x + img.width / 2), +(img.y + img.height / 2), 0.0d);
			}

			renderer.gl.glBegin(GL_QUADS);
			// bottom-left of the texture and quad
			renderer.gl.glTexCoord2f(textureLeft, textureBottom);
			renderer.gl.glVertex3d(img.x, -(img.y + img.height), img.z);
			// bottom-right of the texture and quad
			renderer.gl.glTexCoord2f(textureRight, textureBottom);
			renderer.gl.glVertex3d(img.x + img.width, -(img.y + img.height), img.z);
			// top-right of the texture and quad
			renderer.gl.glTexCoord2f(textureRight, textureTop);
			renderer.gl.glVertex3d(img.x + img.width, -img.y, img.z);
			// top-left of the texture and quad
			renderer.gl.glTexCoord2f(textureLeft, textureTop);
			renderer.gl.glVertex3d(img.x, -img.y, img.z);
			renderer.gl.glEnd();

			if ( img.angle != 0 ) {
				renderer.gl.glTranslated(img.x + img.width / 2, -(img.y + img.height / 2), 0.0d);
				renderer.gl.glRotated(img.angle, 0.0d, 0.0d, 1.0d);
				renderer.gl.glTranslated(-(img.x + img.width / 2), +(img.y + img.height / 2), 0.0d);
			}

			renderer.gl.glDisable(GL_TEXTURE_2D);
		}
	}

	/**
	 * 
	 * The class DEMDrawer.
	 * 
	 * @author grignard
	 * @since 15 mai 2013
	 * 
	 */
	public static class DEMDrawer extends ObjectDrawer<DEMObject> {

		private boolean initialized;

		public DEMDrawer(final JOGLAWTGLRenderer r) {
			super(r);
		}

		@Override
		protected void _draw(final DEMObject demObj) {

			if ( !demObj.fromImage ) {
				// Get Environment Properties
				double envWidth = demObj.envelope.getWidth() / demObj.cellSize;
				double envHeight = demObj.envelope.getHeight() / demObj.cellSize;
				double envWidthStep = 1 / envWidth;
				double envHeightStep = 1 / envHeight;

				// Get Texture Properties
				double textureWidth = demObj.demTexture.getWidth();
				double textureHeight = demObj.demTexture.getHeight();
				double textureWidthInEnvironment = envWidth / textureWidth;
				double textureHeightInEnvironment = envHeight / textureHeight;

				// FIXME: Need to set it dynamicly
				double altFactor = demObj.z_factor;
				double maxZ = GetMaxValue(demObj.dem);

				double x1, x2, y1, y2;
				Double zValue = 0.0;
				double stepX, stepY;

				MyTexture curTexture = renderer.getScene().getTextures().get(demObj.demTexture);
				if ( curTexture == null ) { return; }
				// Enable the texture
				renderer.gl.glEnable(GL_TEXTURE_2D);
				Texture t = curTexture.texture;
				t.enable();
				t.bind();
				renderer.gl.glColor4d(1.0d, 1.0d, 1.0d, demObj.alpha);
				// Draw Grid with square
				// if texture draw with color coming from the texture and z according to gridvalue
				// else draw the grid with color according the gridValue in gray value
				if ( !isInitialized() && demObj.isTextured ) {
					setInitialized(true);
				}
				if ( !demObj.isTriangulated ) {
					for ( int i = 0; i < envWidth; i++ ) {
						x1 = i / envWidth * envWidth;
						x2 = (i + 1) / envWidth * envWidth;
						for ( int j = 0; j < envHeight; j++ ) {
							y1 = j / envHeight * envHeight;
							y2 = (j + 1) / envHeight * envHeight;
							if ( demObj.dem != null ) {
								zValue = demObj.dem[(int) (j * envWidth + i)];
							}

							if ( demObj.isTextured ) {
								renderer.gl.glBegin(GL_QUADS);
								renderer.gl.glTexCoord2d(envWidthStep * i, envHeightStep * j);
								renderer.gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
								renderer.gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * j);
								renderer.gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
								renderer.gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * (j + 1));
								renderer.gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
								renderer.gl.glTexCoord2d(envWidthStep * i, envHeightStep * (j + 1));
								renderer.gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
								renderer.gl.glEnd();
							} else {
								renderer.gl.glColor3d(zValue / maxZ, zValue / maxZ, zValue / maxZ);
								renderer.gl.glBegin(GL_QUADS);
								renderer.gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
								renderer.gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
								renderer.gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
								renderer.gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
								renderer.gl.glEnd();
							}
						}
					}
				}

				Double z1 = 0.0;
				Double z2 = 0.0;
				Double z3 = 0.0;
				Double z4 = 0.0;

				if ( demObj.isTriangulated ) {
					for ( int i = 0; i < envWidth; i++ ) {
						x1 = i / envWidth * envWidth;
						x2 = (i + 1) / envWidth * envWidth;
						for ( int j = 0; j < envHeight; j++ ) {
							y1 = j / envHeight * envHeight;
							y2 = (j + 1) / envHeight * envHeight;
							if ( demObj.dem != null ) {
								zValue = demObj.dem[(int) (j * envWidth + i)];

								if ( i < envWidth - 1 && j < envHeight - 1 ) {
									z1 = demObj.dem[(int) (j * envWidth + i)];
									z2 = demObj.dem[(int) ((j + 1) * envWidth + i)];
									z3 = demObj.dem[(int) ((j + 1) * envWidth + (i + 1))];
									z4 = demObj.dem[(int) (j * envWidth + (i + 1))];
								}

								// Last rows
								if ( j == envHeight - 1 && i < envWidth - 1 ) {
									z1 = demObj.dem[(int) (j * envWidth + i)];
									z4 = demObj.dem[(int) (j * envWidth + (i + 1))];
									z2 = z1;
									z3 = z4;
								}
								// Last cols
								if ( i == envWidth - 1 && j < envHeight - 1 ) {
									z1 = demObj.dem[(int) (j * envWidth + i)];
									z2 = demObj.dem[(int) ((j + 1) * envWidth + i)];
									z3 = z2;
									z4 = z1;
								}

								// last cell
								if ( i == envWidth - 1 && j == envHeight - 1 ) {
									z1 = demObj.dem[(int) (j * envWidth + i)];
									z2 = z1;
									z3 = z1;
									z4 = z1;
								}

							}

							if ( demObj.isTextured ) {
								renderer.gl.glBegin(GL.GL_TRIANGLE_STRIP);
								renderer.gl.glTexCoord2d(envWidthStep * i, envHeightStep * j);
								renderer.gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, z1 * altFactor);
								renderer.gl.glTexCoord2d(envWidthStep * i, envHeightStep * (j + 1));
								renderer.gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, z2 * altFactor);
								renderer.gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * j);
								renderer.gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, z4 * altFactor);
								renderer.gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * (j + 1));
								renderer.gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, z3 * altFactor);
								renderer.gl.glEnd();
							} else {

								renderer.gl.glColor3d(zValue / maxZ, zValue / maxZ, zValue / maxZ);
								renderer.gl.glBegin(GL.GL_TRIANGLE_STRIP);
								renderer.gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, z1 * altFactor);
								renderer.gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, z2 * altFactor);
								renderer.gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, z4 * altFactor);
								renderer.gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, z3 * altFactor);
								renderer.gl.glEnd();
							}
						}
					}
				}

				if ( demObj.isShowText ) {
					// Draw gridvalue as text inside each cell
					Double gridValue = 0.0;
					renderer.gl.glDisable(GL_BLEND);
					renderer.gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
					for ( int i = 0; i < textureWidth; i++ ) {
						stepX = i / textureWidth * envWidth;
						for ( int j = 0; j < textureHeight; j++ ) {
							stepY = j / textureHeight * envHeight;
							if ( demObj.dem != null ) {
								gridValue = demObj.dem[(int) (j * textureWidth + i)];
							}
							renderer.gl.glRasterPos3d(stepX + textureWidthInEnvironment / 2,
								-(stepY + textureHeightInEnvironment / 2), gridValue * altFactor);
							renderer.gl.glScaled(8.0d, 8.0d, 8.0d);
							glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, gridValue.toString());
							renderer.gl.glScaled(0.125d, 0.125d, 0.125d);
						}
					}
					renderer.gl.glEnable(GL_BLEND);
				}
				renderer.gl.glDisable(GL_TEXTURE_2D);
			} else {
				drawFromPNG(demObj);
			}

		}

		public Texture loadTexture(final String fileName) {
			Texture text = null;
			try {
				if ( renderer.getContext() != null ) {
					renderer.getContext().makeCurrent();
					text = TextureIO.newTexture(new File(fileName), false);
					text.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
					text.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
				} else {
					throw GamaRuntimeException.error("(DEM) JOGLRenderer context is null");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Error loading texture " + fileName);
			}
			return text;
		}

		private BufferedImage FlipUpSideDownImage(BufferedImage img) {
			java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -img.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			img = op.filter(img, null);
			return img;

		}

		private BufferedImage FlipRightSideLeftImage(BufferedImage img) {
			java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-img.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			img = op.filter(img, null);
			return img;

		}

		private double GetMaxValue(final double[] gridValue) {
			double maxValue = 0.0;
			if ( gridValue == null ) { return maxValue; }
			for ( int i = 0; i < gridValue.length; i++ ) {
				if ( gridValue[i] > maxValue ) {
					maxValue = gridValue[i];
				}
			}
			return maxValue;
		}

		protected void drawFromPNG(final DEMObject demObj) {

			if ( !isInitialized() ) {
				renderer.gl.glEnable(GL.GL_TEXTURE_2D);
				loadTexture(demObj.demTexture.toString());
				setInitialized(true);
			}

			int rows, cols;
			int x, y;
			float vx, vy, s, t;
			float ts, tt, tw, th;

			// BufferedImage dem = readPNGImage(demFileName);
			BufferedImage dem = demObj.demImg;
			dem = FlipUpSideDownImage(dem);
			dem = FlipRightSideLeftImage(dem);

			rows = dem.getHeight() - 1;
			cols = dem.getWidth() - 1;
			ts = 1.0f / cols;
			tt = 1.0f / rows;

			// FIXME/ need to set w and h dynamicly
			float w = (float) demObj.envelope.getWidth();
			float h = (float) demObj.envelope.getHeight();

			// float altFactor = (float)demObj.envelope.getWidth()/(10*255);//0.025f;//dem.getWidth();

			float altFactor = demObj.z_factor.floatValue();

			tw = w / cols;
			th = h / rows;

			renderer.gl.glTranslated(w / 2, -h / 2, 0);

			renderer.gl.glNormal3f(0.0f, 1.0f, 0.0f);

			for ( y = 0; y < rows; y++ ) {
				renderer.gl.glBegin(GL.GL_QUAD_STRIP);
				for ( x = 0; x <= cols; x++ ) {
					vx = tw * x - w / 2.0f;
					vy = th * y - h / 2.0f;
					s = 1.0f - ts * x;
					t = 1.0f - tt * y;

					float alt1 = (dem.getRGB(cols - x, y) & 255) * altFactor;
					float alt2 = (dem.getRGB(cols - x, y + 1) & 255) * altFactor;

					boolean isTextured = true;
					if ( isTextured ) {
						renderer.gl.glTexCoord2f(s, t);
						renderer.gl.glVertex3f(vx, vy, alt1);
						renderer.gl.glTexCoord2f(s, t - tt);
						renderer.gl.glVertex3f(vx, vy + th, alt2);
					} else {
						float color = dem.getRGB(cols - x, y) & 255;
						color = color / 255.0f;

						renderer.gl.glColor3f(color, color, color);
						renderer.gl.glVertex3f(vx, vy, alt1);
						renderer.gl.glVertex3f(vx, vy + th, alt2);
					}
				}
				renderer.gl.glEnd();
			}
			renderer.gl.glTranslated(-w / 2, h / 2, 0);

			// FIXME: Add disable texture?
		}

		public boolean isInitialized() {

			return initialized;
		}

		public void setInitialized(final boolean initialized) {
			this.initialized = initialized;
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

		public CollectionDrawer(final JOGLAWTGLRenderer r) {
			super(r);
			jtsDrawer = new JTSDrawer(r);
		}

		@Override
		public void _draw(final CollectionObject collection) {
			// Draw Shape file so need to inverse the y composante.
			jtsDrawer.yFlag = 1;
			renderer.gl.glPushMatrix();
			renderer.gl.glTranslated(-collection.collection.getBounds().centre().x, -collection.collection.getBounds()
				.centre().y, 0.0d);
			SimpleFeatureIterator iterator = collection.collection.features();
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry sourceGeometry = (Geometry) feature.getDefaultGeometry();

				if ( sourceGeometry.getGeometryType() == "MultiPolygon" ) {
					jtsDrawer.DrawMultiPolygon((MultiPolygon) sourceGeometry, collection.getColor(), 1.0d, true, null,
						0, 0.0d, false, 0.0);
				} else if ( sourceGeometry.getGeometryType() == "Polygon" ) {
					jtsDrawer.DrawPolygon((Polygon) sourceGeometry, collection.getColor(), 1.0d, true, null, false, 0,
						true, false, 0.0);
				} else if ( sourceGeometry.getGeometryType() == "MultiLineString" ) {
					jtsDrawer.DrawMultiLineString((MultiLineString) sourceGeometry, 0.0d, collection.getColor(), 1.0d,
						0.0d);
				} else if ( sourceGeometry.getGeometryType() == "LineString" ) {
					jtsDrawer.DrawLineString((LineString) sourceGeometry, 0.0d, 1.0d, collection.getColor(), 1.0d);
				} else if ( sourceGeometry.getGeometryType() == "Point" ) {
					jtsDrawer.DrawPoint((Point) sourceGeometry, 0.0d, 10, 10, collection.getColor(), 1.0d);
				}
			}
			renderer.gl.glPopMatrix();
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

		TextRenderer get(final String font, final int size, final int style) {
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

		public StringDrawer(final JOGLAWTGLRenderer r) {
			super(r);
		}

		@Override
		public void draw(final StringObject object) {
			_draw(object);
		}

		@Override
		protected void _draw(final StringObject s) {
			if ( s.bitmap == true ) {
				TextRenderer r = get(s.font, s.size, s.style);
				r.setColor(s.getColor());
				r.begin3DRendering();
				float x = (float) ((float) s.x * s.scale.x + s.offset.x);
				float y = (float) ((float) s.y * s.scale.y - s.offset.y);
				renderer.gl.glPushMatrix();
				// renderer.gl.glScaled(s.scale.x, s.scale.y, 1);
				r.draw3D(s.string, x, y, (float) (s.z + s.z_layer),
					(float) (/* s.scale.y * */renderer.displaySurface.getEnvHeight() / renderer.getHeight()));
				renderer.gl.glPopMatrix();
				r.end3DRendering();
			} else {
				renderer.gl.glDisable(GL_BLEND);
				renderer.gl.glDisable(GL_LIGHTING);
				renderer.gl.glColor3d(s.getColor().getRed() / 255, s.getColor().getGreen() / 255, s.getColor()
					.getBlue() / 255);
				renderer.gl.glRasterPos3d(s.x, s.y, s.z + s.z_layer);
				glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, s.string);
				renderer.gl.glEnable(GL_BLEND);
				renderer.gl.glEnable(GL_LIGHTING);
			}

		}

		@Override
		public void dispose() {
			cache.clear();
		}
	}

	public void dispose() {}

	public GL getGL() {
		return renderer.gl;
	}

}
