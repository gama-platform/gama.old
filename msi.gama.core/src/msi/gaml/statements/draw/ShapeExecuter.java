/**
 * Created by drogoul, 28 janv. 2016
 *
 */
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import com.vividsolutions.jts.geom.Geometry;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.util.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;
import msi.gaml.types.*;

class ShapeExecuter extends DrawExecuter {

	/**
	 *
	 */
	final IExpression endArrow, beginArrow;

	ShapeExecuter(final IExpression beginArrow, final IExpression endArrow) throws GamaRuntimeException {
		this.endArrow = endArrow;
		this.beginArrow = beginArrow;
	}

	@Override
		Rectangle2D executeOn(final IScope scope, final IExpression item, final IGraphics gr,
			final DrawingAttributes attributes) throws GamaRuntimeException {
		IShape shape = Cast.asGeometry(scope, item.value(scope), false);
		if ( shape == null ) { return null; }
		// We push the type of the geometry to the attributes
		attributes.setShapeType(shape.getGeometricalType());
		// We push the depth of the geometry if none have been specified already
		attributes.setDepthIfAbsent((Double) shape.getAttribute(IShape.DEPTH_ATTRIBUTE));
		// We push the (perhaps new) location of the shape to the attributes. Can be necessary as
		// the attributes can have a wrong location
		attributes.setLocationIfAbsent(new GamaPoint(shape.getLocation()));
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
		// The textures are computed as well in advance
		addTextures(scope, attributes);
		// And we ask the IGraphics object to draw the shape
		return gr.drawShape(shape, attributes);
	}

	/**
	 * @param scope
	 * @param attributes
	 */
	private void addTextures(final IScope scope, final DrawingAttributes attributes) {
		if ( attributes.textures == null ) { return; }
		IList<String> textureNames = GamaListFactory.create(Types.STRING);
		textureNames.addAll(attributes.textures);
		attributes.textures.clear();
		for ( String s : textureNames ) {
			BufferedImage image;
			try {
				image = ImageUtils.getInstance().getImageFromFile(scope, s);
				attributes.textures.add(image);
			} catch (IOException e) {
				e.printStackTrace();
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
		IShape end = null, begin = null;
		if ( endArrow != null ) {
			IList<? extends ILocation> points = g1.getPoints();
			int size = points.size();
			if ( size < 2 ) { return g1; }
			double width = Cast.asFloat(scope, endArrow.value(scope));
			end = GamaGeometryType.buildArrow(new GamaPoint(points.get(size - 2)), new GamaPoint(points.get(size - 1)),
				width, width + width / 3, fill);
		}
		if ( beginArrow != null ) {
			IList<? extends ILocation> points = g1.getPoints();
			int size = points.size();
			if ( size < 2 ) { return g1; }
			double width = Cast.asFloat(scope, beginArrow.value(scope));
			begin = GamaGeometryType.buildArrow(new GamaPoint(points.get(1)), new GamaPoint(points.get(0)), width,
				width + width / 3, fill);
		}
		return GamaGeometryType.buildMultiGeometry(g1, begin, end);
	}
}