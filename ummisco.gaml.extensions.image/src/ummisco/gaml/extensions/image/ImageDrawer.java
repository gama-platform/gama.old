/*******************************************************************************************************
 *
 * ImageDrawer.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.geom.Rectangle2D;

import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.draw.DrawingData;
import msi.gaml.statements.draw.AssetDrawer;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class ImageDrawer.
 */
public class ImageDrawer extends AssetDrawer {

	/**
	 * Execute on.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @param items
	 *            the items
	 * @return the rectangle 2 D
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public Rectangle2D executeOn(final IGraphicsScope scope, final DrawingData data, final IExpression... items)
			throws GamaRuntimeException {
		return super.executeOn(scope, data, items);
	}

	/**
	 * Type drawn.
	 *
	 * @return the i type
	 */
	@Override
	public IType<?> typeDrawn() {
		return Types.get(GamaImageType.ID);
	}

}
