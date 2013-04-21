package msi.gama;

import java.io.File;
import java.io.IOException;

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
	
	public static File getTmpFile(String name) {
		
		try {
			File temp = File.createTempFile(name, ".tmp");
			temp.deleteOnExit();
			return temp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 

	}
}
