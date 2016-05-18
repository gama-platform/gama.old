/**
 * Created by drogoul, 4 janv. 2016
 *
 */
package msi.gama.lang.gaml.generator;

import java.io.StringWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;

import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.precompiler.GamlProperties;

/**
 * Class GamlGenerator.
 *
 * @author drogoul
 * @since 4 janv. 2016
 *
 */
public class GamlGenerator implements IGenerator {

	/**
	 *
	 */
	public GamlGenerator() {
	}

	/**
	 * Method doGenerate()
	 * 
	 * @see org.eclipse.xtext.generator.IGenerator#doGenerate(org.eclipse.emf.ecore.resource.Resource,
	 *      org.eclipse.xtext.generator.IFileSystemAccess)
	 */
	@Override
	public void doGenerate(final Resource input, final IFileSystemAccess fsa) {
		final GamlResource resource = (GamlResource) input;
		final String fileName = getFilenameFor(resource);
		final String contents = getContentsFor(resource);
		fsa.generateFile(fileName, GamlOutputConfigurationProvider.META, contents);
	}

	/**
	 * @param input
	 * @return
	 */
	private String getContentsFor(final GamlResource input) {
		final GamlProperties requires = input.getRequires();
		final StringWriter sw = new StringWriter();

		requires.store(sw);

		return sw.toString();
	}

	/**
	 * @param input
	 * @return
	 */
	private String getFilenameFor(final GamlResource input) {
		IPath path = input.getPath();
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		path = file.getProjectRelativePath();
		final String s = path.toString();
		return s + ".meta";
	}

}
