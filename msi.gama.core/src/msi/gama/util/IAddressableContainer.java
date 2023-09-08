/*******************************************************************************************************
 *
 * IAddressableContainer.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util;

/**
 * Class IAddressableContainer.
 * 
 * @author drogoul
 * @since 24 janv. 2014
 * 
 */
public interface IAddressableContainer<K, V, AK, AV> extends IContainer<K, V>, IContainer.Addressable<AK, AV> {}
