package msi.gama.headless.job;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.headless.core.GamaHeadlessException;

public interface IExperimentJob {

	String getExperimentID();

	String getExperimentName();

	String getModelName();

	List<Parameter> getParameters();

	List<Output> getOutputs();

	void addParameter(final Parameter p);

	void addOutput(final Output p);

	List<String> getOutputNames();

	void removeOutputWithName(final String name);

	void setOutputFrameRate(final String name, final int frate);

	void setParameterValueOf(final String name, final Object val);

	void setSeed(final double s);

	double getSeed();

	long getStep();

	void setFinalStep(long step);

	void loadAndBuild() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException,
			GamaHeadlessException;

	Element asXMLDocument(Document doc);

	void playAndDispose();

	void play();

	void dispose();

	void doStep();
}
