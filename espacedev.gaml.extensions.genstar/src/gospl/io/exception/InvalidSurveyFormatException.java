package gospl.io.exception;

import java.util.List;

public class InvalidSurveyFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidSurveyFormatException(String fileName, List<String> supportedFileFormat) {
		super("file "+fileName+" is not a valide file type which are "+supportedFileFormat.toString());	
	}
	
}
