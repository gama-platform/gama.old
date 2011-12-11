/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 28 mai 2011
 * 
 * @todo Description
 * 
 */
@symbol(name = { ISymbol.EXPERIMENT, ISymbol.GUI_ }, kind = ISymbolKind.EXPERIMENT)
@with_sequence
@facets({
	@facet(name = ISymbol.NAME, type = IType.LABEL, optional = false),
	@facet(name = ISymbol.TYPE, type = IType.LABEL, values = { ISymbol.BATCH, ISymbol.REMOTE,
		ISymbol.GUI_ }, optional = false) })
@inside(symbols = ISymbol.MODEL)
public class GuiExperiment extends AbstractExperiment {

	protected final List<IParameter> regularParameters;

	public GuiExperiment(final IDescription description) {
		super(description);
		regularParameters = new ArrayList();

	}

	@Override
	public List<IParameter> getParametersToDisplay() {
		List<IParameter> result = new ArrayList();
		result.addAll(regularParameters);
		result.addAll(systemParameters);
		return result;
	}

	@Override
	protected void addOwnParameters() {
		ISpecies world = model.getWorldSpecies();
		for ( IVariable v : world.getVars() ) {
			if ( v.isParameter() ) {
				addRegularParameter(new ExperimentParameter(v));
			}
		}
		String cat = getSystemParametersCategory();
		addSystemParameter(new ExperimentParameter(world.getVar(GAMA._UQR),
			"Use quality rendering", cat, null, false));
		super.addOwnParameters();
	}

	@Override
	public void stopExperiment() {
		if ( currentSimulation != null ) {
			currentSimulation.stop();
		}
	}

	@Override
	public void startExperiment() throws GamaRuntimeException {
		startCurrentSimulation();
	}

	@Override
	public void stepExperiment() {
		if ( currentSimulation != null && !isLoading() ) {
			currentSimulation.step();
		}
	}

	@Override
	public void reloadExperiment() throws GamaRuntimeException, GamlException, InterruptedException {
		boolean wasRunning = isRunning() && !isPaused();
		closeCurrentSimulation(false);
		initializeExperiment();
		if ( wasRunning ) {
			startExperiment();
		}
	}

	@Override
	public boolean isGui() {
		return true;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) throws GamlException {
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
	public List<String> getParametersNames() {
		final GamaList<String> result = new GamaList<String>();
		for ( final IParameter v : regularParameters ) {
			result.add(v.getName());
		}
		return result;
	}

}
