package msi.gama.headless.runtime_old;

import java.util.ArrayList;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.AbstractExperiment;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbolKind;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.EXPERIMENT, IKeyword.REMOTE }, kind = ISymbolKind.EXPERIMENT)
@with_sequence
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { IKeyword.BATCH, IKeyword.REMOTE,
		IKeyword.GUI_ }, optional = false) }, omissible = IKeyword.NAME)
@inside(symbols = IKeyword.MODEL)
public class HeadLessExperiment extends AbstractExperiment {

	

	
	public HeadLessExperiment(final IDescription description) {
		super(description);
		
	}

	@Override
	public IList<? extends IParameter> getParametersToDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IList<String> getParametersNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopExperiment() {
		// TODO nothing

	}

	@Override
	public void startExperiment() throws GamaRuntimeException {
		// TODO Auto-generated method stub
		this.getCurrentSimulation().step();
	}

	@Override
	public void stepExperiment() {
		// TODO Auto-generated method stub
		this.getCurrentSimulation().step();
	}

	@Override
	public void reloadExperiment() throws GamaRuntimeException,
			InterruptedException {
		// TODO Auto-generated method stub

	}

}
