/*******************************************************************************************************
 *
 * DrawExecuter.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;

/**
 * The Class DrawExecuter.
 */
abstract class DrawExecuter {

	/** The item. */
	final IExpression item;

	/**
	 * Instantiates a new draw executer.
	 *
	 * @param item the item
	 */
	DrawExecuter(final IExpression item) {
		this.item = item.isConst() ? null : item;
	}

	/**
	 * Execute on.
	 *
	 * @param agent the agent
	 * @param g the g
	 * @param data the data
	 * @return the rectangle 2 D
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	abstract Rectangle2D executeOn(IGraphicsScope agent, IGraphics g, DrawingData data) throws GamaRuntimeException;

}