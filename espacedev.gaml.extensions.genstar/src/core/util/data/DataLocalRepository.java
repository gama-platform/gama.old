/*******************************************************************************************************
 *
 * DataLocalRepository.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Base64;

import core.util.exception.GenstarException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Manages a local repository of data; it enables to downloading distant resources only once, and then keep them in a
 * local cache without downloading again.
 *
 * Data is stored in a base directory that you can change, and is by default user.home/.genstar/repo
 *
 * @author Samuel Thiriot
 *
 */
public class DataLocalRepository {

	/** The singleton. */
	private static DataLocalRepository singleton = null;

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public static DataLocalRepository getRepository() {
		if (singleton == null) { singleton = new DataLocalRepository(); }
		return singleton;
	}

	/** The base directory. */
	protected File baseDirectory = null;

	/**
	 * Instantiates a new data local repository.
	 */
	private DataLocalRepository() {}

	/**
	 * Define a base directory
	 *
	 * @param baseDirectory
	 */
	public void setDirectory(final File baseDirectory) {
		if (!baseDirectory.exists()) throw new GenstarException("this directory does not exists");
		if (!baseDirectory.canRead() || !baseDirectory.canWrite())
			throw new GenstarException("this directory does not have read and write permissions");

		this.baseDirectory = baseDirectory;
	}

	/**
	 * get the base directory.
	 *
	 * @return
	 */
	public File getBaseDirectory() {
		if (baseDirectory == null) {
			File homeDir = new File(System.getProperty("user.home"));
			baseDirectory = new File(homeDir, ".genstar" + File.separator + "repo");
			baseDirectory.mkdirs();
		}
		return baseDirectory;
	}

	/**
	 * Returns our local repository for storing this url content
	 *
	 * @param url
	 * @return
	 */
	public File getDirectoryForUrl(final URL url) {
		String hashname = Base64.getUrlEncoder().encodeToString(url.toString().getBytes());
		return new File(getBaseDirectory(), hashname);
	}

	/**
	 * Download file into.
	 *
	 * @param url
	 *            the url
	 * @param targetFile
	 *            the target file
	 */
	protected void downloadFileInto(final URL url, final File targetFile) {

		DEBUG.OUT("opening URL {} : " + url);
		ReadableByteChannel rbc;
		try {
			rbc = Channels.newChannel(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new GenstarException("error while loading data from " + url, e);
		}

		// copy
		DEBUG.OUT("downloading into {} : " + targetFile.getAbsolutePath());
		File targetFileTmp = new File(targetFile.getAbsolutePath() + ".download");
		try (FileOutputStream fos = new FileOutputStream(targetFileTmp)) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			targetFileTmp.renameTo(targetFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new GenstarException(e);
		}
	}

	/**
	 * Gets the or download resource.
	 *
	 * @param url
	 *            the url
	 * @param downloadedName
	 *            the downloaded name
	 * @return the or download resource
	 */
	public File getOrDownloadResource(final URL url, final String downloadedName) {

		// get or create the host repository for this URL
		File dir = getDirectoryForUrl(url);
		if (!dir.exists()) {
			dir.mkdirs();
			try (PrintStream ps = new PrintStream(new File(dir, "url.txt"))) {
				ps.println(url.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new GenstarException(e);
			}

		}

		// do we have the target name ?
		File targetFile = new File(dir, downloadedName);
		if (!targetFile.exists()) {
			DEBUG.OUT("we don't have a local copy for url {}, let's download it... : " + url);
			downloadFileInto(url, targetFile);
		}
		return targetFile;
	}
}
