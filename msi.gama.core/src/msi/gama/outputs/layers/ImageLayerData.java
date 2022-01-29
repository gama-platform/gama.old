/*******************************************************************************************************
 *
 * ImageLayerData.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class ImageLayerData.
 */
public class ImageLayerData extends LayerData {

	/**
	 * Instantiates a new image layer data.
	 *
	 * @param def the def
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@SuppressWarnings ("unchecked")
	public ImageLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
	}

	/**
	 * Own version of refresh. See #2927
	 */
	@Override
	public Boolean getRefresh() {
		final Boolean result = super.getRefresh();
		if (result == null) { return false; }
		return result;
	}

}
