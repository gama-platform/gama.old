/**
 * Created by drogoul, 28 janv. 2016
 *
 */
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;
import java.util.List;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

class ShapeExecuter extends DrawExecuter {

	final IExpression endArrow, beginArrow;
	final IShape constantShape;
	final Double constantEnd, constantBegin;
	final boolean hasArrows;

	ShapeExecuter(final IExpression item, final IExpression beginArrow, final IExpression endArrow)
		throws GamaRuntimeException {
		super(item);
		constantShape = item.isConst() ? Cast.asGeometry(null, item.value(null)) : null;
		hasArrows = beginArrow != null && endArrow != null;
		if ( beginArrow != null ) {
			if ( beginArrow.isConst() ) {
				constantBegin = Cast.asFloat(null, beginArrow.value(null));
				this.beginArrow = null;
			} else {
				constantBegin = null;
				this.beginArrow = beginArrow;
			}
		} else {
			this.beginArrow = null;
			constantBegin = null;
		}
		if ( endArrow != null ) {
			if ( endArrow.isConst() ) {
				constantEnd = Cast.asFloat(null, endArrow.value(null));
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
		Rectangle2D executeOn(final IScope scope, final IGraphics gr, final DrawingData data)
			throws GamaRuntimeException {
		IShape shape = constantShape == null ? Cast.asGeometry(scope, item.value(scope), false) : constantShape;
		if ( shape == null ) { return null; }
		ShapeDrawingAttributes attributes = computeAttributes(scope, data, shape);
		// If the graphics is 2D, we pre-translate and pre-rotate the geometry
		// otherwise we just pre-translate it (the rotation in 3D will be handled separately
		// once the complete shapes are built)
		GamaPair<Double, GamaPoint> rot = attributes.rotation;
		if ( gr.is2D() ) {
			Double rotation = rot == null ? null : rot.key;
			GamaPoint axis = rot == null ? null : rot.value;
			shape = new GamaShape(shape, null, rotation, axis, attributes.location);
		} else {
			shape = new GamaShape(shape, null, null, attributes.location);
		}
		// We add the arrows if any
		shape = addArrows(scope, shape, !attributes.empty);
		// As well as the parts of the shape that can belong to a toroidal representation
		shape = addToroidalParts(scope, shape);

		// XXX EXPERIMENTAL See Issue #1521
		if ( GamaPreferences.DISPLAY_ONLY_VISIBLE.getValue() ) {
			Envelope e = shape.getEnvelope();
			Envelope visible = gr.getVisibleRegion();
			if ( !visible.intersects(e) ) { return null; }
			// XXX EXPERIMENTAL
		}

		// The textures are computed as well in advance
		addTextures(scope, attributes);
		// And we ask the IGraphics object to draw the shape
		return gr.drawShape(shape, attributes);
	}

	ShapeDrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final IShape shape) {
		ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(data.currentSize, data.currentDepth,
			data.currentRotation, data.currentLocation, data.currentEmpty, data.currentColor, data.currentBorder,
			data.currentTextures, scope.getAgentScope(), shape.getGeometricalType());
		// We push the depth of the geometry if none have been specified already
		attributes.setDepthIfAbsent((Double) shape.getAttribute(IShape.DEPTH_ATTRIBUTE));
		// We push the (perhaps new) location of the shape to the attributes. Can be necessary as
		// the attributes can have a wrong location
		attributes.setLocationIfAbsent(new GamaPoint(shape.getLocation()));
		return attributes;
	}

	/**
	 * @param scope
	 * @param attributes
	 */
	private void addTextures(final IScope scope, final ShapeDrawingAttributes attributes) {
		if ( attributes.textures == null ) { return; }
		IList<String> textureNames = GamaListFactory.create(Types.STRING);
		textureNames.addAll(attributes.textures);
		attributes.textures.clear();
		for ( String s : textureNames ) {
			GamaImageFile image;
			image = new GamaImageFile(scope, s);
			attributes.textures.add(image);
		}
	}

	/**
	 * @param scope
	 * @param shape
	 * @return
	 */
	private IShape addToroidalParts(final IScope scope, final IShape shape) {
		IShape result = shape;
		ITopology t = scope.getTopology();
		if ( t != null && t.isTorus() ) {
			List<Geometry> geoms = t.listToroidalGeometries(shape.getInnerGeometry());
			Geometry all = GeometryUtils.FACTORY.buildGeometry(geoms);
			Geometry world = scope.getSimulationScope().getInnerGeometry();
			result = new GamaShape(all.intersection(world));
			// WARNING Does not correctly handle rotations or translations
		}
		return result;
	}

	private IShape addArrows(final IScope scope, final IShape g1, final Boolean fill) {
		if ( !hasArrows ) { return g1; }
		IList<? extends ILocation> points = g1.getPoints();
		int size = points.size();
		if ( size < 2 ) { return g1; }
		IShape end = null, begin = null;
		if ( endArrow != null || constantEnd != null ) {
			double width = constantEnd == null ? Cast.asFloat(scope, endArrow.value(scope)) : constantEnd;
			if ( width > 0 ) {
				end = GamaGeometryType.buildArrow(new GamaPoint(points.get(size - 2)),
					new GamaPoint(points.get(size - 1)), width, width + width / 3, fill);
			}
		}
		if ( beginArrow != null || constantBegin != null ) {
			double width = constantBegin == null ? Cast.asFloat(scope, beginArrow.value(scope)) : constantBegin;
			if ( width > 0 ) {
				begin = GamaGeometryType.buildArrow(new GamaPoint(points.get(1)), new GamaPoint(points.get(0)), width,
					width + width / 3, fill);
			}
		}
		return GamaGeometryType.buildMultiGeometry(g1, begin, end);
	}
}