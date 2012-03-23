package msi.gama.headless.runtime;

import java.util.Collection;
import java.util.Iterator;

import msi.gama.headless.core.Simulation;
import msi.gama.headless.xml.Reader;
import msi.gama.headless.xml.XMLWriter;

public class Load {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/* String AC_Dir = "./lib";
	     String AC_Package = "";
	     DynamicClassLoader AC_Loader = new DynamicClassLoader(AC_Dir, AC_Package, true);
	     String[] AC_Array;
	     if (AC_Loader.LoadPlugIn() != 0) {
	            return;
	        }
	    */
		Reader in=new Reader(args[0]);
		XMLWriter ou=new XMLWriter(args[1]);
		in.parseXmlFile();
		Iterator<Simulation> it=in.getSimulation().iterator();
		System.out.println("Start");
		while(it.hasNext())
		{
			System.out.println("Start 1");
			Simulation si = it.next();
			try {
				si.setBufferedWriter(ou);
				si.loadAndBuild();
				System.out.println("loaded");
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			si.play();
			//System.out.println("it "+p.getName()+" "+p.getValue()+ " "+p.getValue().getClass());
		}
	}

}
