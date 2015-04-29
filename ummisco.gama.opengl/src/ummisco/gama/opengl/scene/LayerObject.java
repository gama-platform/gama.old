/*********************************************************************************************
 * 
 * 
 * 'LayerObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
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
import ummisco.gama.opengl.JOGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.GamaColor;
import com.jogamp.opengl.*;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Class LayerObject.
 * 
 * @author drogoul
 * @since 3 mars 2014
 * 
 */
public class LayerObject implements Iterable<GeometryObject> {

	GamaPoint offset = new GamaPoint();
	GamaPoint scale = new GamaPoint(1, 1, 1);
	Double alpha = 1d;
	final Integer id;
	int trace = 0;
	boolean fading = false;

	protected final SceneObjects<GeometryObject> geometries;
	protected final SceneObjects<ImageObject> images;
	protected final SceneObjects<DEMObject> dems;
	protected final SceneObjects<StringObject> strings;

	public LayerObject(final JOGLRenderer renderer, final Integer i) {
		geometries = buildSceneObjects(new GeometryDrawer(renderer), true, false);
		strings = buildSceneObjects(new StringDrawer(renderer), !StringDrawer.USE_VERTEX_ARRAYS, false);
		images = buildSceneObjects(new ImageDrawer(renderer), true, false);
		dems = buildSceneObjects(new DEMDrawer(renderer), true, false);
		id = i;
	}

	protected SceneObjects buildSceneObjects(final ObjectDrawer drawer, final boolean asList, final boolean asVBO) {
		return new SceneObjects(drawer, asList, asVBO);
	}

	public void draw(final GL2 gl, final JOGLRenderer renderer, final boolean picking) {
		// GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glPushMatrix();
		//
		gl.glTranslated(offset.x, -offset.y, offset.z);
		gl.glScaled(scale.x, scale.y, scale.z);
		//
		geometries.draw(gl, picking);
		gl.glEnable(GL.GL_TEXTURE_2D);
		images.draw(gl, picking);
		gl.glDisable(GL.GL_TEXTURE_2D);

		//
		gl.glPopMatrix();
		// Strings are treated apart for the moment, since they have a very special draw that already applies the scale
		// and offset...
		// FIXME this needs to be changed
		gl.glPushMatrix();
		dems.draw(gl, picking);
		strings.draw(gl, picking);
		gl.glPopMatrix();
		// gl.glFlush();
	}

	public boolean isStatic() {
		return false;
	}

	public void setAlpha(final Double a) {
		alpha = a;
	}

	public GamaPoint getOffset() {
		return offset;
	}

	public void setOffset(final GamaPoint offset) {
		this.offset = offset;
	}

	public GamaPoint getScale() {
		return scale;
	}

	public void setScale(final GamaPoint scale) {
		this.scale = scale;
	}

	public void addString(final String string, final GamaPoint location, final Integer size,
		final Double sizeInModelUnits, final Color color, final String font, final Integer style, final Double angle,
		final Boolean bitmap) {
		strings.add(new StringObject(string, font, style, offset, scale, color, angle, location, sizeInModelUnits,
			size, alpha, bitmap));
	}

	public void addImage(final BufferedImage img, final IAgent agent, final GamaPoint location,
		final GamaPoint dimensions, final Double angle, final boolean isDynamic, final String name) {
		images.add(new ImageObject(img, agent, id, location, alpha, dimensions, angle == null ? 0d : angle, isDynamic,
			name));

	}

	public void addDEM(final double[] dem, final BufferedImage demTexture, final BufferedImage demImg,
		final IAgent agent, final boolean isTextured, final boolean isTriangulated, final boolean isGrayScaled,
		final boolean isShowText, final boolean isFromImage, final boolean isDynamic, final Envelope3D env,
		final double cellSize, final String name) {
		dems.add(new DEMObject(dem, demTexture, demImg, agent, env, isTextured, isTriangulated, isGrayScaled,
			isShowText, isFromImage, isDynamic, null, alpha, cellSize, name/* , id */));
	}

	public void addGeometry(final Geometry geometry, final IAgent agent, final Color color, final boolean fill,
		final Color border, final boolean isTextured, final List<BufferedImage> textures, final Integer angle,
		final double height, final boolean roundCorner, final IShape.Type type, final List<Double> ratio,
		final List<GamaColor> colors) {
		final GeometryObject curJTSGeometry;
		if ( type == IShape.Type.PIESPHERE || type == IShape.Type.PIESPHEREWITHDYNAMICALCOLOR ||
			type == IShape.Type.PACMAN || type == IShape.Type.ANTISLICE || type == IShape.Type.SLICE ) {
			curJTSGeometry =
				new Pie3DObject(geometry, agent, offset.z, id, color, alpha, fill, border, isTextured, textures,
					angle == null ? 0 : angle, height, roundCorner, type, ratio, colors);
		} else {
			curJTSGeometry =
				new GeometryObject(geometry, agent, offset.z, id, color, alpha, fill, border, isTextured, textures,
					angle == null ? 0 : angle, height, roundCorner, type);
		}
		geometries.add(curJTSGeometry);
	}

	public void clear(final GL gl, final int requestedDisplayTraceSize) {
		int traceSize = trace == 0 ? requestedDisplayTraceSize : trace;
		geometries.clear(gl, traceSize, fading);
		images.clear(gl, traceSize, fading);
		dems.clear(gl, traceSize, fading);
		strings.clear(gl, traceSize, fading);
	}

	public void dispose() {
		geometries.dispose();
		strings.dispose();
		images.dispose();
		dems.dispose();
	}

	/**
	 * Method iterator()
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<GeometryObject> iterator() {
		return geometries.getObjects().iterator();
	}

	/**
	 * @param trace
	 */
	public void setTrace(final Integer trace) {
		this.trace = trace;
	}

	/**
	 * @param fading
	 */
	public void setFading(final Boolean fading) {
		this.fading = fading;
	}

}
