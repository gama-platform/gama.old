/*******************************************************************************************************
 *
 * OverlayLayerData.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Color;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.types.Types;

/**
 * The Class OverlayLayerData.
 */
public class OverlayLayerData extends LayerData {

	/** The border. */
	final Attribute<GamaColor> border;
	
	/** The background. */
	final Attribute<GamaColor> background;
	
	/** The rounded. */
	final Attribute<Boolean> rounded;
	
	/** The computed. */
	boolean computed;

	/**
	 * Instantiates a new overlay layer data.
	 *
	 * @param def the def
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public OverlayLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		border = create(IKeyword.BORDER, Types.COLOR, null);
		background = create(IKeyword.BACKGROUND, Types.COLOR, new GamaColor(Color.black));
		rounded = create(IKeyword.ROUNDED, Types.BOOL, true);
	}

	/**
	 * Gets the background color.
	 *
	 * @param scope the scope
	 * @return the background color
	 */
	public Color getBackgroundColor(final IScope scope) {
		return new Color(background.get().getRed(), background.get().getGreen(), background.get().getBlue(),
				(int) ((1 - getTransparency(scope)) * 255));
	}

	@Override
	public void computePixelsDimensions(final IGraphics g) {
		if (computed) { return; }
		super.computePixelsDimensions(g);
		computed = true;
	}

	/**
	 * Gets the border color.
	 *
	 * @return the border color
	 */
	public Color getBorderColor() {
		return border.get();
	}

	/**
	 * Checks if is rounded.
	 *
	 * @return true, if is rounded
	 */
	public boolean isRounded() {
		return rounded.get();
	}

}
