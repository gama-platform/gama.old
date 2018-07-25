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

	boolean validate(IDescription description, EObject emfContext, IExpression... arguments);
}
