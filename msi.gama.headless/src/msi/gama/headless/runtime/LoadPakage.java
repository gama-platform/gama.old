package msi.gama.headless.runtime;

import msi.gama.headless.classLoader.DynamicClassLoader;


public class LoadPakage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 String AC_Dir = "/tmp/lib";
	     String AC_Package = "";
	     DynamicClassLoader AC_Loader = new DynamicClassLoader(AC_Dir, AC_Package, true);
	     String[] AC_Array;
	     if (AC_Loader.LoadPlugIn() != 0) {
	            return;
	        }
	        System.out.println("List of loaded plugins for package " + AC_Package);
	}

}
