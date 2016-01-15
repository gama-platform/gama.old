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
import msi.gama.headless.core.*;

public class Reader {

	public String fileName;
	Vector<Simulation> sims;

	public Reader(final String file) {
		this.fileName = file;
	}

	public Collection<Simulation> getSimulation() {
		return this.sims;
	}

	private Parameter readParameter(final Element e) {
		String name = e.getAttribute("name");
		String value = e.getAttribute("value");
		String type = e.getAttribute("type");
		DataType dtype = DataType.valueOf(type);
		return new Parameter(name, DataTypeFactory.getObjectFromText(value, dtype), dtype);
	}

	private Output readOutput(final Element e) {
		String name = e.getAttribute("name");
		String id = e.getAttribute("id");
		int framerate = Integer.valueOf(e.getAttribute("framerate"));;
		return new Output(name, framerate, id);
	}

	private void readParameter(final Simulation s, final Element docEle) {
		NodeList nl = docEle.getElementsByTagName("Parameter");
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

	private void readOutput(final Simulation s, final Element docEle) {
		NodeList nl = docEle.getElementsByTagName("Output");
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

	private Simulation readSimulation(final Element e) {
		String expId = e.getAttribute("id");
		int max = Integer.valueOf(e.getAttribute("finalstep"));
		String sourcePath = e.getAttribute("sourcePath");
		// String driver= "msi.gama.headless.runtime.GamaSimulator"; //e.getAttribute("driver");
		String experimentName = e.getAttribute("experiment");
		if ( sourcePath.charAt(0) != '/' ) {
			File ff = new File(fileName);
			String prt;
			prt = ff.getPath();
			String pr = prt.substring(0, prt.length() - ff.getName().length());
			sourcePath = pr + sourcePath;
		}
		Simulation res = new Simulation(expId, sourcePath, experimentName, max);
		this.readParameter(res, e);
		this.readOutput(res, e);
		return res;
	}

	private Vector<Simulation> readSimulation(final Document dom) {
		Vector<Simulation> res = new Vector<Simulation>();
		Element docEle = dom.getDocumentElement();
		NodeList nl = dom.getElementsByTagName("Simulation");
		// docEle.getElementsByTagName("Simulation");
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
			Document dom = db.parse(fileName);
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
