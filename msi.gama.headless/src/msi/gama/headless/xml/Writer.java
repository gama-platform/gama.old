package msi.gama.headless.xml;

import msi.gama.headless.core.Simulation;


public interface Writer {
	public void writeSimulationHeader(Simulation s);
	public void writeResultStep(int step,String[] names, Object[] values);
	public void close();
}
