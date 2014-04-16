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

import java.util.Iterator;

import msi.gama.kernel.experiment.ExperimentSpecies;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.AbstractOutput;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.FrontEndController;
import msi.gama.runtime.FrontEndScheduler;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.expressions.IExpression;
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
	// private GamaList sharedResource;
	private String experimentName = "default";
	private String controllerName = "default";
	private String comodelName = "";
	private boolean initDisplay = false;

	// private IExperimentSpecies exp = null;
	public GAMLFile(final IScope scope, final String pathName)
			throws GamaRuntimeException {
		super(scope, pathName);
		mymodel = null;
	}

	public GAMLFile(final IScope scope, final String pathName,
			final String expName, final String cName)
			throws GamaRuntimeException {
		super(scope, pathName);
		// mymodel =
		// ((GamlExpressionFactory)
		// GAML.getExpressionFactory()).getParser().createModelFromFile(getFile().getName());
		mymodel = ((GamlExpressionFactory) GAML.getExpressionFactory())
				.getParser().createModelFromFile(getFile().getName());
		experimentName = expName;
		comodelName = cName;

		// multithread
		controllerName = experimentName + comodelName;

		// initDisplay = false;
	}

	public GamlSpecies getSpecies(final String name) {

		return (GamlSpecies) mymodel.getSpecies(name);
	}
	public void createExperiment(final String expName) {
			IExperimentSpecies exp = mymodel.getExperiment("Experiment "
					+ expName);
		((ExperimentSpecies) exp).setControllerName(controllerName);
		// multithread

			GAMA.getController(controllerName).newExperiment(exp);
		
		// ((ExperimentSpecies) exp).createAgent();
		// ((ExperimentSpecies) exp).open();

		// singlethread
		// GAMA.getController(controllerName).addExperiment(
		// experimentName + comodelName, exp);
	}

	public void execute(final IScope scope,
			final IExpression with_exp, final IExpression param_input,
		final IExpression param_output, GamaMap in, GamaMap out, final IExpression reset, final IExpression repeat,
 final IExpression stopCondition,
			IExpression share) {
		if (GAMA.getController(controllerName) == null) {
			FrontEndController fec = new FrontEndController(
					new FrontEndScheduler());
			GAMA.addController(controllerName, fec);
		}
		// multithread
		if (GAMA.getController(controllerName).getExperiment() == null) {

			// singlethread
			// if (GAMA.getController(controllerName)
			// .getExperiment(
			// experimentName) == null) {

			this.createExperiment(experimentName);
		}

		if (!initDisplay) {
			if (param_input != null) {
				in = (GamaMap) param_input.value(scope);
				for (int i = 0; i < in.getKeys().size(); i++) {
					GAMA.getController(controllerName)
							.getExperiment()
							.getModel().getVar(in.getKeys().get(i).toString())
							.setValue(null, in.getValues().get(i));
				}
			}
			((ExperimentSpecies) GAMA.getController(controllerName)
					.getExperiment()).open();
		// ((ExperimentSpecies)
		// GAMA.getController(controllerName).getExperiment(
		// experimentName + comodelName)).getAgent().schedule();
			SimulationAgent sim = (SimulationAgent) GAMA
					.getController(controllerName)
					.getExperiment().getAgent()
					.getSimulation();
			IScope simScope = null;
			if (sim == null) {
				sim = GAMA.getController(controllerName)
						.getExperiment().getAgent()
						.createSimulation(new ParametersSet(), false);
				GAMA.getController(controllerName).getScheduler()
						.schedule(sim, sim.getScope());
				// sim._init_(sim.getScope());
			}
			initDisplay = true;
			ExperimentDescription expdes = ((ModelDescription) mymodel
					.getDescription()).getExperiment(experimentName);
			if (expdes.getBehaviors().size() > 0) {
				Iterator<IDescription> m = expdes.getBehaviors().iterator();
				IDescription iii = m.next();

				for (IDescription disp : iii.getChildren()) {
					ISymbol output = disp.compile();
					output.setName(output.getName() + "#" + comodelName);
					((AbstractOutput) output).setExpName(experimentName
							+ comodelName);
					GAMA.getExperiment().getSimulationOutputs()
							.addOutput((IOutput) output);
					((AbstractOutputManager) GAMA.getExperiment()
							.getSimulationOutputs()).initSingleOutput(scope,
							(IOutput) output);
				}

				// GAMA.getExperiment().getSimulationOutputs().init(scope);
			}
		}

		SimulationAgent sim = (SimulationAgent) GAMA
				.getController(controllerName)
				.getExperiment().getAgent()
				.getSimulation();
		IScope simScope = null;
		if (sim == null) {
			sim = GAMA.getController(controllerName)
					.getExperiment().getAgent()
					.createSimulation(new ParametersSet(), false);
			// GAMA.getController(controllerName).getScheduler()
			// .schedule(sim, sim.getScope());
			// sim._init_(sim.getScope());
		}
		simScope = sim.getScope();
		if (reset != null && Cast.asBool(scope, reset.value(scope))) {
			sim._init_(simScope);
		}

		// ScopedExpression sss = ScopedExpression.with(simScope, share);
		// share = sss;
		//
		// GamlSpecies gs = (GamlSpecies) ((GamaList)
		// sss.value(simScope)).get(0);
		// GuiUtils.informConsole("" + gs.length(simScope));

		
		int nTimes = 1;
		int i = 0;
		if (repeat != null) {
			nTimes = (Integer) repeat.value(scope);
		}
		boolean mustStop = false;
		if (stopCondition == null) {
			mustStop = true;
		}
		while (!mustStop || i < nTimes) {
			GAMA.getController(controllerName).userStep();// (FrontEndController._STEP);

			if (param_output != null) {
				out = (GamaMap) param_output.value(scope);
				for (int j = 0; j < out.getKeys().size(); j++) { //
				// scope.setAgentVarValue(
				// out.getValues().get(j).toString(), //
				// ((ExperimentSpecies) GAMA
				// .getController(experimentName + comodelName)
				// .getExperiment(experimentName + comodelName)) //
				// .getExperimentScope().getGlobalVarValue(
				// out.getKeys().get(j).toString()));
					Object value = ((ExperimentSpecies) GAMA.getController(
							experimentName + comodelName).getExperiment()).getExperimentScope()
							.getGlobalVarValue(out.getKeys().get(j).toString());
					scope.getAgentScope().setAttribute(
							out.getValues().get(j).toString(), value);
				}
			}
			if (stopCondition != null) {
				mustStop = Cast.asBool(scope,
						scope.evaluate(stopCondition, scope.getAgentScope()));
			}
			i++;
		}
		 
		if (reset != null && Cast.asBool(scope, reset.value(scope))) {
			GAMA.getController(experimentName + comodelName)
					.getExperiment().dispose();
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
