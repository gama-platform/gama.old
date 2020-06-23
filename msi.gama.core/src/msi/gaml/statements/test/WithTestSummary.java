/*******************************************************************************************************
 *
 * msi.gaml.statements.test.WithTestSummary.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.test;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;

public interface WithTestSummary<T extends AbstractSummary<?>> {

	/**
	 * The AbstractSummary instance corresponding to this statement
	 * 
	 * @return an instance of a subclass of AbstractSummary (never null)
	 */
	T getSummary();

	/**
	 * The title of the summary (as it will appear in the TestView and in the log
	 * 
	 * @return
	 */
	String getTitleForSummary();

	/**
	 * The URI corresponding to this statement to retrieve it in the editor, or null if this statement is synthetic
	 * 
	 * @return the uri of the statement or null
	 */
	URI getURI();

	/**
	 * The sub-elements (statements) with test summaries
	 * 
	 * @return a collection of WithTestSummary statements or an empty list (never null)
	 */
	Collection<? extends WithTestSummary<?>> getSubElements();

}
