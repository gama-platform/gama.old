package msi.gama.headless.common;

public abstract class HeadLessErrors {
	public static final int LAUNCHING_ERROR = 1;
	public static final int PARAMETER_ERROR = 0;
	public static final int PERMISSION_ERROR = 2;
	public static final int EXIST_DIRECTORY_ERROR = 3;
	public static final int NOT_EXIST_FILE_ERROR = 4;
	
	private final static String[] ERRORS= {
		"Usage:\n\tjava -jar eclipse/plugins/org.eclipse.equinox.launcher_1.2.0.v20110502.jar -console -application msi.gama.headless.id4 XMLInputFile XMLOutputFile\n",
		"Launching error... try again",
		"Unable to create directory at #. Check your file permission!",
		"Unable to create directory at #. Directory already exist!",
		"Unable to read input file #. File not exist!"
	};

	public  static String getError(int errorCode, String path)
	{
		if(path == null)
			return ERRORS[errorCode];
		else
			return ERRORS[errorCode].replaceFirst("#", path);
	}
}
