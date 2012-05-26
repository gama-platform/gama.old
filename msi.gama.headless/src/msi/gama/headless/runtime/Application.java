package msi.gama.headless.runtime;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;

import javax.imageio.ImageIO;

import msi.gama.headless.core.*;
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
		IHeadLessExperiment exp =
			HeadlessSimulationLoader.newHeadlessSimulation("/Users/marilleau/model_gama_test.gaml");

//	GAMA.getExperiment().setParameterValue(name, v)
//	GAMA.getExperiment().in
		/*
		 * Runnable rnb = new Runnable(){ public void run() {GAMA.startOrPauseExperiment();}};
		 * Thread exec = new Thread(rnb);
		 * exec.start();
		 */
		// GAMA.startOrPauseExperiment();
		// GAMA.startOrPauseExperiment();
		// GAMA.getExperiment().startCurrentSimulation();
		// System.out.println("ccoucoe 3");
		//
		//
		//
		//
		Thread.sleep(2000);

		// GAMA.startOrPauseExperiment();
		//
		 MonitorOutput m =  ((OutputManager)GAMA.getExperiment().getOutputManager()).getMonitors().get(0);
		//Thread.sleep(1000);
			((LayerDisplayOutput)( ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutput("msi.gama.application.view.LayeredDisplayViewmain_display"))).setImageFileName("/tmp/test");

			((LayerDisplayOutput)( ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutput("msi.gama.application.view.LayeredDisplayViewmain_display"))).getSurface().setSnapshotFileName("/pouet");
			((LayerDisplayOutput)( ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutput("msi.gama.application.view.LayeredDisplayViewmain_display"))).getSurface().setAutoSave(true);
			
			
		 
		 long start = Calendar.getInstance().getTimeInMillis();
		// GAMA.getExperiment().getOutputManager().getOutput(id)
		GAMA.getExperiment().step();
		
		
		for(int i=0; i<150; i++)
		{
		GAMA.getExperiment().step();
		BufferedImage buf=((LayerDisplayOutput)( ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutput("msi.gama.application.view.LayeredDisplayViewmain_display"))).getImage();
		
		ImageIO.write(buf, "png", new File("/tmp/snap/snap"+i+".png"));
		}
		
		 long end = Calendar.getInstance().getTimeInMillis();

		 System.out.println("duration (ms)" + (end-start));
		return null;
	}

//	((LayerDisplayOutput)( ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutput("msi.gama.application.view.LayeredDisplayViewmain_display"))).getSurface().snapshot();
		//Image img =Toolkit.getDefaultToolkit().createImage(buf.getSource());
		//((LayerDisplayOutput)( ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutput("msi.gama.application.view.LayeredDisplayViewmain_display"))).getImage()
	
	
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
