package msi.gama.util;

import java.util.Map;

import com.google.common.collect.ForwardingMap;

import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;

@SuppressWarnings ("unchecked")
public class GamaMapWrapper<K, V> extends ForwardingMap<K, V> implements IMap<K, V> {

	final Map<K, V> wrapped;
	final IType keyType, contentsType;

	GamaMapWrapper(final Map<K, V> wrapped, final IType key, final IType contents) {
		this.wrapped = wrapped;
		this.contentsType = contents;
		this.keyType = key;
	}

	@Override
	protected Map<K, V> delegate() {
		return wrapped;
	}

	@Override
	public IContainerType<?> getGamlType() {
		return msi.gaml.types.Types.MAP.of(keyType, contentsType);
	}

}
