package simtools.gaml.extensions.traffic.carfollowing;

import java.util.Comparator;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;

public class CustomDualTreeBidiMap<K, V> extends DualTreeBidiMap<K, V> {
	protected CustomDualTreeBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
    }

    public CustomDualTreeBidiMap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator) {
        super(keyComparator, valueComparator);
    }

    protected CustomDualTreeBidiMap<V, K> createBidiMap(Map<V, K> normalMap, Map<K, V> reverseMap, BidiMap<K, V> inverseMap) {
        return new CustomDualTreeBidiMap(normalMap, reverseMap, inverseMap);
    }

    /**
     * The original method in DualTreeBidiMap does not work correctly when
     * the key is not present in the map.
     */
    @Override
    public K nextKey(K key) {
        if (containsKey(key) || size() == 0) {
            return super.nextKey(key);
        } else {
            K last = lastKey();
            if (comparator().compare(key, last) > 0) {
                return null;
            } else {
                K next = super.nextKey(key);
                return next == null ? last : previousKey(next);
            }
        }
    }
}
