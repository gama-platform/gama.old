/*********************************************************************************************
 *
 * 'GamaFileMetaData.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.lang.reflect.*;
import org.apache.commons.lang.StringUtils;

/**
 * Class GamaFileMetaInformation.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public abstract class GamaFileMetaData implements IGamaFileMetaData {

	/**
	 * The IResource modification stamp of the corresponding file at the
	 * time the cache entry was loaded.
	 */
	public long fileModificationStamp;

	public GamaFileMetaData(final long stamp) {
		this.fileModificationStamp = stamp;
	}

	public static <T extends IGamaFileMetaData> T from(final String s, final long stamp, final Class<T> clazz,
		final boolean includeOutdated) {
		T result = null;
		try {
			Constructor<T> c = clazz.getDeclaredConstructor(String.class);
			result = c.newInstance(s);
			if ( !includeOutdated && result.getModificationStamp() != stamp ) { return null; }
		} catch (Exception ignore) {
			System.err.println("Error loading metadata " + s + " : " + ignore.getClass().getSimpleName() + ":" +
				ignore.getMessage());
			if ( ignore instanceof InvocationTargetException && ignore.getCause() != null ) {
				ignore.getCause().printStackTrace();
			}
		}
		return result;
	}

	public GamaFileMetaData(final String propertyString) {
		String s = StringUtils.substringBefore(propertyString, DELIMITER);
		if ( s == null || s.isEmpty() ) {
			fileModificationStamp = 0;
		} else {
			fileModificationStamp = Long.valueOf(s);
		}
	}

	protected String[] split(final String s) {
		return StringUtils.splitByWholeSeparatorPreserveAllTokens(s, DELIMITER);
	}

	/**
	 * Method getModificationStamp()
	 * @see msi.gama.util.file.IGamaFileInfo#getModificationStamp()
	 */
	@Override
	public long getModificationStamp() {
		return fileModificationStamp;
	}

	@Override
	public abstract String getSuffix();

	@Override
	public Object getThumbnail() {
		return null;
	}

	/**
	 * Subclasses should extend !
	 * Method toPropertyString()
	 * @see msi.gama.util.file.IGamaFileMetaData#toPropertyString()
	 */

	@Override
	public String toPropertyString() {
		return String.valueOf(fileModificationStamp);
	}

	@Override
	public void setModificationStamp(final long ms) {
		fileModificationStamp = ms;
	}

}
