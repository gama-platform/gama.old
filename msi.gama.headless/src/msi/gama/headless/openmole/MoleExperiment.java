/*********************************************************************************************
 *
 *
 * 'MoleExperiment.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.openmole;



import msi.gama.headless.core.Experiment;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MoleExperiment extends Experiment implements IMoleExperiment {
	MoleExperiment(final IModel mdl) {
		super(mdl);
	}

	@Override
	public void play(int finalStep) {
		while(finalStep<this.step());
	}

	@Override
	public void play(String exp, int finalStep) {
		IModel toto;
//		toto.getDescription().
		// TODO Auto-generated method stub
		
	}
		
}
