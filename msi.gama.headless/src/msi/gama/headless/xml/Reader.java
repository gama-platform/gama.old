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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import msi.gama.headless.common.DataType;
import msi.gama.headless.common.DataTypeFactory;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.Output;
import msi.gama.headless.job.Parameter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class Reader {

	public String fileName;
	public InputStream myStream;
	ArrayList<ExperimentJob> sims;
	
	public void dispose()
	{
		this.fileName = null;
		try {
			this.myStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myStream=null;
		sims.clear();
		sims= null;
		
	}
	public Reader(String file) throws FileNotFoundException
	{
		fileName = file;
		myStream = new FileInputStream(new File(file));
	}
	
	public Reader(InputStream inp)
	{
		myStream = inp;
	}
	
	public Collection<ExperimentJob> getSimulation()
	{
		return this.sims;
	}
	
	private Parameter readParameter(Element e)
	{
		String name = e.getAttribute(XmlTAG.NAME_TAG);
		String value=e.getAttribute(XmlTAG.VALUE_TAG);
		String type=e.getAttribute(XmlTAG.TYPE_TAG);
		DataType dtype=DataType.valueOf(type);
		return new Parameter(name, DataTypeFactory.getObjectFromText(value, dtype), dtype);
	}

	private Output readOutput(Element e)
	{
		String name = e.getAttribute(XmlTAG.NAME_TAG);
		String id=e.getAttribute(XmlTAG.ID_TAG);
		int framerate=Integer.valueOf(e.getAttribute(XmlTAG.FRAMERATE_TAG));;
		return new Output(name, framerate, id);
	}
	
	private void readParameter(ExperimentJob s, Element docEle)
	{
		NodeList nl = docEle.getElementsByTagName(XmlTAG.PARAMETER_TAG);
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the employee element
				Element el = (Element)nl.item(i);

				//get the Employee object
				Parameter e = this.readParameter(el);
				//add it to list
				s.addParameter(e);
			}
		}
	}
	
	private void readOutput(ExperimentJob s, Element docEle)
	{
		NodeList nl = docEle.getElementsByTagName(XmlTAG.OUTPUT_TAG);
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the employee element
				Element el = (Element)nl.item(i);

				//get the Employee object
				Output e = this.readOutput(el);
				//add it to list
				s.addOutput(e);
			}
		}
	}
	private ExperimentJob readSimulation(Element e)
	{
		
		String expId=e.getAttribute(XmlTAG.EXPERIMENT_ID_TAG);
		int max=Integer.valueOf(e.getAttribute(XmlTAG.FINAL_STEP_TAG));
		String sourcePath=e.getAttribute(XmlTAG.SOURCE_PATH_TAG);
		String experimentName=e.getAttribute(XmlTAG.EXPERIMENT_NAME_TAG);
		
		String seed = e.getAttribute(XmlTAG.SEED_TAG);
		long selectedSeed = (seed==null||seed.length()==0)?0l:Long.valueOf(seed).longValue();
		if(sourcePath.charAt(0)!='/')
		{
			String pr;
			if(fileName != null)
			{
				String prt;
				File ff=( new File(fileName));
				prt = ff.getPath();
				pr = prt.substring(0, (int)(prt.length()- ff.getName().length()));
				pr = pr + "/";

			}
			else
				pr=new File(".").getAbsolutePath();
				pr = pr.substring(0, pr.length()-1);
			sourcePath = pr+sourcePath; 
		}
		ExperimentJob res=new ExperimentJob(expId, sourcePath, experimentName, max,selectedSeed);
		this.readParameter(res, e);
		this.readOutput(res, e);
		return res;
	}

	
	private ArrayList<ExperimentJob> readSimulation(Document dom)
	{
		ArrayList<ExperimentJob> res=new ArrayList<ExperimentJob>();
		Element docEle = dom.getDocumentElement();
		NodeList nl = dom.getElementsByTagName(XmlTAG.EXPERIMENT_ID_TAG);
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the employee element
				Element el = (Element)nl.item(i);

				//add it to list
				res.add(readSimulation(el));
			}
		}
		return res;
	}

	public void parseXmlFile(){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(myStream);
			this.sims=this.readSimulation(dom);
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
