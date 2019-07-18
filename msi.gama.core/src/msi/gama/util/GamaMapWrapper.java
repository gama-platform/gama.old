package msi.gama.util;

import java.util.Map;

import com.google.common.collect.ForwardingMap;

import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;

@SuppressWarnings ("unchecked")
public class GamaMapWrapper<K, V> extends ForwardingMap<K, V> implements IMap<K, V> {

	final Map<K, V> wrapped;
	final IType keyType, contentsType;
	boolean ordered;

	GamaMapWrapper(final Map<K, V> wrapped, final IType key, final IType contents, final boolean isOrdered) {
		this.wrapped = wrapped;
		this.contentsType = contents;
		this.keyType = key;
		this.ordered = isOrdered;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) { return true; }
		if (!(o instanceof IMap)) { return false; }
		return GamaMapFactory.equals(this, (IMap) o);
	}

	@Override
	protected Map<K, V> delegate() {
		return wrapped;
	}

	@Override
	public IContainerType<?> getGamlType() {
		return msi.gaml.types.Types.MAP.of(keyType, contentsType);
	}

	@Override
	public boolean isOrdered() {
		return ordered;
	}

}
