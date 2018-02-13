package ummisco.gaml.hpc.multicore.command;

import java.io.File;
import java.io.IOException;

import msi.gama.headless.core.Experiment;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class SubModel {

	private static String retrieveModelFileAbsolutePath(final IScope scope, final String filename) {
		if (filename.charAt(0) == '/')
			return filename;
		return new File(scope.getModel().getFilePath()).getParentFile().getAbsolutePath() + "/" + filename;
	}

	@operator (
			value = IKeywords.STEPSUBMODEL,
			can_be_const = true,
			category = IOperatorCategory.FILE,
			concept = { IConcept.HEADLESS })
	@doc (
			value = "Load a submodel",
			comment = "loaded submodel")
	public static Integer stepSubModel(final IScope scope, final IExperimentAgent expName) {
		final Experiment exp = (Experiment) scope.getVarValue(expName.toString());
		return new Integer((int) exp.step());
	}

	@operator (
			value = IKeywords.EVALUATESUBMODEL,
			can_be_const = true,
			category = IOperatorCategory.FILE,
			concept = { IConcept.HEADLESS })
	@doc (
			value = "Load a submodel",
			comment = "loaded submodel")
	public static Object evaluateSubModel(final IScope scope, final IExperimentAgent expName, final String expression) {
		final Experiment exp = (Experiment) scope.getVarValue(expName.toString());
		return exp.evaluateExpression(expression);
	}

	@operator (
			value = IKeywords.LOADSUBMODEL,
			can_be_const = true,
			category = IOperatorCategory.FILE,
			concept = { IConcept.HEADLESS })
	@doc (
			value = "Load a submodel",
			comment = "loaded submodel")
	public static IExperimentAgent loadSubModel(final IScope scope, final String expName, final String mdp) {
		final int seed = 0;
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
			mdl = HeadlessSimulationLoader.loadModel(new File(modelPath));
		} catch (final IOException e) {
			throw GamaRuntimeException.error("Sub model file not found!", scope);
		} catch (final GamaHeadlessException e) {
			throw GamaRuntimeException.error("Sub model file cannot be built", scope);
		}
		final Experiment exp = new Experiment(mdl);
		exp.setup(expName, lseed);
		final IExperimentAgent aa = exp.getSimulation().getExperiment();
		// String varName = exp.toString();
		scope.addVarWithValue(aa.toString(), exp);
		return aa;
	}

}
