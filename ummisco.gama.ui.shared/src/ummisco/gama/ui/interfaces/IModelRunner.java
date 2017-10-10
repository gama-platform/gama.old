/*********************************************************************************************
 *
 * 'IModelRunner.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.interfaces;

import java.util.List;

import msi.gaml.statements.test.TestStatement.TestSummary;

public interface IModelRunner {

	void editModel(Object eObject);

	void runModel(Object object, String exp);

	List<TestSummary> runHeadlessTests(Object model);

}
