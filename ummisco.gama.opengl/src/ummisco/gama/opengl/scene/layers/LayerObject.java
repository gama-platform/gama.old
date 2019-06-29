/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.layers.LayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.jogamp.opengl.GL2;
import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.PixelUnitExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.LayerElement;
import ummisco.gama.opengl.scene.LayerElement.DrawerType;

/**
 * Class LayerObject.
 *
 * @author drogoul
 * @since 3 mars 2014
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class LayerObject {

	final static GamaPoint NULL_OFFSET = GamaPoint.createEmpty();
	final static GamaPoint NULL_SCALE = GamaPoint.create(1, 1, 1);

	GamaPoint offset = GamaPoint.create(NULL_OFFSET);
	GamaPoint scale = GamaPoint.create(NULL_SCALE);
	protected Double alpha = 1d;
	public final ILayer layer;
	volatile boolean isInvalid;
	protected final boolean overlay;
	volatile boolean locked;
	boolean isAnimated;
	protected final IOpenGLRenderer renderer;
	protected final LinkedList<List<LayerElement<?, ?>>> traces;
	protected List<LayerElement<?, ?>> currentList;
	protected Integer openGLListIndex;
	protected boolean isFading;

	public LayerObject(final IOpenGLRenderer renderer2, final ILayer layer) {
		this.renderer = renderer2;
		this.layer = layer;
		this.overlay = computeOverlay();
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

	public void computeScale() {
		if (!overlay) {
			double zScale = layer.getData().getSize().getZ();
			if (zScale <= 0) {
				zScale = 1;
			}
			scale.setLocation(renderer.getLayerWidth() / renderer.getWidth(),
					renderer.getLayerHeight() / renderer.getHeight(), zScale);
		} else {
			scale.setLocation(0.9, 0.9, 1);
		}

	}

	public void computeOffset() {
		final IScope scope = renderer.getSurface().getScope();
		final IExpression expr = layer.getDefinition().getFacet(IKeyword.POSITION);

		if (expr != null) {
			final boolean containsPixels = expr.findAny((e) -> e instanceof PixelUnitExpression);
			offset.setLocation(Cast.asPoint(scope, expr.value(scope)));
			if (Math.abs(offset.x) <= 1 && !containsPixels) {
				offset.x *= renderer.getEnvWidth();
			}
			if (offset.x < 0) {
				offset.x = renderer.getEnvWidth() - offset.x;
			}
			if (Math.abs(offset.y) <= 1 && !containsPixels) {
				offset.y *= renderer.getEnvHeight();
			}
			if (offset.y < 0) {
				offset.y = renderer.getEnvHeight() - offset.y;
			}

		}
		if (!overlay) {
			double currentZLayer = renderer.getMaxEnvDim() * layer.getData().getPosition().getZ();
			currentZLayer += layer.getData().getAddedElevation() * renderer.getMaxEnvDim();
			offset.z = currentZLayer;
		}
	}

	protected boolean computeOverlay() {
		return layer != null && layer.isOverlay();
	}

	public boolean isLightInteraction() {
		return true;
	}

	protected List newCurrentList() {
		return /* Collections.synchronizedList( */new ArrayList()/* ) */;
	}

	protected boolean isPickable() {
		return layer == null ? false : layer.getData().isSelectable();
	}

	public void draw(final OpenGL gl) {
		if (isInvalid()) { return; }
		drawWithoutShader(gl);
	}

	private void drawWithoutShader(final OpenGL gl) {

		if (overlay) {
			gl.getGL().glDisable(GL2.GL_DEPTH_TEST);
			// Addition to fix #2228 and #2222
			gl.suspendZTranslation();
			//
			final double viewHeight = gl.getViewHeight();
			final double viewWidth = gl.getViewWidth();
			final double viewRatio = viewWidth / (viewHeight == 0 ? 1 : viewHeight);
			final double worldHeight = gl.getWorldHeight();
			final double worldWidth = gl.getWorldWidth();
			final double maxDim = worldHeight > worldWidth ? worldHeight : worldWidth;
			gl.pushIdentity(GL2.GL_PROJECTION);
			if (viewRatio >= 1.0) {
				gl.getGL().glOrtho(0, maxDim * viewRatio, -maxDim, 0, -1, 1);
			} else {
				gl.getGL().glOrtho(0, maxDim, -maxDim / viewRatio, 0, -1, 1);
			}
			gl.pushIdentity(GL2.GL_MODELVIEW);
		} else {
			gl.getGL().glEnable(GL2.GL_DEPTH_TEST);
		}
		try {
			gl.push(GL2.GL_MODELVIEW);
			final GamaPoint nonNullOffset = getOffset();
			gl.translateBy(nonNullOffset.x, -nonNullOffset.y, overlay ? 0 : nonNullOffset.z);
			final GamaPoint nonNullScale = getScale();
			gl.scaleBy(nonNullScale.x, nonNullScale.y, nonNullScale.z);

			final boolean picking = renderer.getPickingHelper().isPicking() && isPickable();
			if (picking) {
				if (!overlay) {
					gl.runWithNames(() -> drawAllObjects(gl, true));
				}
			} else {
				if (isAnimated || overlay) {
					drawAllObjects(gl, false);
				} else {
					if (openGLListIndex == null) {
						openGLListIndex = gl.compileAsList(() -> drawAllObjects(gl, false));
					}
					gl.drawList(openGLListIndex);
				}
			}
		} finally {
			gl.pop(GL2.GL_MODELVIEW);
			if (overlay) {
				// Addition to fix #2228 and #2222
				gl.resumeZTranslation();
				gl.pop(GL2.GL_MODELVIEW);
				gl.pop(GL2.GL_PROJECTION);
			}
		}

	}

	private void addFrame(final OpenGL gl) {
		GamaPoint size = GamaPoint.create(renderer.getEnvWidth(), renderer.getEnvHeight());
		final IScope scope = renderer.getSurface().getScope();
		final IExpression expr = layer.getDefinition().getFacet(IKeyword.SIZE);
		if (expr != null) {
			size = Cast.asPoint(scope, expr.value(scope));
			if (size.x <= 1) {
				size.x *= renderer.getEnvWidth();
			}
			if (size.y <= 1) {
				size.y *= renderer.getEnvHeight();
			}
		}
		gl.pushMatrix();
		gl.translateBy(0, -size.y, 0);
		gl.scaleBy(size.x, size.y, 1);
		gl.setCurrentColor(((OverlayLayer) layer).getData().getBackgroundColor(scope));
		gl.setCurrentObjectAlpha(((OverlayLayer) layer).getData().getTransparency(scope));
		gl.drawCachedGeometry(IShape.Type.ROUNDED, true, null);
		gl.popMatrix();
	}

	protected void drawAllObjects(final OpenGL gl, final boolean picking) {
		if (overlay) {
			addFrame(gl);
		}
		if (traces != null) {
			double delta = 0;
			if (isFading) {
				final int size = traces.size();
				delta = size == 0 ? 0 : 1d / size;
			}
			double alpha = 0d;
			for (final List<LayerElement<?, ?>> list : traces) {
				alpha = delta == 0d ? this.alpha : this.alpha * (alpha + delta);
				drawObjects(gl, list, alpha, picking);
			}
		} else {
			drawObjects(gl, currentList, alpha, picking);
		}
	}

	protected void drawObjects(final OpenGL gl, final List<LayerElement<?, ?>> list, final double alpha,
			final boolean picking) {
		final ImmutableList<LayerElement> l = ImmutableList.copyOf(list);
		gl.setCurrentObjectAlpha(alpha);
		for (final LayerElement object : l) {
			object.draw(gl, gl.getDrawerFor(object.getDrawerType()), picking);
		}
	}

	public boolean isStatic() {
		if (layer == null) { return true; }
		return !layer.getData().isDynamic();
	}

	public void setAlpha(final Double a) {
		alpha = a;
	}

	public GamaPoint getOffset() {
		return offset == null ? NULL_OFFSET : offset;
	}

	public void setOffset(final GamaPoint offset) {
		if (offset != null) {
			this.offset = GamaPoint.create(offset);
		} else {
			this.offset = null;
		}
	}

	public GamaPoint getScale() {
		return scale == null ? NULL_SCALE : scale;
	}

	public Double getAlpha() {
		return alpha;
	}

	public void setScale(final GamaPoint scale) {
		this.scale = GamaPoint.create(scale);
	}

	public void addString(final String string, final TextDrawingAttributes attributes) {
		currentList.add(LayerElement.createLayerElement(string, attributes, DrawerType.STRING));
	}

	public void addFile(final GamaGeometryFile file, final DrawingAttributes attributes) {
		currentList.add(LayerElement.createLayerElement(file, attributes, DrawerType.RESOURCE));
	}

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
				GamaGeometryType.buildRectangle(size.getX(), size.getY(), GamaPoint.NULL_POINT).getInnerGeometry();

		attributes.setLocation(newLoc);
		attributes.setTexture(o);
		attributes.setSynthetic(true);
		addGeometry(geometry, attributes);
	}

	public void addField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		currentList.add(LayerElement.createLayerElement(fieldValues, attributes, DrawerType.FIELD));
	}

	public void addGeometry(final Geometry geometry, final DrawingAttributes attributes) {
		isAnimated = attributes.isAnimated();
		currentList.add(LayerElement.createLayerElement(geometry, attributes, DrawerType.GEOMETRY));
	}

	protected int getTrace() {
		if (layer == null) { return 0; }
		final Integer trace = layer.getData().getTrace();
		return trace == null ? 0 : trace;
	}

	protected boolean getFading() {
		if (layer == null) { return false; }
		final Boolean fading = layer.getData().getFading();
		return fading == null ? false : fading;
	}

	public void clear(final OpenGL gl) {
		if (traces != null) {
			final int sizeLimit = getTrace();
			isFading = getFading();
			final int size = traces.size();
			for (int i = 0, n = size - sizeLimit; i < n; i++) {
				final List<LayerElement<?, ?>> list = traces.poll();
				for (final LayerElement o : list) {
					o.dispose();
				}
				list.clear();
			}
			currentList = newCurrentList();
			traces.offer(currentList);
		} else {
			for (final LayerElement o : currentList) {
				o.dispose();
			}
			currentList.clear();
		}
		final Integer index = openGLListIndex;
		if (index != null) {
			gl.deleteList(index);
			openGLListIndex = null;
		}

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

	public boolean isLocked() {
		return locked;
	}

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

	public boolean isOverlay() {
		return overlay;
	}

	public int numberOfTraces() {
		return traces == null ? 1 : traces.size();
	}

	public boolean canSplit() {
		return true;
	}

	protected void addSyntheticObject(final List<LayerElement<?, ?>> list, final IShape shape, final GamaColor color,
			final IShape.Type type, final boolean empty) {
		final DrawingAttributes att = new DrawingAttributes(shape, (IAgent) null, color, color, type,
				GamaPreferences.Displays.CORE_LINE_WIDTH.getValue());
		att.setEmpty(empty);
		att.setHeight(shape.getDepth());
		att.setLighting(false);
		list.add(LayerElement.createLayerElement(shape.getInnerGeometry(), att, DrawerType.GEOMETRY));
	}

	public void forceRedraw() {
		if (layer == null) { return; }
		layer.draw(renderer.getSurface().getScope(), renderer);

	}

}
