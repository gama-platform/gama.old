/*******************************************************************************************************
 *
 * OverlayLayerData.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.Types;

/**
 * The Class OverlayLayerData.
 */
public class OverlayLayerData extends FramedLayerData {

	/** The rounded. */
	final Attribute<Boolean> rounded;

	/** The computed. */
	boolean computed;

	/**
	 * Instantiates a new overlay layer data.
	 *
	 * @param def
	 *            the def
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public OverlayLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		rounded = create(IKeyword.ROUNDED, Types.BOOL, true);
	}

	@Override
	public void computePixelsDimensions(final IGraphics g) {
		if (computed) return;
		super.computePixelsDimensions(g);
		computed = true;
	}

	/**
	 * Checks if is rounded.
	 *
	 * @return true, if is rounded
	 */
	public boolean isRounded() { return rounded.get(); }

}
