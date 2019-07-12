package msi.gama.util;

import java.util.List;

import com.google.common.collect.ForwardingList;

import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class GamaListWrapper<E> extends ForwardingList<E> implements IList<E> {

	final List<E> wrapped;
	final IContainerType type;

	GamaListWrapper(final List<E> wrapped, final IType contents) {
		this.type = Types.LIST.of(contents);
		this.wrapped = wrapped;
	}

	@Override
	public IContainerType<?> getGamlType() {
		return type;
	}

	@Override
	protected List<E> delegate() {
		return wrapped;
	}

}
