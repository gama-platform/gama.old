/*********************************************************************************************
 *
 *
 * 'GamaSimulator.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.runtime;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import msi.gama.headless.common.*;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.core.Simulation.*;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GamaSimulator implements ISimulator {

	private String experimentID;

	private int currentStep;
	private final ParametersSet params;
	private IModel model;
	private String fileName;
	private String experimentName;
	private IExperimentPlan experiment;
	private long seed;
	
	public GamaSimulator() {
		this.params = new ParametersSet();
	}

	@Override
	public void nextStep(final int currentStep) {
		this.currentStep = currentStep;
		experiment.getController().userStep();
		experiment.getSimulationOutputs().step(experiment.getCurrentSimulation().getScope());
	}

	@Override
	public void free() {
		experiment.dispose();
	}

	@Override
	public void setParameterWithName(final String name, final Object value) {
		if ( this.params.containsKey(name) ) {
			this.params.remove(name);
		}
		this.params.put(name, value);
	}

	@Override
	public void retrieveOutputValue(final ListenedVariable v) {
		OutputType type = v.getType();
		switch (type) {
			case EXPERIMENT_ATTRIBUTE:
				v.setValue(experiment.getAgent().getDirectVarValue(experiment.getAgent().getScope(), v.getName()));
				return;
			case OUTPUT: {
				IOutput output =
					((AbstractOutputManager) experiment.getExperimentOutputs()).getOutputWithName(v.getName());
				if ( output == null ) {
					output = ((AbstractOutputManager) experiment.getSimulationOutputs()).getOutputWithName(v.getName());
				}
				output.update();
				if ( output instanceof MonitorOutput ) {
					v.setValue(((MonitorOutput) output).getLastValue());
				} else if ( output instanceof LayeredDisplayOutput ) {
					BufferedImage buf = ((LayeredDisplayOutput) output).getImage();
					if ( buf == null ) {
						v.setValue(null);
					} else {
						v.setValue(writeImageInFile(buf, v.getName()));
					}
				}
			}
				return;
			case SIMULATION_ATTRIBUTE:
				v.setValue(experiment.getAgent().getSimulation()
					.getDirectVarValue(experiment.getAgent().getSimulation().getScope(), v.getName()));
				return;
		}

	}

	private Display2D writeImageInFile(final BufferedImage img, final String name) {
		String fileName = name + this.getExperimentID() + "-" + currentStep + ".png";
		String fileFullName = Globals.IMAGES_PATH + "/" + fileName;
		try {
			ImageIO.write(img, "png", new File(fileFullName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Display2D(name + this.getExperimentID() + "-" + currentStep + ".png");
	}


	@Override
	public void load(final String var, final String exp, final String expName, final long s) {
		this.fileName = var;
		this.experimentID = exp;
		this.experimentName = expName;
		this.seed = s;
		this.model = HeadlessSimulationLoader.loadModel(new File(this.fileName));
	}

	public String getExperimentID() {
		return experimentID;
	}

	@Override
	public void initialize() {
		try {
			experiment = GAMA.addHeadlessExperiment(this.model, this.experimentName, this.params, this.seed);
		} catch (GamaRuntimeException e) {
			e.printStackTrace();
			experiment = null;
		}
		if ( experiment == null ) {
			System.exit(-1);
		}
	}

	@Override
	public OutputType getTypeOf(final String name) {
		if ( experiment == null ) { return OutputType.OUTPUT; }
		if ( experiment.hasVar(name) ) { return OutputType.EXPERIMENT_ATTRIBUTE; }
		if ( experiment.getModel().getSpecies().hasVar(name) ) { return OutputType.SIMULATION_ATTRIBUTE; }
		return OutputType.OUTPUT;
	}
}
