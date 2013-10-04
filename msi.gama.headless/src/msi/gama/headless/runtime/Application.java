package msi.gama.headless.runtime;

import java.io.File;
import java.util.*;
import msi.gama.headless.common.*;
import msi.gama.headless.core.Simulation;
import msi.gama.headless.xml.*;
import org.eclipse.equinox.app.*;

public class Application implements IApplication {

	public static boolean headLessSimulation = false;

	private static boolean checkParameters(final String[] args) {
		if ( args == null ) { return showError(HeadLessErrors.LAUNCHING_ERROR, null); }
		if ( args.length < 2 ) { return showError(HeadLessErrors.PARAMETER_ERROR, null); }
		Globals.OUTPUT_PATH = args[1];
		Globals.IMAGES_PATH = args[1] + "/snapshot";
		File output = new File(Globals.OUTPUT_PATH);
		File images = new File(Globals.IMAGES_PATH);
		File input = new File(args[0]);
		if ( output.exists() ) { return showError(HeadLessErrors.EXIST_DIRECTORY_ERROR, Globals.OUTPUT_PATH); }
		if ( !input.exists() ) { return showError(HeadLessErrors.NOT_EXIST_FILE_ERROR, args[0]); }
		if ( !output.mkdir() ) { return showError(HeadLessErrors.PERMISSION_ERROR, Globals.OUTPUT_PATH); }
		if ( !images.mkdir() ) { return showError(HeadLessErrors.PERMISSION_ERROR, Globals.IMAGES_PATH); }
		return true;

	}

	private static boolean showError(final int errorCode, final String path) {
		System.out.println(HeadLessErrors.getError(errorCode, path));
		return false;
	}

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		Map<String, String[]> mm = context.getArguments();
		String[] args = mm.get("application.args");
		if ( !checkParameters(args) ) {
			System.exit(-1);
		}
		Reader in = new Reader(args[0]);
		XMLWriter ou = new XMLWriter(Globals.OUTPUT_PATH + "/" + Globals.OUTPUT_FILENAME);
		in.parseXmlFile();
		Iterator<Simulation> it = in.getSimulation().iterator();
		while (it.hasNext()) {
			Simulation si = it.next();
			try {
				si.setBufferedWriter(ou);
				si.loadAndBuild();
			} catch (Exception e) {
				e.printStackTrace();
			}
			si.play();
		}
		return null;
	}

	@Override
	public void stop() {}

}
