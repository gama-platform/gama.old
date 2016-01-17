/*********************************************************************************************
 *
 *
 * 'MoleExperiment.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.core;

import msi.gama.headless.job.ExperimentJob.OutputType;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class RichExperiment extends Experiment implements IRichExperiment {

	
	
	public RichExperiment(IModel mdl) {
		super(mdl);
	}

	public OutputType getTypeOf(final String name) {
		if ( currentExperiment == null ) { return OutputType.OUTPUT; }
		if ( currentExperiment.hasVar(name) ) { return OutputType.EXPERIMENT_ATTRIBUTE; }
		if ( currentExperiment.getModel().getSpecies().hasVar(name) ) { return OutputType.SIMULATION_ATTRIBUTE; }
		return OutputType.OUTPUT;
	}
	
	public RichOutput getRichOutput(final String parameterName) {
		IOutput output =
			((AbstractOutputManager) this.currentExperiment.getSimulationOutputs()).getOutputWithName(parameterName);
		if ( output == null ) { throw GamaRuntimeException.error("Output unresolved"); }
		output.update();
		
		Object val = null;
		
		if(output instanceof MonitorOutput)
		{
			val = ((MonitorOutput) output).getLastValue();
		}
		else if(output instanceof LayeredDisplayOutput)
		{
			val = ((LayeredDisplayOutput) output).getImage();
		}
		else if(output instanceof LayeredDisplayOutput)
		{
			val = ((FileOutput)output).getFile();
		}
		return  new RichOutput(parameterName,this.currentStep,val);
	}
		
	@Override
	public void dispose() {
		GAMA.closeExperiment(currentExperiment);
	}
}
