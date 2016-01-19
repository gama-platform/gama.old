package msi.gama.headless.job;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.headless.runtime.RuntimeContext;

public interface IExperimentJob {
	
	public String getExperimentID();
	public void addParameter(final Parameter p);
	public void addOutput(final Output p);
	public void setSeed(final long s);
	public long getSeed();
	public long getStep();
	
	public void loadAndBuild(RuntimeContext rtx) throws InstantiationException, IllegalAccessException, ClassNotFoundException;
	public Element asXMLDocument(Document doc);
	
	
	public void play();
	public void doStep();
}
