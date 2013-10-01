/**
 * Created by drogoul, 20 d�c. 2011
 * 
 */
package msi.gama.common.util;

import java.io.*;
import java.net.URLDecoder;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The class FileUtils.
 * 
 * @author drogoul
 * @since 20 d�c. 2011
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
			if ( absoluteFilePath.startsWith(roots[i].getAbsolutePath()) ) { return absoluteFilePath.substring(roots[i]
				.getAbsolutePath().length(), absoluteFilePath.length()); }
		}
		return absoluteFilePath;
	}

	/**
	 * Construct absolute file path.
	 * 
	 * @param filePath the file path
	 * @param mustExist the must exist
	 * 
	 * @return the string
	 * 
	 * @throws GamlException the gaml exception
	 */
	static public String constructAbsoluteFilePath(final String fp, final String rf, final boolean mustExist)
		throws GamaRuntimeException {
		String filePath = null;
		String referenceFile = null;
		try {
			referenceFile = URLDecoder.decode(rf, "UTF-8");
			filePath = URLDecoder.decode(fp, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String baseDirectory = new File(referenceFile).getParent();
		//GuiUtils.debug("FileUtils.constructAbsoluteFilePath baseDirectory = " + baseDirectory);
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
			ex =
				GamaRuntimeException.error("File denoted by " + file.getAbsolutePath() +
					" not found! Tried the following paths : ");
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

					if ( GuiUtils.isInHeadLessMode() ) {
						return file.getAbsolutePath();
					} else {
						return file.getCanonicalPath();
					}

				} catch (final IOException e) {
					e.printStackTrace();
					return file.getAbsolutePath();
				}
			}
			ex =
				GamaRuntimeException.error("File denoted by " + file.getAbsolutePath() +
					" not found! Tried the following paths : ");
			ex.addContext(file.getAbsolutePath());
		}

		throw ex;
	}

}
