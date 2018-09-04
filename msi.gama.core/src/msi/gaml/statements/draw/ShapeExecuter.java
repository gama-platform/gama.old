/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.ShapeExecuter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.Rotation3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.Types;

class ShapeExecuter extends DrawExecuter {

	final IExpression endArrow, beginArrow;
	final IShape constantShape;
	final Double constantEnd, constantBegin;
	final boolean hasArrows;
	final GamaPoint center = new GamaPoint();

	ShapeExecuter(final IExpression item, final IExpression beginArrow, final IExpression endArrow)
			throws GamaRuntimeException {
		super(item);
		constantShape = item.isConst() ? Cast.asGeometry(null, item.getConstValue()) : null;
		hasArrows = beginArrow != null || endArrow != null;
		if (beginArrow != null) {
			if (beginArrow.isConst()) {
				constantBegin = Cast.asFloat(null, beginArrow.getConstValue());
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
				constantEnd = Cast.asFloat(null, endArrow.getConstValue());
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
		final IShape shape = constantShape == null ? Cast.asGeometry(scope, item.value(scope), false) : constantShape;
		if (shape == null) { return null; }
		// final Geometry geom = shape.getInnerGeometry();
		final ShapeDrawingAttributes attributes = computeAttributes(scope, data, shape);

		Geometry gg = shape.getInnerGeometry();
		if (gg == null) { return null; }
		final ICoordinates ic = GeometryUtils.getContourCoordinates(gg);
		ic.ensureClockwiseness();

		// If the graphics is 2D, we pre-translate and pre-rotate the geometry
		if (gr.is2D()) {
			ic.getCenter(center);
			if (attributes.getRotation() != null) {
				final Rotation3D r = new Rotation3D.CenteredOn(attributes.getRotation(), center);
				gg.apply(r);
			}
			if (gg.getNumPoints() == 1) {
				gg = GeometryUtils.GEOMETRY_FACTORY.createPoint(attributes.getLocation());
			} else {
				final GamaPoint location = attributes.getLocation();
				if (location != null) {
					final double dx = location.x - center.x;
					final double dy = location.y - center.y;
					final double dz = location.z - center.z;
					GeometryUtils.translate(gg, dx, dy, dz);
				}
			}
			gg.geometryChanged();
		}
		final Geometry withArrows = addArrows(scope, gg, !attributes.isEmpty());
		if (withArrows != gg) {
			gg = withArrows;
			attributes.type = IShape.Type.NULL;
		}
		final Geometry withTorus = addToroidalParts(scope, gg);
		if (withTorus != gg) {
			gg = withTorus;
			attributes.type = IShape.Type.NULL;
		}

		// XXX EXPERIMENTAL See Issue #1521
		if (GamaPreferences.Displays.DISPLAY_ONLY_VISIBLE.getValue()
				&& /* !GAMA.isInHeadLessMode() */ !scope.getExperiment().isHeadless()) {
			final Envelope e = shape.getEnvelope();
			final Envelope visible = gr.getVisibleRegion();
			if (visible != null) {
				if (!visible.intersects(e)) { return null; }
				// XXX EXPERIMENTAL
			}
		}

		// The textures are computed as well in advance
		addTextures(scope, attributes);
		// And we ask the IGraphics object to draw the shape
		return gr.drawShape(gg, attributes);
	}

	ShapeDrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final IShape shape) {
		final ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(Scaling3D.of(data.size.get()),
				data.depth.get(), data.rotation.get(), data.getLocation(), data.empty.get(), data.getCurrentColor(),
				data.getColors(), data.border.get(), data.texture.get(), data.material.get(), scope.getAgent(),
				shape.getGeometricalType(), data.lineWidth.get(), data.lighting.get());
		// We push the depth of the geometry if none have been specified already
		attributes.setHeightIfAbsent((Double) shape.getAttribute(IShape.DEPTH_ATTRIBUTE));
		return attributes;
	}

	/**
	 * @param scope
	 * @param attributes
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private void addTextures(final IScope scope, final ShapeDrawingAttributes attributes) {
		if (attributes.getTextures() == null) { return; }
		final List textures = GamaListFactory.create(Types.STRING);
		textures.addAll(attributes.getTextures());
		attributes.getTextures().clear();
		for (final Object s : textures) {
			GamaImageFile image = null;
			if (s instanceof GamaImageFile) {
				image = (GamaImageFile) s;
			} else if (s instanceof String) {
				image = (GamaImageFile) GamaFileType.createFile(scope, (String) s, null);
			}
			if (image == null || !image.exists(scope)) {
				throw new GamaRuntimeFileException(scope, "Texture file not found: " + s);
			} else {
				attributes.getTextures().add(image);
			}
		}
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

	private final List<Geometry> tempArrowList = new ArrayList<>();

	private Geometry addArrows(final IScope scope, final Geometry g1, final Boolean fill) {
		if (!hasArrows) { return g1; }
		final GamaPoint[] points = GeometryUtils.getPointsOf(g1);
		final int size = points.length;
		if (size < 2) { return g1; }
		tempArrowList.clear();
		tempArrowList.add(g1);
		Geometry end = null, begin = null;
		if (endArrow != null || constantEnd != null) {
			final double width = constantEnd == null ? Cast.asFloat(scope, endArrow.value(scope)) : constantEnd;
			if (width > 0) {
				end = GamaGeometryType.buildArrow(points[size - 2], points[size - 1], width, width + width / 3, fill)
						.getInnerGeometry();
				tempArrowList.add(end);
			}
		}
		if (beginArrow != null || constantBegin != null) {
			final double width = constantBegin == null ? Cast.asFloat(scope, beginArrow.value(scope)) : constantBegin;
			if (width > 0) {
				begin = GamaGeometryType.buildArrow(points[1], points[0], width, width + width / 3, fill)
						.getInnerGeometry();
				tempArrowList.add(begin);
			}
		}
		return GeometryUtils.GEOMETRY_FACTORY
				.createGeometryCollection(tempArrowList.toArray(new Geometry[tempArrowList.size()]));
	}
}