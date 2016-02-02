/**
 * Created by drogoul, 28 janv. 2016
 *
 */
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

abstract class DrawExecuter {

	final IExpression item;

	DrawExecuter(final IExpression item) {
		this.item = item.isConst() ? null : item;
	}

	abstract Rectangle2D executeOn(IScope agent, IGraphics g, DrawingAttributes attributes) throws GamaRuntimeException;

}