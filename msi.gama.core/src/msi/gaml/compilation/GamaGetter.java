package msi.gaml.compilation;

import msi.gama.runtime.IScope;

@FunctionalInterface
public interface GamaGetter<T> {

	T get(IScope scope, Object... arguments);

}
