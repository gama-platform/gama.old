/*********************************************************************************************
 *
 * 'GamaPairReducer.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.util.GamaPair;
import msi.gaml.types.IType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GamaPairReducer {
	private final IType keyPairType;
	private final IType dataPairType;
	private final Object key;
	private final Object value;

	public GamaPairReducer(final GamaPair m) {
		keyPairType = m.getType().getKeyType();
		dataPairType = m.getType().getContentType();
		key = m.getKey();
		value = m.getValue();
	}

	public GamaPair constructObject() {
		return new GamaPair(key, value, keyPairType, dataPairType);
	}
}
