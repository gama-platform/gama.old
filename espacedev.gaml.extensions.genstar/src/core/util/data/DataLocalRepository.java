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

	private static DataLocalRepository singleton = null;

	public static DataLocalRepository getRepository() {
		if (singleton == null) { singleton = new DataLocalRepository(); }
		return singleton;
	}

	protected File baseDirectory = null;

	private DataLocalRepository() {}

	/**
	 * Define a base directory
	 *
	 * @param baseDirectory
	 */
	public void setDirectory(final File baseDirectory) {
		if (!baseDirectory.exists()) throw new RuntimeException("this directory does not exists");
		if (!baseDirectory.canRead() || !baseDirectory.canWrite())
			throw new RuntimeException("this directory does not have read and write permissions");

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

	protected void downloadFileInto(final URL url, final File targetFile) {

		DEBUG.OUT("opening URL {} : " + url);
		ReadableByteChannel rbc;
		try {
			rbc = Channels.newChannel(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("error while loading data from " + url, e);
		}

		// copy
		DEBUG.OUT("downloading into {} : " + targetFile.getAbsolutePath());
		File targetFileTmp = new File(targetFile.getAbsolutePath() + ".download");
		try (FileOutputStream fos = new FileOutputStream(targetFileTmp)) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			targetFileTmp.renameTo(targetFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public File getOrDownloadResource(final URL url, final String downloadedName) {

		// get or create the host repository for this URL
		File dir = getDirectoryForUrl(url);
		if (!dir.exists()) {
			dir.mkdirs();
			try (PrintStream ps = new PrintStream(new File(dir, "url.txt"))) {
				ps.println(url.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
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
