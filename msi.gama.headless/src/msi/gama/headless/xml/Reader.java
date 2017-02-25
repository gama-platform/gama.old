/*********************************************************************************************
 *
 *
 * 'Reader.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.xml;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import msi.gama.headless.common.*;
import msi.gama.headless.job.*;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.HeadlessListener;

public class Reader {

	public String fileName;
	public InputStream myStream;
	ArrayList<ExperimentJob> sims;

	public void dispose() {
		this.fileName = null;
		try {
			this.myStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		myStream = null;
		sims.clear();
		sims = null;

	}

	public Reader(final String file) throws FileNotFoundException {
		fileName = file;
		myStream = new FileInputStream(new File(file));
	}

	public Reader(final InputStream inp) {
		myStream = inp;
	}

	public Collection<ExperimentJob> getSimulation() {
		return this.sims;
	}

	private Parameter readParameter(final Element e) {
		String name = e.getAttribute(XmlTAG.NAME_TAG);
		String value = e.getAttribute(XmlTAG.VALUE_TAG);
		String var = e.getAttribute(XmlTAG.VAR_TAG);
		String type = e.getAttribute(XmlTAG.TYPE_TAG);
		DataType dtype = DataType.valueOf(type);
		return new Parameter(name, var, DataTypeFactory.getObjectFromText(value, dtype), dtype);
	}

	private Output readOutput(final Element e) {
		String name = e.getAttribute(XmlTAG.NAME_TAG);
		String id = e.getAttribute(XmlTAG.ID_TAG);
		String path = e.getAttribute(XmlTAG.OUTPUT_PATH);
		int framerate = Integer.valueOf(e.getAttribute(XmlTAG.FRAMERATE_TAG));;
		return new Output(name, framerate, id, path);
	}

	private void readParameter(final ExperimentJob s, final Element docEle) {
		NodeList nl = docEle.getElementsByTagName(XmlTAG.PARAMETER_TAG);
		if ( nl != null && nl.getLength() > 0 ) {
			for ( int i = 0; i < nl.getLength(); i++ ) {

				// get the employee element
				Element el = (Element) nl.item(i);

				// get the Employee object
				Parameter e = this.readParameter(el);
				// add it to list
				s.addParameter(e);
			}
		}
	}

	private void readOutput(final ExperimentJob s, final Element docEle) {
		NodeList nl = docEle.getElementsByTagName(XmlTAG.OUTPUT_TAG);
		if ( nl != null && nl.getLength() > 0 ) {
			for ( int i = 0; i < nl.getLength(); i++ ) {

				// get the employee element
				Element el = (Element) nl.item(i);

				// get the Employee object
				Output e = this.readOutput(el);
				// add it to list
				s.addOutput(e);
			}
		}
	}

	private ExperimentJob readSimulation(final Element e) {

		String expId = e.getAttribute(XmlTAG.EXPERIMENT_ID_TAG);
		
		String finalStep = e.getAttribute(XmlTAG.FINAL_STEP_TAG);
		int max;
		if("".equals(finalStep)) {
			max = -1;
		} else {
			max = Integer.valueOf(finalStep);
		}	
		if(max < 0){System.out.println("WARNING: the headless simulation has no final step!");}
		// int max = Integer.valueOf(e.getAttribute(XmlTAG.FINAL_STEP_TAG));
		
		String untilCond = e.getAttribute(XmlTAG.UNTIL_TAG);
//		GAML.compileExpression(expression, agent, onlyExpression)
		
		String sourcePath = e.getAttribute(XmlTAG.SOURCE_PATH_TAG);
		String experimentName = e.getAttribute(XmlTAG.EXPERIMENT_NAME_TAG);
		try {
			((HeadlessListener) GAMA.getHeadlessGui()).setBufferedWriter(new BufferedWriter(new FileWriter(Globals.OUTPUT_PATH + "/" + Globals.CONSOLE_OUTPUT_FILENAME)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

		String seed = e.getAttribute(XmlTAG.SEED_TAG);
		long selectedSeed = seed == null || seed.length() == 0 ? 0l : Long.valueOf(seed).longValue();
		if ( sourcePath.charAt(0) != '/' && sourcePath.charAt(0) != '\\' ) {
			String pr;
			if ( fileName != null ) {
				String prt;
				File ff = new File(fileName);
				prt = ff.getAbsolutePath();
				pr = prt.substring(0, prt.length() - ff.getName().length());
				pr = pr + "/";
			} else {
				pr = new File(".").getAbsolutePath();
			}
			pr = pr.substring(0, pr.length() - 1);
			sourcePath = pr + sourcePath;
		}
		ExperimentJob res = new ExperimentJob(sourcePath, expId, experimentName, max, untilCond, selectedSeed);
		this.readParameter(res, e);
		this.readOutput(res, e);
		return res;
	}

	private ArrayList<ExperimentJob> readSimulation(final Document dom) {
		ArrayList<ExperimentJob> res = new ArrayList<ExperimentJob>();
//		Element docEle = dom.getDocumentElement();
		NodeList nl = dom.getElementsByTagName(XmlTAG.SIMULATION_TAG);
		if ( nl != null && nl.getLength() > 0 ) {
			for ( int i = 0; i < nl.getLength(); i++ ) {

				// get the employee element
				Element el = (Element) nl.item(i);

				// add it to list
				res.add(readSimulation(el));
			}
		}
		return res;
	}

	public void parseXmlFile() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(myStream);
			this.sims = this.readSimulation(dom);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
