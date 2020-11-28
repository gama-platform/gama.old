/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.ShapeExecuter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;
import static msi.gama.common.geometry.GeometryUtils.getPointsOf;
import static msi.gama.common.geometry.GeometryUtils.rotate;
import static msi.gama.common.geometry.GeometryUtils.translate;
import static msi.gama.common.geometry.Scaling3D.of;
import static msi.gaml.operators.Cast.asFloat;
import static msi.gaml.operators.Cast.asGeometry;
import static msi.gaml.types.GamaFileType.createFile;
import static msi.gaml.types.GamaGeometryType.buildArrow;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.expressions.IExpression;

class ShapeExecuter extends DrawExecuter {

	final IExpression endArrow, beginArrow;
	final IShape constantShape;
	final Double constantEnd, constantBegin;
	final boolean hasArrows;
	final GamaPoint center = new GamaPoint();

	ShapeExecuter(final IExpression item, final IExpression beginArrow, final IExpression endArrow)
			throws GamaRuntimeException {
		super(item);
		constantShape = item.isConst() ? asGeometry(null, item.getConstValue()) : null;
		hasArrows = beginArrow != null || endArrow != null;
		if (beginArrow != null) {
			if (beginArrow.isConst()) {
				constantBegin = asFloat(null, beginArrow.getConstValue());
				this.beginArrow = null;
			} else {
				constantBegin = null;
				this.beginArrow = beginArrow;
			}
		} else {
			this.beginArrow = null;
			constantBegin = null;
		}
		if (endArrow != null) {
			if (endArrow.isConst()) {
				constantEnd = asFloat(null, endArrow.getConstValue());
				this.endArrow = null;
			} else {
				constantEnd = null;
				this.endArrow = beginArrow;
			}
		} else {
			this.endArrow = null;
			constantEnd = null;
		}

	}

	@Override
	Rectangle2D executeOn(final IScope scope, final IGraphics gr, final DrawingData data) throws GamaRuntimeException {
		final IShape shape = constantShape == null ? asGeometry(scope, item.value(scope), false) : constantShape;
		if (shape == null) { return null; }
		final DrawingAttributes attributes = computeAttributes(scope, data, shape);
		Geometry gg = shape.getInnerGeometry();
		if (gg == null) { return null; }
		final ICoordinates ic = getContourCoordinates(gg);
		ic.ensureClockwiseness();

		// If the graphics is 2D, we pre-translate and pre-rotate the geometry
		if (gr.is2D()) {
			ic.getCenter(center);
			rotate(gg, center, attributes.getRotation());
			final GamaPoint location = attributes.getLocation();
			if (location != null) {
				if (gg.getNumPoints() == 1) {
					gg = GEOMETRY_FACTORY.createPoint(location);
				} else {
					translate(gg, center, location);
				}
			}
			gg.geometryChanged();
		}
		if (hasArrows) {
			final Geometry withArrows = addArrows(scope, gg, !attributes.isEmpty());
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
				final Envelope visible = gr.getVisibleRegion();
				if (visible != null) {
					if (!visible.intersects(e)) { return null; }
					// XXX EXPERIMENTAL
				}
			} finally {
				e.dispose();
			}
		}

		// The textures are computed as well in advance
		addTextures(scope, attributes);
		// And we ask the IGraphics object to draw the shape
		return gr.drawShape(gg, attributes);
	}

	DrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final IShape shape) {
		Double depth = data.depth.get();
		if (depth == null) {
			depth = shape.getDepth();
		}
		final DrawingAttributes attributes = new ShapeDrawingAttributes(of(data.size.get()), depth,
				data.rotation.get(), data.getLocation(), data.empty.get(), data.color.get(), /* data.getColors(), */
				data.border.get(), data.texture.get(), data.material.get(), scope.getAgent(),
				shape.getGeometricalType(), data.lineWidth.get(), data.lighting.get());
		return attributes;
	}

	/**
	 * @param scope
	 * @param attributes
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private void addTextures(final IScope scope, final DrawingAttributes attributes) {
		if (attributes.getTextures() == null) { return; }
		attributes.getTextures().replaceAll((s) -> {
			GamaImageFile image = null;
			if (s instanceof GamaImageFile) {
				image = (GamaImageFile) s;
			} else if (s instanceof String) {
				image = (GamaImageFile) createFile(scope, (String) s, null);
			}
			if (image == null || !image.exists(scope)) {
				throw new GamaRuntimeFileException(scope, "Texture file not found: " + s);
			}
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
			final Geometry all = GEOMETRY_FACTORY.buildGeometry(geoms);
			final Geometry world = scope.getSimulation().getInnerGeometry();
			result = all.intersection(world);
			// WARNING Does not correctly handle rotations or translations
		}
		return result;
	}

	private final List<Geometry> tempArrowList = new ArrayList<>();

	private Geometry addArrows(final IScope scope, final Geometry g1, final Boolean fill) {
		final GamaPoint[] points = getPointsOf(g1);
		final int size = points.length;
		if (size < 2) { return g1; }
		tempArrowList.clear();
		tempArrowList.add(g1);
		Geometry end = null, begin = null;
		if (endArrow != null || constantEnd != null) {
			final double width = constantEnd == null ? asFloat(scope, endArrow.value(scope)) : constantEnd;
			if (width > 0) {
				end = buildArrow(points[size - 2], points[size - 1], width, width + width / 3, fill).getInnerGeometry();
				tempArrowList.add(end);
			}
		}
		if (beginArrow != null || constantBegin != null) {
			final double width = constantBegin == null ? asFloat(scope, beginArrow.value(scope)) : constantBegin;
			if (width > 0) {
				begin = buildArrow(points[1], points[0], width, width + width / 3, fill).getInnerGeometry();
				tempArrowList.add(begin);
			}
		}
		return GEOMETRY_FACTORY.createGeometryCollection(tempArrowList.toArray(new Geometry[tempArrowList.size()]));
	}
}