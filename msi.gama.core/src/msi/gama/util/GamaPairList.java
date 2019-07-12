package msi.gama.util;

import java.util.Map;

import msi.gama.util.IMap.IPairList;
import msi.gaml.types.Types;

public class GamaPairList<K, V> extends GamaList<Map.Entry<K, V>> implements IPairList<K, V> {

	GamaPairList(final IMap<K, V> map) {
		super(map.size(), Types.PAIR.of(map.getGamlType().getKeyType(), map.getGamlType().getContentType()));
	}

}