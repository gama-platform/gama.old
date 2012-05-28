package msi.gama.headless.runtime;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import msi.gama.headless.core.*;
import msi.gama.headless.xml.Reader;
import msi.gama.headless.xml.XMLWriter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.outputs.OutputManager;
import msi.gama.runtime.GAMA;
import org.eclipse.equinox.app.*;

public class Application implements IApplication {

	public static boolean headLessSimulation = false;

	private static boolean isHeadlessSimulation() {
		return headLessSimulation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception {
	
		/*
		Map<String, String[]> mm=context.getArguments();
		
		for(String s:mm.keySet())
		{
			System.out.println("arg "+ s + "  "+ mm.get(s).length );//+ " value: "+ mm.get(s).toString());
		}
		*/
		Reader in=new Reader("/Users/marilleau/in.xml");
		XMLWriter ou=new XMLWriter("/Users/marilleau/out.xml");
		in.parseXmlFile();
		Iterator<Simulation> it=in.getSimulation().iterator();
		while(it.hasNext())
		{
			Simulation si = it.next();
			try {
				si.setBufferedWriter(ou);
				si.loadAndBuild();
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
		}
			return null;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
