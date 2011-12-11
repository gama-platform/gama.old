
package msi.gama.lang.gaml;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class GamlStandaloneSetup extends GamlStandaloneSetupGenerated{

	public static void doSetup() {
		new GamlStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

