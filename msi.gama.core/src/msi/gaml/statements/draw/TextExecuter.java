/*******************************************************************************************************
 *
 * TextExecuter.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * The Class TextExecuter.
 */
class TextExecuter extends DrawExecuter {

	/** The const text. */
	private final String constText;

	/**
	 * Instantiates a new text executer.
	 *
	 * @param item the item
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	TextExecuter(final IExpression item) throws GamaRuntimeException {
		super(item);
		constText = item.isConst() ? Cast.asString(null, item.getConstValue()) : null;
	}

	@Override
	Rectangle2D executeOn(final IGraphicsScope scope, final IGraphics g, final DrawingData data)
			throws GamaRuntimeException {
		final String info = constText == null ? Cast.asString(scope, item.value(scope)) : constText;
		if (info == null || info.length() == 0) return null;
		final TextDrawingAttributes attributes = computeAttributes(scope, data);
		return g.drawString(info, attributes);
	}

	/**
	 * Compute attributes.
	 *
	 * @param scope the scope
	 * @param data the data
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
}