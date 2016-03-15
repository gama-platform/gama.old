/*********************************************************************************************
 * 
 *
 * 'HeadLessErrors.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.common;

public abstract class HeadLessErrors {

	public static final int LAUNCHING_ERROR = 1;
	public static final int PARAMETER_ERROR = 0;
	public static final int PERMISSION_ERROR = 2;
	public static final int EXIST_DIRECTORY_ERROR = 3;
	public static final int NOT_EXIST_FILE_ERROR = 4;
	public static final int HPC_PARAMETER_ERROR = 5;
	public static final int INPUT_NOT_DEFINED = 6;
	public static final int OUTPUT_NOT_DEFINED = 7;

	private final static String[] ERRORS =
		{
			"Usage:\n\tjava -jar eclipse/plugins/org.eclipse.equinox.launcher_1.2.0.v20110502.jar -console -application msi.gama.headless.id4 XMLInputFile XMLOutputFile\n",
			"Launching error... try again",
			"Unable to create directory at #. Check your file permission!",
			"Unable to create directory at #. Directory already exist!",
			"Unable to read input file #. File not exist!",
			"Input file is not defined",
			"Output directory is not defined"};

	public static String getError(final int errorCode, final String path) {
		if ( path == null ) { return ERRORS[errorCode]; }
		return ERRORS[errorCode].replaceFirst("#", path);
	}
}
