package msi.gama.util.file;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.variableresolvers.PathVariableResolver;

import msi.gama.common.util.FileUtils;

public class CacheLocationProvider extends PathVariableResolver {

	public static String NAME = "CACHE_LOC";

	@Override
	public String[] getVariableNames(final String variable, final IResource resource) {
		return new String[] { NAME };
	}

	@Override
	public String getValue(final String variable, final IResource resource) {
		return resource.getWorkspace().getRoot().getFolder(FileUtils.CACHE_FOLDER_PATH).getLocationURI()
				.toASCIIString();

	}

}
