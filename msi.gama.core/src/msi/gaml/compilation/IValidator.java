/*******************************************************************************************************
 *
 * IValidator.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

/**
 * 'Tagging' interface for IExpression and IDescription validators
 *
 * @author A. Drogoul
 * @since July 2018
 *
 */
public interface IValidator extends IKeyword, IGamlIssue {

	/** The null. */
	IValidator NULL = (d, c, a) -> true;

	/**
	 * Validate.
	 *
	 * @param description the description
	 * @param emfContext the emf context
	 * @param arguments the arguments
	 * @return true, if successful
	 */
	boolean validate(IDescription description, EObject emfContext, IExpression... arguments);
}
