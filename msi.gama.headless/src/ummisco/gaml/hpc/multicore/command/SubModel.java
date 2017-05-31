package ummisco.gaml.hpc.multicore.command;

import java.io.File;
import java.io.IOException;

import msi.gama.headless.core.Experiment;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.operators.Cast;

public class SubModel {
	
	
	private static String retrieveModelFileAbsolutePath(final IScope scope, final String filename) {
		if (filename.charAt(0) == '/')
			return filename;
		return new File(scope.getModel().getFilePath()).getParentFile().getAbsolutePath() + "/" + filename;
	}

	
	@operator(value = IKeywords.STEPSUBMODEL, can_be_const = true, category = IOperatorCategory.FILE, concept = {
			IConcept.HEADLESS })
	@doc(value = "Load a submodel", comment = "loaded submodel")
	public static Integer stepSubModel(final IScope scope, final String expName) {
		Experiment exp = (Experiment) scope.getVarValue(expName);
		return new Integer((int)exp.step());
	}
	
	@operator(value = IKeywords.EVALUATESUBMODEL, can_be_const = true, category = IOperatorCategory.FILE, concept = {
			IConcept.HEADLESS })
	@doc(value = "Load a submodel", comment = "loaded submodel")
	public static Object evaluateSubModel(final IScope scope, final String expName, final String expression) {
		Experiment exp = (Experiment) scope.getVarValue(expName);
		return exp.evaluateExpression(expression);
	}
	
	
	@operator(value = IKeywords.LOADSUBMODEL, can_be_const = true, category = IOperatorCategory.FILE, concept = {
			IConcept.HEADLESS })
	@doc(value = "Load a submodel", comment = "loaded submodel")
	public static IExperimentAgent loadSubModel(final IScope scope, final String expName, final String mdp) {
		int seed = 0;
		String modelPath = mdp;
		if (modelPath != null && !modelPath.isEmpty()) {
			modelPath = retrieveModelFileAbsolutePath(scope, modelPath);
		} else {
			// no model specified, this caller model path is used.
			modelPath = scope.getModel().getFilePath();
		}

		
		final long lseed = seed;

		
		IModel mdl = null;
		try {
			mdl =HeadlessSimulationLoader.loadModel(new File(modelPath));
		} catch (IOException e) {
			throw GamaRuntimeException.error("Sub model file not found!",scope);
		}
		Experiment exp = new Experiment(mdl);
		exp.setup(expName,lseed);
		IExperimentAgent aa = exp.getSimulation().getExperiment();
		//String varName = exp.toString();
		scope.addVarWithValue(aa.toString(), exp);
		return aa;
	}

}
