/*********************************************************************************************
 * 
 * 
 * 'Simulation.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.core;

import java.util.*;
import msi.gama.headless.common.ISimulator;
import msi.gama.headless.runtime.GamaSimulator;
import msi.gama.headless.xml.Writer;

public class Simulation {

	
	// Temporary solution for getting the variables
	public static enum OutputType {
		OUTPUT, EXPERIMENT_ATTRIBUTE, SIMULATION_ATTRIBUTE
	}

	public static class ListenedVariable {

		String name;
		int frameRate;
		OutputType type;
		Object value;

		public ListenedVariable(final String name, final int frameRate, final OutputType type) {
			this.name = name;
			this.frameRate = frameRate;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setValue(final Object obj) {
			value = obj;
		}

		public Object getValue() {
			return value;
		}

		public OutputType getType() {
			return type;
		}
	}

	/**
	 * Variable listeners
	 */
	private ListenedVariable[] listenedVariables;
	private List<Parameter> parameters;
	private List<Output> outputs;
	private Writer outputFile;
	private final String sourcePath;
	private final String experimentName;
	private final long seed;

	/**
	 * simulator to be loaded
	 */
	public ISimulator simulator;

	/**
	 * current step
	 */
	private int step;

	/**
	 * id of current experiment
	 */
	private String experimentID;
	public int maxStep;

	public void setBufferedWriter(final Writer w) {
		this.outputFile = w;
	}

	public void addParameter(final Parameter p) {
		this.parameters.add(p);
	}

	public void addOutput(final Output p) {
		this.outputs.add(p);
	}

	public Simulation(final Simulation clone) {
		this.experimentID = clone.experimentID;
		this.sourcePath = clone.sourcePath;
		this.maxStep = clone.maxStep;
		this.experimentName = clone.experimentName;
		this.parameters = clone.parameters;
		this.listenedVariables = clone.listenedVariables;
		this.setStep(clone.getStep());
		this.outputs = clone.outputs;
		this.seed = clone.seed;
	}

	public Simulation(final String expId, final String sourcePath, final String exp, final int max, final long s) {
		this.experimentID = expId;
		this.sourcePath = sourcePath;
		this.maxStep = max;
		this.experimentName = exp;
		this.seed = s;
		initialize();
	}

	public void loadAndBuild() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		this.load();
		this.listenedVariables = new ListenedVariable[outputs.size()];

		for ( int i = 0; i < parameters.size(); i++ ) {
			Parameter temp = parameters.get(i);
			this.simulator.setParameterWithName(temp.getName(), temp.getValue());
		}
		this.setup();
		simulator.initialize();
		for ( int i = 0; i < outputs.size(); i++ ) {
			Output temp = outputs.get(i);
			this.listenedVariables[i] =
				new ListenedVariable(temp.getName(), temp.getFrameRate(), simulator.getTypeOf(temp.getName()));
		}

	}

	public void load() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		System.setProperty("user.dir", this.sourcePath);
		this.simulator = new GamaSimulator();
		this.simulator.load(this.sourcePath, this.experimentID, this.experimentName,this.seed);
	}

	public void setup() {
		this.step = 0;
	}

	public void play() {
		if ( this.outputFile != null ) {
			this.outputFile.writeSimulationHeader(this);
		}
		System.out.println("Simulation is running...");
		long startdate = Calendar.getInstance().getTimeInMillis();
		int affDelay = maxStep < 100 ? 1 : maxStep / 100;
		for ( ; step < maxStep; step++ ) {
			if ( step % affDelay == 0 ) {
				System.out.print(".");
			}
			nextStepDone();
		}
		long endDate = Calendar.getInstance().getTimeInMillis();
		this.simulator.free();
		if ( this.outputFile != null ) {
			this.outputFile.close();
		}
		System.out.println("\nSimulation duration: " + (endDate - startdate) + "ms");
	}

	public void nextStepDone() {
		simulator.nextStep(this.step);
		this.exportVariables();
	}

	public String getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(final String experimentID) {
		this.experimentID = experimentID;
	}

	private void exportVariables() {
		// TODO: listenedVariable should contain objects that know "what they are" (simulation outputs, experiment outputs, simulation variables, experiment variables, etc.)
		int size = this.listenedVariables.length;
		if ( size == 0 ) { return; }
		for ( int i = 0; i < size; i++ ) {
			ListenedVariable v = this.listenedVariables[i];
			if ( this.step % v.frameRate == 0 ) {
				simulator.retrieveOutputValue(v);
			}
		}
		if ( this.outputFile != null ) {
			this.outputFile.writeResultStep(this.step, this.listenedVariables);
		}

	}

	public void initialize() {
		parameters = new Vector<Parameter>();
		outputs = new Vector<Output>();

		if ( simulator != null ) {
			simulator.free();
			simulator = null;
		}

	}

	public int getStep() {
		return step;
	}

	public void setStep(final int step) {
		this.step = step;
	}

}
