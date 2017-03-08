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

	private Parameter readParameter(final Node e) {
		String name = getAttributeWithoutCase(e,XmlTAG.NAME_TAG);
		String value = getAttributeWithoutCase(e,XmlTAG.VALUE_TAG);
		String var = getAttributeWithoutCase(e,XmlTAG.VAR_TAG);
		String type = getAttributeWithoutCase(e,XmlTAG.TYPE_TAG);
		DataType dtype = DataType.valueOf(type);
		return new Parameter(name, var, DataTypeFactory.getObjectFromText(value, dtype), dtype);
	}

	private Output readOutput(final Node e) {
		String name = getAttributeWithoutCase(e,XmlTAG.NAME_TAG);
		String id = getAttributeWithoutCase(e,XmlTAG.ID_TAG);
		String path = getAttributeWithoutCase(e,XmlTAG.OUTPUT_PATH);
		int framerate = Integer.valueOf(getAttributeWithoutCase(e,XmlTAG.FRAMERATE_TAG));;
		return new Output(name, framerate, id, path);
	}

	private List<Node> findElementByNameWithoutCase(final Node e, final String name)
	{
		String lname = name.toLowerCase();
		ArrayList<Node> res = new ArrayList<Node>();
		if(e.getNodeName().toLowerCase().equals(lname))
			{
				res.add(e);
				return res;
			}
		NodeList nl = e.getChildNodes();
		//System.out.println("get child "+ nl.getLength()+" "+name+ "  "+e.getNodeName());
		for(int i = 0; i < nl.getLength(); i++)
		{
			Node ee = (Node) nl.item(i);
			res.addAll(findElementByNameWithoutCase(ee,name));
			
		}
		return res;
	}
	
	
	private void readParameter(final ExperimentJob s, final Node docEle) {
		List<Node> nl = findElementByNameWithoutCase(docEle,XmlTAG.PARAMETER_TAG);
		if ( nl != null && nl.size() > 0 ) {
			for ( int i = 0; i < nl.size(); i++ ) {

				// get the employee element
				Node el = (Element) nl.get(i);

				// get the Employee object
				Parameter e = this.readParameter(el);
				// add it to list
				s.addParameter(e);
			}
		}
	}

	private void readOutput(final ExperimentJob s, final Node docEle) {
		//NodeList nl = docEle.getElementsByTagName(XmlTAG.OUTPUT_TAG);
		List<Node> nl = findElementByNameWithoutCase(docEle,XmlTAG.OUTPUT_TAG);
		if ( nl != null && nl.size() > 0 ) {
			for ( int i = 0; i < nl.size(); i++ ) {

				// get the employee element
				Node el = (Node) nl.get(i);

				// get the Employee object
				Output e = this.readOutput(el);
				// add it to list
				s.addOutput(e);
			}
		}
	}

	
	private String getAttributeWithoutCase(final Node e, String flag)
	{
		NamedNodeMap mp = e.getAttributes();
		String lflag = flag.toLowerCase();
		for(int i = 0; i< mp.getLength(); i++)
		{
			Node nd = mp.item(i);
			if(nd.getNodeName().toLowerCase().equals(lflag))
			{
				return nd.getTextContent();
			}
		}
		return null;
	}
	
	private ExperimentJob readSimulation(final Node e) {

		String expId = getAttributeWithoutCase(e,XmlTAG.EXPERIMENT_ID_TAG);
		
		String finalStep = getAttributeWithoutCase(e,XmlTAG.FINAL_STEP_TAG);
		int max;
		if(finalStep==null||"".equals(finalStep)) {
			max = -1;
		} else {
			max = Integer.valueOf(finalStep);
		}	
		if(max < 0){System.out.println("WARNING: the headless simulation has no final step!");}
		// int max = Integer.valueOf(e.getAttribute(XmlTAG.FINAL_STEP_TAG));
		
		String untilCond = getAttributeWithoutCase(e,XmlTAG.UNTIL_TAG);
//		GAML.compileExpression(expression, agent, onlyExpression)
		
		String sourcePath = getAttributeWithoutCase(e,XmlTAG.SOURCE_PATH_TAG);
		String experimentName = getAttributeWithoutCase(e,XmlTAG.EXPERIMENT_NAME_TAG);
		String seed = getAttributeWithoutCase(e,XmlTAG.SEED_TAG);
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
		NodeList ee = dom.getChildNodes();
		
		for(int i = 0; i<ee.getLength();i++)
		{
			
			
			List<Node> nl = findElementByNameWithoutCase(ee.item(i),XmlTAG.SIMULATION_TAG);
			if ( nl != null && nl.size() > 0 ) {
				for ( int j = 0; j < nl.size(); j++ ) {

					// get the employee element
					Node el = (Node) nl.get(j);
					// add it to list
					res.add(readSimulation(el));
				}
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
