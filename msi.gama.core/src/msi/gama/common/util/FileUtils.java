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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
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
	 * @param filePath
	 *            the file path
	 *
	 * @return true, if is absolute path
	 */
	private static boolean isAbsolutePath(final String filePath) {
		final File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			if (filePath.startsWith(roots[i].getAbsolutePath())) {
				return true;
			}
		}
		return false;
	}

	private static String withTrailingSep(final String path) {
		if (path.endsWith("/"))
			return path;
		return path + "/";
	}

	/**
	 * Removes a root.
	 *
	 * @param absoluteFilePath
	 *            the absolute file path
	 *
	 * @return the string
	 */
	private static String removeRoot(final String absoluteFilePath) {
		// OutputManager.debug("absoluteFilePath before = " + absoluteFilePath);

		final File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			if (absoluteFilePath.startsWith(roots[i].getAbsolutePath())) {
				return absoluteFilePath.substring(roots[i].getAbsolutePath().length(), absoluteFilePath.length());
			}
		}
		return absoluteFilePath;
	}

	/**
	 * Construct an absolute file path.
	 *
	 * @param scope
	 *            the scope
	 * @param fp
	 *            the fp
	 * @param mustExist
	 *            the must exist
	 * @return the string
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	static public String constructAbsoluteFilePath(final IScope scope, final String fp, final boolean mustExist)
			throws GamaRuntimeException {
		String filePath = null;
		final List<String> baseDirectories = new ArrayList();
		final IExperimentAgent a = scope.getExperiment();
		final List<String> referenceDirectories = a.getWorkingPaths();
		try {
			for (final String ref : referenceDirectories) {
				baseDirectories.add(withTrailingSep(URLDecoder.decode(ref, "UTF-8")));
			}
			filePath = URLDecoder.decode(fp, "UTF-8");
		} catch (final UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		final GamaRuntimeException ex = new GamaRuntimeFileException(scope,
				"File denoted by " + filePath + " not found.");
		File file = null;
		if (isAbsolutePath(filePath)) {
			file = new File(filePath);
			if (file.exists() || !mustExist) {
				try {
					return file.getCanonicalPath();
				} catch (final IOException e) {
					e.printStackTrace();
					return file.getAbsolutePath();
				}
			}
			for (final String baseDirectory : baseDirectories) {
				file = new File(baseDirectory + removeRoot(filePath));
				if (file.exists()) {
					try {
						return file.getCanonicalPath();
					} catch (final IOException e) {
						e.printStackTrace();
						return file.getAbsolutePath();
					}
				}
				ex.addContext(file.getAbsolutePath());
			}
		} else {
			for (final String baseDirectory : baseDirectories) {
				file = new File(baseDirectory + filePath);
				if (file.exists() || !mustExist) {
					try {
						// We have to try if the test is necessary.

						if (GAMA.isInHeadLessMode()) {
							return file.getAbsolutePath();
						} else {
							return file.getCanonicalPath();
						}

					} catch (final IOException e) {
						e.printStackTrace();
						return file.getAbsolutePath();
					}
				}

				ex.addContext(file.getAbsolutePath());
			}
		}

		throw ex;
	}

	/**
	 * Guess whether given file is binary. Just checks for anything under 0x09.
	 */
	public static boolean isBinaryFile(final IScope scope, final File f) {
		if (f == null || !f.exists())
			return false;
		byte[] data;
		try {
			final FileInputStream in = new FileInputStream(f);
			int size = in.available();
			if (size > 1024)
				size = 1024;
			data = new byte[size];
			in.read(data);
			in.close();
			int ascii = 0;
			int other = 0;

			for (int i = 0; i < data.length; i++) {
				final byte b = data[i];
				if (b < 0x09)
					return true;

				if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D)
					ascii++;
				else if (b >= 0x20 && b <= 0x7E)
					ascii++;
				else
					other++;
			}

			if (other == 0)
				return false;

			return 100 * other / (ascii + other) > 95;
		} catch (final IOException e) {
			GAMA.reportAndThrowIfNeeded(scope,
					GamaRuntimeException.error("Problem determining the type of " + f.getPath(), scope), false);
			return false;
		}

	}

}
