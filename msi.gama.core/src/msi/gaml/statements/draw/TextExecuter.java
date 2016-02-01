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
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

class TextExecuter extends DrawExecuter {

	private final String constText;

	TextExecuter(final IExpression item) throws GamaRuntimeException {
		constText = item.isConst() ? Cast.asString(null, item.value(null)) : null;
	}

	@Override
		Rectangle2D executeOn(final IScope scope, final IExpression item, final IGraphics g,
			final DrawingAttributes attributes) throws GamaRuntimeException {
		// We push the location of the agent if none has been provided
		attributes.setLocationIfAbsent(new GamaPoint(scope.getAgentScope().getLocation()));
		final String info = constText == null ? Cast.asString(scope, item.value(scope)) : constText;
		if ( info == null || info.length() == 0 ) { return null; }
		return g.drawString(info, attributes);
	}
}