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
import msi.gama.util.file.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

class FileExecuter extends DrawExecuter {

	private final GamaImageFile constImg;

	FileExecuter(final IExpression item) throws GamaRuntimeException {
		super(item);
		constImg = (GamaImageFile) (item.isConst() ? Cast.as(item, IGamaFile.class, false) : null);
	}

	@Override
		Rectangle2D executeOn(final IScope scope, final IGraphics g, final DrawingAttributes attributes)
			throws GamaRuntimeException {

		// We push the location of the agent if none has been provided
		attributes.setLocationIfAbsent(new GamaPoint(scope.getAgentScope().getLocation()));
		//
		final GamaFile file = constImg == null ? (GamaFile) item.value(scope) : constImg;
		if ( file instanceof GamaImageFile ) {
			// // No grid line
			attributes.border = null;
			if ( attributes.size != null ) {
				final GamaPoint location = attributes.location;
				final double displayWidth = attributes.size.x;
				final double displayHeight = attributes.size.y;
				final double x = location.x - displayWidth / 2;
				final double y = location.y - displayHeight / 2;
				// New location
				attributes.location = new GamaPoint(x, y, location.z);
			}
		}
		return g.drawFile(file, attributes);

	}
}