/*********************************************************************************************
 *
 * 'DrawExecuter.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;

abstract class DrawExecuter {

	final IExpression item;

	DrawExecuter(final IExpression item) {
		this.item = item.isConst() ? null : item;
	}

	abstract Rectangle2D executeOn(IScope agent, IGraphics g, DrawingData data) throws GamaRuntimeException;

}