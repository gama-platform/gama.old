/*******************************************************************************************************
 *
 * LayerObject.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import com.google.common.collect.ImmutableList;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.matrix.IField;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.units.PixelUnitExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.MeshDrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.geometry.GeometryObject;
import ummisco.gama.opengl.scene.mesh.MeshObject;
import ummisco.gama.opengl.scene.resources.ResourceObject;
import ummisco.gama.opengl.scene.text.StringObject;

/**
 * Class LayerObject.
 *
 * @author drogoul
 * @since 3 mars 2014
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class LayerObject {

	/** The Constant NULL_OFFSET. */
	final static GamaPoint NULL_OFFSET = new GamaPoint();

	/** The Constant NULL_SCALE. */
	final static GamaPoint NULL_SCALE = new GamaPoint(1, 1, 1);

	/** The offset. */
	GamaPoint offset = new GamaPoint(NULL_OFFSET);

	/** The scale. */
	GamaPoint scale = new GamaPoint(NULL_SCALE);

	/** The alpha. */
	protected Double alpha = 1d;

	/** The layer. */
	public final ILayer layer;

	/** The is invalid. */
	volatile boolean isInvalid;

	/** The locked. */
	volatile boolean locked;

	/** The is animated. */
	boolean isAnimated;

	/** The renderer. */
	protected final IOpenGLRenderer renderer;

	/** The traces. */
	protected final LinkedList<List<AbstractObject<?, ?>>> traces;

	/** The current list. */
	protected List<AbstractObject<?, ?>> currentList;

	/** The open GL list index. */
	protected Integer openGLListIndex;

	/** The is fading. */
	protected boolean isFading;

	/**
	 * Instantiates a new layer object.
	 *
	 * @param renderer2
	 *            the renderer 2
	 * @param layer
	 *            the layer
	 */
	public LayerObject(final IOpenGLRenderer renderer2, final ILayer layer) {
		this.renderer = renderer2;
		this.layer = layer;
		computeOffset();
		computeScale();
		currentList = newCurrentList();
		if (layer != null && layer.getData().getTrace() != null || renderer.useShader()) {
			traces = new LinkedList();
			traces.add(currentList);
		} else {
			traces = null;
		}
	}

	/**
	 * Compute scale.
	 */
	public void computeScale() {
		double zScale = layer.getData().getSize().getZ();
		if (zScale <= 0) { zScale = 1; }
		scale.setLocation(renderer.getLayerWidth() / renderer.getWidth(),
				renderer.getLayerHeight() / renderer.getHeight(), zScale);

	}

	/**
	 * Compute offset.
	 */
	public void computeOffset() {
		final IScope scope = renderer.getSurface().getScope();
		final IExpression expr = layer.getDefinition().getFacet(IKeyword.POSITION);

		if (expr != null) {
			final boolean containsPixels = expr.findAny(e -> e instanceof PixelUnitExpression);
			offset.setLocation(Cast.asPoint(scope, expr.value(scope)));
			if (Math.abs(offset.x) <= 1 && !containsPixels) { offset.x *= renderer.getEnvWidth(); }
			if (offset.x < 0) { offset.x = renderer.getEnvWidth() - offset.x; }
			if (Math.abs(offset.y) <= 1 && !containsPixels) { offset.y *= renderer.getEnvHeight(); }
			if (offset.y < 0) { offset.y = renderer.getEnvHeight() - offset.y; }

		}
		increaseZ();
	}

	/**
	 * Increase Z.
	 */
	protected void increaseZ() {
		double currentZLayer = renderer.getMaxEnvDim() * layer.getData().getPosition().getZ();
		currentZLayer += layer.getData().getAddedElevation() * renderer.getMaxEnvDim();
		offset.z = currentZLayer;
	}

	/**
	 * Checks if is light interaction.
	 *
	 * @return true, if is light interaction
	 */
	public boolean isLightInteraction() { return true; }

	/**
	 * New current list.
	 *
	 * @return the list
	 */
	protected List newCurrentList() {
		return /* Collections.synchronizedList( */new ArrayList()/* ) */;
	}

	/**
	 * Checks if is pickable.
	 *
	 * @return true, if is pickable
	 */
	protected boolean isPickable() { return layer == null ? false : layer.getData().isSelectable(); }

	/**
	 * Draw.
	 *
	 * @param gl
	 *            the gl
	 */
	public void draw(final OpenGL gl) {
		if (isInvalid()) return;
		drawWithoutShader(gl);
	}

	/**
	 * Draw without shader.
	 *
	 * @param gl
	 *            the gl
	 */
	private void drawWithoutShader(final OpenGL gl) {
		prepareDrawing(gl);
		try {
			final boolean picking = renderer.getPickingHelper().isPicking();
			doDrawing(gl, picking);
		} finally {
			stopDrawing(gl);
		}

	}

	/**
	 * Do drawing.
	 *
	 * @param gl
	 *            the gl
	 * @param picking
	 *            the picking
	 */
	protected void doDrawing(final OpenGL gl, final boolean picking) {

		if (picking) {
			if (isPickable()) {
				gl.runWithNames(() -> drawAllObjects(gl, true));
			} else if (renderer.getPickingHelper().hasPicked()) {
				// A pickable object from another layer has been picked
				drawAllObjects(gl, false);
			} else {
				// We do not draw the layer during the picking process
			}
		} else if (isAnimated) {
			drawAllObjects(gl, false);
		} else {
			if (openGLListIndex == null) { openGLListIndex = gl.compileAsList(() -> drawAllObjects(gl, false)); }
			gl.drawList(openGLListIndex);
		}
	}

	/**
	 * Prepare drawing.
	 *
	 * @param gl
	 *            the gl
	 */
	protected void prepareDrawing(final OpenGL gl) {
		gl.getGL().glEnable(GL.GL_DEPTH_TEST);
		gl.push(GLMatrixFunc.GL_MODELVIEW);
		final GamaPoint nonNullOffset = getOffset();
		gl.translateBy(nonNullOffset.x, -nonNullOffset.y, nonNullOffset.z);
		final GamaPoint nonNullScale = getScale();
		gl.scaleBy(nonNullScale.x, nonNullScale.y, nonNullScale.z);
	}

	/**
	 * Stop drawing.
	 *
	 * @param gl
	 *            the gl
	 */
	protected void stopDrawing(final OpenGL gl) {
		gl.pop(GLMatrixFunc.GL_MODELVIEW);
	}

	/**
	 * Draw all objects.
	 *
	 * @param gl
	 *            the gl
	 * @param picking
	 *            the picking
	 */
	protected void drawAllObjects(final OpenGL gl, final boolean picking) {
		if (traces != null) {
			double delta = 0;
			if (isFading) {
				final int size = traces.size();
				delta = size == 0 ? 0 : 1d / size;
			}
			double alpha = 0d;
			for (final List<AbstractObject<?, ?>> list : traces) {
				alpha = delta == 0d ? this.alpha : this.alpha * (alpha + delta);
				drawObjects(gl, list, alpha, picking);
			}
		} else {
			drawObjects(gl, currentList, alpha, picking);
		}
	}

	/**
	 * Draw objects.
	 *
	 * @param gl
	 *            the gl
	 * @param list
	 *            the list
	 * @param alpha
	 *            the alpha
	 * @param picking
	 *            the picking
	 */
	protected void drawObjects(final OpenGL gl, final List<AbstractObject<?, ?>> list, final double alpha,
			final boolean picking) {
		final ImmutableList<AbstractObject> l = ImmutableList.copyOf(list);
		gl.setCurrentObjectAlpha(alpha);
		for (final AbstractObject object : l) { object.draw(gl, gl.getDrawerFor(object.type), picking); }
	}

	/**
	 * Checks if is static.
	 *
	 * @return true, if is static
	 */
	public boolean isStatic() {
		if (layer == null) return true;
		return !layer.getData().isDynamic();
	}

	/**
	 * Sets the alpha.
	 *
	 * @param a
	 *            the new alpha
	 */
	public void setAlpha(final Double a) { alpha = a; }

	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public GamaPoint getOffset() { return offset == null ? NULL_OFFSET : offset; }

	/**
	 * Sets the offset.
	 *
	 * @param offset
	 *            the new offset
	 */
	public void setOffset(final GamaPoint offset) {
		if (offset != null) {
			this.offset = new GamaPoint(offset);
		} else {
			this.offset = null;
		}
	}

	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public GamaPoint getScale() { return scale == null ? NULL_SCALE : scale; }

	/**
	 * Sets the scale.
	 *
	 * @param scale
	 *            the new scale
	 */
	public void setScale(final GamaPoint scale) { this.scale = new GamaPoint(scale); }

	/**
	 * Adds the string.
	 *
	 * @param string
	 *            the string
	 * @param attributes
	 *            the attributes
	 */
	public void addString(final String string, final TextDrawingAttributes attributes) {
		currentList.add(new StringObject(string, attributes));
	}

	/**
	 * Adds the file.
	 *
	 * @param file
	 *            the file
	 * @param attributes
	 *            the attributes
	 */
	public void addFile(final GamaGeometryFile file, final DrawingAttributes attributes) {
		currentList.add(new ResourceObject(file, attributes));
	}

	/**
	 * Adds the image.
	 *
	 * @param o
	 *            the o
	 * @param attributes
	 *            the attributes
	 */
	public void addImage(final Object o, final DrawingAttributes attributes) {
		// If no dimensions have been defined, then the image is considered as wide and tall as the environment
		Scaling3D size = attributes.getSize();
		if (size == null) {
			size = Scaling3D.of(renderer.getEnvWidth(), renderer.getEnvHeight(), 0);
			attributes.setSize(size);
		}
		final GamaPoint loc = attributes.getLocation();
		final GamaPoint newLoc = loc == null ? size.toGamaPoint().dividedBy(2) : loc;
		// We build a rectangle that will serve as a "support" for the image (which will become its texture)
		final Geometry geometry =
				GamaGeometryType.buildRectangle(size.getX(), size.getY(), new GamaPoint()).getInnerGeometry();

		attributes.setLocation(newLoc);
		attributes.setTexture(o);
		attributes.setSynthetic(true);
		addGeometry(geometry, attributes);
	}

	/**
	 * Adds the field.
	 *
	 * @param fieldValues
	 *            the field values
	 * @param attributes
	 *            the attributes
	 */
	public void addField(final IField fieldValues, final MeshDrawingAttributes attributes) {
		currentList.add(new MeshObject(fieldValues, attributes));
	}

	/**
	 * Adds the geometry.
	 *
	 * @param geometry
	 *            the geometry
	 * @param attributes
	 *            the attributes
	 */
	public void addGeometry(final Geometry geometry, final DrawingAttributes attributes) {
		isAnimated = attributes.isAnimated();
		currentList.add(new GeometryObject(geometry, attributes));
	}

	/**
	 * Gets the trace.
	 *
	 * @return the trace
	 */
	protected int getTrace() {
		if (layer == null) return 0;
		final Integer trace = layer.getData().getTrace();
		return trace == null ? 0 : trace;
	}

	/**
	 * Gets the fading.
	 *
	 * @return the fading
	 */
	protected boolean getFading() {
		if (layer == null) return false;
		final Boolean fading = layer.getData().getFading();
		return fading == null ? false : fading;
	}

	/**
	 * Clear.
	 *
	 * @param gl
	 *            the gl
	 */
	public void clear(final OpenGL gl) {

		if (traces != null) {
			final int sizeLimit = getTrace();
			isFading = getFading();
			final int size = traces.size();
			for (int i = 0, n = size - sizeLimit; i < n; i++) { traces.poll(); }
			currentList = newCurrentList();
			traces.offer(currentList);
		} else {
			currentList.clear();
		}
		final Integer index = openGLListIndex;
		if (index != null) {
			gl.deleteList(index);
			openGLListIndex = null;
		}

	}

	/**
	 * Checks if is invalid.
	 *
	 * @return true, if is invalid
	 */
	public boolean isInvalid() { return isInvalid; }

	/**
	 * Invalidate.
	 */
	public void invalidate() {
		isInvalid = true;
	}

	/**
	 * Checks for trace.
	 *
	 * @return true, if successful
	 */
	public boolean hasTrace() {
		return getTrace() > 0;
	}

	/**
	 * Checks if is locked.
	 *
	 * @return true, if is locked
	 */
	public boolean isLocked() { return locked; }

	/**
	 * Lock.
	 */
	public void lock() {
		locked = true;
	}

	/**
	 * Unlock.
	 */
	public void unlock() {
		locked = false;
	}

	/**
	 * Checks if is overlay.
	 *
	 * @return true, if is overlay
	 */
	public boolean isOverlay() { return false; }

	/**
	 * Number of traces.
	 *
	 * @return the int
	 */
	public int numberOfTraces() {
		return traces == null ? 1 : traces.size();
	}

	/**
	 * Can split.
	 *
	 * @return true, if successful
	 */
	public boolean canSplit() {
		return true;
	}

	/**
	 * Adds the synthetic object.
	 *
	 * @param list
	 *            the list
	 * @param shape
	 *            the shape
	 * @param color
	 *            the color
	 * @param type
	 *            the type
	 * @param empty
	 *            the empty
	 */
	protected void addSyntheticObject(final List<AbstractObject<?, ?>> list, final IShape shape, final GamaColor color,
			final IShape.Type type, final boolean empty) {
		final DrawingAttributes att = new ShapeDrawingAttributes(shape, (IAgent) null, color, color, type,
				GamaPreferences.Displays.CORE_LINE_WIDTH.getValue(), null);
		att.setEmpty(empty);
		att.setHeight(shape.getDepth());
		att.setLighting(false);
		list.add(new GeometryObject(shape.getInnerGeometry(), att));
	}

	/**
	 * Force redraw.
	 *
	 * @param gl
	 *            the gl
	 */
	public void forceRedraw(final OpenGL gl) {
		if (layer == null) return;
		if (openGLListIndex != null) {
			gl.deleteList(openGLListIndex);
			openGLListIndex = null;
		}
		// layer.draw(renderer.getSurface().getScope(), renderer);

	}

}
