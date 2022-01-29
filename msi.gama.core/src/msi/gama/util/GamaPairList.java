/*******************************************************************************************************
 *
 * GamaPairList.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util;

import java.util.Map;

import msi.gama.util.IMap.IPairList;
import msi.gaml.types.Types;

/**
 * The Class GamaPairList.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GamaPairList<K, V> extends GamaList<Map.Entry<K, V>> implements IPairList<K, V> {

	/**
	 * Instantiates a new gama pair list.
	 *
	 * @param map the map
	 */
	GamaPairList(final IMap<K, V> map) {
		super(map.size(), Types.PAIR.of(map.getGamlType().getKeyType(), map.getGamlType().getContentType()));
	}

}