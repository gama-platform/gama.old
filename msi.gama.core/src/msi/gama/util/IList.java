/*********************************************************************************************
 * 
 * 
 * 'IList.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util;

import java.util.List;

import msi.gama.runtime.IScope;

/**
 * The class IList.
 * 
 * @author drogoul
 * @since 14 d�c. 2011
 * 
 */
public interface IList<E>
		extends IModifiableContainer<Integer, E, Integer, E>, IAddressableContainer<Integer, E, Integer, E>, List<E> {

	@Override
	public IContainer reverse(final IScope scope);

}
