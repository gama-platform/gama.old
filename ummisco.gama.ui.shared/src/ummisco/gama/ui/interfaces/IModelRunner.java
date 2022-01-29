/*******************************************************************************************************
 *
 * IModelRunner.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.interfaces;

import java.util.List;

import msi.gaml.statements.test.TestExperimentSummary;

/**
 * The Interface IModelRunner.
 */
public interface IModelRunner {

	/**
	 * Edits the model.
	 *
	 * @param eObject the e object
	 */
	void editModel(Object eObject);

	/**
	 * Run model.
	 *
	 * @param object the object
	 * @param exp the exp
	 */
	void runModel(Object object, String exp);

	/**
	 * Run headless tests.
	 *
	 * @param model the model
	 * @return the list
	 */
	List<TestExperimentSummary> runHeadlessTests(Object model);

}
