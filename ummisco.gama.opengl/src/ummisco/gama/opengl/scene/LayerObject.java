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
import com.jogamp.opengl.*;
import com.vividsolutions.jts.geom.Geometry;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.files.GLModel;

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
	final ILayer layer;
	volatile boolean isInvalid;

	protected final ISceneObjects<GeometryObject> geometries;
	protected final ISceneObjects<ImageObject> images;
	protected final ISceneObjects<DEMObject> dems;
	protected final ISceneObjects<StringObject> strings;

	public LayerObject(final JOGLRenderer renderer, final ILayer layer) {
		this.layer = layer;
		geometries = buildSceneObjects(new GeometryDrawer(renderer), true, false);
		strings = buildSceneObjects(new StringDrawer(renderer), !StringDrawer.USE_VERTEX_ARRAYS, false);
		images = buildSceneObjects(new ImageDrawer(renderer), true, false);
		dems = buildSceneObjects(new DEMDrawer(renderer), true, false);
	}

	protected ISceneObjects buildSceneObjects(final ObjectDrawer drawer, final boolean asList, final boolean asVBO) {
		return new SceneObjects(drawer, asList, asVBO);
	}

	private boolean isPickable() {
		return layer == null ? false : layer.isSelectable();
	}

	public void draw(final GL2 gl, final JOGLRenderer renderer, final boolean picking) {
		if ( isInvalid() ) { return; }
		// GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glPushMatrix();
		//
		gl.glTranslated(offset.x, -offset.y, offset.z);
		gl.glScaled(scale.x, scale.y, scale.z);
		//NOTE: In the same layer if geometries and image are drawn images are drawn first and then the geometries.
		// To be sure that the line of a grid is well displayed we decide to draw first the image that corresponds to the grid and then the line as geometries. (otherwise the lines are invisible)
		gl.glEnable(GL.GL_TEXTURE_2D);
		images.draw(gl, picking && isPickable());
		gl.glDisable(GL.GL_TEXTURE_2D);
		geometries.draw(gl, picking && isPickable());
		//
		gl.glPopMatrix();
		// Strings are treated apart for the moment, since they have a very special draw that already applies the scale
		// and offset...
		// FIXME this needs to be changed
		gl.glPushMatrix();
		dems.draw(gl, picking && isPickable());
		strings.draw(gl, picking && isPickable());
		gl.glPopMatrix();
		// gl.glFlush();
	}

	public boolean isStatic() {
		if ( layer == null ) { return true; }
		Boolean isDynamic = layer.isDynamic();
		return isDynamic == null ? false : !isDynamic;
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
		strings.add(new StringObject(string, font, style, offset, scale, color, angle, location, sizeInModelUnits, size,
			alpha, bitmap));
	}

	public void addImage(final BufferedImage img, final IAgent agent, final GamaPoint location,
		final GamaPoint dimensions, final Double angle, final boolean isDynamic, final String name) {
		images.add(new ImageObject(img, agent, getOrder(), location, alpha, dimensions, angle == null ? 0d : angle,
			isDynamic, name));

	}

	public void addDEM(final double[] dem, final BufferedImage demTexture, final BufferedImage demImg,
		final IAgent agent, final boolean isTextured, final boolean isTriangulated, final boolean isGrayScaled,
		final boolean isShowText, final boolean isFromImage, final boolean isDynamic, final Envelope3D env,
		final double cellSize, final String name, final Color lineColor) {
		dems.add(new DEMObject(dem, demTexture, demImg, agent, env, isTextured, isTriangulated, isGrayScaled,
			isShowText, isFromImage, isDynamic, null, alpha, cellSize, name, lineColor));
	}

	public void addGeometry(final Geometry geometry, final IAgent agent, final Color color, final boolean fill,
		final Color border, final boolean isTextured, final List<BufferedImage> textures, final GLModel asset3Dmodel, final Integer angle,
		final double height, final boolean roundCorner, final IShape.Type type, final List<Double> ratio,
		final List<GamaColor> colors, final GamaPair<Double, GamaPoint> rotate3D) {
		final GeometryObject curJTSGeometry;
		if ( type == IShape.Type.PIESPHERE || type == IShape.Type.PIESPHEREWITHDYNAMICALCOLOR ||
			type == IShape.Type.PACMAN || type == IShape.Type.ANTISLICE || type == IShape.Type.SLICE ) {
			curJTSGeometry = new Pie3DObject(geometry, agent, offset.z, getOrder(), color, alpha, fill, border,
				isTextured, textures, angle == null ? 0 : angle, height, roundCorner, type, ratio, colors);
		} else {
			curJTSGeometry = new GeometryObject(geometry, agent, offset.z, getOrder(), color, alpha, fill, border,
				isTextured, textures, asset3Dmodel, angle == null ? 0 : angle, height, roundCorner, type, rotate3D);
		}
		geometries.add(curJTSGeometry);
	}

	private int getOrder() {
		return layer == null ? 0 : layer.getOrder();
	}

	private int getTrace() {
		if ( layer == null ) { return 0; }
		Integer trace = layer.getTrace();
		return trace == null ? 0 : trace;
	}

	private boolean getFading() {
		if ( layer == null ) { return false; }
		Boolean fading = layer.getFading();
		return fading == null ? false : fading;
	}

	public void clear(final GL gl) {
		int trace = getTrace();
		// int traceSize = trace == 0 ? requestedDisplayTraceSize : trace;
		boolean fading = getFading();
		geometries.clear(gl, trace, fading);
		images.clear(gl, trace, fading);
		dems.clear(gl, trace, fading);
		strings.clear(gl, trace, fading);
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
	 * @return
	 */
	public boolean isInvalid() {
		return isInvalid;
	}

	public void invalidate() {
		isInvalid = true;
	}

	/**
	 * @return
	 */
	public boolean hasTrace() {
		return getTrace() > 0;
	}

}
