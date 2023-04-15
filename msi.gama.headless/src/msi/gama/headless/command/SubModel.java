/*******************************************************************************************************
 *
 * SubModel.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.command;

import java.io.File;
import java.io.IOException;

import msi.gama.headless.core.Experiment;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

/**
 * The Class SubModel.
 */
public class SubModel {

	/**
	 * Retrieve model file absolute path.
	 *
	 * @param scope the scope
	 * @param filename the filename
	 * @return the string
	 */
	private static String retrieveModelFileAbsolutePath(final IScope scope, final String filename) {
		if (filename.charAt(0) == '/')
			return filename;
		return new File(scope.getModel().getFilePath()).getParentFile().getAbsolutePath() + "/" + filename;
	}

	/**
	 * Step sub model.
	 *
	 * @param scope the scope
	 * @param expName the exp name
	 * @return the integer
	 */
	@operator (
			value = IKeywords.STEPSUBMODEL,
			can_be_const = true,
			category = IOperatorCategory.FILE,
			concept = { IConcept.HEADLESS })
	@doc (
			value = "Load a submodel",
			comment = "loaded submodel")
	@no_test
	public static Integer stepSubModel(final IScope scope, final IExperimentAgent expName) {
		final Experiment exp = (Experiment) scope.getVarValue(expName.toString());
		return new Integer((int) exp.step());
	}

	/**
	 * Evaluate sub model.
	 *
	 * @param scope the scope
	 * @param expName the exp name
	 * @param expression the expression
	 * @return the object
	 */
	@operator (
			value = IKeywords.EVALUATESUBMODEL,
			can_be_const = true,
			category = IOperatorCategory.FILE,
			concept = { IConcept.HEADLESS })
	@doc (
			value = "Load a submodel",
			comment = "loaded submodel")
	@no_test
	public static Object evaluateSubModel(final IScope scope, final IExperimentAgent expName, final String expression) {
		final Experiment exp = (Experiment) scope.getVarValue(expName.toString());
		return exp.evaluateExpression(expression);
	}

	/**
	 * Load sub model.
	 *
	 * @param scope the scope
	 * @param expName the exp name
	 * @param mdp the mdp
	 * @return the i experiment agent
	 */
	@operator (
			value = IKeywords.LOADSUBMODEL,
			//can_be_const = true,
			type=IType.AGENT
			//category = IOperatorCategory.FILE,
			//concept = { IConcept.HEADLESS }
			)
	@doc (
			value = "Load a submodel",
			comment = "loaded submodel")
	@no_test
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
