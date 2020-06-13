/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.GridLayerData.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.runtime.exceptions.GamaRuntimeException;

public class ImageLayerData extends LayerData {

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
