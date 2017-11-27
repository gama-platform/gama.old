/*********************************************************************************************
 *
 * 'FileUtils.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.google.common.collect.Iterables;

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
			if (filePath.startsWith(roots[i].getAbsolutePath())) { return true; }
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
			if (absoluteFilePath.startsWith(roots[i].getAbsolutePath())) { return absoluteFilePath
					.substring(roots[i].getAbsolutePath().length(), absoluteFilePath.length()); }
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
		Iterable<String> baseDirectories = null;
		final IExperimentAgent a = scope.getExperiment();
		// final List<String> referenceDirectories = a.getWorkingPaths();

		try {
			baseDirectories = Iterables.transform(a.getWorkingPaths(), each -> {
				try {
					return withTrailingSep(URLDecoder.decode(each, "UTF-8"));
				} catch (final UnsupportedEncodingException e1) {
					return each;
				}
			});
			filePath = URLDecoder.decode(fp, "UTF-8");
		} catch (final UnsupportedEncodingException e1) {
			filePath = fp;
		}
		final GamaRuntimeException ex =
				new GamaRuntimeFileException(scope, "File denoted by " + filePath + " not found.");
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
				if (file.exists()) {
					try {
						// We have to try if the test is necessary.
						if (scope.getExperiment().isHeadless()) {
							// if (GAMA.isInHeadLessMode()) {
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
			// We havent found the file, but it may not exist. In that case, the
			// first directory is used as a reference.
			if (!mustExist)
				try {
					return new File(Iterables.get(baseDirectories, 0) + filePath).getCanonicalPath();
				} catch (final IOException e) {
					throw ex;
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
		try (FileInputStream in = new FileInputStream(f)) {
			int size = in.available();
			if (size > 1024)
				size = 1024;
			data = new byte[size];
			in.read(data);
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

	public static String constructAbsoluteTempFilePath(final IScope scope, final String suffix) {
		try {
			final File temp = File.createTempFile("tmp", suffix);
			temp.deleteOnExit();
			return temp.getAbsolutePath();
		} catch (final Exception e) {
			// Not allowed to create temp files in system
			final String newPath = constructAbsoluteFilePath(scope, "tmp/" + suffix, false);
			final File file = new File(newPath);
			try {
				file.createNewFile();
			} catch (final IOException e1) {}
			file.deleteOnExit();
			return file.getAbsolutePath();
		}
	}

}
