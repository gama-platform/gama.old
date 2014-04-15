/*********************************************************************************************
 * 
 *
 * 'GAMLFile.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
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
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 13 nov. 2011
 * 
 * @todo Description
 * 
 */
@file(name = "gaml", extensions = { "gaml" }, buffer_type = IType.LIST, buffer_content = IType.SPECIES)
public class GAMLFile extends GamaFile<IList<IModel>, IModel, Integer, IModel> {

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
		// AD BUG: Test impossible
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
				exp.getModel().getVar(in.getKeys().get(i).toString()).setValue(scope, in.getValues().get(i));
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
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( getBuffer() != null ) { return; }
		setBuffer(new GamaList());

		((IList) getBuffer()).add(mymodel);

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
