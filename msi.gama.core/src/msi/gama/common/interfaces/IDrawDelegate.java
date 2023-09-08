/*******************************************************************************************************
 *
 * IDrawDelegate.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.geom.Rectangle2D;

import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.draw.DrawingData;
import msi.gaml.types.IType;

/**
 * Class ICreateDelegate. Allows to create agents from other sources than the ones used in the tradition 'create'
 * statement
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public interface IDrawDelegate {

	/**
	 * Fills the list of maps with the initial values read from the source. Returns true if all the inits have been
	 * correctly filled
	 *
	 * @param scope
	 * @param inits
	 * @param max
	 *            can be null (in that case, the maximum number of agents to create is ignored)
	 * @param source
	 * @return
	 */

	Rectangle2D executeOn(IGraphicsScope agent, DrawingData data, IExpression... items) throws GamaRuntimeException;

	/**
	 * Returns the type expected in the default facet of the 'draw' statement. Should not be null and should be
	 * different from IType.NO_TYPE (in order to be able to check the validity of draw statements at compile time). The
	 * type should also be considered as 'drawable' (see {@link IType#isDrawable()}).
	 *
	 * @return a GAML type representing the type of the objects this draw delegate expects
	 */
	IType<?> typeDrawn();

}
