package msi.gama.jogl.scene;

import static javax.media.opengl.GL.*;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.media.opengl.GL;
import msi.gama.jogl.utils.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.TextureCoords;
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
			switch (geometry.type) {
				case MULTIPOLYGON:
					jtsDrawer.drawMultiPolygon((MultiPolygon) geometry.geometry, geometry.getColor(),
						geometry.getAlpha(), geometry.fill, geometry.border, geometry.isTextured, geometry,
						geometry.height, geometry.rounded, geometry.getZ_fighting_id());
					break;
				case SPHERE:
					jtsDrawer.drawSphere(geometry);
					break;
				case HEMISPHERE:
					jtsDrawer.drawHemiSphere(geometry);
					break;
				case CONE:
					jtsDrawer.drawCone3D(geometry);
					break;
				case TEAPOT:
					jtsDrawer.drawTeapot(geometry);
					break;
				case PYRAMID:
					jtsDrawer.drawPyramid(geometry);
					break;
				case RGBCUBE:
					jtsDrawer.drawRGBCube(geometry);
					break;
				case RGBTRIANGLE:
					jtsDrawer.drawRGBTriangle(geometry);
					break;
				case POLYLINECYLINDER:
					jtsDrawer.DrawMultiLineCylinder(geometry.geometry, geometry.getColor(), geometry.getAlpha(),
						geometry.height);
					break;
				case LINECYLINDER:
					jtsDrawer.drawLineCylinder(geometry.geometry, geometry.getColor(), geometry.getAlpha(),
						geometry.height);
					break;
				case POLYGON:
				case ENVIRONMENT:
				case POLYHEDRON:
				case CUBE:
				case BOX:
				case CIRCLE:
				case CYLINDER:
				case GRIDLINE:
					if ( geometry.height > 0 ) {
						jtsDrawer.DrawPolyhedre((Polygon) geometry.geometry, geometry.getColor(), geometry.getAlpha(),
							geometry.fill, geometry.height, true, geometry.border, geometry.isTextured, geometry,
							geometry.rounded, geometry.getZ_fighting_id());
					} else {
						if ( jtsDrawer.renderer.computeNormal ) {
							int norm_dir = 1;
							Vertex[] vertices = jtsDrawer.getExteriorRingVertices((Polygon) geometry.geometry);
							if ( !jtsDrawer.IsClockwise(vertices) ) {
								norm_dir = -1;
							}
							jtsDrawer.DrawPolygon((Polygon) geometry.geometry, geometry.getColor(),
								geometry.getAlpha(), geometry.fill, geometry.border, geometry.isTextured, geometry,
								true, geometry.rounded, geometry.getZ_fighting_id(), norm_dir);
						} else {
							jtsDrawer.DrawPolygon((Polygon) geometry.geometry, geometry.getColor(),
								geometry.getAlpha(), geometry.fill, geometry.border, geometry.isTextured, geometry,
								true, geometry.rounded, geometry.getZ_fighting_id(), -1);
						}

					}
					break;
				case MULTILINESTRING:
					jtsDrawer.DrawMultiLineString((MultiLineString) geometry.geometry, 0, geometry.getColor(),
						geometry.getAlpha(), geometry.height);
					break;
				case LINESTRING:
				case LINEARRING:
				case PLAN:
				case POLYPLAN:
					if ( geometry.height > 0 ) {
						jtsDrawer.drawPlan((LineString) geometry.geometry, 0, geometry.getColor(), geometry.getAlpha(),
							geometry.height, 0, true);
					} else {
						jtsDrawer.drawLineString((LineString) geometry.geometry, 0, 1.2f, geometry.getColor(),
							geometry.getAlpha());
					}
					break;
				case POINT:
					jtsDrawer.DrawPoint((Point) geometry.geometry, 0, 10, renderer.getMaxEnvDim() / 1000,
						geometry.getColor(), geometry.getAlpha());
					break;
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

			MyTexture curTexture = img.getTexture(renderer);
			if ( curTexture == null ) { return; }
			double width = img.dimensions.x;
			double height = img.dimensions.y;
			double x = img.location.x;
			double y = img.location.y;
			double z = img.location.z;
			// Enable the texture
			curTexture.bindTo(renderer);
			renderer.gl.glColor4d(1.0d, 1.0d, 1.0d, img.getAlpha());
			TextureCoords textureCoords;
			textureCoords = curTexture.getTexture().getImageTexCoords();
			textureTop = textureCoords.top();
			textureBottom = textureCoords.bottom();
			textureLeft = textureCoords.left();
			textureRight = textureCoords.right();
			if ( img.angle != 0 ) {
				renderer.gl.glTranslated(x + width / 2, -(y + height / 2), 0.0d);
				// FIXME:Check counterwise or not, and do we rotate
				// around the center or around a point.
				renderer.gl.glRotated(-img.angle, 0.0d, 0.0d, 1.0d);
				renderer.gl.glTranslated(-(x + width / 2), +(y + height / 2), 0.0d);
			}

			if ( renderer.computeNormal ) {
				Vertex[] vertices = new Vertex[4];
				for ( int i = 0; i < 4; i++ ) {
					vertices[i] = new Vertex();
				}
				vertices[0].x = x;
				vertices[0].y = -(y + height);
				vertices[0].z = z;

				vertices[1].x = x + width;
				vertices[1].y = -(y + height);
				vertices[1].z = z;

				vertices[2].x = x + width;
				vertices[2].y = -y;
				vertices[2].z = z;

				vertices[3].x = x;
				vertices[3].y = -y;
				vertices[3].z = z;
				GLUtilNormal.HandleNormal(vertices, null, img.getAlpha(), -1, renderer);
			}

			renderer.gl.glColor4d(1.0d, 1.0d, 1.0d, img.getAlpha());
			renderer.gl.glBegin(GL_QUADS);
			// bottom-left of the texture and quad
			renderer.gl.glTexCoord2f(textureLeft, textureBottom);
			renderer.gl.glVertex3d(x, -(y + height), z);
			// bottom-right of the texture and quad
			renderer.gl.glTexCoord2f(textureRight, textureBottom);
			renderer.gl.glVertex3d(x + width, -(y + height), z);
			// top-right of the texture and quad
			renderer.gl.glTexCoord2f(textureRight, textureTop);
			renderer.gl.glVertex3d(x + width, -y, z);
			// top-left of the texture and quad
			renderer.gl.glTexCoord2f(textureLeft, textureTop);
			renderer.gl.glVertex3d(x, -y, z);
			renderer.gl.glEnd();

			if ( img.angle != 0 ) {
				renderer.gl.glTranslated(x + width / 2, -(y + height / 2), 0.0d);
				renderer.gl.glRotated(img.angle, 0.0d, 0.0d, 1.0d);
				renderer.gl.glTranslated(-(x + width / 2), +(y + height / 2), 0.0d);
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

		// private boolean initialized;

		public DEMDrawer(final JOGLAWTGLRenderer r) {
			super(r);
		}

		@Override
		protected void _draw(final DEMObject demObj) {

			if ( demObj.fromImage ) {
				drawFromImage(demObj);
				return;
			}

			// Get Environment Properties
			double envWidth = demObj.envelope.getWidth() / demObj.cellSize;
			double envHeight = demObj.envelope.getHeight() / demObj.cellSize;
			double envWidthStep = 1 / envWidth;
			double envHeightStep = 1 / envHeight;

			// Get Texture Properties
			double textureWidth = demObj.textureImage.getWidth();
			double textureHeight = demObj.textureImage.getHeight();
			double textureWidthInEnvironment = envWidth / textureWidth;
			double textureHeightInEnvironment = envHeight / textureHeight;

			// FIXME: Need to set it dynamicly
			double altFactor = demObj.envelope.getDepth();
			double maxZ = GetMaxValue(demObj.dem);

			double x1, x2, y1, y2;
			Double zValue = 0.0;
			double stepX, stepY;

			MyTexture curTexture = demObj.getTexture(renderer);
			if ( curTexture == null ) { return; }
			// Enable the texture
			// renderer.gl.glPushMatrix();
			// renderer.gl.glEnable(GL_TEXTURE_2D);
			renderer.gl.glColor4d(1.0d, 1.0d, 1.0d, demObj.getAlpha());
			curTexture.bindTo(renderer);

			// Draw Grid with square
			// if texture draw with color coming from the texture and z according to gridvalue
			// else draw the grid with color according the gridValue in gray value
			// if ( !isInitialized() && demObj.isTextured ) {
			// setInitialized(true);
			// }
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

						// Compute normal
						if ( renderer.computeNormal ) {
							Vertex[] vertices = new Vertex[4];
							for ( int i1 = 0; i1 < 4; i1++ ) {
								vertices[i1] = new Vertex();
							}
							vertices[0].x = x1 * demObj.cellSize;
							vertices[0].y = -y1 * demObj.cellSize;
							vertices[0].z = z1 * altFactor;

							vertices[1].x = x1 * demObj.cellSize;
							vertices[1].y = -y2 * demObj.cellSize;
							vertices[1].z = z1 * altFactor;

							vertices[2].x = x2 * demObj.cellSize;
							vertices[2].y = -y1 * demObj.cellSize;
							vertices[2].z = z4 * altFactor;

							vertices[3].x = x2 * demObj.cellSize;
							vertices[3].y = -y2 * demObj.cellSize;
							vertices[3].z = z3 * altFactor;
							double[] normal = GLUtilNormal.CalculateNormal(vertices[2], vertices[1], vertices[0]);
							renderer.gl.glNormal3dv(normal, 0);
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
			curTexture.unbindFrom(renderer);
			// renderer.gl.glDisable(GL_TEXTURE_2D);
			// renderer.gl.glPopMatrix();
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

		protected void drawFromImage(final DEMObject demObj) {

			int rows, cols;
			int x, y;
			float vx, vy, s, t;
			float ts, tt, tw, th;

			BufferedImage dem = demObj.demImg;
			MyTexture curTexture = demObj.getTexture(renderer);
			if ( curTexture == null ) { return; }
			// Enable the texture

			curTexture.bindTo(renderer);
			rows = dem.getHeight() - 1;
			cols = dem.getWidth() - 1;
			ts = 1.0f / cols;
			tt = 1.0f / rows;

			// FIXME/ need to set w and h dynamicly
			float w = (float) demObj.envelope.getWidth();
			float h = (float) demObj.envelope.getHeight();
			float altFactor = (float) demObj.envelope.getDepth();

			tw = w / cols;
			th = h / rows;
			renderer.gl.glPushMatrix();
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
			renderer.gl.glPopMatrix();
			// renderer.gl.glTranslated(-w / 2, h / 2, 0);

			// FIXME: Add disable texture?
			curTexture.unbindFrom(renderer);

		}

		// public boolean isInitialized() {
		// return initialized;
		// }
		//
		// public void setInitialized(final boolean initialized) {
		// this.initialized = initialized;
		// }

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
			float x = (float) ((float) s.location.x * s.getScale().x + s.getOffset().x);
			float y = (float) ((float) s.location.y * s.getScale().y - s.getOffset().y);
			float z = (float) ((float) s.location.z * s.getScale().z + s.getOffset().z);

			if ( s.bitmap == true ) {
				renderer.gl.glPushMatrix();
				TextRenderer r = get(s.font, s.size, s.style);
				r.setColor(s.getColor());
				r.begin3DRendering();
				r.draw3D(s.string, x, y, z, (float) (renderer.displaySurface.getEnvHeight() / renderer.getHeight()));
				r.end3DRendering();
				renderer.gl.glPopMatrix();
			} else {
				renderer.gl.glPushMatrix();
				renderer.gl.glDisable(GL_LIGHTING);
				renderer.gl.glDisable(GL_BLEND);

				renderer.gl.glColor4d(s.getColor().getRed() / 255, s.getColor().getGreen() / 255, s.getColor()
					.getBlue() / 255, s.getColor().getAlpha() / 255 * s.getAlpha());
				renderer.gl.glRasterPos3d(x, y, z);
				glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, s.string);
				// FIXME We go back to the white ??
				renderer.gl.glColor4d(1, 1, 1, 1);
				//
				renderer.gl.glEnable(GL_BLEND);
				renderer.gl.glEnable(GL_LIGHTING);
				renderer.gl.glPopMatrix();
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

	public JOGLAWTGLRenderer getRenderer() {
		return renderer;
	}

}
