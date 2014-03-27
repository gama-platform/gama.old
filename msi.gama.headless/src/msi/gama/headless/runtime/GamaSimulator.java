package msi.gama.headless.runtime;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import msi.gama.headless.common.DataType;
import msi.gama.headless.common.Display2D;
import msi.gama.headless.common.Globals;
import msi.gama.headless.common.ISimulator;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.experiment.ExperimentSpecies;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GamaSimulator implements ISimulator {
	private int experimentID;

	private int currentStep;
	private final ParametersSet params;
	private IModel model;
	private String fileName;
	private String experimentName;
	private ExperimentSpecies experiment;

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
	public Object getVariableWithName(final String name) {
		IOutput output = ((AbstractOutputManager) experiment.getSimulationOutputs()).getOutputWithName(name);

		if ( output instanceof MonitorOutput ) {
			return ((MonitorOutput) output).getLastValue();
		} else if ( output instanceof LayeredDisplayOutput ) {
			BufferedImage buf = ((LayeredDisplayOutput) output).getImage();
			return writeImageInFile(buf, name);
		}
		return null;
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
	public DataType getVariableTypeWithName(final String name) {
		IOutput output = ((AbstractOutputManager) GAMA.getExperiment().getSimulationOutputs()).getOutputWithName(name);
		if ( output instanceof LayeredDisplayOutput ) { return DataType.DISPLAY2D; }
		if ( !(output instanceof MonitorOutput) ) { return DataType.UNDEFINED; }

		Object res = ((MonitorOutput) output).getLastValue();
		Class type = null;
		type = res.getClass();

		if ( type.equals(Integer.class) || type.equals(Long.class) || type.equals(int.class) ) {
			return DataType.INT;
		} else if ( type.equals(Float.class) || type.equals(Double.class) ) {
			return DataType.FLOAT;
		} else if ( type.equals(Boolean.class) ) {
			return DataType.BOOLEAN;
		} else if ( type.equals(String.class) ) {
			return DataType.STRING;
		} else {
			return DataType.UNDEFINED;
		}
	}

	@Override
	public boolean containVariableWithName(final String name) {
		IOutput output = ((AbstractOutputManager) experiment.getSimulationOutputs()).getOutputWithName(name);
		return output == null;
	}

	@Override
	public void load(final String var, final int exp, final String expName) {
		this.fileName = var;
		this.experimentID = exp;
		this.experimentName = expName;
		this.model = HeadlessSimulationLoader.loadModel(this.fileName);
	}

	@Override
	public void setSeed(final long seed) {
		this.experiment.getAgent().getRandomGenerator().setSeed(seed);
	}

	public int getExperimentID() {
		return experimentID;
	}

	@Override
	public void initialize() {
		try {
			experiment = HeadlessSimulationLoader.newHeadlessSimulation(this.model, this.experimentName, this.params);
		} catch (GamaRuntimeException e) {
			e.printStackTrace();
			experiment = null;
		}
		if ( experiment == null ) {
			System.exit(-1);
		}
	}
}
