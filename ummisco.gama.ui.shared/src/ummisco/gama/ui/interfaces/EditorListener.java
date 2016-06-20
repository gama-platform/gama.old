/*********************************************************************************************
 * 
 *
 * 'EditorListener.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.ui.interfaces;

import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 27 mai 2011
 * 
 * @todo Description
 * 
 */
public interface EditorListener<T> {

	void valueModified(T newValue) throws GamaRuntimeException;

}
