/*********************************************************************************************
 * 
 *
 * 'GamlStandaloneSetup.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package msi.gama.lang.gaml;

/**
 * Initialization support for running Xtext languages without equinox extension
 * registry
 */
public class GamlStandaloneSetup extends GamlStandaloneSetupGenerated {

	public static void doSetup() {
		new GamlStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}
