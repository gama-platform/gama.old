/*******************************************************************************************************
 *
 * msi.gama.common.util.OldFileUtils.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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

public class OldFileUtils {

	private static String withTrailingSep(final String path) {
		if (path.endsWith("/")) { return path; }
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
		final File[] roots = File.listRoots();
		for (final File root : roots) {
			if (absoluteFilePath.startsWith(root.getAbsolutePath())) {
				return absoluteFilePath.substring(root.getAbsolutePath().length(), absoluteFilePath.length());
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
	static public String constructAbsoluteFilePathAlternate(final IScope scope, final String fp,
			final boolean mustExist) {
		if (scope == null) { return fp; }
		String filePath = null;
		Iterable<String> baseDirectories = null;
		final IExperimentAgent a = scope.getExperiment();

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
		if (FileUtils.isAbsolutePath(filePath)) {
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
			if (!mustExist) {
				try {
					return new File(Iterables.get(baseDirectories, 0) + filePath).getCanonicalPath();
				} catch (final IOException e) {
					throw ex;
				}
			}
		}

		throw ex;
	}

	/**
	 * Guess whether given file is binary. Just checks for anything under 0x09.
	 */
	public static boolean isBinaryFile(final IScope scope, final File f) {
		if (f == null || !f.exists()) { return false; }
		byte[] data;
		try (FileInputStream in = new FileInputStream(f)) {
			int size = in.available();
			if (size > 1024) {
				size = 1024;
			}
			data = new byte[size];
			in.read(data);
			int ascii = 0;
			int other = 0;

			for (final byte b : data) {
				if (b < 0x09) { return true; }

				if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D) {
					ascii++;
				} else if (b >= 0x20 && b <= 0x7E) {
					ascii++;
				} else {
					other++;
				}
			}

			if (other == 0) { return false; }

			return 100 * other / (ascii + other) > 95;
		} catch (final IOException e) {
			GAMA.reportAndThrowIfNeeded(scope,
					GamaRuntimeException.error("Problem determining the type of " + f.getPath(), scope), false);
			return false;
		}

	}

}
