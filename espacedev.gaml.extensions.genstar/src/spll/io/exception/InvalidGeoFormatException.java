package spll.io.exception;

import java.util.List;

import spll.io.SPLGeofileBuilder.SPLGisFileExtension;

public class InvalidGeoFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidGeoFormatException(String fileName, List<SPLGisFileExtension> supportedFileFormat) {
		super("file "+fileName+" is not a valide file type which are "+supportedFileFormat.toString());	
	}

}
