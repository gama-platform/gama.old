/*********************************************************************************************
 * 
 *
 * 'ResultReader.java', in plugin 'msi.gama.hpc', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.hpc.simulation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ResultReader {

	public String fileName;
	Simulation sim;

	public ResultReader(String file) {
		this.fileName = file;
	}

	private void readResult(Simulation s, Element docEle) {
		NodeList nodeList = docEle.getElementsByTagName("Variable");
		for ( int i = 0; i < nodeList.getLength(); i++ ) {
			Result e;
			try {
				String tmpvarname = nodeList.item(i).getAttributes().item(0).getNodeValue();
				String tmpvarvalue = nodeList.item(i).getAttributes().item(1).getNodeValue();
				


//				System.out.println("" + tmpvarname + " " + Float.valueOf(tmpvarvalue));
				e = new Result(tmpvarname, Double.valueOf(tmpvarvalue));
				s.addResult(e);
			} catch (Exception ex) {

			}
			// System.out.println(j+" "+nodeList.item(i).getAttributes().item(j).getNodeValue());

		}

	}

	// private Simulation readSimulation(Document dom) {
	//
	// return tmpsim;
	// }

	public Simulation parseXmlFile() {
		sim = new Simulation(0);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(fileName);
			// this.sim = this.readSimulation(dom);
			Element docEle = dom.getDocumentElement();
			NodeList steplist = docEle.getElementsByTagName("Step");

			// int expId = Integer.valueOf(docEle.getAttribute("id"));//
			// .getAttributes().getNamedItem("id").getTextContent());
			// System.out.println("" + expId);
			// Simulation tmpsim = new Simulation(0);

			if ( steplist != null && steplist.getLength() > 0 ) {
				for ( int i = 0; i < steplist.getLength(); i++ ) {

					Node node = steplist.item(i);

					Element e = (Element) node;
					readResult(sim, e);
				}
			}

			// System.out.println(sim.results.capacity());
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return sim;
	}

}
