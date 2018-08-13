/*********************************************************************************************
 *
 *
 * 'Reader.java', in plugin 'msi.gama.headless', is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import msi.gama.headless.common.DataType;
import msi.gama.headless.common.DataTypeFactory;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.Output;
import msi.gama.headless.job.Parameter;
import ummisco.gama.dev.utils.DEBUG;

public class Reader {

	static {
		DEBUG.ON();
	}

	public String fileName;
	public InputStream myStream;
	ArrayList<ExperimentJob> sims;

	public void dispose() {
		this.fileName = null;
		try {
			this.myStream.close();
		} catch (final IOException e) {
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
		final String name = getAttributeWithoutCase(e, XmlTAG.NAME_TAG);
		final String value = getAttributeWithoutCase(e, XmlTAG.VALUE_TAG);
		final String var = getAttributeWithoutCase(e, XmlTAG.VAR_TAG);
		final String type = getAttributeWithoutCase(e, XmlTAG.TYPE_TAG);
		final DataType dtype = DataType.valueOf(type);
		return new Parameter(name, var, DataTypeFactory.getObjectFromText(value, dtype), dtype);
	}

	private Output readOutput(final Node e) {
		final String name = getAttributeWithoutCase(e, XmlTAG.NAME_TAG);
		final String id = getAttributeWithoutCase(e, XmlTAG.ID_TAG);
		final String path = getAttributeWithoutCase(e, XmlTAG.OUTPUT_PATH);
		final int framerate = Integer.valueOf(getAttributeWithoutCase(e, XmlTAG.FRAMERATE_TAG));
		;
		return new Output(name, framerate, id, path);
	}

	private List<Node> findElementByNameWithoutCase(final Node e, final String name) {
		final String lname = name.toLowerCase();
		final ArrayList<Node> res = new ArrayList<>();
		if (e.getNodeName().toLowerCase().equals(lname)) {
			res.add(e);
			return res;
		}
		final NodeList nl = e.getChildNodes();
		// DEBUG.LOG("get child "+ nl.getLength()+" "+name+ " "+e.getNodeName());
		for (int i = 0; i < nl.getLength(); i++) {
			final Node ee = nl.item(i);
			res.addAll(findElementByNameWithoutCase(ee, name));

		}
		return res;
	}

	private void readParameter(final ExperimentJob s, final Node docEle) {
		final List<Node> nl = findElementByNameWithoutCase(docEle, XmlTAG.PARAMETER_TAG);
		if (nl != null && nl.size() > 0) {
			for (int i = 0; i < nl.size(); i++) {

				// get the employee element
				final Node el = nl.get(i);

				// get the Employee object
				final Parameter e = this.readParameter(el);
				// add it to list
				s.addParameter(e);
			}
		}
	}

	private void readOutput(final ExperimentJob s, final Node docEle) {
		// NodeList nl = docEle.getElementsByTagName(XmlTAG.OUTPUT_TAG);
		final List<Node> nl = findElementByNameWithoutCase(docEle, XmlTAG.OUTPUT_TAG);
		if (nl != null && nl.size() > 0) {
			for (int i = 0; i < nl.size(); i++) {

				// get the employee element
				final Node el = nl.get(i);

				// get the Employee object
				final Output e = this.readOutput(el);
				// add it to list
				s.addOutput(e);
			}
		}
	}

	private String getAttributeWithoutCase(final Node e, final String flag) {
		final NamedNodeMap mp = e.getAttributes();
		final String lflag = flag.toLowerCase();
		for (int i = 0; i < mp.getLength(); i++) {
			final Node nd = mp.item(i);
			if (nd.getNodeName().toLowerCase().equals(lflag)) { return nd.getTextContent(); }
		}
		return null;
	}

	private ExperimentJob readSimulation(final Node e) {

		final String expId = getAttributeWithoutCase(e, XmlTAG.EXPERIMENT_ID_TAG);

		final String finalStep = getAttributeWithoutCase(e, XmlTAG.FINAL_STEP_TAG);
		int max;
		if (finalStep == null || "".equals(finalStep)) {
			max = -1;
		} else {
			max = Integer.valueOf(finalStep);
		}
		if (max < 0) {
			DEBUG.ERR("WARNING: the headless simulation has no final step!");
		}
		// int max = Integer.valueOf(e.getAttribute(XmlTAG.FINAL_STEP_TAG));

		final String untilCond = getAttributeWithoutCase(e, XmlTAG.UNTIL_TAG);
		// GAML.compileExpression(expression, agent, onlyExpression)

		String sourcePath = getAttributeWithoutCase(e, XmlTAG.SOURCE_PATH_TAG);
		final String experimentName = getAttributeWithoutCase(e, XmlTAG.EXPERIMENT_NAME_TAG);
		final String seed = getAttributeWithoutCase(e, XmlTAG.SEED_TAG);
		final long selectedSeed = seed == null || seed.length() == 0 ? 0l : Long.valueOf(seed).longValue();
		if (sourcePath.charAt(0) != '/' && sourcePath.charAt(0) != '\\') {
			String pr;
			if (fileName != null) {
				String prt;
				final File ff = new File(fileName);
				prt = ff.getAbsolutePath();
				pr = prt.substring(0, prt.length() - ff.getName().length());
				pr = pr + "/";
			} else {
				pr = new File(".").getAbsolutePath();
			}
			pr = pr.substring(0, pr.length() - 1);
			sourcePath = pr + sourcePath;
		}
		final ExperimentJob res = new ExperimentJob(sourcePath, expId, experimentName, max, untilCond, selectedSeed);
		this.readParameter(res, e);
		this.readOutput(res, e);
		return res;
	}

	private ArrayList<ExperimentJob> readSimulation(final Document dom) {
		final ArrayList<ExperimentJob> res = new ArrayList<>();
		// Element docEle = dom.getDocumentElement();
		final NodeList ee = dom.getChildNodes();

		for (int i = 0; i < ee.getLength(); i++) {

			final List<Node> nl = findElementByNameWithoutCase(ee.item(i), XmlTAG.SIMULATION_TAG);
			if (nl != null && nl.size() > 0) {
				for (int j = 0; j < nl.size(); j++) {

					// get the employee element
					final Node el = nl.get(j);
					// add it to list
					res.add(readSimulation(el));
				}
			}

		}
		return res;
	}

	public void parseXmlFile() {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final Document dom = db.parse(myStream);
			this.sims = this.readSimulation(dom);
		} catch (final ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (final SAXException se) {
			se.printStackTrace();
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
