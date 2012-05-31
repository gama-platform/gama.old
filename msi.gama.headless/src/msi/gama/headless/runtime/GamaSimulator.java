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
import msi.gama.headless.core.IHeadLessExperiment;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.outputs.OutputManager;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GamaSimulator implements ISimulator {
	private int experimentID;


	private int currentStep;
	private ParametersSet params;
	private String fileName;
	private double seed;
	private IHeadLessExperiment experiment;

	public GamaSimulator()
	{
		this.seed= Math.random();
		this.params =  new ParametersSet();
	}
	
	@Override
	public void nextStep(int currentStep) {
		this.currentStep = currentStep;
		experiment.step();
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setParameterWithName(String name, Object value) {
		if(this.params.containsKey(name))
		{
			this.params.remove(name);
		}
		this.params.put(name, value);	
	}

	@Override
	public Object getVariableWithName(String name) {
		IOutput output = ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutputWithName(name);

		if(output instanceof MonitorOutput)
			return ((MonitorOutput)output).getLastValue();
		else
			if(output instanceof LayeredDisplayOutput)
			{
				BufferedImage buf= ((LayeredDisplayOutput)output).getImage();
				return writeImageInFile(buf,name);
			}
		return null;
	}

    private Display2D writeImageInFile(BufferedImage img, String name)
    {
      String fileName =name+this.getExperimentID()+"-"+ currentStep + ".png";
      String fileFullName = Globals.IMAGES_PATH+"/" + fileName;
      try {
		ImageIO.write(img, "png", new File(fileFullName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      return new Display2D(name+this.getExperimentID()+"-"+ currentStep + ".png");
    }
	
	@Override
	public DataType getVariableTypeWithName(String name) {
		IOutput output = ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutputWithName(name);
		if(output instanceof LayeredDisplayOutput)
    		return DataType.DISPLAY2D;
		if(!(output instanceof MonitorOutput))
			return DataType.UNDEFINED;
		
		Object res=((MonitorOutput)output).getLastValue();
		Class type=null;
			type=	res.getClass();
		
		if(type.equals(Integer.class)||type.equals(Long.class)||type.equals(int.class))
			return DataType.INT;
		else if(type.equals(Float.class)||type.equals(Double.class))
			return DataType.FLOAT;
		else if(type.equals(Boolean.class))
			return DataType.BOOLEAN;
		else if(type.equals(String.class))
			return DataType.STRING;
		else return DataType.UNDEFINED;
	}

	@Override
	public boolean containVariableWithName(String name) {
		IOutput output = ((OutputManager)GAMA.getExperiment().getOutputManager()).getOutputWithName(name);
		return output==null;
	}

	@Override
	public void load(String var, int exp) {
		this.fileName = var;
		this.experimentID = exp;
		
		
	}

	@Override
	public void setSeed(double seed) {
		this.seed = seed;

	}
	public int getExperimentID() {
		return experimentID;
	}

	@Override
	public void initialize() {
		try {
			experiment=HeadlessSimulationLoader.newHeadlessSimulation(this.fileName,this.params);
		} catch (GamaRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
