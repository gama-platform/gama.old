/*******************************************************************************************************
 *
 * IExpressionDescription.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IGamlable;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The class IExpressionDescription.
 *
 * @author drogoul
 * @since 31 mars 2012
 *
 */
public interface IExpressionDescription extends IGamlable, IDisposable {

	/**
	 * Sets the expression.
	 *
	 * @param expr the new expression
	 */
	public abstract void setExpression(final IExpression expr);

	/**
	 * Compile.
	 *
	 * @param context the context
	 * @return the i expression
	 */
	public abstract IExpression compile(final IDescription context);

	/**
	 * Gets the expression.
	 *
	 * @return the expression
	 */
	public abstract IExpression getExpression();

	/**
	 * Compile as label.
	 *
	 * @return the i expression description
	 */
	public abstract IExpressionDescription compileAsLabel();

	/**
	 * Equals string.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public abstract boolean equalsString(String o);

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public EObject getTarget();

	/**
	 * Sets the target.
	 *
	 * @param target the new target
	 */
	public void setTarget(EObject target);

	/**
	 * Checks if is const.
	 *
	 * @return true, if is const
	 */
	public boolean isConst();

	/**
	 * Gets the strings.
	 *
	 * @param context the context
	 * @param skills the skills
	 * @return the strings
	 */
	public Collection<String> getStrings(IDescription context, boolean skills);

	/**
	 * Clean copy.
	 *
	 * @return the i expression description
	 */
	public abstract IExpressionDescription cleanCopy();

	/**
	 * Gets the denoted type.
	 *
	 * @param context the context
	 * @return the denoted type
	 */
	public abstract IType<?> getDenotedType(IDescription context);

	// public abstract void collectMetaInformation(GamlProperties meta);

}