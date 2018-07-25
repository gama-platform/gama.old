/*********************************************************************************************
 *
 * 'ShapeExecuter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
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
		IShape shape = constantShape == null ? Cast.asGeometry(scope, item.value(scope), false) : constantShape;
		if (shape == null) { return null; }
		final Geometry geom = shape.getInnerGeometry();
		final ShapeDrawingAttributes attributes = computeAttributes(scope, data, shape);
		// If the graphics is 2D, we pre-translate and pre-rotate the geometry
		// otherwise we just pre-translate it (the rotation in 3D will be
		// handled separately once the complete shapes are built)

		if (gr.is2D()) {
			final AxisAngle rotation = attributes.getRotation();
			shape = new GamaShape(shape, null, rotation, attributes.getLocation());
		} else {
			shape = new GamaShape(shape, null, null, attributes.getLocation());
		}
		// We add the arrows if any
		shape = addArrows(scope, shape, !attributes.isEmpty());
		// As well as the parts of the shape that can belong to a toroidal
		// representation
		shape = addToroidalParts(scope, shape);
		// In case the shape has been changed
		attributes.type = shape.getGeometricalType();

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
		return gr.drawShape(shape.getInnerGeometry(), attributes);
	}

	ShapeDrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final IShape shape) {
		final ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(Scaling3D.of(data.size.value),
				data.depth.value, data.rotation.value, data.location.value, data.empty.value, data.getCurrentColor(),
				data.getColors(), data.border.value, data.texture.value, data.material.value, scope.getAgent(),
				shape.getGeometricalType(), data.lineWidth.value);
		// We push the depth of the geometry if none have been specified already
		attributes.setHeightIfAbsent((Double) shape.getAttribute(IShape.DEPTH_ATTRIBUTE));
		// We push the (perhaps new) location of the shape to the attributes.
		// Can be necessary as
		// the attributes can have a wrong location
		attributes.setLocationIfAbsent(shape.getLocation().toGamaPoint());
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
	private IShape addToroidalParts(final IScope scope, final IShape shape) {
		IShape result = shape;
		final ITopology t = scope.getTopology();
		if (t != null && t.isTorus()) {
			final List<Geometry> geoms = t.listToroidalGeometries(shape.getInnerGeometry());
			final Geometry all = GeometryUtils.GEOMETRY_FACTORY.buildGeometry(geoms);
			final Geometry world = scope.getSimulation().getInnerGeometry();
			result = new GamaShape(all.intersection(world));
			// WARNING Does not correctly handle rotations or translations
		}
		return result;
	}

	private Geometry addArrows(final IScope scope, final Geometry g1, final Boolean fill) {
		if (!hasArrows) { return g1; }
		final ICoordinates points = GeometryUtils.getContourCoordinates(g1);
		final int size = points.size();
		if (size < 2) { return g1; }
		Geometry end = null, begin = null;
		if (endArrow != null || constantEnd != null) {
			final double width = constantEnd == null ? Cast.asFloat(scope, endArrow.value(scope)) : constantEnd;
			if (width > 0) {
				end = GamaGeometryType
						.buildArrow(points.at(size - 2), points.at(size - 1), width, width + width / 3, fill)
						.getInnerGeometry();
			}
		}
		if (beginArrow != null || constantBegin != null) {
			final double width = constantBegin == null ? Cast.asFloat(scope, beginArrow.value(scope)) : constantBegin;
			if (width > 0) {
				begin = GamaGeometryType.buildArrow(points.at(1), points.at(0), width, width + width / 3, fill)
						.getInnerGeometry();
			}
		}
		return GeometryUtils.GEOMETRY_FACTORY.createGeometryCollection(new Geometry[] { g1, begin, end });
	}

	private IShape addArrows(final IScope scope, final IShape g1, final Boolean fill) {
		if (!hasArrows) { return g1; }
		final IList<? extends ILocation> points = g1.getPoints();
		final int size = points.size();
		if (size < 2) { return g1; }
		IShape end = null, begin = null;
		if (endArrow != null || constantEnd != null) {
			final double width = constantEnd == null ? Cast.asFloat(scope, endArrow.value(scope)) : constantEnd;
			if (width > 0) {
				end = GamaGeometryType.buildArrow(new GamaPoint(points.get(size - 2)),
						new GamaPoint(points.get(size - 1)), width, width + width / 3, fill);
			}
		}
		if (beginArrow != null || constantBegin != null) {
			final double width = constantBegin == null ? Cast.asFloat(scope, beginArrow.value(scope)) : constantBegin;
			if (width > 0) {
				begin = GamaGeometryType.buildArrow(new GamaPoint(points.get(1)), new GamaPoint(points.get(0)), width,
						width + width / 3, fill);
			}
		}
		return GamaGeometryType.buildMultiGeometry(g1, begin, end);
	}
}