/*********************************************************************************************
 * 
 *
 * 'HPCExperiment.java', in plugin 'msi.gama.hpc', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.hpc.common;

import java.io.*;
import java.util.Vector;

public class HPCExperiment {

	private final Vector<Parameter> parameters;
	private final Vector<Output> outputs;
	private int state;
	private final int id;
	private final String modelPath;
	private final int finalStep;

	public HPCExperiment(final int id, final String mod, final int finalStep) {
		this.parameters = new Vector<Parameter>();
		this.outputs = new Vector<Output>();
		this.id = id;
		this.modelPath = mod;
		this.finalStep = finalStep;
	}

	public int getState() {
		return state;
	}

	public void addParameter(final Parameter p) {
		this.parameters.add(p);
	}

	public void addOutput(final Output p) {
		this.outputs.add(p);
	}

	public static File produceXML(final HPCExperiment hp, final String fileName) {
		String chaine =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Simulation id=\"" + hp.id + "\" \n" +
				" driver=\"msi.gama.headless.runtime.GamaSimulator\" \n" + " sourcePath=\"" + hp.modelPath + "\" " +
				" finalstep=\"" + hp.finalStep + "\"> \n" + "<Parameters >\n";

		for ( int i = 0; i < hp.parameters.size(); i++ ) {
			Parameter pp = hp.parameters.get(i);
			chaine =
				chaine + "<Parameter name=\"" + pp.getName() + "\" type=\"" + Parameter.castType(pp.getValue()) +
					"\" value=\"" + pp.getValue().toString() + "\" />\n";
		}

		chaine = chaine + "</Parameters><Outputs>";

		for ( int j = 0; j < hp.outputs.size(); j++ ) {
			Output o = hp.outputs.get(j);
			chaine =
				chaine + "<Output id=\"" + j + "\" name=\"" + o.getName() + "\"  framerate=\"" + o.getFrameRate() +
					"\" />";
		}
		chaine = chaine + "</Outputs></Simulation>";

		FileOutputStream out = null;
		try {
			File outf = new File(fileName);
			out = new FileOutputStream(outf);
			out.write(chaine.getBytes(), 0, chaine.length());
			return outf;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if ( out != null ) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
