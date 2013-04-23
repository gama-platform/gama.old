package msi.gama;

import java.io.File;
import java.io.IOException;

import org.junit.rules.TemporaryFolder;

/**
 * Various utils for tests, like creating tmp files, etc.
 * 
 * @author Samuel Thiriot
 *
 */
public class TestUtils {

	/**
	 * Returns a tmp filename to be used for a junit test 
	 * @param name
	 * @return
	 */
	public static String getTmpFilename(String name) {
		
		try {
			File temp = File.createTempFile(name, ".tmp");
			temp.deleteOnExit();
			return temp.getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 

	}
	
	public static File getTmpFile(String name, String extension) {
		
		try {
			File temp = File.createTempFile(name, "."+extension);
			temp.deleteOnExit();
			System.out.println("tmp file: "+temp.getAbsolutePath());
			return temp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 

	}
	
	public static File getTmpFile(String name) {
		return getTmpFile(name, "tmp");
	}
	
	
	public static File getTmpDirectory(String name) {
		return getTmpFile(name, "tmp");
	}
	
	public static File createTempDirectory() throws IOException {
	    final File temp;

	    temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

	    if(!(temp.delete()))
	    {
	        throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
	    }

	    if(!(temp.mkdir()))
	    {
	        throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
	    }

	    temp.deleteOnExit();
	    
	    return temp;
	}
}
