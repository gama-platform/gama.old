package msi.gama.headless.runtime;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import msi.gama.headless.common.Globals;
import msi.gama.headless.common.HeadLessErrors;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.core.Simulation;
import msi.gama.headless.openMole.IMoleExperiment;
import msi.gama.headless.openMole.MoleSimulationLoader;
import msi.gama.headless.xml.Reader;
import msi.gama.headless.xml.XMLWriter;
import msi.gama.kernel.model.IModel;
import msi.gaml.operators.Cast;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {

	public static boolean headLessSimulation = false;

	private static boolean checkParameters(final String[] args) {
		if ( args == null ) { return showError(HeadLessErrors.LAUNCHING_ERROR, null); }
		if ( args.length < 2 ) { return showError(HeadLessErrors.PARAMETER_ERROR, null); }
		Globals.OUTPUT_PATH = args[1];
		Globals.IMAGES_PATH = args[1] + "/snapshot";
		File output = new File(Globals.OUTPUT_PATH);
		if(!output.exists())
			output.mkdir();
		
		File images = new File(Globals.IMAGES_PATH);
		if(!images.exists())
			images.mkdir();
			
		File input = new File(args[0]);
		if (!input.exists()) {
			return showError(HeadLessErrors.NOT_EXIST_FILE_ERROR, args[0]);
		}
		return true;
	}

	private static boolean showError(final int errorCode, final String path) {
		System.out.println(HeadLessErrors.getError(errorCode, path));
		return false;
	}

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		
//		MoleSimulationLoader.loadGAMA();
//		IModel mdl = MoleSimulationLoader.loadModel(new File("/tmp/headless/samples/predatorPrey/predatorPrey.gaml"));
//		IMoleExperiment exp = MoleSimulationLoader.newExperiment(mdl);
//		exp.setParameter("nb_preys_init", 678);
//		
//		exp.setup("preyPred",123);
//		exp.step();
//		exp.step();
//		exp.step();
//		exp.step();
//		System.out.println("coucocy "+ exp.getOutput("number_of_preys"));
//		
//		exp.step();
//		exp.step();
//		exp.step();
//		System.out.println("coucocy "+ exp.getOutput("number_of_preys"));
//		System.out.println("coucocy "+ exp.getVariableOutput("nb_preys"));
//
//		System.out.println("fin loading;");
//		
//		System.exit(-1);
		
		HeadlessSimulationLoader.preloadGAMA();
		Map<String, String[]> mm = context.getArguments();
		String[] args = mm.get("application.args");
		if ( !checkParameters(args) ) {
			System.exit(-1);
		}
		Reader in = new Reader(args[0]);
		in.parseXmlFile();
		int numSim = 1;
		if (args.length>2 && args[2] != null) {
			numSim = Cast.asInt(null, args[2]);
		}
		Iterator<Simulation> it = in.getSimulation().iterator();
		FakeApplication fa[] = new FakeApplication[50];
		int n = 0;
		while (it.hasNext()) {
			Simulation sim = it.next();
			for (int i = 0; i < numSim; i++) {
				Simulation si = new Simulation(sim);
				try {
					XMLWriter ou = new XMLWriter(Globals.OUTPUT_PATH + "/"
							+ Globals.OUTPUT_FILENAME + i + ".xml");
					si.setBufferedWriter(ou);
					si.loadAndBuild();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
				fa[i] = new FakeApplication(si);
				fa[i].start();
				n++;
			}
		}
		boolean done = false;
		while (!done) {
			done = true;
			for (int i = 0; i < n; i++) {

				if (fa[i].isAlive()) {
					done = false;
				}
			}
		}
		return null;
	}

	@Override
	public void stop() {}

}
