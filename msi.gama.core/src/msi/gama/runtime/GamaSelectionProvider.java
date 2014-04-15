/*********************************************************************************************
 * 
 *
 * 'GamaSelectionProvider.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.runtime;



/**
 * Written by drogoul Modified on 15 avr. 2010
 * 
 * @todo Description
 * 
 */
public interface GamaSelectionProvider {

	public void addGamaSelectionListener(GamaSelectionListener listener);

	public void removeGamaSelectionListener(GamaSelectionListener listener);

	public void fireSelectionChanged(Object entity);

}
