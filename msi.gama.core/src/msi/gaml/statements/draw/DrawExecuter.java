/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.DrawExecuter.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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