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

	public GamaSimulator() {
		this.params = new ParametersSet();
	}

	@Override
	public void nextStep(final int currentStep) {
		this.currentStep = currentStep;
		IScope simScope = experiment.getCurrentSimulation().getScope();
		experiment.getCurrentSimulation().step(simScope);
		experiment.getSimulationOutputs().step(simScope);
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

	//
	// @Override
	// public DataType getVariableTypeWithName(final String name) {
	// IOutput output = ((AbstractOutputManager) GAMA.getExperiment().getSimulationOutputs()).getOutputWithName(name);
	// if ( output instanceof LayeredDisplayOutput ) { return DataType.DISPLAY2D; }
	// if ( !(output instanceof MonitorOutput) ) { return DataType.UNDEFINED; }
	//
	// Object res = ((MonitorOutput) output).getLastValue();
	// Class type = null;
	// type = res.getClass();
	//
	// if ( type.equals(Integer.class) || type.equals(Long.class) || type.equals(int.class) ) {
	// return DataType.INT;
	// } else if ( type.equals(Float.class) || type.equals(Double.class) ) {
	// return DataType.FLOAT;
	// } else if ( type.equals(Boolean.class) ) {
	// return DataType.BOOLEAN;
	// } else if ( type.equals(String.class) ) {
	// return DataType.STRING;
	// } else {
	// return DataType.UNDEFINED;
	// }
	// }
	//
	// @Override
	// public boolean containVariableWithName(final String name) {
	// IOutput output = ((AbstractOutputManager) experiment.getSimulationOutputs()).getOutputWithName(name);
	// return output == null;
	// }

	@Override
	public void load(final String var, final String exp, final String expName) {
		this.fileName = var;
		this.experimentID = exp;
		this.experimentName = expName;
		this.model = HeadlessSimulationLoader.loadModel(new File(this.fileName));
	}

	//
	// @Override
	// public void setSeed(final long seed) {
	// this.experiment.getAgent().getRandomGenerator().setSeed(seed);
	// }

	public String getExperimentID() {
		return experimentID;
	}

	@Override
	public void initialize() {
		try {
			experiment = GAMA.addHeadlessExperiment(this.model, this.experimentName, this.params, null);
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
