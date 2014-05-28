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
import java.util.Map;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.GamlModelSpecies;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.AbstractOutput;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.runtime.FrontEndController;
import msi.gama.runtime.FrontEndScheduler;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.GamlSpecies;
import msi.gaml.species.ISpecies;
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

	private IModel mymodel = null;
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	// private GamaList sharedResource;
	private String experimentName = "default";
//	private String controllerName = "default";
	private String aliasName = "";
	private boolean initDisplay = false;

	// private IExperimentPlan exp = null;
	public GAMLFile(final IScope scope, final String pathName)
			throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GAMLFile(final IScope scope, final String pathName,
			final String expName, final String cName)
			throws GamaRuntimeException {
		super(scope, pathName);
		experimentName = expName;
		aliasName = cName;
		ModelDescription mm=((GamlExpressionFactory) GAML.getExpressionFactory())
		.getParser().createModelDescriptionFromFile(getFile().getName());		
		mm.setAlias(aliasName);
		mymodel=(IModel) mm.compile();
		// multithread
//		controllerName = experimentName + aliasName;

		// initDisplay = false;
	}

	public GamlSpecies getSpecies(final String name) {

		return (GamlSpecies) mymodel.getSpecies(name);
	}
	public void createExperiment(final String expName) {
			IExperimentPlan exp = mymodel.getExperiment("Experiment "
					+ expName);
			for(IOutput o:exp.getSimulationOutputs().getOutputs().values()){
				o.setName(o.getName() + "#" + aliasName);
			}
			for(IOutput o:exp.getExperimentOutputs().getOutputs().values()){
				o.setName(o.getName() + "#" + aliasName);
			}
		((ExperimentPlan) exp).setControllerName(aliasName);
		// multithread
		// eliminate conflict in close-open current Layer to initialize displays
		GAMA.getController(aliasName).newExperiment(exp);
		// singlethread
		// GAMA.getController(controllerName).addExperiment(
		// experimentName + comodelName, exp);
	}

	public void execute(final IScope scope,
			final IExpression with_exp, final IExpression param_input,
		final IExpression param_output, GamaMap in, GamaMap out, final IExpression reset, final IExpression repeat,
 final IExpression stopCondition,
			IExpression share) {
		if (GAMA.getController(aliasName) == null) {
			FrontEndController fec = new FrontEndController(
					new FrontEndScheduler());
			GAMA.addController(aliasName, fec);
		}
		// multithread
		if (GAMA.getController(aliasName).getExperiment() == null) {

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
					GAMA.getController(aliasName)
							.getExperiment()
							.getModel().getVar(in.getKeys().get(i).toString())
							.setValue(null, in.getValues().get(i));
				}
			}
			GAMA.getController(aliasName).directOpenExperiment(); 
		}
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( getBuffer() != null ) { return; }
		setBuffer(new GamaList());
//		IModel mymodel=  ((GamlExpressionFactory) GAML.getExpressionFactory())
//				.getParser().createModelFromFile(getFile().getName());
		if(mymodel==null){
			ModelDescription mm=((GamlExpressionFactory) GAML.getExpressionFactory())
			.getParser().createModelDescriptionFromFile(getFile().getName());	
			mymodel=(IModel) mm.compile();
		}
		((IList) getBuffer()).add(((GamlModelSpecies)mymodel.getSpecies()));
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
