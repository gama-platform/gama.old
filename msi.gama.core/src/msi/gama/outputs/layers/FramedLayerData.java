/*******************************************************************************************************
 *
 * OverlayLayerData.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Color;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.types.Types;

/**
 * The Class OverlayLayerData.
 */
public class FramedLayerData extends LayerData {

	/** The border. */
	final Attribute<GamaColor> border;

	/** The background. */
	final Attribute<GamaColor> background;

	/**
	 * Instantiates a new overlay layer data.
	 *
	 * @param def
	 *            the def
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public FramedLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		border = create(IKeyword.BORDER, Types.COLOR, null);
		background = create(IKeyword.BACKGROUND, Types.COLOR,
				def instanceof OverlayStatement ? new GamaColor(Color.black) : null);
	}

	/**
	 * Gets the background color.
	 *
	 * @param scope
	 *            the scope
	 * @return the background color
	 */
	public Color getBackgroundColor(final IScope scope) {
		Color c = background.get();
		return c == null ? null
				: new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) ((1 - getTransparency(scope)) * 255));
	}

	/**
	 * Gets the border color.
	 *
	 * @return the border color
	 */
	public Color getBorderColor() { return border.get(); }

}
