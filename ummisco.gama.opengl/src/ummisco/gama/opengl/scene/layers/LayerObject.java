/*******************************************************************************************************
 *
 * LayerObject.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import java.util.ArrayList;
import java.util.LinkedList;

import org.locationtech.jts.geom.Geometry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Rotation3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.FramedLayerData;
import msi.gama.outputs.layers.ILayerData;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.matrix.IField;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.units.PixelUnitExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.MeshDrawingAttributes;
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

	/** The Constant NULL_ROTATION. */
	final static AxisAngle NULL_ROTATION = new AxisAngle(0d);

	/**
	 * The Class Trace.
	 */
	class Trace extends ArrayList<AbstractObject<?, ?>> {
		/** The offset. */
		final GamaPoint offset = new GamaPoint(NULL_OFFSET);
		/** The scale. */
		final GamaPoint scale = new GamaPoint(NULL_SCALE);

		/** The rotation. */
		AxisAngle rotation = NULL_ROTATION;

		/**
		 * Instantiates a new trace.
		 */
		Trace() {
			computeOffset();
			computeScale();
			computeRotation();
		}

		/**
		 * Compute scale.
		 *
		 * @return the gama point
		 */
		public void computeScale() {
			LayerObject.this.computeScale(this);
		}

		/**
		 * Compute offset.
		 *
		 * @return the gama point
		 */
		public void computeOffset() {
			LayerObject.this.computeOffset(this);
		}

		/**
		 * Compute rotation.
		 */
		public void computeRotation() {
			LayerObject.this.computeRotation(this);
		}

		/**
		 * Gets the offset.
		 *
		 * @return the offset
		 */
		public GamaPoint getOffset() { return offset; }

		/**
		 * Gets the scale.
		 *
		 * @return the scale
		 */
		public GamaPoint getScale() { return scale; }

		/**
		 * Gets the rotation.
		 *
		 * @return the rotation
		 */
		public AxisAngle getRotation() { return rotation; }

		/**
		 * As array.
		 *
		 * @return the abstract object[]
		 */
		public AbstractObject[] asArray() {
			return toArray(new AbstractObject[size()]);
		}
	}

	/** The alpha. */
	protected volatile Double alpha = 1d;

	/** The layer. */
	public final ILayer layer;

	/** The is invalid. */
	volatile boolean isInvalid;

	/** The locked. */
	private volatile boolean locked;

	/** The is animated. */
	volatile boolean isAnimated;

	/** The renderer. */
	protected final IOpenGLRenderer renderer;

	/** The traces. */
	protected final LinkedList<Trace> traces;

	/** The current list. */
	protected Trace currentList;

	/** The open GL list index. */
	protected volatile Integer openGLListIndex;

	/** The is fading. */
	protected volatile boolean isFading;

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
		currentList = new Trace();
		if (layer == null) {
			traces = null;
		} else {
			Integer number = layer.getData().getTrace();
			if (number == null || number == 0) {
				traces = null;
			} else {
				traces = new LinkedList();
				traces.add(currentList);
			}
		}
	}

	/**
	 * Compute rotation.
	 *
	 * @param trace
	 *            the trace
	 */
	public void computeRotation(final Trace trace) {
		AxisAngle oldRotation = trace.rotation;
		// if vectors are different... (not computed for the moment)
		// if angles are different
		Double newAngleInDeg = layer.getData().getRotation();
		if (newAngleInDeg.equals(oldRotation.getAngle())) return;
		trace.rotation = new AxisAngle(Rotation3D.PLUS_K, newAngleInDeg);
	}

	/**
	 * Compute scale.
	 */
	public void computeScale(final Trace list) {
		double zScale = layer.getData().getSize().getZ();
		if (zScale <= 0) { zScale = 1; }
		list.scale.setLocation(renderer.getLayerWidth() / renderer.getWidth(),
				renderer.getLayerHeight() / renderer.getHeight(), zScale);

	}

	/**
	 * Compute offset.
	 */
	public void computeOffset(final Trace list) {
		final IScope scope = renderer.getSurface().getScope();
		final IExpression expr = layer.getDefinition().getFacet(IKeyword.POSITION);

		if (expr != null) {
			final boolean containsPixels = expr.findAny(e -> e instanceof PixelUnitExpression);
			GamaPoint offset = list.offset;
			offset.setLocation(Cast.asPoint(scope, expr.value(scope)));
			if (Math.abs(offset.x) <= 1 && !containsPixels) { offset.x *= renderer.getEnvWidth(); }
			if (Math.abs(offset.y) <= 1 && !containsPixels) { offset.y *= renderer.getEnvHeight(); }

			// REMOVE TO FIX #3342
			// if (offset.x < 0) { offset.x = renderer.getEnvWidth() - offset.x; }
			// if (offset.y < 0) { offset.y = renderer.getEnvHeight() - offset.y; }

		}
		increaseZ(list);
	}

	/**
	 * Increase Z.
	 */
	protected void increaseZ(final Trace list) {
		double currentZLayer = renderer.getMaxEnvDim() * layer.getData().getPosition().getZ();
		// currentZLayer += layer.getData().getAddedElevation() * renderer.getMaxEnvDim();
		list.offset.z = currentZLayer;
	}

	/**
	 * Checks if is light interaction.
	 *
	 * @return true, if is light interaction
	 */
	public boolean isLightInteraction() { return true; }

	/**
	 * Checks if is pickable.
	 *
	 * @return true, if is pickable
	 */
	protected boolean isPickable() { return layer != null && layer.getData().isSelectable(); }

	/**
	 * Draw.
	 *
	 * @param gl
	 *            the gl
	 */
	public void draw(final OpenGL gl) {
		if (isInvalid()) return;
		if (hasDepth()) {
			gl.getGL().glEnable(GL.GL_DEPTH_TEST);
		} else {
			// Addition to fix #2228 and #2222
			gl.suspendZTranslation();
			gl.getGL().glDisable(GL.GL_DEPTH_TEST);
		}
		gl.push(GLMatrixFunc.GL_MODELVIEW);
		try {
			doDrawing(gl);
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
	protected void doDrawing(final OpenGL gl) {
		if (renderer.getPickingHelper().isPicking()) {
			if (isPickable()) { gl.runWithNames(() -> drawAllObjects(gl, true)); }
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
	protected void prepareDrawing(final OpenGL gl, final Trace list) {
		final GamaPoint nonNullOffset = list.getOffset();
		gl.translateBy(nonNullOffset.x, -nonNullOffset.y, hasDepth() ? nonNullOffset.z : 0);
		final GamaPoint nonNullScale = list.getScale();
		gl.scaleBy(nonNullScale.x, nonNullScale.y, nonNullScale.z);
		final AxisAngle nonNullRotation = list.getRotation();

		// Rotation
		double x = nonNullOffset.x + renderer.getEnvWidth() * nonNullScale.x / 2;
		double y = nonNullOffset.y + renderer.getEnvHeight() * nonNullScale.y / 2;

		gl.translateBy(x, -y, 0d);
		GamaPoint p = nonNullRotation.getAxis();
		gl.rotateBy(nonNullRotation.getAngle(), p.x, p.y, p.z);
		gl.translateBy(-x, y, 0d);
		addFrame(gl);
	}

	protected void addFrame(final OpenGL gl) {
		if (layer == null) return;
		ILayerData d = layer.getData();
		if (d instanceof FramedLayerData data) {
			GamaPoint size = new GamaPoint(renderer.getEnvWidth(), renderer.getEnvHeight());
			final IScope scope = renderer.getSurface().getScope();
			final IExpression expr = layer.getDefinition().getFacet(IKeyword.SIZE);
			if (expr != null) {
				size = Cast.asPoint(scope, expr.value(scope));
				if (size.x <= 1) { size.x *= renderer.getEnvWidth(); }
				if (size.y <= 1) { size.y *= renderer.getEnvHeight(); }
			}
			gl.pushMatrix();
			boolean previous = gl.setObjectWireframe(false);
			try {
				gl.translateBy(size.x / 2, -size.y / 2, 0);
				gl.scaleBy(size.x, size.y, 1);
				if (data.getBackgroundColor(scope) != null) {
					gl.setCurrentColor(data.getBackgroundColor(scope), 1 - data.getTransparency(scope));
					gl.drawCachedGeometry(IShape.Type.SQUARE, null);
				}
				if (data.getBorderColor() != null) {
					gl.setObjectWireframe(true);
					gl.drawCachedGeometry(IShape.Type.SQUARE, data.getBorderColor());
				}

			} finally {
				gl.setObjectWireframe(previous);
				gl.popMatrix();
			}
		}

	}

	/**
	 * Enable depth test.
	 *
	 * @return true, if successful
	 */
	protected boolean hasDepth() {
		return true;
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
			for (final Trace list : traces) {
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
	protected final void drawObjects(final OpenGL gl, final Trace list, final double alpha, final boolean picking) {
		prepareDrawing(gl, list);
		gl.setCurrentObjectAlpha(alpha);
		for (final AbstractObject object : list.asArray()) { gl.getDrawerFor(object.type).draw(object, picking); }
	}

	/**
	 * Checks if is static.
	 *
	 * @return true, if is static
	 */
	public boolean isStatic() { return layer == null || !layer.getData().isDynamic(); }

	/**
	 * Sets the alpha.
	 *
	 * @param a
	 *            the new alpha
	 */
	public void setAlpha(final Double a) { alpha = a; }

	/**
	 * Sets the offset.
	 *
	 * @param offset
	 *            the new offset
	 */
	public void setOffset(final GamaPoint offset) {
		if (offset != null) {
			currentList.offset.setLocation(offset);
		} else {
			currentList.offset.setLocation(NULL_OFFSET);
		}
	}

	/**
	 * Sets the scale.
	 *
	 * @param scale
	 *            the new scale
	 */
	public void setScale(final GamaPoint scale) {
		currentList.scale.setLocation(scale);
	}

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
		isAnimated = /* isAnimated || ?? */attributes.isAnimated();
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
			currentList = new Trace();
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
	 * Recompute offset.
	 */
	public void recomputeOffset() {
		computeOffset(currentList);
	}

}
