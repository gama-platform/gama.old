package msi.gama.headless.core;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.HEADLESS_UI }, kind = ISymbolKind.EXPERIMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { IKeyword.HEADLESS_UI }, optional = false) }, omissible = IKeyword.NAME)
@inside(symbols = IKeyword.MODEL)
public class HeadLessExperiment extends AbstractExperiment implements IHeadLessExperiment {

	protected final List<IParameter> regularParameters;

	public HeadLessExperiment(final IDescription description) {
		super(description);
		regularParameters = new ArrayList();
	}

	@Override
	public void setSeed(final double seed) {
		// this.seed = seed;
	}

	@Override
	public void start(final int nbStep) {
		for ( int i = 0; i < nbStep; i++ ) {
			this.stepExperiment();
		}

	}

	@Override
	public IList<IParameter> getParametersToDisplay() {
		IList<IParameter> result = new GamaList();
		result.addAll(regularParameters);
		result.addAll(systemParameters);
		return result;
	}

	@Override
	public void stop() {}

	@Override
	public void pause() {}

	@Override
	public void step() {
		stepExperiment();
	}

	@Override
	public void stopExperiment() {}

	@Override
	public void startExperiment() throws GamaRuntimeException {}

	@Override
	public void reloadExperiment() throws GamaRuntimeException, InterruptedException {
		boolean wasRunning = isRunning() && !isPaused();
		closeCurrentSimulation(false);
		initializeExperiment();
		if ( wasRunning ) {
			startExperiment();
		}
	}

	@Override
	public boolean isGui() {
		return false;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		super.setChildren(children);
		for ( ISymbol s : children ) {
			if ( s instanceof IParameter ) {
				addRegularParameter((IParameter) s);
			}
		}
	}

	public void addRegularParameter(final IParameter p) {
		if ( registerParameter(p) ) {
			regularParameters.add(p);
		}
	}

	@Override
	public IList<String> getParametersNames() {
		final GamaList<String> result = new GamaList<String>();
		for ( final IParameter v : regularParameters ) {
			result.add(v.getName());
		}
		return result;
	}

	@Override
	public ISimulation createSimulation() {
		return new HeadlessSimulation(this);
	}

	@Override
	public void stepExperiment() {
		this.currentSimulation.step();
		// TODO Auto-generated method stub

	}

	@Override
	public void setParameterWithName(final String name, final Object value)
		throws GamaRuntimeException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getParameterWithName(final String name) throws GamaRuntimeException,
		InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

}
