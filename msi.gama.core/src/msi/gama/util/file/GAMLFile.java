/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.species.GamlSpecies;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 13 nov. 2011
 * 
 * @todo Description
 * 
 */
@file(name = "gaml", extensions = { "gaml" })
public class GAMLFile extends GamaFile<Integer, IModel> {

	private final IModel mymodel;
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */

	private IExperimentSpecies exp = null;

	public GAMLFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		mymodel =
			((GamlExpressionFactory) GAML.getExpressionFactory()).getParser().createModelFromFile(getFile().getName());
		exp = mymodel.getExperiment("Experiment default");

	}

	public GamlSpecies getSpecies(final String name) {

		return (GamlSpecies) mymodel.getSpecies(name);
	}

	public IScope getModelScope() {
		if ( exp == null ) {
			((ExperimentSpecies) exp).createAgent();
		}
		return exp.getAgent().getScope();
	}

	public IExperimentSpecies getExperiment(final String exp_name) {
		if ( exp == null ) {
			exp = mymodel.getExperiment("Experiment " + exp_name);
			((ExperimentSpecies) exp).createAgent();
		}
		return exp;

	}

	public void execute(final IScope scope, final IExpression with_exp, final IExpression param_input,
		final IExpression param_output, GamaMap in, GamaMap out, final IExpression reset, final IExpression repeat,
		final IExpression stopCondition) {
		if ( with_exp != null ) {
			this.getExperiment(with_exp.getName());
		}

		if ( param_input != null ) {
			in = (GamaMap) param_input.value(scope);
			for ( int i = 0; i < in.getKeys().size(); i++ ) {
				exp.getModel().getVar(in.getKeys().get(i).toString()).setValue(in.getValues().get(i));
			}
		}

		exp.getSimulationOutputs().removeAllOutput();

		SimulationAgent sim = (SimulationAgent) exp.getAgent().getSimulation();
		IScope simScope = null;
		if ( sim == null ) {
			sim = exp.getAgent().createSimulation(new ParametersSet(), false);
			sim._init_(sim.getScope());
		}
		simScope = sim.getScope();
		if ( reset != null && Cast.asBool(scope, reset.value(scope)) ) {
			sim._init_(simScope);
		}

		int n = 1;
		int i = 0;
		if ( repeat != null ) {
			n = (Integer) repeat.value(scope);
		}
		boolean mustStop = false;
		if ( stopCondition == null ) {
			mustStop = true;
		}
		while (!mustStop || i < n) {
			exp.getAgent().getSimulation().step(simScope);
			if ( param_output != null ) {
				out = (GamaMap) param_output.value(scope);
				for ( int j = 0; j < out.getKeys().size(); j++ ) {
					scope.setAgentVarValue(out.getValues().get(j).toString(), ((ExperimentSpecies) exp)
						.getExperimentScope().getGlobalVarValue(out.getKeys().get(j).toString()));

				}
			}
			if ( stopCondition != null ) {
				mustStop = Cast.asBool(scope, scope.evaluate(stopCondition, scope.getAgentScope()));
			}
			i++;
		}

		if ( reset != null && Cast.asBool(scope, reset.value(scope)) ) {
			exp = null;
		}
	}

	/**
	 * 
	 * @see msi.gama.util.GamaFile#_copy()
	 */
	@Override
	protected IGamaFile _copy(final IScope scope) {
		// TODO ? Will require to do a copy of the file. But how to get the new
		// name ? Or maybe just
		// as something usable like
		// let f type: file value: write(copy(f2))
		return null;
	}

	/**
	 * 
	 * @see msi.gama.util.GamaFile#_isFixedLength()
	 */
	// @Override
	// protected boolean _isFixedLength() {
	// return false;
	// }

	/**
	 * @see msi.gama.util.GamaFile#_toGaml()
	 */
	// @Override
	// public String getKeyword() {
	// return Files.SHAPE;
	// }

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList();

		((IList) buffer).add(mymodel);

	}

	public IModel getModel() {
		return mymodel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO Regarder ce qu'il y a dans la commande "save" pour sauvegarder
		// les fichiers.
		// Merger progressivement save et le syst�me de fichiers afin de ne plus
		// d�pendre de �a.

	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {

		return null;

	}

}
