/*******************************************************************************************************
 *
 * Main.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.generator;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class Main.
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		if (args.length == 0) {
			System.err.println("Aborting: no path to EMF resource provided!");
			return;
		}
		final Injector injector =
				new msi.gama.lang.gaml.GamlStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
		final Main main = injector.getInstance(Main.class);
		main.runGenerator(args[0]);
	}

	/** The resource set provider. */
	@Inject private Provider<ResourceSet> resourceSetProvider;

	/** The validator. */
	@Inject private IResourceValidator validator;

	/** The generator. */
	@Inject private IGenerator generator;

	/** The file access. */
	@Inject private JavaIoFileSystemAccess fileAccess;

	/**
	 * Run generator.
	 *
	 * @param string the string
	 */
	protected void runGenerator(final String string) {
		// load the resource
		final ResourceSet set = resourceSetProvider.get();
		final Resource resource = set.getResource(URI.createURI(string, false), true);

		// validate the resource
		final List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		if (!list.isEmpty()) {
			for (final Issue issue : list) {
				DEBUG.ERR(issue.toString());
			}
			return;
		}

		// configure and start the generator
		fileAccess.setOutputPath("src-gen/");
		generator.doGenerate(resource, fileAccess);

		DEBUG.LOG("Code generation finished.");
	}
}
