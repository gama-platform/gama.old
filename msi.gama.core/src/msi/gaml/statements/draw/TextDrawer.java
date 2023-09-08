/*******************************************************************************************************
 *
 * TextDrawer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IDrawDelegate;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class TextExecuter.
 */
public class TextDrawer implements IDrawDelegate {

	/**
	 * Execute on.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @param data
	 *            the data
	 * @return the rectangle 2 D
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public Rectangle2D executeOn(final IGraphicsScope scope, final DrawingData data, final IExpression... items)
			throws GamaRuntimeException {
		final String info = Cast.asString(scope, items[0].value(scope));
		if (info == null || info.length() == 0) return null;
		final TextDrawingAttributes attributes = computeAttributes(scope, data);
		return scope.getGraphics().drawString(info, attributes);
	}

	/**
	 * Compute attributes.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the text drawing attributes
	 */
	TextDrawingAttributes computeAttributes(final IScope scope, final DrawingData data) {
		final TextDrawingAttributes attributes = new TextDrawingAttributes(Scaling3D.of(data.size.get()),
				data.rotation.get(), data.getLocation(), data.color.get());
		// We push the location of the agent if none has been provided
		if (attributes.getLocation() == null) { attributes.setLocation(scope.getAgent().getLocation().clone()); }
		attributes.setFont(data.font.get());
		attributes.setAnchor(data.getAnchor());
		attributes.setBorder(data.border.get());
		attributes.setEmpty(data.empty.get());
		attributes.setHeight(data.depth.get());
		attributes.setPerspective(data.perspective.get());
		attributes.setTextures(data.texture.get());
		attributes.setLineWidth(data.lineWidth.get());
		attributes.setPrecision(data.precision.get());
		return attributes;
	}

	@Override
	public IType<?> typeDrawn() {
		return Types.STRING;
	}
}