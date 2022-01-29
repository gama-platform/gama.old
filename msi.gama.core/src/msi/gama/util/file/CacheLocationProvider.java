/*******************************************************************************************************
 *
 * CacheLocationProvider.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.variableresolvers.PathVariableResolver;

import msi.gama.common.util.FileUtils;

/**
 * The Class CacheLocationProvider.
 */
public class CacheLocationProvider extends PathVariableResolver {

	/** The name. */
	public static String NAME = "CACHE_LOC";

	@Override
	public String[] getVariableNames(final String variable, final IResource resource) {
		return new String[] { NAME };
	}

	@Override
	public String getValue(final String variable, final IResource resource) {
		return FileUtils.CACHE.toURI().toASCIIString();
	}

}
