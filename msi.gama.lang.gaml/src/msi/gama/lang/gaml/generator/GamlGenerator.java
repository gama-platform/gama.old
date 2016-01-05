/**
 * Created by drogoul, 4 janv. 2016
 *
 */
package msi.gama.lang.gaml.generator;

import java.util.Set;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.*;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gaml.operators.Strings;

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
	public GamlGenerator() {}

	/**
	 * Method doGenerate()
	 * @see org.eclipse.xtext.generator.IGenerator#doGenerate(org.eclipse.emf.ecore.resource.Resource, org.eclipse.xtext.generator.IFileSystemAccess)
	 */
	@Override
	public void doGenerate(final Resource input, final IFileSystemAccess fsa) {
		GamlResource resource = (GamlResource) input;
		String fileName = getFilenameFor(resource);
		String contents = getContentsFor(resource);
		fsa.generateFile(fileName, GamlOutputConfigurationProvider.META, contents);
	}

	/**
	 * @param input
	 * @return
	 */
	private String getContentsFor(final GamlResource input) {
		Set<String> requires = input.getRequires();
		StringBuilder sb = new StringBuilder(requires.size() * 30);
		for ( String s : requires ) {
			sb.append(s).append(Strings.LN);
		}
		return sb.toString();
	}

	/**
	 * @param input
	 * @return
	 */
	private String getFilenameFor(final GamlResource input) {
		IPath path = input.getPath();
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		path = file.getProjectRelativePath();
		String s = path.toString();
		return s + ".meta";
	}

}
