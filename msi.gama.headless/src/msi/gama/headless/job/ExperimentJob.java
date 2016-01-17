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
package msi.gama.headless.job;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import msi.gama.headless.common.Display2D;
import msi.gama.headless.common.Globals;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.core.IRichExperiment;
import msi.gama.headless.core.RichExperiment;
import msi.gama.headless.core.RichOutput;
import msi.gama.headless.xml.Writer;
import msi.gama.kernel.model.IModel;

public class ExperimentJob {

	public static enum OutputType {
		OUTPUT, EXPERIMENT_ATTRIBUTE, SIMULATION_ATTRIBUTE
	}

	public static class ListenedVariable {

		String name;
		int frameRate;
		OutputType type;
		Object value;
		long step;

		public ListenedVariable(final String name, final int frameRate, final OutputType type) {
			this.name = name;
			this.frameRate = frameRate;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setValue(final Object obj,long st) {
			value = obj;
			this.step=st;
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
	public IRichExperiment simulator;

	/**
	 * current step
	 */
	private long step;

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

	public ExperimentJob(final ExperimentJob clone) {
		this.experimentID = clone.experimentID;
		this.sourcePath = clone.sourcePath;
		this.maxStep = clone.maxStep;
		this.experimentName = clone.experimentName;
		this.parameters = clone.parameters;
		this.listenedVariables = clone.listenedVariables;
		this.step = clone.getStep();
		this.outputs = clone.outputs;
		this.seed = clone.seed;
	}

	public ExperimentJob(final String expId, final String sourcePath, final String exp, final int max, final long s) {
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
			this.simulator.setParameter(temp.getName(), temp.getValue());
		}
		this.setup();
		simulator.setup(experimentName,this.seed);
		for ( int i = 0; i < outputs.size(); i++ ) {
			Output temp = outputs.get(i);
			this.listenedVariables[i] =
				new ListenedVariable(temp.getName(), temp.getFrameRate(), simulator.getTypeOf(temp.getName()));
		}

	}

	public void load() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		System.setProperty("user.dir", this.sourcePath);
		IModel mdl = HeadlessSimulationLoader.loadModel(new File(this.sourcePath));
		this.simulator = new RichExperiment(mdl);
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
			//System.out.println(simulator.isInterrupted());
			if(simulator.isInterrupted())
				break;
			doStep();
			
		}
		long endDate = Calendar.getInstance().getTimeInMillis();
		this.simulator.dispose();
		if ( this.outputFile != null ) {
			this.outputFile.close();
		}
		System.out.println("\nSimulation duration: " + (endDate - startdate) + "ms");
	}

	public void doStep() {
		this.step=simulator.step();
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
				RichOutput out = simulator.getRichOutput(v.getName());
				if(out.getValue() == null)
				{
					//LOGGER UNE ERREUR
					//GAMA.reportError(this.  .getCurrentSimulation().getScope(), g, shouldStopSimulation)
				}
				else if(out.getValue() instanceof BufferedImage)
				{
					v.setValue(writeImageInFile((BufferedImage)out.getValue(), v.getName()),step);
				}
				else
				{
					v.setValue(out.getValue(), out.getStep());
				}
				
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
			simulator.dispose();
			simulator = null;
		}
	}

	
	public long getStep() {
		return step;
	}

	private Display2D writeImageInFile(final BufferedImage img, final String name) {
		String fileName = name + this.getExperimentID() + "-" + step + ".png";
		String fileFullName = Globals.IMAGES_PATH + "/" + fileName;
		try {
			ImageIO.write(img, "png", new File(fileFullName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Display2D(name + this.getExperimentID() + "-" + step + ".png");
	}

	

}
