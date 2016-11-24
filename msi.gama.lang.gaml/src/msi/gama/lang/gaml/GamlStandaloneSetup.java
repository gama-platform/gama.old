/*********************************************************************************************
 *
 * 'GamlStandaloneSetup.java, in plugin msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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
