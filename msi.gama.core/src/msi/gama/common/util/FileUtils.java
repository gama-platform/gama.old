/*********************************************************************************************
 *
 *
 * 'FileUtils.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.util;

import java.io.*;
import java.net.URLDecoder;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;

/**
 * The class FileUtils.
 *
 * @author drogoul
 * @since 20 dec. 2011
 *
 */
public class FileUtils {

	/**
	 * Checks if is absolute path.
	 *
	 * @param filePath the file path
	 *
	 * @return true, if is absolute path
	 */
	private static boolean isAbsolutePath(final String filePath) {
		final File[] roots = File.listRoots();
		for ( int i = 0; i < roots.length; i++ ) {
			if ( filePath.startsWith(roots[i].getAbsolutePath()) ) { return true; }
		}
		return false;
	}

	/**
	 * Removes a root.
	 *
	 * @param absoluteFilePath the absolute file path
	 *
	 * @return the string
	 */
	private static String removeRoot(final String absoluteFilePath) {
		// OutputManager.debug("absoluteFilePath before = " + absoluteFilePath);

		final File[] roots = File.listRoots();
		for ( int i = 0; i < roots.length; i++ ) {
			if ( absoluteFilePath.startsWith(roots[i].getAbsolutePath()) ) { return absoluteFilePath
				.substring(roots[i].getAbsolutePath().length(), absoluteFilePath.length()); }
		}
		return absoluteFilePath;
	}

	/**
	 * Construct an absolute file path.
	 *
	 * @param scope the scope
	 * @param fp the fp
	 * @param mustExist the must exist
	 * @return the string
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	static public String constructAbsoluteFilePath(final IScope scope, final String fp, final boolean mustExist)
		throws GamaRuntimeException {
		String filePath = null;
		String baseDirectory = null;
		IExperimentAgent a = scope.getExperiment();
		String referenceDirectory = a.getWorkingPath();
		try {
			baseDirectory = URLDecoder.decode(referenceDirectory, "UTF-8");
			filePath = URLDecoder.decode(fp, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// scope.getGui().debug("FileUtils.constructAbsoluteFilePath baseDirectory = " + baseDirectory);
		final GamaRuntimeException ex;
		File file = null;
		if ( isAbsolutePath(filePath) ) {
			file = new File(filePath);
			if ( file.exists() || !mustExist ) {
				try {
					return file.getCanonicalPath();
				} catch (final IOException e) {
					e.printStackTrace();
					return file.getAbsolutePath();
				}
			}
			ex = new GamaRuntimeFileException(scope,
				"File denoted by " + file.getAbsolutePath() + " not found! Tried the following paths : ");
			ex.addContext(file.getAbsolutePath());
			file = new File(baseDirectory + File.separator + removeRoot(filePath));
			if ( file.exists() ) {
				try {
					return file.getCanonicalPath();
				} catch (final IOException e) {
					e.printStackTrace();
					return file.getAbsolutePath();
				}
			}
			ex.addContext(file.getAbsolutePath());
		} else {
			file = new File(baseDirectory + File.separatorChar + filePath);
			if ( file.exists() || !mustExist ) {
				try {
					// We have to try if the test is necessary.

					if ( GAMA.isInHeadLessMode() ) {
						return file.getAbsolutePath();
					} else {
						return file.getCanonicalPath();
					}

				} catch (final IOException e) {
					e.printStackTrace();
					return file.getAbsolutePath();
				}
			}
			ex = new GamaRuntimeFileException(scope,
				"File denoted by " + file.getAbsolutePath() + " not found! Tried the following paths : ");
			ex.addContext(file.getAbsolutePath());
		}

		throw ex;
	}

}
