/**
 * Created by drogoul, 28 janv. 2016
 *
 */
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

class TextExecuter extends DrawExecuter {

	private final String constText;

	TextExecuter(final IExpression item) throws GamaRuntimeException {
		super(item);
		constText = item.isConst() ? Cast.asString(null, item.value(null)) : null;
	}

	@Override
		Rectangle2D executeOn(final IScope scope, final IGraphics g, final DrawingData data)
			throws GamaRuntimeException {
		final String info = constText == null ? Cast.asString(scope, item.value(scope)) : constText;
		if ( info == null || info.length() == 0 ) { return null; }
		TextDrawingAttributes attributes = computeAttributes(scope, data);
		return g.drawString(info, attributes);
	}

	TextDrawingAttributes computeAttributes(final IScope scope, final DrawingData data) {
		TextDrawingAttributes attributes = new TextDrawingAttributes(data.currentSize, data.currentRotation,
			data.currentLocation, data.currentColor, data.currentFont, data.currentperspective);
		// We push the location of the agent if none has been provided
		attributes.setLocationIfAbsent(new GamaPoint(scope.getAgent().getLocation()));
		return attributes;
	}
}