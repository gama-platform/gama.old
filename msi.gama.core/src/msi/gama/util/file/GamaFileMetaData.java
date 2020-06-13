/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaFileMetaData.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;

import ummisco.gama.dev.utils.DEBUG;

/**
 * Class GamaFileMetaInformation.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public abstract class GamaFileMetaData implements IGamaFileMetaData {

	/**
	 * The IResource modification stamp of the corresponding file at the time the cache entry was loaded.
	 */
	public long fileModificationStamp;
	boolean hasFailed;

	public GamaFileMetaData(final long stamp) {
		this.fileModificationStamp = stamp;
	}

	public static <T extends IGamaFileMetaData> T from(final String s, final long stamp, final Class<T> clazz,
			final boolean includeOutdated) {
		T result = null;
		try {
			final Constructor<T> c = clazz.getDeclaredConstructor(String.class);
			result = c.newInstance(s);
			final boolean hasFailed = result.hasFailed();
			if (!hasFailed && !includeOutdated && result.getModificationStamp() != stamp) { return null; }
		} catch (final Exception ignore) {
			DEBUG.ERR("Error loading metadata " + s + " : " + ignore.getClass().getSimpleName() + ":"
					+ ignore.getMessage());
			if (ignore instanceof InvocationTargetException && ignore.getCause() != null) {
				ignore.getCause().printStackTrace();
			}
		}
		return result;
	}

	public GamaFileMetaData(final String propertyString) {
		final String s = StringUtils.substringBefore(propertyString, DELIMITER);
		if (FAILED.equals(s)) {
			hasFailed = true;
		} else if (s == null || s.isEmpty()) {
			fileModificationStamp = 0;
		} else {
			fileModificationStamp = Long.parseLong(s);
		}
	}

	@Override
	public boolean hasFailed() {
		return hasFailed;
	}

	protected String[] split(final String s) {
		return StringUtils.splitByWholeSeparatorPreserveAllTokens(s, DELIMITER);
	}

	/**
	 * Method getModificationStamp()
	 *
	 * @see msi.gama.util.file.IGamaFileInfo#getModificationStamp()
	 */
	@Override
	public long getModificationStamp() {
		return fileModificationStamp;
	}

	@Override
	public Object getThumbnail() {
		return null;
	}

	/**
	 * Subclasses should extend ! Method toPropertyString()
	 *
	 * @see msi.gama.util.file.IGamaFileMetaData#toPropertyString()
	 */

	@Override
	public String toPropertyString() {
		if (hasFailed) { return FAILED; }
		return String.valueOf(fileModificationStamp);
	}

	@Override
	public void setModificationStamp(final long ms) {
		fileModificationStamp = ms;
	}

}
