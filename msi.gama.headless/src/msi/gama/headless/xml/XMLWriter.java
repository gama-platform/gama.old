/*********************************************************************************************
 * 
 * 
 * 'XMLWriter.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.xml;

import java.io.*;
import msi.gama.headless.core.*;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.ExperimentJob.ListenedVariable;

public class XMLWriter implements Writer {

	private BufferedWriter file;

	public XMLWriter(final String f) {
		try {
			this.file = new BufferedWriter(new FileWriter(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public XMLWriter(final BufferedWriter f) {
			this.file = f;
		
	}

	
	@Override
	public void close() {
		String res = "</Simulation>";
		try {
			this.file.write(res);
			this.file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void writeResultStep(final long step, final ListenedVariable[] vars) {
		StringBuffer sb = new StringBuffer("\t<Step id='").append(step).append("' >\n");
		for ( int i = 0; i < vars.length; i++ ) {
			sb.append("\t\t<Variable name='").append(vars[i].getName()).append("' type='").append(vars[i].getDataType().name()).append("'>").append(vars[i].getValue())
				.append("</Variable>\n");
		}
		sb.append("\t</Step>\n");
		try {
			this.file.write(sb.toString());
			this.file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeSimulationHeader(final ExperimentJob s) {
		String res = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		res += "<Simulation id=\"" + s.getExperimentID() + "\" >\n";
		try {
			this.file.write(res);
			this.file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
