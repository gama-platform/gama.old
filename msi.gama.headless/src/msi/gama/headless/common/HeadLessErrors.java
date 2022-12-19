/*******************************************************************************************************
 *
 * HeadLessErrors.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.common;

/**
 * The Class HeadLessErrors.
 */
public abstract class HeadLessErrors {

	/** The Constant LAUNCHING_ERROR. */
	public static final int LAUNCHING_ERROR = 1;
	
	/** The Constant PARAMETER_ERROR. */
	public static final int PARAMETER_ERROR = 0;
	
	/** The Constant PERMISSION_ERROR. */
	public static final int PERMISSION_ERROR = 2;
	
	/** The Constant EXIST_DIRECTORY_ERROR. */
	public static final int EXIST_DIRECTORY_ERROR = 3;
	
	/** The Constant NOT_EXIST_FILE_ERROR. */
	public static final int NOT_EXIST_FILE_ERROR = 4;
	
	/** The Constant HPC_PARAMETER_ERROR. */
	public static final int HPC_PARAMETER_ERROR = 5;
	
	/** The Constant INPUT_NOT_DEFINED. */
	public static final int INPUT_NOT_DEFINED = 6;
	
	/** The Constant OUTPUT_NOT_DEFINED. */
	public static final int OUTPUT_NOT_DEFINED = 7;

	/** The Constant ERRORS. */
	private final static String[] ERRORS =
		{
			"Usage:\n\tjava -jar eclipse/plugins/org.eclipse.equinox.launcher_1.2.0.v20110502.jar -console -application msi.gama.headless.id4 XMLInputFile XMLOutputFile\n",
			"Launching error... try again",
			"Unable to create directory at #. Check your file permission!",
			"Unable to create directory at #. Directory already exist!",
			"Unable to read input file #. File not exist!",
			"Input file is not defined",
			"Output directory is not defined"};

	/**
	 * Gets the error.
	 *
	 * @param errorCode the error code
	 * @param path the path
	 * @return the error
	 */
	public static String getError(final int errorCode, final String path) {
		if ( path == null ) { return ERRORS[errorCode]; }
		return ERRORS[errorCode].replaceFirst("#", path);
	}
}
