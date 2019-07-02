/*******************************************************************************************************
 *
 * msi.gaml.compilation.GamaGetter.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import msi.gama.runtime.IScope;

@FunctionalInterface
public interface GamaGetter<T> {

	T get(IScope scope, Object... arguments);

}
