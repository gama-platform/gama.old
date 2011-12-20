/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

package msi.gama.lang.gaml;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.ISetup;
import com.google.inject.*;

/**
 * Generated from StandaloneSetup.xpt!
 */
@SuppressWarnings("all")
public class GamlStandaloneSetupGenerated implements ISetup {

	public Injector createInjectorAndDoEMFRegistration() {
		// register default ePackages
		if ( !Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("ecore") ) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore",
				new org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl());
		}
		if ( !Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("xmi") ) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi",
				new org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl());
		}
		if ( !EPackage.Registry.INSTANCE.containsKey(org.eclipse.xtext.XtextPackage.eNS_URI) ) {
			EPackage.Registry.INSTANCE.put(org.eclipse.xtext.XtextPackage.eNS_URI,
				org.eclipse.xtext.XtextPackage.eINSTANCE);
		}

		Injector injector = createInjector();
		register(injector);
		return injector;
	}

	public Injector createInjector() {
		return Guice.createInjector(new msi.gama.lang.gaml.GamlRuntimeModule());
	}

	public void register(final Injector injector) {
		if ( !EPackage.Registry.INSTANCE.containsKey("http://www.gama.msi/lang/gaml/Gaml") ) {
			EPackage.Registry.INSTANCE.put("http://www.gama.msi/lang/gaml/Gaml",
				msi.gama.lang.gaml.gaml.GamlPackage.eINSTANCE);
		}

		org.eclipse.xtext.resource.IResourceFactory resourceFactory =
			injector.getInstance(org.eclipse.xtext.resource.IResourceFactory.class);
		org.eclipse.xtext.resource.IResourceServiceProvider serviceProvider =
			injector.getInstance(org.eclipse.xtext.resource.IResourceServiceProvider.class);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("gaml", resourceFactory);
		org.eclipse.xtext.resource.IResourceServiceProvider.Registry.INSTANCE
			.getExtensionToFactoryMap().put("gaml", serviceProvider);

	}
}
