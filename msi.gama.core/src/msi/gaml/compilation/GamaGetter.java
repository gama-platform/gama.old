/*******************************************************************************************************
 *
 * msi.gaml.compilation.GamaGetter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import msi.gama.runtime.IScope;

public interface GamaGetter<T> {
	@FunctionalInterface
	public interface Unary<T> extends GamaGetter<T> {
		T get(IScope scope, Object argument);
	}

	@FunctionalInterface
	public interface Binary<T> extends GamaGetter<T> {
		T get(IScope scope, Object argument1, Object argument2);
	}

	@FunctionalInterface
	public interface NAry<T> extends GamaGetter<T> {
		T get(IScope scope, Object... arguments);
	}

}
