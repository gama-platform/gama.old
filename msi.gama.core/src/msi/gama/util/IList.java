/*******************************************************************************************************
 *
 * msi.gama.util.IList.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util;

import java.util.List;

import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IType;

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
	public IContainer<Integer, E> reverse(final IScope scope);

	@Override
	public abstract IMatrix<E> matrixValue(IScope scope, IType<?> contentType, ILocation size, boolean copy);

	@Override
	public abstract IMatrix<E> matrixValue(IScope scope, IType<?> contentType, boolean copy);

}
