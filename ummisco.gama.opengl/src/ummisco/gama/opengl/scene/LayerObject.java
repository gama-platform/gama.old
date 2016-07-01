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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.ModernRenderer;
import ummisco.gama.webgl.SimpleGeometryObject;
import ummisco.gama.webgl.SimpleLayer;

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
	volatile boolean overlay;
	volatile boolean locked;
	final Abstract3DRenderer renderer;
	final LinkedList<List<AbstractObject>> objects = new LinkedList();
	List<AbstractObject> currentList;
	Integer openGLListIndex;
	boolean isFading;
	
	boolean isInit = false;
	

	public LayerObject(final Abstract3DRenderer renderer, final ILayer layer) {
		this.renderer = renderer;
		this.layer = layer;
		currentList = newCurrentList();
		objects.add(currentList);
	}

	private List newCurrentList() {
		return new CopyOnWriteArrayList();
	}

	private boolean isPickable() {
		return layer == null ? false : layer.isSelectable();
	}

	public void draw(final GL2 gl, final Abstract3DRenderer renderer) {
		if (isInvalid()) {
			return;
		}
		
		if (this.renderer.useShader()) {
			drawWithShader(gl, (ModernRenderer)renderer);
		}
		else {
			drawWithoutShader(gl, (JOGLRenderer)renderer);
		}
	}
	
	public void drawWithShader(final GL2 gl, final ModernRenderer renderer) {
		renderer.getDrawer().clearEntityList();
		for (final List<AbstractObject> list : objects) {
			for (final AbstractObject object : list) {
				if (object instanceof GeometryObject) {
					renderer.getDrawer().addDrawingEntities(renderer.getVAOGenerator().GenerateVAO(object));
				}
			}
		}	
		renderer.getDrawer().draw();
		
	}
	
	public void drawWithoutShader(final GL2 gl, final JOGLRenderer renderer) {
		
		if (overlay) {
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, renderer.data.getEnvWidth(), renderer.data.getEnvHeight(), 0.0, -1.0,
					renderer.getMaxEnvDim());
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			gl.glLoadIdentity();
		}
		try {
			gl.glPushMatrix();
			gl.glTranslated(offset.x, -offset.y, offset.z);
			gl.glScaled(scale.x, scale.y, scale.z);
			final boolean picking = renderer.getPickingState().isPicking() && isPickable();
			if (objects.size() == 0) {
				return;
			}
			renderer.setCurrentColor(gl, Color.white);
			if (picking) {
				drawPicking(gl);
				return;
			}
			Integer index = openGLListIndex;
			if (index == null) {
				index = gl.glGenLists(1);
				gl.glNewList(index, GL2.GL_COMPILE);
				double alpha = 0d;
				final int size = objects.size();
				final double delta = size == 0 ? 0 : 1d / size;
				for (final List<AbstractObject> list : objects) {
					alpha = alpha + delta;
					for (final AbstractObject object : list) {
						final ObjectDrawer drawer = renderer.getDrawerFor(object.getClass());
						if (isFading) {
							final double originalAlpha = object.getAlpha();
							object.setAlpha(originalAlpha * alpha);
							object.draw(gl, drawer, picking);
							object.setAlpha(originalAlpha);
						} else {
							object.draw(gl, drawer, picking);
						}
					}
				}
				gl.glEndList();
			}
			gl.glCallList(index);
			openGLListIndex = index;
		} finally {
			gl.glPopMatrix();
		}

		if (overlay) {
			// Making sure we can render 3d again
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		}
	}

	private void drawPicking(final GL2 gl) {
		gl.glPushMatrix();
		gl.glInitNames();
		gl.glPushName(0);
		double alpha = 0d;
		final int size = objects.size();
		final double delta = size == 0 ? 0 : 1d / size;
		for (final List<AbstractObject> list : objects) {
			alpha = alpha + delta;
			for (final AbstractObject object : list) {
				final ObjectDrawer drawer = ((JOGLRenderer)renderer).getDrawerFor(object.getClass());
				if (isFading) {
					final double originalAlpha = object.getAlpha();
					object.setAlpha(originalAlpha * alpha);
					object.draw(gl, drawer, true);
					object.setAlpha(originalAlpha);
				} else {
					object.draw(gl, drawer, true);
				}
			}
		}

		gl.glPopName();
		gl.glPopMatrix();

	}

	public boolean isStatic() {
		if (layer == null) {
			return true;
		}
		final Boolean isDynamic = layer.isDynamic();
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

	public void addString(final String string, final DrawingAttributes attributes) {
		currentList.add(new StringObject(string, attributes, this));
	}

	public void addFile(final GamaGeometryFile file, final DrawingAttributes attributes) {
		currentList.add(new ResourceObject(file, attributes, this));
	}

	public void addImage(final GamaImageFile img, final DrawingAttributes attributes) {
		currentList.add(new ImageObject(img, attributes, this));
	}

	public void addImage(final BufferedImage img, final DrawingAttributes attributes) {
		currentList.add(new ImageObject(img, attributes, this));
	}

	public void addField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		currentList.add(new FieldObject(fieldValues, attributes, this));
	}

	public void addGeometry(final Geometry geometry, final DrawingAttributes attributes) {
		currentList.add(new GeometryObject(geometry, attributes, this));
	}

	public int getOrder() {
		return layer == null ? 0 : layer.getOrder();
	}

	private int getTrace() {
		if (layer == null) {
			return 0;
		}
		final Integer trace = layer.getTrace();
		return trace == null ? 0 : trace;
	}

	private boolean getFading() {
		if (layer == null) {
			return false;
		}
		final Boolean fading = layer.getFading();
		return fading == null ? false : fading;
	}

	public void clear(final GL gl) {
		final int sizeLimit = getTrace();
		final boolean fading = getFading();

		isFading = fading;

		final int size = objects.size();
		for (int i = 0, n = size - sizeLimit; i < n; i++) {
			final List<AbstractObject> list = objects.poll();
			for (final AbstractObject t : list) {
				t.dispose(gl);
			}
		}

		currentList = newCurrentList();
		objects.offer(currentList);
		final Integer index = openGLListIndex;
		if (index != null) {
			gl.getGL2().glDeleteLists(index, 1);
			openGLListIndex = null;
		}

	}

	@Override
	public Iterator<GeometryObject> iterator() {
		return Iterators.filter(currentList.iterator(), GeometryObject.class);
	}

	public boolean isInvalid() {
		return isInvalid;
	}

	public void invalidate() {
		isInvalid = true;
	}

	public boolean hasTrace() {
		return getTrace() > 0;
	}

	public void preload(final GL2 gl) {
		if (objects.size() == 0) {
			return;
		}
		for (final AbstractObject object : currentList) {
			object.preload(gl, renderer);
		}
	}

	public void setOverlay(final boolean b) {
		overlay = b;
	}

	public boolean isLocked() {
		return locked;
	}

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

	public SimpleLayer toSimpleLayer() {
		final List<SimpleGeometryObject> geom = new ArrayList<SimpleGeometryObject>();
		for (final GeometryObject object : Iterables.filter(currentList, GeometryObject.class)) {
			geom.add(object.toSimpleGeometryObject());
		}
		return new SimpleLayer(offset, scale, alpha, geom);
	}

}
