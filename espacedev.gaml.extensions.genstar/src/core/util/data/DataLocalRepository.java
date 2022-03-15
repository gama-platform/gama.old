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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * Manages a local repository of data; 
 * it enables to downloading distant resources only once, 
 * and then keep them in a local cache without downloading again. 
 * 
 * Data is stored in a base directory that you can change, and is by default 
 * user.home/.genstar/repo
 * 
 * @author Samuel Thiriot
 *
 */
public class DataLocalRepository {

	private static DataLocalRepository singleton = null;
	
	public static DataLocalRepository getRepository() {
		if (singleton == null)
			singleton = new DataLocalRepository();
		return singleton;
	}
	
	private Logger logger = LogManager.getLogger(DataLocalRepository.class);

	
	protected File baseDirectory = null;
	
	private DataLocalRepository() { }

	/**
	 * Define a base directory
	 * @param baseDirectory
	 */
	public void setDirectory(File baseDirectory) {
		if (!baseDirectory.exists())
			throw new RuntimeException("this directory does not exists");
		if (!baseDirectory.canRead() || !baseDirectory.canWrite())
			throw new RuntimeException("this directory does not have read and write permissions");
		
		this.baseDirectory = baseDirectory;
	}

	/**
	 * get the base directory. 
	 * @return
	 */
	public File getBaseDirectory() {
		if (baseDirectory == null) {
			File homeDir = new File(System.getProperty("user.home"));
			baseDirectory = new File(homeDir, ".genstar"+File.separator+"repo");
			baseDirectory.mkdirs();
		}
		return baseDirectory;
	}
	
	/**
	 * Returns our local repository for storing this url content
	 * @param url
	 * @return
	 */
	public File getDirectoryForUrl(URL url) {
		String hashname = Base64.getUrlEncoder().encodeToString(url.toString().getBytes());
		return new File(getBaseDirectory(), hashname);
	}
	
	
	protected void downloadFileInto(URL url, File targetFile) {
		
		logger.debug("opening URL {} : " + url);
		ReadableByteChannel rbc;
		try {
			rbc = Channels.newChannel(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("error while loading data from "+url,e);
		}
		
		// copy
		logger.debug("downloading into {} : " + targetFile.getAbsolutePath());
		File targetFileTmp = new File(targetFile.getAbsolutePath()+".download");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFileTmp);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			targetFileTmp.renameTo(targetFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	public File getOrDownloadResource(URL url, String downloadedName) {
		
		// get or create the host repository for this URL
		File dir = getDirectoryForUrl(url);
		if (!dir.exists()) {
			dir.mkdirs();
			PrintStream ps;
			try {
				ps = new PrintStream(new File(dir, "url.txt"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			ps.println(url.toString());
			ps.close();
		}
		
		// do we have the target name ? 
		File targetFile = new File(dir, downloadedName);
		if (!targetFile.exists()) {
			logger.debug("we don't have a local copy for url {}, let's download it... : " + url);
			downloadFileInto(url, targetFile);
		}
		return targetFile;
	}
}
