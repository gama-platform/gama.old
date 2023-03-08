/*******************************************************************************************************
 *
 * ShapeDrawer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.buffer.BufferParameters;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IDrawDelegate;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class ShapeExecuter.
 */
public class ShapeDrawer implements IDrawDelegate {

	/**
	 * Execute on.
	 *
	 * @param scope
	 *            the scope
	 * @param gr
	 *            the gr
	 * @param data
	 *            the data
	 * @return the rectangle 2 D
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public Rectangle2D executeOn(final IGraphicsScope scope, final DrawingData data, final IExpression... items)
			throws GamaRuntimeException {
		final IShape shape = Cast.asGeometry(scope, items[0].value(scope), false);
		if (shape == null) return null;
		final DrawingAttributes attributes = computeAttributes(scope, data, shape);
		Geometry gg = shape.getInnerGeometry();
		if (gg == null) return null;
		final ICoordinates ic = GeometryUtils.getContourCoordinates(gg);
		ic.ensureClockwiseness();

		// If the graphics is 2D, we pre-translate and pre-rotate the geometry
		if (scope.getGraphics().is2D()) {
			/** The center. */
			final GamaPoint center = ic.getCenter();
			final AxisAngle rot = attributes.getRotation();
			final GamaPoint location = attributes.getLocation();
			if (rot != null || location != null) {
				// Do this instead of copy() or clone() to avoid the exception quoted in #3602
				// Seems to work...
				gg = gg.buffer(0.0, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_FLAT);
			}
			GeometryUtils.rotate(gg, center, rot);
			if (location != null) {
				if (gg.getNumPoints() == 1) {
					gg = GeometryUtils.GEOMETRY_FACTORY.createPoint(location);
				} else {
					GeometryUtils.translate(gg, center, location);
				}
			}
			// gg.geometryChanged();
		}
		// Items is of length 3 , but let's verify anyway
		if (items.length > 1) {
			final Geometry withArrows = addArrows(scope, gg, items[1], items[2], !attributes.isEmpty());
			if (withArrows != gg) {
				gg = withArrows;
				attributes.setType(IShape.Type.NULL);
			}
		}
		final Geometry withTorus = addToroidalParts(scope, gg);
		if (withTorus != gg) {
			gg = withTorus;
			attributes.setType(IShape.Type.NULL);
		}

		// XXX EXPERIMENTAL See Issue #1521
		if (GamaPreferences.Displays.DISPLAY_ONLY_VISIBLE.getValue() && !scope.getExperiment().isHeadless()) {
			final Envelope3D e = shape.getEnvelope();
			try {
				final Envelope visible = scope.getGraphics().getVisibleRegion();
				if (visible != null && !visible.intersects(e)) return null;
			} finally {
				e.dispose();
			}
		}

		// The textures are computed as well in advance
		addTextures(scope, attributes);
		// And we ask the IGraphics object to draw the shape
		return scope.getGraphics().drawShape(gg, attributes);
	}

	/**
	 * Compute attributes.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @param shape
	 *            the shape
	 * @return the drawing attributes
	 */
	DrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final IShape shape) {
		Double depth = data.depth.get();
		if (depth == null) { depth = shape.getDepth(); }
		return new ShapeDrawingAttributes(Scaling3D.of(data.size.get()), depth, data.rotation.get(), data.getLocation(),
				data.empty.get(), data.color.get(), /* data.getColors(), */
				data.border.get(), data.texture.get(), data.material.get(), scope.getAgent(),
				shape.getGeometricalType(), data.lineWidth.get(), data.lighting.get());
	}

	/**
	 * @param scope
	 * @param attributes
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private void addTextures(final IScope scope, final DrawingAttributes attributes) {
		if (attributes.getTextures() == null) return;
		attributes.getTextures().replaceAll(s -> {
			GamaImageFile image = null;
			if (s instanceof GamaImageFile) {
				image = (GamaImageFile) s;
			} else if (s instanceof String) {
				image = (GamaImageFile) GamaFileType.createFile(scope, (String) s, null);
			}
			if (image == null || !image.exists(scope))
				throw new GamaRuntimeFileException(scope, "Texture file not found: " + s);
			return image;

		});
	}

	/**
	 * @param scope
	 * @param shape
	 * @return
	 */
	private Geometry addToroidalParts(final IScope scope, final Geometry shape) {
		Geometry result = shape;
		final ITopology t = scope.getTopology();
		if (t != null && t.isTorus()) {
			final List<Geometry> geoms = t.listToroidalGeometries(shape);
			final Geometry all = GeometryUtils.GEOMETRY_FACTORY.buildGeometry(geoms);
			final Geometry world = scope.getSimulation().getInnerGeometry();
			result = all.intersection(world);
			// WARNING Does not correctly handle rotations or translations
		}
		return result;
	}

	/** The temp arrow list. */
	// private final List<Geometry> tempArrowList = new ArrayList<>();

	/**
	 * Adds the arrows.
	 *
	 * @param scope
	 *            the scope
	 * @param g1
	 *            the g 1
	 * @param fill
	 *            the fill
	 * @return the geometry
	 */
	private Geometry addArrows(final IScope scope, final Geometry g1, final IExpression beginArrow,
			final IExpression endArrow, final Boolean fill) {
		if (g1 == null) return g1;
		final GamaPoint[] points = GeometryUtils.getPointsOf(g1);
		final int size = points.length;
		if (size < 2) return g1;
		Geometry end = null, begin = null;
		if (endArrow != null) {
			final double width = Cast.asFloat(scope, endArrow.value(scope));
			if (width > 0) {
				end = GamaGeometryType.buildArrow(points[size - 2], points[size - 1], width, width + width / 3, fill)
						.getInnerGeometry();
			}
		}
		if (beginArrow != null) {
			final double width = Cast.asFloat(scope, beginArrow.value(scope));
			if (width > 0) {
				begin = GamaGeometryType.buildArrow(points[1], points[0], width, width + width / 3, fill)
						.getInnerGeometry();
			}
		}
		if (end == null) {
			if (begin == null) return g1;
			return GeometryUtils.GEOMETRY_FACTORY.createCollection(g1, begin);
		}
		if (begin == null) return GeometryUtils.GEOMETRY_FACTORY.createCollection(g1, end);
		return g1;
	}

	/**
	 * Type drawn.
	 *
	 * @return the i type
	 */
	@Override
	public IType<?> typeDrawn() {
		return Types.GEOMETRY;
	}
}