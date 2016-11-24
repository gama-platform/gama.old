/*********************************************************************************************
 *
 * 'Main.java, in plugin msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.generator;

import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.generator.*;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.*;
import com.google.inject.*;

public class Main {

	public static void main(final String[] args) {
		if ( args.length == 0 ) {
			System.err.println("Aborting: no path to EMF resource provided!");
			return;
		}
		Injector injector = new msi.gama.lang.gaml.GamlStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
		Main main = injector.getInstance(Main.class);
		main.runGenerator(args[0]);
	}

	@Inject
	private Provider<ResourceSet> resourceSetProvider;

	@Inject
	private IResourceValidator validator;

	@Inject
	private IGenerator generator;

	@Inject
	private JavaIoFileSystemAccess fileAccess;

	protected void runGenerator(final String string) {
		// load the resource
		ResourceSet set = resourceSetProvider.get();
		Resource resource = set.getResource(URI.createURI(string, false), true);

		// validate the resource
		List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		if ( !list.isEmpty() ) {
			for ( Issue issue : list ) {
				System.err.println(issue);
			}
			return;
		}

		// configure and start the generator
		fileAccess.setOutputPath("src-gen/");
		generator.doGenerate(resource, fileAccess);

		System.out.println("Code generation finished.");
	}
}
